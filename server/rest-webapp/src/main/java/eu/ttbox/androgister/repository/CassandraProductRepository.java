package eu.ttbox.androgister.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.PostConstruct;

import me.prettyprint.cassandra.serializers.BigDecimalSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hom.EntityManagerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

import com.netflix.astyanax.model.ColumnFamily;

import eu.ttbox.androgister.config.cassandra.ColumnFamilyKeys;
import eu.ttbox.androgister.model.Product;

//@Repository
public class CassandraProductRepository extends AbstractEntityRepository<UUID, String> {

    private static final Logger log = LoggerFactory.getLogger(CassandraProductRepository.class);

//    ColumnFamily<String, String> CF_USER_INFO =
//            new ColumnFamily<String, String>(
//              "Standard1",              // Column Family Name
//              StringSerializer.get(),   // Key Serializer
//              StringSerializer.get());  // Column Serializer
    
    @Autowired
    public CassandraProductRepository(Keyspace keyspace) {
        super(keyspace);
    }

    public enum ProductColumn {
        uuid, name, description, priceHT;
    }

    @Autowired
    private EntityManagerImpl em;

    private ThriftColumnFamilyTemplate<String, UUID> productSalespointTemplate;

    @PostConstruct
    public void init() {
        productSalespointTemplate = new ThriftColumnFamilyTemplate<String, UUID>(keyspace, ColumnFamilyKeys.PRODUCT_SALESPOINT_CF.cfName, StringSerializer.get(), UUIDSerializer.get());
    }

    @Override
    public ThriftColumnFamilyTemplate<UUID, String> createTemplate(Keyspace keyspace) {
        ThriftColumnFamilyTemplate<UUID, String> template = new ThriftColumnFamilyTemplate<UUID, String>(keyspace, ColumnFamilyKeys.PRODUCT_CF.cfName, UUIDSerializer.get(), StringSerializer.get());
        template.addColumn("name", StringSerializer.get());
        template.addColumn("description", StringSerializer.get());
        template.addColumn("priceHT", BigDecimalSerializer.get());
        return template;
    }

    public void persist(Map<String, Object> entity) {
        // Define Id
        UUID uuid = (UUID) entity.get(ProductColumn.uuid.name());
        if (uuid == null) {
            uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
            entity.put(ProductColumn.uuid.name(), uuid);
        } else {
            // Get Previous
            getById(uuid);
        }
        // Save It
        ColumnFamilyUpdater<UUID, String> updater = template.createUpdater(uuid);
        for (Entry<String, Object> entry : entity.entrySet()) {
            String colName = entry.getKey();
            if (!colName.equals(ProductColumn.uuid.name())) {
                updater.setColumn(HFactory.createColumn(colName, entry.getValue()));
            }
        }
        template.update(updater);
        // Dep
        ColumnFamilyUpdater<String, UUID> spUpdater = productSalespointTemplate.createUpdater("salepoint");
        spUpdater.setByteArray(uuid, new byte[0]);
        productSalespointTemplate.update(spUpdater);
    }

    public void validateEntity(Product product) {
    }

    public void persist(Product product) {
        log.debug("Creating product : {}", product);
        validateEntity(product);

        if (product.serverId == null) {
            product.serverId = TimeUUIDUtils.getUniqueTimeUUIDinMillis(); // UUID.randomUUID();//
        }

        try {
            em.persist(product);
            addProductToSalespointline(product.serverId);
        } catch (Exception e) {
            log.error("Error Creating product {} : " + e.getMessage());
        }
        log.debug("Creating End product : {}", product);
    }

    public void addProductToSalespointline(UUID productUUID) {
        Mutator<String> mutator = HFactory.createMutator(keyspace, StringSerializer.get());
        mutator.insert("ALL", ColumnFamilyKeys.PRODUCT_SALESPOINT_CF.cfName, HFactory.createColumn(productUUID, "", UUIDSerializer.get(), StringSerializer.get()));
    }

    public void removeProductToSalespointline(UUID productUUID) {
        Mutator<String> mutator = HFactory.createMutator(keyspace, StringSerializer.get());
        mutator.addDeletion("ALL", ColumnFamilyKeys.PRODUCT_SALESPOINT_CF.cfName, productUUID, UUIDSerializer.get());
        mutator.execute();
    }

    @CacheEvict(value = "product-cache", key = "#product.login")
    public void remove(Product product) {
        log.debug("Deleting product : {} ", product);
        Mutator<UUID> mutator = HFactory.createMutator(keyspace, UUIDSerializer.get());
        mutator.addDeletion(product.serverId, ColumnFamilyKeys.PRODUCT_CF.cfName);
        mutator.execute();
        // template.deleteRow(product.uuid);

        // productSalespointTemplate.doExecuteSlice(key, predicate, mapper)
        // Depends
        UUID productUUID = product.serverId;
        removeProductToSalespointline(productUUID);
    }

    public Product findById(UUID uuid) {
        Product entity = null;
        try {
            entity = em.find(Product.class, uuid);
        } catch (Exception e) {
            log.warn("Exception while looking for product {} : {}", uuid, e.getMessage());

        }
        return entity;
    }

    // https://github.com/rantav/hector/blob/master/core/src/test/java/me/prettyprint/cassandra/service/template/ColumnFamilyTemplateTest.java
    // https://github.com/rantav/hector/wiki/Getting-started-%285-minutes%29
    public List<Map<String, Object>> finddAll() {
        // --- Version Slice --- //
        // HFactory.createRangeSlicesQuery(keyspace, StringSerializer.get(),
        // UUIDSerializer.get(), StringSerializer.get())

        ColumnSlice<UUID, String> query = HFactory.createSliceQuery(keyspace, StringSerializer.get(), UUIDSerializer.get(), StringSerializer.get()).setColumnFamily(ColumnFamilyKeys.PRODUCT_SALESPOINT_CF.cfName)//
                .setKey("ALL")//
                .setRange(null, null, false, 5)//
                .execute().get();
        // Read
        List<UUID> productUUIDs = new ArrayList<UUID>();
        List<HColumn<UUID, String>> resultCols = query.getColumns();
        for (HColumn<UUID, String> column : resultCols) {
            productUUIDs.add(column.getName());
        }

        // Multi get
        MultigetSliceQuery<UUID, String, String> multiQuery = HFactory.createMultigetSliceQuery(keyspace, UUIDSerializer.get(), StringSerializer.get(), StringSerializer.get()) //
                .setColumnFamily(ColumnFamilyKeys.PRODUCT_CF.cfName) //
                .setRange(null, null, false, Integer.MAX_VALUE) //
                .setKeys(productUUIDs);
        Rows<UUID, String, String> rows = multiQuery.execute().get();

        // --- Version VQL --- //
        // CqlQuery<UUID, String, String> cqlQuery = new CqlQuery<UUID, String,
        // String>(keyspace, UUIDSerializer.get(), StringSerializer.get(),
        // StringSerializer.get()) //
        // .setQuery("select * from Product")//
        // ;
        List<Map<String, Object>> entities = new LinkedList<Map<String, Object>>();
        // QueryResult<CqlRows<UUID, String, String>> result =
        // cqlQuery.execute();
        // CqlRows<UUID, String, String> rows = result.get();
        if (rows.getCount() > 0) {
            Iterator<Row<UUID, String, String>> it = rows.iterator();
            while (it.hasNext()) {
                Row<UUID, String, String> row = it.next();
                UUID key = row.getKey();
                ColumnSlice<String, String> colSlice = row.getColumnSlice();
                Map<String, Object> entity = readEntityRow(key, colSlice);
                entities.add(entity);
            }
        }
        return entities;
    }

    public Map<String, Object> readEntityRow(UUID key, ColumnSlice<String, String> colSlice) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("uuid", key);
        entity.put("name", getColumnStringValueByName(colSlice, "name"));
        entity.put("description", getColumnStringValueByName(colSlice, "description"));
        return entity;
    }

    public <T> T getColumnStringValueByName(ColumnSlice<String, T> colSlice, String columName) {
        HColumn<String, T> col = colSlice.getColumnByName(columName);
        if (col != null) {
            return col.getValue();
        } else {
            return null;
        }
    }

}

package eu.ttbox.androgister.repository;

import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.model.CqlRows;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
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
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hom.EntityManagerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

import eu.ttbox.androgister.config.cassandra.ColumnFamilyKeys;
import eu.ttbox.androgister.model.Product;

@Repository
public class ProductRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductRepository.class);

    @Autowired
    private EntityManagerImpl em;

    @Autowired
    private Keyspace keyspace;

    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static Validator validator = factory.getValidator();

    private ColumnFamilyTemplate<String, String> template;

    private ColumnFamilyTemplate<String, UUID> productSalespointTemplate;

    @PostConstruct
    public void init() {
        template = new ThriftColumnFamilyTemplate<String, String>(keyspace, ColumnFamilyKeys.PRODUCT_CF.cfName, StringSerializer.get(), StringSerializer.get());
        productSalespointTemplate = new ThriftColumnFamilyTemplate<String, UUID>(keyspace, ColumnFamilyKeys.PRODUCT_SALESPOINT_CF.cfName, StringSerializer.get(), UUIDSerializer.get());
    }

    public void validateEntity(Product product) {
        Set<ConstraintViolation<Product>> constraintViolations = validator.validate(product);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
        }
    }

    public void persist(Product product) {
        log.debug("Creating product : {}", product);
        validateEntity(product);

        if (product.uuid == null) {
            product.uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis(); // UUID.randomUUID();//
        }

        try {
            em.persist(product);
            addProductToSalespointline(product.uuid);
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
        mutator.addDeletion(product.uuid, ColumnFamilyKeys.PRODUCT_CF.cfName);
        mutator.execute();
        // Depends
        UUID productUUID = product.uuid;
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
        ColumnSlice<UUID, String> query = createSliceQuery(keyspace, StringSerializer.get(), UUIDSerializer.get(), StringSerializer.get()).setColumnFamily(ColumnFamilyKeys.PRODUCT_SALESPOINT_CF.cfName)//
                .setKey("ALL")//
                .setRange(null, null, true, 5)//
                .execute().get();
        // Read
        List<HColumn<UUID, String>> resultCols = query.getColumns();
        LinkedList<UUID> productUUIDs = new LinkedList<UUID>();
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

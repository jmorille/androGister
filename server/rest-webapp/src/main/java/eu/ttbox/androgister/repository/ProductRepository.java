package eu.ttbox.androgister.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

import com.google.common.base.Function;
import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.entitystore.DefaultEntityManager;
import com.netflix.astyanax.entitystore.MyEntityMapper;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.query.IndexQuery;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.UUIDSerializer;

import eu.ttbox.androgister.model.Product;

@Repository
public class ProductRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ProductRepository.class);

    public static ColumnFamily<UUID, String> CF_PRODUCT = ColumnFamily.newColumnFamily("Product", UUIDSerializer.get(), StringSerializer.get());
    public static ColumnFamily<String, UUID> CF_SALESPOINT_PRODUCT = ColumnFamily.newColumnFamily("SalepointProduct", StringSerializer.get(), UUIDSerializer.get());

    @Autowired
    public Keyspace keyspace;

    public DefaultEntityManager<Product, UUID> entityManager;
    public MyEntityMapper<Product, UUID> entityMapper;

    public enum ProductColumn {
        uuid, salepointId, versionDate, creationDate, name, description, priceHT;
    }

    @PostConstruct
    public void init() {
        entityManager = new DefaultEntityManager.Builder<Product, UUID>() //
                .withEntityType(Product.class) //
                .withKeyspace(keyspace) //
                .withColumnFamily(CF_PRODUCT) //
                .build();
        entityMapper = new MyEntityMapper<Product, UUID>(Product.class);
    }

    public Product findById(UUID uuid) {
        return entityManager.get(uuid);
    }

    public List<Product> findAll() {
        return entityManager.getAll();
    }

    public void findEntityUpdatedFrom(String salespointId, long timestamp, Function<Product, Boolean> callback) throws ConnectionException {

        IndexQuery<UUID, String> query = keyspace.prepareQuery(CF_PRODUCT) //
                .searchWithIndex() //
                .setRowLimit(20) // This is the page size
                .autoPaginateRows(true)//
                .withColumnSlice("versionDate")//
                .addExpression().whereColumn("salepointId").equals().value(salespointId) //
                .addExpression().whereColumn("versionDate").greaterThanEquals().value(timestamp) //
        ;

        int pageCount = 0;
        int rowCount = 0;
        OperationResult<Rows<UUID, String>> result;
        while (!(result = query.execute()).getResult().isEmpty()) {
            pageCount++;
            rowCount += result.getResult().size();
            for (Row<UUID, String> row : result.getResult()) {
                // TODO
                // Product entity = entityManager.get(row.getKey());
                // TODO
                UUID id = row.getKey();
                ColumnList<String> cl = row.getColumns();
                if (cl.isEmpty())
                    continue;
                Product entity = entityMapper.constructEntity(id, cl);
                callback.apply(entity);
            }
        }

    }

    public void persist(Product product) {
        long now = System.currentTimeMillis();
        persist(product,  Long.valueOf( now));
    }

    public void persist(Product product, Long updateTime) {
        LOG.debug("Creating product : {}", product);
        // validateEntity(product);
        if (product.serverId == null) {
            product.serverId = UUID.randomUUID();
            product.creationDate = updateTime;
        }
        product.versionDate = updateTime;
        entityManager.put(product);
        // Deps
        try {
//            addProductToSalespointline(product.salepointId, product.uuid, updateTime);
        } catch (Exception e) {
            throw new PersistenceException("failed to put entity ", e);
        }
        LOG.debug("Creating End product : {}", product);
    }

    private void addProductToSalespointline(String salespointId, UUID uuid, long updateTime) throws ConnectionException {
        MutationBatch mb = keyspace.prepareMutationBatch();
        mb.withRow(CF_SALESPOINT_PRODUCT, salespointId).putColumn(uuid, updateTime);
        mb.execute();
    }

    private void removeProductToSalespointline(String salespointId, UUID uuid) throws ConnectionException {
        MutationBatch mb = keyspace.prepareMutationBatch();
        mb.withRow(CF_SALESPOINT_PRODUCT, salespointId).deleteColumn(uuid);
        mb.execute();
    }

    public void persist(Map<String, Object> entity) {
        long updateTime = System.currentTimeMillis();
        // Define Id
        UUID uuid = (UUID) entity.get(ProductColumn.uuid.name());
        String salepointId = (String) entity.get(ProductColumn.salepointId.name());
        if (uuid == null) {
            uuid = UUID.randomUUID();// TimeUUIDUtils.getUniqueTimeUUIDinMillis();
            entity.put(ProductColumn.uuid.name(), uuid);
            entity.put(ProductColumn.creationDate.name(), updateTime);
        } else {
            // Get Previous
            // getById(uuid);
        }
        entity.put(ProductColumn.versionDate.name(), updateTime);
        // Save It
        MutationBatch m = keyspace.prepareMutationBatch();
        ColumnListMutation<String> updaterEntity = m.withRow(CF_PRODUCT, uuid); //
        for (Entry<String, Object> entry : entity.entrySet()) {
            String colName = entry.getKey();
            if (!colName.equals(ProductColumn.uuid.name())) {
                Object value = entry.getValue();
                setColumnListMutation(updaterEntity, colName, value);
            }
        }
        try {
            OperationResult<Void> result = m.execute();
            // Deps
            addProductToSalespointline(salepointId, uuid, updateTime);
        } catch (Exception e) {
            throw new PersistenceException("failed to put entity ", e);
        }
    }

    public void setColumnListMutation(ColumnListMutation<String> updaterEntity, String columnName, Object value) {
        if (value == null) {
            updaterEntity.putColumn(columnName, (byte[]) value);
        } else if (value instanceof String) {
            updaterEntity.putColumn(columnName, (String) value);
        } else if (value instanceof UUID) {
            updaterEntity.putColumn(columnName, (UUID) value);
        } else if (value instanceof Date) {
            updaterEntity.putColumn(columnName, (Date) value);
        } else if (value instanceof Integer) {
            updaterEntity.putColumn(columnName, (Integer) value);
        } else if (value instanceof Long) {
            updaterEntity.putColumn(columnName, (Long) value);
        }
    }

    @CacheEvict(value = "product-cache", key = "#product.uuid")
    public void remove(Product product) {
        LOG.debug("Deleting product : {} ", product);
        remove(product.serverId, product.salepointId);
    }

    @CacheEvict(value = "product-cache", key = "#uuid")
    public void remove(UUID uuid, String salepointId) {
        LOG.debug("Deleting product : {} ", uuid);

        // entityManager.delete(uuid);
        // removeProductToSalespointline(salepointId, uuid);
        // Deps
        try {
            MutationBatch mb = keyspace.prepareMutationBatch();// public
                                                               // entityManager.newMutationBatch()
            mb.withRow(CF_PRODUCT, uuid).delete();
            mb.withRow(CF_SALESPOINT_PRODUCT, salepointId).deleteColumn(uuid);
            mb.execute();
        } catch (Exception e) {
            throw new PersistenceException("failed to put entity ", e);
        }
    }

}

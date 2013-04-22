package eu.ttbox.androgister.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.entitystore.DefaultEntityManager;
import com.netflix.astyanax.entitystore.EntityManager;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.UUIDSerializer;

import eu.ttbox.androgister.model.Product;

@Repository
public class ProductRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ProductRepository.class);

    public static ColumnFamily<UUID, String> CF_PRODUCT = ColumnFamily.newColumnFamily("Product", UUIDSerializer.get(), StringSerializer.get());

    @Autowired
    public Keyspace keyspace;

    public EntityManager<Product, UUID> entityManager;

    public enum ProductColumn {
        uuid, name, description, priceHT;
    }

    @PostConstruct
    public void init() {
        entityManager = new DefaultEntityManager.Builder<Product, UUID>() //
                .withEntityType(Product.class) //
                .withKeyspace(keyspace) //
                .withColumnFamily(CF_PRODUCT) //
                .build();
    }

    public Product findById(UUID uuid) {
        return entityManager.get(uuid);
    }

    public List<Product> findAll() {
        return entityManager.getAll();
    }

    public void persist(Product product) {
        LOG.debug("Creating product : {}", product);
        // validateEntity(product);
        if (product.uuid == null) {
            product.uuid = UUID.randomUUID();// TimeUUIDUtils.getUniqueTimeUUIDinMillis();
        }
        entityManager.put(product);
        // addProductToSalespointline(product.uuid);

        LOG.debug("Creating End product : {}", product);
    }

    public void persist(Map<String, Object> entity) {
        // Define Id
        UUID uuid = (UUID) entity.get(ProductColumn.uuid.name());
        if (uuid == null) {
            uuid = UUID.randomUUID();// TimeUUIDUtils.getUniqueTimeUUIDinMillis();
            entity.put(ProductColumn.uuid.name(), uuid);
        } else {
            // Get Previous
            // getById(uuid);
        }
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
        } catch (ConnectionException e) {
            LOG.error("Error Persisting Entity Product : " + e.getMessage(), e);
        }
        // Dep
        // ColumnFamilyUpdater<String, UUID> spUpdater =
        // productSalespointTemplate.createUpdater("salepoint");
        // spUpdater.setByteArray(uuid, new byte[0]);
        // productSalespointTemplate.update(spUpdater);
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
        remove(product.uuid); 
    }

    @CacheEvict(value = "product-cache", key = "#uuid")
    public void remove(UUID uuid) {
        LOG.debug("Deleting product : {} ", uuid);
        entityManager.delete( uuid);
        // removeProductToSalespointline(productUUID);
    }

    
}

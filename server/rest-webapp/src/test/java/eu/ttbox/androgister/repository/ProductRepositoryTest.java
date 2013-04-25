package eu.ttbox.androgister.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.ttbox.androgister.model.Product;
import eu.ttbox.androgister.repository.ProductRepository.ProductColumn;
import eu.ttbox.androgister.web.rest.ProductService;

public class ProductRepositoryTest extends AbstractCassandraTest {

    private static final Logger LOG = LoggerFactory.getLogger(ProductRepositoryTest.class);

    @Autowired
    ProductRepository repository;
    
    private Map<String, Object> createMockEntity(int id) {
        Map<String, Object> entity = new HashMap<String, Object>(); 
        entity.put(ProductColumn.name.name(), "Product name " + id);
        entity.put(ProductColumn.description.name(), "Product description " + id);
        return entity;
    }

    @Test
    public void testPersistCrudMap() {
        Map<String, Object> entity = createMockEntity(1);
        // Persist
        repository.persist(entity);
        UUID uuid = (UUID)entity.get(ProductColumn.uuid.name());
        LOG.debug("Save entity with id : {}", uuid);
        Assert.assertNotNull(uuid);
        // Read
        Product readEntity = repository.findById(uuid);
        Assert.assertNotNull(readEntity);
        // Delete
        String salespointId = "ttbox";
        repository.remove(uuid, salespointId);
        // Re Read
        Product reReadEntity = repository.findById(uuid);
        Assert.assertNull(reReadEntity);    
    }

//    @Test
//    public void testFindById() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testFinddAll() {
//        fail("Not yet implemented");
//    }

}

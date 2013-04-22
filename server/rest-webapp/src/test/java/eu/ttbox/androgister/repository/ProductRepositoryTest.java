package eu.ttbox.androgister.repository;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.ttbox.androgister.repository.ProductRepository.ProductColumn;

public class ProductRepositoryTest extends AbstractCassandraTest {

    @Autowired
    ProductRepository repository;
    
    private Map<String, Object> createMockEntity(int id) {
        Map<String, Object> entity = new HashMap<String, Object>(); 
        entity.put(ProductColumn.name.name(), "Product name " + id);
        entity.put(ProductColumn.description.name(), "Product description " + id);
        return entity;
    }

    @Test
    public void testPersist() {
        Map<String, Object> entity = createMockEntity(1);
        repository.persist(entity);
    }

    @Test
    public void testFindById() {
        fail("Not yet implemented");
    }

    @Test
    public void testFinddAll() {
        fail("Not yet implemented");
    }

}

package eu.ttbox.androgister.repository;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.entitystore.DefaultEntityManager;
import com.netflix.astyanax.entitystore.EntityManager;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.StringSerializer;

import eu.ttbox.androgister.model.Salespoint;

@Repository
public class SalespointRepository {

	private static final Logger LOG = LoggerFactory.getLogger(SalespointRepository.class);
	
    public static ColumnFamily<String, String> CF_SALESPOINT = ColumnFamily.newColumnFamily("Salespoint", StringSerializer.get(), StringSerializer.get());

    @Autowired
    public Keyspace keyspace;

    public EntityManager<Salespoint, String> entityManager;
   
    
    @PostConstruct
    public void init() {
        entityManager = new DefaultEntityManager.Builder<Salespoint, String>() //
                .withEntityType(Salespoint.class) //
                .withKeyspace(keyspace) //
                .withColumnFamily(CF_SALESPOINT) //
                .build(); 
    }

    public void persist(Salespoint entity) {
        entityManager.put(entity);
    }

    public Salespoint getById(String entityId) {
        return entityManager.get(entityId);
    }

    public List<Salespoint> getAll() {
        return entityManager.getAll();
    }
 
    public List<Salespoint> getFindAll() {
        return entityManager.find("SELECT * from Salespoint LIMIT 5");
    }
    
    public void remove(String id) {
        entityManager.delete(id);
    }

}

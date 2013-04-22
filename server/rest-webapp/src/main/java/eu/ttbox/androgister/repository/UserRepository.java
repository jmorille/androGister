package eu.ttbox.androgister.repository;

import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.entitystore.DefaultEntityManager;
import com.netflix.astyanax.entitystore.EntityManager;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.UUIDSerializer;

import eu.ttbox.androgister.model.User;

@Repository
public class UserRepository {

    public static ColumnFamily<UUID, String> CF_USER = ColumnFamily.newColumnFamily("User", UUIDSerializer.get(), StringSerializer.get());

    @Autowired
    public Keyspace keyspace;

    public EntityManager<User, UUID> entityManager;
   
    
    @PostConstruct
    public void init() {
        entityManager = new DefaultEntityManager.Builder<User, UUID>() //
                .withEntityType(User.class) //
                .withKeyspace(keyspace) //
                .withColumnFamily(CF_USER) //
                .build(); 
    }

    public User getById(UUID entityId) {
        return entityManager.get(entityId);
    }

    
    public User findUserByLogin(String email) {
        UUID entityId = UUID.fromString(email);
        return getById(entityId);
    }
    
    
    public void persist(User entity) {
        if (entity.uuid == null) {
            entity.uuid = UUID.randomUUID();// TimeUUIDUtils.getUniqueTimeUUIDinMillis();
        } 
        entityManager.put(entity);
    }

 
    public void remove(User entity) {
        if (entity != null ) {
            remove(entity.uuid);
        }        
    }
    
    public void remove(UUID id) {
        entityManager.delete(id);
    }
    
    public List<User> getAll() {
        return entityManager.getAll();
    }
 
    public List<User> getFindAll() {
        return entityManager.find("SELECT * from User LIMIT 5");
    }

    public List<User > findUser(int firstResult, int maxResult) {
         return getFindAll();
    }



  
    

}

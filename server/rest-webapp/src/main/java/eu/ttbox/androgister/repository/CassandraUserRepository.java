package eu.ttbox.androgister.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.model.CqlRows;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hom.CFMappingDef;
import me.prettyprint.hom.ClassCacheMgr;
import me.prettyprint.hom.EntityManagerImpl;
import me.prettyprint.hom.HectorObjectMapper;
import me.prettyprint.hom.HectorObjectMapperHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import eu.ttbox.androgister.config.cassandra.ColumnFamilyKeys;
import eu.ttbox.androgister.model.User;

@Repository
public class CassandraUserRepository {

    private static final Logger log = LoggerFactory.getLogger(CassandraUserRepository.class);

    @Autowired
    private EntityManagerImpl em;

    @Autowired
    private Keyspace keyspaceOperator;

    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static Validator validator = factory.getValidator();

    private ColumnFamilyTemplate<String, String> template;

    @PostConstruct
    public void init() {
        template = new ThriftColumnFamilyTemplate<String, String>(keyspaceOperator, ColumnFamilyKeys.USER_CF.cfName, StringSerializer.get(), StringSerializer.get());
    }

    public List<User> findUser(@RequestParam(value = "s", defaultValue = "0") int firstResult, @RequestParam(value = "p", defaultValue = "10") int maxResult) {
//        ClassCacheMgr cacheMgr = new ClassCacheMgr();
//        HectorObjectMapper objMapper = new HectorObjectMapper(cacheMgr);
//        cacheMgr.initializeCacheForClass(User.class);
//        CFMappingDef<User> cfMapDef = cacheMgr.getCfMapDef(User.class, false);

        CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspaceOperator, StringSerializer.get(), StringSerializer.get(), StringSerializer.get()) //
                .setQuery("select * from User")//
                ;
        QueryResult<CqlRows<String, String, String>> result = cqlQuery.execute();
        CqlRows<String, String, String> rows = result.get();

        Iterator<Row<String, String, String>> it = rows.iterator();
        while (it.hasNext()) {
            Row<String, String, String> row = it.next();
            User user = null; // HectorObjectMapperHelper.getObject(objMapper,
                              // cfMapDef, keyspaceOperator,
                              // cfMapDef.getEffectiveColFamName(), row);
            String key = row.getKey();
            ColumnSlice<String, String> colSlice = row.getColumnSlice();
//            colSlice.getColumnByName(columnName)
            List<HColumn<String, String>> colSlices = row.getColumnSlice().getColumns();  
//            colSlices.
            log.info("Row : " + row + " =======> " + user);
        }
        List<User> users = new ArrayList<User>();
        return users;
    }

    @CacheEvict(value = "user-cache", key = "#user.login")
    public void createUser(User user) {
        log.debug("Creating user : {}", user);
        // Set<ConstraintViolation<User>> constraintViolations =
        // validator.validate(user, ContraintsUserCreation.class);
        // if (!constraintViolations.isEmpty()) {
        // throw new ConstraintViolationException(new
        // HashSet<ConstraintViolation<?>>(constraintViolations));
        // }
        try {
            em.persist(user);
        } catch (Exception e) {
            log.error("Erro Creating user {} : " + e.getMessage());
        }
        log.debug("Creating End user : {}", user);
    }

    @CacheEvict(value = "user-cache", key = "#user.login", beforeInvocation = true)
    public void updateUser(User user) throws ConstraintViolationException, IllegalArgumentException {
        log.debug("Updating user : {}", user);

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
        }
        em.persist(user);
    }

    @CacheEvict(value = "user-cache", key = "#user.login")
    public void deleteUser(User user) {
        if (log.isDebugEnabled()) {
            log.debug("Deleting user : " + user);
        }
        Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, StringSerializer.get());
        mutator.addDeletion(user.login, ColumnFamilyKeys.USER_CF.cfName);
        mutator.execute();
    }

    @Cacheable("user-cache")
    public User findUserByLogin(String login) {
        User user;
        try {
            user = em.find(User.class, login);
        } catch (Exception e) {
            // if (log.isDebugEnabled()) {
            log.warn("Exception while looking for user " + login + " : " + e.toString());
            // }
            return null;
        }
        // if (user != null) {
        // user.setStatusCount(counterRepository.getStatusCounter(login));
        // user.setFollowersCount(counterRepository.getFollowersCounter(login));
        // user.setFriendsCount(counterRepository.getFriendsCounter(login));
        // if (user.getIsNew() == null) {
        // user.setIsNew(false);
        // }
        // }
        return user;
    }
}

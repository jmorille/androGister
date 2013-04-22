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
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hom.EntityManagerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import eu.ttbox.androgister.config.cassandra.ColumnFamilyKeys;
import eu.ttbox.androgister.model.User;
import eu.ttbox.androgister.model.UserLight;

//@Repository
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

    public List<UserLight> findUser(@RequestParam(value = "s", defaultValue = "0") int firstResult, @RequestParam(value = "p", defaultValue = "10") int maxResult) {
        // ClassCacheMgr cacheMgr = new ClassCacheMgr();
        // HectorObjectMapper objMapper = new HectorObjectMapper(cacheMgr);
        // cacheMgr.initializeCacheForClass(User.class);
        // CFMappingDef<User> cfMapDef = cacheMgr.getCfMapDef(User.class,
        // false);

        CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspaceOperator, StringSerializer.get(), StringSerializer.get(), StringSerializer.get()) //
                .setQuery("select * from User")//
        ;
        // cqlQuery.sestColumnNameSerializer(columnNameSerializer)
        QueryResult<CqlRows<String, String, String>> result = cqlQuery.execute();
        CqlRows<String, String, String> rows = result.get();

        List<UserLight> users = new ArrayList<UserLight>();
        Iterator<Row<String, String, String>> it = rows.iterator();
        while (it.hasNext()) {
            Row<String, String, String> row = it.next();
            // User user = null; //
            // HectorObjectMapperHelper.getObject(objMapper,
            // cfMapDef, keyspaceOperator,
            // cfMapDef.getEffectiveColFamName(), row);
            String key = row.getKey();
            UserLight user = new UserLight();
            ColumnSlice<String, String> colSlice = row.getColumnSlice();
            user.login = key;
            user.username = getColumnStringValueByName(colSlice, "username");
            user.lastName = getColumnStringValueByName(colSlice, "lastName");
            user.firstName = getColumnStringValueByName(colSlice, "firstName");
            // colSlices.
            users.add(user);
            log.info("Row : " + row + " =======> " + user);
        }

        return users;
    }

    public <T> T getColumnStringValueByName(ColumnSlice<String, T> colSlice, String columName) {
        HColumn<String, T> col = colSlice.getColumnByName(columName);
        if (col != null) {
            return col.getValue();
        } else {
            return null;
        }
    }

    @CacheEvict(value = "user-cache", key = "#user.login")
    public void persist(User user) {
        log.debug("Creating user : {}", user);
        // Set<ConstraintViolation<User>> constraintViolations =
        // validator.validate(user, ContraintsUserCreation.class);
        // if (!constraintViolations.isEmpty()) {
        // throw new ConstraintViolationException(new
        // HashSet<ConstraintViolation<?>>(constraintViolations));
        // }
        if (user.uuid == null) {
            user.uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
        }

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
        log.debug("Deleting user : {}", user);
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
            log.warn("Exception while looking for user " + login + " : " + e.getMessage());
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

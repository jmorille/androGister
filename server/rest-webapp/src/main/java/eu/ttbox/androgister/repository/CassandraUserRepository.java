package eu.ttbox.androgister.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
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
import eu.ttbox.androgister.model.validation.ContraintsUserCreation;

@Repository
public class CassandraUserRepository {

    private static final Logger log = LoggerFactory.getLogger(CassandraUserRepository.class);

    @Autowired
    private EntityManagerImpl em;

    @Autowired
    private Keyspace keyspaceOperator;

    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static Validator validator = factory.getValidator();

    public List<User> findUser(@RequestParam(value = "s", defaultValue = "0") int firstResult, @RequestParam(value = "p", defaultValue = "10") int maxResult) {
        Query query = em.createQuery("from User");
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResult);
        List<User> users = query.getResultList();
        return users;
    }
    
    @CacheEvict(value = "user-cache", key = "#user.login")
    public void createUser(User user) {
        log.debug("Creating user : {}", user);
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user, ContraintsUserCreation.class);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
        }
        em.persist(user);
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
            if (log.isDebugEnabled()) {
                log.warn("Exception while looking for user " + login + " : " + e.toString());
            }
            return null;
        }
//        if (user != null) {
            // user.setStatusCount(counterRepository.getStatusCounter(login));
            // user.setFollowersCount(counterRepository.getFollowersCounter(login));
            // user.setFriendsCount(counterRepository.getFriendsCounter(login));
//            if (user.getIsNew() == null) {
//                user.setIsNew(false);
//            }
//        }
        return user;
    }
}

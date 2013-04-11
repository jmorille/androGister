package eu.ttbox.androgister.web.rest;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import eu.ttbox.androgister.web.model.User;

@Controller
@RequestMapping("/users")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, timeout = 1000)
public class UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@PersistenceContext
	private EntityManager entityManager;

	private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private static Validator validator = factory.getValidator();

	@RequestMapping(value = "/{userId}", method = RequestMethod.GET, headers = "Accept=application/json")
	@ResponseBody
	public ResponseEntity<User> getUserById(@PathVariable Long userId) {
		User user = entityManager.find(User.class, userId);
		LOG.info("Get Uer by Id {} : {} ", userId, user);
		// Result
		return user == null ? new ResponseEntity<User>(HttpStatus.NOT_FOUND) : new ResponseEntity<User>(user, HttpStatus.OK);
	}

	public User getUserByEmail(String email) { 
		TypedQuery<User> query = entityManager.createQuery("from User where email = :email", User.class) //
		 .setParameter("email", email)//
		 .setMaxResults(2);

		List<User> results = query.getResultList();
		if (results.isEmpty())
			return null;
		else if (results.size() == 1)
			return results.get(0);
		else return results.get(0);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET, headers = "Accept=application/json")
	@ResponseBody
	public List<User> findUser(@RequestParam(value = "s", defaultValue = "0") int firstResult, @RequestParam(value = "p", defaultValue = "10") int maxResult) {
		Query query = entityManager.createQuery("from User");
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		List<User> users = query.getResultList();
		return users;
	}

	/**
	 * @see http 
	 *      ://fcamblor.wordpress.com/2012/05/21/valider-ses-pojos-avec-bean-
	 *      validation-spring-mvc-et-jquery/ Fait troublant : la validation est
	 *      un rare cas où Spring n’a pas une gestion homogène des exceptions !
	 *      Si vous validez un body content (annotation @RequestBody), vous
	 *      obtiendrez une MethodArgumentNotValidException, à contrario, si vous
	 *      validez des query parameters (pas d’annotation @RequestBody), vous
	 *      obtiendrez une BindException. Ces 2 exceptions encapsulent toutefois
	 *      un BindingResult, contenant les erreurs de validation.
	 * 
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
	@ResponseBody
	public List<ObjectError> handleValidationFailure(MethodArgumentNotValidException exception) {
		return exception.getBindingResult().getAllErrors();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public User createUser(@RequestBody @Validated({ Default.class }) User user) {
		LOG.info("Create User {}", user);
		entityManager.persist(user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public User updateUser(@RequestBody User user) {
		LOG.info("Merge User {}", user);
		entityManager.merge(user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/{userId}/delete", method = RequestMethod.GET, headers = "Accept=application/json")
	public String deleteUser(@PathVariable Long userId) {
		LOG.debug("deleteUser Id : {} ", userId);
		ResponseEntity<User> entity = getUserById(userId);
		if (entity.getBody() != null) {
			entityManager.remove(entity.getBody());
		}
		return "redirect:/service/users/";
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<User> deleteUserByDel(@PathVariable Long userId) {
		LOG.debug("deleteUser Id : {} ", userId);
		ResponseEntity<User> entity = getUserById(userId);
		if (entity.getBody() != null) {
			entityManager.remove(entity.getBody());
			return new ResponseEntity<User>(HttpStatus.OK);
		} else {
			return entity;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@RequestMapping(value = "/init", method = RequestMethod.GET)
	@ResponseBody
	public int initUserList() {
		int count = 0;
		for (int i = 1; i < 20; i++) {
			User user = createMockUser(i);
			entityManager.persist(user);
			LOG.debug("persist User  : {} ", user);
			count++;
		}
		return count;
	}

	private User createMockUser(int userId) {
		User user = new User();
		// user.id = Long.valueOf(userId);
		user.firstname = String.format("Prenom %s", userId);
		user.lastname = String.format("Nom %s", userId);
		user.password = "toto"; // UUID.randomUUID().toString();
		return user;
	}

}

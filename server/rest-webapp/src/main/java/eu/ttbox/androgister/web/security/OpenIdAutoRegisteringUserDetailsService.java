package eu.ttbox.androgister.web.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Component;

import eu.ttbox.androgister.web.model.User;
import eu.ttbox.androgister.web.rest.UserService;

/**
 * UserDetails Service to be used with OpenId authentication.
 * It auto-registers the user based on its "email" OpenId attribute
 * (that must have been asked to the OpenId provider). 
 */
@Component
public class OpenIdAutoRegisteringUserDetailsService implements
        AuthenticationUserDetailsService<OpenIDAuthenticationToken> {

    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String FIRSTNAME_ATTRIBUTE = "firstname";
    private static final String LASTNAME_ATTRIBUTE = "lastname";
    private static final String FULLNAME_ATTRIBUTE = "fullname";

    private final Logger log = LoggerFactory.getLogger(OpenIdAutoRegisteringUserDetailsService.class);
 
  
    @Autowired
    private AppUserDetailsService userDetailsService; // => handles grantedAuthorities

    @Autowired
    private UserService userService; 
    
    @Override
    public UserDetails loadUserDetails(OpenIDAuthenticationToken token) throws UsernameNotFoundException {

        String login = getAttributeValue(token, EMAIL_ATTRIBUTE);
        // Important security assumption : here we are trusting the OpenID provider
        // to give us an email that has already been verified to belong to the user

        if (login == null) {
            String msg = "OpendId response did not contain the user email";
            log.error(msg);
            throw new UsernameNotFoundException(msg);
        }
        if (!login.contains("@")) {
            if (log.isDebugEnabled()) {
                log.debug("User login {} from OpenId response is incorrect.", login);
            }
            throw new UsernameNotFoundException("OpendId response did not contains a valid user email");
        }

        // Automatically create OpenId users in Tatami :
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(login);
            
        } catch (UsernameNotFoundException e) {
            if (log.isInfoEnabled()) {
                log.info("User with login : \"" + login + "\" doesn't exist yet in Tatami database - creating it...");
            }
            userDetails = getNewlyCreatedUserDetails(token);
        }
        return userDetails;
    }

    private org.springframework.security.core.userdetails.User getNewlyCreatedUserDetails(OpenIDAuthenticationToken token) {
        String login = getAttributeValue(token, EMAIL_ATTRIBUTE);
        String firstName = getAttributeValue(token, FIRSTNAME_ATTRIBUTE);
        String lastName = getAttributeValue(token, LASTNAME_ATTRIBUTE);

        String fullName = getAttributeValue(token, FULLNAME_ATTRIBUTE);
        if (firstName == null && lastName == null) {
            // if we haven't first nor last name, we use fullName as last name to begin with :
            lastName = fullName;
        }

        User user = new User();
        // Note : The email could change... and the OpenId not
        // moreover an OpenId account could potentially be associated with several email addresses
        // so we store it for future use case :
        user.openIdUrl = token.getName();

        user.email = login;
        user.firstname = firstName;
        user.lastname = lastName;
        
        // Create User  
        userService.createUser(user);

        return userDetailsService.getAppUserDetails(login, user.password );
    }

    private String getAttributeValue(OpenIDAuthenticationToken token, String name) {
        String value = null;
        for (OpenIDAttribute attribute : token.getAttributes()) {
            if (name.equals(attribute.getName())) {
                List<String> values = attribute.getValues();
                String firstValue = values.isEmpty() ? null : values.iterator().next();
                value = firstValue;
                break;
            }
        }
        return value;
    }

}

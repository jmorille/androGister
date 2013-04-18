package eu.ttbox.androgister.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import eu.ttbox.androgister.model.User;
import eu.ttbox.androgister.repository.CassandraUserRepository;

//@Service
public class AppUserDetailsService implements UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(AppUserDetailsService.class);

	private Collection<GrantedAuthority> userGrantedAuthorities = new ArrayList<GrantedAuthority>();

	private Collection<GrantedAuthority> adminGrantedAuthorities = new ArrayList<GrantedAuthority>();

	private Collection<String> adminUsers = null;

    @Autowired
    private CassandraUserRepository userService; 
      
	@PostConstruct 
	public void init() {
		if (userGrantedAuthorities.size() == 0) { // to prevent a bug that makes
													// this bean initialized
													// twice
			// Roles for "normal" users
			GrantedAuthority roleUser = new SimpleGrantedAuthority("ROLE_USER");
			userGrantedAuthorities.add(roleUser);

			// Roles for "admin" users, configured in tatami.properties
			GrantedAuthority roleAdmin = new SimpleGrantedAuthority("ROLE_ADMIN");
			adminGrantedAuthorities.add(roleUser);
			adminGrantedAuthorities.add(roleAdmin);

			String adminUsersList = "jmorille@generali.fr,jmorille@gmail.com,admin";
			String[] adminUsersArray = adminUsersList.split(",");
			adminUsers = new ArrayList<String>(Arrays.asList(adminUsersArray));
			if (log.isDebugEnabled()) {
				for (String admin : adminUsers) {
					log.debug("Initialization : user \"" + admin + "\" is an administrator");
				}
			}
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  
        User userFromCassandra = userService.findUserByLogin(username);
        if ( userFromCassandra == null) {
            throw new UsernameNotFoundException("User " + username + " was not found in Db");
        }
        
		AppUserDetails user = getAppUserDetails(username, userFromCassandra.password );
		return user;
	}

	public AppUserDetails getAppUserDetails(String username, String password) {
		// TODO Load in DB
		Collection<GrantedAuthority> grantedAuthorities;
		if (adminUsers.contains(username)) {
			if (log.isDebugEnabled()) {
				log.debug("User \"{}\" is an administrator", username);
			}
			grantedAuthorities = adminGrantedAuthorities;
		} else {
			grantedAuthorities = userGrantedAuthorities;
		}
 
		AppUserDetails user = new AppUserDetails(username, password, grantedAuthorities); 
		return user;
	}

}

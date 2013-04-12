package eu.ttbox.androgister.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class AppUserDetails extends User  {

	 
	private static final long serialVersionUID = -3719053220666292161L;

	public AppUserDetails(String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities); 
	}

 

}

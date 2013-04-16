package eu.ttbox.androgister.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.ttbox.androgister.model.validation.ContraintsUserCreation;

//@Entity
//@Table(name = "User")
public class UserLight {

//	@Id
	@Column(name = "uuid")
	public UUID uuid;
	
	@NotEmpty(message = "Login is mandatory.", groups = { ContraintsUserCreation.class, Default.class })
	@NotNull(message = "Login is mandatory.", groups = { ContraintsUserCreation.class, Default.class })
	@Email(message = "Email is invalid.") 
	@Id
	@JsonIgnore
	public String login;

	@Column(name = "username")
	public String username;

	@Size(min = 0, max = 50)
	@Column(name = "firstName")
	public String firstName;

	@Size(min = 0, max = 50)
	@Column(name = "lastName")
	public String lastName;

	@Override
	public String toString() {
		return new StringBuffer(64).append("User [id=").append(login).append(", version=").append(login) //
				.append(", firstname=").append(firstName)//
				.append(", lastname=").append(lastName) //
				.append("]") //
				.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserLight other = (UserLight) obj;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		return true;
	}



	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}

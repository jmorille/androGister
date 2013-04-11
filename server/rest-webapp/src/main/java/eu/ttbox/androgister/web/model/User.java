package eu.ttbox.androgister.web.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import org.hibernate.validator.constraints.Email;

import eu.ttbox.androgister.web.model.validation.ContraintsUserCreation;

@Entity 
public class User {

    @Id
    @GeneratedValue
    public Long id;

    @Version
    public Long version;

    public String openIdUrl;
    
    @Email
    public String email;
    
    @Size(min = 2, max = 50)
    public String firstname;

    @Size(min = 0, max = 50)
    public String lastname;


    @NotNull(message = "Password is mandatory.", groups = {ContraintsUserCreation.class, Default.class}) 
    @Size(min=3, groups = {ContraintsUserCreation.class, Default.class})
    public String password;
    
    @Override
    public String toString() {
        return new StringBuffer(64).append("User [id=").append(id).append(", version=").append(version) //
                        .append(", firstname=").append(firstname)//
                        .append(", lastname=").append(lastname) //
                        .append("]") //
                        .toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        User other = (User ) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}

package eu.ttbox.androgister.model.user;

import java.io.Serializable;

@Deprecated
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    public long id = -1;
    public String lastname;
    public String firstname;
    public String login;
    public String password;

    public String tag;

    public User setId(long id) {
        this.id = id;
        return this;
    }

    public User setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public User setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public User setLogin(String login) {
        this.login = login;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setTag(String tag) {
        this.tag = tag;
        return this;
    }

}

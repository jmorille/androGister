package eu.ttbox.androgister.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

 

@Entity
public class Salespoint {

    @Id
    public String id;
    
    @Column(name="name")
    public String name;

    @Override
    public String toString() {
        return "Salespoint [id=" + id + ", name=" + name + "]";
    }
    
    
}

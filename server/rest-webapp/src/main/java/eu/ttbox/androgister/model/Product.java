package eu.ttbox.androgister.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Product")
public class Product {

    @Id
    public UUID uuid;

    @Column(name = "creationDate")
    public Long creationDate;

    @Column(name = "creationDate")
    public Long versionDate;

    @Column(name = "name")
    public String name;
    
    @Column(name = "description")
    public String description;
    
    @Column(name = "priceHT")
    public Long priceHT;
    
    @Column(name = "tagId")
    public Long tagId;
    
    @Column(name = "taxeId")
    public Long taxeId;

     

    @Override
    public String toString() {
        return "Product [uuid=" + uuid + ", name=" + name + "]";
    }
    
    
    
    
}

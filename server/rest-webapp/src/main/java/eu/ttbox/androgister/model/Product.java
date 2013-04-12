package eu.ttbox.androgister.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "Product")
public class Product {

    @Id
    @GeneratedValue
    public Long id;
    
}

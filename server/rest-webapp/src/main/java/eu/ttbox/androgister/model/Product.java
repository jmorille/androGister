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
    public long priceHT;
    
    @Column(name = "tagId")
    public long tagId;
    
    @Column(name = "taxeId")
    public Long taxeId;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Long versionDate) {
        this.versionDate = versionDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPriceHT() {
        return priceHT;
    }

    public void setPriceHT(long priceHT) {
        this.priceHT = priceHT;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public Long getTaxeId() {
        return taxeId;
    }

    public void setTaxeId(Long taxeId) {
        this.taxeId = taxeId;
    }
    
    
    
    
}

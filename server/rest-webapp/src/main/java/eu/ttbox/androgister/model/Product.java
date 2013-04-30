package eu.ttbox.androgister.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import eu.ttbox.androgister.web.sync.EntitySyncable;

@Entity
@Table(name = "Product")
public class Product  implements EntitySyncable {

    @Id
    public UUID serverId;

    @Column(name = "creationDate")
    public Long creationDate;

    @Column(name = "versionDate")
    public Long versionDate;

    @Column(name = "salepointId")
    public String salepointId;

    
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
        return "Product [serverId=" + serverId + ", name=" + name + "]";
    }
 

	@Override
	public UUID getServerId() { 
		return serverId;
	} 
	@Override
	public void setServerId(UUID serverId) {
		this.serverId = serverId;
	}
 

	@Override
	public Long getVersionDate() { 
		return versionDate;
	}
 
	@Override
	public void setVersionDate(Long versionDate) {
		this.versionDate = versionDate;
	}
    
    
    
    
}

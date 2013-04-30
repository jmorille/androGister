package eu.ttbox.androgister.web.sync;

import java.util.UUID;

public interface EntitySyncable  {
 
	public UUID getServerId() ;
	
	public void setServerId(UUID serverId) ;

	public Long getVersionDate();

	public void setVersionDate(Long versionDate) ;

}

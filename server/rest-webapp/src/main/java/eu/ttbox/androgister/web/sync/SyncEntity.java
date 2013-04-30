package eu.ttbox.androgister.web.sync;

public class SyncEntity<T> {

	public T serverId;

	public Long versionDate;

	@Override
	public String toString() {
		return "SyncEntity [serverId=" + serverId + ", versionDate=" + versionDate + "]";
	}

	public T getServerId() {
		return serverId;
	}

	public void setServerId(T serverId) {
		this.serverId = serverId;
	}

	public Long getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Long versionDate) {
		this.versionDate = versionDate;
	}

	
}

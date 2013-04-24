package eu.ttbox.androgister.web.sync;

public class SyncEntity<T> {

	public T serverId;

	public Long versionDate;

	@Override
	public String toString() {
		return "SyncEntity [serverId=" + serverId + ", versionDate=" + versionDate + "]";
	}

	
}

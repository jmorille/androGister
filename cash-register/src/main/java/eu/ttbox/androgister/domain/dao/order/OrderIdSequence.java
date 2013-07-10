package eu.ttbox.androgister.domain.dao.order;

import java.util.concurrent.atomic.AtomicLong;

public class OrderIdSequence {

	private AtomicLong cacheCounter;
	
	private long cacheMidnight = -1;
	
	public void initCacheCounter(AtomicLong cacheCounter, long cacheMidnight) {
		this.cacheCounter = cacheCounter;
		this.cacheMidnight = cacheMidnight;
	}
	
	public boolean isValidCache(long dateMidenight) {
		return dateMidenight==cacheMidnight;
	}
	
	public void invalidateCacheCounter() {
		cacheMidnight = -1;
		cacheCounter = null;
	}
	
	public long incrementAndGet() {
		return cacheCounter.incrementAndGet();
	}
}

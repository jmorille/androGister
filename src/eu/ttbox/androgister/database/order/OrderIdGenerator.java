package eu.ttbox.androgister.database.order;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import eu.ttbox.androgister.database.order.OrderDatabase.OrderColumns;

public class OrderIdGenerator {

	private final static String TAG = "OrderIdGenerator";
	
	private final static String QUERY_SELECT_MAX_ORDER_NUMBER = 
 			"SELECT MAX(" + OrderColumns.KEY_ORDER_NUMBER + ") AS max_id FROM " + OrderDatabase.ORDER_TABLE
			+ " WHERE " + OrderColumns.KEY_ORDER_DATE + " >= %s and "+ OrderColumns.KEY_ORDER_DATE + "< %s";

	private AtomicLong cacheCounter;
	private long cacheMidnight = -1;
	
	public long getNextOrderNum(SQLiteDatabase db, long now) {
		// Compute Minighth date
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(now);
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		long dateMidenight = cal.getTimeInMillis();
 		if (dateMidenight!=cacheMidnight) {
 			// Compute tomorow day
 			cal.add(Calendar.DATE, 1);
 			long tomorrow = cal.getTimeInMillis();
			// Read for Db
			getDbMaxId(db, dateMidenight, tomorrow);
		}  
		long nextOrderNum =cacheCounter.incrementAndGet();
 		Log.i(TAG, String.format("Transform now %s to Date Mightnight %s => Max Number = %s", now, dateMidenight, nextOrderNum));
 		return nextOrderNum;
	}
 
	public void invalidateCacheCounter() {
		cacheMidnight = -1;
		cacheCounter = null;
	}
	
	private long getDbMaxId(SQLiteDatabase db, long dateMightnight, long tomorrow) {
 		// Do Database Request
		String query = String.format(QUERY_SELECT_MAX_ORDER_NUMBER, dateMightnight, tomorrow);
		Log.i(TAG, query);
		Cursor cursor = db.rawQuery(query, null);
		long id = 0;
		try { 
			if (cursor.moveToFirst()) {
				do {
					id = cursor.getLong(0);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
		cacheCounter = new AtomicLong(id);
		cacheMidnight = dateMightnight;
		return id;
	}
}

package eu.ttbox.androgister.domain.dao.order;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.domain.OrderDao;

/**
 * Android Identifier {link http://android-developers.blogspot.fr/2011/03/identifying-app-installations.html}
 * @author jmorille
 *
 */
public class OrderIdGenerator {

	private final static String TAG = "OrderIdGenerator";

	private final static String QUERY_SELECT_MAX_ORDER_NUMBER = "SELECT MAX(" + OrderDao.Properties.OrderNumber.columnName + ") AS max_id FROM " + OrderDao.TABLENAME //
	        + " WHERE "
			+ OrderDao.Properties.OrderDate.columnName + " >= %s and " + OrderDao.Properties.OrderDate.columnName + " < %s";

  
	private AndroGisterApplication application;

	public OrderIdGenerator(Context context) {
		this.application = (AndroGisterApplication) context.getApplicationContext();
	}

	public long getNextOrderNum(SQLiteDatabase db, long now) {
		// Compute Minighth date
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(now);
		cal.set(Calendar.HOUR_OF_DAY, 0);  
		cal.set(Calendar.MINUTE, 0);  
		cal.set(Calendar.SECOND, 0);  
		cal.set(Calendar.MILLISECOND, 0);   
		long dateMidnight = cal.getTimeInMillis();
		if (! getOrderIdSequence().isValidCache(dateMidnight)) {
			// Compute tomorow day
			cal.add(Calendar.DATE, 1);
			long tomorrow = cal.getTimeInMillis();
			// Read for Db
			getDbMaxId(db, dateMidnight, tomorrow);
		}
		long nextOrderNum =  getOrderIdSequence().incrementAndGet();
		Log.i(TAG, String.format("Transform now %s to Date Mightnight %s => Max Number = %s", now, dateMidnight, nextOrderNum));
		return nextOrderNum;
	}

	public OrderIdSequence getOrderIdSequence() {
		return application.getOrderIdSequence();
	}
	
	public void invalidateCacheCounter() {
		 getOrderIdSequence().invalidateCacheCounter();
	}

	private long getDbMaxId(SQLiteDatabase db, long dateMightnight, long tomorrow) {
		// Do Database Request
		
		String query = String.format(QUERY_SELECT_MAX_ORDER_NUMBER, dateMightnight, tomorrow);
		Log.i(TAG, String.format("Check for max Order Number in range of %1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS,%1$tL to Tomorrow %2$tY-%2$tm-%2$td %2$tH:%2$tM:%2$tS,%2$tL", dateMightnight, tomorrow));
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
		 getOrderIdSequence().initCacheCounter(new AtomicLong(id), dateMightnight);
		return id;
	}
}

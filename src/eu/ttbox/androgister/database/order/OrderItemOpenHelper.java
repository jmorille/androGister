package eu.ttbox.androgister.database.order;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OrderItemOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "OrderItemOpenHelper";

	public static final String DATABASE_NAME = "orderDb";
	public static final String FTS_VIRTUAL_TABLE = "FTSorder";
	public static final int DATABASE_VERSION = 1;

	/*
	 * Note that FTS3 does not support column constraints and thus, you cannot
	 * declare a primary key. However, "rowid" is automatically used as a unique
	 * identifier, so when making requests, we will use "_id" as an alias for
	 * "rowid"
	 */
	private static final String FTS_TABLE_CREATE = "CREATE VIRTUAL TABLE "
			+ FTS_VIRTUAL_TABLE + //
			" USING fts3 (" //
			+ OrderItemDatabase.Column.KEY_NAME //
			+ ", " + OrderItemDatabase.Column.KEY_PRODUCT_ID //
			+ ", " + OrderItemDatabase.Column.KEY_EAN  //
			+ ", " + OrderItemDatabase.Column.KEY_QUANTITY //
			+ ", " + OrderItemDatabase.Column.KEY_PRICE_UNIT_HT //
			+ ", " + OrderItemDatabase.Column.KEY_PRICE_SUM_HT //
			+ ");";


	private final Context mHelperContext;
	private SQLiteDatabase mDatabase;

	OrderItemOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mHelperContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		mDatabase = db;
		mDatabase.execSQL(FTS_TABLE_CREATE); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
		onCreate(db);
	}
   
}

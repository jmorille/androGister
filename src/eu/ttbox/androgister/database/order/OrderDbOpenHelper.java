package eu.ttbox.androgister.database.order;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import eu.ttbox.androgister.database.order.OrderDatabase.OrderColumns;
import eu.ttbox.androgister.database.order.OrderDatabase.OrderItemColumns;

public class OrderDbOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "OrderDbOpenHelper";

	public static final String DATABASE_NAME = "orderDb";

	public static final int DATABASE_VERSION = 1;

	private static final String CREATE_TABLE_ORDER = "CREATE TABLE " + OrderDatabase.ORDER_TABLE //
			+ "( " + OrderColumns.KEY_ORDER_NUMBER + " INTEGER NOT NULL" //
			+ ", " + OrderColumns.KEY_ORDER_UUID + " TEXT NOT NULL" //
			+ ", " + OrderColumns.KEY_ORDER_DATE + " INTEGER NOT NULL" //
			+ ", " + OrderColumns.KEY_STATUS + " INTEGER NOT NULL" //
			+ ", " + OrderColumns.KEY_PRICE_SUM_HT + " INTEGER NOT NULL" //
			+ ");";

	private static final String CREATE_INDEX_AK_UUID = "CREATE UNIQUE INDEX IDX_ORDER_AK_UUID ON " + OrderDatabase.ORDER_TABLE //
			+ " (" + OrderColumns.KEY_ORDER_UUID + "," + OrderColumns.KEY_STATUS + " )";

	private static final String CREATE_TABLE_ORDER_ITEM = "CREATE TABLE " + OrderDatabase.ORDER_ITEM_TABLE //
			+ "( " + OrderItemColumns.KEY_ORDER_ID + " INTEGER NOT NULL" //
			+ ", " + OrderItemColumns.KEY_NAME + " TEXT NOT NULL" //
			+ ", " + OrderItemColumns.KEY_PRODUCT_ID + " INTEGER NOT NULL" //
			+ ", " + OrderItemColumns.KEY_EAN + " TEXT"//
			+ ", " + OrderItemColumns.KEY_QUANTITY + " INTEGER NOT NULL" //
			+ ", " + OrderItemColumns.KEY_PRICE_UNIT_HT + " INTEGER NOT NULL" //
			+ ", " + OrderItemColumns.KEY_PRICE_SUM_HT + " INTEGER NOT NULL" //
			+ ", FOREIGN KEY (" + OrderItemColumns.KEY_ORDER_ID + ") REFERENCES " //
			+ OrderDatabase.ORDER_TABLE + " ( " + OrderColumns.KEY_ID + ")" //
			+ ");";

	private final Context mHelperContext;
	private SQLiteDatabase mDatabase;

	OrderDbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.mHelperContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		mDatabase = db;
		mDatabase.execSQL(CREATE_TABLE_ORDER);
		mDatabase.execSQL(CREATE_INDEX_AK_UUID);
		mDatabase.execSQL(CREATE_TABLE_ORDER_ITEM);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + OrderDatabase.ORDER_ITEM_TABLE);
		db.execSQL("DROP INDEX IF EXISTS IDX_ORDER_AK_UUID");
		db.execSQL("DROP TABLE IF EXISTS " + OrderDatabase.ORDER_TABLE);
		onCreate(db);
	}

}

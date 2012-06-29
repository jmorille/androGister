package eu.ttbox.androgister.database.product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import eu.ttbox.androgister.R;

public class OfferOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "ProductOpenHelper";

	public static final String DATABASE_NAME = "offerDb";
	public static final int DATABASE_VERSION = 1;

	/*
	 * Note that FTS3 does not support column constraints and thus, you cannot
	 * declare a primary key. However, "rowid" is automatically used as a unique
	 * identifier, so when making requests, we will use "_id" as an alias for
	 * "rowid"
	 */
	private static final String FTS_TABLE_CREATE = "CREATE VIRTUAL TABLE "
			+ OfferDatabase.TABLE_OFFER_FTS + //
			" USING fts3 (" //
			+ OfferDatabase.Column.KEY_NAME //
			+ ", " + OfferDatabase.Column.KEY_DESCRIPTION //
			+ ", " + OfferDatabase.Column.KEY_EAN  //
			+ ", " + OfferDatabase.Column.KEY_PRICEHT //
			+ ", " + OfferDatabase.Column.KEY_TAG //
			+ ");";


	private final Context mHelperContext;
	private SQLiteDatabase mDatabase;

	OfferOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mHelperContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		mDatabase = db;
		mDatabase.execSQL(FTS_TABLE_CREATE);
		loadDictionary();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + OfferDatabase.TABLE_OFFER_FTS);
		onCreate(db);
	}

	/**
	 * Starts a thread to load the database table with words
	 */
	private void loadDictionary() {
		new Thread(new Runnable() {
			public void run() {
				try {
					loadProducts();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
	}

	private void loadProducts() throws IOException {
		Log.d(TAG, "Loading words...");
		final Resources resources = mHelperContext.getResources();
		InputStream inputStream = resources.openRawResource(R.raw.definitions);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] strings = TextUtils.split(line, "-");
				if (strings.length < 2)
					continue;
				long id = addProduct(strings[0].trim(), strings[1].trim(), strings[2].trim());
				Log.i(TAG, String.format("Add Product id %s : name=%s", id, strings[0]));
				if (id < 0) {
					Log.e(TAG, "unable to add word: " + strings[0].trim());
				}
			}
		} finally {
			reader.close();
		}
		Log.d(TAG, "DONE loading words.");
	}

	/**
	 * Add a word to the dictionary.
	 * 
	 * @return rowId or -1 if failed
	 */
	public long addProduct(String name, String tag, String price) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(OfferDatabase.Column.KEY_NAME, name);
		initialValues.put(OfferDatabase.Column.KEY_TAG, tag);
		initialValues.put(OfferDatabase.Column.KEY_PRICEHT, price);

		return mDatabase.insert(OfferDatabase.TABLE_OFFER_FTS, null, initialValues);
	}

}

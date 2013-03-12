package eu.ttbox.androgister.database.user;

import java.util.HashMap;

import eu.ttbox.androgister.database.order.OrderDatabase;
import eu.ttbox.androgister.database.product.PersonDatabase.PersonColumns;
import eu.ttbox.androgister.model.order.Order;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

public class UserDatabase {

	@SuppressWarnings("unused")
	private static final String TAG = "UserDatabase";

	public static final String TABLE_USER_FTS = "userFTS";

	public static class UserColumns {

		public static final String KEY_ID = BaseColumns._ID;
		public static final String KEY_LASTNAME = "LASTNAME";
		public static final String KEY_FIRSTNAME = "FIRSTNAME";
		public static final String KEY_MATRICULE = "MATRICULE";
		public static final String KEY_PASSWORD = "PASSWORD";
		public static final String KEY_TAG = "TAG";

		public static final String[] ALL_KEYS = new String[] { KEY_ID, KEY_LASTNAME, KEY_FIRSTNAME, KEY_MATRICULE, KEY_PASSWORD, KEY_TAG };

	}

	private final UserOpenHelper mDatabaseOpenHelper;
	private static final HashMap<String, String> mUserColumnMap = buildUserColumnMap();

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The Context within which to work, used to create the DB
	 */
	public UserDatabase(Context context) {
		mDatabaseOpenHelper = new UserOpenHelper(context);
	}

	/**
	 * Builds a map for all columns that may be requested, which will be given
	 * to the SQLiteQueryBuilder. This is a good way to define aliases for
	 * column names, but must include all columns, even if the value is the key.
	 * This allows the ContentProvider to request columns w/o the need to know
	 * real column names and create the alias itself.
	 */
	private static HashMap<String, String> buildUserColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		// Add Id
		map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
		// Add Identity Column
		for (String col : UserColumns.ALL_KEYS) {
			if (!col.equals(UserColumns.KEY_ID)) {
				map.put(col, col);
			}
		}
		// Add Suggest Aliases
		map.put(SearchManager.SUGGEST_COLUMN_TEXT_1, String.format("%s || ', ' || %s AS %s", UserColumns.KEY_LASTNAME, UserColumns.KEY_FIRSTNAME, SearchManager.SUGGEST_COLUMN_TEXT_1));
		map.put(SearchManager.SUGGEST_COLUMN_TEXT_2, String.format("%s AS %s", UserColumns.KEY_MATRICULE, SearchManager.SUGGEST_COLUMN_TEXT_2));
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
		// Add Other Aliases
		return map;
	}

	/**
	 * Returns a Cursor positioned at the word specified by rowId
	 * 
	 * @param rowId
	 *            id of word to retrieve
	 * @param columns
	 *            The columns to include, if null then all are included
	 * @return Cursor positioned to matching word, or null if not found.
	 */
	public Cursor getUser(String rowId, String[] columns) {
		String selection = "rowid = ?";
		String[] selectionArgs = new String[] { rowId };
		return queryUser(selection, selectionArgs, columns, null);
	}

	/**
	 * Returns a Cursor over all words that match the given query
	 * 
	 * @param query
	 *            The string to search for
	 * @param columns
	 *            The columns to include, if null then all are included
	 * @return Cursor over all words that match, or null if none found.
	 */
	public Cursor getUserMatches(String query, String[] columns, String order) {
		String selection = UserColumns.KEY_FIRSTNAME + " MATCH ?";
		String queryString = new StringBuilder(query).append("*").toString();
		String[] selectionArgs = new String[] { queryString };
		return queryUser(selection, selectionArgs, columns, order);
	}

	/**
	 * Performs a database query.
	 * 
	 * @param selection
	 *            The selection clause
	 * @param selectionArgs
	 *            Selection arguments for "?" components in the selection
	 * @param columns
	 *            The columns to return
	 * @return A Cursor over all rows matching the query
	 */
	public Cursor queryUser(String selection, String[] selectionArgs, String[] columns, String order) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(TABLE_USER_FTS);
		builder.setProjectionMap(mUserColumnMap);
		Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(), columns, selection, selectionArgs, null, null, order);
		// Manage Cursor
		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}

	public long insertUser( ContentValues userValues) throws SQLException {
		long result = -1;
		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
		try {
			db.beginTransaction();
			try {
		 result = db.insertOrThrow(UserDatabase.TABLE_USER_FTS, null, userValues);
				// commit
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		} finally {
			db.close();
		}
		return result;
	}

	public int delete(String selection, String[] selectionArgs) {
		int result = -1;
		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
		try {
			db.beginTransaction();
			try {
				result = db.delete(UserDatabase.TABLE_USER_FTS, selection, selectionArgs);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		} finally {
			db.close();
		}
		return result;
	}

	public int update(ContentValues values, String selection, String[] selectionArgs) {
		int result = -1;
		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
		try {
			db.beginTransaction();
			try {
				result = db.update(UserDatabase.TABLE_USER_FTS, values, selection, selectionArgs);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		} finally {
			db.close();
		}
		return result;
	}
	
}

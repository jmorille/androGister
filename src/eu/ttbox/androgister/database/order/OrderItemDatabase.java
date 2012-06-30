package eu.ttbox.androgister.database.order;

import java.util.HashMap;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

public class OrderItemDatabase {

	private static final String TAG = "OrderItemDatabase";

	public static final String ORDER_TABLE = "order";
	public static final String ORDER_ITEM_TABLE = "order_item";

	public static class OrderColumns {
		public static final String KEY_ID = BaseColumns._ID;
		public static final String KEY_ORDER_NUMBER = "ORDER_NUMBER";
		public static final String KEY_STATUS = "STATUS";
		public static final String KEY_PRICE_SUM_HT = "PRICE_SUM_HT";
	}

	public static class OrderItemColumns {

		public static final String KEY_ID = BaseColumns._ID;
		public static final String KEY_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
		public static final String KEY_ORDER_ID = "ORDER_ID";
		public static final String KEY_PRODUCT_ID = "PRODUCT_ID";
		public static final String KEY_EAN = "EAN";
		public static final String KEY_QUANTITY = "QUANTITY";
		public static final String KEY_PRICE_UNIT_HT = "PRICE_UNIT_HT";
		public static final String KEY_PRICE_SUM_HT = "PRICE_SUM_HT";

		public static final String[] ALL_KEYS = new String[] { KEY_ID, KEY_ORDER_ID, KEY_NAME, KEY_PRODUCT_ID, KEY_EAN, KEY_PRICE_SUM_HT, KEY_PRICE_UNIT_HT };

	}

	private final OrderDbOpenHelper mDatabaseOpenHelper;
	private static final HashMap<String, String> mColumnMap = buildColumnMap();

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The Context within which to work, used to create the DB
	 */
	public OrderItemDatabase(Context context) {
		mDatabaseOpenHelper = new OrderDbOpenHelper(context);
	}

	/**
	 * Builds a map for all columns that may be requested, which will be given to the SQLiteQueryBuilder. This is a good way to define aliases for column names,
	 * but must include all columns, even if the value is the key. This allows the ContentProvider to request columns w/o the need to know real column names and
	 * create the alias itself.
	 */
	private static HashMap<String, String> buildColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		// Add Id
		map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
		// Add Identity Column
		for (String col : OrderItemColumns.ALL_KEYS) {
			if (!col.equals(OrderItemColumns.KEY_ID)) {
				map.put(col, col);
			}
		}
		// Add Suggest Aliases
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
	public Cursor getWord(String rowId, String[] columns) {
		String selection = "rowid = ?";
		String[] selectionArgs = new String[] { rowId };

		return query(selection, selectionArgs, columns);

		/*
		 * This builds a query that looks like: SELECT <columns> FROM <table> WHERE rowid = <rowId>
		 */
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
	public Cursor getWordMatches(String query, String[] columns) {
		String selection = OrderItemColumns.KEY_NAME + " MATCH ?";
		String[] selectionArgs = new String[] { query + "*" };

		return query(selection, selectionArgs, columns);

		/*
		 * This builds a query that looks like: SELECT <columns> FROM <table> WHERE <KEY_WORD> MATCH 'query*' which is an FTS3 search for the query text (plus a
		 * wildcard) inside the word column.
		 * 
		 * - "rowid" is the unique id for all rows but we need this value for the "_id" column in order for the Adapters to work, so the columns need to make
		 * "_id" an alias for "rowid" - "rowid" also needs to be used by the SUGGEST_COLUMN_INTENT_DATA alias in order for suggestions to carry the proper
		 * intent data. These aliases are defined in the DictionaryProvider when queries are made. - This can be revised to also search the definition text with
		 * FTS3 by changing the selection clause to use FTS_VIRTUAL_TABLE instead of KEY_WORD (to search across the entire table, but sorting the relevance
		 * could be difficult.
		 */
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
	public Cursor query(String selection, String[] selectionArgs, String[] columns) {
		/*
		 * The SQLiteBuilder provides a map for all possible columns requested to actual columns in the database, creating a simple column alias mechanism by
		 * which the ContentProvider does not need to know the real column names
		 */
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(ORDER_ITEM_TABLE);
		builder.setProjectionMap(mColumnMap);

		Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(), columns, selection, selectionArgs, null, null, null);

		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}
}

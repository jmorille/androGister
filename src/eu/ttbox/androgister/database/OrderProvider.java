package eu.ttbox.androgister.database;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import eu.ttbox.androgister.database.order.OrderDatabase;

/**
 * Conent provider tutorial {link http://www.tutos-android.com/contentprovider-android}
 * @author jmorille
 *
 */
public class OrderProvider extends ContentProvider {
	private static final String TAG = "OrderProvider";

	// MIME types used for searching words or looking up a single definition
	public static final String ORDERS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.example.android.searchableorder";
	public static final String ORDER_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.example.android.searchableorder";
	public static final String ORDERS_ITEMS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.example.android.searchableordeitemr";
	public static final String ORDER_ITEM_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.example.android.searchableordeitemr";

	public static class Constants {
		public static String AUTHORITY = "eu.ttbox.androgister.searchableorder.OrderProvider";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/order");
		public static final Uri CONTENT_URI_GET_ODRER = Uri.parse("content://" + AUTHORITY + "/order/");
	}

	private OrderDatabase orderDatabase;

	// UriMatcher stuff
	private static final int SEARCH_ORDERS = 0;
	private static final int GET_ORDER = 1;
	private static final int GET_ORDER_ITEM = 2;
	private static final int SEARCH_SUGGEST = 3;
	private static final int REFRESH_SHORTCUT = 4;
	private static final UriMatcher sURIMatcher = buildUriMatcher();

	/**
	 * Builds up a UriMatcher for search suggestion and shortcut refresh queries.
	 */
	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		// to get definitions...
		matcher.addURI(Constants.AUTHORITY, "offer", SEARCH_ORDERS);
		matcher.addURI(Constants.AUTHORITY, "offer/#", GET_ORDER);
		// to get suggestions...
		matcher.addURI(Constants.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		matcher.addURI(Constants.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
	 
		/*
		 * The following are unused in this implementation, but if we include {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions
		 * table, we could expect to receive refresh queries when a shortcutted suggestion is displayed in Quick Search Box, in which case, the following Uris
		 * would be provided and we would return a cursor with a single item representing the refreshed suggestion data.
		 */
		matcher.addURI(Constants.AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
		matcher.addURI(Constants.AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
		return matcher;
	}
	/**
	 * This method is required in order to query the supported types. It's also useful in our own query() method to determine the type of Uri received.
	 */
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_ORDERS:
			return ORDERS_MIME_TYPE;
		case GET_ORDER:
			return ORDER_MIME_TYPE;
		case GET_ORDER_ITEM:
			return ORDER_ITEM_MIME_TYPE;
		case SEARCH_SUGGEST:
			return SearchManager.SUGGEST_MIME_TYPE;
		case REFRESH_SHORTCUT:
			return SearchManager.SHORTCUT_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		orderDatabase = new OrderDatabase(getContext());
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST:
 		case SEARCH_ORDERS:
 		case GET_ORDER:
 		case REFRESH_SHORTCUT:
 		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = orderDatabase.getWritableDatabase();
		try {
			long id = db.insertOrThrow(OrderDatabase.ORDER_TABLE, null , values);
			
		} finally {
			db.close();
		}
		
		return null;
	}
	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new RuntimeException("Delete Order Not Implemented");
 	}
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new RuntimeException("Delete Order Not Implemented");
	}

}

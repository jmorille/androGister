package eu.ttbox.androgister.database;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import eu.ttbox.androgister.database.user.UserDatabase;

public class UserProvider extends ContentProvider {

 
	private static final String TAG = "UserProvider";

	// MIME types used for searching words or looking up a single definition
	public static final String USERS_LIST_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ttbox.cursor.item/user";
	public static final String USER_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ttbox.cursor.item/user";

	public static class Constants {
		public static String AUTHORITY = "eu.ttbox.androgister.searchableuser.UserProvider";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/user");
		public static final Uri CONTENT_URI_GET_USER = Uri.parse("content://" + AUTHORITY + "/user/");
	}

	private UserDatabase userDatabase;

	// UriMatcher stuff
	private static final int SEARCH_USERS = 0;
	private static final int GET_USER = 1;
	private static final int SEARCH_SUGGEST = 2;
	private static final int REFRESH_SHORTCUT = 3;
	private static final UriMatcher sURIMatcher = buildUriMatcher();

	/**
	 * Builds up a UriMatcher for search suggestion and shortcut refresh
	 * queries.
	 */
	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		// to get definitions...
		matcher.addURI(Constants.AUTHORITY, "user", SEARCH_USERS);
		matcher.addURI(Constants.AUTHORITY, "user/#", GET_USER);
		// to get suggestions...
		matcher.addURI(Constants.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		matcher.addURI(Constants.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

		/*
		 * The following are unused in this implementation, but if we include
		 * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our
		 * suggestions table, we could expect to receive refresh queries when a
		 * shortcutted suggestion is displayed in Quick Search Box, in which
		 * case, the following Uris would be provided and we would return a
		 * cursor with a single item representing the refreshed suggestion data.
		 */
		matcher.addURI(Constants.AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
		matcher.addURI(Constants.AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
		return matcher;
	}

	@Override
	public boolean onCreate() {
		userDatabase = new UserDatabase(getContext());
		return true;
	}

	/**
	 * Handles all the dictionary searches and suggestion queries from the
	 * Search Manager. When requesting a specific word, the uri alone is
	 * required. When searching all of the dictionary for matches, the
	 * selectionArgs argument must carry the search query as the first element.
	 * All other arguments are ignored.
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.i(TAG, "query for uri : " + uri);
		// Use the UriMatcher to see what kind of query we have and format the
		// db query accordingly
		switch (sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			return getSuggestions(selectionArgs[0]);
		case SEARCH_USERS:
			return search(projection, selection, selectionArgs, sortOrder);
			// if (selectionArgs == null) {
			// throw new
			// IllegalArgumentException("selectionArgs must be provided for the Uri: "
			// + uri);
			// }
			// return search(selectionArgs[0]);
		case GET_USER:
			return getUser(uri);
		case REFRESH_SHORTCUT:
			return refreshShortcut(uri);
		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}

	private Cursor getSuggestions(String query) {
		query = query.toLowerCase();
		String[] columns = new String[] { UserDatabase.UserColumns.KEY_ID, //
				UserDatabase.UserColumns.KEY_LASTNAME, UserDatabase.UserColumns.KEY_FIRSTNAME, //
				SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2, //
				/*
				 * SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, (only if you want
				 * to refresh shortcuts)
				 */
				SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID };

		return userDatabase.getUserMatches(query, columns, null);
	}

	private Cursor search(String[] _projection, String _selection, String[] _selectionArgs, String _sortOrder) {
		String[] projection = _projection == null ? UserDatabase.UserColumns.ALL_KEYS : _projection;
		String selection = _selection;
		String[] selectionArgs = _selectionArgs;
		String sortOrder = _sortOrder;
		return userDatabase.queryUser(selection, selectionArgs, projection, sortOrder);
	}

	private Cursor getUser(Uri uri) {
		String rowId = uri.getLastPathSegment();
		String[] columns = UserDatabase.UserColumns.ALL_KEYS;
		return userDatabase.getUser(rowId, columns);
	}

	private Cursor refreshShortcut(Uri uri) {
		Log.i(TAG, "refreshShortcut uri " + uri);
		String rowId = uri.getLastPathSegment();
		String[] columns = new String[] { UserDatabase.UserColumns.KEY_ID //
				, BaseColumns._ID //
				, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2 //
		};

		return userDatabase.getUser(rowId, columns);
	}

	/**
	 * This method is required in order to query the supported types. It's also
	 * useful in our own query() method to determine the type of Uri received.
	 */
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_USERS:
			return USERS_LIST_MIME_TYPE;
		case GET_USER:
			return USER_MIME_TYPE;
		case SEARCH_SUGGEST:
			return SearchManager.SUGGEST_MIME_TYPE;
		case REFRESH_SHORTCUT:
			return SearchManager.SHORTCUT_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	// Other required implementations...

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowId = userDatabase.insertUser(values);
		Log.d(TAG, String.format("Insert user %s for Uri %s", rowId, uri));
		if (rowId > -1) {
			Uri insertUri = ContentUris.withAppendedId(Constants.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(insertUri, null);
			return insertUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// String id = uri.getLastPathSegment(); ??
		// Where Clause
		int count = userDatabase.delete(selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = userDatabase.update(values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}

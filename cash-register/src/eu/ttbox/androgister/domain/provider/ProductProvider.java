package eu.ttbox.androgister.domain.provider;

import java.util.HashMap;

import android.app.SearchManager;
import android.app.backup.BackupManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.ProductDao.Properties;

public class ProductProvider extends ContentProvider {

    @SuppressWarnings("unused")
    private static final String TAG = "ProductProvider";

    // MIME types used for searching words or looking up a single definition
    public static final String PRODUCTS_LIST_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ttbox.cursor.item/product";
    public static final String PRODUCT_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ttbox.cursor.item/product";

    public static class Constants {
        public static String AUTHORITY = "eu.ttbox.androgister.product";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/product");
        public static final Uri CONTENT_URI_GET_PRODUCT = Uri.parse("content://" + AUTHORITY + "/product/");
    }

    // UriMatcher stuff
    private static final int ENTITIES = 0;
    private static final int ENTITY = 1;
    private static final int SEARCH_SUGGEST = 2;
    private static final int REFRESH_SHORTCUT = 3;
    private static final UriMatcher sURIMatcher = buildUriMatcher();

    public static final String SELECT_BY_ENTITY_ID = String.format("%s = ?", Properties.Id);

    private HashMap<String, String> mPersonColumnMap = buildEntityColumnMap();

    private ProductDao entityDao;

    private HashMap<String, String> buildEntityColumnMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        // Add Id
        // map.put(PersonColumns.COL_ID, "rowid AS " + BaseColumns._ID);
        // Add Identity Column
        for (String col : entityDao.getAllColumns()) {
            map.put(col, col);
        }
        // Add Suggest Aliases
        map.put(SearchManager.SUGGEST_COLUMN_TEXT_1, String.format("%s AS %s", Properties.Name, SearchManager.SUGGEST_COLUMN_TEXT_1));
        map.put(SearchManager.SUGGEST_COLUMN_TEXT_2, String.format("%s AS %s", Properties.Description, SearchManager.SUGGEST_COLUMN_TEXT_2));
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        // Add Other Aliases
        return map;
    }

    /**
     * Builds up a UriMatcher for search suggestion and shortcut refresh
     * queries.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...
        matcher.addURI(Constants.AUTHORITY, "product", ENTITIES);
        matcher.addURI(Constants.AUTHORITY, "product/#", ENTITY);
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

    public int matchUriMatcher(Uri uri) {
        return sURIMatcher.match(uri);
    }

    @Override
    public boolean onCreate() {
        entityDao = getEntityDao();
        return true;
    }

    public ProductDao getEntityDao() {
        return ((AndroGisterApplication) getContext().getApplicationContext()).getDaoSession().getProductDao();
    }

    public String getSelectClauseByEntityId() {
        return SELECT_BY_ENTITY_ID;
    }

    /**
     * This method is required in order to query the supported types. It's also
     * useful in our own query() method to determine the type of Uri received.
     */
    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
        case ENTITIES:
            return PRODUCTS_LIST_MIME_TYPE;
        case ENTITY:
            return PRODUCT_MIME_TYPE;
        case SEARCH_SUGGEST:
            return SearchManager.SUGGEST_MIME_TYPE;
        case REFRESH_SHORTCUT:
            return SearchManager.SHORTCUT_MIME_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query for uri : " + uri);
        // Use the UriMatcher to see what kind of query we have and format the
        // db query accordingly
        switch (matchUriMatcher(uri)) {
        case ENTITIES:
            return queryEntities(projection, selection, selectionArgs, sortOrder);
        case ENTITY:
            String selectionMerged = selection;
            String entityId = uri.getLastPathSegment();
            String[] args = new String[] { entityId };
            if (!TextUtils.isEmpty(selection)) {
                Log.w(TAG, "Merge selection [" + selection + "] with Uri : " + uri);
                selectionMerged = String.format("%s and (%s)", SELECT_BY_ENTITY_ID, selection);
                int pSelectionArgSize = selectionArgs.length;
                args = new String[pSelectionArgSize + 1];
                System.arraycopy(selectionArgs, 0, args, 1, pSelectionArgSize);
                selectionArgs[0] = entityId;
            } else {
                args = new String[] { entityId };
            }
            return queryEntities(projection, selectionMerged, args, null);
        default:
            throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sURIMatcher.match(uri)) {
        case ENTITIES:
            long entityId = insertEntity(values);
            Uri entityUri = null;
            if (entityId > -1) {
                entityUri = Uri.withAppendedPath(Constants.CONTENT_URI, String.valueOf(entityId));
                getContext().getContentResolver().notifyChange(uri, null);
                // Backup
                BackupManager.dataChanged(getContext().getPackageName());
            }
            return entityUri;
        default:
            throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (sURIMatcher.match(uri)) {
        case ENTITY:
            String entityId = uri.getLastPathSegment();
            String[] args = new String[] { entityId };
            count = updateEntity(values, SELECT_BY_ENTITY_ID, args);
            break;
        case ENTITIES:
            count = updateEntity(values, selection, selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            // Backup
            BackupManager.dataChanged(getContext().getPackageName());
        }
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (sURIMatcher.match(uri)) {
        case ENTITY:
            String entityId = uri.getLastPathSegment();
            String[] args = new String[] { entityId };
            count = deleteEntity(SELECT_BY_ENTITY_ID, args);
            break;
        case ENTITIES:
            count = deleteEntity(selection, selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            // Backup
            BackupManager.dataChanged(getContext().getPackageName());
        }
        return count;
    }

    public long insertEntity(ContentValues values) throws SQLException {
        long result = -1;
        SQLiteDatabase db = entityDao.getDatabase();
        try {
            // normalizedContentValues(values);
            db.beginTransaction();
            try {
                result = db.insertOrThrow(entityDao.getTablename(), null, values);
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

    public int updateEntity(ContentValues values, String selection, String[] selectionArgs) {
        int result = -1;
        // normalizedContentValues(values);
        SQLiteDatabase db = entityDao.getDatabase();
        try {
            db.beginTransaction();
            try {
                result = db.update(entityDao.getTablename(), values, selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            db.close();
        }
        return result;
    }

    public int deleteEntity(String selection, String[] selectionArgs) {
        int result = -1;
        SQLiteDatabase db = entityDao.getDatabase();
        try {
            db.beginTransaction();
            try {
                result = db.delete(entityDao.getTablename(), selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            db.close();
        }
        return result;
    }

    public Cursor queryEntities(String[] _projection, String selection, String[] selectionArgs, String order) {
        // Params
        String[] projection = _projection == null ? entityDao.getAllColumns() : _projection;
        // Query
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(entityDao.getTablename());
        builder.setProjectionMap(mPersonColumnMap);
        Cursor cursor = builder.query(entityDao.getDatabase(), projection, selection, selectionArgs, null, null, order);
        return cursor;
    }

}

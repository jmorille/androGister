package eu.ttbox.androgister.domain.provider;

import java.util.HashMap;
import java.util.Map;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.domain.OrderItem;
import eu.ttbox.androgister.domain.OrderItemDao;
import eu.ttbox.androgister.domain.OrderItemDao.Properties;

public class OrderItemProvider extends AbstractGreenContentProvider<OrderItem> {

    private static final String TAG = "OrderItemProvider";

    public static final String SELECT_BY_ORDER_ID = String.format("%s = ?", Properties.OrderId.columnName);
    
    // MIME types used for searching words or looking up a single definition
    public static final String ORDER_ITEMS_LIST_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ttbox.cursor.item/orderItem";
    public static final String ORDER_ITEM_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ttbox.cursor.item/orderItem";

    public static final int  BY_ORDER_ENTITIES = 10;
    
    public static class Constants {
        public static String AUTHORITY = "eu.ttbox.androgister.orderItem";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/orderItem");
        public static final Uri CONTENT_URI_GET_ORDER_ITEM = Uri.parse("content://" + AUTHORITY + "/orderItem/order/");

        public static Uri getEntityUri(long entityId) {
            return Uri.withAppendedPath(CONTENT_URI, String.valueOf(entityId));
        }
    }

    // UriMatcher stuff
    private static final UriMatcher sURIMatcher = buildUriMatcher();

    public static final String SELECT_BY_ENTITY_ID = String.format("%s = ?", Properties.Id);

    private HashMap<String, String> mEntityColumnMap ;

    @Override
    public Map<String, String> getEntityColumnMap() {
        return mEntityColumnMap;
    }

    public OrderItemDao getEntityDao() {
        OrderItemDao dao =  ((AndroGisterApplication) getContext().getApplicationContext()).getDaoSession().getOrderItemDao(); 
        mEntityColumnMap = buildEntityColumnMap(dao);
        return dao;
    }

    private HashMap<String, String> buildEntityColumnMap(OrderItemDao entityDao) {
        HashMap<String, String> map = new HashMap<String, String>();
        
        // Add Identity Column
        for (String col : entityDao.getAllColumns()) {
            map.put(col, col);
        }
        // Add Suggest Aliases
        map.put(SearchManager.SUGGEST_COLUMN_TEXT_1, String.format("%s AS %s", Properties.Name, SearchManager.SUGGEST_COLUMN_TEXT_1));
        map.put(SearchManager.SUGGEST_COLUMN_TEXT_2, String.format("%s AS %s", Properties.Ean, SearchManager.SUGGEST_COLUMN_TEXT_2));
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
        matcher.addURI(Constants.AUTHORITY, "orderItem", ENTITIES);
        matcher.addURI(Constants.AUTHORITY, "orderItem/#", ENTITY);
        matcher.addURI(Constants.AUTHORITY, "orderItem/order/#", BY_ORDER_ENTITIES);
        return matcher;
    }

    public int matchUriMatcher(Uri uri) {
        return sURIMatcher.match(uri);
    }

    public String getSelectClauseByEntityId() {
        return SELECT_BY_ENTITY_ID;
    }

    public Uri getEntityUri(long entityId) {
        return Constants.getEntityUri(entityId);
    }

    /**
     * This method is required in order to query the supported types. It's also
     * useful in our own query() method to determine the type of Uri received.
     */
    @Override
    public String getType(Uri uri) {
        switch (matchUriMatcher(uri)) {
        case ENTITIES:
            return ORDER_ITEMS_LIST_MIME_TYPE;
        case BY_ORDER_ENTITIES:
            return ORDER_ITEMS_LIST_MIME_TYPE;
        case ENTITY:
            return ORDER_ITEM_MIME_TYPE; 
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
        case BY_ORDER_ENTITIES:
            String orderId = uri.getLastPathSegment();
            String mergerSelection  = mergeQuerySelectionClause(selection, SELECT_BY_ORDER_ID);
            String[] mergedArgs = mergeQuerySelectionArgsClause(selectionArgs, new String[] {orderId} );
            Log.d(TAG, "mergerSelection : " + mergerSelection);
            Log.d(TAG, "mergedArgs : " + mergedArgs.length);
            return queryEntities(projection, mergerSelection, mergedArgs, sortOrder);
        }
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

}

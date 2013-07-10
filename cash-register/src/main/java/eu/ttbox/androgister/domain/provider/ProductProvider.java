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
import eu.ttbox.androgister.domain.CatalogProductDao;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.ProductDao.Properties;

public class ProductProvider extends AbstractGreenContentProvider<Product> {

    private static final String TAG = "ProductProvider";

    public static final int CATALOG_ENTITIES = 10;

    // MIME types used for searching words or looking up a single definition
    public static final String PRODUCTS_LIST_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ttbox.cursor.item/product";
    public static final String PRODUCT_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ttbox.cursor.item/product";

    public static class Constants {
        public static String AUTHORITY = "eu.ttbox.androgister.product";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/product");
        public static final Uri CONTENT_URI_GET_PRODUCT = Uri.parse("content://" + AUTHORITY + "/product/");
        public static final Uri CONTENT_URI_CATALOG_PRODUCT = Uri.parse("content://" + AUTHORITY + "/catalog/");

        public static Uri getEntityUri(long entityId) {
            return Uri.withAppendedPath(CONTENT_URI, String.valueOf(entityId));
        }
    }

    // UriMatcher stuff
    private static final UriMatcher sURIMatcher = buildUriMatcher();

    public static final String SELECT_BY_ENTITY_ID = String.format("%s = ?", Properties.Id.columnName);

    private HashMap<String, String> mEntityColumnMap;

    @Override
    public Map<String, String> getEntityColumnMap() {
        return mEntityColumnMap;
    }

    public ProductDao getEntityDao() {
        ProductDao dao = ((AndroGisterApplication) getContext().getApplicationContext()).getDaoSession().getProductDao();
        mEntityColumnMap = buildEntityColumnMap(dao);
        return dao;
    }

    private HashMap<String, String> buildEntityColumnMap(ProductDao entityDao) {
        HashMap<String, String> map = new HashMap<String, String>();

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

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query for uri : " + uri);
        switch (matchUriMatcher(uri)) {
        case CATALOG_ENTITIES:
            String filterCatalog = String.format("%s in (select %s from %s)", Properties.Id.columnName, //
                    CatalogProductDao.Properties.ProductId.columnName, CatalogProductDao.TABLENAME);
            String whereClause = selection == null ? filterCatalog : filterCatalog + " and (" + selection + ")";
            return queryEntities(projection, whereClause, selectionArgs, sortOrder);
        }
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
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
        matcher.addURI(Constants.AUTHORITY, "catalog", CATALOG_ENTITIES);
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
        case ENTITY:
            return PRODUCT_MIME_TYPE;
        case ENTITIES:
            return PRODUCTS_LIST_MIME_TYPE;
        case CATALOG_ENTITIES:
            return PRODUCTS_LIST_MIME_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

}

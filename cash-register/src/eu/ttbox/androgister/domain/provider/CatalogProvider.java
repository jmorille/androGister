package eu.ttbox.androgister.domain.provider;

import java.util.HashMap;
import java.util.Map;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.domain.Catalog;
import eu.ttbox.androgister.domain.CatalogDao;
import eu.ttbox.androgister.domain.CatalogDao.Properties;

public class CatalogProvider extends AbstractGreenContentProvider<Catalog> {

    private static final String TAG = "CatalogProvider";

    // MIME types used for searching words or looking up a single definition
    public static final String CATALOGS_LIST_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ttbox.cursor.item/catalog";
    public static final String CATALOG_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ttbox.cursor.item/catalog";

    public static class Constants {
        public static String AUTHORITY = "eu.ttbox.androgister.catalog";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/catalog");
        public static final Uri CONTENT_URI_GET_CATALOG = Uri.parse("content://" + AUTHORITY + "/catalog/");

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

    public CatalogDao getEntityDao() {
        CatalogDao dao =  ((AndroGisterApplication) getContext().getApplicationContext()).getDaoSession().getCatalogDao(); 
        mEntityColumnMap = buildEntityColumnMap(dao);
        return dao;
    }

    private HashMap<String, String> buildEntityColumnMap(CatalogDao entityDao) {
        HashMap<String, String> map = new HashMap<String, String>();
        ;
        // Add Identity Column
        for (String col : entityDao.getAllColumns()) {
            map.put(col, col);
        }
        // Add Suggest Aliases
        map.put(SearchManager.SUGGEST_COLUMN_TEXT_1, String.format("%s AS %s", Properties.Name, SearchManager.SUGGEST_COLUMN_TEXT_1));
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
        matcher.addURI(Constants.AUTHORITY, "catalog", ENTITIES);
        matcher.addURI(Constants.AUTHORITY, "catalog/#", ENTITY);
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
            return CATALOGS_LIST_MIME_TYPE;
        case ENTITY:
            return CATALOG_MIME_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

}

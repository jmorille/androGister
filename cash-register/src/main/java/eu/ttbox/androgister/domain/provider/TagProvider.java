package eu.ttbox.androgister.domain.provider;

import java.util.HashMap;
import java.util.Map;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.domain.Tag;
import eu.ttbox.androgister.domain.TagDao;
import eu.ttbox.androgister.domain.TagDao.Properties;

public class TagProvider extends AbstractGreenContentProvider<Tag> {

    private static final String TAG = "TagProvider";

    // MIME types used for searching words or looking up a single definition
    public static final String TAGS_LIST_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ttbox.cursor.item/tag";
    public static final String TAG_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ttbox.cursor.item/tag";

    public static class Constants {
        public static String AUTHORITY = "eu.ttbox.androgister.tag";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/tag");
        public static final Uri CONTENT_URI_GET_PRODUCT = Uri.parse("content://" + AUTHORITY + "/tag/");

        public static Uri getEntityUri(long entityId) {
            return Uri.withAppendedPath(CONTENT_URI, String.valueOf(entityId));
        }
    }

    // UriMatcher stuff
    private static final UriMatcher sURIMatcher = buildUriMatcher();

    public static final String SELECT_BY_ENTITY_ID = String.format("%s = ?", Properties.Id.columnName);

    private HashMap<String, String> mEntityColumnMap ;

    @Override
    public Map<String, String> getEntityColumnMap() {
        return mEntityColumnMap;
    }

    public TagDao getEntityDao() {
        TagDao dao =  ((AndroGisterApplication) getContext().getApplicationContext()).getDaoSession().getTagDao(); 
        mEntityColumnMap = buildEntityColumnMap(dao);
        return dao;
    }

    private HashMap<String, String> buildEntityColumnMap(TagDao entityDao) {
        HashMap<String, String> map = new HashMap<String, String>();
        ;
        // Add Identity Column
        for (String col : entityDao.getAllColumns()) {
            map.put(col, col);
        }
        // Add Suggest Aliases
        map.put(SearchManager.SUGGEST_COLUMN_TEXT_1, String.format("%s AS %s", Properties.Name, SearchManager.SUGGEST_COLUMN_TEXT_1));
//        map.put(SearchManager.SUGGEST_COLUMN_TEXT_2, String.format("%s AS %s", Properties.Description, SearchManager.SUGGEST_COLUMN_TEXT_2));
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
        matcher.addURI(Constants.AUTHORITY, "tag", ENTITIES);
        matcher.addURI(Constants.AUTHORITY, "tag/#", ENTITY);
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
            return TAGS_LIST_MIME_TYPE;
        case ENTITY:
            return TAG_MIME_TYPE; 
        default:
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

}

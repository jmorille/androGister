package eu.ttbox.androgister.database.product;

import java.util.HashMap;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

public class OfferDatabase {

    @SuppressWarnings("unused")
    private static final String TAG = "OfferDatabase";

    public static final String TABLE_OFFER_FTS = "offerFTS";

    public static class OfferColumns {

        public static final String KEY_ID = BaseColumns._ID;
        public static final String KEY_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
        public static final String KEY_DESCRIPTION = SearchManager.SUGGEST_COLUMN_TEXT_2;
        public static final String KEY_EAN = "EAN";
        public static final String KEY_PRICEHT = "PRICEHT";
        public static final String KEY_TAG = "TAG";

        public static final String[] ALL_KEYS = new String[] { KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_EAN, KEY_PRICEHT, KEY_TAG };

    }

    private final OfferOpenHelper mDatabaseOpenHelper;
    private static final HashMap<String, String> mOfferColumnMap = buildOfferColumnMap();

    /**
     * Constructor
     * 
     * @param context
     *            The Context within which to work, used to create the DB
     */
    public OfferDatabase(Context context) {
        mDatabaseOpenHelper = new OfferOpenHelper(context);
    }

    /**
     * Builds a map for all columns that may be requested, which will be given
     * to the SQLiteQueryBuilder. This is a good way to define aliases for
     * column names, but must include all columns, even if the value is the key.
     * This allows the ContentProvider to request columns w/o the need to know
     * real column names and create the alias itself.
     */
    private static HashMap<String, String> buildOfferColumnMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        // Add Id
        map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
        // Add Identity Column
        for (String col : OfferColumns.ALL_KEYS) {
            if (!col.equals(OfferColumns.KEY_ID)) {
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
    public Cursor getOffer(String rowId, String[] columns) {
        String selection = "rowid = ?";
        String[] selectionArgs = new String[] { rowId };
        return queryOffer(selection, selectionArgs, columns, null);
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
    public Cursor getOfferMatches(String query, String[] columns, String order) {
        String selection = OfferColumns.KEY_NAME + " MATCH ?";
        String queryString = query + "*";
        String[] selectionArgs = new String[] { queryString };
        return queryOffer(selection, selectionArgs, columns, order);
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
    public Cursor queryOffer(String selection, String[] selectionArgs, String[] columns, String order) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_OFFER_FTS);
        builder.setProjectionMap(mOfferColumnMap);
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
}

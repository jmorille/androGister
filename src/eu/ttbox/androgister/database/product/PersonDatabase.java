package eu.ttbox.androgister.database.product;

import java.util.HashMap;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

public class PersonDatabase {

    @SuppressWarnings("unused")
    private static final String TAG = "PersonDatabase";

    public static final String TABLE_PERSON_FTS = "personFTS";

    public static class PersonColumns {

        public static final String KEY_ID = "rowid";
        public static final String KEY_LASTNAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
        public static final String KEY_FIRSTNAME = SearchManager.SUGGEST_COLUMN_TEXT_2;
        public static final String KEY_MATRICULE = "MATRICULE";
        public static final String KEY_PRICEHT = "PRICEHT";
        public static final String KEY_TAG = "TAG";

        public static final String[] ALL_KEYS = new String[] { KEY_ID, KEY_LASTNAME, KEY_FIRSTNAME, KEY_MATRICULE, KEY_PRICEHT, KEY_TAG };

    }

    private final OfferOpenHelper mDatabaseOpenHelper;
    private static final HashMap<String, String> mPersonColumnMap = buildPersonColumnMap();

    /**
     * Constructor
     * 
     * @param context
     *            The Context within which to work, used to create the DB
     */
    public PersonDatabase(Context context) {
        mDatabaseOpenHelper = new OfferOpenHelper(context);
    }

    /**
     * Builds a map for all columns that may be requested, which will be given
     * to the SQLiteQueryBuilder. This is a good way to define aliases for
     * column names, but must include all columns, even if the value is the key.
     * This allows the ContentProvider to request columns w/o the need to know
     * real column names and create the alias itself.
     */
    private static HashMap<String, String> buildPersonColumnMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        // Add Id
        map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
        // Add Identity Column
        for (String col : PersonColumns.ALL_KEYS) {
            if (!col.equals(PersonColumns.KEY_ID)) {
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
    public Cursor getPerson(String rowId, String[] columns) {
        String selection = "rowid = ?";
        String[] selectionArgs = new String[] { rowId };
        return queryPerson(selection, selectionArgs, columns, null);
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
    public Cursor getPersonMatches(String query, String[] columns, String order) {
        String selection = String.format("%s MATCH ? or %s MATCH ?", PersonColumns.KEY_LASTNAME, PersonColumns.KEY_FIRSTNAME);
        String queryString = query + "*";
        String[] selectionArgs = new String[] { queryString, queryString };
        return queryPerson(selection, selectionArgs, columns, order);
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
    public Cursor queryPerson(String selection, String[] selectionArgs, String[] columns, String order) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_PERSON_FTS);
        builder.setProjectionMap(mPersonColumnMap);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(), columns, selection, selectionArgs, null, null, order);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}

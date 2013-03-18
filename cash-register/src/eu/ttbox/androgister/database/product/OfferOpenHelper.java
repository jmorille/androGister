package eu.ttbox.androgister.database.product;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import eu.ttbox.androgister.database.bootstrap.OfferDbBootstrap;
import eu.ttbox.androgister.database.bootstrap.PersonDbBootstrap;
import eu.ttbox.androgister.database.bootstrap.ProductDbBootstrap;

public class OfferOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "ReferentialOpenHelper";

    public static final String DATABASE_NAME = "offerDb";
    public static final int DATABASE_VERSION = 1;

    /*
     * Note that FTS3 does not support column constraints and thus, you cannot
     * declare a primary key. However, "rowid" is automatically used as a unique
     * identifier, so when making requests, we will use "_id" as an alias for
     * "rowid"
     */
    private static final String FTS_TABLE_CREATE_PRODUCT = "CREATE VIRTUAL TABLE " + ProductDatabase.TABLE_PRODUCT_FTS + //
            " USING fts3 " //
            + "( " + OfferDatabase.OfferColumns.KEY_NAME //
            + ", " + OfferDatabase.OfferColumns.KEY_DESCRIPTION //
            + ", " + OfferDatabase.OfferColumns.KEY_EAN //
            + ", " + OfferDatabase.OfferColumns.KEY_PRICEHT //
            + ", " + OfferDatabase.OfferColumns.KEY_TAG //
            + ");";
    
    private static final String FTS_TABLE_CREATE_OFFER = "CREATE VIRTUAL TABLE " + OfferDatabase.TABLE_OFFER_FTS + //
            " USING fts3 " //
            + "( " + OfferDatabase.OfferColumns.KEY_NAME //
            + ", " + OfferDatabase.OfferColumns.KEY_DESCRIPTION //
            + ", " + OfferDatabase.OfferColumns.KEY_EAN //
            + ", " + OfferDatabase.OfferColumns.KEY_PRICEHT //
            + ", " + OfferDatabase.OfferColumns.KEY_TAG //
            + ");";

    private static final String FTS_TABLE_CREATE_PERSON = "CREATE VIRTUAL TABLE " + PersonDatabase.TABLE_PERSON_FTS + //
            " USING fts3 " //
            + "( " + PersonDatabase.PersonColumns.KEY_LASTNAME //
            + ", " + PersonDatabase.PersonColumns.KEY_FIRSTNAME //
            + ", " + PersonDatabase.PersonColumns.KEY_MATRICULE //
            + ", " + PersonDatabase.PersonColumns.KEY_PRICEHT //
            + ", " + PersonDatabase.PersonColumns.KEY_TAG //
            + ");";

    
    private final Context mHelperContext;
    private SQLiteDatabase mDatabase;

    OfferOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mHelperContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDatabase = db;
        mDatabase.execSQL(FTS_TABLE_CREATE_PRODUCT);
        mDatabase.execSQL(FTS_TABLE_CREATE_OFFER);
        mDatabase.execSQL(FTS_TABLE_CREATE_PERSON);
        new ProductDbBootstrap(mHelperContext, mDatabase).loadDictionary();
        new OfferDbBootstrap(mHelperContext, mDatabase).loadDictionary();
        new PersonDbBootstrap(mHelperContext, mDatabase).loadDictionary();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + ProductDatabase.TABLE_PRODUCT_FTS);
        db.execSQL("DROP TABLE IF EXISTS " + OfferDatabase.TABLE_OFFER_FTS);
        db.execSQL("DROP TABLE IF EXISTS " + PersonDatabase.TABLE_PERSON_FTS);
        onCreate(db);
    }

    

}

package eu.ttbox.androgister.database.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import eu.ttbox.androgister.database.bootstrap.UserDbBootstrap;
import eu.ttbox.androgister.database.bootstrap.PersonDbBootstrap;

public class UserOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "UserOpenHelper";

    public static final String DATABASE_NAME = "userDb";
    public static final int DATABASE_VERSION = 1;

    /*
     * Note that FTS3 does not support column constraints and thus, you cannot
     * declare a primary key. However, "rowid" is automatically used as a unique
     * identifier, so when making requests, we will use "_id" as an alias for
     * "rowid"
     */
    private static final String FTS_TABLE_CREATE_USER = "CREATE VIRTUAL TABLE " + UserDatabase.TABLE_USER_FTS + //
            " USING fts3 " //
            + "( " + UserDatabase.UserColumns.KEY_LASTNAME //
            + ", " + UserDatabase.UserColumns.KEY_FIRSTNAME //
            + ", " + UserDatabase.UserColumns.KEY_MATRICULE //
            + ", " + UserDatabase.UserColumns.KEY_PASSWORD //
            + ", " + UserDatabase.UserColumns.KEY_TAG //
            + ");";

   
    
    private final Context mHelperContext;
    private SQLiteDatabase mDatabase;

    UserOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mHelperContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDatabase = db;
        mDatabase.execSQL(FTS_TABLE_CREATE_USER); 
        new UserDbBootstrap(mHelperContext, mDatabase).loadDictionary(); 
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + UserDatabase.TABLE_USER_FTS); 
        onCreate(db);
    }

    

}

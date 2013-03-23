package eu.ttbox.androgister.domain.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import eu.ttbox.androgister.domain.DaoMaster.DevOpenHelper;
import eu.ttbox.androgister.domain.dao.bootstrap.DbBootstrapLoader;

public class RegisterDbOpenHelper extends DevOpenHelper {

    private static final String TAG = "RegisterDbOpenHelper";

    // Config
    public static final String DATABASE_NAME = "register-db";

    // Instance
    private Context context;

    public RegisterDbOpenHelper(Context context, CursorFactory factory) {
        super(context, DATABASE_NAME, factory);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        // Bootstrap
        Log.d(TAG, "onCreate : init ProductDbBootstrap");
        DbBootstrapLoader.loadData(context, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

}

package eu.ttbox.androgister.domain.dao.bootstrap;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.UserDao;
import eu.ttbox.androgister.domain.UserDao.Properties;

public class UserDbBootstrap extends AbstractEntityDbBootstrap {

    private static final String TAG = "UserDbBootstrap";

    public UserDbBootstrap(Context mHelperContext, SQLiteDatabase mDatabase) {
        super(mHelperContext, mDatabase, R.raw.users, '_');
    }

    /**
     * Add a word to the dictionary.
     * 
     * @return rowId or -1 if failed
     */
    @Override
    public long addLineEntity( SQLiteDatabase db, String[] strings) {
        String name = strings[0];
        String tag = strings[1];
        String price = strings[2];
        ContentValues initialValues = new ContentValues();
        initialValues.put(Properties.Firstname.columnName, name);
        initialValues.put(Properties.Lastname.columnName, tag);
        initialValues.put(Properties.Login.columnName, price); 
        // Default
//        initialValues.put(Properties.Deleted.columnName, false);
//        initialValues.put(Properties.Dirty.columnName, false); 
        
        return mDatabase.insert(UserDao.TABLENAME, null, initialValues);
    }

}

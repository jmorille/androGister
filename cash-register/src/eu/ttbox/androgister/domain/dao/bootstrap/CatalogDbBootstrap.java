package eu.ttbox.androgister.domain.dao.bootstrap;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.database.DatabaseUtilsCompat;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.CatalogDao;
import eu.ttbox.androgister.domain.CatalogDao.Properties;

public class CatalogDbBootstrap extends AbstractEntityDbBootstrap {

    private static final String TAG = "CatalogDbBootstrap";

    public CatalogDbBootstrap(Context mHelperContext, SQLiteDatabase mDatabase) {
        super(mHelperContext, mDatabase, R.raw.catalogs, ';');
    }

    /**
     * Add a word to the dictionary.
     * 
     * @return rowId or -1 if failed
     */
    @Override
    public long addLineEntity( SQLiteDatabase db, String[] strings) {
        String id = strings[0] ;
        String name = strings[1] ; 
        ContentValues initialValues = new ContentValues();
        initialValues.put(Properties.Id.columnName, id);
        initialValues.put(Properties.Name.columnName, name); 
        initialValues.put(Properties.Enabled.columnName, Boolean.TRUE); 
//        InsertHelper insertHelper = new InsertHelper(db, tableName);
//      insertHelper.insert(initialValues) ;
      
        return mDatabase.insert(CatalogDao.TABLENAME, null, initialValues);
    }

}

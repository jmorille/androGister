package eu.ttbox.androgister.domain.dao.bootstrap;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import eu.ttbox.androgister.R; 
import eu.ttbox.androgister.domain.CatalogDao;
import eu.ttbox.androgister.domain.CatalogDao.Properties;

public class CatalogDbBootstrap extends AbstractEntityDbBootstrap {

    private static final String TAG = "CatalogDbBootstrap";

    public CatalogDbBootstrap(Context mHelperContext, SQLiteDatabase mDatabase) {
        super(mHelperContext, mDatabase, R.raw.catalogs);
    }

    /**
     * Add a word to the dictionary.
     * 
     * @return rowId or -1 if failed
     */
    @Override
    public long addLineEntity(String[] strings) {
        String id = strings[0].trim();
        String name = strings[1].trim(); 
        ContentValues initialValues = new ContentValues();
        initialValues.put(Properties.Id.columnName, id);
        initialValues.put(Properties.Name.columnName, name); 
        return mDatabase.insert(CatalogDao.TABLENAME, null, initialValues);
    }

}

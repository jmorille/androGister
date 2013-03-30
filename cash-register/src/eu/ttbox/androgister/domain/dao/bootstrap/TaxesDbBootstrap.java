package eu.ttbox.androgister.domain.dao.bootstrap;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.TaxeDao;
import eu.ttbox.androgister.domain.TaxeDao.Properties;

public class TaxesDbBootstrap extends AbstractEntityDbBootstrap {

    private static final String TAG = "TaxesDbBootstrap";

    public TaxesDbBootstrap(Context context, SQLiteDatabase db) {
        super(context, db, R.raw.taxes, ';');
    }

    /**
     * Add a word to the dictionary.
     * 
     * @return rowId or -1 if failed
     */
    @Override
    public long addLineEntity( SQLiteDatabase db, String[] strings) {
        Long id = Long.valueOf(strings[0]);
        String name = strings[1];
        String taxe = strings[2];
        int colCounts = strings.length;

        ContentValues initialValues = new ContentValues();
        // primary
        initialValues.put(Properties.Id.columnName, id);
        initialValues.put(Properties.Title.columnName, name);
        initialValues.put(Properties.TaxeName.columnName, name);
        initialValues.put(Properties.TaxePercent.columnName, taxe);
        // Alternate
        if (colCounts > 4) {
            initialValues.put(Properties.AlternateName.columnName, strings[3]);
            initialValues.put(Properties.AlternateTaxePercent.columnName, strings[4]);
        }
        return mDatabase.insert(TaxeDao.TABLENAME, null, initialValues);
    }

}

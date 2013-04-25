package eu.ttbox.androgister.domain.dao.bootstrap;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.ProductDao.Properties;

public class ProductDbBootstrap extends AbstractEntityDbBootstrap {

    private static final String TAG = "ProductDbBootstrap";

    public ProductDbBootstrap(Context mHelperContext, SQLiteDatabase mDatabase) {
        super(mHelperContext, mDatabase, R.raw.products, ';');
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
        initialValues.put(Properties.Name.columnName, name);
        initialValues.put(Properties.TagId.columnName, tag);
        initialValues.put(Properties.PriceHT.columnName, price);
        // Taxe
        String taxeId = strings.length > 3 ? strings[3] : "1";
        initialValues.put(Properties.TaxeId.columnName, taxeId);
        // Default
        initialValues.put(Properties.Deleted.columnName, false);
        initialValues.put(Properties.Dirty.columnName, false); 
        
        return mDatabase.insert(ProductDao.TABLENAME, null, initialValues);
    }

}

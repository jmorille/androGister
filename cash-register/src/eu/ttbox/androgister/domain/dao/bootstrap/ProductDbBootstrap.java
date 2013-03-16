package eu.ttbox.androgister.domain.dao.bootstrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.ProductDao.Properties;

public class ProductDbBootstrap {

    private static final String TAG = "ProductDbBootstrap";

    private final Context mHelperContext;
    private SQLiteDatabase mDatabase;

    public ProductDbBootstrap(Context mHelperContext, SQLiteDatabase mDatabase) {
        super();
        this.mHelperContext = mHelperContext;
        this.mDatabase = mDatabase;
    }

    /**
     * Starts a thread to load the database table with words
     */
    public void loadDataInDb() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    loadProducts();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void loadProducts() throws IOException {
        Log.d(TAG, "Loading products...");
        final Resources resources = mHelperContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.definitions);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        mDatabase.beginTransaction();
        int insertCount = 0;
        long begin = System.currentTimeMillis();
         try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] strings = TextUtils.split(line, "-");
                if (strings.length < 2)
                    continue;
                long id = addProduct(strings[0].trim(), strings[1].trim(), strings[2].trim());
                Log.d(TAG, String.format("Add Product id %s : name=%s", id, strings[0]));
                if (id < 0) {
                    Log.e(TAG, "unable to add Product: " + strings[0].trim());
                } else {
                    insertCount++;
                }
            }
            mDatabase.setTransactionSuccessful();
            long end = System.currentTimeMillis();
            Log.i(TAG, String.format("Insert %s Products in %s ms", insertCount, (end-begin)));
        } finally {
            reader.close();
            mDatabase.endTransaction();
        }
        Log.d(TAG, "DONE loading products.");
    }

    /**
     * Add a word to the dictionary.
     * 
     * @return rowId or -1 if failed
     */
    public long addProduct(String name, String tag, String price) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(Properties.Name.columnName, name);
        initialValues.put(Properties.Tag.columnName, tag);
        initialValues.put(Properties.PriceHT.columnName, price);
        return mDatabase.insert(ProductDao.TABLENAME, null, initialValues);
    }

}

package eu.ttbox.androgister.domain.dao.bootstrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public abstract class AbstractEntityDbBootstrap {

    private static final String TAG = "AbstractEntityDbBootstrap";

    public final Context mHelperContext;
    public SQLiteDatabase mDatabase;
    public int sourceRawRessourceId;

    public String splitSep = "-";

    public AbstractEntityDbBootstrap(Context mHelperContext, SQLiteDatabase mDatabase, int sourceRawRessourceId) {
        super();
        this.mHelperContext = mHelperContext;
        this.mDatabase = mDatabase;
        this.sourceRawRessourceId = sourceRawRessourceId;
    }

    /**
     * Starts a thread to load the database table with words
     */
    public void loadDataInDb() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    loadEntitiesFormRawId();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void loadEntitiesFormRawId() throws IOException {
        Log.d(TAG, "Loading entities...");
        final Resources resources = mHelperContext.getResources();
        InputStream inputStream = resources.openRawResource(sourceRawRessourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        mDatabase.beginTransaction();
        int insertCount = 0;
        long begin = System.currentTimeMillis();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] strings = TextUtils.split(line, splitSep);
                if (strings.length < 2)
                    continue;
                long id = addLineEntity(strings);
                Log.d(TAG, String.format("Add Entity id %s : name=%s", id, strings[0]));
                if (id < 0) {
                    Log.e(TAG, "unable to add Entity line : " + line);
                } else {
                    insertCount++;
                }
            }
            mDatabase.setTransactionSuccessful();
            long end = System.currentTimeMillis();
            Log.i(TAG, String.format("Insert %s Entities in %s ms", insertCount, (end - begin)));
        } finally {
            reader.close();
            mDatabase.endTransaction();
        }
        Log.d(TAG, "DONE loading entities.");
    }

    /**
     * Add a word to the dictionary.
     * 
     * @return rowId or -1 if failed
     */
    public abstract long addLineEntity(String[] values);
}

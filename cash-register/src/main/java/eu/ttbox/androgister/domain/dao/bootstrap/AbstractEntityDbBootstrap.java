package eu.ttbox.androgister.domain.dao.bootstrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.text.TextUtils.SimpleStringSplitter;
import android.util.Log;

public abstract class AbstractEntityDbBootstrap {

    private static final String TAG = "AbstractEntityDbBootstrap";

    public final Context mHelperContext;
    public SQLiteDatabase mDatabase;
    public int sourceRawRessourceId;

    public char splitSep = ';';

    public AbstractEntityDbBootstrap(Context mHelperContext, SQLiteDatabase mDatabase, int sourceRawRessourceId, char sep) {
        super();
        this.mHelperContext = mHelperContext;
        this.mDatabase = mDatabase;
        this.sourceRawRessourceId = sourceRawRessourceId;
        this.splitSep = sep;
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
//            String splitSepString = "" + splitSep;
            SimpleStringSplitter splitter = getNewStringSplitter();
            while ((line = reader.readLine()) != null) {
//                String[] strings = TextUtils.split(line, splitSepString);
                ArrayList<String> colVals = splitLineEntity(line, splitter);
                if (colVals==null) {
                    continue;
                }
                String[] strings =   colVals.toArray(new String[colVals.size()]);
                 long id = addLineEntity(mDatabase, strings);
//                Log.d(TAG, String.format("Add Entity id %s : name=%s", id, strings[0] ));
                if (id < 0) {
                    Log.e(TAG, "unable to add Entity line : " + line);
                } else {
                    insertCount++;
                }
                mDatabase.yieldIfContendedSafely();
            }
            mDatabase.setTransactionSuccessful();
            long end = System.currentTimeMillis();
            Log.i(TAG, String.format("Insert %s Entities in %s ms", insertCount, (end - begin)));
        } finally {
            reader.close();
            mDatabase.endTransaction();
        }
    }

    public SimpleStringSplitter getNewStringSplitter() {
        SimpleStringSplitter splitter = new SimpleStringSplitter(splitSep);
        return splitter;
    }
    
    /**
     * Add a word to the dictionary.
     * 
     * @return rowId or -1 if failed
     */
    public  ArrayList<String> splitLineEntity(String line,  SimpleStringSplitter splitter) {
        if (line == null || line.length() < 1) {
            return null;
        } 
        splitter.setString(line);
        ArrayList<String> colVals = new ArrayList<String>();
        for (String col : splitter) {
            String colVal = col;
            // Trim to null
            int trimmedSize = TextUtils.getTrimmedLength(colVal);
            if (trimmedSize < 1) {
                colVal = null;
            } else if (trimmedSize < colVal.length()) {
                colVal = colVal.trim();
            }
           // All col value
            colVals.add(colVal);
        }
        return  colVals;
    }

    /**
     * Add a word to the dictionary.
     * 
     * @return rowId or -1 if failed
     */
    public abstract long addLineEntity(  SQLiteDatabase db,  String[]  values);
}

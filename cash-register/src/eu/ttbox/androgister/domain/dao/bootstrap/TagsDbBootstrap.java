package eu.ttbox.androgister.domain.dao.bootstrap;

import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.TagDao;
import eu.ttbox.androgister.domain.TagDao.Properties;

public class TagsDbBootstrap extends AbstractEntityDbBootstrap {

    private static final String TAG = "TagsDbBootstrap";

    public TagsDbBootstrap(Context mHelperContext, SQLiteDatabase mDatabase) {
        super(mHelperContext, mDatabase, R.raw.tags, ';');
    }

    /**
     * Add a word to the dictionary.
     * 
     * @return rowId or -1 if failed
     */
    @Override
    public long addLineEntity( SQLiteDatabase db, String[] strings) {
        Long id = Long.valueOf(strings[0] );
        String name = strings[1] ;
        Integer color = strings.length > 2 ? Integer.valueOf(strings[2] ) : doColorChangeRamdom();
        ContentValues initialValues = new ContentValues();
        initialValues.put(Properties.Id.columnName, id);
        initialValues.put(Properties.Name.columnName, name);
        initialValues.put(Properties.Color.columnName, color);
        return mDatabase.insert(TagDao.TABLENAME, null, initialValues);
    }

    private Integer doColorChangeRamdom() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        int ramdomColor = Color.rgb(r, g, b);
        Log.d(TAG, "Generate Ramdom Color :" + ramdomColor);
        return Integer.valueOf(ramdomColor);
    }

}

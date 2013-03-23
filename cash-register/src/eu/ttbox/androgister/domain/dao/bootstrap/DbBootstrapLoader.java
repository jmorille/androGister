package eu.ttbox.androgister.domain.dao.bootstrap;

import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbBootstrapLoader  implements Runnable {

    private SQLiteDatabase db;
    private Context context;
    
    public static void loadData(Context context, SQLiteDatabase db) {
        new Thread(new DbBootstrapLoader(context, db)).start();
    }
    
    public DbBootstrapLoader(Context context, SQLiteDatabase db) {
        super();
        this.db = db;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            new TagsDbBootstrap(context, db).loadEntitiesFormRawId();
            new ProductDbBootstrap(context, db).loadEntitiesFormRawId();
            new CatalogDbBootstrap(context, db).loadEntitiesFormRawId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
 
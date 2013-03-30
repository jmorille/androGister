package eu.ttbox.androgister.domain.dao.bootstrap;

import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

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
            new TaxesDbBootstrap(context, db).loadEntitiesFormRawId();
            new TagsDbBootstrap(context, db).loadEntitiesFormRawId();
            new ProductDbBootstrap(context, db).loadEntitiesFormRawId();
            new CatalogDbBootstrap(context, db).loadEntitiesFormRawId();
        } catch (IOException e) {
            Toast.makeText(context, "Could not load BootStrap : " +e.getMessage(), Toast.LENGTH_LONG).show();
            throw new RuntimeException("Could not load BootStrap : " +e.getMessage(), e);
        }
    }

}
 
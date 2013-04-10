package eu.ttbox.androgister.sync.syncadapter;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.ProductDao.Properties;
import eu.ttbox.androgister.domain.provider.ProductProvider;
import eu.ttbox.androgister.domain.provider.ProductProvider.Constants;

public class ProductManager {

    private static final String TAG = "ProductManager";

    public static List<Product> getDirtyProducts(Context context, Account account) {
        Log.i(TAG, "*** Looking for local dirty contacts");
        List<Product> dirtyProducts = new ArrayList<Product>();
        // V1 : Product Dao
        AndroGisterApplication app = ((AndroGisterApplication) context.getApplicationContext());
        ProductDao dao = app.getDaoSession().getProductDao();

//        QueryBuilder<Product> query = dao.queryBuilder();
//        query.or(Properties.Dirty.eq(true), Properties.Deleted.eq(true));
//        LazyList<Product> products = query.listLazy();
//        try {
//            for (Product product : products) {
//
//            }
//        } finally {
//            products.close();
//        }

        // V2 : Product ContentResolver
        final ContentResolver resolver = context.getContentResolver();
        String DirtyQuery_SELECTION = String.format("%s = 1 or %s = 1", Properties.Dirty.columnName, Properties.Deleted.columnName);
        final Cursor c = resolver.query(ProductProvider.Constants.CONTENT_URI, dao.getAllColumns(), DirtyQuery_SELECTION, new String[] { "1", "1" }, null);
        try {
            while (c.moveToNext()) {
                Product product = dao.readEntity(c, 0);
                if (product.getDeleted()) {
                    // TODO Operation VO
                    dirtyProducts.add(product);
                } else if (product.getDirty()) {
                    // TODO Operation VO
                    dirtyProducts.add(product);
                 }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return dirtyProducts;
    }
}

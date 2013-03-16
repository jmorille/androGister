package eu.ttbox.androgister.database.product;

import android.content.ContentValues;
import android.database.Cursor;
import android.widget.TextView;
import eu.ttbox.androgister.database.product.ProductDatabase.ProductColumns;
import eu.ttbox.androgister.model.PriceHelper;
import eu.ttbox.androgister.model.product.Product;

public class ProductHelper {

    boolean isNotInit = true;
    public int idIdx = -1;
    public int nameIdx = -1;
    public int descIdx = -1;
    public int eanIdx = -1;
    public int tagIdx = -1;
    public int priceIdx = -1;

    public ProductHelper initWrapper(Cursor cursor) {
        idIdx = cursor.getColumnIndex(ProductColumns.KEY_ID);
        nameIdx = cursor.getColumnIndex(ProductColumns.KEY_NAME);
        descIdx = cursor.getColumnIndex(ProductColumns.KEY_DESCRIPTION);
        eanIdx = cursor.getColumnIndex(ProductColumns.KEY_EAN);
        tagIdx = cursor.getColumnIndex(ProductColumns.KEY_TAG);
        priceIdx = cursor.getColumnIndex(ProductColumns.KEY_PRICEHT);
        isNotInit = false;
        return this;
    }

    public Product getEntity(Cursor cursor) {
        if (isNotInit) {
            initWrapper(cursor);
        }
        Product product = new Product();
        product.setId(idIdx > -1 ? cursor.getLong(idIdx) : -1);
        product.setName(nameIdx > -1 ? cursor.getString(nameIdx) : null);
        // Description
        product.setDescription(descIdx > -1 ? cursor.getString(descIdx) : null);
        // Ean
        product.setEan(eanIdx > -1 ? cursor.getString(eanIdx) : null);
        // Tag
        product.setTag(tagIdx > -1 ? cursor.getString(tagIdx) : null);
        // Price
        long priceHT = cursor.getLong(priceIdx);
        product.setPriceHT(priceIdx > -1 ? priceHT : -1);
        return product;
    }
     
    private ProductHelper setTextWithIdx(TextView view, Cursor cursor, int idx) {
        view.setText(cursor.getString(idx));
        return this;
    }

    public ProductHelper setTextProductId(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, idIdx);
    }

    public ProductHelper setTextProductName(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, nameIdx);
    }

    public ProductHelper setTextProductDescription(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, descIdx);
    }
    
    public ProductHelper setTextProductPrice(TextView view, Cursor cursor) {
        long priceSum = cursor.getLong(priceIdx);
        String priceText = PriceHelper.getToStringPrice(priceSum);
        view.setText(priceText);
        return this;
    }

    public static ContentValues getContentValues(Product product) {
        ContentValues initialValues = new ContentValues();
        if (product.getId() > -1) {
            initialValues.put(ProductColumns.KEY_ID, Long.valueOf(product.getId()));
        }
        initialValues.put(ProductColumns.KEY_NAME, product.getName());
        initialValues.put(ProductColumns.KEY_DESCRIPTION, product.getDescription());
        initialValues.put(ProductColumns.KEY_EAN, product.getEan());
        initialValues.put(ProductColumns.KEY_TAG, product.getTag());
        initialValues.put(ProductColumns.KEY_PRICEHT, Long.valueOf(product.getPriceHT()));

        return initialValues;
    }

}

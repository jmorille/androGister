package eu.ttbox.androgister.database.product;

import android.content.ContentValues;
import android.database.Cursor;
import eu.ttbox.androgister.database.product.OfferDatabase.Column;
import eu.ttbox.androgister.model.Offer;

public class OfferHelper {

    boolean isNotInit = true;
    public int idIdx = -1;
    public int nameIdx = -1;
    public int descIdx = -1;
    public int eanIdx = -1;
    public int tagIdx = -1;
    public int priceIdx = -1;

    public OfferHelper initWrapper(Cursor cursor) {
        idIdx = cursor.getColumnIndex(Column.KEY_ID);
        nameIdx = cursor.getColumnIndex(Column.KEY_NAME);
        descIdx = cursor.getColumnIndex(Column.KEY_DESCRIPTION);
        eanIdx = cursor.getColumnIndex(Column.KEY_EAN);
        tagIdx = cursor.getColumnIndex(Column.KEY_TAG);
        priceIdx = cursor.getColumnIndex(Column.KEY_PRICEHT);
        isNotInit = false;
        return this;
    }

    public Offer getEntity(Cursor cursor) {
        if (isNotInit) {
            initWrapper(cursor);
        }
        Offer product = new Offer();
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

    public static ContentValues getContentValues(Offer product) {
        ContentValues initialValues = new ContentValues();
        if (product.getId() > -1) {
            initialValues.put(Column.KEY_ID, Long.valueOf(product.getId()));
        }
        initialValues.put(Column.KEY_NAME, product.getName());
        initialValues.put(Column.KEY_DESCRIPTION, product.getDescription());
        initialValues.put(Column.KEY_EAN, product.getEan());
        initialValues.put(Column.KEY_TAG, product.getTag());
        initialValues.put(Column.KEY_PRICEHT, Long.valueOf(product.getPriceHT()));

        return initialValues;
    }

}

package eu.ttbox.androgister.database.product;

import android.content.ContentValues;
import android.database.Cursor;
import android.widget.TextView;
import eu.ttbox.androgister.database.product.OfferDatabase.OfferColumns;
import eu.ttbox.androgister.model.Offer;
import eu.ttbox.androgister.model.PriceHelper;

public class OfferHelper {

    boolean isNotInit = true;
    public int idIdx = -1;
    public int nameIdx = -1;
    public int descIdx = -1;
    public int eanIdx = -1;
    public int tagIdx = -1;
    public int priceIdx = -1;

    public OfferHelper initWrapper(Cursor cursor) {
        idIdx = cursor.getColumnIndex(OfferColumns.KEY_ID);
        nameIdx = cursor.getColumnIndex(OfferColumns.KEY_NAME);
        descIdx = cursor.getColumnIndex(OfferColumns.KEY_DESCRIPTION);
        eanIdx = cursor.getColumnIndex(OfferColumns.KEY_EAN);
        tagIdx = cursor.getColumnIndex(OfferColumns.KEY_TAG);
        priceIdx = cursor.getColumnIndex(OfferColumns.KEY_PRICEHT);
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
     
    private OfferHelper setTextWithIdx(TextView view, Cursor cursor, int idx) {
        view.setText(cursor.getString(idx));
        return this;
    }

    public OfferHelper setTextOfferId(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, idIdx);
    }

    public OfferHelper setTextOfferName(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, nameIdx);
    }

    public OfferHelper setTextOfferDescription(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, descIdx);
    }
    
    public OfferHelper setTextOfferPrice(TextView view, Cursor cursor) {
        long priceSum = cursor.getLong(priceIdx);
        String priceText = PriceHelper.getToStringPrice(priceSum);
        view.setText(priceText);
        return this;
    }

    public static ContentValues getContentValues(Offer product) {
        ContentValues initialValues = new ContentValues();
        if (product.getId() > -1) {
            initialValues.put(OfferColumns.KEY_ID, Long.valueOf(product.getId()));
        }
        initialValues.put(OfferColumns.KEY_NAME, product.getName());
        initialValues.put(OfferColumns.KEY_DESCRIPTION, product.getDescription());
        initialValues.put(OfferColumns.KEY_EAN, product.getEan());
        initialValues.put(OfferColumns.KEY_TAG, product.getTag());
        initialValues.put(OfferColumns.KEY_PRICEHT, Long.valueOf(product.getPriceHT()));

        return initialValues;
    }

}

package eu.ttbox.androgister.database.product;

import android.content.ContentValues;
import android.database.Cursor;
import eu.ttbox.androgister.database.product.OfferDatabase.Column;
import eu.ttbox.androgister.model.Product;

public class OfferWrapper {
	
	boolean isNotInit = true;
	int idIdx = -1;
	int nameIdx = -1;
	int descIdx = -1;
	int eanIdx = -1;
	int tagIdx = -1;
	int priceIdx = -1;

	public void initWrapper(Cursor cursor) {
		 idIdx = cursor.getColumnIndex(Column.KEY_ID);
		 nameIdx = cursor.getColumnIndex(Column.KEY_NAME);
		 descIdx = cursor.getColumnIndex(Column.KEY_DESCRIPTION);
		 eanIdx = cursor.getColumnIndex(Column.KEY_EAN);
		 tagIdx = cursor.getColumnIndex(Column.KEY_TAG);
		 priceIdx = cursor.getColumnIndex(Column.KEY_PRICEHT); 
		 isNotInit = false;
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

	public static ContentValues getContentValues(Product product) {
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

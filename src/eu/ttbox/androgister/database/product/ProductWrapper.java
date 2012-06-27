package eu.ttbox.androgister.database.product;

import android.content.ContentValues;
import android.database.Cursor;
import eu.ttbox.androgister.database.product.ProductDatabase.Column;
import eu.ttbox.androgister.model.Product;

public class ProductWrapper {

	public static Product getEntity(Cursor cursor) {
		Product product = new Product();
		product.setId(cursor.getLong(cursor.getColumnIndex(Column.KEY_ID)));
		product.setName(cursor.getString(cursor.getColumnIndex(Column.KEY_NAME)));
		// Description
		product.setDescription(cursor.getString(cursor.getColumnIndex(Column.KEY_DESCRIPTION)));
		// Ean
		int eanIdx = cursor.getColumnIndex(Column.KEY_EAN);
		product.setEan(eanIdx>-1?cursor.getString(eanIdx):null);
		// Tag
		int tagIdx = cursor.getColumnIndex(Column.KEY_TAG);
		product.setTag(tagIdx>-1?cursor.getString(tagIdx):null);
		// Price
		int priceIdx = cursor.getColumnIndex(Column.KEY_PRICEHT);
		long priceHT = cursor.getLong(priceIdx);
		product.setPriceHT(priceIdx>-1?priceHT:-1);
		return product;
	}

	public static ContentValues getContentValues(Product product) {
		ContentValues initialValues = new ContentValues();
		if (product.getId()>-1) {
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

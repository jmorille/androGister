package eu.ttbox.androgister.model;

import android.content.ContentValues;
import android.database.Cursor;
import eu.ttbox.androgister.database.order.OrderItemDatabase.Column;

public class OrderItemHelper {

	boolean isNotInit = true;
	int idIdx = -1;
	int nameIdx = -1;
	int productIdx = -1;
	int eanIdx = -1;
	int quantityIdx = -1;
	int priceUnitIdx = -1;
	int priceSumIdx = -1;

	public static OrderItem createFromProduct(Product p) {
		OrderItem item = null;
		if (p != null) {
			item = new OrderItem();
			item.setProductId(p.getId()).setName(p.getName()).setEan(p.getEan()).setPriceUnitHT(p.getPriceHT());
		}
		return item;
	}

	public void initWrapper(Cursor cursor) {
		idIdx = cursor.getColumnIndex(Column.KEY_ID);
		nameIdx = cursor.getColumnIndex(Column.KEY_NAME);
		productIdx = cursor.getColumnIndex(Column.KEY_PRODUCT_ID);
		eanIdx = cursor.getColumnIndex(Column.KEY_EAN);
		quantityIdx = cursor.getColumnIndex(Column.KEY_QUANTITY);
		priceUnitIdx = cursor.getColumnIndex(Column.KEY_PRICE_UNIT_HT);
		priceSumIdx = cursor.getColumnIndex(Column.KEY_PRICE_SUM_HT);
		isNotInit = false;
	}

	public OrderItem getEntity(Cursor cursor) {
		if (isNotInit) {
			initWrapper(cursor);
		}
		OrderItem orderItem = new OrderItem();
		orderItem.setId(idIdx > -1 ? cursor.getLong(idIdx) : -1);
		orderItem.setProductId(productIdx > -1 ? cursor.getLong(productIdx) : null);
 		// Ean
		orderItem.setName(nameIdx > -1 ? cursor.getString(nameIdx) : null);
		orderItem.setEan(eanIdx > -1 ? cursor.getString(eanIdx) : null);
		// Price
		orderItem.quantity = cursor.getInt(quantityIdx);
		orderItem.priceUnitHT = cursor.getLong(priceUnitIdx);
		orderItem.priceSumHT = cursor.getLong(priceUnitIdx);
		 
		return orderItem;
	}
	

	public static ContentValues getContentValues(OrderItem product) {
		ContentValues initialValues = new ContentValues();
		if (product.getId() > -1) {
			initialValues.put(Column.KEY_ID, Long.valueOf(product.getId()));
		}
		initialValues.put(Column.KEY_PRODUCT_ID, product.getProductId());
		
		initialValues.put(Column.KEY_NAME, product.getName());
		initialValues.put(Column.KEY_EAN, product.getEan());

		initialValues.put(Column.KEY_QUANTITY, product.getQuantity());
		initialValues.put(Column.KEY_PRICE_UNIT_HT, Long.valueOf(product.getPriceUnitHT()));
		initialValues.put(Column.KEY_PRICE_SUM_HT, Long.valueOf(product.getPriceSumHT()));

		return initialValues;
	}
}

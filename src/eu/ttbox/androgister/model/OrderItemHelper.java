package eu.ttbox.androgister.model;

import android.content.ContentValues;
import android.database.Cursor;
import eu.ttbox.androgister.database.order.OrderDatabase.OrderItemColumns;

public class OrderItemHelper {

	boolean isNotInit = true;
	int idIdx = -1;
	int orderIdIdx = -1; 
	int nameIdx = -1;
	int productIdx = -1;
	int eanIdx = -1;
	int quantityIdx = -1;
	int priceUnitIdx = -1;
	int priceSumIdx = -1;

	public static OrderItem createFromProduct(Offer p) {
		OrderItem item = null;
		if (p != null) {
			item = new OrderItem();
			item.setProductId(p.getId()).setName(p.getName()).setEan(p.getEan()).setPriceUnitHT(p.getPriceHT());
		}
		return item;
	}

	public void initWrapper(Cursor cursor) {
		idIdx = cursor.getColumnIndex(OrderItemColumns.KEY_ID);
 		orderIdIdx = cursor.getColumnIndex(OrderItemColumns.KEY_ORDER_ID);
		nameIdx = cursor.getColumnIndex(OrderItemColumns.KEY_NAME);
		productIdx = cursor.getColumnIndex(OrderItemColumns.KEY_PRODUCT_ID);
		eanIdx = cursor.getColumnIndex(OrderItemColumns.KEY_EAN);
		quantityIdx = cursor.getColumnIndex(OrderItemColumns.KEY_QUANTITY);
		priceUnitIdx = cursor.getColumnIndex(OrderItemColumns.KEY_PRICE_UNIT_HT);
		priceSumIdx = cursor.getColumnIndex(OrderItemColumns.KEY_PRICE_SUM_HT);
		isNotInit = false;
	}

	public OrderItem getEntity(Cursor cursor) {
		if (isNotInit) {
			initWrapper(cursor);
		}
		OrderItem orderItem = new OrderItem();
		orderItem.setId(idIdx > -1 ? cursor.getLong(idIdx) : -1);
		orderItem.setOrderId(orderIdIdx > -1 ? cursor.getLong(orderIdIdx) : null);
		orderItem.setProductId(productIdx > -1 ? cursor.getLong(productIdx) : null);
 		// Ean
		orderItem.setName(nameIdx > -1 ? cursor.getString(nameIdx) : null);
		orderItem.setEan(eanIdx > -1 ? cursor.getString(eanIdx) : null);
		// Price
		orderItem.quantity = cursor.getInt(quantityIdx);
		orderItem.priceUnitHT = cursor.getLong(priceUnitIdx);
		orderItem.priceSumHT = cursor.getLong(priceSumIdx);
		 
		return orderItem;
	}
	

	public static ContentValues getContentValues(OrderItem orderItem) {
		ContentValues initialValues = new ContentValues();
		if (orderItem.getId() > -1) {
			initialValues.put(OrderItemColumns.KEY_ID, Long.valueOf(orderItem.getId()));
		}
		initialValues.put(OrderItemColumns.KEY_ORDER_ID, orderItem.getOrderId());
		initialValues.put(OrderItemColumns.KEY_PRODUCT_ID, orderItem.getProductId());
		
		initialValues.put(OrderItemColumns.KEY_NAME, orderItem.getName());
		initialValues.put(OrderItemColumns.KEY_EAN, orderItem.getEan());

		initialValues.put(OrderItemColumns.KEY_QUANTITY, orderItem.getQuantity());
		initialValues.put(OrderItemColumns.KEY_PRICE_UNIT_HT, Long.valueOf(orderItem.getPriceUnitHT()));
		initialValues.put(OrderItemColumns.KEY_PRICE_SUM_HT, Long.valueOf(orderItem.getPriceSumHT()));

		return initialValues;
	}
}

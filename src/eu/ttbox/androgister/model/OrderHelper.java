package eu.ttbox.androgister.model;

import android.content.ContentValues;
import android.database.Cursor;
import eu.ttbox.androgister.database.order.OrderDatabase.OrderColumns;

public class OrderHelper {
	
	boolean isNotInit = true;
	int idIdx = -1;
 	int priceSumIdx = -1;

	 

	public void initWrapper(Cursor cursor) {
		idIdx = cursor.getColumnIndex(OrderColumns.KEY_ID);
 		priceSumIdx = cursor.getColumnIndex(OrderColumns.KEY_PRICE_SUM_HT);
		isNotInit = false;
	}

	public OrderItem getEntity(Cursor cursor) {
		if (isNotInit) {
			initWrapper(cursor);
		}
		OrderItem orderItem = new OrderItem();
		orderItem.setId(idIdx > -1 ? cursor.getLong(idIdx) : -1); 
		orderItem.priceSumHT = cursor.getLong(priceSumIdx); 
		return orderItem;
	}
	

	public static ContentValues getContentValues(Order order) {
		ContentValues initialValues = new ContentValues();
		if (order.getId() > -1) {
			initialValues.put(OrderColumns.KEY_ID, Long.valueOf(order.getId()));
		} 
		initialValues.put(OrderColumns.KEY_STATUS, Integer.valueOf(order.getStatus().ordinal()));
		initialValues.put(OrderColumns.KEY_ORDER_DATE, Long.valueOf(order.getOrderDate()));
		initialValues.put(OrderColumns.KEY_PRICE_SUM_HT, Long.valueOf(order.getPriceSumHT()));

		return initialValues;
	}
}

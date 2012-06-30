package eu.ttbox.androgister.model;

import android.content.ContentValues;
import android.database.Cursor;
import eu.ttbox.androgister.database.order.OrderItemDatabase.OrderColumns;

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
	

	public static ContentValues getContentValues(OrderItem orderItem) {
		ContentValues initialValues = new ContentValues();
		if (orderItem.getId() > -1) {
			initialValues.put(OrderColumns.KEY_ID, Long.valueOf(orderItem.getId()));
		} 
		initialValues.put(OrderColumns.KEY_PRICE_SUM_HT, Long.valueOf(orderItem.getPriceSumHT()));

		return initialValues;
	}
}

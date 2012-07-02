package eu.ttbox.androgister.ui.order;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.order.OrderDatabase.OrderColumns;
import eu.ttbox.androgister.model.PriceHelper;

public class OrderAdapter extends ResourceCursorAdapter {

	private int dateIdx = -1;
	private int orderNumberIdx = -1;
	private int priceIdx = -1;
	private int statusIdx = -1;
	private boolean isNotBinding = true;
 
	public OrderAdapter(Context context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
	}
 

	private void intViewBinding(View view, Context context, Cursor cursor) { 
		// Init Cursor
		orderNumberIdx = cursor.getColumnIndex(OrderColumns.KEY_ORDER_NUMBER);
		dateIdx = cursor.getColumnIndex(OrderColumns.KEY_ORDER_DATE);
		priceIdx = cursor.getColumnIndex(OrderColumns.KEY_PRICE_SUM_HT);
		statusIdx = cursor.getColumnIndex(OrderColumns.KEY_STATUS);
		isNotBinding = false;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (isNotBinding) {
			intViewBinding(view, context, cursor);
		}
		// Bind View
		TextView orderNumText = (TextView) view.findViewById(R.id.order_list_item_orderNum);
		TextView dateText = (TextView) view.findViewById(R.id.order_list_item_date);
		TextView statusText = (TextView) view.findViewById(R.id.order_list_item_status);
		TextView priceText = (TextView) view.findViewById(R.id.order_list_item_price);
		// Bind Value
		orderNumText.setText(cursor.getString(orderNumberIdx));
		dateText.setText(cursor.getString(dateIdx));
		statusText.setText(cursor.getString(statusIdx));
		priceText.setText(PriceHelper.getToStringPrice(cursor.getLong(priceIdx)));
		 

	}
	
	
}

package eu.ttbox.androgister.ui.order;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.product.OfferDatabase.Column;
import eu.ttbox.androgister.model.PriceHelper;

public class OrderAdapter extends ResourceCursorAdapter {

	private int nameIdx = -1;
	private int priceIdx = -1;
	private int tagIdx = -1;
	private boolean isNotBinding = true;
 
	public OrderAdapter(Context context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
	}
 

	private void intViewBinding(View view, Context context, Cursor cursor) { 
		// Init Cursor
		nameIdx = cursor.getColumnIndex(Column.KEY_NAME);
		priceIdx = cursor.getColumnIndex(Column.KEY_PRICEHT);
		tagIdx = cursor.getColumnIndex(Column.KEY_TAG);
		isNotBinding = false;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (isNotBinding) {
			intViewBinding(view, context, cursor);
		}
		// Bind View
		TextView nameText = (TextView) view.findViewById(R.id.product_list_item_name);
		TextView priceText = (TextView) view.findViewById(R.id.product_list_item_price);
		// Bind Value
		nameText.setText(cursor.getString(nameIdx));
		priceText.setText(PriceHelper.getToStringPrice(cursor.getLong(priceIdx)));
		 

	}
	
	
}

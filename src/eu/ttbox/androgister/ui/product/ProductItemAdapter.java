package eu.ttbox.androgister.ui.product;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.product.ProductDatabase.Column;
import eu.ttbox.androgister.model.PriceHelper;

public class ProductItemAdapter extends ResourceCursorAdapter {
 

	private int nameIdx = -1;
	private int priceIdx = -1;
	private boolean isNotBinding = true;

	public ProductItemAdapter(Context context, int layout, Cursor c,  int flags) {
		super(context, layout, c, flags);
	}

	private void intViewBinding(View view, Context context, Cursor cursor) {
 		// Init Cursor
		nameIdx = cursor.getColumnIndex(Column.KEY_NAME);
		priceIdx = cursor.getColumnIndex(Column.KEY_PRICEHT);
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

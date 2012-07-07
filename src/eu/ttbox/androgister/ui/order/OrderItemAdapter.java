package eu.ttbox.androgister.ui.order;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.model.OrderItemHelper;

public class OrderItemAdapter extends ResourceCursorAdapter {

    private OrderItemHelper helper;
    private boolean isNotBinding = true;

    public OrderItemAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    private void intViewBinding(View view, Context context, Cursor cursor) {
        helper = new OrderItemHelper().initWrapper(cursor);
        // Init Cursor
        isNotBinding = false;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (isNotBinding) {
            intViewBinding(view, context, cursor);
        }
        // Bind
        TextView quantityTextView = (TextView) view.findViewById(R.id.basket_list_item_quantity);
        TextView nameTextView = (TextView) view.findViewById(R.id.basket_list_item_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.basket_list_item_price);
        // Set value
        helper.setTextItemName(nameTextView, cursor) //
                .setTextItemQuantity(quantityTextView, cursor)//
                .setTextItemPriceUnit(priceTextView, cursor);
    }

}

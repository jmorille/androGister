package eu.ttbox.androgister.ui.order;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
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
        ViewHolder holder = (ViewHolder) view.getTag();
        // Set value
        helper.setTextItemName(holder.nameTextView, cursor) //
                .setTextItemQuantity(holder.quantityTextView, cursor)//
                .setTextItemPriceUnit(holder.priceTextView, cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = super.newView(context, cursor, parent);
        // Then populate the ViewHolder
        ViewHolder holder = new ViewHolder();
        holder.quantityTextView = (TextView) view.findViewById(R.id.basket_list_item_quantity);
        holder.nameTextView = (TextView) view.findViewById(R.id.basket_list_item_name);
        holder.priceTextView = (TextView) view.findViewById(R.id.basket_list_item_price);
        // and store it inside the layout.
        view.setTag(holder);
        return view;

    }

    static class ViewHolder {
        TextView quantityTextView;
        TextView nameTextView;
        TextView priceTextView;
    }

}

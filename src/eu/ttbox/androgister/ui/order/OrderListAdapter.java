package eu.ttbox.androgister.ui.order;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.model.OrderHelper;

public class OrderListAdapter extends ResourceCursorAdapter {

    private OrderHelper helper;

    private boolean isNotBinding = true;

    public OrderListAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    private void intViewBinding(View view, Context context, Cursor cursor) {
        // Init Cursor
        helper = new OrderHelper().initWrapper(cursor);
        isNotBinding = false;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (isNotBinding) {
            intViewBinding(view, context, cursor);
        }
        // Bind View 
        ViewHolder holder =(ViewHolder)view.getTag();
        // Bind Value
        helper.setTextOrderNumber(holder.orderNumText, cursor) //
                .setTextOrderUuid(holder.orderUuidText, cursor)//
                .setTextOrderStatus(holder.statusText, cursor)//
                .setTextOrderPriceSum(holder.priceText, cursor)//
                .setTextOrderDate(holder.dateText, cursor);
        // Test Invalid
        if (helper.isOrderDeletePossible(cursor)) {
            view.setBackgroundResource(R.drawable.entity_list_item_bg);
        } else {
            view.setBackgroundResource(R.drawable.order_list_item_cancel_bg);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = super.newView(context, cursor, parent);
        // Then populate the ViewHolder
        ViewHolder holder = new ViewHolder();
        holder.orderNumText = (TextView) view.findViewById(R.id.order_list_item_orderNum);
        holder.orderUuidText = (TextView) view.findViewById(R.id.order_list_item_orderUuid);
        holder.dateText = (TextView) view.findViewById(R.id.order_list_item_date);
        holder.statusText = (TextView) view.findViewById(R.id.order_list_item_status);
        holder.priceText = (TextView) view.findViewById(R.id.order_list_item_price);
        // and store it inside the layout.
        view.setTag(holder);
        return view;

    }

    static class ViewHolder {
        TextView orderNumText;
        TextView orderUuidText;
        TextView dateText;
        TextView statusText;
        TextView priceText;
    }

}

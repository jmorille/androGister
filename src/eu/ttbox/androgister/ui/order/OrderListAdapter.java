package eu.ttbox.androgister.ui.order;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
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
        TextView orderNumText = (TextView) view.findViewById(R.id.order_list_item_orderNum);
        TextView orderUuidText = (TextView) view.findViewById(R.id.order_list_item_orderUuid);
        TextView dateText = (TextView) view.findViewById(R.id.order_list_item_date);
        TextView statusText = (TextView) view.findViewById(R.id.order_list_item_status);
        TextView priceText = (TextView) view.findViewById(R.id.order_list_item_price);
        // Bind Value
        helper.setTextOrderNumber(orderNumText, cursor) //
                .setTextOrderUuid(orderUuidText, cursor)//
                .setTextOrderStatus(statusText, cursor)//
                .setTextOrderPriceSum(priceText, cursor)//
                .setTextOrderDate(dateText, cursor);
    }

}

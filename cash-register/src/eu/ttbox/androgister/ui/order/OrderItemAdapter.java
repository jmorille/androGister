package eu.ttbox.androgister.ui.order;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.PriceHelper;
import eu.ttbox.androgister.domain.OrderItemDao;
import eu.ttbox.androgister.domain.OrderItemDao.OrderItemCursorHelper;

public class OrderItemAdapter extends ResourceCursorAdapter {

    private OrderItemCursorHelper helper; 

    public OrderItemAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
        // Dao 
        AndroGisterApplication app = (AndroGisterApplication) context.getApplicationContext();
       OrderItemDao orderDao = app.getDaoSession().getOrderItemDao();
       helper = orderDao.getCursorHelper(null);
    }
 

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (helper.isNotInit) {
            helper.initWrapper(cursor);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        // Set value
        helper.setTextName(holder.nameTextView, cursor);
        // Qunatity
        int qty =   helper.getQuantity(cursor);
        holder.quantityTextView.setText(String.valueOf(qty));
        // Price Unit
        long priceUnit = helper.getPriceUnitHT(cursor);
        String  priceAsString = PriceHelper.getToStringPrice(priceUnit);
        holder.priceTextView.setText(priceAsString); 
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

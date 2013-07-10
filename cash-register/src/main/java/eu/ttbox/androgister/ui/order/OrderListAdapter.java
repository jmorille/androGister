package eu.ttbox.androgister.ui.order;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.PriceHelper;
import eu.ttbox.androgister.domain.OrderDao;
import eu.ttbox.androgister.domain.OrderDao.OrderCursorHelper;
import eu.ttbox.androgister.domain.dao.helper.OrderHelper;
import eu.ttbox.androgister.domain.ref.OrderStatusEnum;


public class OrderListAdapter extends ResourceCursorAdapter {

    private OrderCursorHelper helper;
    private java.text.DateFormat dateFormat;

    
    public OrderListAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
        AndroGisterApplication app = (AndroGisterApplication) context.getApplicationContext();
        OrderDao orderDao = app.getDaoSession().getOrderDao();
        helper = orderDao.getCursorHelper(null);
        // FOrmat 
        dateFormat = OrderHelper.getOrderDateFormat(context);
    }
 

    public void setTextOrderStatus(TextView view, Cursor cursor) {
        int statusId = helper.getStatusId(cursor);
        OrderStatusEnum status = OrderStatusEnum.getEnumFromKey(statusId);
         if (status != null) {
            view.setText(status.name());
        } else {
            view.setText(null);
        } 
    }
    
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (helper.isNotInit) {
            helper.initWrapper(cursor);
        }
        // Bind View 
        ViewHolder holder =(ViewHolder)view.getTag();
        // Bind Value
        helper .setTextOrderUUID(holder.orderUuidText, cursor)// 
                .setTextPersonFirstname(holder.personFirstnameText, cursor) //
                .setTextPersonLastname(holder.personLastnameText, cursor) //
                .setTextPersonMatricule(holder.personMatriculeText, cursor) //
                ;
        
         
       setTextOrderStatus(holder.statusText, cursor);
        
       // Number
        holder.orderNumText.setText(String.valueOf(helper.getOrderNumber(cursor)));
        
        // Date
        String dateAsString = dateFormat.format( helper.getOrderDate(cursor) );
        holder.dateText.setText(dateAsString);
        
        // Price
        String priceSum = PriceHelper.getToStringPrice(helper.getPriceSumHT(cursor) );
        holder.priceText.setText(priceSum);
        
        // Test Invalid
        if (OrderHelper.isOrderDeletePossible(cursor, helper)) {
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
        // Person
        holder.personFirstnameText = (TextView) view.findViewById(R.id.order_list_item_person_firstname);
        holder.personLastnameText = (TextView) view.findViewById(R.id.order_list_item_person_lastname);
        holder.personMatriculeText = (TextView) view.findViewById(R.id.order_list_item_person_matricule);
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
        // Person
        TextView personFirstnameText;
        TextView personLastnameText;
        TextView personMatriculeText;
        
    }

}

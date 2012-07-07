package eu.ttbox.androgister.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.widget.TextView;
import eu.ttbox.androgister.database.order.OrderDatabase.OrderColumns;

public class OrderHelper {

    boolean isNotInit = true;
    public int idIdx = -1;
    public int orderNumberIdx = -1;
    public int orderUuidIdx = -1;
    public int statusIdx = -1;
    public int orderDateIdx = -1;
    public int priceSumIdx = -1;
    // Formater
    private SimpleDateFormat dateFormat;

    private final String datePattern;

    public OrderHelper() {
        this("yyyy-MM-dd HH:mm:ss");
    }

    public OrderHelper(String datePattern) {
        this.datePattern = datePattern;
    }

    public OrderHelper initWrapper(Cursor cursor) {
        idIdx = cursor.getColumnIndex(OrderColumns.KEY_ID);
        orderNumberIdx = cursor.getColumnIndex(OrderColumns.KEY_ORDER_NUMBER);
        orderUuidIdx = cursor.getColumnIndex(OrderColumns.KEY_ORDER_UUID);
        statusIdx = cursor.getColumnIndex(OrderColumns.KEY_STATUS);
        orderDateIdx = cursor.getColumnIndex(OrderColumns.KEY_ORDER_DATE);
        priceSumIdx = cursor.getColumnIndex(OrderColumns.KEY_PRICE_SUM_HT);
        // Formater
        dateFormat = new SimpleDateFormat(datePattern);
        isNotInit = false;
        return this;
    }

    private OrderHelper setTextWithIdx(TextView view, Cursor cursor, int idx) {
        view.setText(cursor.getString(idx));
        return this;
    }

    public OrderHelper setTextOrderId(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, idIdx);
    }

    public OrderHelper setTextOrderNumber(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, orderNumberIdx);
    }

    public OrderHelper setTextOrderUuid(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, orderUuidIdx);
    }

    public OrderHelper setTextOrderStatus(TextView view, Cursor cursor) {
        int statusKey = cursor.getInt(statusIdx);
        OrderStatusEnum status = OrderStatusEnum.getEnumFromKey(statusKey);
        view.setText(status.name());
        return this;
    }

    public OrderHelper setTextOrderDate(TextView view, Cursor cursor) {
        long dateTime = cursor.getLong(orderDateIdx);
        String dateString = dateFormat.format(new Date(dateTime));
        view.setText(dateString);
        return this;
    }

    public OrderHelper setTextOrderPriceSum(TextView view, Cursor cursor) {
        long priceSum = cursor.getLong(priceSumIdx);
        String priceText = PriceHelper.getToStringPrice(priceSum);
        view.setText(priceText);
        return this;
    }

    public Order getEntity(Cursor cursor) {
        if (isNotInit) {
            initWrapper(cursor);
        }
        Order orderItem = new Order();
        orderItem.setId(idIdx > -1 ? cursor.getLong(idIdx) : -1);
        orderItem.setOrderNumber(orderNumberIdx > -1 ? cursor.getLong(orderNumberIdx) : -1);
        orderItem.setOrderUUID(orderUuidIdx > -1 ? cursor.getString(orderUuidIdx) : null);
        orderItem.setStatus(statusIdx > -1 ? OrderStatusEnum.getEnumFromKey(cursor.getInt(statusIdx)) : null);
        orderItem.setOrderDate(orderDateIdx > -1 ? cursor.getLong(orderDateIdx) : -1);
        orderItem.setPriceSumHT(priceSumIdx > -1 ? cursor.getLong(priceSumIdx) : 0);
        return orderItem;
    }

    public static ContentValues getContentValues(Order order) {
        ContentValues initialValues = new ContentValues();
        if (order.getId() > -1) {
            initialValues.put(OrderColumns.KEY_ID, Long.valueOf(order.getId()));
        }
        // order Number
        long orderNumber = order.getOrderNumber();
        if (orderNumber > -1) {
            initialValues.put(OrderColumns.KEY_ORDER_NUMBER, Long.valueOf(orderNumber));
        }
        initialValues.put(OrderColumns.KEY_ORDER_UUID, order.getOrderUUID());
        // Other
        initialValues.put(OrderColumns.KEY_STATUS, Integer.valueOf(order.getStatus().getKey()));
        if (order.getOrderDate() > -1) {
            initialValues.put(OrderColumns.KEY_ORDER_DATE, Long.valueOf(order.getOrderDate()));
        }
        // Price
        initialValues.put(OrderColumns.KEY_PRICE_SUM_HT, Long.valueOf(order.getPriceSumHT()));

        return initialValues;
    }

    public static String generateOrderUUID(long today, String hardwareId, long orderNumber) {
        String result = String.format("%1$tY%1$tm%1$td-%2$s-%3$s", today, hardwareId, orderNumber);
        return result;
    }

}

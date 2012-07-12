package eu.ttbox.androgister.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.widget.TextView;
import eu.ttbox.androgister.database.order.OrderDatabase.OrderColumns;

public class OrderHelper {

    private static final String TAG = "OrderHelper";

    boolean isNotInit = true;
    public int idIdx = -1;
    public int orderNumberIdx = -1;
    public int orderUuidIdx = -1;
    public int orderDeleteUuidIdx = -1;

    public int statusIdx = -1;
    public int orderDateIdx = -1;
    public int priceSumIdx = -1;

    public int paymentModeIdx = -1;
    public int persIdIdx = -1;
    public int persMatriculeIdx = -1;
    public int persFirstnameIdx = -1;
    public int persLastnameIdx = -1;

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
        orderDeleteUuidIdx = cursor.getColumnIndex(OrderColumns.KEY_ORDER_DELETE_UUID);

        statusIdx = cursor.getColumnIndex(OrderColumns.KEY_STATUS);
        orderDateIdx = cursor.getColumnIndex(OrderColumns.KEY_ORDER_DATE);
        priceSumIdx = cursor.getColumnIndex(OrderColumns.KEY_PRICE_SUM_HT);

        // KEY_PAYMENT_MODE, KEY_PERS_ID, KEY_PERS_MATRICULE,
        // KEY_PERS_FIRSTNAME, KEY_PERS_LASTNAME

        paymentModeIdx = cursor.getColumnIndex(OrderColumns.KEY_PAYMENT_MODE);
        persIdIdx = cursor.getColumnIndex(OrderColumns.KEY_PERS_ID);
        persMatriculeIdx = cursor.getColumnIndex(OrderColumns.KEY_PERS_MATRICULE);
        persFirstnameIdx = cursor.getColumnIndex(OrderColumns.KEY_PERS_FIRSTNAME);
        persLastnameIdx = cursor.getColumnIndex(OrderColumns.KEY_PERS_LASTNAME);

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

    public OrderHelper setTextOrderDeleteUuid(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, orderDeleteUuidIdx);
    }

    public OrderHelper setTextPersonMatricule(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, persMatriculeIdx);
    }

    public OrderHelper setTextPersonFirstname(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, persFirstnameIdx);
    }

    public OrderHelper setTextPersonLastname(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, persLastnameIdx);
    }

    public OrderHelper setTextOrderStatus(TextView view, Cursor cursor) {
        OrderStatusEnum status = getOrderStatusEnum(cursor);
        if (status != null) {
            view.setText(status.name());
        } else {
            view.setText(null);
        }
        return this;
    }

    public OrderStatusEnum getOrderStatusEnum(Cursor cursor) {
        OrderStatusEnum status = statusIdx > -1 ? OrderStatusEnum.getEnumFromKey(cursor.getInt(statusIdx)) : null;
        return status;
    }

    public OrderPaymentModeEnum getOrderPaymentModeEnum(Cursor cursor) {
        OrderPaymentModeEnum status = statusIdx > -1 ? OrderPaymentModeEnum.getEnumFromKey(cursor.getInt(statusIdx)) : null;
        return status;
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
        Order order = new Order();
        order.setId(idIdx > -1 ? cursor.getLong(idIdx) : -1);
        order.setOrderNumber(orderNumberIdx > -1 ? cursor.getLong(orderNumberIdx) : -1);
        order.setOrderUUID(orderUuidIdx > -1 ? cursor.getString(orderUuidIdx) : null);
        order.setOrderDeleteUUID(orderDeleteUuidIdx > -1 ? cursor.getString(orderDeleteUuidIdx) : null);
        order.setOrderDate(orderDateIdx > -1 ? cursor.getLong(orderDateIdx) : -1);
        order.setPriceSumHT(priceSumIdx > -1 ? cursor.getLong(priceSumIdx) : 0);

        order.setStatus(getOrderStatusEnum(cursor));
        order.setPaymentMode(getOrderPaymentModeEnum(cursor));

        order.setPersonId(persIdIdx > -1 ? cursor.getLong(persIdIdx) : 0);
        order.setPersonMatricule(persMatriculeIdx > -1 ? cursor.getString(persMatriculeIdx) : null);
        order.setPersonFirstname(persFirstnameIdx > -1 ? cursor.getString(persFirstnameIdx) : null);
        order.setPersonLastname(persLastnameIdx > -1 ? cursor.getString(persLastnameIdx) : null);

        return order;
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
        // UUID
        initialValues.put(OrderColumns.KEY_ORDER_UUID, order.getOrderUUID());
        // Delete UUID Ref
        // If not set, shoulb be equal to UUID in order to use database
        // constraints
        String deleteUUID = order.getOrderDeleteUUID();
        if (deleteUUID == null || deleteUUID.isEmpty()) {
            deleteUUID = order.getOrderUUID();
        }
        initialValues.put(OrderColumns.KEY_ORDER_DELETE_UUID, deleteUUID);

        // Other
        initialValues.put(OrderColumns.KEY_STATUS, Integer.valueOf(order.getStatus().getKey()));
        if (order.getOrderDate() > -1) {
            initialValues.put(OrderColumns.KEY_ORDER_DATE, Long.valueOf(order.getOrderDate()));
        } else {
            initialValues.putNull(OrderColumns.KEY_ORDER_DATE);
        }
        // Price
        initialValues.put(OrderColumns.KEY_PRICE_SUM_HT, Long.valueOf(order.getPriceSumHT()));

        // Pers
        initialValues.put(OrderColumns.KEY_PAYMENT_MODE, Long.valueOf(order.getPriceSumHT()));
        initialValues.put(OrderColumns.KEY_PERS_ID, Long.valueOf(order.getPriceSumHT()));
        initialValues.put(OrderColumns.KEY_PERS_MATRICULE, Long.valueOf(order.getPriceSumHT()));
        initialValues.put(OrderColumns.KEY_PERS_FIRSTNAME, Long.valueOf(order.getPriceSumHT()));
        initialValues.put(OrderColumns.KEY_PERS_LASTNAME, Long.valueOf(order.getPriceSumHT()));

        return initialValues;
    }

    public static String generateOrderUUID(long today, String hardwareId, long orderNumber) {
        String result = String.format("%1$tY%1$tm%1$td-%2$s-%3$s", today, hardwareId, orderNumber);
        return result;
    }

    public boolean isOrderDeletePossible(Cursor cursor) {
        if (isNotInit) {
            initWrapper(cursor);
        }
        String orderUUID = cursor.getString(orderUuidIdx);
        OrderStatusEnum status = getOrderStatusEnum(cursor);
        String orderDeleteUUID = cursor.getString(orderDeleteUuidIdx);
        return OrderHelper.isOrderDeletePossible(orderUUID, status, orderDeleteUUID);
    }

    public static boolean isOrderDeletePossible(Order order) {
        String orderUUID = order.getOrderUUID();
        OrderStatusEnum status = order.getStatus();
        String orderDeleteUUID = order.getOrderDeleteUUID();
        return OrderHelper.isOrderDeletePossible(orderUUID, status, orderDeleteUUID);
    }

    private static boolean isOrderDeletePossible(String orderUUID, OrderStatusEnum status, String orderDeleteUUID) {
        boolean isPossible = true;
        if (isPossible && !OrderStatusEnum.ORDER.equals(status)) {
            isPossible = false;
            Log.w(TAG, String.format("Order Delete %s is NOT Possible for order status %s", orderUUID, status));
        }
        if (isPossible && !orderUUID.equals(orderDeleteUUID)) {
            // Already Invalidate, is not impossible to do again
            isPossible = false;
            Log.w(TAG, String.format("Order Delete %s is NOT Possible for previous delete by %s", orderUUID, orderDeleteUUID));
        }
        return isPossible;
    }

}

package eu.ttbox.androgister.domain.dao.helper;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import eu.ttbox.androgister.domain.Order;
import eu.ttbox.androgister.domain.OrderDao.OrderCursorHelper;
import eu.ttbox.androgister.domain.ref.OrderStatusEnum;

public class OrderHelper {

    private static final String TAG = "OrderHelper";

    public static String generateOrderUUID(long today, String hardwareId, long orderNumber) {
        String result = String.format("%1$tY%1$tm%1$td-%2$s-%3$s", today, hardwareId, orderNumber);
        return result;
    }

    public static boolean isOrderDeletePossible(Cursor cursor, OrderCursorHelper helper) {
        String orderUUID = helper.getOrderUUID(cursor);
        OrderStatusEnum status = getOrderStatus(cursor, helper);
        String orderDeleteUUID = helper.getOrderDeleteUUID(cursor);
        return OrderHelper.isOrderDeletePossible(orderUUID, status, orderDeleteUUID);
    }

    public static boolean isOrderDeletePossible(Order order) {
        String orderUUID = order.getOrderUUID();
        OrderStatusEnum status = order.getStatus();
        String orderDeleteUUID = order.getOrderDeleteUUID();
        return isOrderDeletePossible(orderUUID, status, orderDeleteUUID);
    }

    private static boolean isOrderDeletePossible(String orderUUID, OrderStatusEnum status, String orderDeleteUUID) {
        boolean isPossible = true;
        if (isPossible && !OrderStatusEnum.ORDER.equals(status)) {
            isPossible = false;
            Log.d(TAG, String.format("Order Delete %s is NOT Possible for order status %s", orderUUID, status));
        }
        if (isPossible && !TextUtils.isEmpty(orderDeleteUUID)) {
            // Already Invalidate, is not impossible to do again
            isPossible = false;
            Log.d(TAG, String.format("Order Delete %s is NOT Possible for previous delete by %s", orderUUID, orderDeleteUUID));
        }
        return isPossible;
    }

    public static OrderStatusEnum getOrderStatus(Cursor cursor, OrderCursorHelper helper) {
        int statusId = helper.getStatusId(cursor);
        OrderStatusEnum status = OrderStatusEnum.getEnumFromKey(statusId);
        Log.d(TAG, "OrderStatusEnum id : " + statusId + " = " + status);
        return status;
    }

    public static String getOrderStatusLabel(Context context, OrderStatusEnum status) {
        // FIXME context.getString(resId)
        return status.toString();
    }

    public static java.text.DateFormat getOrderDateFormat(Context context) {
        java.text.DateFormat longDateFormat = DateFormat.getLongDateFormat(context);

        return longDateFormat;
    }

}

package eu.ttbox.androgister.domain.dao.order;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.domain.Order;
import eu.ttbox.androgister.domain.OrderDao;
import eu.ttbox.androgister.domain.OrderItem;
import eu.ttbox.androgister.domain.OrderItemDao;
import eu.ttbox.androgister.domain.dao.helper.OrderHelper;
import eu.ttbox.androgister.domain.ref.OrderStatusEnum;

public class OrderDatabase {

    private static final String TAG = "OrderItemDatabase";

    private static final String ORDER_WHERE_SELECT_BY_ID = String.format("%s = ?", OrderDao.Properties.Id.columnName);

    // Dao
    private OrderDao orderDao;
    private OrderItemDao orderItemDao;

    // Id Generator
    private OrderIdGenerator orderIdGenerator;

    // Concurency Lock
    private Object[] lockInsertOrder = new Object[0];

    /**
     * Constructor
     * 
     * @param context
     *            The Context within which to work, used to create the DB
     */
    public OrderDatabase(Context context) {
        // Init Dao
        AndroGisterApplication app = (AndroGisterApplication) context.getApplicationContext();
        orderDao = app.getDaoSession().getOrderDao();
        orderItemDao = app.getDaoSession().getOrderItemDao();
        // Generator
        orderIdGenerator = new OrderIdGenerator(context);
    }

    public SQLiteDatabase getWritableDatabase() {
        return orderDao.getDatabase();
    }

    public long insertOrder(String deviceId, Order order, ArrayList<OrderItem> orderItems) throws SQLException {
        long result = -1;
        synchronized (lockInsertOrder) {
            SQLiteDatabase db = orderDao.getDatabase();
            try {
                try {
                    db.beginTransaction();
                    result = insertOrder(deviceId, order, orderItems, db);
                    // Commit
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            } finally {
                // db.close();
            }
        }
        return result;
    }

    private Order getOrderModel(SQLiteDatabase db, String orderId) {
        Order order = null;
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(OrderDao.TABLENAME);
        Cursor cursorOrder = builder.query(db, orderDao.getAllColumns(), ORDER_WHERE_SELECT_BY_ID, new String[] { orderId }, null, null, null);

        try {
            // Validate Query
            if (cursorOrder.getCount() != 1) {
                throw new RuntimeException("Not unique result for Order with id " + orderId);
            }
            // Read Cursor
            cursorOrder.moveToFirst();
            OrderHelper helper = new OrderHelper();
            order = orderDao.readEntity(cursorOrder, 0);
        } finally {
            cursorOrder.close();
        }
        return order;
    }

    private long insertOrder(String deviceId, Order order, ArrayList<OrderItem> items, SQLiteDatabase db) throws SQLException {
        long result = -1;
        try {
            // TODO Check for increment number
            long now = System.currentTimeMillis();
            order.setOrderDate(now);
            // Order Id
            long orderNumberNum = orderIdGenerator.getNextOrderNum(db, now);
            order.setOrderNumber(orderNumberNum);
            if (order.getStatus() == null) {
                order.setStatus(OrderStatusEnum.ORDER);
            }
            // Order UUID
            String orderUUID = OrderHelper.generateOrderUUID(now, deviceId, orderNumberNum);
            order.setOrderUUID(orderUUID);

            Log.d(TAG, "Compute new Order UUID : " + orderUUID);
            // Order
            ContentValues orderValues = orderDao.readContentValues(order);
            long orderId = db.insertOrThrow(OrderDao.TABLENAME, null, orderValues);
            order.setId(orderId);
            // Orders Items

            if (items != null && !items.isEmpty()) {
                for (OrderItem item : items) {
                    item.setOrderId(orderId);
                    ContentValues itemValues = orderItemDao.readContentValues(item);
                    long itemId = db.insertOrThrow(OrderItemDao.TABLENAME, null, itemValues);
                    item.setId(itemId);
                }
            }
            result = orderId;
        } catch (SQLException e) {
            orderIdGenerator.invalidateCacheCounter();
            Log.w(TAG, "invalidate orderIdGenerator Cache Counter for SQLException : " + e.getMessage());
            throw e;
        } catch (RuntimeException er) {
            orderIdGenerator.invalidateCacheCounter();
            Log.w(TAG, "invalidate orderIdGenerator Cache Counter for RuntimeException : " + er.getMessage());
            throw er;
        }
        return result;
    }

    public long deleteOrder(String deviceId, long orderId) throws SQLException {
        long result = -1;
        synchronized (lockInsertOrder) {
            SQLiteDatabase db = orderDao.getDatabase();
            try {
                try {
                    db.beginTransaction();
                    // Get A order clone
                    String orderIdAsString = String.valueOf(orderId);
                    Order order = getOrderModel(db, orderIdAsString);

                    // Validate Order Delete Possible
                    boolean isPossible = OrderHelper.isOrderDeletePossible(order);
                    if (!isPossible) {
                        return -1;
                    }
                    // TODO Manage flag
                    Order orderInv = new Order(order);
                    // Revert Order
                    orderInv.setId(null);
                    orderInv.setStatus(OrderStatusEnum.ORDER_CANCEL);
                    orderInv.resetItems();
                    orderInv.setOrderDeleteUUID(order.getOrderUUID());
                    orderInv.setOrderUUID(null);
                    orderInv.setOrderNumber(-1);
                    orderInv.setPriceSumHT(-1 * orderInv.getPriceSumHT());
                    // Insert New Clone
                    result = insertOrder(deviceId, orderInv, null, db);
                    if (result == -1) {
                        throw new RuntimeException(String.format("Could not insert Inversed Order for Original Order Id %s", orderId));
                    }
                    // Update Order fot Cancel status
                    ContentValues values = new ContentValues();
                    values.put(OrderDao.Properties.OrderDeleteUUID.columnName, orderInv.getOrderUUID());
                    int updatedRow = db.update(OrderDao.TABLENAME, values, ORDER_WHERE_SELECT_BY_ID, new String[] { orderIdAsString });
                    if (updatedRow != 1) {
                        throw new RuntimeException(String.format("Wrong number of update %s line for Order Id %s", updatedRow, orderId));
                    }
                    // Commit
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            } finally {
//                db.close();
            }
        }
        return result;
    }

}

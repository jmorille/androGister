package eu.ttbox.androgister.domain.dao.order;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.database.order.OrderDatabase.OrderColumns;
import eu.ttbox.androgister.database.order.OrderIdGenerator;
import eu.ttbox.androgister.domain.Order;
import eu.ttbox.androgister.domain.OrderDao;
import eu.ttbox.androgister.domain.OrderItem;
import eu.ttbox.androgister.domain.OrderItemDao;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.model.order.OrderHelper;
import eu.ttbox.androgister.model.order.OrderItemHelper;
import eu.ttbox.androgister.model.order.OrderStatusEnum;

public class OrderDatabase {


    private static final String TAG = "OrderItemDatabase";
    
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
    
    
    public long insertOrder(String deviceId, Order order) throws SQLException {
        long result = -1;
        synchronized (lockInsertOrder) {
            SQLiteDatabase db = orderDao.getDatabase();
            try {
                try {
                    db.beginTransaction();
                    result = insertOrder(deviceId, order, db);
                    // Commit
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            } finally {
                db.close();
            }
        }
        return result;
    }

    private long insertOrder(String deviceId, Order order, SQLiteDatabase db) throws SQLException {
        long result = -1;
        try {
            // TODO Check for increment number
            long now = System.currentTimeMillis();
            order.setOrderDate(now);
            // Order Id
            long orderNumberNum = orderIdGenerator.getNextOrderNum(db, now);
            String orderNumber = String.valueOf(orderNumberNum);
            order.setOrderNumber(orderNumber);
           
            // Order UUID
            String orderUUID = OrderHelper.generateOrderUUID(now, deviceId, orderNumberNum);
            order.setOrderUUID(orderUUID);
            
            Log.d(TAG, "Compute new Order UUID : " + orderUUID);
            // Order
            orderDao.insertInTx(order); 
         
            // Orders Items
            Long orderId = order.getId();
            List<OrderItem> items = order.getItems();
            if (items != null && !items.isEmpty()) {
                for (OrderItem item : items) {
                    item.setOrderId(orderId); 
                }
                orderItemDao.insertInTx(items);
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
                    String orderIdString = String.valueOf(orderId);
                    Long orderIdLong = Long.valueOf(orderId);
                    // Get A order clone
                    Order orderInv = orderDao.load(orderId );
                    // Validate Order Delete Possible
                    boolean isPossible = OrderHelper.isOrderDeletePossible(orderInv);
                    if (!isPossible) {
                        return -1;
                    }
                    // TODO Manage flag
                    // Revert Order
                    orderInv.setId(-1);
                    orderInv.setStatus(OrderStatusEnum.ORDER_CANCEL);
                    orderInv.setItems(null);
                    orderInv.setOrderDeleteUUID(orderInv.getOrderUUID());
                    orderInv.setOrderUUID(null);
                    orderInv.setOrderNumber(-1);
                    orderInv.setPriceSumHT(-1 * orderInv.getPriceSumHT());
                    // Insert New Clone
                    result = insertOrder(deviceId, orderInv, db);
                    if (result == -1) {
                        throw new RuntimeException(String.format("Could not insert Inversed Order for Original Order Id %s", orderIdString));
                    }
                    // Update Order fot Cancel status
                    ContentValues values = new ContentValues();
                    values.put(OrderColumns.KEY_ORDER_DELETE_UUID, orderInv.getOrderUUID());
                    String whereClause = String.format("%s = ?", OrderColumns.KEY_ID);
                    int updatedRow = db.update(OrderDao.TABLENAME, values, whereClause, new String[] { orderIdString });
                    if (updatedRow != 1) {
                        throw new RuntimeException(String.format("Wrong number of update %s line for Order Id %s", updatedRow, orderIdString));
                    }
                    // Commit
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            } finally {
                db.close();
            }
        }
        return result;
    }
    
}

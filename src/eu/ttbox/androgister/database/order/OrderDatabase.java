package eu.ttbox.androgister.database.order;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.model.Order;
import eu.ttbox.androgister.model.OrderHelper;
import eu.ttbox.androgister.model.OrderItem;
import eu.ttbox.androgister.model.OrderItemHelper;
import eu.ttbox.androgister.model.OrderStatusEnum;

public class OrderDatabase {

    private static final String TAG = "OrderItemDatabase";

    public static final String ORDER_TABLE = "b_order";
    public static final String ORDER_ITEM_TABLE = "b_order_item";

    private Object[] lockInsertOrder = new Object[0];

    private OrderIdGenerator orderIdGenerator;

    public static class OrderColumns {
        public static final String KEY_ID = BaseColumns._ID;
        public static final String KEY_ORDER_NUMBER = "ORDER_NUMBER";
        public static final String KEY_ORDER_UUID = "ORDER_UUID";
        public static final String KEY_ORDER_DELETE_UUID = "ORDER_DELETE_UUID";
        public static final String KEY_STATUS = "STATUS";
        public static final String KEY_ORDER_DATE = "ORDER_DATE";
        public static final String KEY_PRICE_SUM_HT = "PRICE_SUM_HT";

        public static final String KEY_PAYMENT_MODE = "PAYMENT_MODE";
        public static final String KEY_PERS_MATRICULE = "PERS_MATRICULE";
        public static final String KEY_PERS_FIRSTNAME = "PERS_FIRSTNAME";
        public static final String KEY_PERS_LASTNAME = "PERS_LASTNAME";
        public static final String KEY_PERS_ID = "PERS_IDE";

        // KEY_PAYMENT_MODE, KEY_PERS_ID, KEY_PERS_MATRICULE,
        // KEY_PERS_FIRSTNAME, KEY_PERS_LASTNAME
        public static final String[] ALL_KEYS = new String[] { KEY_ID, KEY_ORDER_NUMBER, KEY_ORDER_UUID, KEY_ORDER_DELETE_UUID, KEY_STATUS, KEY_ORDER_DATE, KEY_PRICE_SUM_HT, KEY_PAYMENT_MODE,
                KEY_PERS_ID, KEY_PERS_MATRICULE, KEY_PERS_FIRSTNAME, KEY_PERS_LASTNAME };

    }

    public static class OrderItemColumns {

        public static final String KEY_ID = BaseColumns._ID;
        public static final String KEY_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
        public static final String KEY_ORDER_ID = "ORDER_ID";
        public static final String KEY_PRODUCT_ID = "PRODUCT_ID";
        public static final String KEY_EAN = "EAN";
        public static final String KEY_QUANTITY = "QUANTITY";
        public static final String KEY_PRICE_UNIT_HT = "PRICE_UNIT_HT";
        public static final String KEY_PRICE_SUM_HT = "PRICE_SUM_HT";

        public static final String[] ALL_KEYS = new String[] { KEY_ID, KEY_ORDER_ID, KEY_NAME, KEY_PRODUCT_ID, KEY_EAN, KEY_QUANTITY, KEY_PRICE_UNIT_HT, KEY_PRICE_SUM_HT };

    }

    private final OrderDbOpenHelper mDatabaseOpenHelper;
    private static final HashMap<String, String> mOrderItemColumnMap = buildOrderItemColumnMap();
    private static final HashMap<String, String> mOrderColumnMap = buildOrderColumnMap();

    /**
     * Constructor
     * 
     * @param context
     *            The Context within which to work, used to create the DB
     */
    public OrderDatabase(Context context) {
    
        mDatabaseOpenHelper = new OrderDbOpenHelper(context);
        orderIdGenerator = new OrderIdGenerator(context);

    }

    public SQLiteDatabase getWritableDatabase() {
        return mDatabaseOpenHelper.getWritableDatabase();
    }

    /**
     * Builds a map for all columns that may be requested, which will be given
     * to the SQLiteQueryBuilder. This is a good way to define aliases for
     * column names, but must include all columns, even if the value is the key.
     * This allows the ContentProvider to request columns w/o the need to know
     * real column names and create the alias itself.
     */
    private static HashMap<String, String> buildOrderItemColumnMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        // Add Id
        // Add Identity Column
        for (String col : OrderItemColumns.ALL_KEYS) {
            map.put(col, col);
        }
        // Add Suggest Aliases
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, OrderItemColumns.KEY_ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, OrderItemColumns.KEY_ID + " AS " + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        // Add Other Aliases
        return map;
    }

    private static HashMap<String, String> buildOrderColumnMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        // Add Id
        // Add Identity Column
        for (String col : OrderColumns.ALL_KEYS) {
            map.put(col, col);
        }
        // Add Suggest Aliases
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, OrderColumns.KEY_ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, OrderColumns.KEY_ID + " AS " + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        // Add Other Aliases
        return map;
    }

    /**
     * Returns a Cursor positioned at the word specified by rowId
     * 
     * @param rowId
     *            id of word to retrieve
     * @param columns
     *            The columns to include, if null then all are included
     * @return Cursor positioned to matching word, or null if not found.
     */
    public Cursor getOrder(String rowId, String[] columns) {
        String selection = String.format("%s = ?", OrderColumns.KEY_ID);
        String[] selectionArgs = new String[] { rowId };
        return queryOrder(selection, selectionArgs, columns, null);
    }

    private Order getOrderModel(SQLiteDatabase db, String orderId) {
        Order order = null;
        String selection = String.format("%s = ?", OrderColumns.KEY_ID);
        String[] selectionArgs = new String[] { orderId };
        Cursor cursorOrder = queryOrder(db, selection, selectionArgs, OrderColumns.ALL_KEYS, null);
        try {
            // Validate Query
            if (cursorOrder.getCount() != 1) {
                throw new RuntimeException("Not unique result for Order with id " + orderId);
            }
            // Read Cursor
            cursorOrder.moveToFirst();
            OrderHelper helper = new OrderHelper();
            order = helper.getEntity(cursorOrder);
        } finally {
            cursorOrder.close();
        }
        return order;
    }

    public Cursor getOrderItems(String orderId, String[] columns, String sortOrder) {
        String selection = String.format("%s = ?", OrderItemColumns.KEY_ORDER_ID);
        String[] selectionArgs = new String[] { orderId };
        return queryOrderItem(selection, selectionArgs, columns, sortOrder);
    }

    /**
     * Performs a database query.
     * 
     * @param selection
     *            The selection clause
     * @param selectionArgs
     *            Selection arguments for "?" components in the selection
     * @param columns
     *            The columns to return
     * @return A Cursor over all rows matching the query
     */
    public Cursor queryOrder(String selection, String[] selectionArgs, String[] columns, String sortOrder) {
        SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = queryOrder(db, selection, selectionArgs, columns, sortOrder);
        } finally {
//            db.close();
        }
        return cursor;
    }

    private Cursor queryOrder(SQLiteDatabase db, String selection, String[] selectionArgs, String[] columns, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ORDER_TABLE);
        builder.setProjectionMap(mOrderColumnMap);
        Cursor cursor = builder.query(db, columns, selection, selectionArgs, null, null, sortOrder);
        // Read Cursor
        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor queryOrderItem(String selection, String[] selectionArgs, String[] columns, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ORDER_ITEM_TABLE);
        builder.setProjectionMap(mOrderItemColumnMap);
        SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = builder.query(db, columns, selection, selectionArgs, null, null, sortOrder);
        } finally {
//            db.close();
        }
        // Read Cursor
        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public long insertOrder(String deviceId, Order order) throws SQLException {
        long result = -1;
        synchronized (lockInsertOrder) {
            SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
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
            long orderNumber = orderIdGenerator.getNextOrderNum(db, now);
            order.setOrderNumber(orderNumber);
            // Order UUID
            String orderUUID = OrderHelper.generateOrderUUID(now, deviceId, orderNumber);
            order.setOrderUUID(orderUUID);
            Log.d(TAG, "Compute new Order UUID : " + orderUUID);
            // Order
            ContentValues orderValues = OrderHelper.getContentValues(order);
            long orderId = db.insertOrThrow(OrderDatabase.ORDER_TABLE, null, orderValues);
            order.setId(orderId);
            // Orders Items
            ArrayList<OrderItem> items = order.getItems();
            if (items != null && !items.isEmpty()) {
                for (OrderItem item : items) {
                    item.setOrderId(orderId);
                    ContentValues itemValues = OrderItemHelper.getContentValues(item);
                    long itemId = db.insertOrThrow(OrderDatabase.ORDER_ITEM_TABLE, null, itemValues);
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
            SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
            try {
                try {
                    db.beginTransaction();
                    String orderIdString = String.valueOf(orderId);
                    // Get A order clone
                    Order orderInv = getOrderModel(db, orderIdString);
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
                    int updatedRow = db.update(OrderDatabase.ORDER_TABLE, values, whereClause, new String[] { orderIdString });
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

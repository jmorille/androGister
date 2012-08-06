package eu.ttbox.androgister.model.order;

import android.content.ContentValues;
import android.database.Cursor;
import android.widget.TextView;
import eu.ttbox.androgister.database.order.OrderDatabase.OrderItemColumns;
import eu.ttbox.androgister.model.Offer;
import eu.ttbox.androgister.model.PriceHelper;

public class OrderItemHelper {

    boolean isNotInit = true;
    public int idIdx = -1;
    public int orderIdIdx = -1;
    public int nameIdx = -1;
    public int productIdx = -1;
    public int eanIdx = -1;
    public int quantityIdx = -1;
    public int priceUnitIdx = -1;
    public int priceSumIdx = -1;

    public static OrderItem createFromOffer(Offer p) {
        OrderItem item = null;
        if (p != null) {
            item = new OrderItem();
            item.setProductId(p.getId()).setName(p.getName()).setEan(p.getEan()).setPriceUnitHT(p.getPriceHT());
        }
        return item;
    }

    public OrderItemHelper initWrapper(Cursor cursor) {
        idIdx = cursor.getColumnIndex(OrderItemColumns.KEY_ID);
        orderIdIdx = cursor.getColumnIndex(OrderItemColumns.KEY_ORDER_ID);
        nameIdx = cursor.getColumnIndex(OrderItemColumns.KEY_NAME);
        productIdx = cursor.getColumnIndex(OrderItemColumns.KEY_PRODUCT_ID);
        eanIdx = cursor.getColumnIndex(OrderItemColumns.KEY_EAN);
        quantityIdx = cursor.getColumnIndex(OrderItemColumns.KEY_QUANTITY);
        priceUnitIdx = cursor.getColumnIndex(OrderItemColumns.KEY_PRICE_UNIT_HT);
        priceSumIdx = cursor.getColumnIndex(OrderItemColumns.KEY_PRICE_SUM_HT);
        isNotInit = false;
        return this;
    }

    private OrderItemHelper setTextWithIdx(TextView view, Cursor cursor, int idx) {
        view.setText(cursor.getString(idx));
        return this;
    }

    public OrderItemHelper setTextItemId(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, idIdx);
    }

    public OrderItemHelper setTextItemOrderId(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, orderIdIdx);
    }

    public OrderItemHelper setTextItemQuantity(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, quantityIdx);
    }

    public OrderItemHelper setTextItemName(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, nameIdx);
    }

    public OrderItemHelper setTextItemPriceUnit(TextView view, Cursor cursor) {
        long priceSum = cursor.getLong(priceUnitIdx);
        String priceText = PriceHelper.getToStringPrice(priceSum);
        view.setText(priceText);
        return this;
    }

    public OrderItem getEntity(Cursor cursor) {
        if (isNotInit) {
            initWrapper(cursor);
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setId(idIdx > -1 ? cursor.getLong(idIdx) : -1);
        orderItem.setOrderId(orderIdIdx > -1 ? cursor.getLong(orderIdIdx) : null);
        orderItem.setProductId(productIdx > -1 ? cursor.getLong(productIdx) : null);
        // Ean
        orderItem.setName(nameIdx > -1 ? cursor.getString(nameIdx) : null);
        orderItem.setEan(eanIdx > -1 ? cursor.getString(eanIdx) : null);
        // Price
        orderItem.quantity = cursor.getInt(quantityIdx);
        orderItem.priceUnitHT = cursor.getLong(priceUnitIdx);
        orderItem.priceSumHT = cursor.getLong(priceSumIdx);

        return orderItem;
    }

    public static ContentValues getContentValues(OrderItem orderItem) {
        ContentValues initialValues = new ContentValues();
        if (orderItem.getId() > -1) {
            initialValues.put(OrderItemColumns.KEY_ID, Long.valueOf(orderItem.getId()));
        }
        initialValues.put(OrderItemColumns.KEY_ORDER_ID, orderItem.getOrderId());
        initialValues.put(OrderItemColumns.KEY_PRODUCT_ID, orderItem.getProductId());

        initialValues.put(OrderItemColumns.KEY_NAME, orderItem.getName());
        initialValues.put(OrderItemColumns.KEY_EAN, orderItem.getEan());

        initialValues.put(OrderItemColumns.KEY_QUANTITY, orderItem.getQuantity());
        initialValues.put(OrderItemColumns.KEY_PRICE_UNIT_HT, Long.valueOf(orderItem.getPriceUnitHT()));
        initialValues.put(OrderItemColumns.KEY_PRICE_SUM_HT, Long.valueOf(orderItem.getPriceSumHT()));

        return initialValues;
    }
}

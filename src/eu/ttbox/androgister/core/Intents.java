package eu.ttbox.androgister.core;

import android.content.Context;
import android.content.Intent;
import eu.ttbox.androgister.model.Offer;
import eu.ttbox.androgister.model.Order;
import eu.ttbox.androgister.service.OrderService;
import eu.ttbox.androgister.ui.order.OrderEditActivity;

public class Intents {

    public static final String ACTION_ADD_BASKET = "eu.ttbox.androgister.intent.ACTION_ADD_BASKET";
    public static final String ACTION_SAVE_BASKET = "eu.ttbox.androgister.intent.ACTION_SAVE_BASKET";

    public static final String ACTION_ORDER_ADD = "eu.ttbox.androgister.intent.ACTION_ORDER_ADD";
    public static final String ACTION_ORDER_VIEW_DETAIL = "eu.ttbox.androgister.intent.ACTION_ORDER_VIEW_DETAIL";
    public static final String ACTION_ORDER_DELETE = "eu.ttbox.androgister.intent.ACTION_ORDER_DELETE";
    public static final String ACTION_ORDER_SAVED = "eu.ttbox.androgister.intent.ACTION_ORDER_SAVED";

    
    public static final String EXTRA_OFFER = "eu.ttbox.androgister.intent.EXTRA_OFFER";
    public static final String EXTRA_ORDER = "eu.ttbox.androgister.intent.EXTRA_ORDER";
    public static final String EXTRA_ORDER_CANCELED_ID = "eu.ttbox.androgister.intent.EXTRA_ORDER_CANCELED_ID";

    public static Intent addToBasket(Offer status) {
        return new Intent(ACTION_ADD_BASKET).putExtra(EXTRA_OFFER, status);
    }

    public static Intent saveBasket() {
        return new Intent(ACTION_SAVE_BASKET);
    }

    public static Intent saveOrder(Context context, Order order) {
        return new Intent(context, OrderService.class) //
                .setAction(ACTION_ORDER_ADD) //
                .putExtra(EXTRA_ORDER, order);

        // return new Intent(ACTION_ORDER_ADD).putExtra(EXTRA_ORDER, order);
    }

    public static Intent viewOrderDetail(Context context, long orderId) {
        return new Intent(context, OrderEditActivity.class)//
                .setAction(Intents.ACTION_ORDER_VIEW_DETAIL)//
                .putExtra(Intents.EXTRA_ORDER, orderId);

//        return new Intent(ACTION_ORDER_VIEW_DETAIL).putExtra(EXTRA_ORDER, orderId);
    }

    public static Intent deleteOrderDetail(Context context, long orderId) {
        // return new Intent(ACTION_ORDER_DELETE).putExtra(EXTRA_ORDER,
        // orderId);
        Intent intent = new Intent(context, OrderService.class)//
                .setAction(ACTION_ORDER_DELETE) //
                .putExtra(EXTRA_ORDER, orderId);
        return intent;
    }

    public static Intent orderSaved(  long orderId) {
        return new Intent(ACTION_ORDER_SAVED).putExtra(EXTRA_ORDER, orderId);
    }
    
    public static Intent orderSaved(  long orderId, long canceledOrderId) {
        return new Intent(ACTION_ORDER_SAVED).putExtra(EXTRA_ORDER, orderId).putExtra(EXTRA_ORDER_CANCELED_ID, canceledOrderId);
    }

}

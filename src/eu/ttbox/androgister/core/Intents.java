package eu.ttbox.androgister.core;

import android.content.Intent;
import eu.ttbox.androgister.model.Offer;
import eu.ttbox.androgister.model.Order;

public class Intents {

    public static final String ACTION_ADD_BASKET = "eu.ttbox.androgister.intent.ACTION_ADD_BASKET";
    public static final String ACTION_SAVE_BASKET = "eu.ttbox.androgister.intent.ACTION_SAVE_BASKET";
    public static final String ACTION_SAVE_ORDER = "eu.ttbox.androgister.intent.ACTION_SAVE_ORDER";

    public static final String ACTION_VIEW_ORDER_DETAIL = "eu.ttbox.androgister.intent.ACTION_SAVE_ORDER";

    public static final String EXTRA_OFFER = "eu.ttbox.androgister.intent.EXTRA_OFFER";
    public static final String EXTRA_ORDER = "eu.ttbox.androgister.intent.EXTRA_ORDER";

    public static Intent addToBasket(Offer status) {
        return new Intent(ACTION_ADD_BASKET).putExtra(EXTRA_OFFER, status);
    }

    public static Intent saveBasket() {
        return new Intent(ACTION_SAVE_BASKET);
    }

    public static Intent saveOrder(Order order) {
        return new Intent(ACTION_SAVE_ORDER).putExtra(EXTRA_ORDER, order);
    }

    public static Intent viewOrderDetail(long orderId) {
        return new Intent(ACTION_VIEW_ORDER_DETAIL).putExtra(EXTRA_ORDER, orderId);
    }
    
}

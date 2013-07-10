package eu.ttbox.androgister.domain.dao.helper;

import eu.ttbox.androgister.domain.OrderItem;
import eu.ttbox.androgister.domain.ProductDao.Properties;
import android.os.Bundle;

public class OrderItemHelper {

    public static OrderItem createFromOffer(Bundle p) {
        OrderItem item = null;
        if (p != null) {
            item = new OrderItem();
            int qty = 1;
            item.withProductId(p.getLong(Properties.Id.columnName)) //
                    .withName(p.getString(Properties.Name.columnName)) //
                    .withEan(p.getString(Properties.Ean.columnName))//
                    .withQuantity(qty) // TODO Quantity
                    .withPriceUnitHT(p.getLong(Properties.PriceHT.columnName))//
                    .withPriceSumHT(qty * p.getLong(Properties.PriceHT.columnName));
        }
        return item;
    }
}

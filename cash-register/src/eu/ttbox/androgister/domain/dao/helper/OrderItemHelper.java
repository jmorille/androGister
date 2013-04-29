package eu.ttbox.androgister.domain.dao.helper;

import eu.ttbox.androgister.domain.OrderItem;
import eu.ttbox.androgister.domain.ProductDao.Properties;
import android.os.Bundle;

public class OrderItemHelper {

    public static OrderItem createFromOffer(Bundle p) {
        OrderItem item = null;
        if (p != null) {
            item = new OrderItem();

            item.withProductId(p.getLong(Properties.Id.columnName)) //
                    .withName(p.getString(Properties.Name.columnName)) //
                    .withEan(p.getString(Properties.Ean.columnName))//
                    .withQuantity(1) //TODO Quantity
                    .withPriceUnitHT(p.getLong(Properties.PriceHT.columnName));
        }
        return item;
    }

}

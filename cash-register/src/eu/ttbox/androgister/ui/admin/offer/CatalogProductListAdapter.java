package eu.ttbox.androgister.ui.admin.offer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.CatalogProduct;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.core.LazyListAdapter;
import eu.ttbox.androgister.model.PriceHelper;
import eu.ttbox.androgister.ui.admin.offer.CatalogProductListAdapter.ViewHolder;

public class CatalogProductListAdapter extends LazyListAdapter<CatalogProduct, ViewHolder> {

    public CatalogProductListAdapter(Context context, LazyList<CatalogProduct> lazyList) {
        super(context, R.layout.admin_product_list_item, lazyList);
    }
    @Override
    public void bindView(View view, ViewHolder holder, Context context, CatalogProduct item) {
        Product product = item.getProduct();
        holder.nameText.setText(product.getName());
        String priceString = PriceHelper.getToStringPrice(product.getPriceHT());
        holder.priceText.setText(priceString);
    }

    @Override
    public ViewHolder newViewHolder(View view, Context context, CatalogProduct item, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        holder.nameText = (TextView) view.findViewById(R.id.product_list_item_name);
        holder.priceText = (TextView) view.findViewById(R.id.product_list_item_price);
        return holder;
    }

    static class ViewHolder {
        TextView nameText;
        TextView priceText;
    }

}

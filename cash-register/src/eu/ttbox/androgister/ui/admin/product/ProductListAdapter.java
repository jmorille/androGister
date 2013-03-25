package eu.ttbox.androgister.ui.admin.product;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.core.LazyListAdapter;
import eu.ttbox.androgister.model.PriceHelper;
import eu.ttbox.androgister.ui.admin.product.ProductListAdapter.ViewHolder;

public class ProductListAdapter extends LazyListAdapter<Product, ViewHolder> {

    public ProductListAdapter(Context context, LazyList<Product> lazyList) {
        super(context, R.layout.admin_product_list_item, lazyList);
    }

    @Override
    public void bindView(View view, ViewHolder holder, Context context, Product item) {
        holder.nameText.setText(item.getName());
        String priceString = PriceHelper.getToStringPrice(item.getPriceHT());
        holder.priceText.setText(priceString);
    }

    @Override
    public ViewHolder newViewHolder(View view, Context context, Product item, ViewGroup parent) {
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

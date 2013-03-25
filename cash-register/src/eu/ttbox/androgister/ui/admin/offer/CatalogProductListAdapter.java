package eu.ttbox.androgister.ui.admin.offer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.domain.CatalogProduct;
import eu.ttbox.androgister.domain.core.LazyListAdapter;
import eu.ttbox.androgister.ui.admin.offer.CatalogProductListAdapter.ViewHolder;

public class CatalogProductListAdapter extends LazyListAdapter<CatalogProduct, ViewHolder> {

    public CatalogProductListAdapter(Context context, LazyList<CatalogProduct> lazyList) {
        super(context, android.R.layout.simple_spinner_item, lazyList);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public void bindView(View view, ViewHolder holder, Context context, CatalogProduct item) {
        holder.nameText.setText(item.getProduct().getName());
    }

    @Override
    public ViewHolder newViewHolder(View view, Context context, CatalogProduct item, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        holder.nameText = (TextView) view;
        return holder;
    }

    static class ViewHolder {
        TextView nameText;
    }

}

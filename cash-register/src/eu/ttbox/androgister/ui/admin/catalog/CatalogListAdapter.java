package eu.ttbox.androgister.ui.admin.catalog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.domain.Catalog;
import eu.ttbox.androgister.domain.core.LazyListAdapter;
import eu.ttbox.androgister.ui.admin.catalog.CatalogListAdapter.ViewHolder;

public class CatalogListAdapter extends LazyListAdapter<Catalog, ViewHolder> {

    public CatalogListAdapter(Context context, LazyList<Catalog> lazyList) {
        super(context, android.R.layout.simple_spinner_item, lazyList);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public void bindView(View view, ViewHolder holder, Context context, Catalog item) {
        holder.nameText.setText(item.getName());
    }

    @Override
    public ViewHolder newViewHolder(View view, Context context, Catalog item, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        holder.nameText = (TextView) view;
        return holder;
    }

    static class ViewHolder {
        TextView nameText;
    }

}

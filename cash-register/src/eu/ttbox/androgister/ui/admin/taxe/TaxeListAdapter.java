package eu.ttbox.androgister.ui.admin.taxe;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.Taxe;
import eu.ttbox.androgister.domain.core.LazyListAdapter;
import eu.ttbox.androgister.ui.admin.taxe.TaxeListAdapter.ViewHolder;

public class TaxeListAdapter extends LazyListAdapter<Taxe, ViewHolder> {

    private static final String TAG = "TaxeListAdapter";

    public TaxeListAdapter(Context context, LazyList<Taxe> lazyList) {
        super(context, R.layout.admin_calatog_list_item, lazyList);     
    }

    @Override
    public void bindView(View view, ViewHolder holder, Context context, Taxe item) { 
        holder.nameText.setText(item.getTitle());
    }

    @Override
    public ViewHolder newViewHolder(View view, Context context, Taxe item, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        holder.nameText = (TextView) view;
        return holder;
    }

    static class ViewHolder {
        TextView nameText;
    }

}

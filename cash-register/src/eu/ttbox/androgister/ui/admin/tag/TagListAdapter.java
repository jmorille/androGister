package eu.ttbox.androgister.ui.admin.tag;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.Tag;
import eu.ttbox.androgister.domain.core.LazyListAdapter;
import eu.ttbox.androgister.ui.admin.tag.TagListAdapter.ViewHolder;

public class TagListAdapter extends LazyListAdapter<Tag, ViewHolder> {

    private static final String TAG = "TagListAdapter";

    public TagListAdapter(Context context, LazyList<Tag> lazyList) {
        super(context, R.layout.admin_calatog_list_item, lazyList);     
    }

    @Override
    public void bindView(View view, ViewHolder holder, Context context, Tag item) { 
        holder.nameText.setText(item.getName());
    }

    @Override
    public ViewHolder newViewHolder(View view, Context context, Tag item, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        holder.nameText = (TextView) view;
        return holder;
    }

    static class ViewHolder {
        TextView nameText;
    }

}

package eu.ttbox.androgister.ui.admin.tag;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.domain.Tag;
import eu.ttbox.androgister.domain.core.LazyListAdapter;
import eu.ttbox.androgister.ui.admin.tag.TagListAdapter.ViewHolder;

public class TagListAdapter extends LazyListAdapter<Tag, ViewHolder> {

    public TagListAdapter(Context context, LazyList<Tag> lazyList) {
        super(context, android.R.layout.simple_spinner_item, lazyList);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

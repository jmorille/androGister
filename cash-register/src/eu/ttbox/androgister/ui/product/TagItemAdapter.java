package eu.ttbox.androgister.ui.product;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.TagDao;
import eu.ttbox.androgister.domain.TagDao.TagCursorHelper;
import eu.ttbox.androgister.domain.core.ViewHolderResourceCursorAdapter;
import eu.ttbox.androgister.ui.product.TagItemAdapter.ViewHolder;

public class TagItemAdapter extends ViewHolderResourceCursorAdapter<ViewHolder> {

    TagDao tagDao;
    public TagCursorHelper helper;

    public TagItemAdapter(Context context, Cursor c ) {
        super(context, R.layout.admin_calatog_list_item, c, true);
        AndroGisterApplication app = (AndroGisterApplication) context.getApplicationContext();
        tagDao = app.getDaoSession().getTagDao();
        helper = tagDao.getCursorHelper(null);
    }

    @Override
    public void bindView(View view, ViewHolder holder, Context context, Cursor cursor) {
        if (helper.isNotInit) {
            helper = helper.initWrapper(cursor);
        }
        holder.nameText.setText(helper.getName(cursor));
    }

    @Override
    public ViewHolder newViewHolder(View view, Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        holder.nameText = (TextView) view;
        return holder;
    }

    static class ViewHolder {
        TextView nameText;
    }

}

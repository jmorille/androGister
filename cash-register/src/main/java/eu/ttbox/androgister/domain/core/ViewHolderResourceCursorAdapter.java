package eu.ttbox.androgister.domain.core;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;

public abstract class ViewHolderResourceCursorAdapter<VIEW_HOLDER> extends ResourceCursorAdapter {

    public ViewHolderResourceCursorAdapter(Context context, int layout, Cursor c, boolean autoRequery) {
        super(context, layout, c, autoRequery); 
    }

    public ViewHolderResourceCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags); 
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = super.newView(context, cursor, parent);
        VIEW_HOLDER holder = newViewHolder(view, context, cursor, parent);
        view.setTag(holder);
        return view;
    }

    public abstract VIEW_HOLDER newViewHolder(View view, Context context, Cursor cursor, ViewGroup parent);

    
    public void bindView(View view, Context context,  Cursor cursor) {
        VIEW_HOLDER holder = (VIEW_HOLDER)view.getTag();
        bindView(view, holder, context, cursor);
    }

    public abstract void bindView(View view, VIEW_HOLDER holder, Context context,  Cursor cursor);

}

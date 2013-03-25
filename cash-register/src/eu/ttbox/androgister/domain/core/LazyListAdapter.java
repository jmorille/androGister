package eu.ttbox.androgister.domain.core;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.domain.DomainModel;

public abstract class LazyListAdapter<T extends DomainModel, VIEW_HOLDER> extends BaseAdapter {

    private int mLayout;
    private int mDropDownLayout;

    private LayoutInflater mInflater;

    protected boolean mDataValid;
    protected LazyList<T> lazyList;
    protected Context context;

    public LazyListAdapter(Context context, int layout, LazyList<T> lazyList) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = mDropDownLayout = layout;
        this.lazyList = lazyList;
        this.mDataValid = lazyList != null;
        this.context = context;
    }
    
    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there wasa not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */
    public LazyList<T> swapCursor(LazyList<T> newCursor) {
        if (newCursor == lazyList) {
            return null;
        }
        LazyList<T> oldCursor = lazyList;
        // Unregister cursor listener
        
        // Swap
        lazyList = newCursor;
        if (newCursor!=null) {
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyDataSetInvalidated();
        } 
        return oldCursor;
    }
    
    public void changeCursor( LazyList<T>  cursor) {
        LazyList<T>  old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public void setDropDownViewResource(int dropDownLayout) {
        this.mDropDownLayout = dropDownLayout;
    }

    public void close() {
        lazyList.close();
    }

    /**
     * Returns the list.
     * 
     * @return the list.
     */
    public LazyList<T> getLazyList() {
        return lazyList;
    }

    /**
     * @see android.widget.ListAdapter#getCount()
     */
    @Override
    public int getCount() {
        if (mDataValid && lazyList != null) {
            return lazyList.size();
        } else {
            return 0;
        }
    }

    /**
     * @see android.widget.ListAdapter#getItem(int)
     */
    @Override
    public T getItem(int position) {
        if (mDataValid && lazyList != null) {
            return lazyList.get(position);
        } else {
            return null;
        }
    }

    /**
     */
    @Override
    public long getItemId(int position) {
        if (mDataValid && lazyList != null) {
            T item = lazyList.get(position);
            if (item != null) {
                return item.getId();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * @see android.widget.ListAdapter#getView(int, View, ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when lazylist is populated");
        }

        T item = lazyList.get(position);
        if (item == null) {
            throw new IllegalStateException("Item at position " + position + " is null");
        }

        View v;
        if (convertView == null) {
            v = newView(context, item, parent, mLayout);
        } else {
            v = convertView;
        }
        bindView(v, context, item);
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (mDataValid) {
            T item = lazyList.get(position);
            View v;
            if (convertView == null) {
                v = newDropDownView(context, item, parent);
            } else {
                v = convertView;
            }
            bindView(v, context, item);
            return v;
        } else {
            return null;
        }
    }

    /**
     * Makes a new view to hold the data contained in the item.
     * 
     * @param context
     *            Interface to application's global information
     * @param item
     *            The object that contains the data
     * @param parent
     *            The parent to which the new view is attached to
     * @return the newly created view.
     */

    public View newView(Context context, T item, ViewGroup parent, int layoutResourceId) {
        View view = mInflater.inflate(layoutResourceId, parent, false);
        VIEW_HOLDER holder = newViewHolder(view, context, item, parent);
        view.setTag(holder);
        return view;
    }

    public abstract VIEW_HOLDER newViewHolder(View view, Context context, T item, ViewGroup parent);

    /**
     * Makes a new drop down view to hold the data contained in the item.
     * 
     * @param context
     *            Interface to application's global information
     * @param item
     *            The object that contains the data
     * @param parent
     *            The parent to which the new view is attached to
     * @return the newly created view.
     */
    public View newDropDownView(Context context, T item, ViewGroup parent) {
        return newView(context, item, parent, mDropDownLayout);
    }

    /**
     * Bind an existing view to the data data contained in the item.
     * 
     * @param view
     *            Existing view, returned earlier by newView
     * @param context
     *            Interface to application's global information
     * @param cursor
     *            The object that contains the data
     */
    @SuppressWarnings("unchecked")
    public void bindView(View view, Context context, T item) {
        VIEW_HOLDER holder = (VIEW_HOLDER) view.getTag();
        bindView(view, holder, context, item);
    }

    public abstract void bindView(View view, VIEW_HOLDER holder, Context context, T item);

}

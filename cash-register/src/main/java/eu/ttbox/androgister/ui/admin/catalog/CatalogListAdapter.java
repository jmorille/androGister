package eu.ttbox.androgister.ui.admin.catalog;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.Catalog;
import eu.ttbox.androgister.domain.core.LazyListAdapter;
import eu.ttbox.androgister.domain.provider.ProductProvider;
import eu.ttbox.androgister.ui.admin.catalog.CatalogListAdapter.ViewHolder;

public class CatalogListAdapter extends LazyListAdapter<Catalog, ViewHolder> {

    private static final String TAG = "CatalogListAdapter";

    private Context context;
//    public CharSequence dragData;
    private MyDragEventListener myDragEventListener = new MyDragEventListener();

    public CatalogListAdapter(Context context, LazyList<Catalog> lazyList) {
        super(context, R.layout.admin_calatog_list_item, lazyList);
        this.context = context;
    }

    @Override
    public void bindView(View view, ViewHolder holder, Context context, Catalog item) {
        holder.nameText.setText(item.getName());

    }

    @Override
    public ViewHolder newViewHolder(View view, Context context, Catalog item, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        holder.nameText = (TextView) view;
        view.setOnDragListener(myDragEventListener);
        return holder;
    }

    static class ViewHolder {
        TextView nameText;
    }

    protected class MyDragEventListener implements View.OnDragListener {

        // This is the method that the system calls when it dispatches a drag
        // event to the
        // listener.
        public boolean onDrag(View v, DragEvent event) {
//            boolean result = false;
//            boolean mAcceptsDrag = true;
//            boolean mDragInProgress = false;
//            boolean mHovering = false;

            switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED: {
                if (event.getClipDescription().hasMimeType(ProductProvider.PRODUCT_MIME_TYPE)) {
                    // claim to accept any dragged content
                    Log.i(TAG, "Drag started, event=" + event);
                    // cache whether we accept the drag to return for LOCATION
                    // events
//                    mDragInProgress = true;
//                    mAcceptsDrag = result = true;
                    // Redraw in the new visual state if we are a potential drop
                    // target
                    v.invalidate();
                    return true;
                } else {
                    return false;
                }
            }
//                break;

            case DragEvent.ACTION_DRAG_ENDED: {
                Log.d(TAG, "Drag ended ." + ((TextView) v).getText());
//                if (mAcceptsDrag) {
                    v.invalidate();
//                }
//                mDragInProgress = false;
//                mHovering = false;
                    return true;
            }
//                break;

            case DragEvent.ACTION_DRAG_LOCATION: {
                // we returned true to DRAG_STARTED, so return true here
//                Log.d(TAG, "... seeing drag locations ..." + ((TextView) v).getText());
//                result = mAcceptsDrag;
                return true;
            }
//                break;

            case DragEvent.ACTION_DROP: {
                Log.i(TAG, "Got a drop! dot=" + this + " event=" + event + " // " + ((TextView) v).getText());
                processDrop(event);
//                result = true;
                return true;
            }
//                break;

            case DragEvent.ACTION_DRAG_ENTERED: {
                Log.d(TAG, "Entered dot @ " + this);
                v.setBackgroundColor(Color.RED);
//                mHovering = true;
                v.invalidate();
                return true;
            }
//                break;

            case DragEvent.ACTION_DRAG_EXITED: {
                Log.d(TAG, "Exited dot @ " + this);
                v.setBackgroundColor(Color.BLACK);
//                mHovering = false;
                v.invalidate();
                return true;
            }
//                break;

            default:
                Log.i(TAG, "other drag event: " + event);
//                result = mAcceptsDrag;
                break;
            }

            return false;

        };
    }

    private void processDrop(DragEvent event) {
        final ClipData data = event.getClipData();
        final int N = data.getItemCount();
        for (int i = 0; i < N; i++) {
            ClipData.Item item = data.getItemAt(i);
            Log.i(TAG, "Dropped item " + i + " : " + item);
            // if (mReportView != null) {
            String text = item.coerceToText(context).toString();
            if (event.getLocalState() == (Object) this) {
                text += " : Dropped on self!";
            }
            text += " : ";
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            // mReportView.setText(text);
            // }
        }
    }

}

package eu.ttbox.androgister.ui.admin.catalog;

import android.content.ClipData;
import android.content.ClipDescription;
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
import eu.ttbox.androgister.ui.admin.catalog.CatalogListAdapter.ViewHolder;

public class CatalogListAdapter extends LazyListAdapter<Catalog, ViewHolder> {

    private Context context;
    public CharSequence dragData;

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
//        view.setOnDragListener(new MyDragEventListener());
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

            // Defines a variable to store the action type for the incoming
            // event
            final int action = event.getAction();

            // Handles each of the expected events
            switch (action) {

            case DragEvent.ACTION_DRAG_STARTED:

                // Determines if this View can accept the dragged data
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                     // As an example of what your application might do,
                    // applies a blue color tint to the View to indicate that it
                    // can accept
                    // data.
                    // v.setColorFilter(Color.BLUE);
                     // Invalidate the view to force a redraw in the new tint
                    v.invalidate();
                     // returns true to indicate that the View can accept the
                    // dragged data.
                    return (true);
                 } else {
                     // Returns false. During the current drag and drop
                    // operation, this View will
                    // not receive events again until ACTION_DRAG_ENDED is sent.
                    return (false);
                 }
               

            case DragEvent.ACTION_DRAG_ENTERED: 
                // Applies a green tint to the View. Return true; the return
                // value is ignored. 
                // v.setColorFilter(Color.GREEN);

                // Invalidate the view to force a redraw in the new tint
                v.invalidate();

                return (true);

                

            case DragEvent.ACTION_DRAG_LOCATION: 
                // Ignore the event
                return (true);

                

            case DragEvent.ACTION_DRAG_EXITED: 
                // Re-sets the color tint to blue. Returns true; the return
                // value is ignored.
                // v.setColorFilter(Color.BLUE); 
                // Invalidate the view to force a redraw in the new tint
                v.invalidate(); 
                return (true);

               

            case DragEvent.ACTION_DROP:

                // Gets the item containing the dragged data
                ClipData.Item item = event.getClipData().getItemAt(0);

                // Gets the text data from the item.
                dragData = item.getText();

                // Displays a message containing the dragged data.
                Toast.makeText(context, "Dragged data is " + dragData, Toast.LENGTH_LONG);

                // Turns off any color tints
                // v.clearColorFilter(); 
                // Invalidates the view to force a redraw
                v.invalidate(); 
                // Returns true. DragEvent.getResult() will return true.
                return (true);
 

            case DragEvent.ACTION_DRAG_ENDED:

                // Turns off any color tinting
                // v.clearColorFilter();

                // Invalidates the view to force a redraw
                v.invalidate();

                // Does a getResult(), and displays what happened.
                if (event.getResult()) {
                    Toast.makeText(context, "The drop was handled.", Toast.LENGTH_LONG);

                } else {
                    Toast.makeText(context, "The drop didn't work.", Toast.LENGTH_LONG);

                }
                ;

                // returns true; the value is ignored.
                return (true);
 

            // An unknown action type was received.
            default:
                Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");

                return false;
            }
            
        };
    }
}

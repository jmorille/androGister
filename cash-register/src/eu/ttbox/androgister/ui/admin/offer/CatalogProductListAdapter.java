package eu.ttbox.androgister.ui.admin.offer;

import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.PriceHelper;
import eu.ttbox.androgister.domain.CatalogProduct;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.core.LazyListAdapter;
import eu.ttbox.androgister.domain.provider.ProductProvider;
import eu.ttbox.androgister.ui.admin.offer.CatalogProductListAdapter.ViewHolder;
import eu.ttbox.androgister.ui.admin.product.ProductUiHelper;

public class CatalogProductListAdapter extends LazyListAdapter<CatalogProduct, ViewHolder> {

    private static final String TAG = "CatalogProductListAdapter";

    private Context context;

    private ProductUiHelper productColor;
    private GestureListener gestureListener = new GestureListener();

    // ===========================================================
    // Constructor
    // ===========================================================

    public CatalogProductListAdapter(Context context, LazyList<CatalogProduct> lazyList) {
        super(context, R.layout.admin_product_list_item, lazyList);
        this.context = context;
        this.productColor = new ProductUiHelper(context);
    }

    // ===========================================================
    // Bindings
    // ===========================================================

    @Override
    public void bindView(View view, ViewHolder holder, Context context, CatalogProduct item) {
        Product product = item.getProduct();
        holder.nameText.setText(product.getName());
        String priceString = PriceHelper.getToStringPrice(product.getPriceHT());
        holder.priceText.setText(priceString);
        // Color
        Drawable grad = productColor.getStateGradientDrawable(product.getTag().getColor());
        view.setBackgroundDrawable(grad);
        // Listener
        view.setOnTouchListener(gestureListener);
        // view.setOn
    }

    @Override
    public ViewHolder newViewHolder(View view, Context context, CatalogProduct item, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        holder.nameText = (TextView) view.findViewById(R.id.product_list_item_name);
        holder.priceText = (TextView) view.findViewById(R.id.product_list_item_price);
        return holder;
    }

    static class ViewHolder {
        TextView nameText;
        TextView priceText;
    }

    // ===========================================================
    // Gesture Listener
    // ===========================================================
    // http://mobile.tutsplus.com/tutorials/android/android-sdk-implementing-drag-and-drop-functionality/

    private class ListGestureListener extends SimpleOnGestureListener {

        private View view;

        public ListGestureListener(View view) {
            super();
            this.view = view;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (distanceX > distanceY) {
                return startDrag(view, e1);
            }
            return false;
        }

    }

    /**
     * http://developer.android.com/training/gestures/movement.html
     */
    private class GestureListener implements OnTouchListener {

        private VelocityTracker mVelocityTracker = null;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // dumpEvent(event);
            int action = MotionEventCompat.getActionMasked(event);
            int index = MotionEventCompat.getActionIndex(event);
            int pointerId = MotionEventCompat.getPointerId(event, index);

            switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the
                    // velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                Log.d(TAG, "PointerCount : " + MotionEventCompat.getPointerCount(event));
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);
                return true;
                // break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer
                // ID.
                mVelocityTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.
                // Log.d("", "Velocity (X,Y): " +
                // VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                // pointerId) + " , " +
                // VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                // pointerId));
                float veloX = Math.abs(VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId));
                float veloY = Math.abs(VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId));
                if (veloX > veloY) {
                    return startDrag(view, event);
                }
                // return true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker.recycle();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "GestureListener ignore  action ACTION_POINTER_DOWN : " + event.getAction());
                Log.d(TAG, "PointerCount : " + MotionEventCompat.getPointerCount(event));
                break;
            default:
                Log.d(TAG, "GestureListener ignore  action  : " + event.getAction());
                return false;
            }

            return false;
        }
    }

    private boolean startDrag(View view, MotionEvent event) {

        Long productId = null; // item.getProductId();
        Intent intent = new Intent(Intent.ACTION_INSERT + ".Product");
        intent.putExtra(Intent.EXTRA_UID, productId);
        // ClipData
        String label = "Product Catalog";
        String[] mimeTypes = new String[] { ProductProvider.PRODUCT_MIME_TYPE };
        Item item = new Item(intent);
        ClipData data = new ClipData(label, mimeTypes, item);

        // ClipData data = ClipData.newIntent("Product", intent);
        DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(data, shadowBuilder, (Object) view, 0);
        return true;
    }

}

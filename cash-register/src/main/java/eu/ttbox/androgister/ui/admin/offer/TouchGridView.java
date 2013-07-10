package eu.ttbox.androgister.ui.admin.offer;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
/**
 *  http://developer.android.com/training/gestures/viewgroup.html
 * @author jmorille
 *
 */
public class TouchGridView extends GridView {

    private boolean mIsScrolling;
    private int mTouchSlop;

    //

    public TouchGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TouchGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchGridView(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);
        // Always handle the case of the touch gesture being complete.
        
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                int itemnum = pointToPosition(x, y);
                if (itemnum == AdapterView.INVALID_POSITION) {
                        break;
                }
                 View item = (View) getChildAt(itemnum - getFirstVisiblePosition());

        }
        
//        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
//            // Release the scroll.
//            mIsScrolling = false;
//            int x = (int) ev.getX();
//            int y = (int) ev.getY();
//            int itemnum = pointToPosition(x, y);
//            if (itemnum == AdapterView.INVALID_POSITION) {
//                break;
//        }
//         }

        switch (action) {
        case MotionEvent.ACTION_MOVE: {
            if (mIsScrolling) {
                // We're currently scrolling, so yes, intercept the
                // touch event!
                return true;
            }

            // If the user has dragged her finger horizontally more than
            // the touch slop, start the scroll

            // left as an exercise for the reader
            final boolean xDiff = calculateDistanceX(ev);

            // Touch slop should be calculated using ViewConfiguration
            // constants.
            if (xDiff ) {
                // Start scrolling!
                mIsScrolling = true;
                return true;
            }
            break;
        }
        }

        return super.onInterceptTouchEvent(ev);

    }

    private boolean calculateDistanceX(MotionEvent ev) {

        float dx = Math.abs(  ev.getHistoricalX(0) - ev.getX());
        float dy =Math.abs(  ev.getHistoricalY(0) - ev.getY());
        if (dx>dy && dx >20) {
//            return start
            return true;
        }
        return false;
    }
    
}

package eu.ttbox.androgister.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class PinnedHeaderListView extends AutoScrollListView 
implements OnScrollListener, OnItemSelectedListener {

    // listener
    private OnItemSelectedListener mOnItemSelectedListener;
    private OnScrollListener mOnScrollListener;
    private int mScrollState;
    
    // Other
    
    public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle); 
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs); 
    }

    public PinnedHeaderListView(Context context) {
        super(context); 
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int height = getHeight();

        int windowTop = 0;
        int windowBottom = height;
        // Adjust with header 
//        for (int i = 0; i < mSize; i++) {
//            PinnedHeader header = mHeaders[i];
//            if (header.visible) {
//                if (header.state == TOP) {
//                    windowTop = header.y + header.height;
//                } else if (header.state == BOTTOM) {
//                    windowBottom = header.y;
//                    break;
//                }
//            }
//        }
        
        View selectedView = getSelectedView();
        if (selectedView!=null) {
            if (selectedView.getTop() < windowTop) {
                setSelectionFromTop(position, windowTop);
            } else if (selectedView.getBottom() > windowBottom) {
                setSelectionFromTop(position, windowBottom - selectedView.getHeight());
            }
        }
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(parent, view, position, id);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onNothingSelected(parent);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollState = scrollState;
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(this, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // Something with adpater 
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(this, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

}

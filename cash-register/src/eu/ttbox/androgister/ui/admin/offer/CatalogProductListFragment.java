package eu.ttbox.androgister.ui.admin.offer;

import java.util.ArrayList;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.CoreHelper;
import eu.ttbox.androgister.domain.CatalogProduct;
import eu.ttbox.androgister.domain.CatalogProductDao;
import eu.ttbox.androgister.domain.CatalogProductDao.Properties;
import eu.ttbox.androgister.domain.core.LazyListAdapter;
import eu.ttbox.androgister.ui.core.crud.EntityLazyListFragment;

public class CatalogProductListFragment extends EntityLazyListFragment<CatalogProduct, CatalogProductDao> {

    private static final String TAG = "CatalogProductListFragment";

    private GridView listView;

    // Instance
    private Long catalogId;

    // ===========================================================
    // Constructor
    // ===========================================================
// http://developer.android.com/training/gestures/viewgroup.html
    private class TouchGridView extends GridView {

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
            if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                // Release the scroll.
                mIsScrolling = false;
                return false; // Do not intercept touch event, let the child
                              // handle it
            }

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

            return false;

        }

        private boolean calculateDistanceX(MotionEvent ev) {

            float dx = Math.abs(  ev.getHistoricalX(0) - ev.getX());
            float dy =Math.abs(  ev.getHistoricalY(0) - ev.getY());
            if (dx>dy && dx >20) {
//                return start
                return true;
            }
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_offer_creator, container, false);
        // Binding
        listView = (GridView) v.findViewById(R.id.calalog_product_gridview);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new ModeCallback());
        // listView.setOnItemClickListener(new DragAndDropListener());
        // listView.setOnItemLongClickListener(new DragAndDropListener());
        // listView.setOnTouchListener(new GestureListener());
        return v;
    }

    @Override
    public AdapterView<ListAdapter> getAdapterContainer() {
        return listView;
    }

    // ===========================================================
    // Gesture Listener
    // ===========================================================
    // http://mobile.tutsplus.com/tutorials/android/android-sdk-implementing-drag-and-drop-functionality/
    private class GestureListener implements OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // setup drag
                // CatalogProduct item = (CatalogProduct)
                // parent.getItemAtPosition(position);
                Long productId = null; // item.getProductId();
                Intent intent = new Intent(Intent.ACTION_INSERT + ".Product");
                intent.putExtra(Intent.EXTRA_UID, productId);

                ClipData data = ClipData.newIntent("Product", intent);
                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, (Object) view, 0);
                return true;
            }
            return false;
        }
    }

    // ===========================================================
    // Multi Choice Listener
    // ===========================================================

    // http://developer.android.com/guide/topics/ui/drag-drop.html
    private class DragAndDropListener implements OnItemLongClickListener, OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Create a new ClipData.Item from the ImageView object's tag
            // ClipData.Item item = new ClipData.Item("Tag");
            // ClipData dragData = new
            // ClipData(v.getTag(),ClipData.MIMETYPE_TEXT_PLAIN,item);
            CatalogProduct item = (CatalogProduct) parent.getItemAtPosition(position);
            Long productId = item.getProductId();
            Intent intent = new Intent(Intent.ACTION_INSERT + ".Product");
            intent.putExtra(Intent.EXTRA_UID, productId);

            ClipData data = ClipData.newIntent("Product", intent);
            DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, new ANRShadowBuilder(view), (Object) view, 0);
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            onItemClick(parent, view, position, id);

            return true;
        }

        // Shadow builder that can ANR if desired
        class ANRShadowBuilder extends DragShadowBuilder {

            public ANRShadowBuilder(View view) {
                super(view);
            }

            @Override
            public void onDrawShadow(Canvas canvas) {

                super.onDrawShadow(canvas);
            }
        }

    }

    // ===========================================================
    // Multi Choice Listener
    // ===========================================================
    private class ModeCallback implements ListView.MultiChoiceModeListener {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.admin_list_select_menu, menu);
            mode.setTitle(getResources().getString(R.string.item_selected_title));
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
            case R.id.menu_admin_edit_delete:
                deleteSelectedItems();
                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                Toast.makeText(getActivity(), "Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            final int checkedCount = listView.getCheckedItemCount();
            switch (checkedCount) {
            case 0:
                mode.setSubtitle(null);
                break;
            default:
                String subtitlesSelected = getResources().getQuantityString(R.plurals.item_selected, checkedCount, checkedCount);
                mode.setSubtitle(subtitlesSelected);
                break;
            }
        }

    }

    // ===========================================================
    // Service
    // ===========================================================

    @Override
    public CatalogProductDao getEntityDao() {
        return getDaoSession().getCatalogProductDao();
    }

    @Override
    public LazyListAdapter<CatalogProduct, ? extends Object> createListAdapter(LazyList<CatalogProduct> lazyList) {
        return new CatalogProductListAdapter(getActivity(), lazyList);
    }

    @Override
    public QueryBuilder<CatalogProduct> createSearchQuery(CatalogProductDao entityDao) {
        QueryBuilder<CatalogProduct> query = entityDao.queryBuilder(); //
        if (catalogId != null) {
            query.where(Properties.CatalogId.eq(catalogId));
        }
        // .orderAsc(Properties.Name); //
        ;
        // query.

        return query;
    }

    // ===========================================================
    // Action
    // ===========================================================

    @Override
    public void onEntityClick(Long id) {
        // TODO Auto-generated method stub

    }

    private void deleteSelectedItems() {
        long[] checkedIds = listView.getCheckedItemIds();
        Long[] catProdIds = CoreHelper.convertToLongArray(checkedIds);
        deleteSelectedItems(catProdIds);
    }

    private void deleteSelectedItems(Long... catProdIds) {
        if (catProdIds != null && catProdIds.length > 0) {
            entityDao.deleteByKeyInTx(catProdIds);
            reloadData();
        }
    }

    public void onSelectCalalogId(Long catalogId) {
        Log.d(TAG, "Select Catalog Id : " + catalogId);
        this.catalogId = catalogId;
        reloadData();

    }

    public void onSelectProductId(Long[] productIds) {
        if (catalogId == null) {
            Log.w(TAG, "onSelectProductId : No Catlog Selected");
        } else {
            ArrayList<CatalogProduct> toInsert = new ArrayList<CatalogProduct>(productIds.length);
            for (Long productId : productIds) {
                CatalogProduct catProd = new CatalogProduct();
                catProd.setCatalogId(catalogId);
                catProd.setProductId(productId);
                toInsert.add(catProd);
            }
            entityDao.insertOrReplaceInTx(toInsert);
            reloadData();
        }

    }
}

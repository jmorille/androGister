package eu.ttbox.androgister.ui.admin.offer;

import java.util.ArrayList;

import android.content.ClipData;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
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

    private ListView listView;

    // Instance
    private Long catalogId;

    // ===========================================================
    // Constructor
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_offer_creator, container, false);
        // Binding
        listView = (ListView) v.findViewById(R.id.calalog_product_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new ModeCallback());

//        listView.setOnItemLongClickListener(new DragAndDropListener());
        return v;
    }

    @Override
    public AdapterView<ListAdapter> getAdapterContainer() {
        return listView;
    }

    // ===========================================================
    // Multi Choice Listener
    // ===========================================================

    // http://developer.android.com/guide/topics/ui/drag-drop.html
    private class DragAndDropListener implements OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
           
            // Create a new ClipData.Item from the ImageView object's tag
            // ClipData.Item item = new ClipData.Item("Tag");
            // ClipData dragData = new
            // ClipData(v.getTag(),ClipData.MIMETYPE_TEXT_PLAIN,item);

            ClipData data = ClipData.newPlainText("dot", "Dot : " + v.toString());

            v.startDrag(data, new ANRShadowBuilder(v), (Object) v, 0);
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

package eu.ttbox.androgister.ui.admin.product;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.CoreHelper;
import eu.ttbox.androgister.domain.DaoSession;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.ProductDao.Properties;
import eu.ttbox.androgister.domain.core.LazyListAdapter;
import eu.ttbox.androgister.ui.core.crud.EntityLazyListFragment;

public class ProductListFragment extends EntityLazyListFragment<Product, ProductDao> {

    private static final String TAG = "ProductAdminFragment";
 
    // Binding
    private  GridView productList;

    private OnSelectProductListener onSelectProductListener;

    // Instance
    private Long tagId;

    // ===========================================================
    // Listener
    // ===========================================================

    public void setOnSelectProductListener(OnSelectProductListener onSelectTagListener) {
        this.onSelectProductListener = onSelectTagListener;
    }

    public interface OnSelectProductListener {

        void onSelectProductId(Long... productIds);

    }

    // ===========================================================
    // Constructor
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_product_list, container, false);
        // Binding
        productList = (GridView) v.findViewById(R.id.product_gridview);
        // List Listener
        // http://developer.android.com/guide/topics/ui/menus.html#CAB
        productList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        productList.setMultiChoiceModeListener(new ModeCallback());

        // Menu on Fragment
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public LazyListAdapter<Product, ? extends Object> createListAdapter(LazyList<Product> lazyList) {
        return new ProductListAdapter(getActivity(), null);
    }

    @Override
    public AdapterView<ListAdapter> getAdapterContainer() {
        return productList;
    }

    // ===========================================================
    // Menu
    // ===========================================================

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.admin_product_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_admin_product_add:
            onProductEditClick();
            return true;
        }
        return false;
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
            case R.id.share:
                shareSelectedItems();
                mode.finish();
                break;
            default:
                Toast.makeText(getActivity(), "Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            final int checkedCount = productList.getCheckedItemCount();
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
    // Load Data
    // ===========================================================

    public ProductDao getEntityDao() {
        AndroGisterApplication app = (AndroGisterApplication) getActivity().getApplication();
        DaoSession daoSession = app.getDaoSession();
        return daoSession.getProductDao();
    }

    @Override
    public QueryBuilder<Product> createSearchQuery(ProductDao entityDao) {
        QueryBuilder<Product> query = entityDao.queryBuilder() //
                .orderAsc(Properties.Name, Properties.Description);
        if (tagId != null) {
            query.where(Properties.TagId.eq(tagId));
        }
        return query;
    }

    // ===========================================================
    // Business
    // ===========================================================

    private void deleteSelectedItems() {
        long[] checkedIds = productList.getCheckedItemIds();
        Long[] deleteIds = CoreHelper.convertToLongArray(checkedIds);
        entityDao.deleteByKeyInTx(deleteIds);
        reloadData();
    }

    private void shareSelectedItems() {
        long[] checkedIds = productList.getCheckedItemIds();
        Toast.makeText(getActivity(), "Shared " + checkedIds.length + " items", Toast.LENGTH_SHORT).show();
        if (onSelectProductListener!=null) {
            Long[] productIds = CoreHelper.convertToLongArray(checkedIds);
            onSelectProductListener.onSelectProductId(productIds);
        }
     }

    public void onEntityClick(Long entityId) {
        Intent intent = new Intent(getActivity(), ProductEditActivity.class);
        intent.setAction(Intent.ACTION_EDIT);
        intent.putExtra(Intent.EXTRA_UID, entityId);
       
        startActivityForResult(intent, ENTITY_EDIT_REQUEST_CODE);
    }

    private void onProductEditClick() {
        Intent intent = new Intent(getActivity(), ProductEditActivity.class);
        intent.setAction(Intent.ACTION_INSERT);
        if (tagId!=null) {
            intent.putExtra(Properties.TagId.columnName, tagId);
        }
        startActivityForResult(intent, ENTITY_EDIT_REQUEST_CODE);
    }

    public void onSelectTagId(Long tagId) {
        this.tagId = tagId;
        Log.d(TAG, "onSelectTagId : " + tagId);
        reloadData();
    }

}

package eu.ttbox.androgister.ui.admin.product;

import android.app.Activity;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.CoreHelper;
import eu.ttbox.androgister.domain.DaoSession;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.ProductDao.Properties;
import eu.ttbox.androgister.ui.core.crud.EntityLazyListFragment;

public class ProductListFragment extends EntityLazyListFragment<Product> {

    private static final String TAG = "ProductAdminFragment";

    public static final int PRODUCT_EDIT_REQUEST_CODE = 111;

    // Service
    private ProductDao productDao;

    // Binding
    ListView productList;

    // Instance
    ProductListAdapter listAdapter;

    

    // ===========================================================
    // Event
    // ===========================================================

    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "onActivityResult :  requestCode = " + requestCode + "  ==> resultCode = " + resultCode);
        if (requestCode == PRODUCT_EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(getActivity(), "Success Edit", Toast.LENGTH_LONG).show();
            reloadData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ===========================================================
    // Constructor
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_product_list, container, false);
        // Binding
        productList = (ListView) v.findViewById(R.id.product_list);
        // List Listener
        // http://developer.android.com/guide/topics/ui/menus.html#CAB
        productList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        productList.setMultiChoiceModeListener(new ModeCallback());

        // Menu on Fragment
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Service
        productDao = getEntityDao();
        // List
        loadData(null);
        // Listener
        productList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product item = (Product) parent.getItemAtPosition(position);
                onProductEditClick(item.getId());
            }
        });
    }

    @Override
    public void onDestroyView() {
        if (listAdapter != null) {
            // Close LazyListAdpater for closing LazyList
            listAdapter.close();
            listAdapter = null;
        }
        super.onDestroyView();
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
                mode.setSubtitle( subtitlesSelected);
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

    public void reloadData() {
        loadData(null);
    }
    
    public void loadData(Bundle bundle) {
        ProductListAdapter oldListAdapter = listAdapter;
        // Search The List
        LazyList<Product> products = productDao.queryBuilder() //
                .orderAsc(Properties.Name, Properties.Description) //
                .listLazy();
        listAdapter = new ProductListAdapter(getActivity(), products);
        productList.setAdapter(listAdapter);
        // Close Previous Adapter
        if (oldListAdapter != null) {
            oldListAdapter.close();
        }
    }

    // ===========================================================
    // Business
    // ===========================================================

    private void deleteSelectedItems() {
        long[] checkedIds = productList.getCheckedItemIds();
        Long[] deleteIds = CoreHelper.convertToLongArray(checkedIds);
        productDao.deleteByKeyInTx(deleteIds);
        reloadData();
    }

    private void shareSelectedItems() {
        long[] checkedIds = productList.getCheckedItemIds();
        Toast.makeText(getActivity(), "Shared " + checkedIds.length + " items", Toast.LENGTH_SHORT).show();
        
//        productDao.load(key)
    }

    private void onProductEditClick(Long entityId) {
        Intent intent = new Intent(getActivity(), ProductEditActivity.class);
        intent.setAction(Intent.ACTION_EDIT);
        intent.putExtra(Intent.EXTRA_UID, entityId);
        startActivityForResult(intent, PRODUCT_EDIT_REQUEST_CODE);
    }

    private void onProductEditClick() {
        Intent intent = new Intent(getActivity(), ProductEditActivity.class);
        intent.setAction(Intent.ACTION_INSERT);
        startActivityForResult(intent, PRODUCT_EDIT_REQUEST_CODE);
    }

}

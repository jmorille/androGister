package eu.ttbox.androgister.ui.admin.product;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "onActivityResult :  requestCode = " + requestCode + "  ==> resultCode = " + resultCode);
        if (requestCode == PRODUCT_EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(getActivity(), "Success Edit", Toast.LENGTH_LONG).show();
            loadData(null);
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
    // Load Data
    // ===========================================================

    public ProductDao getEntityDao() {
        AndroGisterApplication app = (AndroGisterApplication) getActivity().getApplication();
        DaoSession daoSession = app.getDaoSession();
        return daoSession.getProductDao();
    }

    public void loadData(Bundle bundle) { 
        ProductListAdapter oldListAdapter = listAdapter;
        // Search The List
        LazyList<Product> products = productDao.queryBuilder() //
                .orderAsc(Properties.Tag, Properties.Description) //
                .listLazy();
        listAdapter = new ProductListAdapter(getActivity(), products);
        productList.setAdapter(listAdapter);
        // Close Previous Adapter
        if (oldListAdapter!=null) {
            oldListAdapter.close();
        }
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
    // Business
    // ===========================================================

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

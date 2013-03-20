package eu.ttbox.androgister.ui.admin.product;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.DaoSession;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.ProductDao.Properties;

public class ProductAdminFragment extends Fragment {


    // Service
    private DaoSession daoSession;
    private ProductDao productDao;

    // Binding
    ListView productList;

    // Instance
    ProductListAdapter listAdapter;

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
        AndroGisterApplication app = (AndroGisterApplication) getActivity().getApplication();
        daoSession = app.getDaoSession();
        productDao = daoSession.getProductDao();
        // List
        LazyList<Product> products = productDao.queryBuilder() //
                .orderAsc(Properties.Tag, Properties.Description) //
                .listLazy();
        ProductListAdapter listAdapter = new ProductListAdapter(getActivity(), products);
        productList.setAdapter(listAdapter);
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
    // Business
    // ===========================================================

    private void onProductEditClick(Long entityId) {
        Intent intent = new Intent(getActivity(), ProductEditActivity.class);
        intent.setAction(Intent.ACTION_EDIT);
        intent.putExtra(Intent.EXTRA_UID , entityId);
        getActivity().startActivity(intent);
    }
    
    private void onProductEditClick() {
        Intent intent = new Intent(getActivity(), ProductEditActivity.class);
        intent.setAction(Intent.ACTION_INSERT);
        getActivity().startActivity(intent);
    }

}

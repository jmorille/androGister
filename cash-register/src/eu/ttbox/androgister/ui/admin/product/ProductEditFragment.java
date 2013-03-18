package eu.ttbox.androgister.ui.admin.product;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.DaoSession;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.ProductDao;

public class ProductEditFragment extends Fragment {

    private static final String TAG = "ProductEditFragment";
    // Service
    private DaoSession daoSession;
    private ProductDao productDao;

    // Binding
    private EditText nameText;

    // Instance
    private Product entity;

    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.product_edit, container, false);
        // Binding
        // nameText = (EditText) v.findViewById(R.id.product_edit);
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
        // Load Data
        loadEntity(getArguments());
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
            onSaveClick();
            return true;
        }
        return false;
    }

    // ===========================================================
    // Load
    // ===========================================================

    private void loadEntity(Bundle args) {
        if (args != null && args.containsKey(Intent.EXTRA_UID)) {
            Long entityId = args.getLong(Intent.EXTRA_UID);
            Log.d(TAG, "Edit Entity Id : " + entityId);
            entity = productDao.load(entityId);
            bindView(entity);
        } else {
            Log.d(TAG, "Prepare new Entity" );
            // prepare for insert
            prepareInsert();
        }
    }

    private void prepareInsert() {
          
    }

    private void bindView(Product entity) {
        
    }
    
    // ===========================================================
    // Action
    // ===========================================================

    
    public void onDeleteClick() {
        
    }
    
    public void onSaveClick() {
        
    }
    
    public void onCancelClick() {
        
    }
    
    // ===========================================================
    // Business
    // ===========================================================
 
}

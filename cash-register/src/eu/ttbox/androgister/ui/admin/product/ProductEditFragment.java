package eu.ttbox.androgister.ui.admin.product;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.Toast;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.DaoSession;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.ProductDao.Properties;
import eu.ttbox.androgister.model.PriceHelper;
import eu.ttbox.androgister.ui.core.validator.Form;
import eu.ttbox.androgister.ui.core.validator.validate.ValidateTextView;
import eu.ttbox.androgister.ui.core.validator.validator.NotEmptyValidator;

public class ProductEditFragment extends Fragment {

    private static final String TAG = "ProductEditFragment";
    // Service
    private DaoSession daoSession;
    private ProductDao productDao;

    // Binding
    private EditText nameText;
    private EditText descText;
    private EditText priceHTText;
    private EditText tagText;

    // Instance
    private Form mForm;
    private Product entity;

    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView BEGIN");
        View v = inflater.inflate(R.layout.product_edit, container, false);
        // Binding
        nameText = (EditText) v.findViewById(R.id.product_name);
        descText = (EditText) v.findViewById(R.id.product_description);
        priceHTText = (EditText) v.findViewById(R.id.product_price_ht);
        tagText = (EditText) v.findViewById(R.id.product_tag);

        // Menu on Fragment
        setHasOptionsMenu(true);
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated BEGIN");
        super.onActivityCreated(savedInstanceState);
 
        // Service
        AndroGisterApplication app = (AndroGisterApplication) getActivity().getApplication();
        daoSession = app.getDaoSession();
        productDao = daoSession.getProductDao();
        // Form
        createValidator(getActivity());
        
        // Load Data
        // Bundle args = getArguments();
        // loadEntity(args);
    }


    // ===========================================================
    // Menu
    // ===========================================================

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.admin_edit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_admin_edit_save:
            onSaveClick();
            return true;
        case R.id.menu_admin_edit_cancel:
            onCancelClick();
            return true;
        case R.id.menu_admin_edit_delete:
            onDeleteClick();
            return true;
        }
        return false;
    }

    // ===========================================================
    // Load
    // ===========================================================

    public void handleIntent(Intent intent) {
        loadEntity(intent.getExtras());
    }

    private void loadEntity(Bundle args) {
        if (args != null && args.containsKey(Intent.EXTRA_UID)) {
            Long entityId = args.getLong(Intent.EXTRA_UID);
            Log.d(TAG, "Edit Entity Id : " + entityId);
            entity = productDao.load(entityId);
            bindView(entity);
        } else {
            Log.d(TAG, "Prepare new Entity");
            // prepare for insert
            prepareInsert();
        }
    }

    private void prepareInsert() {

    }

    private void bindView(Product entity) {
        nameText.setText(entity.getName());
        descText.setText(entity.getDescription());
        String priceString = PriceHelper.getToStringPrice(entity.getPriceHT());
        priceHTText.setText(priceString);
        tagText.setText(entity.getTag());
    }

    // ===========================================================
    // Validator
    // ===========================================================

    private void createValidator(Context context) {
        mForm = new Form();
        Log.d(TAG, " Validate nameTextField : " + nameText);
        ValidateTextView nameTextField = new ValidateTextView(nameText);
        nameTextField.addValidator(new NotEmptyValidator(context));
        mForm.addValidates(nameTextField);
    }

    // ===========================================================
    // Action
    // ===========================================================

    public void onDeleteClick() {
        productDao.delete(entity);
        getActivity().finish();
    }

    public void onSaveClick() {
        if (mForm.validate()) {
            Toast.makeText(getActivity(), "Valid Form", Toast.LENGTH_LONG).show();

            entity.setName(nameText.getText().toString());
            entity.setDescription(descText.getText().toString());
            entity.setTag(tagText.getText().toString());
            String priceString = priceHTText.getText().toString();
            // save
            productDao.update(entity);
            getActivity().finish();
        }
    }

    public void onCancelClick() {
        getActivity().finish();
    }

    // ===========================================================
    // Business
    // ===========================================================

}

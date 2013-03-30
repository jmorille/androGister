package eu.ttbox.androgister.ui.admin.catalog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.Catalog;
import eu.ttbox.androgister.domain.CatalogProduct;
import eu.ttbox.androgister.domain.CatalogProductDao;
import eu.ttbox.androgister.domain.CatalogProductDao.Properties;
import eu.ttbox.androgister.ui.core.crud.CrudHelper;
import eu.ttbox.androgister.ui.core.crud.EntityEditFragment;
import eu.ttbox.androgister.ui.core.validator.Form;
import eu.ttbox.androgister.ui.core.validator.validate.ValidateTextView;
import eu.ttbox.androgister.ui.core.validator.validator.NotEmptyValidator;

public class CatalogEditFragment  extends EntityEditFragment<Catalog> {

    private static final String TAG = "CatalogEditFragment";

    private EditText nameText;
    private  CompoundButton enabledSwitch;
    
    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView BEGIN");
        View v = inflater.inflate(R.layout.admin_catalog_edit, container, false);
        // Binding
        nameText = (EditText) v.findViewById(R.id.catalog_name); 
        enabledSwitch = (CompoundButton)v.findViewById(R.id.catalog_enabled_switch); 
        
        // Menu on Fragment
        setHasOptionsMenu(true);
        return v;
    }
    
    
    @Override
    public AbstractDao<Catalog, Long> getEntityDao() {
        return getDaoSession().getCatalogDao();
    }


    // ===========================================================
    // Validator
    // ===========================================================

    public Form createValidator(Context context) {
        Form formValidator = new Form();
        Log.d(TAG, " Validate nameTextField : " + nameText);
        // Name
        ValidateTextView nameTextField = new ValidateTextView(nameText)//
                .addValidator(new NotEmptyValidator());
        formValidator.addValidates(nameTextField); 

        return formValidator;
    }


    // ===========================================================
    // Bindings
    // ===========================================================

    
    @Override
    public void bindView(Catalog entity) {
        nameText.setText(   entity.getName());
        enabledSwitch.setChecked(entity.getEnabled()); 
    }

    @Override
    public Catalog bindValue(Catalog entity) {
        entity.setName(  CrudHelper.getStringTrimmed(nameText) );
        // switch
        boolean isChecked = enabledSwitch.isChecked();
        entity.setEnabled(isChecked);
        return entity;
    }

    @Override
    public Catalog prepareInsert(Bundle args) {
        Catalog entity =  new Catalog();
        enabledSwitch.setChecked(true);
        return entity;
    }
    // ===========================================================
    // Action
    // ===========================================================

    public CatalogProductDao getCatalogProductDao() {
        return getDaoSession().getCatalogProductDao();
    }
    
    @Override
    public void onDeleteClick() {
        //   validate Deps
        long productCount = 0;
        if (entity != null && entity.getId() != null) {
            QueryBuilder<CatalogProduct> queryCount = getCatalogProductDao().queryBuilder();
            queryCount.where(Properties.CatalogId.eq(entity.getId()));
            productCount = queryCount.count();
        }
        if (productCount < 1) {
            super.onDeleteClick();
        } else {
            String text = getResources().getQuantityString(R.plurals.product_delete_constraints, (int)productCount,  productCount);            
            Log.w(TAG, "Could not delete entity for " + productCount + " Products");
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }
    }
}

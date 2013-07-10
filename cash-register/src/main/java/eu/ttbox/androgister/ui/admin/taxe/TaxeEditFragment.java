package eu.ttbox.androgister.ui.admin.taxe;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.ProductDao; 
import eu.ttbox.androgister.domain.Taxe;
import eu.ttbox.androgister.ui.core.crud.CrudHelper;
import eu.ttbox.androgister.ui.core.crud.EntityEditFragment;
import eu.ttbox.androgister.ui.core.validator.Form;
import eu.ttbox.androgister.ui.core.validator.validate.GroupFieldsRequiredValidate;
import eu.ttbox.androgister.ui.core.validator.validate.ValidateTextView;
import eu.ttbox.androgister.ui.core.validator.validator.NotEmptyValidator;

public class TaxeEditFragment extends EntityEditFragment<Taxe> {

    private static final String TAG = "TaxeEditFragment";

    private EditText titleText;

    private EditText nameText;
    private EditText percentText;

    private EditText alternateNameText;
    private EditText alternatePercentText;

    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView BEGIN");
        View v = inflater.inflate(R.layout.admin_taxe_edit, container, false);
        // Binding
        titleText = (EditText) v.findViewById(R.id.taxe_title);
        nameText = (EditText) v.findViewById(R.id.taxe_name);
        percentText = (EditText) v.findViewById(R.id.taxe_percent);
        // Alternate Taxe
        alternateNameText = (EditText) v.findViewById(R.id.taxe_alternate_name);
        alternatePercentText = (EditText) v.findViewById(R.id.taxe_alternate_percent);

        // Menu on Fragment
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public AbstractDao<Taxe, Long> getEntityDao() {
        return getDaoSession().getTaxeDao();
    }

    // ===========================================================
    // Validator
    // ===========================================================

    public Form createValidator(Context context) {
        Form formValidator = new Form();
        Log.d(TAG, " Validate nameTextField : " + nameText);
        formValidator.addValidates(new ValidateTextView(titleText)//
                .addValidator(new NotEmptyValidator()));
        // Taxe Primary
        formValidator.addValidates(new ValidateTextView(nameText)//
                .addValidator(new NotEmptyValidator())); 
        formValidator.addValidates(new ValidateTextView(percentText)//
                .addValidator(new NotEmptyValidator()));

        // Taxe alternate
        formValidator.addValidates(new GroupFieldsRequiredValidate(alternateNameText, alternatePercentText));
        return formValidator;
    }

    // ===========================================================
    // Bindings
    // ===========================================================

    @Override
    public void bindView(Taxe entity) {
        titleText.setText(entity.getTitle());
        // Taxe
        nameText.setText(entity.getTaxeName());
        percentText.setText(convertTaxeAsString(entity.getTaxePercent()));
        // Alternate Taxe
        alternateNameText.setText(entity.getAlternateName());
        alternatePercentText.setText(convertTaxeAsString(entity.getAlternateTaxePercent()));
    }

    private String convertTaxeAsString(Float taxe) {
        if (taxe == null) {
            return null;
        }
        return convertTaxeAsString(taxe.floatValue());
    }

    private String convertTaxeAsString(float taxe) {
        return String.valueOf(taxe);
    }

    @Override
    public Taxe bindValue(Taxe entity) {
        entity.setTitle(CrudHelper.getStringTrimmed(titleText));
        // Taxe
        entity.setTaxeName(CrudHelper.getStringTrimmed(nameText));
        entity.setTaxePercent(CrudHelper.getFloat(percentText).floatValue());
        // Taxe Alternate
        entity.setAlternateName(CrudHelper.getStringTrimmed(alternateNameText));
        entity.setAlternateTaxePercent(CrudHelper.getFloat(alternatePercentText));

        return entity;
    }

    @Override
    public Taxe prepareInsert(Bundle args) {
        return new Taxe();
    }

    // ===========================================================
    // Action
    // ===========================================================

    public ProductDao getProductDao() {
        return getDaoSession().getProductDao();
    }

    @Override
    public void onDeleteClick() {
        // Validate Deps
        long productCount = 0;
        if (entity != null && entity.getId() != null) {
            QueryBuilder<Product> queryCount = getProductDao().queryBuilder();
            queryCount.where(ProductDao.Properties.TaxeId.eq(entity.getId()));
            productCount = queryCount.count();
        }
        if (productCount < 1) {
            super.onDeleteClick();
        } else {
            String text = getResources().getQuantityString(R.plurals.product_delete_constraints, (int) productCount, productCount);
            Log.w(TAG, "Could not delete entity for " + productCount + " Products");
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }
    }
}

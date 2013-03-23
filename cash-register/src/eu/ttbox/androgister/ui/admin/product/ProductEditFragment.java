package eu.ttbox.androgister.ui.admin.product;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.TagDao;
import eu.ttbox.androgister.ui.core.crud.EntityEditFragment;
import eu.ttbox.androgister.ui.core.validator.Form;
import eu.ttbox.androgister.ui.core.validator.validate.ValidateTextView;
import eu.ttbox.androgister.ui.core.validator.validator.NotEmptyValidator;
import eu.ttbox.androgister.ui.core.validator.validator.NumberValidator;

public class ProductEditFragment extends EntityEditFragment<Product> {

    private static final String TAG = "ProductEditFragment";

    // service
    private TagDao tagDao;

    // Binding
    private EditText nameText;
    private EditText descText;
    private EditText priceHTText;
    private EditText tagText;

    private Spinner tagSpinner;

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
        tagSpinner = (Spinner) v.findViewById(R.id.product_tag_spinner);

        // Load Spinner
        tagDao = getDaoSession().getTagDao();

        // Menu on Fragment
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public ProductDao getEntityDao() {
        return getDaoSession().getProductDao();
    }

    @Override
    public Product prepareInsert() {
        return new Product();
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
        // Price
        ValidateTextView priceTextField = new ValidateTextView(priceHTText)//
                .addValidator(new NotEmptyValidator()) //
                .addValidator(new NumberValidator());
        formValidator.addValidates(nameTextField);
        formValidator.addValidates(priceTextField);

        return formValidator;
    }

    // ===========================================================
    // Bindings
    // ===========================================================

    @Override
    public void bindView(Product entity) {
        nameText.setText(entity.getName());
        descText.setText(entity.getDescription());
        // String priceString =
        // PriceHelper.getToStringPrice(entity.getPriceHT());
        String priceString = entity.getPriceHT() != null ? entity.getPriceHT().toString() : null;
        priceHTText.setText(priceString);
        // tagText.setText(entity.getTag());
    }

    @Override
    public Product bindValue(Product entity) {
        entity.setName(nameText.getText().toString());
        entity.setDescription(descText.getText().toString());
        // entity.setTag(tagText.getText().toString());
        String priceString = priceHTText.getText().toString();
        Long priceHt = Long.parseLong(priceString);
        entity.setPriceHT(priceHt);
        return entity;
    }

}

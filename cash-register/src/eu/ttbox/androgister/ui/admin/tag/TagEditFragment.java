package eu.ttbox.androgister.ui.admin.tag;

import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import de.greenrobot.dao.AbstractDao;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.Tag;
import eu.ttbox.androgister.ui.admin.tag.holocolorpicker.ColorPicker.OnColorChangedListener;
import eu.ttbox.androgister.ui.admin.tag.holocolorpicker.HoloColorPickerDialog;
import eu.ttbox.androgister.ui.core.crud.EntityEditFragment;
import eu.ttbox.androgister.ui.core.validator.Form;
import eu.ttbox.androgister.ui.core.validator.validate.ValidateTextView;
import eu.ttbox.androgister.ui.core.validator.validator.NotEmptyValidator;

public class TagEditFragment  extends EntityEditFragment<Tag> implements OnColorChangedListener {

    private static final String TAG = "TagEditFragment";

    private static final int PICK_COLOR = 325;

    private EditText nameText;
    
    // Instance 
    private   Paint colorPaint = new Paint();
    private Button colorPickerButton;

    
    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView BEGIN");
        View v = inflater.inflate(R.layout.admin_tag_edit, container, false);
        // Binding
        nameText = (EditText) v.findViewById(R.id.tag_name); 
        // Color
        colorPickerButton = (Button) v.findViewById(R.id.tag_color_picker_button);
        colorPickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onColorPickerClick(v);
            }
        });
        
        // Menu on Fragment
        setHasOptionsMenu(true);
        return v;
    }
    
    
    @Override
    public AbstractDao<Tag, Long> getEntityDao() {
        return getDaoSession().getTagDao();
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
    public void bindView(Tag entity) {
        nameText.setText(entity.getName());
        colorPaint.setColor(entity.getColor());
    }

    @Override
    public Tag bindValue(Tag entity) {
        entity.setName(nameText.getText().toString());
        return entity;
    }

    @Override
    public Tag prepareInsert(Bundle args) {
        doColorChangeRamdom() ;
        return new Tag();
    }
    
    // ===========================================================
    // Color Picker
    // ===========================================================

    public void onColorPickerClick(View v) {
        HoloColorPickerDialog colorPicker = HoloColorPickerDialog.newInstance(colorPaint.getColor());
        colorPicker.setTargetFragment(this, PICK_COLOR);
        colorPicker.show(getFragmentManager(), "colorPickerDialog");
    }
    
    private void doColorChangeRamdom() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        int ramdomColor = Color.rgb(r, g, b);
        onColorChanged(ramdomColor);
    }
    
    @Override
    public void onColorChanged(int color) {
        Log.d(TAG, "Choose Color : " + color);
        colorPaint.setColor(color);
        colorPickerButton.setBackgroundColor(color);
    }

}

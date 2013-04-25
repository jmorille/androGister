package eu.ttbox.androgister.ui.admin.product;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import de.greenrobot.dao.query.LazyList;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.Product;
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.ProductDao.Properties;
import eu.ttbox.androgister.domain.Tag;
import eu.ttbox.androgister.domain.TagDao;
import eu.ttbox.androgister.domain.Taxe;
import eu.ttbox.androgister.domain.TaxeDao;
import eu.ttbox.androgister.ui.admin.product.photo.PhotoActionPopup;
import eu.ttbox.androgister.ui.admin.product.photo.PhotoSelectionHandler;
import eu.ttbox.androgister.ui.admin.tag.TagListAdapter;
import eu.ttbox.androgister.ui.admin.taxe.TaxeListAdapter;
import eu.ttbox.androgister.ui.core.crud.CrudHelper;
import eu.ttbox.androgister.ui.core.crud.EntityEditFragment;
import eu.ttbox.androgister.ui.core.validator.Form;
import eu.ttbox.androgister.ui.core.validator.validate.ValidateTextView;
import eu.ttbox.androgister.ui.core.validator.validator.NotEmptyValidator;
import eu.ttbox.androgister.ui.core.validator.validator.NumberValidator;

public class ProductEditFragment extends EntityEditFragment<Product> {

    private static final String TAG = "ProductEditFragment";

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;

    // Photo
    private Bitmap bitmap;
    private ImageView imageView;
    private PhotoHandler mPhotoHandler;

    // Binding
    private EditText nameText;
    private EditText descText;
    private EditText priceHTText;
    private EditText tagText;

    private Spinner tagSpinner;
    private Spinner taxeSpinner;

    // Instance
    TaxeListAdapter taxeListAdapter;
    TagListAdapter tagListAdapter;

    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView BEGIN");
        View v = inflater.inflate(R.layout.admin_product_edit, container, false);
        // Binding
        nameText = (EditText) v.findViewById(R.id.product_name);
        descText = (EditText) v.findViewById(R.id.product_description);
        priceHTText = (EditText) v.findViewById(R.id.product_price_ht);
        tagText = (EditText) v.findViewById(R.id.product_tag);
        tagSpinner = (Spinner) v.findViewById(R.id.product_tag_spinner);
        taxeSpinner = (Spinner) v.findViewById(R.id.product_taxe_spinner);

        // Photo
        imageView = (ImageView) v.findViewById(R.id.product_photo);
        // int mode = (mPhotoUri == null) ? PhotoActionPopup.Modes.NO_PHOTO :
        // PhotoActionPopup.Modes.PHOTO_DISALLOW_PRIMARY;
        mPhotoHandler = new PhotoHandler(getActivity(), imageView, PhotoActionPopup.Modes.NO_PHOTO);
        imageView.setOnClickListener(mPhotoHandler);

        // Listener
        // tagSpinner.setOnItemSelectedListener(tagOnItemSelectedListener);

        // Load Taxe Spinner
        taxeListAdapter = new TaxeListAdapter(getActivity(), null);
        taxeSpinner.setAdapter(taxeListAdapter);

        // Load Tag Spinner
        tagListAdapter = new TagListAdapter(getActivity(), null);
        tagSpinner.setAdapter(tagListAdapter);

        // Load
        loadTagSpinner();
        loadTaxeSpinner();

        // Menu on Fragment
        setHasOptionsMenu(true);
        return v;
    }

    private void loadTaxeSpinner() {
        TaxeDao taxeDao = getDaoSession().getTaxeDao();
        LazyList<Taxe> taxes = taxeDao.queryBuilder()//
                .orderAsc(TaxeDao.Properties.Title) //
                .listLazy();
        taxeListAdapter.changeCursor(taxes);
    }

    private void loadTagSpinner() {
        TagDao tagDao = getDaoSession().getTagDao();
        LazyList<Tag> tags = tagDao.queryBuilder()//
                .orderAsc(TagDao.Properties.Name) //
                .listLazy();
        tagListAdapter.changeCursor(tags);
    }

    @Override
    public void onDestroyView() {
        if (tagListAdapter != null) {
            // Close LazyListAdpater for closing LazyList
            tagListAdapter.close();
            tagListAdapter = null;
            // Taxe
            taxeListAdapter.close();
            taxeListAdapter = null;
        }
        super.onDestroyView();
    }

    @Override
    public ProductDao getEntityDao() {
        return getDaoSession().getProductDao();
    }

    @Override
    public Product prepareInsert(Bundle args) {
        Log.d(TAG, "prepareInsert with args : " + (args != null));
        if (args != null) {
            Long tagId = args.getLong(Properties.TagId.columnName);
            Log.d(TAG, "prepareInsert with tagId : " + tagId);
            if (tagId != null) {
                selectSpinnerToTagId(tagId);
            }
        }
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
        formValidator.addValidates(nameTextField);

        // Price
        ValidateTextView priceTextField = new ValidateTextView(priceHTText)//
                .addValidator(new NotEmptyValidator()) //
                .addValidator(new NumberValidator());
        formValidator.addValidates(priceTextField);

        // Tag
        // ValidateTextView tagTextField = new ValidateTextView(tagSpinner)//
        // .addValidator(new NotEmptyValidator());

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
        String priceString = String.valueOf(entity.getPriceHT());
        priceHTText.setText(priceString);

        // Tag
        selectSpinnerToTagId(entity.getTagId());
        selectSpinnerToTaxeId(entity.getTaxeId());
    }

    private void selectSpinnerToTagId(Long wantedTagId) {
        if (wantedTagId != null) {
            long tagId = wantedTagId.longValue();
            for (int i = 0; i < tagListAdapter.getCount(); i++) {
                if (tagListAdapter.getItemId(i) == tagId) {
                    this.tagSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void selectSpinnerToTaxeId(Long wantedTaxeId) {
        if (wantedTaxeId != null) {
            long tagId = wantedTaxeId.longValue();
            for (int i = 0; i < taxeListAdapter.getCount(); i++) {
                if (taxeListAdapter.getItemId(i) == tagId) {
                    this.taxeSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public Product bindValue(Product entity) {
        entity.setName(CrudHelper.getStringTrimmed(nameText));
        entity.setDescription(CrudHelper.getStringTrimmed(descText));
        // entity.setTag(tagText.getText().toString());
        String priceString = CrudHelper.getStringTrimmed(priceHTText);
        Long priceHt = Long.parseLong(priceString);
        entity.setPriceHT(priceHt);
        //
        Long selectedTagId = Long.valueOf(tagSpinner.getSelectedItemId());
        entity.setTagId(selectedTagId);

        return entity;
    }

    private OnItemSelectedListener tagOnItemSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub

        }

    };

    // ===========================================================
    // Photo Picker
    // ===========================================================
    // http://www.vogella.com/articles/AndroidCamera/article.html
    // http://developer.android.com/training/camera/photobasics.html
    // ===========================================================

    public void pickImage(View View) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //
        // Intent intent = new Intent();
        // intent.setType("image/*");
        // intent.setAction(Intent.ACTION_GET_CONTENT);
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
    }

    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        setProductPhoto((Bitmap) extras.get("data"));
    }

    private void setProductPhoto(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPhotoHandler != null) {
            mPhotoHandler.handlePhotoActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String mCurrentPhotoFile;

    private final class PhotoHandler extends PhotoSelectionHandler {
        private final PhotoActionListener mListener;

        private PhotoHandler(Context context, View photoView, int photoMode) {
            super(context, photoView, photoMode, false);
            mListener = new PhotoListener();
        }

        @Override
        public PhotoActionListener getListener() {
            return mListener;
        }

        @Override
        public void startPhotoActivity(Intent intent, int requestCode, String photoFile) {
            Log.d(TAG, "startPhotoActivity for photoFile : " + photoFile);
            // mSubActivityInProgress = true;
            mCurrentPhotoFile = photoFile;
            startActivityForResult(intent, requestCode);
        }

        private final class PhotoListener extends PhotoActionListener {
            @Override
            public void onPhotoSelected(Bitmap bitmap) {
                Log.d(TAG, "onPhotoSelected Bitmap : " + bitmap);
                setProductPhoto(bitmap);

                // EntityDeltaList delta = getDeltaForAttachingPhotoToContact();
                // long rawContactId = getWritableEntityId();
                // final String croppedPath =
                // ContactPhotoUtils.pathForCroppedPhoto(
                // PhotoSelectionActivity.this, mCurrentPhotoFile);
                // Intent intent = ContactSaveService.createSaveContactIntent(
                // mContext, delta, "", 0, mIsProfile, null, null, rawContactId,
                // croppedPath);
                // startService(intent);
                // finish();
            }

            @Override
            public String getCurrentPhotoFile() {
                Log.d(TAG, "getCurrentPhotoFile : " + mCurrentPhotoFile); 
                return mCurrentPhotoFile;
            }

            @Override
            public void onPhotoSelectionDismissed() {
                // if (!mSubActivityInProgress) {
                // finish();
                // }
            }
        }
    }

}

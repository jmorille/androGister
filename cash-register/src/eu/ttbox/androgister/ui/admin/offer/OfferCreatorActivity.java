package eu.ttbox.androgister.ui.admin.offer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.ui.admin.product.ProductListFragment;

public class OfferCreatorActivity extends Activity {

    private static final String TAG = "OfferCreatorActivity";

    private static final int PRODUCT_EDIT_REQUEST_CODE = 0;

    private ProductListFragment productListFragment;

    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_offer_creator_activity);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof ProductListFragment) {
            productListFragment = (ProductListFragment) fragment;
            Log.d(TAG, "onAttachFragment ProductListFragment");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "onActivityResult :  requestCode = " + requestCode + "  ==> resultCode = " + resultCode);
//        if (requestCode == PRODUCT_EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            // productListFragment.onActivityResult(requestCode, resultCode,
//            // data);
//            Toast.makeText(this, "Success Edit", Toast.LENGTH_LONG).show();
//        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

package eu.ttbox.androgister.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.CoreHelper;
import eu.ttbox.androgister.model.Offer;
import eu.ttbox.androgister.ui.product.ProductSelectorFragment;
import eu.ttbox.androgister.ui.register.RegisterMultiBasketFragment;

/**
 * Sample Fragment {@link http
 * ://sberfini.developpez.com/tutoriaux/android/fragments/}
 * 
 * @author jmorille
 * 
 */
public class CashRegisterActivity extends Activity {

    private static final String TAG = "CashRegisterActivity";

    // Data
    private RegisterMultiBasketFragment basketFragment;

    // Listener
    private ProductSelectorFragment.OnOfferSelectedListener onOfferSelectedListener = new ProductSelectorFragment.OnOfferSelectedListener() {

        @Override
        public void onOfferSelected(Offer offer) {
            basketFragment.onAddBasketItem(offer);
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_register);
        // displayDensity();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof ProductSelectorFragment) {
            ProductSelectorFragment pfrag = (ProductSelectorFragment) fragment;
            pfrag.setOnOfferSelectedListener(onOfferSelectedListener);
        } else if (fragment instanceof RegisterMultiBasketFragment) {
            basketFragment = (RegisterMultiBasketFragment)fragment;
        }
    }

    private void displayDensity() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.i(TAG, dm.toString());
        int[] fragWidth = CoreHelper.getTwoFragmentOr(dm.widthPixels - 120);
        int[] fragHeight = CoreHelper.getTwoFragmentOr(dm.heightPixels);

    }

}

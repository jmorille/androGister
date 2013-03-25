package eu.ttbox.androgister.ui.admin.offer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.ui.admin.catalog.CatalogListFragment;
import eu.ttbox.androgister.ui.admin.catalog.CatalogListFragment.OnSelectCatalogListener;
import eu.ttbox.androgister.ui.admin.product.ProductListFragment;
import eu.ttbox.androgister.ui.admin.product.ProductListFragment.OnSelectProductListener;
import eu.ttbox.androgister.ui.admin.tag.TagListFragment;
import eu.ttbox.androgister.ui.admin.tag.TagListFragment.OnSelectTagListener;

public class OfferCreatorActivity extends Activity {

    private static final String TAG = "OfferCreatorActivity";

    private static final int PRODUCT_EDIT_REQUEST_CODE = 0;

    private ProductListFragment productListFragment;

    private TagListFragment tagListFragment;

    private CatalogListFragment catalogListFragment;

    private CatalogProductListFragment catalogProductListFragment;

    // Instance
    private OfferCreateListener offerCreateListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_offer_creator_activity);
        // Listener
        offerCreateListener = new OfferCreateListener();
        productListFragment.setOnSelectProductListener(offerCreateListener);
        tagListFragment.setOnSelectTagListener(offerCreateListener);
        catalogListFragment.setOnSelectCatalogListener(offerCreateListener);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.d(TAG, "onAttachFragment : " + fragment.getClass().getSimpleName());

        if (fragment instanceof CatalogProductListFragment) {
            catalogProductListFragment = (CatalogProductListFragment) fragment;

        } else if (fragment instanceof ProductListFragment) {
            productListFragment = (ProductListFragment) fragment;

        } else if (fragment instanceof TagListFragment) {
            tagListFragment = (TagListFragment) fragment;

        } else if (fragment instanceof CatalogListFragment) {
            catalogListFragment = (CatalogListFragment) fragment;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "onActivityResult :  requestCode = " + requestCode + "  ==> resultCode = " + resultCode);
        // if (requestCode == PRODUCT_EDIT_REQUEST_CODE && resultCode ==
        // Activity.RESULT_OK) {
        // // productListFragment.onActivityResult(requestCode, resultCode,
        // // data);
        // Toast.makeText(this, "Success Edit", Toast.LENGTH_LONG).show();
        // }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    private class OfferCreateListener implements OnSelectCatalogListener, OnSelectTagListener, OnSelectProductListener {

        private Long tagId;
        private Long catalogId;

        @Override
        public void onSelectTagId(Long tagId) {
            this.tagId = tagId;
            Log.d(TAG, "onSelectTagId : " + tagId);
            if (productListFragment != null) {
                productListFragment.onSelectTagId(tagId);
            }
        }

        @Override
        public void onSelectCalalogId(Long catalogId) {
            this.catalogId = catalogId;
            if (catalogProductListFragment != null) {
                catalogProductListFragment.onSelectCalalogId(catalogId);
            }
        }

        @Override
        public void onSelectProductId(Long... productIds) {
            if (catalogListFragment != null && productIds != null) {
                catalogListFragment.onSelectProductId(productIds);
            }

        }

    }

}

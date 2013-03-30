package eu.ttbox.androgister.ui.admin.offer;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import eu.ttbox.androgister.AndroGisterActivity;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.ui.admin.catalog.CatalogListFragment;
import eu.ttbox.androgister.ui.admin.catalog.CatalogListFragment.OnSelectCatalogListener;
import eu.ttbox.androgister.ui.admin.product.ProductListFragment;
import eu.ttbox.androgister.ui.admin.product.ProductListFragment.OnSelectProductListener;
import eu.ttbox.androgister.ui.admin.tag.TagListFragment;
import eu.ttbox.androgister.ui.admin.tag.TagListFragment.OnSelectTagListener;

public class OfferCreatorActivity extends AndroGisterActivity {

    private static final String TAG = "OfferCreatorActivity";

   
    // Fragment
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
            if (catalogProductListFragment != null  ) {
//                catalogsProductListFragment.onSelectTagId(tagId);
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
            if (catalogProductListFragment != null && productIds != null) {
                catalogProductListFragment.onSelectProductId(productIds);
            }

        }

    }

}

package eu.ttbox.androgister.ui;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import eu.ttbox.androgister.AndroGisterActivity;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.CoreHelper;
import eu.ttbox.androgister.domain.OrderItem;
import eu.ttbox.androgister.domain.dao.helper.OrderItemHelper;
import eu.ttbox.androgister.ui.admin.user.UserAdminActivity;
import eu.ttbox.androgister.ui.config.MyPreferencesActivity;
import eu.ttbox.androgister.ui.product.ProductSelectorFragment;
import eu.ttbox.androgister.ui.register.RegisterMultiBasketFragment;

/**
 * Sample Fragment {@link http
 * ://sberfini.developpez.com/tutoriaux/android/fragments/}
 * 
 * @author jmorille
 * 
 */
public class CashRegisterActivity extends AndroGisterActivity {

	private static final String TAG = "CashRegisterActivity";

	// Data
	private RegisterMultiBasketFragment basketFragment;

	// Listener
	private ProductSelectorFragment.OnOfferSelectedListener onOfferSelectedListener = new ProductSelectorFragment.OnOfferSelectedListener() {

		@Override
		public void onOfferSelected(Bundle offer) {
		    OrderItem item = OrderItemHelper.createFromOffer(offer);
			basketFragment.onAddBasketItem( item);
		}
 	};

// 	@Override
// 	public void onSaveInstanceState(Bundle outState){
// 	    super.onSaveInstanceState(outState);
// 	    getFragmentManager().putFragment(outState,"basketFragment",basketFragment);
// 	}
//    @Override
// 	public void onRestoreInstanceState(Bundle inState){
//        super.onRestoreInstanceState(inState);
// 	   basketFragment =(RegisterMultiBasketFragment) getFragmentManager().getFragment(inState,"basketFragment");
// 	}
 	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "------------- Activity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cash_register);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		 displayDensity();
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
        Log.d(TAG, "------------- Activity onAttachFragment");
		super.onAttachFragment(fragment);
		if (fragment instanceof ProductSelectorFragment) {
			ProductSelectorFragment pfrag = (ProductSelectorFragment) fragment;
			pfrag.setOnOfferSelectedListener(onOfferSelectedListener);
		} else if (fragment instanceof RegisterMultiBasketFragment) {
			basketFragment = (RegisterMultiBasketFragment) fragment;
		}
	}

	private void displayDensity() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Log.i(TAG, dm.toString());
		int[] fragWidth = CoreHelper.getTwoFragmentOr(dm.widthPixels  );
		int[] fragHeight = CoreHelper.getTwoFragmentOr(dm.heightPixels);
	}

	// ### Action Bar Menu
	// ######################## //

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_preference:{
			// app icon in action bar clicked; go home
			Intent intent = new Intent(this, MyPreferencesActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
	    case R.id.menu_admin_user: {
            // app icon in action bar clicked; go home
            Intent intent  = new Intent(this, UserAdminActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;		
	    }
		default:
			
		}
		return super.onOptionsItemSelected(item);
	}

	// ### Other
	// ######################## //

}

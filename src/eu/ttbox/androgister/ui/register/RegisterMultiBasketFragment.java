package eu.ttbox.androgister.ui.register;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.model.Offer;
import eu.ttbox.androgister.model.PriceHelper;
import eu.ttbox.androgister.service.OrderService;
import eu.ttbox.androgister.ui.register.RegisterBasketFragment.OnBasketSunUpdateListener;

public class RegisterMultiBasketFragment extends Fragment {

	private static final String TAG = "RegisterMultiBasketFragment";

	// Listener
	private BroadcastReceiver mStatusReceiver; 

	// View
	private LinearLayout viewTabs;
	private Button addTabButton;

	// config
	private int MAX_KEY = 3;

	// Data
	private int mCurrentTab = -1;

	private RegisterBasketFragment currentBasket;

	SparseArray<RegisterBasketFragment> cacheBasket = new SparseArray<RegisterBasketFragment>();
	SparseArray<Button> cacheButton = new SparseArray<Button>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Services 
		mStatusReceiver = new StatusReceiver(); 
	}

	@Override
	public void onDestroy() { 
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.register_multi_basket, null);
		viewTabs = (LinearLayout) view.findViewById(android.R.id.tabs);
		addTabButton = (Button) view.findViewById(R.id.button_add_tab);
		addTabButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addNewTab();
			}
		});
		// add Nav
		// for (int i = 0; i < 3; i++) {
		// addNewTab(i, false);
		// }
		updateTab(0);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Register Listener
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intents.ACTION_ADD_BASKET);
		filter.addAction(Intents.ACTION_SAVE_BASKET);
		// Listener
		getActivity().registerReceiver(mStatusReceiver, filter);
	}

	@Override
	public void onPause() {
		// Listener
		getActivity().unregisterReceiver(mStatusReceiver);

		super.onPause();
	}

	private void addNewTab() {
		for (int i = 0; i < MAX_KEY; i++) {
			Button btn = cacheButton.get(i);
			if (btn == null) {
				updateTab(i);
 				return;
			}
		}
	}

	private Button addNewTab(final int tabId, boolean checkexiting) {
		Button btn = null;
		// Check Existing button
		if (checkexiting) {
			btn = cacheButton.get(tabId);
			if (btn != null) {
				return btn;
			}
		}
		// Create Button
		btn = new Button(getActivity());
		btn.setText("Bouton " + tabId);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateTab(tabId);
			}
		});
		btn.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				removeTab(tabId);
				return true;
			}
		});
		btn.setBackgroundResource(R.drawable.tab_selector);
		cacheButton.put(tabId, btn);
		viewTabs.addView(btn);
		return btn;
	}

	private void removeTab(int tabId) {
		Log.i(TAG, "Remove for basket Size " + cacheButton.size());
		if (cacheButton.size() > 1) {
			// Delete the tabs
			cacheBasket.delete(tabId);
			Button btn = cacheButton.get(tabId);
			if (btn != null) {
				cacheButton.delete(tabId);
				viewTabs.removeView(btn);
			}
			// Reaffecte an another tab
			// NEED TO BE DELETE BEFORE TO FIND NEW
			if (mCurrentTab == tabId) {
				int newTab = cacheButton.keyAt(0);
				Log.i(TAG, String.format(
						"After remove Tab %s need to set new Tab as %s",
						mCurrentTab, newTab));
				updateTab(newTab);
			}
		} else {
			cacheBasket.clear();
		}
	}

	private void updateTab(int whichChild) {
		if (mCurrentTab != whichChild) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction fmt = fm.beginTransaction();
			RegisterBasketFragment newFrag = cacheBasket.get(whichChild);
			boolean isNeedtoCreate = newFrag == null;
			if (isNeedtoCreate) {
				newFrag = new RegisterBasketFragment();
				cacheBasket.put(whichChild, newFrag);
			}
			fmt.replace(android.R.id.tabcontent, newFrag);
			// Update Display
			Button btn = cacheButton.get(mCurrentTab);
			if (btn != null) {
				btn.setBackgroundResource(R.drawable.tab_selector);
			}
			final Button btnNew = addNewTab(whichChild, true);
			if (isNeedtoCreate) {
				newFrag.setOnBasketSunUpdateListener(new OnBasketSunUpdateListener() {

					@Override
					public void onBasketSum(long sum) {
						String sumText = PriceHelper.getToStringPrice(sum);
						btnNew.setText(String.format("Total %s", sumText));
					}
				});
			}
			btnNew.setBackgroundResource(R.drawable.tab_selected);
			// Update Current Status
			currentBasket = newFrag;
			mCurrentTab = whichChild;
			fmt.commit();
		}
	}
 
	private class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intents.ACTION_ADD_BASKET.equals(action)) {
				Offer status = (Offer) intent
						.getSerializableExtra(Intents.EXTRA_OFFER);
				currentBasket.onAddBasketItem(status);
//				context.removeStickyBroadcast(intent);
			} else if (Intents.ACTION_SAVE_BASKET.equals(action)) {
				currentBasket.askToSaveBasketToOrder();
				removeTab(mCurrentTab);
			}
		}
	}

}

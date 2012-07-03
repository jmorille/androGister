package eu.ttbox.androgister.ui.register;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.model.Offer;

public class RegisterMultiBasketFragment extends Fragment implements OnTabChangeListener {

	private static final String TAG = "FragmentTabs";
	public static final String TAB_WORDS = "words";
	public static final String TAB_NUMBERS = "numbers";

	// Listener
	private BroadcastReceiver mStatusReceiver;

	// View
	private View mRoot;
	private TabHost mTabHost;
	private int mCurrentTab;

	private RegisterBasketFragment currentBasket;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.register_multi_basket, null);
		mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
		setupTabs();

		return mRoot;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);

		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab(mCurrentTab);
		// manually start loading stuff in the first tab
		updateTab(TAB_WORDS, android.R.id.tabcontent);
		mRoot.invalidate();
	}

	private void setupTabs() {
		mTabHost.setup(); // you must call this before adding your tabs!
		mTabHost.addTab(newTab(TAB_WORDS, "Word", R.id.tab_1));
		mTabHost.addTab(newTab(TAB_NUMBERS, "Number", R.id.tab_2));
	}

	private TabSpec newTab(String tag, String labelId, int tabContentId) {
		Log.d(TAG, "buildTab(): tag=" + tag);
		ViewGroup tabWidget = (ViewGroup) mRoot.findViewById(android.R.id.tabs);
		View indicator = LayoutInflater.from(getActivity()).inflate(R.layout.register_multi_basket_tab, tabWidget, false);
		((TextView) indicator.findViewById(R.id.text)).setText(labelId);

		TabSpec tabSpec = mTabHost.newTabSpec(tag);
		tabSpec.setIndicator(indicator);
//		if (tabContentId!=-1) 
		tabSpec.setContent(tabContentId);
		return tabSpec;
	}

	@Override
	public void onTabChanged(String tabId) {
		Log.d(TAG, "onTabChanged(): tabId=" + tabId);
		if (TAB_WORDS.equals(tabId)) {
			updateTab(tabId,  R.id.tab_1);
			mCurrentTab = 0;
			return;
		}
		if (TAB_NUMBERS.equals(tabId)) {
			updateTab(tabId, R.id.tab_2);
			mCurrentTab = 1;
			return;
		}
	}

	private void updateTab(String tabId, int placeholder) {

		FragmentManager fm = getFragmentManager();
		RegisterBasketFragment newFrag = (RegisterBasketFragment) fm.findFragmentByTag(tabId);
		if (newFrag == null) {
			FragmentTransaction fmt = fm.beginTransaction();
			newFrag = new RegisterBasketFragment();
			// V replace
			fmt.replace(placeholder,newFrag, tabId) ;
			 // V add
//			fmt.add(android.R.id.tabcontent, newFrag, tabId);
//			fmt.addToBackStack(null);
			fmt.commit();
		}
		currentBasket = newFrag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Services
		mStatusReceiver = new StatusReceiver();
	}

	@Override
	public void onResume() {
		super.onResume();
		// Register Listener
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intents.ACTION_ADD_BASKET);
		filter.addAction(Intents.ACTION_SAVE_BASKET);
		getActivity().registerReceiver(mStatusReceiver, filter);

	}

	@Override
	public void onPause() {
		// Listener
		getActivity().unregisterReceiver(mStatusReceiver);
		super.onPause();
	}

	private class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intents.ACTION_ADD_BASKET.equals(action)) {
				Offer status = (Offer) intent.getSerializableExtra(Intents.EXTRA_OFFER);
				currentBasket.onAddBasketItem(status);
				context.removeStickyBroadcast(intent);
			} else if (Intents.ACTION_SAVE_BASKET.equals(action)) {
				currentBasket.saveOrder();
			}
		}
	}

}

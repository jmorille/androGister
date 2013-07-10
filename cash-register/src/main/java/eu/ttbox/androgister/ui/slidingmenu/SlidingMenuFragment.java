package eu.ttbox.androgister.ui.slidingmenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.slidingmenu.lib.app.SlidingActivityBase;

import eu.ttbox.androgister.R;
import eu.ttbox.androgister.security.DeviceAdminActivity;
import eu.ttbox.androgister.ui.CashRegisterActivity;
import eu.ttbox.androgister.ui.admin.offer.OfferCreatorActivity;
import eu.ttbox.androgister.ui.config.MyPreferencesActivity;

/**
 * @see SampleListFragment
 * 
 */
public class SlidingMenuFragment extends Fragment {

    private static final String TAG = "SlidingMenuFragment";

    private ScrollView slidingmenuContainer;

    private SparseArray<SlindingMenuItemView> menuItems;
    private static int[] menuIds = new int[] { //
    R.id.menu_cash_register, R.id.menu_catalog_manager //
            , R.id.menu_preference , R.id.menu_security_preference //
    };

    // ===========================================================
    // Constructors
    // ===========================================================

    public SlidingMenuFragment() {
        super();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.slidingmenu_item_list, null);
        slidingmenuContainer = (ScrollView) v.findViewById(R.id.slidingmenu_container);

        // Register Menu Item
        OnClickListener menuItemOnClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int itemId = v.getId();
                onSlidingMenuSelectItem(itemId);
            }

        };
        // Register listener
        SparseArray<SlindingMenuItemView> menuItems = new SparseArray<SlindingMenuItemView>();
        for (int menuId : menuIds) {
            SlindingMenuItemView menuItem = (SlindingMenuItemView) v.findViewById(menuId);
            menuItems.put(menuId, menuItem);
            if (menuItem != null) {
                menuItem.setOnClickListener(menuItemOnClickListener);
            }
            // Clear All Selector, just in case
            if (menuItem.isSlidingMenuSelectedVisible()) {
                menuItem.setSlidingMenuSelectedVisible(false);
            }
        }
        this.menuItems = menuItems;

        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Binding Menu
        Activity activity = getActivity();
        Class<? extends Activity> activityClass = (Class<? extends Activity>) activity.getClass();
        for (int menuId : menuIds) {
            Class<? extends Activity> menuItemClass = getActivityClassByItemId(menuId);
            if (menuItemClass != null && activityClass.isAssignableFrom(menuItemClass)) {
                SlindingMenuItemView menuItem = menuItems.get(menuId);
                menuItem.setSlidingMenuSelectedVisible(true);
            }
        }
    }

    // ===========================================================
    // Scroll View
    // ===========================================================

    private void showScrollViewOnTop() {
        // Ugly way to display always the top
        slidingmenuContainer.post(new Runnable() {
            public void run() {
                // slidingmenuContainer.scrollTo(0, 0);
                slidingmenuContainer.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    // ===========================================================
    // Sliding Menu Select Item
    // ===========================================================

    @SuppressLint("InlinedApi")
    private void clearActivityHistoryStack(Intent intentOption, boolean isRootActivity) {
        if (isRootActivity) {

            intentOption.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Log.d(TAG, "Clear ActivityHistoryStack for Intent : " + intentOption);

        }
    }

    private boolean onSlidingMenuSelectItem(int itemId) {
        Context context = getActivity();
        boolean isRootActivity = false;
        switch (itemId) {
        case R.id.menu_preference:
        case R.id.menu_security_preference:
        case R.id.menu_cash_register:
        case R.id.menu_catalog_manager:
            // case R.id.menu_pairing:
            // case R.id.menu_track_person:
            // case R.id.menu_smslog:
            // isRootActivity = true;
            // case R.id.menu_extra_feature:
            // case R.id.menuGeotracker:
            // case R.id.menuMap:
            Class<? extends Activity> intentClass = getActivityClassByItemId(itemId);
            if (intentClass != null) {
                Intent intentOption = new Intent(context, intentClass);
                switchFragment();
                // Activity
                clearActivityHistoryStack(intentOption, isRootActivity);
                context.startActivity(intentOption);

                return true;
            }
            // return false;

        }
        return false;
    }

    private Class<? extends Activity> getActivityClassByItemId(int itemId) {
        switch (itemId) {
        case R.id.menu_cash_register:
            return CashRegisterActivity.class;
        case R.id.menu_catalog_manager:
            return OfferCreatorActivity.class;
        case R.id.menu_preference:
            return MyPreferencesActivity.class;
        case R.id.menu_security_preference:
            return DeviceAdminActivity.class;
            // case R.id.menuGeotracker:
            // return GeoTrakerActivity.class;
            // case R.id.menuMap:
            // return ShowMapActivity.class;
            // case R.id.menu_pairing:
            // return PairingListActivity.class;
            // case R.id.menu_track_person:
            // return PersonListActivity.class;
            // case R.id.menu_smslog:
            // return SmsLogListActivity.class;
            // case R.id.menu_extra_feature:
            // return PayFeaturesActivity.class;
        default:
            return null;
        }
    }

    // the meat of switching the above fragment
    private void switchFragment() {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof SlidingActivityBase) {
            SlidingActivityBase fca = (SlidingActivityBase) getActivity();
            fca.showContent();
        }
    }

}

package eu.ttbox.androgister.ui.register;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.core.PriceHelper;
import eu.ttbox.androgister.domain.OrderItem;
import eu.ttbox.androgister.domain.dao.helper.OrderItemHelper;
import eu.ttbox.androgister.domain.ref.OrderPaymentModeEnum;
import eu.ttbox.androgister.ui.register.RegisterBasketFragment.OnBasketSunUpdateListener;

/**
 * {link
 * http://answers.oreilly.com/topic/2448-how-to-use-the-fragment-class-in-the
 * -android-honeycomb-sdk-preview/}
 * 
 * @author jmorille
 * 
 */
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

//    private SparseArray<RegisterBasketFragment> cacheBasket = new SparseArray<RegisterBasketFragment>();
//    private SparseArray<Button> cacheButton = new SparseArray<Button>();

    private SparseArray<CacheMultibasketBean> cacheButton = new SparseArray<CacheMultibasketBean>();

    
    private static class CacheMultibasketBean {
         Button button;
        RegisterBasketFragment basket;

    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        filter.addAction(Intents.ACTION_PERSON_ASK_SELECT_DIALOG);
        filter.addAction(Intents.ACTION_ORDER_SAVED);
        // Listener
        getActivity().registerReceiver(mStatusReceiver, filter);
        Log.i(TAG, "###  onResume");
    }

    @Override
    public void onPause() {
        // Listener
        getActivity().unregisterReceiver(mStatusReceiver);
        Log.i(TAG, "###  onPause");

        super.onPause();
    }

    private void addNewTab() {
        for (int i = 0; i < MAX_KEY; i++) {
            CacheMultibasketBean btn = cacheButton.get(i);
            if (btn == null) {
                updateTab(i);
                return;
            }
        }
    }

    private CacheMultibasketBean addNewTab(final int tabId, boolean checkexiting) {
        CacheMultibasketBean bask = null;
        // Check Existing button
        if (checkexiting) {
            bask = cacheButton.get(tabId);
            if (bask != null) {
                return bask;
            }
        }
        // Create Button
        bask = new CacheMultibasketBean();
        final Button btn = new Button(getActivity()); 
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
         
        // Prepare to display
        bask.button =btn;
        bask.basket = createRegisterBasketFragment(btn);
        viewTabs.addView(btn);
        cacheButton.put(tabId, bask); 
        return bask;
    }

   private RegisterBasketFragment createRegisterBasketFragment(final Button btn) {
       RegisterBasketFragment newFrag = new RegisterBasketFragment();
       newFrag.setOnBasketSunUpdateListener(new OnBasketSunUpdateListener() {

           @Override
           public void onBasketSum(long sum) {
               String sumText = PriceHelper.getToStringPrice(sum);
               btn.setText(String.format("Total %s", sumText));
           }
       });
       return newFrag;
   }
    
    
    private void removeTab(int tabId) {
        Log.i(TAG, String.format("Remove tabId %s for basket Size %s", tabId, cacheButton.size()));
        if (cacheButton.size() > 1) {
            // Delete the tabs 
            CacheMultibasketBean btn = cacheButton.get(tabId);
            if (btn != null) {
                viewTabs.removeView(btn.button);
                cacheButton.delete(tabId); 
                if (tabId==mCurrentTab ) {
                    mCurrentTab = -1;
                }
             }
            // Reaffecte an another tab
            // NEED TO BE DELETE BEFORE TO FIND NEW
//            if (mCurrentTab == tabId) {
                int newTab = cacheButton.keyAt( Math.max( 0,cacheButton.size()-1)); 
                Log.i(TAG, String.format("After remove Tab %s need to set new Tab as %s", mCurrentTab, newTab));
                updateTab(newTab);
//            }
        } else {
            CacheMultibasketBean btn = cacheButton.get(tabId);
            // Clear Previous Listener
            RegisterBasketFragment previousBasket = btn.basket; 
            previousBasket.setOnBasketSunUpdateListener(null); 
            // add new basket
            btn.basket = createRegisterBasketFragment(btn.button);
            mCurrentTab = -1;
            updateTab(tabId);
        }
    }

    private void updateTab(int whichChild) {
        if (mCurrentTab != whichChild) {
            Log.i(TAG, String.format("Ask update tabId %s", whichChild));
            CacheMultibasketBean bask = cacheButton.get(whichChild); 
            if (bask==null) {
                bask = addNewTab(whichChild, true); 
            } 
            // Update Display
            CacheMultibasketBean btn = cacheButton.get(mCurrentTab);
            if (btn != null) {
                btn.button.setBackgroundResource(R.drawable.tab_selector);
            }
            Button btnNew = bask.button;
            btnNew.setBackgroundResource(R.drawable.tab_selected);
            // Update Current Status
            currentBasket = bask.basket;
            mCurrentTab = whichChild;
            // Do switch fragment
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(android.R.id.tabcontent, bask.basket);
            ft.setTransition(FragmentTransaction.TRANSIT_NONE);
            ft.commit();
        }
    }
    
    public void onAddBasketItem(OrderItem item) {
        currentBasket.onAddBasketItem(item);
    }
 
    private class StatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive Intent action : " + action);
            if (Intents.ACTION_ADD_BASKET.equals(action)) {
                Bundle offer =   intent.getExtras();
                OrderItem item = OrderItemHelper.createFromOffer(offer);
                onAddBasketItem(item);
                // context.removeStickyBroadcast(intent);
            } else if (Intents.ACTION_SAVE_BASKET.equals(action)) {
                OrderPaymentModeEnum paymentMode = OrderPaymentModeEnum.getEnumFromKey(intent.getIntExtra(Intents.EXTRA_ORDER_PAYMENT_MODE, -1));
                boolean isSave = currentBasket.askToSaveBasketToOrder(paymentMode);
                if (isSave) {
                    removeTab(mCurrentTab);
                }
            } else if (Intents.ACTION_PERSON_ASK_SELECT_DIALOG.equals(action)) {
                currentBasket.askOpenSelectPersonList();
            } else if (Intents.ACTION_ORDER_SAVED.equals(action)) {
                Toast.makeText(getActivity(), "Order Saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

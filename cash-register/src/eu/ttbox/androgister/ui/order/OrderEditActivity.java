package eu.ttbox.androgister.ui.order;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;

public class OrderEditActivity extends Activity {

    private static final String TAG = "OrderEditActivity";

    private OrderEditFragment orderEditFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_edit_activity);
        Log.d(TAG, "onCreate OrderEditActivity");
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof OrderEditFragment) {
            orderEditFragment = (OrderEditFragment) fragment;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Handle Intent
        handleIntent(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    protected void handleIntent(Intent intent) {
        Log.d(TAG, "handleIntent : " + intent);
        if (intent == null) {
            return;
        }
        // Handel Intent
        String action = intent.getAction();
        Log.i(TAG, "Handle Intent action " + action);
        if (Intents.ACTION_ORDER_VIEW_DETAIL.equals(action)) {
            long orderId = intent.getLongExtra(Intents.EXTRA_ORDER, -1);
            Log.i(TAG, "Handle Intent action ACTION_VIEW_ORDER_DETAIL : orderId = " + orderId);
            if (orderId != -1) {
                orderEditFragment.doSearchOrder(orderId); 
            }
        }
    }

    

}

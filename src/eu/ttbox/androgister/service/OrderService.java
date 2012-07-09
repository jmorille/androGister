package eu.ttbox.androgister.service;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import eu.ttbox.androgister.core.AppConstants;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.database.order.OrderDatabase;
import eu.ttbox.androgister.model.Order;

public class OrderService extends IntentService implements SharedPreferences.OnSharedPreferenceChangeListener {


	private static final String TAG = "OrderService";

    private BroadcastReceiver receiver;
    private IBinder localBinder;
    private OrderDatabase orderDatabase;

    private SharedPreferences prefs;

    // Instance Data
    private String deviceId;

    public OrderService() {
		super("OrderService");
	}

    
    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Service
        orderDatabase = new OrderDatabase(getBaseContext());
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefs.registerOnSharedPreferenceChangeListener(this);
        // Instance Data
        final String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        this.deviceId = prefs.getString(AppConstants.PREFS_DEVICE_ID, androidId);
        // Register Listener
        localBinder = new LocalBinder();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intents.ACTION_ORDER_ADD);
        receiver = new StatusReceiver();
        registerReceiver(receiver, filter); //
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (AppConstants.PREFS_DEVICE_ID.equals(key)) {
            this.deviceId = sharedPreferences.getString(AppConstants.PREFS_DEVICE_ID, deviceId);
        }
    }

    @Override
    public void onDestroy() {
        // Service
        orderDatabase = null;
        // Listener
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void saveOrder(Order order) {
        long orderId = orderDatabase.insertOrder(deviceId, order);
        Log.i(TAG, "Save Order with id " + orderId);
    }

    private void deleteOrder(long orderId) {
        long orderCancelId = orderDatabase.deleteOrder(deviceId, orderId);
        Log.i(TAG, String.format("Cancel Order %s with CancelOrderId %s", orderId, orderCancelId));
    }

    public class LocalBinder extends Binder {

        public OrderService getService() {
            return OrderService.this;
        }
    };

    private class StatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Log.i(TAG, "Service onReceive action : " + action);
//            if (Intents.ACTION_ORDER_ADD.equals(action)) {
//                Order order = (Order) intent.getSerializableExtra(Intents.EXTRA_ORDER);
//                saveOrder(order);
//            } else if (Intents.ACTION_ORDER_DELETE.equals(action)) {
//                long orderId = intent.getLongExtra(Intents.EXTRA_ORDER, -1);
//                if (orderId!=-1) {
//                    deleteOrder(orderId);
//                }
//            }
        }
    }

	@Override
	protected void onHandleIntent(Intent intent) {
		 String action = intent.getAction();
         Log.i(TAG, "Service onReceive action : " + action);
         if (Intents.ACTION_ORDER_ADD.equals(action)) {
             Order order = (Order) intent.getSerializableExtra(Intents.EXTRA_ORDER);
             saveOrder(order);
         } else if (Intents.ACTION_ORDER_DELETE.equals(action)) {
             long orderId = intent.getLongExtra(Intents.EXTRA_ORDER, -1);
             if (orderId!=-1) {
                 deleteOrder(orderId);
             }
         }
	}

}

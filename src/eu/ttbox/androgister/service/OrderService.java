package eu.ttbox.androgister.service;

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

public class OrderService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = "OrderService";

	private BroadcastReceiver receiver;
	private IBinder localBinder;
	private OrderDatabase orderDatabase;

	private SharedPreferences prefs;

	// Instance Data
	private String deviceId;

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
		this.deviceId = prefs.getString(AppConstants.PREFS_DEVICE_ID, androidId );
 		// Register Listener
		localBinder = new LocalBinder();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intents.ACTION_SAVE_ORDER);
		receiver = new StatusReceiver();
		registerReceiver(receiver, filter);
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
		// ContentValues orderValues = OrderHelper.getContentValues(order);
		// Uri orderUri = getContentResolver().insert(OrderProvider.Constants.CONTENT_URI, orderValues);
	}

	public class LocalBinder extends Binder {

		public OrderService getService() {
			return OrderService.this;
		}
	};

	private class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intents.ACTION_SAVE_ORDER.equals(action)) {
				Order order = (Order) intent.getSerializableExtra(Intents.EXTRA_ORDER);
				saveOrder(order);
			}
		}
	}


}

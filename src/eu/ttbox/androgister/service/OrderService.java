package eu.ttbox.androgister.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.database.OrderProvider;
import eu.ttbox.androgister.model.Order;
import eu.ttbox.androgister.model.OrderHelper;

public class OrderService extends Service {

	private BroadcastReceiver receiver;

	private final IBinder localBinder = new Binder() {
		@SuppressWarnings("unused")
		public OrderService getService() {
			return OrderService.this;
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return localBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// Register Listener
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intents.ACTION_SAVE_ORDER);
		receiver = new StatusReceiver();
		registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { 
		return super.onStartCommand(intent, flags, startId);
	}

	private void saveOrder(Order order) {
		ContentValues orderValues = OrderHelper.getContentValues(order);
		Uri orderUri = getContentResolver().insert(OrderProvider.Constants.CONTENT_URI, orderValues);
	}
	

	private class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intents.ACTION_SAVE_ORDER.equals(action)) {
				Order order = (Order)intent.getSerializableExtra(Intents.EXTRA_ORDER);
				saveOrder(order);
			}  
		}
	}
}

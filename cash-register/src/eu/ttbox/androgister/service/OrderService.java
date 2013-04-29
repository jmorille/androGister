package eu.ttbox.androgister.service;

import java.util.ArrayList;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.AppConstants;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.domain.Order;
import eu.ttbox.androgister.domain.OrderItem;
import eu.ttbox.androgister.domain.dao.order.OrderDatabase;
import eu.ttbox.androgister.service.core.WorkerService;
import eu.ttbox.androgister.ui.CashRegisterActivity;
// FIXME

public class OrderService extends WorkerService implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "OrderService";

    private int ORDER_SERVICE_NOTIFICATION = R.id.order_service_notification_started;

    // private BroadcastReceiver receiver;
    private IBinder localBinder;
    private OrderDatabase orderDatabase;

    private SharedPreferences prefs;

    // Instance Data
    private String deviceId;

    public OrderService() {
        super("OrderService");
        setIntentRedelivery(true);
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
        // receiver = new StatusReceiver();
        // registerReceiver(receiver, filter); //
        doInForegroundService();
        Log.i(TAG, "############################################");
        Log.i(TAG, "###        On Create OrderService       ####");
        Log.i(TAG, "############################################");
    }

    private void doInForegroundService() {
        Log.i(TAG, "############################################");
        Log.i(TAG, "###     In Foreground OrderService      ####");
        Log.i(TAG, "############################################");
        // Notification notification =
        // new
        // Notification.Builder(getBaseContext()).setContentTitle(getText(R.string.app_name))
        // //
        // .build();
        CharSequence tickerText = getText(R.string.order_service_notification_started);
        Intent notificationIntent = new Intent(this, CashRegisterActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(getBaseContext()) //
                .setContentTitle(getText(R.string.app_name)) //
                .setSmallIcon(R.drawable.ic_launcher) //
                .setWhen(System.currentTimeMillis()) //
                .setTicker(tickerText) //
                .setContentIntent(pendingIntent) //
                .getNotification();
        // notification.setLatestEventInfo(this,
        // getText(R.string.notification_title),
        // getText(R.string.notification_message), pendingIntent);
        startForeground(ORDER_SERVICE_NOTIFICATION, notification);
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
        // unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void saveOrder(Order order,  ArrayList<OrderItem> orderItems ) {
        long orderId = orderDatabase.insertOrder(deviceId, order, orderItems);
        if (orderId != -1) {
            sendBroadcast(Intents.orderSaved(orderId));
            Log.i(TAG, "Save Order with id " + orderId);
        }
    }

    private void deleteOrder(long orderId) {
        long orderCancelId = orderDatabase.deleteOrder(deviceId, orderId);
        if (orderCancelId != -1) {
            sendBroadcast(Intents.orderSaved(orderCancelId, orderId));
            Log.i(TAG, String.format("Cancel Order %s with Canceler OrderId %s", orderId, orderCancelId));
        }
    }

    public class LocalBinder extends Binder {

        public OrderService getService() {
            return OrderService.this;
        }
    };

    //
    // private class StatusReceiver extends BroadcastReceiver {
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // String action = intent.getAction();
    // Log.i(TAG, "Service onReceive action : " + action);
    // if (Intents.ACTION_ORDER_ADD.equals(action)) {
    // Order order = (Order) intent.getSerializableExtra(Intents.EXTRA_ORDER);
    // saveOrder(order);
    // } else if (Intents.ACTION_ORDER_DELETE.equals(action)) {
    // long orderId = intent.getLongExtra(Intents.EXTRA_ORDER, -1);
    // if (orderId!=-1) {
    // deleteOrder(orderId);
    // }
    // }
    // }
    // }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "Service onReceive action : " + action);
        if (Intents.ACTION_ORDER_ADD.equals(action)) {
            Order order =  intent.getParcelableExtra(Intents.EXTRA_ORDER);
            Parcelable[] parcels =  intent.getParcelableArrayExtra(Intents.EXTRA_ORDER_ITEMS);
            ArrayList<OrderItem> orderItems = new ArrayList<OrderItem>(parcels.length);
             for (Parcelable par : parcels){
                 orderItems.add((OrderItem) par);              
             }
             
            saveOrder(order, orderItems);
        } else if (Intents.ACTION_ORDER_DELETE.equals(action)) {
            long orderId = intent.getLongExtra(Intents.EXTRA_ORDER, -1);
            if (orderId != -1) {
                deleteOrder(orderId);
            }
        }
    }

}

package eu.ttbox.androgister.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.util.Log;

/**
 * {link http://developer.android.com/tools/adk/adk2.html} {link http://developer.android.com/guide/topics/connectivity/usb/accessory.html}
 * 
 * @author jmorille
 * 
 */
public class UsbService extends Service {

	private static final String TAG = "UsbService";

	UsbManager mUSBManager;
	PendingIntent mPermissionIntent;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// Init service
		mUSBManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		// UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
		UsbAccessory[] accessoryList = mUSBManager.getAccessoryList();
		for (UsbAccessory acc : accessoryList) {
			Log.i(TAG, "Detected UsbAccessory " + acc.getModel() + " / " + acc.getManufacturer() + " / " + acc.describeContents());
		}

		// Register
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		registerReceiver(mUsbReceiver, filter);
	}

	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (accessory != null) {
							// call method to set up accessory communication
						}
					} else {
						Log.d(TAG, "permission denied for accessory " + accessory);
					}
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
	            UsbAccessory accessory = (UsbAccessory)intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
	            if (accessory != null) {
	                // call your method that cleans up and closes communication with the accessory
	            }
	        }
		}
	};

}

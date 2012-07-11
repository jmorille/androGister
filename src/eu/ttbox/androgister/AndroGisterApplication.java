package eu.ttbox.androgister;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import eu.ttbox.androgister.core.AppConstants;
import eu.ttbox.androgister.database.order.OrderIdSequence;

public class AndroGisterApplication extends Application {

	private String TAG = "AndroGisterApp";

	private OrderIdSequence orderIdSequence = new OrderIdSequence();
	
	@Override
	public void onCreate() {
 		// Create Application
		super.onCreate();

		// Increment Counter Lauch
		int laugthCount = incrementApplicationLaunchCounter();
 	}
 
	
	private int incrementApplicationLaunchCounter() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		// Read previous values
		int counter = settings.getInt(AppConstants.PREFS_APP_COUNT_LAUGHT, 0);
		counter++;
		// Edit
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putInt(AppConstants.PREFS_APP_COUNT_LAUGHT, counter);
		prefEditor.commit();
		return counter;
	}
	
	public OrderIdSequence getOrderIdSequence() {
		 return orderIdSequence;
	}
	
	
}

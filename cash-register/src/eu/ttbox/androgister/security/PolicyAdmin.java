package eu.ttbox.androgister.security;

import eu.ttbox.androgister.core.AppConstants;
import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class PolicyAdmin extends DeviceAdminReceiver {

    @Override
    public void onDisabled(Context context, Intent intent) {
        // Called when the app is about to be deactivated as a device
        // administrator.
        // Deletes previously stored password policy.
        super.onDisabled(context, intent);
        SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREF, Activity.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

}

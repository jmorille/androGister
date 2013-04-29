package eu.ttbox.androgister;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.otto.Bus;

import eu.ttbox.androgister.core.AppConstants;
import eu.ttbox.androgister.domain.DaoMaster;
import eu.ttbox.androgister.domain.DaoSession;
import eu.ttbox.androgister.domain.dao.RegisterDbOpenHelper;
import eu.ttbox.androgister.domain.dao.order.OrderIdSequence;

public class AndroGisterApplication extends Application {

    private String TAG = "AndroGisterApp";

    private OrderIdSequence orderIdSequence = new OrderIdSequence();
    private DaoSession daoSession;

    private Bus eventBus = new Bus();

    @Override
    public void onCreate() {
        // Create Application
        super.onCreate();

        // Perform the initialization that doesn't have to finish immediately.
        // We use an async task here just to avoid creating a new thread.
        (new DelayedInitializer()).execute();

    }

    private class DelayedInitializer extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            final Context context = AndroGisterApplication.this;
            // Initalize Event Bus
            initDaoSession();
            // Increment Counter Lauch
            int laugthCount = incrementApplicationLaunchCounter(context);
            Log.i(TAG, "Laugth count " + laugthCount);
            return null;
        }

        public void execute() {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        }
    }

    private int incrementApplicationLaunchCounter(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
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

    public Bus getEventBus() {
        return eventBus;
    }

    public DaoSession getDaoSession() {
        if (daoSession == null) {
            daoSession = initDaoSession();
        }
        return daoSession;
    }

    private synchronized DaoSession initDaoSession() {
        if (daoSession == null) {
            Log.d(TAG, "initDaoSession");
            RegisterDbOpenHelper helper = new RegisterDbOpenHelper(AndroGisterApplication.this, null);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            this.daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

}

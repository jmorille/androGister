package eu.ttbox.androgister.ui.admin.user;

import eu.ttbox.androgister.R;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

public class UserEditActivity extends Activity {

    private static final String TAG = "UserEditActivity";

    public static final String ACTION_SAVE_COMPLETED = "saveCompleted";
    public static final String INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED = "finishActivityOnSaveCompleted";

    private boolean mFinishActivityOnSaveCompleted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user_edit_activity);
        handleIntent(getIntent());
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        final String action = intent.getAction();
        mFinishActivityOnSaveCompleted = intent.getBooleanExtra(INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED, false);
        if (ACTION_SAVE_COMPLETED.equals(action)) {
            finish();
            return;
        }

    }

}

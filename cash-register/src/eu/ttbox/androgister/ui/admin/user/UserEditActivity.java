package eu.ttbox.androgister.ui.admin.user;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.ui.admin.user.UserEditFragment.SaveMode;
import eu.ttbox.androgister.ui.admin.user.UserEditFragment.UserEditListener;

public class UserEditActivity extends Activity {

    private static final String TAG = "UserEditActivity";

    public static final String ACTION_SAVE_COMPLETED = "saveCompleted";
    public static final String INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED = "finishActivityOnSaveCompleted";

    private boolean mFinishActivityOnSaveCompleted;
   private  UserEditFragment editFragment;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user_edit_activity);
        handleIntent(getIntent());
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof UserEditFragment) {
        	editFragment = (UserEditFragment)fragment;
        	editFragment.setListener(userEditListener);
        }
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
        
        // load uri
        Uri uri = Intent.ACTION_EDIT.equals(action) ? getIntent().getData() : null;
        editFragment.load(action, uri, getIntent().getExtras());

    }

    @Override
    public void onBackPressed() {
    	editFragment.save(SaveMode.CLOSE);
    }
    
    private UserEditListener userEditListener = new UserEditListener() {

		@Override
		public void onEntityNotFound() {
			finish();
		}

		@Override
		public void onReverted() {
			finish();
		}

		@Override
		public void onSaveFinished(Intent resultIntent) {
			  if (mFinishActivityOnSaveCompleted) {
	                setResult(resultIntent == null ? RESULT_CANCELED : RESULT_OK, resultIntent);
	            } else if (resultIntent != null) {
	                startActivity(resultIntent);
	            }
	            finish();
			
		}

		@Override
		public void onEditOtherEntityRequested(Uri contactLookupUri, ArrayList<ContentValues> values) {
			   Intent intent = new Intent(Intent.ACTION_EDIT, contactLookupUri);
	            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
	                    | Intent.FLAG_ACTIVITY_FORWARD_RESULT);  
	            // Pass on all the data that has been entered so far
	            if (values != null && values.size() != 0) {
	                intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, values);
	            } 
	            startActivity(intent);
	            finish();
		}

 
    	
    };
    
}

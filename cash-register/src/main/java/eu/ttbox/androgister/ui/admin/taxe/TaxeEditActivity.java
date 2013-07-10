package eu.ttbox.androgister.ui.admin.taxe;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import eu.ttbox.androgister.R;

public class TaxeEditActivity extends Activity {

    private static final String TAG = "TaxeEditActivity";

    // Instance
    private TaxeEditFragment editFragment;

    // ===========================================================
    // Constructor
    // ===========================================================

    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_tag_edit_activity); 
    }

    @Override
    public void onAttachFragment(Fragment fragment) { 
        super.onAttachFragment(fragment);
        if (fragment instanceof TaxeEditFragment) {
            editFragment = (TaxeEditFragment) fragment;
        } 
    }

    @Override
    protected void onResume() { 
        super.onResume();
        // Intent 
        handleIntent(getIntent());
    }
    
    // ===========================================================
    // Intent
    // ===========================================================

 

    @Override
    public void onNewIntent(Intent intent) {
        handleIntent(getIntent());
    }

    public void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        Log.d(TAG, "handleIntent : " + action);
        if (Intent.ACTION_EDIT.equals(action)) { 
//            editFragment.setArguments(intent.getExtras());
            editFragment.handleIntent(intent);
        } else if (Intent.ACTION_INSERT.equals(action)) { 
            editFragment.handleIntent(intent);

        }
    }

}

package eu.ttbox.androgister.ui.order;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import eu.ttbox.androgister.R;

public class OrderEditActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_register);
        // Handle Intent
        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    protected void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        // Handel Intent
        String action = intent.getAction();
        if (Intent.ACTION_EDIT.equals(action)) {
            Uri uri = intent.getData();
            String orderIdString = uri.getLastPathSegment();
//            doSearch(orderIdString);
        } else if (Intent.ACTION_DELETE.equals(action)) {

        }
    }

   
    
}

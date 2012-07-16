package eu.ttbox.androgister.ui.admin.offer;

import eu.ttbox.androgister.R;
import android.app.Activity;
import android.os.Bundle;

public class OfferCreatorActivity extends Activity {

    private static final String TAG = "OfferCreatorActivity";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_offer_creator_activity);
    }
}

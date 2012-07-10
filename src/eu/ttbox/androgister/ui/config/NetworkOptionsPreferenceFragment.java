package eu.ttbox.androgister.ui.config;

import eu.ttbox.androgister.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class NetworkOptionsPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.network_options_preferences);
    }
    
}

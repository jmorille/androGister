package eu.ttbox.androgister.ui.config;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import eu.ttbox.androgister.R;

public class GeneralSettingsPreferenceFragment  extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_settings_preferences);
    }

}

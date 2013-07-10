package eu.ttbox.androgister.ui.config;

import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import eu.ttbox.androgister.R;

/**
 * {link http://stackoverflow.com/questions/9199996/how-to-get-android-preferencefragment-title-header-to-show-in-right-pane}
 * 
 * Android sample @see com.example.android.apis.preference.PreferenceWithHeaders
 * 
 * @author jmorille
 *
 */
public class MyPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTitle(R.string.taskCaptionSettings);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
        super.onBuildHeaders(target);
    }
    
}

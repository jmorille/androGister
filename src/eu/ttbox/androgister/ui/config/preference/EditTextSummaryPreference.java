package eu.ttbox.androgister.ui.config.preference;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class EditTextSummaryPreference extends EditTextPreference {

    public EditTextSummaryPreference(Context context) {
        super(context);
    }

    public EditTextSummaryPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EditTextSummaryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setText(String text) {
        String savedText = text;
        if (text != null  ) {
            savedText = text.trim();
            if (savedText.isEmpty()) {
                savedText = null;
            }
        }
        super.setText(savedText);
        setSummary(text);
    }

}

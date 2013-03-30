package eu.ttbox.androgister.ui.core.crud;

import android.text.TextUtils;
import android.widget.EditText;

public class CrudHelper {

    // ===========================================================
    // Helper
    // ===========================================================

    public static String getStringTrimmed(EditText editText) {
        String value = editText.getText().toString();
        int trimmedSize = TextUtils.getTrimmedLength(value);
        if (trimmedSize < 1) {
            value = null;
        } else if (trimmedSize < value.length()) {
            value = value.trim();
        }
        return value;
    }

    public static Float getFloat(EditText editText) {
        String string = getStringTrimmed(editText);
        Float value = string == null ? null : Float.parseFloat(string);
        return value;
    }

    public static Integer getInteger(EditText editText) {
        String string = getStringTrimmed(editText);
        Integer value = string == null ? null : Integer.parseInt(string);
        return value;
    }

    public static Long getLong(EditText editText) {
        String string = getStringTrimmed(editText);
        Long value = string == null ? null : Long.parseLong(string);
        return value;
    }

}

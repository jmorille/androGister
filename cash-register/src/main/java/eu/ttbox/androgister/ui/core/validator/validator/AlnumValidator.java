package eu.ttbox.androgister.ui.core.validator.validator;

import java.util.regex.Pattern;

import android.content.Context;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.ui.core.validator.Validator;

/**
 * Validator to check if a field contains only numbers and letters. Avoids
 * having special characters like accents.
 */
public class AlnumValidator implements Validator {

    /**
     * This si Alnum Pattern to verify value.
     */
    private static final Pattern mPattern = Pattern.compile("^[A-Za-z0-9]+$");

    private int mErrorMessage = R.string.validator_alnum;

    public AlnumValidator() {
        super();
    }

    @Override
    public boolean isValid(CharSequence value) {
        return mPattern.matcher(value).matches();
    }

    @Override
    public String getMessage(Context context) {
        return context.getString(mErrorMessage);
    }
}

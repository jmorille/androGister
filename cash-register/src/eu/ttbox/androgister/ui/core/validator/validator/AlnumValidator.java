package eu.ttbox.androgister.ui.core.validator.validator;

import android.content.Context;
import eu.ttbox.androgister.ui.core.validator.AbstractValidator;
import eu.ttbox.androgister.R;

import java.util.regex.Pattern;

/**
 * Validator to check if a field contains only numbers and letters.
 * Avoids having special characters like accents.
 */
public class AlnumValidator extends AbstractValidator{

    /**
     * This si Alnum Pattern to verify value.
     */
    private static final Pattern mPattern = Pattern.compile("^[A-Za-z0-9]+$");

    private int mErrorMessage = R.string.validator_alnum;

    public AlnumValidator(Context c) {
        super(c);
    }

    @Override
    public boolean isValid(String value) {
        return mPattern.matcher(value).matches();
    }

    @Override
    public String getMessage() {
        return mContext.getString(mErrorMessage);
    }
}

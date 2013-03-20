package eu.ttbox.androgister.ui.core.validator.validator;

import android.content.Context;
import eu.ttbox.androgister.ui.core.validator.AbstractValidator;
import eu.ttbox.androgister.R;

import java.util.regex.Pattern;


public class HexValidator extends AbstractValidator {

    /**
     * This is Hex Pattern to verify value.
     */
    private static final Pattern mPattern = Pattern.compile("^(#|)[0-9A-Fa-f]+$");

    private int mErrorMessage = R.string.validator_alnum;

    public HexValidator(Context c) {
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

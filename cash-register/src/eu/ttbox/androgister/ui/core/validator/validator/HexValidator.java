package eu.ttbox.androgister.ui.core.validator.validator;

import android.content.Context;
import eu.ttbox.androgister.ui.core.validator.Validator;
import eu.ttbox.androgister.R;

import java.util.regex.Pattern;


public class HexValidator implements Validator {

    /**
     * This is Hex Pattern to verify value.
     */
    private static final Pattern mPattern = Pattern.compile("^(#|)[0-9A-Fa-f]+$");

    protected Context mContext;
    private int mErrorMessage = R.string.validator_alnum;

    public HexValidator(Context c) {
        super();
        this.mContext = c;
    }

    @Override
    public boolean isValid(CharSequence value) {
        return mPattern.matcher(value).matches();
    }

    @Override
    public String getMessage() {
        return mContext.getString(mErrorMessage);
    }
}

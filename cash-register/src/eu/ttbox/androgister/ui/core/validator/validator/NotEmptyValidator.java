package eu.ttbox.androgister.ui.core.validator.validator;

import android.content.Context;

import eu.ttbox.androgister.ui.core.validator.Validator;
import eu.ttbox.androgister.R;

public class NotEmptyValidator implements Validator {

    private int mErrorMessage = R.string.validator_empty;

    protected Context mContext;

    public NotEmptyValidator(Context c) {
        super();
        this.mContext = c;
    }

    @Override
    public boolean isValid(CharSequence value) {
        return (value != null && value.length() > 0);
    }

    @Override
    public String getMessage() {
        return mContext.getString(mErrorMessage);
    }

}

package eu.ttbox.androgister.ui.core.validator.validator;

import android.content.Context;

import eu.ttbox.androgister.ui.core.validator.Validator;
import eu.ttbox.androgister.R;

public class NotEmptyValidator implements Validator {

    private int mErrorMessage = R.string.validator_empty;

   

    public NotEmptyValidator( ) {
        super(); 
    }

    @Override
    public boolean isValid(CharSequence value) {
        return (value != null && value.length() > 0);
    }

    @Override
    public String getMessage(Context context) {
        return context.getString(mErrorMessage);
    }

}

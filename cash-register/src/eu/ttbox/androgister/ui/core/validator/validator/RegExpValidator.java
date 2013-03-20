package eu.ttbox.androgister.ui.core.validator.validator;

import android.content.Context;
import eu.ttbox.androgister.ui.core.validator.Validator;
import eu.ttbox.androgister.ui.core.validator.ValidatorException;
import eu.ttbox.androgister.R;

import java.util.regex.Pattern;

/**
 * This validator test value with custom Regex Pattern.
 */
public class RegExpValidator implements Validator {

    protected Context mContext;
    
    private Pattern mPattern;

    private int mErrorMessage = R.string.validator_regexp;

    public RegExpValidator(Context c) {
        super();
        this.mContext = c;
    }

    public void setPattern(String pattern){
        mPattern = Pattern.compile(pattern);
    }

    @Override
    public boolean isValid(CharSequence value) throws ValidatorException {
        if(mPattern != null){
            return mPattern.matcher(value).matches();
        }else{
            throw new ValidatorException("You can set Regexp Pattern first");
        }
    }

    @Override
    public String getMessage() {
        return mContext.getString(mErrorMessage);
    }
}

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
		if(value != null){
			if(value.length() > 0)
				return true;
			else
				return false;
		}else{
			return false;
		}
	}

	@Override
	public String getMessage() {
		return mContext.getString(mErrorMessage);
	}

}

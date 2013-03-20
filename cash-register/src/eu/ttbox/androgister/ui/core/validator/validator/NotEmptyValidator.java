package eu.ttbox.androgister.ui.core.validator.validator;

import android.content.Context;

import eu.ttbox.androgister.ui.core.validator.AbstractValidator;
import eu.ttbox.androgister.R;

public class NotEmptyValidator extends AbstractValidator {
	
	private int mErrorMessage = R.string.validator_empty;
	
	
	public NotEmptyValidator(Context c) {
		super(c);
	}
	
	@Override
	public boolean isValid(String value) {
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

package eu.ttbox.androgister.ui.core.validator.validator;


import android.content.Context;
import android.webkit.URLUtil;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.ui.core.validator.Validator;

public class UrlValidator implements Validator {
	private int mErrorMessage = R.string.validator_url;
	
	protected Context mContext;
	
	public UrlValidator(Context c) {
		super();
		this.mContext = c;
	}
	
	@Override
	public boolean isValid(CharSequence url) {
		if(url.length() > 0){
			if(URLUtil.isValidUrl(url.toString()))
				return true;
			else
				return false;
		}else{
			return true;
		}
	}

	@Override
	public String getMessage() {
		return mContext.getString(mErrorMessage);
	}

}

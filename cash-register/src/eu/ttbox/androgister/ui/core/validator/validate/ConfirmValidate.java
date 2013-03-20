package eu.ttbox.androgister.ui.core.validator.validate;

import android.content.Context;
import android.widget.TextView;

import eu.ttbox.androgister.ui.core.validator.ValidateField;
import eu.ttbox.androgister.ui.core.validator.Validator;
import eu.ttbox.androgister.R;

public class ConfirmValidate implements ValidateField {

	private TextView _field1;
	private TextView _field2;
	private Context mContext;
	private TextView source;
	private int _errorMessage = R.string.validator_confirm;
	
	public ConfirmValidate(TextView field1, TextView field2){
		this._field1 = field1;
		this._field2 = field2;
		source = _field2;
		mContext = field1.getContext();
	}

	@Override
	public boolean isValid(CharSequence value) {
		if(_field1.getText().length() > 0 && _field1.getText().equals(_field2.getText() )){
			return true;
		}else{
			return false;
		}
	}


	@Override
	public String getMessages() { 
		return mContext.getString(_errorMessage);
	}


	@Override
	public void addValidator(Validator validator) {
	}

	@Override
	public TextView getSource() {
		return source;
	}
	
	
}

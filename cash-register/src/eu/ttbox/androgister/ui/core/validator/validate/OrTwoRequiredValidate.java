package eu.ttbox.androgister.ui.core.validator.validate;

import android.content.Context;
import android.widget.TextView;

import eu.ttbox.androgister.ui.core.validator.ValidateField;
import eu.ttbox.androgister.ui.core.validator.Validator;
import eu.ttbox.androgister.ui.core.validator.validator.NotEmptyValidator;

/**
 * Validator class to validate if the fields are empty fields of 2 or not.
 * If one of them is null, no error.
 * If both are nulls: Error
 * @author throrin19
 *
 */
public class OrTwoRequiredValidate implements ValidateField {

	private TextView _field1;
	private TextView _field2;
	private Context mContext;
	
	public OrTwoRequiredValidate(TextView field1, TextView field2){
		this._field1 = field1;
		this._field2 = field2;
		source = _field1;
		mContext = field1.getContext();
	}
	
	private TextView source;
	
	private String _errorMessage;

	@Override
	public boolean isValid(CharSequence value) {
		ValidateTextView field1Validator = new ValidateTextView(_field1);
		field1Validator.addValidator(new NotEmptyValidator(mContext));
		
		ValidateTextView field2Validator = new ValidateTextView(_field2);
		field2Validator.addValidator(new NotEmptyValidator(mContext));
		
		if(field1Validator.isValid(_field1.getText().toString()) || field2Validator.isValid(_field2.getText().toString())){
			return true;
		}else{
			_errorMessage = field1Validator.getMessages();
			return false;
		}
	}


	@Override
	public String getMessages() { 
		return _errorMessage;
	}


	@Override
	public void addValidator(Validator validator) {
	}

	@Override
	public TextView getSource() {
		return source;
	}
	
	
}

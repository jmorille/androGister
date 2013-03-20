package eu.ttbox.androgister.ui.core.validator.validate;

import java.util.ArrayList;
import java.util.Iterator;

import eu.ttbox.androgister.ui.core.validator.ValidateField;
import eu.ttbox.androgister.ui.core.validator.Validator;
import eu.ttbox.androgister.ui.core.validator.ValidatorException;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

public class ValidateTextView implements ValidateField {

    private static final String TAG = "Validate";
    /**
     * Validator chain
     */
    protected ArrayList<Validator> _validators = new ArrayList<Validator>();

    /**
     * Validation failure messages
     */
    protected String mMessage = "";

    protected TextView mSource;

    public ValidateTextView(TextView source) {
        this(source, false);
    }

    public ValidateTextView(TextView source, boolean isTextWatcher) {
        this.mSource = source;
        if (isTextWatcher) {
            this.mSource.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Clear on Input
                    if (s != null && s.length() > 0 && mSource.getError() != null) {
                        mSource.setError(null);
                    }
                    // Check On input
                    if (!isValid(s)) {
                        mSource.setError(mMessage);
                    }
                }
            });
        }
    }

    /**
     * Adds a validator to the end of the chain
     * 
     * @param validator
     */
    public void addValidator(Validator validator) {
        this._validators.add(validator);
        return;
    }

    public boolean isValid(CharSequence value) {
        boolean result = true;
        this.mMessage = "";

        Iterator<Validator> it = this._validators.iterator();
        while (it.hasNext()) {
            Validator validator = it.next();
            try {
                if (!validator.isValid(value)) {
                    this.mMessage = validator.getMessage();
                    result = false;
                    break;
                }
            } catch (ValidatorException e) {
                Log.e(TAG, "ValidatorException : " + e.getMessage(), e);
                this.mMessage = e.getMessage();
                result = false;
                break;
            }
        }

        return result;
    }

    public String getMessages() {
        return this.mMessage;
    }

    public TextView getSource() {
        return this.mSource;
    }

}

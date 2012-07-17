package eu.ttbox.androgister.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class EventEditText extends EditText {

	private OnInputEvent onInputEvent;
	
	public EventEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EventEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EventEditText(Context context) {
		super(context);

	}

	
	
	public void setOnInputEvent(OnInputEvent onInputEvent) {
		this.onInputEvent = onInputEvent; 
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean consumed =  super.onKeyUp(keyCode, event);
		if (consumed) {
			 switch (keyCode) {
	            // if the list accepts the key events and the key event
	            // was a click, the text view gets the selected item
	            // from the drop down as its content
	            case KeyEvent.KEYCODE_ENTER:
	            case KeyEvent.KEYCODE_DPAD_CENTER:
	            case KeyEvent.KEYCODE_TAB:
	                if (event.hasNoModifiers()) {
	                    performCompletion(  keyCode,   event);
	                }
	                return true;
	            }
		}
		performCompletion(  keyCode,   event);
		return consumed;
	}

	private void performCompletion(int keyCode, KeyEvent event) {
		if (onInputEvent!=null) {
			onInputEvent.onKeyUp(keyCode, event);
		}
		
	}
	
	public interface OnInputEvent {
		public void onKeyUp(int keyCode, KeyEvent event);
	}
	
	
}

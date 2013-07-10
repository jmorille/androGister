package eu.ttbox.androgister.ui.register;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import eu.ttbox.androgister.R;

public class KeyboardDigitEventListener implements View.OnKeyListener, View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "KeyboardDigitEventListener";

    KeyboardDigitLogicHandler mHandler;

    void setHandler(KeyboardDigitLogicHandler handler) {
        mHandler = handler;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
        case R.id.del:
            // Log.w(TAG, "onDelete");
            mHandler.onDelete();
            break;
        case R.id.clear:
            // Log.w(TAG, "onClear");
            mHandler.onClear();
            break; 
        case R.id.equal:
            mHandler.onEnter();
            break;
        default:
            if (view instanceof Button) {
                String text = ((Button) view).getText().toString();
//                if (text.length() >= 2) {
//                    // add paren after sin, cos, ln, etc. from buttons
//                    text += '(';
//                }
                mHandler.insert(text);
                
            }
            break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        if (id == R.id.del) {
            mHandler.onClear();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent keyEvent) {
        int action = keyEvent.getAction();

        // if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode ==
        // KeyEvent.KEYCODE_DPAD_RIGHT) {
        // boolean eat = mHandler.eatHorizontalMove(keyCode ==
        // KeyEvent.KEYCODE_DPAD_LEFT);
        // return eat;
        // }

        // Work-around for spurious key event from IME, bug #1639445
        if (action == KeyEvent.ACTION_MULTIPLE && keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            return true; // eat it
        }

        Log.d(TAG, "KEY " + keyCode + "; " + action);

        if (keyEvent.getUnicodeChar() == '=') {
            if (action == KeyEvent.ACTION_UP) {
                mHandler.onEnter();
            }
            return true;
        }
        //
        // if (keyCode != KeyEvent.KEYCODE_DPAD_CENTER && keyCode !=
        // KeyEvent.KEYCODE_DPAD_UP && keyCode != KeyEvent.KEYCODE_DPAD_DOWN &&
        // keyCode != KeyEvent.KEYCODE_ENTER) {
        // if (keyEvent.isPrintingKey() && action == KeyEvent.ACTION_UP) {
        // // Tell the handler that text was updated.
        // mHandler.onTextChanged();
        // }
        // return false;
        // }

        return false;
    }

}

package eu.ttbox.androgister.ui.register;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import eu.ttbox.androgister.R;

public class KeyboardDigitFragment extends Fragment {

    private static final String TAG = "KeyboardDigitFragment";

    OnClickListener mListener = null;// new EventListener();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.keyboard_digit, container, false);
        Log.d(TAG, "Restore onCreateView savedInstanceState: " + savedInstanceState);
        // Listener
        // mListener.setHandler(mLogic, mPager);

        // Single page UI
        final TypedArray buttons = getResources().obtainTypedArray(R.array.keyboard_digit_button_ids);
        for (int i = 0; i < buttons.length(); i++) {
            setOnClickListener(view, buttons.getResourceId(i, 0));
        }
        buttons.recycle();

        return view;
    }

    void setOnClickListener(View v, int id) {
        View target = v.findViewById(id);
        target.setOnClickListener(mListener);
    }

    static class OnKeyboardDigitClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

        }

    }

    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
    // if (keyCode == KeyEvent.KEYCODE_BACK && getAdvancedVisibility() && mPager
    // != null) {
    // mPager.setCurrentItem(BASIC_PANEL);
    // return true;
    // } else {
    // return super.onKeyDown(keyCode, keyEvent);
    // }
    // }

}

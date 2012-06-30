package eu.ttbox.androgister.ui.register;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;

public class RegisterKeyboardFragment extends Fragment {

	Button validateButton;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.register_keyboard, container, false);
		validateButton = (Button)view.findViewById(R.id.register_kb_validate);
		validateButton.setOnClickListener(new View.OnClickListener() {
 			@Override
			public void onClick(View v) {
 				getActivity().sendBroadcast(Intents.saveBasket());
 			}
		});
		return view;
	}

	
}

package eu.ttbox.androgister.ui.register;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import eu.ttbox.androgister.R;

public class ProductRegisterFragment extends Fragment {

	 
	 	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.product_register, container, false); 
		return view;
	}
 
 
}

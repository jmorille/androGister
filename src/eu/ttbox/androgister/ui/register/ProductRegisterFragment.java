package eu.ttbox.androgister.ui.register;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.model.Offer;

public class ProductRegisterFragment extends Fragment {

	private BroadcastReceiver mStatusReceiver;

	 	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.product_register, container, false); 
		return view;
	}
 

	@Override
	public void onResume() {
		super.onResume();
		mStatusReceiver = new StatusReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intents.ACTION_ADD_BASKET);
		getActivity().registerReceiver(mStatusReceiver, filter);
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mStatusReceiver);
		mStatusReceiver = null;
		super.onPause();
	}

	private class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intents.ACTION_ADD_BASKET.equals(action)) {
				// TODO: Filter by authority
				Offer status = (Offer) intent.getSerializableExtra(Intents.EXTRA_OFFER);
				onStatusChanged(status);
			}
		}
	}

	public void onStatusChanged(Offer status) {
		//setText(status.getName());
 	}
}

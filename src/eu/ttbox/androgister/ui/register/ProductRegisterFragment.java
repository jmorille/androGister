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
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.model.Product;

public class ProductRegisterFragment extends Fragment {

	private BroadcastReceiver mStatusReceiver;

	TextView resultTextview;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.product_register, container, false);
		resultTextview = (TextView)view.findViewById(R.id.resultText);
		return view;
	}

	public void setText(String capt) {
		resultTextview.setText(capt);
	}

	@Override
	public void onResume() {
		super.onResume();
		mStatusReceiver = new StatusReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intents.ACTION_STATUS);
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
			if (Intents.ACTION_STATUS.equals(action)) {
				// TODO: Filter by authority
				Product status = (Product) intent.getSerializableExtra(Intents.EXTRA_STATUS);
				onStatusChanged(status);
			}
		}
	}

	public void onStatusChanged(Product status) {
		setText(status.getName());
 	}
}

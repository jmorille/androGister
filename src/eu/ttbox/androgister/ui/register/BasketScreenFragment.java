package eu.ttbox.androgister.ui.register;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.model.Article;

public class BasketScreenFragment extends ListFragment {

	private BroadcastReceiver mStatusReceiver;

	private ArrayList<String> basket = new ArrayList<String>();
	private BasketItemAdapter listAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		listAdapter = new BasketItemAdapter (getActivity(),  basket);
		setListAdapter(listAdapter);
		// Services
		mStatusReceiver = new StatusReceiver();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.basket_screen, container, false);
	}

	@Override
	public void onResume() {
		super.onResume(); 
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intents.ACTION_STATUS); 
		getActivity().registerReceiver(mStatusReceiver, filter);
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mStatusReceiver); 
		super.onPause();
	}

	private class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intents.ACTION_STATUS.equals(action)) { 
				Article status = (Article) intent.getSerializableExtra(Intents.EXTRA_STATUS);
				onStatusChanged(status);
			}
		}
	}

	public void onStatusChanged(Article status) {
 		listAdapter.add(status.getState());
		Toast.makeText(getActivity(), "Add basket " + status.getState() + " / "+basket.size(), Toast.LENGTH_LONG).show();
	}

}

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
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.model.PriceHelper;
import eu.ttbox.androgister.model.Product;

public class BasketScreenFragment extends ListFragment {

	private BroadcastReceiver mStatusReceiver;

	private ArrayList<Product> basket = new ArrayList<Product>();
	private BasketItemAdapter listAdapter;
	private TextView sumTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		// Adpater
		listAdapter = new BasketItemAdapter (getActivity(),  basket);
		setListAdapter(listAdapter);
		// Services
		mStatusReceiver = new StatusReceiver();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=  inflater.inflate(R.layout.basket_screen, container, false);
		// View
		sumTextView = (TextView)view.findViewById(R.id.basket_screen_sum);
		return view;
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
				Product status = (Product) intent.getSerializableExtra(Intents.EXTRA_STATUS);
				onStatusChanged(status);
			}
		}
	}
	
	private void doSumBasket() {
		long sum = 0;
		for (Product item: basket) {
			sum+=item.getPriceHT();
		}
		sumTextView.setText(PriceHelper.getToStringPrice(sum));
	}

	public void onStatusChanged(Product status) {
 		listAdapter.add(status );
 		doSumBasket();
//		Toast.makeText(getActivity(), "Add basket " + status.getName() + " / "+basket.size(), Toast.LENGTH_LONG).show();
	}

}

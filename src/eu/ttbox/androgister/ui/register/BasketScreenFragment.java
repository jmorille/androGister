package eu.ttbox.androgister.ui.register;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.model.PriceHelper;
import eu.ttbox.androgister.model.Product;

public class BasketScreenFragment extends Fragment {

	private BroadcastReceiver mStatusReceiver;

	private ArrayList<Product> basket = new ArrayList<Product>();
	private BasketItemAdapter listAdapter;
	// View
	private TextView sumTextView;
	private ListView listView;

	private final OnItemLongClickListener mOnLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			return onListItemLongClick((ListView) parent, view, position, id);
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Adpater
		listAdapter = new BasketItemAdapter(getActivity(), basket);
		// Services
		mStatusReceiver = new StatusReceiver();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.basket_screen, container, false);
		// Bind
		listView = (ListView) view.findViewById(R.id.basket_screen_list);
		listView.setOnItemLongClickListener(mOnLongClickListener);
		listView.setAdapter(listAdapter);
		// View
		sumTextView = (TextView) view.findViewById(R.id.basket_screen_sum);
		// Compute
		doSumBasket();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Register Listener
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
		for (Product item : basket) {
			sum += item.getPriceHT();
		}
		sumTextView.setText(PriceHelper.getToStringPrice(sum));
	}

	public void onStatusChanged(Product status) {
		listAdapter.add(status);
		doSumBasket();
	}

	protected boolean onListItemLongClick(ListView list, View view, int position, long id) {
		Product product = (Product) listAdapter.getItem(position);
		listAdapter.remove(product);
		doSumBasket();
		return true;
	}

}

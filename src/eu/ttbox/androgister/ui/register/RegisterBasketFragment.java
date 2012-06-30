package eu.ttbox.androgister.ui.register;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.model.Offer;
import eu.ttbox.androgister.model.Order;
import eu.ttbox.androgister.model.OrderItem;
import eu.ttbox.androgister.model.OrderItemHelper;
import eu.ttbox.androgister.model.PriceHelper;

public class RegisterBasketFragment extends Fragment {

	private BroadcastReceiver mStatusReceiver;

	private ArrayList<OrderItem> basket = new ArrayList<OrderItem>();
	private BasketItemAdapter listAdapter;
	// View
	private TextView sumTextView;
	private ListView listView;

	private Executor executor = Executors.newSingleThreadExecutor();

	private static final int UI_MSG_SET_BASKET_SUM = 1;

	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UI_MSG_SET_BASKET_SUM:
				Long sum = (Long) msg.obj;
				sumTextView.setText(PriceHelper.getToStringPrice(sum));
				break;

			default:
				break;
			}
		}
	};

	private final Runnable doBasketSum = new Runnable() {
		@Override
		public void run() {
			long sum = getComputeBasketSum();
			uiHandler.sendMessage(uiHandler.obtainMessage(UI_MSG_SET_BASKET_SUM, Long.valueOf(sum)));
		}
	};

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
		View view = inflater.inflate(R.layout.register_basket, container, false);
		// Bind
		listView = (ListView) view.findViewById(R.id.basket_screen_list);
		listView.setOnItemLongClickListener(mOnLongClickListener);
		listView.setAdapter(listAdapter);
		// View
		sumTextView = (TextView) view.findViewById(R.id.basket_screen_sum);
		// Compute Initila Values
		executor.execute(doBasketSum);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Register Listener
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intents.ACTION_ADD_BASKET);
		filter.addAction(Intents.ACTION_SAVE_BASKET);
		getActivity().registerReceiver(mStatusReceiver, filter);

	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mStatusReceiver);
		super.onPause();
	}

	private long getComputeBasketSum() {
		return getComputeBasketSum(basket);
	}

	private long getComputeBasketSum(ArrayList<OrderItem> items) {
		long sum = 0;
		if (items != null && !items.isEmpty()) {
			for (OrderItem item : items) {
				sum += item.getPriceSumHT();
			}
		}
		return sum;
	}

	public void onAddBasketItem(Offer product) {
		OrderItem item = OrderItemHelper.createFromProduct(product);
		listAdapter.add(item);
		// TODO ADD item
		executor.execute(doBasketSum);
	}

	protected boolean onListItemLongClick(ListView list, View view, int position, long id) {
		OrderItem item = (OrderItem) listAdapter.getItem(position);
		listAdapter.remove(item);
		// TODO Delete Basket
		executor.execute(doBasketSum);
		return true;
	}

	private void clearBasket() {
		basket.clear();
		listAdapter.notifyDataSetChanged();
		executor.execute(doBasketSum);
	}

	private void saveOrder() {
		// Get Clone of Basket Items
		ArrayList<OrderItem> items = new ArrayList<OrderItem>(basket);
		long sumBasket = getComputeBasketSum(items);
		// Prepare Object
		Order order = new Order();
		order.setItems(items);
		order.setPriceSumHT(sumBasket);
		// Save It
		getActivity().sendBroadcast(Intents.saveOrder(order));
		// Temporay Del
		clearBasket();
	}

	private class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intents.ACTION_ADD_BASKET.equals(action)) {
				Offer status = (Offer) intent.getSerializableExtra(Intents.EXTRA_OFFER);
				onAddBasketItem(status);
			} else if (Intents.ACTION_SAVE_BASKET.equals(action)) {
				saveOrder();
			}
		}
	}
}

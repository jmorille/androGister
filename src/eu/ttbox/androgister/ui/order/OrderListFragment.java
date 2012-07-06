package eu.ttbox.androgister.ui.order;

import android.app.Fragment;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.Loader.OnLoadCompleteListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.OrderProvider;
import eu.ttbox.androgister.database.order.OrderDatabase.OrderColumns;

public class OrderListFragment extends Fragment implements
		OnLoadCompleteListener<Cursor> {

	private static final String[] SEARCH_PROJECTION_COLOMN = new String[] {
			OrderColumns.KEY_ID, OrderColumns.KEY_ORDER_NUMBER,
			OrderColumns.KEY_ORDER_UUID, OrderColumns.KEY_STATUS,
			OrderColumns.KEY_ORDER_DATE, OrderColumns.KEY_PRICE_SUM_HT };

	private OrderAdapter listAdapter;
	private ListView listView;

	private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			onListItemClick((ListView) parent, v, position, id);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.order_list, container, false);
		// Bind
		listView = (ListView) view.findViewById(R.id.order_list_list);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(mOnClickListener);
		// Header
		ViewGroup mTop = (ViewGroup) inflater.inflate(
				R.layout.order_list_header, listView, false);
		listView.addHeaderView(mTop, null, false);
		// Do Search
		doSearch(null, (String[]) null);
		return view;
	}

	private void doSearch(String selection, String... selectionArgs) {
		// String selection = null;
		// String[] selectionArgs = null; // new String[] { query };
		String sortOrder = String
				.format("%s DESC", OrderColumns.KEY_ORDER_DATE);
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				OrderProvider.Constants.CONTENT_URI, SEARCH_PROJECTION_COLOMN,
				selection, selectionArgs, sortOrder);
		cursorLoader.registerListener(1, this);
		cursorLoader.startLoading();
	}

	@Override
	public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
		OrderAdapter myListAdapter = new OrderAdapter(getActivity(),
				R.layout.order_list_item, data,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		listView.setAdapter(myListAdapter);
	}

	private void onListItemClick(ListView l, View v, int position, long id) {
		Cursor item = (Cursor) l.getAdapter().getItem(position);
		long orderId = item.getLong(item.getColumnIndex(OrderColumns.KEY_ID));

	}
}

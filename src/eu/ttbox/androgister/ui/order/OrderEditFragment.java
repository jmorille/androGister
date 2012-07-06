package eu.ttbox.androgister.ui.order;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.Loader.OnLoadCompleteListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.database.OrderProvider;
import eu.ttbox.androgister.model.OrderHelper;

public class OrderEditFragment extends Fragment {

    private BroadcastReceiver mStatusReceiver;

    ListView itemList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Services
        mStatusReceiver = new StatusReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        // Register Listener
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intents.ACTION_VIEW_ORDER_DETAIL);
        // Listener
        getActivity().registerReceiver(mStatusReceiver, filter);
        super.onResume();
    }

    @Override
    public void onPause() {
        // Listener
        getActivity().unregisterReceiver(mStatusReceiver);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.order_edit, container, false);
        // itemList = (itemList)v.findViewById(R.id.);
        // TODO
        return v;
    }

    private void doSearch(long orderId) {
        String orderIdString = String.valueOf(orderId);
        doSearch(orderIdString, OrderProvider.Constants.CONTENT_URI_GET_ODRER, orderLoader);
        doSearch(orderIdString, OrderProvider.Constants.CONTENT_URI_GET_ODRER_ITEMS, itemLoader);
    }

    private void doSearch(String orderId, Uri baseUri, OnLoadCompleteListener<Cursor> loader) {
        Uri orderUri = Uri.withAppendedPath(baseUri, orderId);
        CursorLoader cursorLoader = new CursorLoader(getActivity(), orderUri, (String[]) null, null, (String[]) null, null);
        cursorLoader.registerListener(1, loader);
        cursorLoader.startLoading();
    }

    private OnLoadCompleteListener<Cursor> orderLoader = new OnLoadCompleteListener<Cursor>() {

        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            data.moveToFirst();
            OrderHelper helper = new OrderHelper().initWrapper(data);
            //TODO
        }

    };

    private OnLoadCompleteListener<Cursor> itemLoader = new OnLoadCompleteListener<Cursor>() {
        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            OrderItemAdapter myListAdapter = new OrderItemAdapter(getActivity(), R.layout.register_basket_list_item, data, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            itemList.setAdapter(myListAdapter);
        }
    };

    private class StatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intents.ACTION_VIEW_ORDER_DETAIL.equals(action)) {
                long orderId = intent.getLongExtra(Intents.EXTRA_ORDER, -1);
                if (orderId != -1) {
                    doSearch(orderId); 
                }
            }
        }
    }
}

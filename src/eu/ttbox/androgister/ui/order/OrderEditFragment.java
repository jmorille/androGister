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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.database.OrderProvider;
import eu.ttbox.androgister.model.OrderHelper;

public class OrderEditFragment extends Fragment {

    private static final String TAG = "OrderEditFragment";

    private BroadcastReceiver mStatusReceiver;

    private ListView itemList;
    private Button cancelButton, deleteButton, editButton;
    private TextView orderNum, orderUuid, status, orderDate, price;

    private long orderId = -1;

    // @Override
    // public void onCreate(Bundle savedInstanceState) {
    // super.onCreate(savedInstanceState);
    // // Services
    //
    // }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.order_edit, container, false);
        // View
        itemList = (ListView) v.findViewById(R.id.order_edit_items_list);
        orderNum = (TextView) v.findViewById(R.id.order_orderNum_input);
        orderUuid = (TextView) v.findViewById(R.id.order_orderUuid_input);
        status = (TextView) v.findViewById(R.id.order_status_input);
        orderDate = (TextView) v.findViewById(R.id.order_date_input);
        price = (TextView) v.findViewById(R.id.order_price_input);
        // Button
        cancelButton = (Button) v.findViewById(R.id.order_edit_button_cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
             }
        });
        deleteButton = (Button) v.findViewById(R.id.order_edit_button_delete);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                 deleteOrder();
            }
        });
        editButton = (Button) v.findViewById(R.id.order_edit_button_edit);
        editButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return v;
    }

    private void deleteOrder() {
        Intent intent = Intents.deleteOrderDetail(orderId);
        getActivity().sendBroadcast(intent);
    }
    //
    // @Override
    // public void onDestroy() {
    // super.onDestroy();
    // }

    @Override
    public void onResume() {
        super.onResume();
        mStatusReceiver = new StatusReceiver();
        // Register Listener
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intents.ACTION_ORDER_VIEW_DETAIL);
        // Listener
        getActivity().registerReceiver(mStatusReceiver, filter);
        Log.i(TAG, "Register Receiver " + mStatusReceiver);
    }

    @Override
    public void onPause() {
        // Listener
        getActivity().unregisterReceiver(mStatusReceiver);
        mStatusReceiver = null;
        super.onPause();
    }

    public void doSearchOrder(long orderId) {
        this.orderId = orderId;
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
        public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
            Log.d(TAG, "OnLoadCompleteListener for Order");
            cursor.moveToFirst();
            OrderHelper helper = new OrderHelper().initWrapper(cursor);
            // bind Values
            helper.setTextOrderNumber(orderNum, cursor) //
                    .setTextOrderUuid(orderUuid, cursor)//
                    .setTextOrderStatus(status, cursor)//
                    .setTextOrderDate(orderDate, cursor)//
                    .setTextOrderPriceSum(price, cursor);
        }

    };

    private OnLoadCompleteListener<Cursor> itemLoader = new OnLoadCompleteListener<Cursor>() {
        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            Log.d(TAG, "OnLoadCompleteListener for Items");
            OrderItemAdapter myListAdapter = new OrderItemAdapter(getActivity(), R.layout.register_basket_list_item, data, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            itemList.setAdapter(myListAdapter);
        }
    };

    private class StatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive Intent action : " + action);
            if (Intents.ACTION_ORDER_VIEW_DETAIL.equals(action)) {
                long orderId = intent.getLongExtra(Intents.EXTRA_ORDER, -1);
                Log.i(TAG, "onReceive Intent action ACTION_VIEW_ORDER_DETAIL : orderId =" + orderId);
                if (orderId != -1) {
                    doSearchOrder(orderId);
                }
            }
        }
    }
}

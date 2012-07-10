package eu.ttbox.androgister.ui.order;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
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

    private static final int LOADER_ORDER_DETAILS = R.string.config_id_order_edit_loader_started;
    private static final int LOADER_ORDER_ITEMS = R.string.config_id_order_edit_items_loader_started;

    // Bindngs
    private ListView itemList;
    private Button cancelButton, deleteButton, editButton;
    private TextView orderNum, orderUuid, status, orderDate, price;

    // Adapters
    private OrderItemAdapter myListAdapter;

    // Instance Data
    private long orderId = -1;
    private String orderIdString = null;

    // Listeners
    private BroadcastReceiver mStatusReceiver;

    //

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
        // adapater
        myListAdapter = new OrderItemAdapter(getActivity(), R.layout.register_basket_list_item, null, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        itemList.setAdapter(myListAdapter);
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
        Intent intent = Intents.deleteOrderDetail(getActivity(), orderId);
        getActivity().startService(intent);
    }

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
        this.orderIdString = String.valueOf(orderId);
        getLoaderManager().initLoader(LOADER_ORDER_DETAILS, null, orderLoaderCallback);
        getLoaderManager().initLoader(LOADER_ORDER_ITEMS, null, orderItemsLoaderCallback);
    }

    private final LoaderManager.LoaderCallbacks<Cursor> orderLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String sortOrder = null;
            String selection = null;
            String[] selectionArgs = null;
            // Loader
            Uri orderUri = Uri.withAppendedPath(OrderProvider.Constants.CONTENT_URI_GET_ODRER, orderIdString);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), orderUri, null, selection, selectionArgs, sortOrder);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.d(TAG, "OnLoadCompleteListener for Order");
            OrderHelper helper = new OrderHelper().initWrapper(cursor);
            // TODO HIDE ACTION BUTTON on context
            // bind Values
            helper.setTextOrderNumber(orderNum, cursor) //
                    .setTextOrderUuid(orderUuid, cursor)//
                    .setTextOrderStatus(status, cursor)//
                    .setTextOrderDate(orderDate, cursor)//
                    .setTextOrderPriceSum(price, cursor);
            // Validate
            boolean isDeleteAvailaible = helper.isOrderDeletePossible(cursor);
            deleteButton.setEnabled(isDeleteAvailaible);
            editButton.setEnabled(isDeleteAvailaible);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            TextView[] textViews = new TextView[] { orderNum, orderUuid, status, orderDate, price };
            for (TextView textView : textViews) {
                textView.setText(null);
            }

        }

    };

    private final LoaderManager.LoaderCallbacks<Cursor> orderItemsLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String sortOrder = null;
            String selection = null;
            String[] selectionArgs = null;
            // Loader
            Uri orderUri = Uri.withAppendedPath(OrderProvider.Constants.CONTENT_URI_GET_ODRER_ITEMS, orderIdString);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), orderUri, null, selection, selectionArgs, sortOrder);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            myListAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            myListAdapter.swapCursor(null);

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
                    orderIdString = String.valueOf(orderId);
                    doSearchOrder(orderId);
                }
            }
        }
    }
}

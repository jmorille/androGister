package eu.ttbox.androgister.ui.order;

import java.util.Date;

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
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.core.PriceHelper;
import eu.ttbox.androgister.domain.OrderDao;
import eu.ttbox.androgister.domain.OrderDao.OrderCursorHelper;
import eu.ttbox.androgister.domain.dao.helper.OrderHelper;
import eu.ttbox.androgister.domain.provider.OrderItemProvider;
import eu.ttbox.androgister.domain.provider.OrderProvider;
import eu.ttbox.androgister.domain.ref.OrderStatusEnum;

public class OrderEditFragment extends Fragment {

    private static final String TAG = "OrderEditFragment";

    private static final int LOADER_ORDER_DETAILS = R.id.config_id_order_edit_loader_started;
    private static final int LOADER_ORDER_ITEMS = R.id.config_id_order_edit_items_loader_started;

    // Bindngs
    private ListView itemList;
    private Button cancelButton, deleteButton, editButton;
    private TextView orderNumTextView, orderUuidTextView, orderDeleteUuiTextViewd, statusTextView, orderDateTextView, priceTextView;

    // Adapters
    private OrderItemAdapter myListAdapter;

    // Instance Data
    private long orderId = -1;
    private String orderIdString = null;

    // Listeners
    private BroadcastReceiver mStatusReceiver;

    // DAO
    private OrderDao orderDao;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.order_edit, container, false);
        // Dao
        Context context = getActivity();
        AndroGisterApplication app = (AndroGisterApplication) context.getApplicationContext();
        orderDao = app.getDaoSession().getOrderDao();
        
        // View
        itemList = (ListView) v.findViewById(R.id.order_edit_items_list);
        orderNumTextView = (TextView) v.findViewById(R.id.order_orderNum_input);
        orderUuidTextView = (TextView) v.findViewById(R.id.order_orderUuid_input);
        orderDeleteUuiTextViewd = (TextView) v.findViewById(R.id.order_orderDeleteUuid_input);
        statusTextView = (TextView) v.findViewById(R.id.order_status_input);
        orderDateTextView = (TextView) v.findViewById(R.id.order_date_input);
        priceTextView = (TextView) v.findViewById(R.id.order_price_input);
        // Adpater
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
        filter.addAction(Intents.ACTION_ORDER_SAVED); 
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
        getLoaderManager().restartLoader(LOADER_ORDER_DETAILS, null, orderLoaderCallback);
        getLoaderManager().restartLoader(LOADER_ORDER_ITEMS, null, orderItemsLoaderCallback);
    }

    private final LoaderManager.LoaderCallbacks<Cursor> orderLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String sortOrder = null;
            String selection = null;
            String[] selectionArgs = null;
            // Loader
            Uri orderUri = Uri.withAppendedPath(OrderProvider.Constants.CONTENT_URI_GET_ORDER, orderIdString);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), orderUri, null, selection, selectionArgs, sortOrder);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.d(TAG, "OnLoadCompleteListener for Order");
            cursor.moveToFirst();
            OrderCursorHelper helper = orderDao.getCursorHelper(cursor);  
            // bind Values
            helper.setTextOrderUUID(orderUuidTextView, cursor);
           // Date
            long orderDate =  helper.getOrderDate(cursor);
            String orderDateString = OrderHelper.getOrderDateFormat(getActivity()).format( orderDate );
            orderDateTextView.setText(orderDateString);
            // Price
            long priceSumHt = helper.getPriceSumHT(cursor);
            String priceSumHtString = PriceHelper.getToStringPrice(priceSumHt);
            priceTextView.setText(priceSumHtString);
            // Number
            helper.setTextOrderNumber(orderNumTextView, cursor);
            // Status
            int statusId =   helper.getStatusId(cursor);
            OrderStatusEnum status = OrderStatusEnum.getEnumFromKey(statusId);
            String statusLabel = OrderHelper.getOrderStatusLabel(getActivity(), status);
            statusTextView.setText(statusLabel); // FIXME Get a label
            // Validate
            boolean isDeleteAvailaible = OrderHelper.isOrderDeletePossible(cursor, helper);
            deleteButton.setEnabled(isDeleteAvailaible);
            editButton.setEnabled(isDeleteAvailaible);
            // Display Invalidate
            if (!isDeleteAvailaible && OrderStatusEnum.ORDER.equals(status)) {
                String orderDelete = helper.getOrderDeleteUUID(cursor); 
                orderDeleteUuiTextViewd.setText(orderDelete);
                orderDeleteUuiTextViewd.setVisibility(View.VISIBLE);
                orderDeleteUuiTextViewd.setOnClickListener(new OnClickListener() {
                     @Override
                    public void onClick(View v) {
                        startActivity( Intents.viewOrderDetail(getActivity(), orderId));
                     }
                });
            } else {
                orderDeleteUuiTextViewd.setText(null);
                orderDeleteUuiTextViewd.setVisibility(View.GONE);
                orderDeleteUuiTextViewd.setOnClickListener(null);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            TextView[] textViews = new TextView[] { orderNumTextView, orderUuidTextView, orderDeleteUuiTextViewd, statusTextView, orderDateTextView, priceTextView };
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
            Uri orderUri = Uri.withAppendedPath(OrderItemProvider.Constants.CONTENT_URI_GET_ORDER_ITEM, orderIdString);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), orderUri, null, selection, selectionArgs, sortOrder);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            myListAdapter.changeCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            myListAdapter.changeCursor(null); 
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
            } else  if (Intents.ACTION_ORDER_SAVED.equals(action)) {
                long orderLocalId = intent.getLongExtra(Intents.EXTRA_ORDER, -1);
                long orderCanceledId = intent.getLongExtra(Intents.EXTRA_ORDER_CANCELED_ID, -1);
                if (  orderLocalId == orderId || orderCanceledId == orderId ) {
                    doSearchOrder(orderId); 
                }
            }
        }
    }
}

package eu.ttbox.androgister.ui.order;

import java.util.Calendar;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.AppConstants;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.database.OrderProvider;
import eu.ttbox.androgister.database.order.OrderDatabase.OrderColumns;

/**
 * {link http://mobile.tutsplus.com/tutorials/android/android-sdk_loading-
 * data_cursorloader/}
 * 
 * @author jmorille
 * 
 */
public class OrderListFragment extends Fragment {

    private static final String TAG = "OrderListFragment";

    private static final String[] SEARCH_PROJECTION_COLOMN = new String[] { OrderColumns.KEY_ID, OrderColumns.KEY_ORDER_NUMBER, OrderColumns.KEY_ORDER_UUID, OrderColumns.KEY_STATUS,
            OrderColumns.KEY_ORDER_DATE, OrderColumns.KEY_PRICE_SUM_HT };

    private static final int ORDER_LIST_LOADER = 0x01;
    private static final String ORDER_SORT_DEFAULT = String.format("%s DESC, %s DESC", OrderColumns.KEY_ORDER_DATE, OrderColumns.KEY_ORDER_NUMBER);

    // Search Filter Value
    String filterOrderDateMin = null;
    String filterOrderDateMax = null;

    // Adapter
    private OrderListAdapter listAdapter;

    // Binding
    private ListView listView;
    private DatePicker searchDate;

    // Listener
    private final LoaderManager.LoaderCallbacks<Cursor> orderLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String sortOrder = ORDER_SORT_DEFAULT;
            String selection = null;
            String[] selectionArgs = null;
            // Filter
            if (filterOrderDateMin != null) {
                selection = String.format("%1$s >= ? and %1$s < ?", OrderColumns.KEY_ORDER_DATE);
                selectionArgs = new String[] { filterOrderDateMin, filterOrderDateMax };
            }
            // Loader
            CursorLoader cursorLoader = new CursorLoader(getActivity(), OrderProvider.Constants.CONTENT_URI, SEARCH_PROJECTION_COLOMN, selection, selectionArgs, sortOrder);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            listAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            listAdapter.swapCursor(null);
        }

    };

    private final OnDateChangedListener filterOnDateChangedListener = new OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setFilterOrderDate(year, monthOfYear, dayOfMonth);
            getLoaderManager().restartLoader(ORDER_LIST_LOADER, null, orderLoaderCallback);
        }
    };

    private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            onListItemClick((ListView) parent, v, position, id);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        getLoaderManager().restartLoader(ORDER_LIST_LOADER, null, orderLoaderCallback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_list, container, false);
        // Bind
        listView = (ListView) view.findViewById(R.id.order_list_list);
        listView.setOnItemClickListener(mOnClickListener);
        searchDate = (DatePicker) view.findViewById(R.id.order_list_search_date);
        // List Header
        ViewGroup mTop = (ViewGroup) inflater.inflate(R.layout.order_list_header, listView, false);
        listView.addHeaderView(mTop, null, false);

        // List Adpater
        listAdapter = new OrderListAdapter(getActivity(), R.layout.order_list_item, null, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(listAdapter);

        // Search Form: Date
        final Calendar c = setFilterOrderDate(-1, -1, -1);
        int year = c.get(Calendar.YEAR);
        int monthOfYear = c.get(Calendar.MONTH);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        searchDate.setMaxDate(c.getTimeInMillis());
        searchDate.init(year, monthOfYear, dayOfMonth, filterOnDateChangedListener);

        // Do Search
        getLoaderManager().initLoader(ORDER_LIST_LOADER, null, orderLoaderCallback);
        return view;
    }

    private void onListItemClick(ListView l, View v, int position, long id) {
        Cursor item = (Cursor) l.getAdapter().getItem(position);
        long orderId = item.getLong(item.getColumnIndex(OrderColumns.KEY_ID));
        Intent intent = new Intent(getActivity(), OrderEditActivity.class);
        intent.setAction(Intents.ACTION_ORDER_VIEW_DETAIL);
        intent.putExtra(Intents.EXTRA_ORDER, orderId);
        startActivity(intent);
    }

    private Calendar setFilterOrderDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        if (year != -1) {
            c.set(Calendar.YEAR, year);
        }
        if (monthOfYear != -1) {
            c.set(Calendar.MONTH, monthOfYear);
        }
        if (dayOfMonth != -1) {
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
        c.clear(Calendar.HOUR);
        c.clear(Calendar.HOUR_OF_DAY);
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);
        long minFilter = c.getTimeInMillis();
        long maxFilter = minFilter + AppConstants.ONE_DAY_MS;
        filterOrderDateMin = String.valueOf(minFilter);
        filterOrderDateMax = String.valueOf(maxFilter);
        Log.i(TAG, String.format("set Filter OrderDate in range of %1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS,%1$tL to %2$tY-%2$tm-%2$td %2$tH:%2$tM:%2$tS,%2$tL", minFilter, maxFilter));

        return c;
    }

}

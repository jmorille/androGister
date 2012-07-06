package eu.ttbox.androgister.ui.order;

import android.app.Fragment;
import android.content.CursorLoader;
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
import eu.ttbox.androgister.database.OrderProvider;
import eu.ttbox.androgister.ui.product.ProductItemAdapter;

public class OrderEditFragment extends Fragment {

    ListView itemList;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.order_list, container, false);
//        itemList = (itemList)v.findViewById(R.id.);
        return v;
    }

    private void doSearch(String orderId) {
        doSearch(orderId, OrderProvider.Constants.CONTENT_URI_GET_ODRER, orderLoader);
        doSearch(orderId, OrderProvider.Constants.CONTENT_URI_GET_ODRER_ITEMS, itemLoader);
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
            // TODO Auto-generated method stub

        }

    };

    private OnLoadCompleteListener<Cursor> itemLoader = new OnLoadCompleteListener<Cursor>() {

        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            ProductItemAdapter myListAdapter = new ProductItemAdapter(getActivity(), R.layout.register_basket_list_item, data,
                    SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            itemList.setAdapter(myListAdapter);
        }

    };

}

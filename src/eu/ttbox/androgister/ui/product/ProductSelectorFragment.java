package eu.ttbox.androgister.ui.product;

import android.app.ListFragment;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.Loader.OnLoadCompleteListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.database.ProductProvider;
import eu.ttbox.androgister.database.product.ProductDatabase.Column;
import eu.ttbox.androgister.database.product.ProductWrapper;
import eu.ttbox.androgister.model.Product;

public class ProductSelectorFragment extends ListFragment implements OnLoadCompleteListener<Cursor> {

	private static final String[] SEARCH_PROJECTION_COLOMN = new String[] { Column.KEY_ID, Column.KEY_NAME, Column.KEY_DESCRIPTION, Column.KEY_PRICEHT };

	String[] month = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.product_selector, container, false);
		 doSearch();
		return view;
	}

	private void doSearch() {
		String selection = null;
		String[] selectionArgs = null; // new String[] { query };
		String sortOrder = String.format("%s ASC", Column.KEY_NAME);
		CursorLoader cursorLoader = new CursorLoader(getActivity(), ProductProvider.Constants.CONTENT_URI, SEARCH_PROJECTION_COLOMN, selection, selectionArgs,
				sortOrder);

		cursorLoader.registerListener(1, this);
		cursorLoader.startLoading();
		
		// Uri searchUri = ProductProvider.Constants.CONTENT_URI;
		// Cursor cursor = managedQuery(searchUri, SEARCH_PROJECTION_COLOMN, selection, selectionArgs, sortOrder);
		// showResult(cursorLoader);
	}

	@Override
	public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
		// Mapping
		String[] from = new String[] { Column.KEY_NAME, Column.KEY_DESCRIPTION, Column.KEY_PRICEHT }; // ,
		int[] to = new int[] { R.id.product_list_item_name, R.id.product_list_item_description, R.id.product_list_item_price }; // ,
		ProductItemAdapter myListAdapter = new ProductItemAdapter(getActivity(), R.layout.product_list_item, data, from, to,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		// ListAdapter myListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, month);
		setListAdapter(myListAdapter);
	}
 

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor item = (Cursor) getListAdapter().getItem(position);
 		Product status = ProductWrapper.getEntity(item);
		getActivity().sendBroadcast(Intents.status(status));
		// Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
	}
 

}

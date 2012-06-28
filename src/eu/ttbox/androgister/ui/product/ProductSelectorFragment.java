package eu.ttbox.androgister.ui.product;

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
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.database.ProductProvider;
import eu.ttbox.androgister.database.product.ProductDatabase.Column;
import eu.ttbox.androgister.database.product.ProductWrapper;
import eu.ttbox.androgister.model.Product;

public class ProductSelectorFragment extends Fragment implements OnLoadCompleteListener<Cursor> {

	private static final String[] SEARCH_PROJECTION_COLOMN = new String[] { Column.KEY_ID, Column.KEY_NAME, Column.KEY_PRICEHT };

	private GridView gridView;

	private ProductWrapper productWrapper;

	private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			onListItemClick((GridView) parent, v, position, id);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		productWrapper = new ProductWrapper();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.product_selector, container, false);
		// Link search
		gridView = (GridView) view.findViewById(R.id.product_selector_gridview);
		gridView.setOnItemClickListener(mOnClickListener);
		// Init Search
		doSearch();
		return view;
	}

	private void doSearch() {
		String selection = null;
		String[] selectionArgs = null; // new String[] { query };
		String sortOrder = String.format("%s ASC, %s ASC", Column.KEY_TAG, Column.KEY_NAME);
		CursorLoader cursorLoader = new CursorLoader(getActivity(), ProductProvider.Constants.CONTENT_URI, SEARCH_PROJECTION_COLOMN, selection, selectionArgs,
				sortOrder);

		cursorLoader.registerListener(1, this);
		cursorLoader.startLoading(); 
	}

	@Override
	public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
		// Mapping
		ProductItemAdapter myListAdapter = new ProductItemAdapter(getActivity(), R.layout.product_list_item, data,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		gridView.setAdapter(myListAdapter);
	}

	public void onListItemClick(GridView l, View v, int position, long id) {
		Cursor item = (Cursor) l.getAdapter().getItem(position);
		Product status = productWrapper.getEntity(item);
		getActivity().sendBroadcast(Intents.status(status));
		// Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
	}

}

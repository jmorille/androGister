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
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.database.OfferProvider;
import eu.ttbox.androgister.database.product.OfferDatabase.Column;
import eu.ttbox.androgister.database.product.OfferWrapper;
import eu.ttbox.androgister.model.Product;

public class ProductSelectorFragment extends Fragment implements OnLoadCompleteListener<Cursor> {

	private static final String[] SEARCH_PROJECTION_COLOMN = new String[] { Column.KEY_ID, Column.KEY_NAME, Column.KEY_PRICEHT, Column.KEY_TAG };

	private static final String SEARCH_SELECTION_TAG = String.format("%s MATCH ?", Column.KEY_TAG);

	private static final String SEARCH_SELECTION_TAG_NO_VALUE = "Tous";

	private String[] filterValues = new String[] { SEARCH_SELECTION_TAG_NO_VALUE, "Entrée", "Plat", "Dessert", "Boisson" };

	private GridView gridView;

	private ListView filterListView;

	private OfferWrapper offerWrapper;

	private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			onListItemClick((GridView) parent, v, position, id);
		}
	};

	private final AdapterView.OnItemClickListener mOnFilterClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			onFilterItemClick((ListView) parent, v, position, id);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		offerWrapper = new OfferWrapper();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.product_selector, container, false);
		// Link search
		gridView = (GridView) view.findViewById(R.id.product_selector_gridview);
		gridView.setOnItemClickListener(mOnClickListener);
		// Filter
		filterListView = (ListView) view.findViewById(R.id.product_selector_filterList);
		ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, filterValues);
		filterListView.setOnItemClickListener(mOnFilterClickListener);
		filterListView.setAdapter(filterAdapter);
		// Init Search
		doSearch(null, (String[]) null);
		return view;
	}

	private void doSearch(String selection, String... selectionArgs) {
		// String selection = null;
		// String[] selectionArgs = null; // new String[] { query };
		String sortOrder = String.format("%s ASC, %s ASC", Column.KEY_TAG, Column.KEY_NAME);
		CursorLoader cursorLoader = new CursorLoader(getActivity(), OfferProvider.Constants.CONTENT_URI, SEARCH_PROJECTION_COLOMN, selection, selectionArgs,
				sortOrder);
		cursorLoader.registerListener(1, this);
		cursorLoader.startLoading();
	}

	@Override
	public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
		ProductItemAdapter myListAdapter = new ProductItemAdapter(getActivity(), R.layout.product_list_item, data,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		gridView.setAdapter(myListAdapter);
	}

	private void onFilterItemClick(ListView l, View v, int position, long id) {
		String filterName = (String) l.getAdapter().getItem(position);
		if (SEARCH_SELECTION_TAG_NO_VALUE.equals(filterName)) {
			doSearch(null, (String[])null);
		} else {
			doSearch(SEARCH_SELECTION_TAG, filterName);
		}
		// ((Filterable)gridView.getAdapter()).getFilter().filter(filterName);
	}

	public void onListItemClick(GridView l, View v, int position, long id) {
		Cursor item = (Cursor) l.getAdapter().getItem(position);
		Product status = offerWrapper.getEntity(item);
		getActivity().sendBroadcast(Intents.status(status));
		// Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
	}

}

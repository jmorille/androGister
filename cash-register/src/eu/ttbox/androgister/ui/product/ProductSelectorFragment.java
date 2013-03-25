package eu.ttbox.androgister.ui.product;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
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
import eu.ttbox.androgister.database.product.OfferDatabase.OfferColumns;
import eu.ttbox.androgister.database.product.OfferHelper;
import eu.ttbox.androgister.model.Offer;

/**
 * TODO {link
 * http://blogingtutorials.blogspot.fr/2010/11/android-listview-header
 * -two-or-more-in.html}
 * 
 * @author jmorille
 * 
 */
public class ProductSelectorFragment extends Fragment {

    private static final int OFFER_LIST_LOADER = R.id.config_id_offer_list_loader_started;

    // Constante
    private static final String[] SEARCH_PROJECTION_COLOMN = new String[] { OfferColumns.KEY_ID, OfferColumns.KEY_NAME, OfferColumns.KEY_PRICEHT, OfferColumns.KEY_TAG };
    private static final String SEARCH_SELECTION_TAG = String.format("%s MATCH ?", OfferColumns.KEY_TAG);
    private static final String OFFER_SORT_DEFAULT = String.format("%s ASC, %s ASC", OfferColumns.KEY_TAG, OfferColumns.KEY_NAME);

    // Mock Value
    private static final String SEARCH_SELECTION_TAG_NO_VALUE = "Tous";
    private String[] filterValues = new String[] { SEARCH_SELECTION_TAG_NO_VALUE, "Entrée", "Plat", "Dessert", "Boisson" };

    // Search Filter Value
    private String filterOfferTag = null;

    // Binding Values
    private GridView gridView;
    private ListView filterListView;

    // Helper
    private OfferHelper offerHelper;
    private ProductItemAdapter listAdapter;

    // Registered Listeenr
    private OnOfferSelectedListener onOfferSelectedListener;
    // Listener
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

    private final LoaderManager.LoaderCallbacks<Cursor> offerLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String sortOrder = OFFER_SORT_DEFAULT;
            String selection = null;
            String[] selectionArgs = null;
            if (filterOfferTag != null) {
                selection = SEARCH_SELECTION_TAG;
                selectionArgs = new String[] { filterOfferTag };
            }
            CursorLoader cursorLoader = new CursorLoader(getActivity(), OfferProvider.Constants.CONTENT_URI, SEARCH_PROJECTION_COLOMN, selection, selectionArgs, sortOrder);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offerHelper = new OfferHelper();
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
        // List Adapter
        listAdapter = new ProductItemAdapter(getActivity(), R.layout.admin_product_list_item, null, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        gridView.setAdapter(listAdapter);

        // Init Search
        getLoaderManager().initLoader(OFFER_LIST_LOADER, null, offerLoaderCallback);
        return view;
    }

    private void onFilterItemClick(ListView l, View v, int position, long id) {
        String filterName = (String) l.getAdapter().getItem(position);
        if (SEARCH_SELECTION_TAG_NO_VALUE.equals(filterName)) {
            filterOfferTag = null;
        } else {
            filterOfferTag = filterName;
        }
        getLoaderManager().restartLoader(OFFER_LIST_LOADER, null, offerLoaderCallback);
        // ((Filterable)gridView.getAdapter()).getFilter().filter(filterName);
    }

    public void onListItemClick(GridView l, View v, int position, long id) {
        Cursor item = (Cursor) l.getAdapter().getItem(position);
        Offer offer = offerHelper.getEntity(item);
        if (onOfferSelectedListener != null) {
            onOfferSelectedListener.onOfferSelected(offer);
        } else {
            getActivity().sendBroadcast(Intents.addToBasket(offer));
        }
    }

    public void setOnOfferSelectedListener(OnOfferSelectedListener listener) {
        this.onOfferSelectedListener = listener;
    }
    
    public static interface OnOfferSelectedListener {
        public void onOfferSelected(Offer offer);
    }

}

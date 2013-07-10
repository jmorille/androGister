package eu.ttbox.androgister.ui.product;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.domain.provider.ProductProvider; 
import eu.ttbox.androgister.domain.ProductDao;
import eu.ttbox.androgister.domain.ProductDao.ProductCursorHelper;
import eu.ttbox.androgister.domain.ProductDao.Properties;
import eu.ttbox.androgister.domain.Tag;
import eu.ttbox.androgister.domain.TagDao;
import eu.ttbox.androgister.domain.provider.TagProvider; 

/**
 * TODO {link
 * http://blogingtutorials.blogspot.fr/2010/11/android-listview-header
 * -two-or-more-in.html}
 * 
 * @author jmorille
 * 
 */
public class ProductSelectorFragment extends Fragment {

    private static final String TAG = "ProductSelectorFragment";

    private static final Long UNSET_ID = Long.valueOf(-1);

    private static final int TAG_LIST_LOADER = R.id.config_id_tag_list_loader_started;
    private static final int OFFER_LIST_LOADER = R.id.config_id_offer_list_loader_started;

    // Constante
    private static final String[] TAG_PROJECTION_COLOMN = new String[] { TagDao.Properties.Id.columnName, TagDao.Properties.Name.columnName };
    private static final String TAG_SORT_DEFAULT = String.format("%s ASC", TagDao.Properties.Name.columnName);

//    private static final String[] SEARCH_PROJECTION_COLOMN = new String[] { OfferColumns.KEY_ID, OfferColumns.KEY_NAME, OfferColumns.KEY_PRICEHT, OfferColumns.KEY_TAG };
    private static final String[] SEARCH_PROJECTION_COLOMN = new String[] { Properties.Id.columnName, Properties.Name.columnName,
        Properties.PriceHT.columnName, Properties.TagId.columnName };
    private static final String SEARCH_SELECTION_TAG = String.format("%s = ?", Properties.TagId.columnName);
    private static final String OFFER_SORT_DEFAULT = String.format("%s ASC, %s ASC",Properties.TagId.columnName, Properties.Id.columnName);

    // Mock Value
    // private static final String SEARCH_SELECTION_TAG_NO_VALUE = "Tous";
    // private String[] filterValues = new String[] {
    // SEARCH_SELECTION_TAG_NO_VALUE, "Entrée", "Plat", "Dessert", "Boisson" };

    // Search Filter Value
    private String filterTagId = null;

    // Binding Values
    private GridView gridView;
    private ListView filterListView;

    // Helper
    private ProductDao productDao;
    private ProductCursorHelper productHelper;
    private ProductItemAdapter listAdapter;

    private TagItemAdapter tagListAdapter;

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

   
    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Dao
        Context context = getActivity();
        AndroGisterApplication app = (AndroGisterApplication) context.getApplicationContext();
        productDao = app.getDaoSession().getProductDao();
         productHelper =productDao.getCursorHelper(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_selector, container, false);
        // Link search
        gridView = (GridView) view.findViewById(R.id.product_selector_gridview);
        gridView.setOnItemClickListener(mOnClickListener);
        // Filter By Tag
        Context context = getActivity();
        filterListView = (ListView) view.findViewById(R.id.product_selector_filterList);
        // Tag Header
        View listViewHeader = inflater.inflate(R.layout.admin_calatog_list_item, container, false);
        String allLabel = getString(R.string.all);
        Tag headerData = new Tag();
        headerData.setId(UNSET_ID);
        headerData.setName(allLabel);
        ((TextView) listViewHeader).setText(allLabel);
        filterListView.addHeaderView(listViewHeader, headerData, true);
        // Tag
        filterListView.setOnItemClickListener(mOnFilterClickListener);
        tagListAdapter = new TagItemAdapter(context, null);
        filterListView.setAdapter(tagListAdapter);
        // List Adapter
        listAdapter = new ProductItemAdapter(context, R.layout.admin_product_list_item, null, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        gridView.setAdapter(listAdapter);

        // Init Search
        getLoaderManager().initLoader(TAG_LIST_LOADER, null, tagLoaderCallback);
        getLoaderManager().initLoader(OFFER_LIST_LOADER, null, offerLoaderCallback);
        return view;
    }

    // ===========================================================
    // Actions
    // ===========================================================

    private void onFilterItemClick(ListView l, View v, int position, long id) {
        long itemId = l.getAdapter().getItemId(position);
        if (UNSET_ID.longValue() == itemId) {
            filterTagId = null;
        } else {
            filterTagId = String.valueOf(itemId); 
        }
        getLoaderManager().restartLoader(OFFER_LIST_LOADER, null, offerLoaderCallback);
        // ((Filterable)gridView.getAdapter()).getFilter().filter(filterName);
    }

    public void onListItemClick(GridView l, View v, int position, long id) {
        Cursor item = (Cursor) l.getAdapter().getItem(position);
        Bundle offer =    productHelper.readBundleValues(item); 
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
        public void onOfferSelected(Bundle offer);
    }

    // ===========================================================
    // Cursor Loader
    // ===========================================================

    private final LoaderManager.LoaderCallbacks<Cursor> offerLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String sortOrder = OFFER_SORT_DEFAULT;
            String selection = null;
            String[] selectionArgs = null;
            if (filterTagId != null) {
                selection = SEARCH_SELECTION_TAG;
                selectionArgs = new String[] { filterTagId };
            }
            Log.d(TAG, "Search for filterTagId : " + filterTagId);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), ProductProvider.Constants.CONTENT_URI_CATALOG_PRODUCT, SEARCH_PROJECTION_COLOMN, selection, selectionArgs, sortOrder);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            listAdapter.changeCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            listAdapter.changeCursor(null);
        }

    };

    private final LoaderManager.LoaderCallbacks<Cursor> tagLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String selection = null;
            String[] selectionArgs = null;
            CursorLoader cursorLoader = new CursorLoader(getActivity(), TagProvider.Constants.CONTENT_URI, TAG_PROJECTION_COLOMN, selection, selectionArgs, TAG_SORT_DEFAULT);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            tagListAdapter.changeCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            tagListAdapter.changeCursor(null);
        }

    };
}

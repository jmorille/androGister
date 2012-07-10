package eu.ttbox.androgister.ui.person;

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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.PersonProvider;
import eu.ttbox.androgister.database.product.PersonDatabase.PersonColumns;

public class PersonListFragment extends Fragment {

	private static final String TAG = "PersonListFragment";

	private static final int PERSON_LIST_LOADER = R.string.config_id_person_list_loader_started;

	private static final String[] SEARCH_PROJECTION_COLOMN = new String[] { PersonColumns.KEY_ID, PersonColumns.KEY_LASTNAME, PersonColumns.KEY_FIRSTNAME, PersonColumns.KEY_MATRICULE };

	private static final String PERSON_SORT_DEFAULT = String.format("%s DESC, %s DESC", PersonColumns.KEY_LASTNAME, PersonColumns.KEY_FIRSTNAME);

	// Adapter
	private PersonListAdapter listAdapter;

	// Binding
	private ListView listView;

	// Listener
	private final LoaderManager.LoaderCallbacks<Cursor> orderLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String sortOrder = PERSON_SORT_DEFAULT;
			String selection = null;
			String[] selectionArgs = null;
			// Filter
			// Loader
			CursorLoader cursorLoader = new CursorLoader(getActivity(), PersonProvider.Constants.CONTENT_URI, SEARCH_PROJECTION_COLOMN, selection, selectionArgs, sortOrder);
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

	private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			onListItemClick((ListView) parent, v, position, id);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.person_list, container, false);
		// Bind
		listView = (ListView) view.findViewById(R.id.person_list_list);
		listView.setOnItemClickListener(mOnClickListener);
		// List Header
		ViewGroup mTop = (ViewGroup) inflater.inflate(R.layout.person_list_header, listView, false);
		listView.addHeaderView(mTop, null, false);

		// List Adpater
		listAdapter = new PersonListAdapter(getActivity(), R.layout.person_list_item, null, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		listView.setAdapter(listAdapter);

		// Do Search
		getLoaderManager().initLoader(PERSON_LIST_LOADER, null, orderLoaderCallback);
		return view;
	}

	protected void onListItemClick(ListView parent, View v, int position, long id) {
		// TODO Auto-generated method stub

	}

}

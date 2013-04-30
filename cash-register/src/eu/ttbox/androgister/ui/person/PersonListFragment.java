package eu.ttbox.androgister.ui.person;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents; 
import eu.ttbox.androgister.domain.Person;
import eu.ttbox.androgister.domain.PersonDao;
import eu.ttbox.androgister.domain.PersonDao.PersonCursorHelper;
import eu.ttbox.androgister.domain.PersonDao.Properties;
import eu.ttbox.androgister.domain.provider.PersonProvider;

/**
 * Autocompletion {link
 * http://android.foxykeep.com/dev/how-to-add-autocompletion-to-an-edittext}
 * 
 * @author jmorille
 * 
 */
public class PersonListFragment extends Fragment implements OnQueryTextListener {

    private static final String TAG = "PersonListFragment";

    private static final int PERSON_LIST_LOADER = R.id.config_id_person_list_loader_started;

    private static final String[] SEARCH_PROJECTION_COLOMN = new String[] { Properties.Id.columnName, Properties.Lastname.columnName, Properties.Firstname.columnName, Properties.Matricule.columnName };

    private static final String PERSON_SORT_DEFAULT = String.format("%s DESC, %s DESC", Properties.Lastname.columnName, Properties.Firstname.columnName);

    // Dao
    private PersonDao personDao;

    // Adapter
    private PersonListAdapter listAdapter;

    // Binding
    private TextView searchResultTextView;
    private ListView listView;
    // private TextView emptyListView;
    private EditText searchNameTextView;

    // Listener

    private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            onListItemClick((ListView) parent, v, position, id);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person_list, container, false);
        // Dao
        Context context = getActivity();
        AndroGisterApplication app = (AndroGisterApplication) context.getApplicationContext();
        personDao = app.getDaoSession().getPersonDao();

        // Bind
        listView = (ListView) view.findViewById(R.id.person_list_list);
        listView.setOnItemClickListener(mOnClickListener);
        // emptyListView = (TextView) view.findViewById(android.R.id.empty);
        // listView.setEmptyView(emptyListView);
        // Search Criteria
        searchResultTextView = (TextView) view.findViewById(R.id.person_search_result);
        searchNameTextView = (EditText) view.findViewById(R.id.person_list_search_name_input);
        searchNameTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "On onKeyUp searchNameTextView");
                getLoaderManager().restartLoader(PERSON_LIST_LOADER, null, orderLoaderCallback);
            }

        });

        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        // android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        // searchNameTextView.setAdapter(adapter);
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

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = (Cursor) l.getAdapter().getItem(position);
        PersonCursorHelper helper = personDao.getCursorHelper(cursor);
        // Define result
        getActivity().setResult(Activity.RESULT_OK, Intents.selectedPerson( //
                helper.getId(cursor), helper.getLastname(cursor), helper.getFirstname(cursor), helper.getMatricule(cursor) //
                ));
        getActivity().finish();
    }

    public void doSearch(String query) {
        Bundle args = new Bundle();
        args.putString(SearchManager.QUERY, query);
        getLoaderManager().restartLoader(PERSON_LIST_LOADER, args, orderLoaderCallback);
    }

    private final LoaderManager.LoaderCallbacks<Cursor> orderLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String sortOrder = PERSON_SORT_DEFAULT;
            String selection = null;
            String[] selectionArgs = null;
            String queryString = searchNameTextView.getText().toString();
            if (args != null) {
                queryString = args.getString(SearchManager.QUERY, null);
            } else {
                queryString = searchNameTextView.getText().toString();
            }

            if (queryString != null) {
                queryString = queryString.trim();
                if (!queryString.isEmpty()) {
                    queryString = queryString + "*";
                    // selection = String.format("%s MATCH ? or %s MATCH ?",
                    // Properties.KEY_LASTNAME, Properties.KEY_FIRSTNAME);
                    // selectionArgs = new String[] { queryString, queryString
                    // };
                    selection = String.format("(%s like ? or %s like ?)", Properties.Lastname.columnName, Properties.Firstname.columnName);
                    selectionArgs = new String[] { queryString, queryString };
                }
            }

            // Filter
            // Loader
            CursorLoader cursorLoader = new CursorLoader(getActivity(), PersonProvider.Constants.CONTENT_URI, SEARCH_PROJECTION_COLOMN, selection, selectionArgs, sortOrder);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            // Display List
            listAdapter.changeCursor(cursor);
            // Display Counter
            int count = 0;
            if (cursor != null) {
                count = cursor.getCount();
            }
            if (count < 1) {
                searchResultTextView.setText(R.string.search_no_results);
            } else {
                String countString = getResources().getQuantityString(R.plurals.search_results, count, new Object[] { count });
                searchResultTextView.setText(countString);
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            searchResultTextView.setText(R.string.search_instructions);
            listAdapter.changeCursor(null);
        }

    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        doSearch(newText);
        return true;
    }

}

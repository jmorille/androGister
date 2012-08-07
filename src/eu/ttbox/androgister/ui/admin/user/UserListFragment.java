package eu.ttbox.androgister.ui.admin.user;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.UserProvider;
import eu.ttbox.androgister.database.user.UserDatabase;
import eu.ttbox.androgister.database.user.UserDatabase.UserColumns;
import eu.ttbox.androgister.database.user.UserHelper;
import eu.ttbox.androgister.widget.AutoScrollListView;

public class UserListFragment extends Fragment implements OnQueryTextListener {

    private static final String TAG = "UserListFragment";

    private static final int USER_LIST_LOADER = R.id.config_id_admin_user_list_loader_started;

    private static final String[] SEARCH_PROJECTION_COLOMN = new String[] { UserColumns.KEY_ID, UserColumns.KEY_LASTNAME, UserColumns.KEY_FIRSTNAME, UserColumns.KEY_MATRICULE };

    private static final String USER_SORT_DEFAULT = String.format("%s DESC, %s DESC", UserColumns.KEY_LASTNAME, UserColumns.KEY_FIRSTNAME);

    // Adapter
    private UserListAdapter listAdapter;
    private UserHelper helper;

    // Config
    private boolean mSelectionToScreenRequested;
    private boolean mSmoothScrollRequested;
    
    // Binding
    private TextView searchResultTextView;
    private ListView listView;
    private EditText searchNameTextView;

    // Listener
    private OnSelectUserListener onSelectUserListener;

    private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
          
            onListItemClick((ListView) parent, v, position, id);
        }
    };

  

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_user_list, container, false);
        // Bind
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnItemClickListener(mOnClickListener);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setFastScrollEnabled(true);
        listView.setFastScrollAlwaysVisible(true);
        listView.setVerticalScrollbarPosition(0);
        listView.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);
        
//        listView.setOnItemSelectedListener(listener)
        // Search Criteria
        // searchResultTextView = (TextView)
        // view.findViewById(R.id.user_search_result);
        // searchNameTextView = (EditText)
        // view.findViewById(R.id.user_list_search_name_input);
        // searchNameTextView.addTextChangedListener(new TextWatcher() {
        //
        // @Override
        // public void onTextChanged(CharSequence s, int start, int before, int
        // count) {
        //
        // }
        //
        // @Override
        // public void beforeTextChanged(CharSequence s, int start, int count,
        // int after) {
        //
        // }
        //
        // @Override
        // public void afterTextChanged(Editable s) {
        // Log.i(TAG, "On onKeyUp searchNameTextView");
        // getLoaderManager().restartLoader(USER_LIST_LOADER, null,
        // orderLoaderCallback);
        // }
        //
        // });

        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        // android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        // searchNameTextView.setAdapter(adapter);
        // List Header
        // List Adpater
        listAdapter = new UserListAdapter(getActivity(), R.layout.admin_user_list_item, null, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(listAdapter);

        // Do Search
        getLoaderManager().initLoader(USER_LIST_LOADER, null, orderLoaderCallback);
        return view;
    }
    
    public ListView getListView() {
        return listView;
    }
    
    protected void requestSelectionToScreen(int selectedPosition) {
        if (selectedPosition != -1) {
            AutoScrollListView listView = (AutoScrollListView)getListView();
            listView.requestPositionToScreen(
                    selectedPosition + listView.getHeaderViewsCount(), mSmoothScrollRequested);
            mSelectionToScreenRequested = false;
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (onSelectUserListener != null) {
            Cursor cursor = (Cursor) l.getAdapter().getItem(position);
            if (cursor != null) {
                if (helper == null) {
                    helper = new UserHelper().initWrapper(cursor);
                }
                String userId = helper.getUserIdAsString(cursor);
                Uri uri = Uri.withAppendedPath(UserProvider.Constants.CONTENT_URI, userId);
                // User user = helper.getEntity(cursor);
                // Define result
                // TODO
                listView.requestFocusFromTouch();
                listView.setSelection(position);
                Log.i(TAG, "listView.setSelection = " + position );
                listAdapter.setSelectedId(Long.valueOf(userId));
                onSelectUserListener.onViewAction(uri);
            }
        }
    }

    public void doSearch(String query) {
        Bundle args = new Bundle();
        args.putString(SearchManager.QUERY, query);
        getLoaderManager().restartLoader(USER_LIST_LOADER, args, orderLoaderCallback);
    }

    private final LoaderManager.LoaderCallbacks<Cursor> orderLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String sortOrder = USER_SORT_DEFAULT;
            String selection = null;
            String[] selectionArgs = null;
            String queryString = null;// searchNameTextView.getText().toString();
            if (args != null) {
                queryString = args.getString(SearchManager.QUERY, null);
            } else {
                // queryString = searchNameTextView.getText().toString();
            }

            if (queryString != null) {
                queryString = queryString.trim();
                if (!queryString.isEmpty()) {
                    queryString = queryString + "*";
                    // selection = String.format("%s MATCH ? or %s MATCH ?",
                    // UserColumns.KEY_LASTNAME, UserColumns.KEY_FIRSTNAME);
                    // selectionArgs = new String[] { queryString, queryString
                    // };
                    selection = String.format("%s MATCH ?", UserDatabase.TABLE_USER_FTS);
                    selectionArgs = new String[] { queryString };
                }
            }

            // Filter
            // Loader
            CursorLoader cursorLoader = new CursorLoader(getActivity(), UserProvider.Constants.CONTENT_URI, SEARCH_PROJECTION_COLOMN, selection, selectionArgs, sortOrder);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            // Display List
            listAdapter.swapCursor(cursor);
            // Display Counter
            int count = 0;
            if (cursor != null) {
                count = cursor.getCount();
            }
            if (count < 1) {
                // searchResultTextView.setText(R.string.search_no_results);
            } else {
                String countString = getResources().getQuantityString(R.plurals.search_results, count, new Object[] { count });
                // searchResultTextView.setText(countString);
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // searchResultTextView.setText(R.string.search_instructions);
            listAdapter.swapCursor(null);
        }

    };

    public void setOnSelectUserListener(OnSelectUserListener onSelectUserListener) {
        this.onSelectUserListener = onSelectUserListener;
    }

    public interface OnSelectUserListener {
        // void onSelectionChange() ;
        void onViewAction(Uri entityUri);

        void onCreateNewAction();

        void onEditAction(Uri entityUri);

        void onDeleteAction(Uri entityUri);

        void onFinishAction();

        // void onSelectUser(User user);

    }

    @Override
    public boolean onQueryTextSubmit(String query) { 
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        doSearch(newText);
        return true;
    }

}

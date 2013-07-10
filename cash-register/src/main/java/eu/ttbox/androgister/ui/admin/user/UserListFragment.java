package eu.ttbox.androgister.ui.admin.user;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import eu.ttbox.androgister.domain.UserDao;
import eu.ttbox.androgister.domain.UserDao.Properties;
import eu.ttbox.androgister.domain.UserDao.UserCursorHelper;
import eu.ttbox.androgister.domain.provider.UserProvider;
import eu.ttbox.androgister.widget.AutoScrollListView;

public class UserListFragment extends Fragment implements OnQueryTextListener {

    private static final String TAG = "UserListFragment";

    private static final int USER_LIST_LOADER = R.id.config_id_admin_user_list_loader_started;

    private static final String[] SEARCH_PROJECTION_COLOMN = new String[] { UserDao.Properties.Id.columnName, UserDao.Properties.Lastname.columnName, UserDao.Properties.Firstname.columnName,
            UserDao.Properties.Login.columnName };

    private static final String USER_SORT_DEFAULT = String.format("%s DESC, %s DESC", UserDao.Properties.Lastname.columnName, UserDao.Properties.Firstname.columnName);

    /**
     * The id for a delayed message that triggers automatic selection of the
     * first found contact in search mode.
     */
    private static final int MESSAGE_AUTOSELECT_FIRST_FOUND_CONTACT = 1;

    /**
     * The delay that is used for automatically selecting the first found
     * contact.
     */
    private static final int DELAY_AUTOSELECT_FIRST_FOUND_CONTACT_MILLIS = 500;

    // Dao
    private UserDao userDao;
    private UserCursorHelper helper;

    // Adapter
    private UserListAdapter listAdapter;

    // Config
    private boolean mSelectionToScreenRequested;
    private boolean mSmoothScrollRequested;

    // Binding
    private TextView searchResultTextView;
    private ListView listView;
    private EditText searchNameTextView;

    // Instance Data
    private Uri selectedEntityUri;
    private int mLastSelectedPosition = -1;

    // Listener
    private OnSelectUserListener onSelectUserListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_AUTOSELECT_FIRST_FOUND_CONTACT:
                selectDefaultContact();
                break;
            }
        }
    };

    private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            onListItemClick((ListView) parent, v, position, id);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_user_list, container, false);
        // Dao
        Context context = getActivity();
        AndroGisterApplication app = (AndroGisterApplication) context.getApplicationContext();
        userDao = app.getDaoSession().getUserDao();

        // Bind
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnItemClickListener(mOnClickListener);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setFastScrollEnabled(true);
        listView.setFastScrollAlwaysVisible(true);
        listView.setVerticalScrollbarPosition(0);
        listView.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);

        // listView.setOnItemSelectedListener(listener)
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
            AutoScrollListView listView = (AutoScrollListView) getListView();
            listView.requestPositionToScreen(selectedPosition + listView.getHeaderViewsCount(), mSmoothScrollRequested);
            mSelectionToScreenRequested = false;
        }
    }

    protected void selectDefaultContact() {
        Uri contactUri = null;
        UserListAdapter adapter = getAdapter();
        if (mLastSelectedPosition != -1) {
            int count = adapter.getCount();
            int pos = mLastSelectedPosition;
            if (pos >= count && count > 0) {
                pos = count - 1;
            }
            contactUri = adapter.getContactUri(pos);
        }

        if (contactUri == null) {
            contactUri = adapter.getFirstEntityUri();
        }

        setSelectedEntityUri(contactUri, false, mSmoothScrollRequested, false, false);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (onSelectUserListener != null) {
            Cursor cursor = (Cursor) l.getAdapter().getItem(position);
            if (cursor != null) {
                if (helper == null) {
                    helper = userDao.getCursorHelper(cursor);
                }
                String userId = helper.getId(cursor).toString();
                Uri uri = Uri.withAppendedPath(UserProvider.Constants.CONTENT_URI, userId);
                selectedEntityUri = uri;
                // User user = helper.getEntity(cursor);
                // Define result
                // TODO
                listView.requestFocusFromTouch();
                listView.setSelection(position);
                Log.i(TAG, "listView.setSelection = " + position);
                listAdapter.setSelectedId(Long.valueOf(userId));
                onSelectUserListener.onViewEntityAction(uri);
            }
        }
    }

    public Uri getSelectedEntityUri() {
        return selectedEntityUri;
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
                    selection = String.format("(%s like ? or %s like ?)", UserDao.Properties.Lastname.columnName, UserDao.Properties.Firstname.columnName);
                    selectionArgs = new String[] { queryString, queryString };
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
            listAdapter.changeCursor(cursor);
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
            listAdapter.changeCursor(null);
        }

    };

    public void setOnSelectUserListener(OnSelectUserListener onSelectUserListener) {
        this.onSelectUserListener = onSelectUserListener;
    }

    public interface OnSelectUserListener {
        // void onSelectionChange() ;
        void onViewEntityAction(Uri entityUri);

        void onCreateNewEntityAction();

        void onEditEntityAction(Uri entityUri);

        void onDeleteEntityAction(Uri entityUri);

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

    public void setSelectedEntityUri(Uri uri) {
        setSelectedEntityUri(uri, true, false /* no smooth scroll */, true, false);
    }

    private void setSelectedEntityUri(Uri uri, boolean required, boolean smoothScroll, boolean persistent, boolean willReloadData) {
        mSmoothScrollRequested = smoothScroll;
        mSelectionToScreenRequested = true;
        if ((selectedEntityUri == null && uri != null) || (selectedEntityUri != null && !selectedEntityUri.equals(uri))) {
            selectedEntityUri = uri;
            long selectedEntityId = parseSelectedEntityUri(uri);
            if (!willReloadData) {
                UserListAdapter adapter = getAdapter();
                if (adapter != null) {
                    adapter.setSelectedId(selectedEntityId);
                    getListView().invalidateViews();
                }
            }
        }
    }

    private long parseSelectedEntityUri(Uri uri) {
        String entityId = uri.getLastPathSegment();
        return Long.valueOf(entityId).longValue();
    }

    private UserListAdapter getAdapter() {
        return listAdapter;
    }
}

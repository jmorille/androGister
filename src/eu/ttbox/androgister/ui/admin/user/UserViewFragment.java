package eu.ttbox.androgister.ui.admin.user;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.UserProvider;
import eu.ttbox.androgister.database.user.UserHelper;

public class UserViewFragment extends Fragment {

    private static final String TAG = "UserViewFragment";

    private static final int LOADER_USER_DETAILS = R.id.config_id_admin_user_view_loader_started;

    // Bindings
    private TextView userFirstnameTextView, userLastnameTextView, userMatriculeTextView;

    // Instance Data
    private long userId = -1;
    private String userIdString = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_user_view, container, false);
        // View
        userLastnameTextView = (TextView) v.findViewById(R.id.user_lastname);
        userFirstnameTextView = (TextView) v.findViewById(R.id.user_firstname);
        userMatriculeTextView = (TextView) v.findViewById(R.id.user_matricule);
        return v;
    }
    
 
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        // Listener
        super.onPause();
    }

    public void doSearchUser(long userId) {
        this.userId = userId;
        this.userIdString = String.valueOf(userId);
        getLoaderManager().restartLoader(LOADER_USER_DETAILS, null, userLoaderCallback);
    }

    private final LoaderManager.LoaderCallbacks<Cursor> userLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String sortUser = null;
            String selection = null;
            String[] selectionArgs = null;
            // Loader
            Uri userUri = Uri.withAppendedPath(UserProvider.Constants.CONTENT_URI_GET_USER, userIdString);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), userUri, null, selection, selectionArgs, sortUser);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.d(TAG, "OnLoadCompleteListener for User");
            UserHelper helper = new UserHelper().initWrapper(cursor);
            // bind Values
            helper.setTextUserFirstname(userFirstnameTextView, cursor) //
                    .setTextUserLastname(userLastnameTextView, cursor)//
                    .setTextUserMatricule(userMatriculeTextView, cursor)//
            ;

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            TextView[] textViews = new TextView[] { userFirstnameTextView, userLastnameTextView, userMatriculeTextView };
            for (TextView textView : textViews) {
                textView.setText(null);
            }

        }

    };
}

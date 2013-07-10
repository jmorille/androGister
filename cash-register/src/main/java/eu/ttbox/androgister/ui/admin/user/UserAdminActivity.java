package eu.ttbox.androgister.ui.admin.user;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.provider.UserProvider;
import eu.ttbox.androgister.ui.admin.user.UserListFragment.OnSelectUserListener;

public class UserAdminActivity extends Activity {

    private static final String TAG = "UserAdminActivity";

    private UserListFragment userListFragment;
    private UserViewFragment userViewFragment;
    private SearchView searchView;

    private static final int SUBACTIVITY_NEW_CONTACT = 2;
    private static final int SUBACTIVITY_EDIT_CONTACT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user_activity);
        handleIntent(getIntent());
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof UserListFragment) {
            userListFragment = (UserListFragment) fragment;
            userListFragment.setOnSelectUserListener(onSelectUserListener);
        } else if (fragment instanceof UserViewFragment) {
            userViewFragment = (UserViewFragment) fragment;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_user_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        if (searchView != null) {
            searchView.setIconifiedByDefault(true);
            // searchView.setQueryHint(this.getString(R.string.hint_findContacts));
            searchView.setOnQueryTextListener(userListFragment);
            // searchView.setOnCloseListener(this);
            // searchView.setQuery(mQueryString, false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_admin_user_add: {
            // app icon in action bar clicked; go home

            return true;
        }
        case R.id.menu_admin_user_edit: {
            Uri entityUri = userListFragment.getSelectedEntityUri();
            actionEditEntity(entityUri);
            // if (userId != -1) {
            // showUserEditDialog(userId);
            // }
            return true;
        }
        case R.id.menu_admin_user_delete: {

            return true;
        }
        case R.id.menu_search: {
            onSearchRequested();
            return true;
        }
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    // protected void showUserEditDialog(long userId) {
    // final String dialogUserEdit = "dialogUserEdit";
    // FragmentTransaction ft = getFragmentManager().beginTransaction();
    // Fragment prev = getFragmentManager().findFragmentByTag(dialogUserEdit);
    // if (prev != null) {
    // ft.remove(prev);
    // }
    // ft.addToBackStack(null);
    //
    // // Create and show the dialog.
    // DialogFragment newFragment = UserEditFragment.newInstance(userId);
    // newFragment.show(ft, dialogUserEdit);
    // }

    private OnSelectUserListener onSelectUserListener = new OnSelectUserListener() {

        // @Override
        // public void onSelectionChange() {
        // if (userViewFragment!=null)
        // userViewFragment.loadUri(userListFragment.getSelectedUri());
        // }

        @Override
        public void onViewEntityAction(Uri entityUri) {
            actionViewEntity(entityUri);
        }

        @Override
        public void onCreateNewEntityAction() {
            actionNewEntity();
        }

        @Override
        public void onEditEntityAction(Uri entityUri) {
            actionEditEntity(entityUri);
        }

        @Override
        public void onDeleteEntityAction(Uri entityUri) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinishAction() {
            onBackPressed();
        }
    };

    private void actionViewEntity(Uri entityUri) {
        if (userViewFragment != null) {
            userViewFragment.loadUri(entityUri);
        }
    }

    private void actionEditEntity(Uri entityUri) {

        // Intent intent = new Intent(Intent.ACTION_EDIT, entityUri);
        Intent intent = new Intent(this, UserEditActivity.class).setAction(Intent.ACTION_EDIT).setData(entityUri);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.putExtra(UserEditActivity.INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED, true);
        startActivityForResult(intent, SUBACTIVITY_EDIT_CONTACT);
    }

    private void actionNewEntity() {
        Intent intent = new Intent(Intent.ACTION_INSERT, UserProvider.Constants.CONTENT_URI);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case SUBACTIVITY_NEW_CONTACT:
        case SUBACTIVITY_EDIT_CONTACT: {
            if (resultCode == RESULT_OK) {
                // mRequest.setActionCode(ContactsRequest.ACTION_VIEW_CONTACT);
                // userViewFragment.setSelectionRequired(true);
                userListFragment.setSelectedEntityUri(data.getData());
                // Suppress IME if in search mode
                // if (mActionBarAdapter != null) {
                // mActionBarAdapter.clearFocusOnSearchView();
                // }
                // No need to change the contact filter
                // mCurrentFilterIsValid = true;
            }
            break;
        }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

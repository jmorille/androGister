package eu.ttbox.androgister.ui.admin.user;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.model.user.User;
import eu.ttbox.androgister.ui.admin.user.UserListFragment.OnSelectUserListener;
import eu.ttbox.androgister.ui.config.MyPreferencesActivity;

public class UserAdminActivity extends Activity {

    private static final String TAG = "UserAdminActivity";

    private UserListFragment userListFragment;
    private UserViewFragment userViewFragment;
    
    private long userId = -1;
    
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
            userListFragment.setOnSelectUserListener(new OnSelectUserListener() {
                
                @Override
                public void onSelectUser(User user) {
                 long selectUserId = user.id;
                 userId = selectUserId;
                   if (userViewFragment!=null) {
                       userViewFragment.doSearchUser(selectUserId);
                   }
                    
                }
            });
        }else if (fragment instanceof UserViewFragment) {
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
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_admin_user_add:{
			// app icon in action bar clicked; go home
		 
			return true;
		}
	    case R.id.menu_admin_user_edit: {
	    	if (userId != -1) {
	    		showUserEditDialog(userId);
	    	}
	    	return true;		
	    }
	    case R.id.menu_admin_user_delete: {

	    	return true;		
	    }
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	protected void showUserEditDialog(long userId) {
		final String dialogUserEdit = "dialogUserEdit";
		FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag(dialogUserEdit);
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
 
	    // Create and show the dialog.
	    DialogFragment newFragment = UserEditFragment.newInstance(userId);
	    newFragment.show(ft,dialogUserEdit);
	}

	
}

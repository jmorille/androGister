package eu.ttbox.androgister.ui.admin.user;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.model.user.User;
import eu.ttbox.androgister.ui.admin.user.UserListFragment.OnSelectUserListener;

public class UserAdminActivity extends Activity {

    private static final String TAG = "UserAdminActivity";

    private UserListFragment userListFragment;
    private UserViewFragment userViewFragment;
    
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
                   if (userViewFragment!=null) {
                       userViewFragment.doSearchUser(user.id);
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
}

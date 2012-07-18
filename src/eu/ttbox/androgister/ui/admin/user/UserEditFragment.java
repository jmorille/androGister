package eu.ttbox.androgister.ui.admin.user;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.UserProvider;
import eu.ttbox.androgister.database.user.UserHelper;

public class UserEditFragment extends Fragment {

    private static final String TAG = "UserEditFragment";

    private static final int REQUEST_CODE = 435;
    private static final int LOADER_USER_DETAILS = R.string.config_id_admin_user_edit_loader_started;

    // Bindings
    private EditText userFirstnameTextView, userLastnameTextView, userMatriculeTextView;
    private ImageView imageView;
    
    // Instance Data
    private long userId = -1;
    private String userIdString = null;
  
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_user_edit, container, false);
        // View
        userLastnameTextView = (EditText) v.findViewById(R.id.user_lastname);
        userFirstnameTextView = (EditText) v.findViewById(R.id.user_firstname);
        userMatriculeTextView = (EditText) v.findViewById(R.id.user_matricule);
//        imageView = (ImageView) v.findViewById(R.id.user_image);
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
    
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
        
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try {
                InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap bm = BitmapFactory.decodeStream(is);
                is.close();
                imageView.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            
        }
        
        
        super.onActivityResult(requestCode, resultCode, data);
    }
}

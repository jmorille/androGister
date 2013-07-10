package eu.ttbox.androgister.ui.admin.user;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R; 
import eu.ttbox.androgister.domain.UserDao;
import eu.ttbox.androgister.domain.UserDao.UserCursorHelper;
import eu.ttbox.androgister.domain.provider.UserProvider;

public class UserEditFragment extends  Fragment {

	private static final String TAG = "UserEditFragment";

	private static final String USER_ID = "userId";

	private static final int REQUEST_CODE = 435;
	private static final int LOADER_DATA = R.id.config_id_admin_user_edit_loader_started;

	// Dao
	private  UserDao userDao;
	  
	// Bindings
	private EditText userFirstnameTextView, userLastnameTextView, userMatriculeTextView;
	private ImageView imageView;

	// Instance Data
    private Uri mLookupUri;
    private String mAction;
	private Status mStatus ;

	// Listener
	private UserEditListener userEditListener;
	
	static UserEditFragment newInstance(long userId) {
		UserEditFragment f = new UserEditFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putLong(USER_ID, userId);
		f.setArguments(args);

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.admin_user_edit, container, false);
		// Dao 
		Context context = getActivity();
        AndroGisterApplication app = (AndroGisterApplication) context.getApplicationContext();
          userDao = app.getDaoSession().getUserDao();
		// View
		userLastnameTextView = (EditText) v.findViewById(R.id.user_lastname);
		userFirstnameTextView = (EditText) v.findViewById(R.id.user_firstname);
		userMatriculeTextView = (EditText) v.findViewById(R.id.user_matricule);
		// imageView = (ImageView) v.findViewById(R.id.user_image);
		if (getArguments() != null) {
			long userId = getArguments().getLong(USER_ID);
			String userIdString = String.valueOf(userId);
			Uri userUri = Uri.withAppendedPath(UserProvider.Constants.CONTENT_URI , "/"+ userIdString);
			load("", userUri, null);
		}
		// Title

		return v;
	}

	 

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

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

//	public void doSearchUser(long userId) {
//		this.userId = userId;
//		this.userIdString = String.valueOf(userId);
//		Log.i(TAG, "do Search User " + userIdString);
//		getLoaderManager().restartLoader(LOADER_DATA, null, userLoaderCallback);
//	}

	private final LoaderManager.LoaderCallbacks<Cursor> userLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String sortUser = null;
			String selection = null;
			String[] selectionArgs = null;
			// Loader 
			CursorLoader cursorLoader = new CursorLoader(getActivity(), mLookupUri, null, selection, selectionArgs, sortUser);
			return cursorLoader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			Log.d(TAG, "OnLoadCompleteListener for User");
			UserCursorHelper helper = userDao.getCursorHelper(cursor);
			// bind Values
			helper.setTextFirstname(userFirstnameTextView, cursor) //
					.setTextLastname(userLastnameTextView, cursor)//
					.setTextLogin(userMatriculeTextView, cursor)//
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
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			try {
				InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
				Bitmap bm = BitmapFactory.decodeStream(is);
				is.close();
				imageView.setImageBitmap(bm);
			} catch (FileNotFoundException e) {
				Log.e(TAG, "onActivityResult pickImage FileNotFoundException : " + e.getMessage(), e);
			} catch (IOException e) {
				Log.e(TAG, "onActivityResult pickImage IOException : " + e.getMessage(), e);
			} 
		} 
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public static interface UserEditListener {
        void onEntityNotFound();
        /**
         * User has tapped Revert, close the fragment now.
         */
        void onReverted();

        /**
         * Contact was saved and the Fragment can now be closed safely.
         */
        void onSaveFinished(Intent resultIntent);

        /**
         * User switched to editing a different contact (a suggestion from the
         * aggregation engine).
         */
        void onEditOtherEntityRequested(
                Uri contactLookupUri, ArrayList<ContentValues> contentValues);
  
    }

	 /**
     * Modes that specify what the AsyncTask has to perform after saving
     */
    public enum SaveMode {
        /**
         * Close the editor after saving
         */
        CLOSE,

        /**
         * Reload the data so that the user can continue editing
         */
        RELOAD ,

        /**
         * Navigate to Contacts Home activity after saving.
         */
       HOME ;
    }
    

    private enum Status { 
        LOADING, EDITING , SAVING , CLOSING , SUB_ACTIVITY;
    }
    

    
	public void load(String action, Uri lookupUri, Bundle extras) {
		mAction = action;
        mLookupUri = lookupUri;
		getLoaderManager().restartLoader(LOADER_DATA, null, userLoaderCallback);
		
	}

	public void setListener(UserEditListener userEditListener) {
		this.userEditListener = userEditListener;
		
	}
	public boolean save(SaveMode saveMode) {
		  // If we are about to close the editor - there is no need to refresh the data
        if (saveMode == SaveMode.CLOSE ) {
            getLoaderManager().destroyLoader(LOADER_DATA);
        }

        mStatus = Status.SAVING;
        
        return true;
	}
}

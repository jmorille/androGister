package eu.ttbox.androgister.ui.admin.user;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;  
import eu.ttbox.androgister.domain.UserDao;
import eu.ttbox.androgister.domain.UserDao.UserCursorHelper;
import eu.ttbox.androgister.domain.provider.UserProvider;

public class UserListAdapter extends ResourceCursorAdapter {

	private static final String TAG = "UserListAdapter";

	private UserCursorHelper helper;
  
	private long selectedId = -1;

	public UserListAdapter(Context context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
		  // Init Dao
        AndroGisterApplication app = (AndroGisterApplication) context.getApplicationContext();
        UserDao userDao = app.getDaoSession().getUserDao();
        helper  =userDao.getCursorHelper(c);
	}

	public void setSelectedId(long selectedEntity) {
		this.selectedId = selectedEntity;
		notifyDataSetChanged();
	}

 

	public Uri getContactUri(int position) {
		Cursor item = (Cursor) getItem(position);
		return item != null ? getEntityUri(item) : null;
	}

	public Uri getEntityUri(Cursor cursor) {
		String contactId = helper.getId(cursor).toString();
		Uri uri = Uri.withAppendedPath(UserProvider.Constants.CONTENT_URI, contactId);
		return uri;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (helper.isNotInit) {
			helper.initWrapper(cursor);
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		// Bind Value
		helper.setTextLastname(holder.lastnameText, cursor)//
				.setTextFirstname(holder.firstnameText, cursor)//
				.setTextLogin(holder.matriculeText, cursor);
		// String
		long currentId = helper.getId(cursor);
		boolean selected = currentId == selectedId;
		Log.i(TAG, "Is selected for userid = " + currentId + " ==> " + selected);
		view.setSelected(selected);
		view.setActivated(selected);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = super.newView(context, cursor, parent);
		// Then populate the ViewHolder
		ViewHolder holder = new ViewHolder();
		holder.firstnameText = (TextView) view.findViewById(R.id.user_list_item_firstname);
		holder.lastnameText = (TextView) view.findViewById(R.id.user_list_item_lastname);
		holder.matriculeText = (TextView) view.findViewById(R.id.user_list_item_matricule);
		// and store it inside the layout.
		view.setTag(holder);
		return view;

	}

	static class ViewHolder {
		TextView firstnameText;
		TextView lastnameText;
		TextView matriculeText;
	}

	public Uri getFirstEntityUri() {
		Cursor cursor = (Cursor) getItem(0);
		if (cursor == null || !cursor.moveToFirst()) {
			return null;
		}
		 
		return getEntityUri(cursor);
	}
}

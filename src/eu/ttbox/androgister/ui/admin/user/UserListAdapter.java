package eu.ttbox.androgister.ui.admin.user;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.UserProvider;
import eu.ttbox.androgister.database.user.UserHelper;

public class UserListAdapter extends ResourceCursorAdapter {

    private UserHelper helper;

    private boolean isNotBinding = true;

    private long selectedId = -1;
    
    public UserListAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }
 
    public void setSelectedId(long selectedEntity) {
		this.selectedId = selectedEntity; 
		notifyDataSetChanged();
	}
 

	private void intViewBinding(View view, Context context, Cursor cursor) {
        // Init Cursor
        helper = new UserHelper().initWrapper(cursor);
        isNotBinding = false;
 
    }

    public Uri getContactUri(int position) { 
        Cursor item = (Cursor)getItem(position);
        return item != null ? getEntityUri(item) : null;
    }
    
    public Uri getEntityUri( Cursor cursor) {
    	String contactId = helper.getUserIdAsString(cursor);
    	Uri uri =Uri.withAppendedPath(UserProvider.Constants.CONTENT_URI, contactId); 
    	return uri;
    }
    
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (isNotBinding) {
            intViewBinding(view, context, cursor);
        }
        ViewHolder holder = (ViewHolder)view.getTag(); 
        // Bind Value
        helper.setTextUserLastname(holder.lastnameText, cursor)//
                .setTextUserFirstname(holder.firstnameText, cursor)//
                .setTextUserMatricule(holder.matriculeText, cursor);
        // String
       long currentId =  helper.getUserId (cursor);
       boolean selected = currentId==selectedId;

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
}

package eu.ttbox.androgister.ui.person;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.PersonDao;
import eu.ttbox.androgister.domain.PersonDao.PersonCursorHelper;

public class PersonListAdapter extends ResourceCursorAdapter {

//    private PersonHelper helper;
//
//    private boolean isNotBinding = true;

    private PersonCursorHelper helper;
    
    
    public PersonListAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
        // Init Dao
        AndroGisterApplication app = (AndroGisterApplication) context.getApplicationContext();
        PersonDao personDao = app.getDaoSession().getPersonDao();
        // Init Cursor
        helper = personDao.getCursorHelper(c); 
    }

 

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (helper.isNotInit) {
            helper.initWrapper(cursor);
        }
        ViewHolder holder = (ViewHolder)view.getTag(); 
        // Bind Value
        helper.setTextLastname(holder.lastnameText, cursor)//
                .setTextFirstname(holder.firstnameText, cursor)//
                .setTextMatricule(holder.matriculeText, cursor);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = super.newView(context, cursor, parent);
       // Then populate the ViewHolder 
        ViewHolder holder = new ViewHolder();
        holder.firstnameText = (TextView) view.findViewById(R.id.person_list_item_firstname);
        holder.lastnameText = (TextView) view.findViewById(R.id.person_list_item_lastname);
        holder.matriculeText = (TextView) view.findViewById(R.id.person_list_item_matricule);
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

package eu.ttbox.androgister.ui.person;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.product.PersonHelper;

public class PersonListAdapter extends ResourceCursorAdapter {

    private PersonHelper helper;

    private boolean isNotBinding = true;

    public PersonListAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    private void intViewBinding(View view, Context context, Cursor cursor) {
        // Init Cursor
        helper = new PersonHelper().initWrapper(cursor);
        isNotBinding = false;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (isNotBinding) {
            intViewBinding(view, context, cursor);
        }
        ViewHolder holder = (ViewHolder)view.getTag(); 
        // Bind Value
        helper.setTextPersonLastname(holder.lastnameText, cursor)//
                .setTextPersonFirstname(holder.firstnameText, cursor)//
                .setTextPersonMatricule(holder.matriculeText, cursor);

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

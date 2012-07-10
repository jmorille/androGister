package eu.ttbox.androgister.ui.person;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
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
		// Bind View
		TextView firstnameText = (TextView) view.findViewById(R.id.person_list_item_firstname);
		TextView lastnameText = (TextView) view.findViewById(R.id.person_list_item_lastname);
		TextView matriculeText = (TextView) view.findViewById(R.id.person_list_item_matricule);
		// Bind Value
		helper.setTextPersonLastname(lastnameText, cursor)//
				.setTextPersonFirstname(firstnameText, cursor).setTextPersonMatricule(matriculeText, cursor);

	}
}

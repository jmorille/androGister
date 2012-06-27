package eu.ttbox.androgister.ui.register;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import eu.ttbox.androgister.R;

public class BasketItemAdapter extends ArrayAdapter<String> {

	private Context context;

	public BasketItemAdapter(Context context, List<String> objects) {
		super(context, R.layout.basket_list_item, android.R.id.text1 , objects);
		this.context = context;
	}

}

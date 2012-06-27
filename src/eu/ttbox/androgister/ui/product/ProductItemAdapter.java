package eu.ttbox.androgister.ui.product;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class ProductItemAdapter  extends SimpleCursorAdapter  {

	private static final String TAG = "ProductItemAdapter";

 
	public ProductItemAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags); 
	}

	
	
	
	
}

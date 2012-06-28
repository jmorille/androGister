package eu.ttbox.androgister.ui.register;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.model.PriceHelper;
import eu.ttbox.androgister.model.Product;

public class BasketItemAdapter extends ArrayAdapter<Product> {

	private Context context;
	private LayoutInflater mInflater;

	public BasketItemAdapter(Context context, List<Product> objects) {
		super(context, R.layout.basket_list_item, objects);
		this.context = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) { 
		View view; 
		if (convertView == null) {
			view = mInflater.inflate( R.layout.basket_list_item, parent, false);
 		} else {
			view = convertView;
		}
		Product data = getItem(position);
		bindView(view, context, data);
		return  view;
	}

	 public void  bindView(View view, Context context, Product data) {
		 TextView nameTexView =  (TextView)view.findViewById(R.id.basket_list_item_article);
		 TextView priceTexView =  (TextView)view.findViewById(R.id.basket_list_item_price);
		 nameTexView.setText(data.getName());
		 long price = data.getPriceHT();
		 String priceString = null;
		 if (price>-1) {
			 priceString = PriceHelper.getToStringPrice(price);
		 }
 		 priceTexView.setText(priceString);
	 }
	 

}

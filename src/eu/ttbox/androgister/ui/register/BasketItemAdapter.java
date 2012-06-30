package eu.ttbox.androgister.ui.register;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.model.OrderItem;
import eu.ttbox.androgister.model.PriceHelper;

public class BasketItemAdapter extends ArrayAdapter<OrderItem> {

	private Context context;
	private LayoutInflater mInflater;

	public BasketItemAdapter(Context context, List<OrderItem> objects) {
		super(context, R.layout.register_basket_list_item, objects);
		this.context = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.register_basket_list_item, parent, false);
		} else {
			view = convertView;
		}
		OrderItem data = getItem(position);
		bindView(view, context, data);
		return view;
	}

	public void bindView(View view, Context context, OrderItem data) {
		// Bind
		TextView quantityTextView = (TextView) view.findViewById(R.id.basket_list_item_quantity);
		TextView nameTextView = (TextView) view.findViewById(R.id.basket_list_item_name);
		TextView priceTextView = (TextView) view.findViewById(R.id.basket_list_item_price);
		// Set value
		quantityTextView.setText(String.valueOf(data.getQuantity()));
		nameTextView.setText(data.getName());
		long price = data.getPriceSumHT();
		String priceString = null;
		if (price > -1) {
			priceString = PriceHelper.getToStringPrice(price);
		}
		priceTextView.setText(priceString);
	}

}

package eu.ttbox.androgister.ui.product;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.model.Article;
import eu.ttbox.androgister.ui.register.ProductRegisterFragment;

public class ProductSelectorFragment extends ListFragment {

	String[] month = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListAdapter myListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, month);
		setListAdapter(myListAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.product_selector, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
//		ProductRegisterFragment frag = (ProductRegisterFragment) getFragmentManager().findFragmentById(R.id.fragment_product_register);
//		if (frag != null && frag.isInLayout()) {
//			frag.setText(getCapt(item));
//		}
		Article status = new Article().setName(item);
		getActivity().sendBroadcast(Intents.status(status));
//		Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
	}

	private String getCapt(String ship) {
		return ship;
	}

}

package eu.ttbox.androgister.ui.register;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.model.Offer;
import eu.ttbox.androgister.model.Order;
import eu.ttbox.androgister.model.OrderItem;
import eu.ttbox.androgister.model.OrderItemHelper;
import eu.ttbox.androgister.model.PriceHelper;

public class RegisterBasketFragment extends Fragment {

    private static final String TAG = "RegisterBasketFragment";

    // Binding
    private TextView sumTextView;
    private ListView listView;

    // Listener
    private OnBasketSunUpdateListener onBasketSunUpdateListener;

    // Data Instance
    private ArrayList<OrderItem> basket = new ArrayList<OrderItem>();
    private BasketItemAdapter listAdapter;
    private long basketSum = 0;

    private final OnItemLongClickListener mOnLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            return onListItemLongClick((ListView) parent, view, position, id);
        }

    };

    public void setOnBasketSunUpdateListener(OnBasketSunUpdateListener onBasketSunUpdateListener) {
        this.onBasketSunUpdateListener = onBasketSunUpdateListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Adpater
        listAdapter = new BasketItemAdapter(getActivity(), basket);
        // Services
        // mStatusReceiver = new StatusReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_basket, container, false);
        // Bind
        listView = (ListView) view.findViewById(R.id.basket_screen_list);
        listView.setOnItemLongClickListener(mOnLongClickListener);
        listView.setAdapter(listAdapter);
        // View
        sumTextView = (TextView) view.findViewById(R.id.basket_screen_sum);
        // Compute Initila Values
        setTextSum(getComputeBasketSum());
        // executor.execute(doBasketSum);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setTextSum(long sumPrice) {
        this.basketSum = sumPrice;
        sumTextView.setText(PriceHelper.getToStringPrice(sumPrice));
        if (onBasketSunUpdateListener != null) {
            onBasketSunUpdateListener.onBasketSum(sumPrice);
        }
    }

    private long getComputeBasketSum() {
        return getComputeBasketSum(basket);
    }

    private long getComputeBasketSum(ArrayList<OrderItem> items) {
        long sum = 0;
        if (items != null && !items.isEmpty()) {
            for (OrderItem item : items) {
                sum += item.getPriceSumHT();
            }
        }
        return sum;
    }

    public void onAddBasketItem(Offer offer) {
        OrderItem item = OrderItemHelper.createFromProduct(offer);
        onAddBasketItem(item);
    }

    public void onAddBasketItem(OrderItem item) {
        listAdapter.add(item);
        setTextSum(this.basketSum + item.getPriceSumHT());
        // executor.execute(doBasketSum);
    }

    public void onRemoveBasketItem(OrderItem item) {
        listAdapter.remove(item);
        setTextSum(this.basketSum - item.getPriceSumHT());
        // executor.execute(doBasketSum);
    }

    protected boolean onListItemLongClick(ListView list, View view, int position, long id) {
        OrderItem item = (OrderItem) listAdapter.getItem(position);
        onRemoveBasketItem(item);
        return true;
    }

    public void clearBasket() {
        basket.clear();
        listAdapter.notifyDataSetChanged();
        setTextSum(getComputeBasketSum());
        // executor.execute(doBasketSum);
    }

    public boolean isCurrentBasket() {
        boolean isABasket = !basket.isEmpty();
        return isABasket;
    }

    public void askToSaveBasketToOrder() {
        Log.i(TAG, "Ask to save Basket to Order");
        if (isCurrentBasket()) {
            // Get Clone of Basket Items
            ArrayList<OrderItem> items = new ArrayList<OrderItem>(basket);
            long sumBasket = getComputeBasketSum(items);
            // Prepare Object
            Order order = new Order();
            order.setItems(items);
            order.setPriceSumHT(sumBasket);
            // Validate Order
            // TODO
            // Save It
            Log.i(TAG, "Ask to save Basket to Order with " + items.size() + " Items");
            getActivity().startService(Intents.saveOrder(getActivity(),order));
            // getActivity().getContentResolver().insert(O, values)
            // Temporay Del
            clearBasket();
        }
    }

    public interface OnBasketSunUpdateListener {
        void onBasketSum(long sum);
    }

}

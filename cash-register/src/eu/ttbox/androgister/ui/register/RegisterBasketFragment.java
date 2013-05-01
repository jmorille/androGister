package eu.ttbox.androgister.ui.register;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.core.PriceHelper;
import eu.ttbox.androgister.domain.Order;
import eu.ttbox.androgister.domain.OrderItem;
import eu.ttbox.androgister.domain.PersonDao;
import eu.ttbox.androgister.domain.ref.OrderPaymentModeEnum;
import eu.ttbox.androgister.service.OrderService;
import eu.ttbox.androgister.ui.person.PersonListActivity;

public class RegisterBasketFragment extends Fragment {

    private static final String TAG = "RegisterBasketFragment";

    private static final int SELECT_PERSON_REQUEST_CODE = 111;
    private static final int SELECT_PERSON_REQUEST_CODE_ON_SAVE_BASKET = 112;

    // Binding
    private TextView sumTextView, personFirstnameTextView, personLastnameTextView, personMatriculeTextView;
    private ListView listView;

    // Listener
    private OnBasketSunUpdateListener onBasketSunUpdateListener;
    private BasketItemAdapter listAdapter;

    // Data Instance
    private SparseArray<OrderItem> cacheOrderItemByProductId = new SparseArray<OrderItem>();
    private ArrayList<OrderItem> basket = new ArrayList<OrderItem>();
    private long basketSum = 0;

    // Instance
    private Order order;

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
       
     // Restore States
        Log.d(TAG, "Restore onCreate savedInstanceState: " + savedInstanceState);
        if (savedInstanceState != null) {
            order = savedInstanceState.getParcelable(Intents.EXTRA_ORDER);
            basket = savedInstanceState.getParcelableArrayList(Intents.EXTRA_ORDER_ITEMS);
            Log.d(TAG, "Restore Order : " + order);
            Log.d(TAG, "Restore basket : " + (basket == null ? 0 : basket.size()) + " Items");
        }
        // Adpater
        listAdapter = new BasketItemAdapter(getActivity(), basket);

        // Services
        // mStatusReceiver = new StatusReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_basket, container, false);
        Log.d(TAG, "Restore onCreateView savedInstanceState: " + savedInstanceState);
        // Bind
        listView = (ListView) view.findViewById(R.id.basket_screen_list);
        listView.setOnItemLongClickListener(mOnLongClickListener);
        listView.setAdapter(listAdapter);
        // View
        sumTextView = (TextView) view.findViewById(R.id.basket_screen_sum);
        // View Person
        personFirstnameTextView = (TextView) view.findViewById(R.id.basket_screen_person_firstname);
        personLastnameTextView = (TextView) view.findViewById(R.id.basket_screen_person_lastname);
        personMatriculeTextView = (TextView) view.findViewById(R.id.basket_screen_person_matricule);
        // Compute Initila Values
        setTextSum(getComputeBasketSum());
        // executor.execute(doBasketSum);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
         outState.putParcelable(Intents.EXTRA_ORDER, order);
        outState.putParcelableArrayList(Intents.EXTRA_ORDER_ITEMS, basket);
        Log.d(TAG, "Save onSaveInstanceState : " + outState);
        
     }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(new Intent(getActivity(), OrderService.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        setPersonData(order);
        Log.i(TAG, "###  onResume with order " + order);

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

    public void onAddBasketItem(OrderItem item) {
        listAdapter.add(item);
        // addOrIncrementItem(item);
        setTextSum(this.basketSum + item.getPriceSumHT());
    }

    private void addOrIncrementItem(OrderItem item) {
        boolean isIncrement = false;
        // Could cast
        int productId = (int) item.getProductId().intValue();
        OrderItem order = cacheOrderItemByProductId.get(productId);
        if (order != null) {
            if (order.getPriceUnitHT() == item.getPriceUnitHT()) {
                order.setQuantity(order.getQuantity() + item.getQuantity());
                isIncrement = true;
            }
        } else {
            cacheOrderItemByProductId.put(productId, item);
        }

        if (isIncrement) {
            listAdapter.notifyDataSetChanged();
        } else {
            listAdapter.add(item);
        }
    }

    public void onRemoveBasketItem(OrderItem item) {
        listAdapter.remove(item);
        setTextSum(this.basketSum - item.getPriceSumHT());
    }

    protected boolean onListItemLongClick(ListView list, View view, int position, long id) {
        OrderItem item = (OrderItem) listAdapter.getItem(position);
        onRemoveBasketItem(item);
        return true;
    }

    public void clearBasket() {
        // List Items
        basket.clear();
        cacheOrderItemByProductId.clear();
        listAdapter.notifyDataSetChanged();
        // Sum
        setTextSum(getComputeBasketSum());
        // Order Data
        order = null;
        setPersonData(null);
    }

    public boolean isCurrentBasket() {
        return (order != null) || (!basket.isEmpty());
    }

    public boolean askToSaveBasketToOrder(OrderPaymentModeEnum paymentMode) {
        Log.i(TAG, "Ask to save Basket to Order " + paymentMode);
        boolean isValid = false;
        if (isCurrentBasket()) {
            // Get Clone of Basket Items
            ArrayList<OrderItem> items = new ArrayList<OrderItem>(basket);
            long sumBasket = getComputeBasketSum(items);
            // Prepare Object
            Order order = getOrder();
            // order.setItems(items);
            order.setPriceSumHT(sumBasket);

            order.setPaymentMode(paymentMode);
            // Validate Order
            isValid = isValidOrder(order);
            if (isValid) {
                // Save It
                Log.i(TAG, "Ask to save Basket to Order with " + items.size() + " Items");
                getActivity().startService(Intents.saveOrder(getActivity(), order, basket));
                clearBasket();
            }
        }
        return isValid;
    }

    private boolean isValidOrder(Order order) {
        boolean isValid = true;
        if (order.getPaymentMode() == null) {
            isValid = false;
            return isValid;
        } else if (OrderPaymentModeEnum.CASH == order.getPaymentMode()) {
            return true;
        } else if (OrderPaymentModeEnum.CREDIT == order.getPaymentMode()) {
            if (order.getPersonId() == null) {
                askOpenSelectPersonList(SELECT_PERSON_REQUEST_CODE_ON_SAVE_BASKET);
                return false;
            }
        }

        return isValid;
    }

    public interface OnBasketSunUpdateListener {
        void onBasketSum(long sum);
    }

    private Order getOrder() {
        if (order == null) {
            order = new Order();
        }
        return order;
    }

    private void setPersonData(Order order) { 
        // Define Text
        String personFirstname = null;
        String personLastname = null;
        String personMatricule = null;
        if (order != null) {
            personFirstname = order.getPersonFirstname();
            personLastname = order.getPersonLastname();
            personMatricule = order.getPersonMatricule();
        }
        personFirstnameTextView.setText(personFirstname);
        personLastnameTextView.setText(personLastname);
        personMatriculeTextView.setText(personMatricule);
        this.order = order;
    }

    public void askOpenSelectPersonList() {
        askOpenSelectPersonList(SELECT_PERSON_REQUEST_CODE);
    }

    private void askOpenSelectPersonList(int requestCode) {
        Intent intent = new Intent(getActivity(), PersonListActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "#### onActivityResult " + data);
        // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (SELECT_PERSON_REQUEST_CODE == requestCode || SELECT_PERSON_REQUEST_CODE_ON_SAVE_BASKET == requestCode) {
                // Person person = (Person)
                // data.getSerializableExtra(Intents.EXTRA_PERSON);
                Bundle extras = data.getExtras();
                Order localOrder = null;
                if (extras != null && !extras.isEmpty()) {
                    localOrder = getOrder()//
                            .withPersonId(extras.getLong(PersonDao.Properties.Id.columnName))//
                            .withPersonMatricule(extras.getString(PersonDao.Properties.Lastname.columnName))//
                            .withPersonLastname(extras.getString(PersonDao.Properties.Firstname.columnName))//
                            .withPersonFirstname(extras.getString(PersonDao.Properties.Matricule.columnName));
                }
                setPersonData(localOrder);
                if (SELECT_PERSON_REQUEST_CODE_ON_SAVE_BASKET == requestCode) {
                    askToSaveBasketToOrder(order.getPaymentMode());
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

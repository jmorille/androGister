package eu.ttbox.androgister.ui.register;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.domain.ref.OrderPaymentModeEnum;
import eu.ttbox.androgister.ui.order.OrderListActivity;

public class RegisterKeyboardFragment extends Fragment {

    private static final String TAG = "RegisterKeyboardFragment";

    private Button saveCashButton, saveCreditButton, orderConsultButton, personListButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_keyboard, container, false);
        saveCashButton = (Button) view.findViewById(R.id.register_kb_save_basket_cash);
        saveCashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().sendBroadcast(Intents.saveBasket(OrderPaymentModeEnum.CASH));
            }
        });
        saveCreditButton = (Button) view.findViewById(R.id.register_kb_save_basket_credit);
        saveCreditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().sendBroadcast(Intents.saveBasket(OrderPaymentModeEnum.CREDIT));
            }
        });
        // Order Consult
        orderConsultButton = (Button) view.findViewById(R.id.register_kb_order_consult);
        orderConsultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickViewOrderDetail(v);
            }
        });
        // Person
        personListButton = (Button) view.findViewById(R.id.register_kb_person_view_list);
        personListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickViewPersonList(v);
            }

        });
        return view;
    }

    public void onClickViewOrderDetail(View v) {
        Intent intent = new Intent(getActivity(), OrderListActivity.class);
        startActivity(intent);
    }

    private void onClickViewPersonList(View v) {
        getActivity().sendBroadcast(Intents.askSelectPersonDialog());
    }

}

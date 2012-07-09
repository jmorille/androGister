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
import eu.ttbox.androgister.ui.order.OrderListActivity;

public class RegisterKeyboardFragment extends Fragment {

    private Button validateButton, orderConsultButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_keyboard, container, false);
        validateButton = (Button) view.findViewById(R.id.register_kb_validate);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickValidate(v);
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
        return view;
    }

    public void onClickValidate(View v) {
        getActivity().sendBroadcast(Intents.saveBasket());
    }

    public void onClickViewOrderDetail(View v) {
        Intent intent = new Intent(getActivity(), OrderListActivity.class);
        startActivity(intent);
    }

}

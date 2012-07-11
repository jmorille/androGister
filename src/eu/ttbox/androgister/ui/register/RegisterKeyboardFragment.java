package eu.ttbox.androgister.ui.register;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.core.Intents;
import eu.ttbox.androgister.model.Person;
import eu.ttbox.androgister.ui.order.OrderListActivity;
import eu.ttbox.androgister.ui.person.PersonListActivity;

public class RegisterKeyboardFragment extends Fragment {

    private static final String TAG = "RegisterKeyboardFragment";

    private static final int SELECT_PERSON_REQUEST_CODE = 111;
    
    private Button validateButton, orderConsultButton, personListButton;

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

    public void onClickValidate(View v) {
        getActivity().sendBroadcast(Intents.saveBasket());
    }

    public void onClickViewOrderDetail(View v) {
        Intent intent = new Intent(getActivity(), OrderListActivity.class);
        startActivity(intent);
    }

    private void onClickViewPersonList(View v) {
        Intent intent = new Intent(getActivity(), PersonListActivity.class);
        startActivityForResult(intent, SELECT_PERSON_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "#### onActivityResult " + data);
        // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_PERSON_REQUEST_CODE) {
            getActivity().sendBroadcast(data);
            Person person = (Person) data.getSerializableExtra(Intents.EXTRA_PERSON);
            String msg = "person " + person.getLastname() + " " + person.getFirstname() + " / id : " + person.getId();
            Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

}

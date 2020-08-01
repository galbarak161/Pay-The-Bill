package app.paythebill;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatDialogFragment;

public class OrderListDialog extends AppCompatDialogFragment{
    GridView ordersGrid;
    Order[] ordersArr;
    View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Init the dialog window
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.orderlist_dialog,null); // Edit
        builder.setView(view);

        // Init the orders list grid view
        ordersArr = (Order[]) getArguments().getSerializable("Orders_Array"); // Get the orders
        ordersGrid = view.findViewById(R.id.Diners_CheckBox_Grid); // init GridView
        DinersCustomAdapter customAdapter = new DinersCustomAdapter(view.getContext(), dinersArr, orderToEdit); // Create adapter for checkBox
        dinersGrid.setAdapter(customAdapter); // Set the GridView according to the adapter

        // Create buttons click_events
        builder.setTitle("רשימת מנות")
                .setPositiveButton("סגור", null)
                .setIcon(android.R.drawable.ic_input_get);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (OrderDialogListener) context;
        } catch (ClassCastException e){
            e.printStackTrace();
            throw  new ClassCastException(context.toString());
        }

    }

    private Order GetOrdersDetails() {
        Order o = new Order();
        TextView txt;
        GridView checkBoxGrid = view.findViewById(R.id.Diners_CheckBox_Grid);

        // Get the name of the order
        txt = view.findViewById(R.id.DishName_txt);
        o.Name = txt.getText().toString();

        // Get the price of the order
        txt = view.findViewById(R.id.DishPrice_txt);
        String tempNumString = txt.getText().toString();
        if(tempNumString.matches(""))
            o.Price = 0;
        else
            o.Price = Double.parseDouble(tempNumString);

        // Get the diners who ordered the order
        o.Diners.clear();
        for(int i=0; i<checkBoxGrid.getChildCount(); i++) {
            View v = checkBoxGrid.getChildAt(i);
            if (v instanceof LinearLayout) {
                LinearLayout checkBoxRow = (LinearLayout) v;
                for (int j = 0; j < checkBoxRow.getChildCount(); j++) {
                    v = checkBoxRow.getChildAt(j);
                    if (v instanceof CheckBox && ((CheckBox) v).isChecked()) {
                        String dinerName = ((CheckBox) v).getText().toString();
                        o.Diners.add(dinerName);
                    }
                }
            }
        }
        return o;
    }

}

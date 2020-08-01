package app.paythebill;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Order_Dialog extends AppCompatDialogFragment{

    private OrderDialogListener listener;
    GridView dinersGrid;
    String dinersArr[];
    View view;
    Order orderToEdit;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Init the dialog window
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.order_dialog,null);
        builder.setView(view);

        // Init dialog for Edit mode
        orderToEdit = (Order) getArguments().getSerializable("Order_To_Edit");

        if(orderToEdit!=null){
            SetOrderDetails();
            builder.setTitle("עידכון מנה");
        }
        else{
            builder.setTitle("הוספת מנה");
        }

        // Init the diners checkBox grid view
        dinersArr = getArguments().getStringArray("Diners_Array"); // Get the names
        dinersGrid = view.findViewById(R.id.Diners_CheckBox_Grid); // init GridView
        DinersCustomAdapter customAdapter = new DinersCustomAdapter(view.getContext(), dinersArr, orderToEdit); // Create adapter for checkBox
        dinersGrid.setAdapter(customAdapter); // Set the GridView according to the adapter

        // Create buttons click_events
        builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.CancelOrder();
                    }
                })
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Order order = GetOrderDetails();
                        // Check validation
                        boolean is_valid = CheckValidOrder(order.Name, order.Price, order.Diners.isEmpty());
                        listener.ApplyOrder(order,is_valid);
                    }
                })
                .setIcon(android.R.drawable.ic_input_add);

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

    private void SetOrderDetails() {
        TextView txt;

        // Set name and price for edited order
        txt = view.findViewById(R.id.DishName_txt);
        txt.setText(orderToEdit.Name);

        txt = view.findViewById(R.id.DishPrice_txt);
        if (orderToEdit.Price != 0)
            txt.setText(Double.toString(orderToEdit.Price));
    }

    private Order GetOrderDetails() {
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

    private boolean CheckValidOrder(String name, double price, boolean isDinersListEmpty) {
        return (!name.isEmpty() && price != 0 && !isDinersListEmpty);
    }

    // Implementation in Add_Orders class
    public interface OrderDialogListener{
        void ApplyOrder(Order order, boolean is_valid);
        void CancelOrder();
    }
}

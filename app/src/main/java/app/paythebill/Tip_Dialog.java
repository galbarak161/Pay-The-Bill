package app.paythebill;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatDialogFragment;

public class Tip_Dialog extends AppCompatDialogFragment {

    private TipDialogListener listener;
    EditText tipValue;
    Switch tipSwitch;
    View view;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Init the dialog window
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.tip_dialog,null);
        builder.setView(view);

        // Init dialog with current tip value
        tipSwitch = view.findViewById(R.id.tip_switch);
        boolean switchStatus = (Order.tip_for_order != 0);
        tipSwitch.setChecked(switchStatus);
        ToggleEditTip(switchStatus); // Toggle EditText tip value layout

        tipSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                ToggleEditTip(checked); // Toggle EditText tip value layout
            }
        });

        // Create buttons click_events
        builder.setTitle("טופס עדכון / ביטול תוספת טיפ לחשבון")
                .setNegativeButton("ביטול", null)
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int tip = 0;
                        tipSwitch = view.findViewById(R.id.tip_switch);

                        if(tipSwitch.isChecked()) // Take the new tip value or set value to 0 if user choose to switch off
                            tip = Integer.parseInt(tipValue.getText().toString());

                        listener.ApplyTip(tip);
                    }
                })
                .setIcon(android.R.drawable.ic_input_add);

        return builder.create();
    }

    private void ToggleEditTip(boolean checked) {
        // Set the current tip value to EditText
        tipValue = view.findViewById(R.id.tipValue_txt);
        tipValue.setText(Integer.toString(Order.tip_for_order));

        // Show / Hide EditText tip layout
        LinearLayout tip_layout = view.findViewById(R.id.EditTip_Layout);
        int viewStatus = (checked) ? View.VISIBLE : View.GONE;
        tip_layout.setVisibility(viewStatus);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (TipDialogListener) context;
        } catch (ClassCastException e){
            e.printStackTrace();
            throw  new ClassCastException(context.toString());
        }

    }

    // Implementation in Order_Summary class
    public interface TipDialogListener{
        void ApplyTip(int tip);
    }
}

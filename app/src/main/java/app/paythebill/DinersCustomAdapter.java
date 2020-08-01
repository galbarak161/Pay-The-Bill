package app.paythebill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

public class DinersCustomAdapter extends BaseAdapter {
    Context context;
    String diners[];
    Order orderToEdit;
    LayoutInflater inflater;
    boolean checkBoxStatus[];

    // C'tor
    public DinersCustomAdapter(Context applicationContext, String diners[], Order orderToEdit) {
        this.context = applicationContext;
        this.diners = diners;
        this.orderToEdit = orderToEdit;

        // Boolean array for keeping the checkBox status after scrolling
        this.checkBoxStatus = new boolean[diners.length];
        InitCheckBoxStatus();

        inflater = (LayoutInflater.from(applicationContext));
    }

    private void InitCheckBoxStatus() {
        for (int i = 0; i < checkBoxStatus.length; i++) {
            if (orderToEdit != null && orderToEdit.Diners.contains(diners[i]))
                checkBoxStatus[i] = true; // If we edit order then the original diners will be checked
            else
                checkBoxStatus[i] = false; // Init the default status to false
        }
    }

    @Override
    public int getCount() {
        return diners.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.diners_gridview, null); // inflate the layout
        final CheckBox diner_cb = view.findViewById(R.id.simpleCheckBox); // get the reference of ImageView
        diner_cb.setText(diners[i]); // set checkBox text
        diner_cb.setChecked(checkBoxStatus[i]);
        diner_cb.setOnClickListener(new View.OnClickListener() {
            @Override // Update checkBox status array after every click (Set status by user)
            public void onClick(View v) {
                checkBoxStatus[i] = diner_cb.isChecked();
            }
        });
        return view;
    }
}
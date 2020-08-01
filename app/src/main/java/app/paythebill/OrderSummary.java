package app.paythebill;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class OrderSummary extends AppCompatActivity implements Tip_Dialog.TipDialogListener {

    HashMap<String,Diner> dinerHashMap;
    Order[] ordersArray;
    LinearLayout summaryOrderLayout;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        dinerHashMap = (HashMap<String, Diner>) intent.getSerializableExtra("dinersHashMap");
        ordersArray = (Order[]) intent.getSerializableExtra("ordersArray");

        // Init the order summery form
        InitDinersSummeryLayout();
    }

    private void InitDinersSummeryLayout() {
        double totalPayment = 0;
        summaryOrderLayout = findViewById(R.id.Diners_summery);
        summaryOrderLayout.removeAllViews();

        // Init iterator for running on HashMap data
        Set set = dinerHashMap.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            TextView textView = new TextView(this);

            // Create new diner and add it to layout
            Diner d = (Diner) entry.getValue();

            // Do not print diners who didn't order
            if(d.DinerOrders.isEmpty())
                continue;

            textView.setText(d.PrintDinerDetails());
            SetTextViewAttributes(textView, false);
            summaryOrderLayout.addView(textView);

            AddLineSeparator();
            totalPayment += d.TotalToPay; // Calculate total payment
        }

        // Create summery text
        summaryOrderLayout = findViewById(R.id.Order_summery);
        summaryOrderLayout.removeAllViews();
        DecimalFormat df = new DecimalFormat("###.##"); // Take only to numbers after decimal point
        StringBuilder sb = new StringBuilder();

        sb.append("סך כל החשבון שצריך לשלם - ");

        if(Order.tip_for_order!=0) // Calculate total payment with tip
            totalPayment = totalPayment * (Order.tip_for_order + 100) / 100;

        sb.append(df.format(totalPayment) + " \u20AA" + "\n\t");
        sb.append("לפי טיפ של: " + "% " + Order.tip_for_order);
        TextView TotalPaymentView = new TextView(this);
        TotalPaymentView.setText(sb.toString());
        SetTextViewAttributes(TotalPaymentView, true);
        summaryOrderLayout.addView(TotalPaymentView);
    }

    public void SetTipToOrder(View view){
        // Create the Tip_Order dialog
        Tip_Dialog dialog = new Tip_Dialog();
        dialog.show(getSupportFragmentManager(),"add/edit_tip_Dialog");
    }

    public void ShowAllOrders(View view){
        // Create the Set_Order dialog
        OrderListDialog dialog = new OrderListDialog();
        Bundle b = new Bundle();
        b.putSerializable("Orders_Array", ordersArray);

        // Open the dialog for add / edit orders
        dialog.setArguments(b);
        dialog.show(getSupportFragmentManager(),"OrdersList_Dialog");
    }

    @Override
    public void ApplyTip(int tip) {
        // Update tip value and load again summery layout
        Order.tip_for_order = tip;
        InitDinersSummeryLayout();
    }

    private void SetTextViewAttributes(TextView textView, boolean is_summary_txt) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(ConvertDpToPixel(12), 0, ConvertDpToPixel(12), ConvertDpToPixel(12));

        textView.setLayoutParams(params);

        if(is_summary_txt) // for total payment text set "Center"
            textView.setGravity(Gravity.CENTER);

        textView.setTextColor(Color.BLACK);
        textView.setTextSize(ConvertDpToPixel(5));
        textView.setPadding(ConvertDpToPixel(3), ConvertDpToPixel(3), ConvertDpToPixel(3), ConvertDpToPixel(3));
        textView.setTypeface(null, Typeface.BOLD);
    }

    private int ConvertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    private void AddLineSeparator() {
        LinearLayout lineLayout = new LinearLayout(this);
        lineLayout.setBackgroundColor(Color.GRAY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2);
        params.setMargins(0, ConvertDpToPixel(10), 0, ConvertDpToPixel(10));
        lineLayout.setLayoutParams(params);
        summaryOrderLayout.addView(lineLayout);
    }
}

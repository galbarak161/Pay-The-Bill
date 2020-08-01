package app.paythebill;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Add_Orders extends AppCompatActivity implements Order_Dialog.OrderDialogListener {

    LinearLayout orderListLayout;
    String[] dinersNameArray;
    HashMap<String,Diner> dinersOrdersHasMap;
    List<Order> ordersList;
    Order currentOrder;
    int ordersCounter;
    TextView orderTextView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__orders);

        // Init the orders list layout (use on addNewOrder event
        orderListLayout = findViewById(R.id.order_list_layout);
        ordersList = new LinkedList<>();
        currentOrder = null;
        ordersCounter = 0;

        // Init variables for saving data
        sharedPreferences = getPreferences(MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Get number of diners and names from previous activity
        Intent intent = getIntent();
        dinersNameArray = intent.getStringArrayExtra("dinersName-array");
        dinersOrdersHasMap = new HashMap<>();

        for(int i=0;i<dinersNameArray.length;i++) {
            dinersOrdersHasMap.put(dinersNameArray[i], new Diner(i, dinersNameArray[i]));
        }

        // Load orders from memory (if exist)
        if (sharedPreferences.contains("OrdersCounter")){
            LoadOrders(sharedPreferences);
        }
    }

    @Override // Save the data if the user pause the activity (close the app to return to prev activity)
    protected void onPause() {
        Gson gson = new Gson();
        int counter = 0;

        // Run on all the orders
        for (int i = 0; i < ordersCounter; i++) {
            if(ordersList.get(i).is_removed == true) // Check if the order was removed
                continue;

            // Convert the order object to json and add it to memory
            String json = gson.toJson(ordersList.get(i));
            editor.putString("order_" + counter, json);
            counter++;
        }

        // Add the final number orders
        editor.putInt("OrdersCounter", counter);

        // Update the memory
        editor.commit();

        // pause the activity
        super.onPause();
    }

    // Load all orders from memory
    private void LoadOrders(SharedPreferences sharedPreferences) {

        // Get the number of orders in memory
        int counter = sharedPreferences.getInt("OrdersCounter", 0);

        // Convert each order from json to Order object and add it to layout and ordersList (using "ApplayOrder" function)
        for(int i=0;i<counter;i++){
            Gson gson = new Gson();
            String json = sharedPreferences.getString("order_" + i, "");
            Order o = gson.fromJson(json, Order.class);
            ApplyOrder(o, true);
        }
    }

    public void AddEditOrder(View view){
        // Create the Set_Order dialog
        Order_Dialog dialog = new Order_Dialog();
        Bundle b = new Bundle();
        b.putStringArray("Diners_Array", dinersNameArray);

        // If we edit order
        if(currentOrder != null)
            b.putSerializable("Order_To_Edit", currentOrder);

        // Open the dialog for add / edit orders
        dialog.setArguments(b);
        dialog.show(getSupportFragmentManager(),"add/edit_order_Dialog");
    }

    public void DeleteOrderList(View view){
        // Open alert dialog window
        new AlertDialog.Builder(this)
                .setTitle("מחיקת רשימה")
                .setMessage("האם למחוק את כל רשימת המנות?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteAllOrders();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void DeleteAllOrders() {
        ///////////////////// Delete memory ////////////////
        editor.clear();
        editor.commit();
        orderListLayout.removeAllViews();
        ordersList.clear();
        ordersCounter = 0;
    }

    private void UpdateOrder(Order order) {
        // Update order details
        currentOrder.Name = order.Name;
        currentOrder.Price = order.Price;
        currentOrder.Diners.clear();
        for (String diner: order.Diners) {
            currentOrder.Diners.add(diner);
        }
    }

    @Override
    public void ApplyOrder(final Order order, boolean is_valid) {
        if(!is_valid){
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.Orders_ConstraintLayout), "שגיאה...  יש לוודא כי מזינים \nשם, מחיר ולפחות סועד אחד.", Snackbar.LENGTH_LONG)
                    .setAction("נסה/י שנית", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            boolean errorWhenEditOrder = (currentOrder != null); // Check if user made wrong edit to exist order
                            Button btn = findViewById(R.id.OrdersListShow_btn);
                            if(!errorWhenEditOrder) // Keep the correct data
                                currentOrder = order;

                            btn.performClick(); // Open Add_Order dialog

                            if(!errorWhenEditOrder) // Reset currentOrder (currentOrder store only edited order
                                currentOrder = null;
                        }
                    });
            snackbar.show();
            return;
        }

        if(currentOrder == null) // Create new order and add it to orderList
        {
            //Adding a LinearLayout with HORIZONTAL orientation
            LinearLayout textLinearLayout = new LinearLayout(this);
            textLinearLayout.setOrientation(LinearLayout.VERTICAL);
            orderListLayout.addView(textLinearLayout);

            // Create the new order object
            ordersList.add(order);

            // Create new line on order list TextView
            TextView textView = new TextView(this);
            textView.setText(order.toString());
            SetTextViewAttributes(textView, ordersList.indexOf(order));
            textLinearLayout.addView(textView);
            ordersCounter++;
        }

        else // Edit order and update the TextView line
        {
            UpdateOrder(order);
            orderTextView.setText(currentOrder.toString());
            currentOrder = null;
            orderTextView = null;
        }
    }

    @Override
    public void CancelOrder() {
        currentOrder = null;
        orderTextView = null;
    }

    public void SummaryOrder(View view){

        // Open alert dialog window
        new AlertDialog.Builder(this)
                .setTitle("מעבר לחשבון")
                .setMessage("האם סיימתם להזמין? \nלאחר מעבר לחשבון לא ניתן להוסיף מנות")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CloseOrderAndContinue(); // Close the order
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void CloseOrderAndContinue() {
        // Parse all the data from layout to HashMap
        int orderIndex = 0;
        Order[] ordersArray = new Order[ordersList.size()];

        for (Order order:ordersList) {
            ordersArray[orderIndex++] = order; // Save array of all orders
            int numberOfDinersInOrder = order.Diners.size();
            for(String diner: order.Diners){
                Diner d = dinersOrdersHasMap.get(diner);
                d.TotalToPay += order.Price / numberOfDinersInOrder;
                d.DinerOrders.add(order);
            }
        }

        // Delete all orders from memory (continue only with HashMap)
        DeleteAllOrders();

        // Create new activity and send the data
        Intent intent = new Intent(this, OrderSummary.class);
        intent.putExtra("dinersHashMap",dinersOrdersHasMap);
        intent.putExtra("ordersArray",ordersArray);
        startActivity(intent);
    }

    private void SetTextViewAttributes(final TextView textView, final int orderIndex) {
        // Set view attributes
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(ConvertDpToPixel(20), 0,ConvertDpToPixel(20), ConvertDpToPixel(25));
        textView.setLayoutParams(params);
        textView.setTextSize(ConvertDpToPixel(5));
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(Color.BLUE);

        // click style
        TypedValue outValue = new TypedValue();
        textView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        textView.setBackgroundResource(outValue.resourceId);

        // Set longClick event to edit orders
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                currentOrder = ordersList.get(orderIndex); // Get the order and update the form
                orderTextView = textView; // Save the textView to change it's text after update
                Button btn = findViewById(R.id.OrdersListShow_btn);
                btn.performClick();
                return true;
            }
        });

        // Set sweep left / right for delete the order
        textView.setOnTouchListener(new OnSwipeTouchListener() {
            public boolean onSwipeLeft() {
                DeleteOrder(orderIndex, textView);
                return true;
            }
            public boolean onSwipeRight() {
                DeleteOrder(orderIndex, textView);
                return true;
            }
        });
    }

    private void DeleteOrder(int orderIndex, TextView textView){
        ordersList.get(orderIndex).is_removed = true; // Change order status
        textView.setVisibility(View.GONE); // Hide the line;
    }

    private int ConvertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
}

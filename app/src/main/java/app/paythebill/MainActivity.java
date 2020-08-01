package app.paythebill;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    LinearLayout diners_linearLayout;
    String[] dinersArray;
    boolean is_oneDiner;
    int dinersCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init variables
        diners_linearLayout = findViewById(R.id.order_list_layout);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        // Check if there is loaded list in memory
        if (sharedPreferences.contains("DinersCounter")){
            LoadDiners(sharedPreferences);
            is_oneDiner = true;
        }

        // start empty list
        else{
            dinersCounter = 0;
            is_oneDiner = false;
            // Hide next button until we have one diner
            ToggleBtnStatus(View.GONE);
        }
    }

    @Override // Save the data if the user pause the activity (close the app to return to prev activity)
    protected void onPause() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        for (int i = 0; i < dinersCounter; i++) {
            editor.putString("diner_" + i, dinersArray[i]);
        }
        editor.putInt("DinersCounter", dinersCounter);
        editor.commit();

        super.onPause();
    }

    // If there is list in memory - load it to layout
     private void LoadDiners(SharedPreferences sharedPreferences) {
         dinersCounter = sharedPreferences.getInt("DinersCounter", 0); // Get the number of diners

         // Add the diners to layout (not to diners list)
         for (int i = 0; i < dinersCounter; i++) {
             EditText editText = new EditText(this);
             editText.setText(sharedPreferences.getString("diner_" + i, ""));
             SetEditTextAttributes(editText);
             diners_linearLayout.addView(editText);
         }
     }

    //  Called when the user click the "Add" button
    public void AddDiner(View view) {

        // Create new Edit Text view
        EditText editText = new EditText(this);
        editText.setHint("הכנס את שם הסועד");
        SetEditTextAttributes(editText);
        diners_linearLayout.addView(editText);

        // Set focus and open the keyboard
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        // Show next button
        if(!is_oneDiner){
            ToggleBtnStatus(View.VISIBLE);
            is_oneDiner = true;
        }

        dinersCounter++;

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.Diners_ConstraintLayout), "סועד נוסף בהצלחה", Snackbar.LENGTH_LONG)
                .setAction("בטל", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UndoAddAction();
                    }
                });
        snackbar.show();

    }

    private void UndoAddAction() {
        int numberOfEditTextView = diners_linearLayout.getChildCount();
        diners_linearLayout.removeViewAt(numberOfEditTextView - 1);
        dinersCounter--;

        if(diners_linearLayout.getChildCount() == 0){
            ToggleBtnStatus(View.GONE);
            is_oneDiner = false;
        }
    }

    //  Called when the user click the "Delete List" button
    public void DeleteDinersList(View view) {

        // Open alert dialog window
        new AlertDialog.Builder(this)
                .setTitle("מחיקת רשימה")
                .setMessage("האם למחוק את כל רשימת הסועדים?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        ///////////////////// Delete memory ////////////////
                        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();

                        diners_linearLayout.removeAllViews();
                        dinersCounter = 0;
                        is_oneDiner = false;

                        // Hide next/delete button until we have one diner
                        ToggleBtnStatus(View.GONE);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Called when the user click the "Next" button
    public void SaveDinersList(View view) {
        // Create array for keeping diners names
        dinersArray = new String[dinersCounter];
        int counter = 0;

        // Run on the layout Edit-Text views and get the diner's name
        for (int i = 0; i < diners_linearLayout.getChildCount(); i++) {
            View v = diners_linearLayout.getChildAt(i);
            if (v instanceof EditText) {
                String dinerName = ((EditText) v).getText().toString();
                if (dinerName == null || dinerName.isEmpty()) {
                    // If no name
                    ShowErrorDialog("סועד ללא שם");
                    return;
                }
                // Add new name to list
                dinersArray[counter] = dinerName;
                counter++;
            }
        }

        // Open new activity and send the data
        Intent intent = new Intent(this, Add_Orders.class);
        intent.putExtra("dinersName-array", dinersArray);
        startActivity(intent);
    }

    private void ShowErrorDialog(String error) {
        new AlertDialog.Builder(this)
                .setTitle("שגיאה")
                .setMessage(error)
                .setPositiveButton(android.R.string.yes, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void ToggleBtnStatus(int status) {
        Button nextDelBtn;
        nextDelBtn = findViewById(R.id.Tip_btn);
        nextDelBtn.setVisibility(status);
        nextDelBtn = findViewById(R.id.Delete_btn);
        nextDelBtn.setVisibility(status);
    }

    private void SetEditTextAttributes(EditText editText) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(ConvertDpToPixel(2),
                ConvertDpToPixel(16),
                ConvertDpToPixel(16),
                ConvertDpToPixel(8)
        );

        editText.setLayoutParams(params);
    }

    private int ConvertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }


}








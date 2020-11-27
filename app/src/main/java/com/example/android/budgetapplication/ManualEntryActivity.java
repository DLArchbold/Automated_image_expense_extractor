package com.example.android.budgetapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.android.budgetapplication.data.ExpenseContract;
import com.example.android.budgetapplication.data.ExpenseDbHelper;
import com.example.android.budgetapplication.data.ExpenseProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.android.budgetapplication.data.ExpenseContract.ExpenseEntry;

import java.net.URI;
import java.util.Calendar;

public class ManualEntryActivity extends AppCompatActivity {




    private Spinner expenseIncomeOptionSpinner;
//    private EditText day;
//    private EditText month;
//    private EditText year;
    private EditText amount;
    private EditText description;
    private EditText date;
    DatePickerDialog datePickerDialog;
    private Spinner expenseIncomeCategorySpinner;
    private Button addButton;

    String expenseIncomeOption;
    String expenseIncomeCategory;

     int yr;
     int mh;
     int dy;
    Uri newUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);






        expenseIncomeOptionSpinner= (Spinner) findViewById(R.id.spinner_expense_income_option);
//        day = (EditText) findViewById(R.id.edit_day);
//        month = (EditText) findViewById(R.id.edit_month);
//        year = (EditText) findViewById(R.id.edit_year);
        amount = (EditText) findViewById(R.id.edit_amount);
        description = (EditText) findViewById(R.id.edit_description);
        date = findViewById(R.id.date);
        expenseIncomeCategorySpinner= (Spinner) findViewById(R.id.spinner_expense_income_category);
        addButton = findViewById(R.id.button_add);

        setupDatePicker();
        setupExpenseIncomeOptionSpinner();
        setupExpenseIncomeCategorySpinner();
        setupAddButton();

    }


    private void insertExpense()
    {

        //Read from input fields
        expenseIncomeOption = expenseIncomeOption;
        int    expenseDay = dy;
        int    expenseMonth = mh;
        int    expenseYear = yr;
        double expenseAmount;
        if(amount.getText().toString() == null || amount.getText().toString().isEmpty()){
            expenseAmount = 0;
        }else{
            expenseAmount = Double.parseDouble(amount.getText().toString());
        }

        String expenseDescription = description.getText().toString();
        expenseIncomeCategory = expenseIncomeCategory;


        //Create db helper
        //ExpenseDbHelper mDbHelper = new ExpenseDbHelper(this);

       // SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ExpenseEntry.COLUMN_OPTION, expenseIncomeOption);
        values.put(ExpenseEntry.COLUMN_DAY, expenseDay);
        values.put(ExpenseEntry.COLUMN_MONTH, expenseMonth);
        values.put(ExpenseEntry.COLUMN_YEAR, expenseYear);
        values.put(ExpenseEntry.COLUMN_AMOUNT, expenseAmount);
        values.put(ExpenseEntry.COLUMN_DESCRIPTION, expenseDescription);
        values.put(ExpenseEntry.COLUMN_CATEGORY, expenseIncomeCategory);

        // Insert a new row for pet in the database, returning the ID of that new row.
        //long newRowId = db.insert(ExpenseEntry.TABLE_NAME, null, values);

        // Insert a new row for an expense into the provider using the ContentResolver.
        // Use the {@link ExpenseEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access this record's data in the future.
        newUri = getContentResolver().insert(ExpenseEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {



            // If the neUri is null, then there was an error with insertion.
            //Toasts created in ExpenseProvider, specific to whether Date/Amount/Description empty
            //Toast.makeText(this, "Error with saving expense/income", Toast.LENGTH_LONG).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            String toDisplay = String.valueOf(expenseIncomeOption) + ": " + expenseDescription + " saved successfully.";
            Toast.makeText(this, toDisplay ,  Toast.LENGTH_LONG).show();
            finish();//Return to MainActivity
        }





    }



    private void setupAddButton()
    {

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
               insertExpense();


            }
        });
    }


    private void setupDatePicker(){
        date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                yr = calendar.get(Calendar.YEAR);
                mh = calendar.get(Calendar.MONTH);
                dy = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(ManualEntryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yrCal, int mhCal, int dyCal) {
                        yr = yrCal;
                        mh = mhCal;
                        dy = dyCal;
                        String dateToSet = dy + "/" + String.valueOf(Integer.valueOf(mh)+1) + "/" + yr;

                        date.setText(dateToSet);
                    }
                }, yr, mh, dy);
                datePickerDialog.show();
            }
        });
    }

    private void setupExpenseIncomeOptionSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter expenseIncomeOptionSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_expense_income_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        expenseIncomeOptionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        expenseIncomeOptionSpinner.setAdapter(expenseIncomeOptionSpinnerAdapter);

        // Set the integer mSelected to the constant values
        expenseIncomeOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                expenseIncomeOption = selection;
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.expense))) {
                        Log.e("MainActivity", selection);
                    } else {
                        Log.e("MainActivity", selection);
                        // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setupExpenseIncomeCategorySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter expenseIncomeCategorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_expense_income_category, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        expenseIncomeCategorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        expenseIncomeCategorySpinner.setAdapter(expenseIncomeCategorySpinnerAdapter);

        // Set the integer mSelected to the constant values
        expenseIncomeCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                expenseIncomeCategory = selection;
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.bills))) {
                        Log.e("ManualEntryActivity", selection);
                    } else if (selection.equals(getString(R.string.house))) {
                        Log.e("ManualEntryActivity", selection);
                        // Female
                    } else {
                        Log.e("ManualEntryActivity", selection);
                        // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}

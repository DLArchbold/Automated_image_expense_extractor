package com.example.android.budgetapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.android.budgetapplication.data.ExpenseContract.ExpenseEntry;
import java.util.Calendar;

public class ManualEntryActivity extends AppCompatActivity {




    private Spinner expenseIncomeOptionSpinner;
    private EditText day;
    private EditText month;
    private EditText year;
    private EditText amount;
    private EditText description;
    private Spinner expenseIncomeCategorySpinner;
    private Button addButton;

    String expenseIncomeOption;
    String expenseIncomeCategory;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);






        expenseIncomeOptionSpinner= (Spinner) findViewById(R.id.spinner_expense_income_option);
        day = (EditText) findViewById(R.id.edit_day);
        month = (EditText) findViewById(R.id.edit_month);
        year = (EditText) findViewById(R.id.edit_year);
        amount = (EditText) findViewById(R.id.edit_amount);
        description = (EditText) findViewById(R.id.edit_description);
        expenseIncomeCategorySpinner= (Spinner) findViewById(R.id.spinner_expense_income_category);
        addButton = findViewById(R.id.button_add);

        setupExpenseIncomeOptionSpinner();
        setupExpenseIncomeCategorySpinner();
        setupAddButton();

    }


    private void insertExpense()
    {

        //Read from input fields
        expenseIncomeOption = expenseIncomeOption;
        int    expenseDay = Integer.parseInt(day.getText().toString());
        int    expenseMonth = Integer.parseInt(month.getText().toString());
        int    expenseYear = Integer.parseInt(year.getText().toString());

        double expenseAmount = Double.parseDouble(amount.getText().toString());
        String expenseDescription = description.getText().toString();
        expenseIncomeCategory = expenseIncomeCategory;


        //Create db helper
        ExpenseDbHelper mDbHelper = new ExpenseDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

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
        long newRowId = db.insert(ExpenseEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving expense/income", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Expense/income saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();

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
               finish();

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
                //mGender = 0; // Unknown
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
                //mGender = 0; // Unknown
            }
        });
    }



    public void showDatePickerDialog(View v) {

        DialogFragment newFragment = new DatePickerFragment();


        newFragment.show(getSupportFragmentManager(), "datePicker");


    }




//ManualEntryActivity.this,



//
//    DialogFragment newFragment = new DatePickerDialog.OnDateSetListener() {
//
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current date as the default date in the picker
//            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int day = c.get(Calendar.DAY_OF_MONTH);
//
//            // Create a new instance of DatePickerDialog and return it
//            return new DatePickerDialog(ManualEntryActivity.this, this, year, month, day);
//        }
//
//        @Override
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//            // Do something with the date chosen by the user
//
//
//        }
//
//    };









//    DialogFragment newFragment = new DatePickerFragment();
//
//        newFragment.show(getSupportFragmentManager(), "datePicker");
//
//
//
//
//
//
//
//    int ab = ((DatePickerFragment) newFragment).mYear;




}

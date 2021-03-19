package com.example.android.budgetapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.budgetapplication.data.ExpenseContract;
import com.example.android.budgetapplication.data.BudgetContract;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class ManualBudgetActivity extends AppCompatActivity {
    String[] oneBudgetValues;

    EditText startDate;
    EditText endDate;
    private EditText spendLimit;
    private Spinner expenseIncomeCategorySpinner;
    private Button addButton;
    DatePickerDialog datePickerDialogStart;
    DatePickerDialog datePickerDialogEnd;

    int startYr;
    int startMh;
    int startDy;
    String fullStartDate;

    int endYr;
    int endMh;
    int endDy;
    String fullEndDate;
    Uri newUri;
    private static DecimalFormat df = new DecimalFormat("0.00");
    String expenseIncomeCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_budget);


        //If updating, get updated record
        if (getIntent().hasExtra("oneBudgetValues") == true) {
            oneBudgetValues = getIntent().getExtras().getStringArray("oneBudgetValues");
            TextView limit = (TextView) findViewById(R.id.spend_limit);
            limit.setText(df.format(Double.parseDouble(oneBudgetValues[3])));
            setupDeleteButton();

        }


        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);
        spendLimit = findViewById(R.id.spend_limit);
        expenseIncomeCategorySpinner = (Spinner) findViewById(R.id.spinner_category);
        addButton = findViewById(R.id.button_add_budget);

        setupDatePickers();
        setupExpenseIncomeCategorySpinner();
         setupAddButton();



    }

    protected void setupDatePickers() {


        if (oneBudgetValues != null) {

            String startDateToSet = oneBudgetValues[1];
            startDate.setText(startDateToSet.replace("-", "/"));
            startYr = Integer.parseInt(oneBudgetValues[1].substring(oneBudgetValues[1].lastIndexOf("-") + 1));
            startMh = Integer.parseInt(oneBudgetValues[1].substring(oneBudgetValues[1].indexOf("-") + 1, oneBudgetValues[1].lastIndexOf("-")));
            startDy = Integer.parseInt(oneBudgetValues[1].substring(0, oneBudgetValues[1].indexOf("-")));
            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    startYr = Integer.parseInt(oneBudgetValues[1].substring(oneBudgetValues[1].lastIndexOf("-")+1));
//                    startMh = Integer.parseInt(oneBudgetValues[1].substring(oneBudgetValues[1].indexOf("-")+1, oneBudgetValues[1].lastIndexOf("-")));
//                    startDy = Integer.parseInt(oneBudgetValues[1].substring(0, oneBudgetValues[1].indexOf("-")));

                    datePickerDialogStart = new DatePickerDialog(ManualBudgetActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int startYrCal, int startMhCal, int startDyCal) {
                            startYr = startYrCal;
                            //Need to +1 b4 db since Jan is 0 for datePicker
                            startMh = startMhCal + 1;
                            startDy = startDyCal;
                            String dateToSet = startDy + "/" + String.valueOf(Integer.valueOf(startMh)) + "/" + startYr;

                            startDate.setText(dateToSet);
                            datePicker.updateDate(startYr, startMh, startDy);
                            datePickerDialogStart.updateDate(startYr, startMh, startDy);

                            System.out.println("date print");

                        }


                    }, startYr, startMh - 1, startDy);

                    //startMh-1 since datepicker starts Jan from 0
                    datePickerDialogStart.updateDate(startYr, startMh - 1, startDy);
                    //From db to datePicker need to -1 for month
                    datePickerDialogStart.show();
                }
            });


            String endDateToSet = oneBudgetValues[2];
            endDate.setText(endDateToSet.replace("-", "/"));
            endYr = Integer.parseInt(oneBudgetValues[2].substring(oneBudgetValues[2].lastIndexOf("-") + 1));
            endMh = Integer.parseInt(oneBudgetValues[2].substring(oneBudgetValues[2].indexOf("-") + 1, oneBudgetValues[2].lastIndexOf("-")));
            endDy = Integer.parseInt(oneBudgetValues[2].substring(0, oneBudgetValues[2].indexOf("-")));
            endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    endYr = Integer.parseInt(oneBudgetValues[2].substring(oneBudgetValues[2].lastIndexOf("-")+2));
//                    endMh = Integer.parseInt(oneBudgetValues[2].substring(oneBudgetValues[2].indexOf("-")+2, oneBudgetValues[2].lastIndexOf("-")));
//                    endDy = Integer.parseInt(oneBudgetValues[2].substring(0, oneBudgetValues[2].indexOf("-")));

                    datePickerDialogEnd = new DatePickerDialog(ManualBudgetActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int endYrCal, int endMhCal,
                                              int endDyCal) {
                            endYr = endYrCal;
                            //Need to +1 b4 db since Jan is 0 for datePicker
                            endMh = endMhCal + 1;
                            endDy = endDyCal;
                            String dateToSet = endDy + "/" + String.valueOf(Integer.valueOf(endMh)) + "/" + endYr;

                            endDate.setText(dateToSet);
                            datePicker.updateDate(endYr, endMh, endDy);
                            datePickerDialogEnd.updateDate(endYr, endMh, endDy);

                            System.out.println("date print");

                        }


                    }, endYr, endMh - 1, endDy);

                    //endMh-1 since datepicker starts Jan from 0
                    datePickerDialogEnd.updateDate(endYr, endMh - 1, endDy);
                    //From db to datePicker need to -1 for month
                    datePickerDialogEnd.show();
                }
            });


        } else {
            Calendar calendar = Calendar.getInstance();
            startYr = calendar.get(Calendar.YEAR);
            startMh = calendar.get(Calendar.MONTH) + 1;
            startDy = calendar.get(Calendar.DAY_OF_MONTH);
            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    datePickerDialogStart = new DatePickerDialog(ManualBudgetActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int startYrCal, int startMhCal, int startDyCal) {
                            startYr = startYrCal;
                            startMh = startMhCal + 1;
                            startDy = startDyCal;
                            String dateToSet = startDy + "/" + String.valueOf(Integer.valueOf(startMh)) + "/" + startYr;

                            startDate.setText(dateToSet);


                        }
                    }, startYr, startMh, startDy);

                    datePickerDialogStart.updateDate(startYr, startMh - 1, startDy);
                    datePickerDialogStart.show();
                }
            });


            calendar = Calendar.getInstance();
            endYr = calendar.get(Calendar.YEAR);
            endMh = calendar.get(Calendar.MONTH) + 1;
            endDy = calendar.get(Calendar.DAY_OF_MONTH);
            endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    datePickerDialogEnd = new DatePickerDialog(ManualBudgetActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int endYrCal, int endMhCal, int endDyCal) {
                            endYr = endYrCal;
                            endMh = endMhCal + 1;
                            endDy = endDyCal;
                            String dateToSet = endDy + "/" + String.valueOf(Integer.valueOf(endMh)) + "/" + endYr;

                            endDate.setText(dateToSet);


                        }
                    }, endYr, endMh, endDy);

                    datePickerDialogEnd.updateDate(endYr, endMh - 1, endDy);
                    datePickerDialogEnd.show();
                }
            });


        }
    }

    protected void setupExpenseIncomeCategorySpinner(){
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

        if (oneBudgetValues != null) {
            String[] categories = getResources().getStringArray(R.array.array_expense_income_category);
            Map<String, Integer> categoryToPosition = new HashMap<>();
            int i = 0;
            for (String category : categories) {
                categoryToPosition.put(category, i++);
            }
            //oneRecordValues[4] is the category of record selected (Bills, House, Eating out, Food, Transportation etc.. )
            expenseIncomeCategorySpinner.setSelection(categoryToPosition.get(oneBudgetValues[4]));

        }
    }
    private void setupDeleteButton() {
        Button deleteButton = findViewById(R.id.button_delete_budget);
        deleteButton.setVisibility(View.VISIBLE);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecord();
                finish();
            }
        });

    }

    private void deleteRecord(){

        String path = "/";
        path = path.concat(oneBudgetValues[0]);
        getContentResolver().delete(Uri.withAppendedPath(BudgetContract.BudgetEntry.CONTENT_URI, path), null, null);

    }

    protected void setupAddButton(){

        if (oneBudgetValues != null) {
            addButton.setText("Update");
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                    updateBudget();


                }
            });

        } else {

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                    insertBudget();


                }
            });
        }
    }

    private void updateBudget(){
        //Read from input fields
        int startDay = startDy;
        int startMonth = startMh;
        int startYear = startYr;

        int endDay = endDy;
        int endMonth = endMh;
        int endYear = endYr;


        expenseIncomeCategory = expenseIncomeCategory;
        fullStartDate = startDy + "-" + startMh + "-" + startYr;
        fullEndDate = endDy + "-" + endMh + "-" + endYr;
        String limit = spendLimit.getText().toString();


        //Create db helper
        //ExpenseDbHelper mDbHelper = new ExpenseDbHelper(this);

        // SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and expense attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BudgetContract.BudgetEntry.COLUMN_START_DAY, startDay);
        values.put(BudgetContract.BudgetEntry.COLUMN_START_MONTH, startMonth);
        values.put(BudgetContract.BudgetEntry.COLUMN_START_YEAR, startYear);
        values.put(BudgetContract.BudgetEntry.COLUMN_START_DATE, fullStartDate);
        values.put(BudgetContract.BudgetEntry.COLUMN_END_DAY, endDay);
        values.put(BudgetContract.BudgetEntry.COLUMN_END_MONTH, endMonth);
        values.put(BudgetContract.BudgetEntry.COLUMN_END_YEAR, endYear);
        values.put(BudgetContract.BudgetEntry.COLUMN_END_DATE, fullEndDate);
        values.put(BudgetContract.BudgetEntry.COLUMN_SPEND_LIMIT, Double.valueOf(spendLimit.getText().toString()));
        values.put(BudgetContract.BudgetEntry.COLUMN_CATEGORY, expenseIncomeCategory);

        // Insert a new row for expense in the database, returning the ID of that new row.
        //long newRowId = db.insert(ExpenseEntry.TABLE_NAME, null, values);

        // Insert a new row for an expense into the provider using the ContentResolver.
        // Use the {@link ExpenseEntry#CONTENT_URI} to indicate that we want to insert
        // into the budget database table.
        // Receive the new content URI that will allow us to access this record's data in the future.
        String path = "/" + oneBudgetValues[0];
        int rowsUpdated = getContentResolver().update(Uri.withAppendedPath(BudgetContract.BudgetEntry.CONTENT_URI, path), values, null, null);

        // Show a toast message depending on whether or not the insertion was successful
        if (rowsUpdated == 0) {

            // If rowsUpdated is 0, then there was an error with updating.
            //Toasts created in ExpenseProvider, specific to whether Date/Amount/Description empty
            //Toast.makeText(this, "Error with updating expense/income", Toast.LENGTH_LONG).show();
            String toDisplay = "Error in updating: " + fullStartDate + ": " + fullEndDate;
            Toast.makeText(this, toDisplay, Toast.LENGTH_LONG).show();
            finish();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            String toDisplay = fullStartDate + ": " + fullEndDate + " updated successfully.";
            Toast.makeText(this, toDisplay, Toast.LENGTH_LONG).show();
            finish();//Return to MainActivity
        }

    }

    private void insertBudget(){

        //Read from input fields
        int startDay = startDy;
        int startMonth = startMh;
        int startYear = startYr;

        int endDay = endDy;
        int endMonth = endMh;
        int endYear = endYr;


        expenseIncomeCategory = expenseIncomeCategory;
        fullStartDate = startDy + "-" + startMh + "-" + startYr;
        fullEndDate = endDy + "-" + endMh + "-" + endYr;
        String limit = spendLimit.getText().toString();


        //Create db helper
        //ExpenseDbHelper mDbHelper = new ExpenseDbHelper(this);

        // SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and expense attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BudgetContract.BudgetEntry.COLUMN_START_DAY, startDay);
        values.put(BudgetContract.BudgetEntry.COLUMN_START_MONTH, startMonth);
        values.put(BudgetContract.BudgetEntry.COLUMN_START_YEAR, startYear);
        values.put(BudgetContract.BudgetEntry.COLUMN_START_DATE, fullStartDate);
        values.put(BudgetContract.BudgetEntry.COLUMN_END_DAY, endDay);
        values.put(BudgetContract.BudgetEntry.COLUMN_END_MONTH, endMonth);
        values.put(BudgetContract.BudgetEntry.COLUMN_END_YEAR, endYear);
        values.put(BudgetContract.BudgetEntry.COLUMN_END_DATE, fullEndDate);
        values.put(BudgetContract.BudgetEntry.COLUMN_SPEND_LIMIT, Double.valueOf(spendLimit.getText().toString()));
        values.put(BudgetContract.BudgetEntry.COLUMN_CATEGORY, expenseIncomeCategory);

        // Insert a new row for expense in the database, returning the ID of that new row.
        //long newRowId = db.insert(ExpenseEntry.TABLE_NAME, null, values);

        // Insert a new row for an expense into the provider using the ContentResolver.
        // Use the {@link ExpenseEntry#CONTENT_URI} to indicate that we want to insert
        // into the budget database table.
        // Receive the new content URI that will allow us to access this record's data in the future.
        newUri = getContentResolver().insert(BudgetContract.BudgetEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {


            // If the neUri is null, then there was an error with insertion.
            //Toasts created in ExpenseProvider, specific to whether Date/Amount/Description empty
            //Toast.makeText(this, "Error with saving expense/income", Toast.LENGTH_LONG).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            String toDisplay = String.valueOf(spendLimit.getText().toString()) + " budget of " + String.valueOf(expenseIncomeCategory)+ " saved successfully.";
            Toast.makeText(this, toDisplay, Toast.LENGTH_LONG).show();
            finish();//Return to MainActivity
        }

    }
}

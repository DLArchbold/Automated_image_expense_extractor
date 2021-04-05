package com.example.android.budgetapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

import android.text.format.DateUtils;

import org.w3c.dom.Text;

import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ManualEntryActivity extends AppCompatActivity {


    //UI elements/widgets
    private Spinner expenseIncomeOptionSpinner;
    private EditText amount;
    private EditText description;
    private EditText date;
    DatePickerDialog datePickerDialog;
    private Spinner expenseIncomeCategorySpinner;
    private Button addButton;

    //Field values
    String expenseIncomeOption;
    String expenseIncomeCategory;
    int yr;
    int mh;
    int dy;
    double expenseAmount;
    String expenseDescription;
    String fullDate;

    //URI for ContentResolver, see if rows inserted successfully
    Uri newUri;

    //to see how many rows updated
    int rowsUpdated;


    //For editing an expense/income
    String[] oneRecordValues;

    //For ImageRecognitionEntryActivtiy confirmation
    String[] automatedValues;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private EditText coordinateField;
    int waitTime = 0;
    private TextView coordinate_label;
    //ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService executorService;
    private Button seeLocationButton;
    static final String ACTION_STARTLISTENING = "startListening";
    LocationService locServ;
    String test;
    BroadcastReceiver br;
    String latitude;
    String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        //If updating, get updated record
        if (getIntent().hasExtra("oneRecordValues") == true) {
            oneRecordValues = getIntent().getExtras().getStringArray("oneRecordValues");
        }else if(getIntent().hasExtra("automatedValues") == true){
            automatedValues = getIntent().getExtras().getStringArray("automatedValues");
        }





        expenseIncomeOptionSpinner = (Spinner) findViewById(R.id.spinner_expense_income_option);
//        day = (EditText) findViewById(R.id.edit_day);
//        month = (EditText) findViewById(R.id.edit_month);
//        year = (EditText) findViewById(R.id.edit_year);
        amount = (EditText) findViewById(R.id.edit_amount);
        description = (EditText) findViewById(R.id.edit_description);
        date = findViewById(R.id.date);
        expenseIncomeCategorySpinner = (Spinner) findViewById(R.id.spinner_expense_income_category);
        addButton = findViewById(R.id.button_add);
        coordinate_label = (TextView) findViewById(R.id.coordinate_label);
        coordinateField = (EditText) findViewById(R.id.coordinates);
        seeLocationButton = (Button) findViewById(R.id.button_see_location);

        locServ = new LocationService();

        br = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                    coordinateField.setEnabled(true);
                    coordinateField.setClickable(true);

                    latitude =  intent.getStringExtra("latitude");
                    longitude = intent.getStringExtra("longitude");
                    Uri uri = Uri.parse("google.streetview:cbll:" + latitude + ", " + longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                    coordinateField.setText(latitude + "," + longitude);


            }
        };
        IntentFilter filter = new IntentFilter("com.example.android.budgetapplication/locationResult");
        this.registerReceiver(br, filter);


        if (oneRecordValues != null && !oneRecordValues[6].isEmpty()) {
            coordinateField.setText(oneRecordValues[6]);
        }

        checkLocationPermissions();



        //Set up regardless if new or updating record
        setupDatePicker();
        setupExpenseIncomeOptionSpinner();
        setupExpenseIncomeCategorySpinner();
        setupAddButton();
        setupLocationServices();
        setupSeeLocationButton();

        //Set additional fields if updating a record
        if (oneRecordValues != null || automatedValues != null) {
            setupAmount();
            setupDescription();
            setupDeleteButton();
        }


    }



    @Override
    protected void onDestroy() {

        //you should unregister it in onDestroy() to prevent leaking the receiver out of the activity context.
        super.onDestroy();
        unregisterReceiver(br);
    }

    private void setupLocationServices() {

//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                coordinateField.setEnabled(true);
//                coordinateField.setClickable(true);
//                locationManager.removeUpdates(locationListener);
//                locationManager = null;
//                Uri uri = Uri.parse("google.streetview:cbll:" + String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                //String tx = coordinateField.getText().toString();
////                coordinateField.setText("");
////               tx = coordinateField.getText().toString();
////                coordinateField.setText(location.getLatitude() + "," + location.getLongitude());
////                //coordinateField.setInputType(InputType.TYPE_NULL);
////                tx = coordinateField.getText().toString();
//                startActivity(mapIntent);
//                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//                //coordinateField.append("\n " + location.getLatitude() + " " + location.getLongitude());
//
//                coordinateField.setText(location.getLatitude() + "," + location.getLongitude());
//
//            }
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String s) {
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {
//                //Check if GPS is disabled, open settings
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//
//        };



    }

    private void insertExpense() {

        //Read from input fields
        expenseIncomeOption = expenseIncomeOption;
        int expenseDay = dy;
        int expenseMonth = mh;
        int expenseYear = yr;

        if (amount.getText().toString() == null || amount.getText().toString().isEmpty()) {
            expenseAmount = 0;
        } else {
            if (expenseIncomeOption.equals("Expense")) {
                expenseAmount = -Double.parseDouble(amount.getText().toString());
            } else {
                expenseAmount = Double.parseDouble(amount.getText().toString());
            }

        }

        expenseDescription = description.getText().toString();
        expenseIncomeCategory = expenseIncomeCategory;
        fullDate = dy + "-" + mh + "-" + yr;

        String coordinates = coordinateField.getText().toString();

        //Create db helper
        //ExpenseDbHelper mDbHelper = new ExpenseDbHelper(this);

        // SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and expense attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ExpenseEntry.COLUMN_OPTION, expenseIncomeOption);
        values.put(ExpenseEntry.COLUMN_DAY, expenseDay);
        values.put(ExpenseEntry.COLUMN_MONTH, expenseMonth);
        values.put(ExpenseEntry.COLUMN_YEAR, expenseYear);
        values.put(ExpenseEntry.COLUMN_AMOUNT, expenseAmount);
        values.put(ExpenseEntry.COLUMN_DESCRIPTION, expenseDescription);
        values.put(ExpenseEntry.COLUMN_CATEGORY, expenseIncomeCategory);
        values.put(ExpenseEntry.COLUMN_DATE, fullDate);
        values.put(ExpenseEntry.COLUMN_COORDINATES, coordinates);
        // Insert a new row for expense in the database, returning the ID of that new row.
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
            Toast.makeText(this, toDisplay, Toast.LENGTH_LONG).show();
            finish();//Return to MainActivity
        }


    }

    public void checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Only SDKs >= M (Marshmellow) need to check and grant permission, otherwise it's auto granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Permissions not yet granted

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                        10);
                return;
            } else {//Permissions alr granted
                configureCoordinateField();
            }
        } else {//Android vers no need permissions
            configureCoordinateField();
        }
    }

    private void setupSeeLocationButton() {

        seeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(coordinateField.getText().toString().contains(",")){
                    //open map

                    Uri uri = Uri.parse("google.streetview:cbll:" + latitude + ", " + longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }else{
                    //dialog to get location

                    AlertDialog.Builder builder = new AlertDialog.Builder(ManualEntryActivity.this);
                    builder.setMessage("Touch coordinate field to get location");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked Yes button

                        }
                    });


                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configureCoordinateField();
                    return;
                } else {

                    coordinateField.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkLocationPermissions();
                        }
                    });


                }
        }
        ;
    }

    private void configureCoordinateField() {


        coordinateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(ManualEntryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ManualEntryActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    //When user switch off location services in current activity
                    checkLocationPermissions();
                }

                //Location set
                if (coordinateField.getText().toString().contains(",")) {
                    //Specific prompts when getting coordinates again when location already set for Add new record and Update record


                    coordinate_label.setText("Location/Coordinates");

                    //Location set, Update activity
                    if (oneRecordValues != null && !oneRecordValues[6].isEmpty()) {
                        //When getting coordinates again for Update record & previous location set
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManualEntryActivity.this);
                        builder.setMessage("Get location again?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked Yes button
                                coordinateField.setText("Getting coordinates...");
                                //Do not touch before coords updated, since got background thread updating wait msg, less complications
                                //Re-enable touch at onLocationChanged()
                                coordinateField.setEnabled(false);
                                coordinateField.setClickable(false);
                                //Set minDist to 0 to get 2nd update to execute onLocationChanged
                                Intent sendIntent = new Intent(getApplicationContext(), LocationService.class);
                                sendIntent.setAction(ACTION_STARTLISTENING);
                                sendIntent.setType("text/plain");
                                startService(sendIntent);
                                //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                                executorService = Executors.newSingleThreadScheduledExecutor();
                                setupBackgroundThread(executorService);
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        //Location set, Add activity
                    } else if (oneRecordValues == null) {
                        //When getting coordinates again for Add new record and location already set
                        coordinateField.setText("Tap to get coordinates");


                        //Prompts for when coordinates set before

                        //Do not touch before coords updated, since got background thread updating wait msg, less complications
                        //Re-enable touch at onLocationChanged()
                        coordinateField.setEnabled(false);
                        coordinateField.setClickable(false);
                        //Set minDist to 0 to get 2nd update to execute onLocationChanged
                        Intent sendIntent = new Intent(getApplicationContext(), LocationService.class);
                        sendIntent.setAction(ACTION_STARTLISTENING);
                        sendIntent.setType("text/plain");
                        startService(sendIntent);
                        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                        executorService = Executors.newSingleThreadScheduledExecutor();
                        setupBackgroundThread(executorService);
                    }


                    //Location not set, Add/Update activity
                } else {
                    //Add new record, location not yet set
                    //Prompts for when coordinates not set before

                    //Do not touch before coords updated, since got background thread updating wait msg, less complications
                    //Re-enable touch at onLocationChanged()
                    coordinateField.setEnabled(false);
                    coordinateField.setClickable(false);
                    //Set minDist to 0 to get 2nd update to execute onLocationChanged
                    Intent sendIntent = new Intent(getApplicationContext(), LocationService.class);
                    sendIntent.setAction(ACTION_STARTLISTENING);
                    sendIntent.setType("text/plain");
                    startService(sendIntent);
                    //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    executorService = Executors.newSingleThreadScheduledExecutor();
                    setupBackgroundThread(executorService);
                }


            }
        });

    }

    private void
    setupBackgroundThread(final ScheduledExecutorService executorService) {
        if (executorService != null) {
            waitTime = 0;

            //Background thread
            final ScheduledFuture<?> loadingHandle = executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {

                    String coordFieldContents = coordinateField.getText().toString();
                    if (coordFieldContents.equals("Tap to get coordinates") || !coordFieldContents.contains(",") || coordFieldContents.equals("Getting coordinates...")) {
                        //coord not retrieved yet
                        String coordinateLabel = coordinate_label.getText().toString();
                        if (coordinateLabel.equals("Location/Coordinates")) {
                            coordinate_label.append(" - (0)");
                        } else {
                            waitTime++;
                            coordinate_label.setText("Location/Coordinates - (" + String.valueOf(waitTime) + ")");
                        }
                    } else {

                        executorService.shutdown();
                    }
                }
            }, 0, 1, SECONDS);

        }

    }


    private void setupDeleteButton() {
        if(oneRecordValues!=null){
            Button deleteButton = findViewById(R.id.button_delete);
            deleteButton.setVisibility(View.VISIBLE);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteRecord();
                    finish();
                }
            });
        }


    }

    private void deleteRecord() {
        String path = "/";
        path = path.concat(oneRecordValues[5]);
        getContentResolver().delete(Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, path), null, null);

    }


    private void setupAmount() {
        if(oneRecordValues != null){
            if (oneRecordValues[0].equals("Expense")) {
                amount.setText(oneRecordValues[2].substring(oneRecordValues[2].indexOf("-") + 1));
            } else {
                amount.setText(oneRecordValues[2]);
            }
        }else if(automatedValues!=null){
            amount.setText(automatedValues[3]);
        }

    }

    private void setupDescription() {
        if(oneRecordValues!=null){
            description.setText(oneRecordValues[3]);
        }

    }

    private void updateExpense() {

        //Read from input fields
        expenseIncomeOption = expenseIncomeOption;
        int expenseDay = dy;
        int expenseMonth = mh;
        int expenseYear = yr;

        if (amount.getText().toString() == null || amount.getText().toString().isEmpty()) {
            expenseAmount = 0;
        } else {
            if (expenseIncomeOption.equals("Expense")) {
                expenseAmount = -Double.parseDouble(amount.getText().toString());
            } else {
                expenseAmount = Double.parseDouble(amount.getText().toString());
            }

        }

        expenseDescription = description.getText().toString();
        expenseIncomeCategory = expenseIncomeCategory;
        fullDate = dy + "-" + mh + "-" + yr;
        String coordinates = coordinateField.getText().toString();

        //Create db helper
        //ExpenseDbHelper mDbHelper = new ExpenseDbHelper(this);

        // SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and expense attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ExpenseEntry.COLUMN_OPTION, expenseIncomeOption);
        values.put(ExpenseEntry.COLUMN_DAY, expenseDay);
        values.put(ExpenseEntry.COLUMN_MONTH, expenseMonth);
        values.put(ExpenseEntry.COLUMN_YEAR, expenseYear);
        values.put(ExpenseEntry.COLUMN_AMOUNT, expenseAmount);
        values.put(ExpenseEntry.COLUMN_DESCRIPTION, expenseDescription);
        values.put(ExpenseEntry.COLUMN_CATEGORY, expenseIncomeCategory);
        values.put(ExpenseEntry.COLUMN_DATE, fullDate);
        values.put(ExpenseEntry.COLUMN_COORDINATES, coordinates);
        // Update existing row for expense in the database, returning the ID of that new row.


        // Update a row for an expense into the provider using the ContentResolver.
        // Use the {@link ExpenseEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access this record's data in the future.
        String path;
        if (oneRecordValues[0].equals("Expense")) {
            //path = "/" + getIntent().getExtras().getString("expenseID");
            path = "/";
            path = path.concat(oneRecordValues[5]);
        } else {
            //path = "/" + getIntent().getExtras().getString("incomeID");
            path = "/";
            path = path.concat(oneRecordValues[5]);
        }
        rowsUpdated = getContentResolver().update(Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, path), values, null, null);

        // Show a toast message depending on whether or not the insertion was successful
        if (rowsUpdated == 0) {

            // If rowsUpdated is 0, then there was an error with updating.
            //Toasts created in ExpenseProvider, specific to whether Date/Amount/Description empty
            //Toast.makeText(this, "Error with updating expense/income", Toast.LENGTH_LONG).show();
            String toDisplay = "Error in updating: " + String.valueOf(expenseIncomeOption) + ": " + expenseDescription;
            Toast.makeText(this, toDisplay, Toast.LENGTH_LONG).show();
            finish();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            String toDisplay = String.valueOf(expenseIncomeOption) + ": " + expenseDescription + " updated successfully.";
            Toast.makeText(this, toDisplay, Toast.LENGTH_LONG).show();
            finish();//Return to MainActivity
        }


    }


    private void setupAddButton() {


        if (oneRecordValues != null) {
            addButton.setText("Update");
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                    updateExpense();


                }
            });
        } else {

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                    insertExpense();


                }
            });
        }


    }


    private void setupDatePicker() {


        if (oneRecordValues != null || automatedValues != null) {
            if(automatedValues == null){
                String dateToSet = oneRecordValues[1];
                date.setText(dateToSet.replace("-", "/"));
                yr = Integer.parseInt(oneRecordValues[1].substring(oneRecordValues[1].lastIndexOf("-") + 1));
                mh = Integer.parseInt(oneRecordValues[1].substring(oneRecordValues[1].indexOf("-") + 1, oneRecordValues[1].lastIndexOf("-")));
                dy = Integer.parseInt(oneRecordValues[1].substring(0, oneRecordValues[1].indexOf("-")));
            }else if(oneRecordValues == null){
                date.setText(automatedValues[0] + "/" + automatedValues[1] + "/" + automatedValues[2]);
                yr = Integer.parseInt(automatedValues[2]);
                mh = Integer.parseInt(automatedValues[1]);
                dy = Integer.parseInt(automatedValues[0]);
            }

            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    yr = Integer.parseInt(oneRecordValues[1].substring(oneRecordValues[1].lastIndexOf("-")+1));
//                    mh = Integer.parseInt(oneRecordValues[1].substring(oneRecordValues[1].indexOf("-")+1, oneRecordValues[1].lastIndexOf("-")));
//                    dy = Integer.parseInt(oneRecordValues[1].substring(0, oneRecordValues[1].indexOf("-")));

                    datePickerDialog = new DatePickerDialog(ManualEntryActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int yrCal, int mhCal, int dyCal) {
                            yr = yrCal;
                            //Need to +1 b4 db since Jan is 0 for datePicker
                            mh = mhCal + 1;
                            dy = dyCal;
                            String dateToSet = dy + "/" + String.valueOf(Integer.valueOf(mh)) + "/" + yr;

                            date.setText(dateToSet);
                            datePicker.updateDate(yr, mh, dy);
                            datePickerDialog.updateDate(yr, mh, dy);

                            System.out.println("date print");

                        }


                    }, yr, mh - 1, dy);

                    //mh-1 since datepicker starts Jan from 0
                    datePickerDialog.updateDate(yr, mh - 1, dy);
                    //From db to datePicker need to -1 for month
                    datePickerDialog.show();
                }
            });


        } else {
            Calendar calendar = Calendar.getInstance();
            yr = calendar.get(Calendar.YEAR);
            mh = calendar.get(Calendar.MONTH) + 1;
            dy = calendar.get(Calendar.DAY_OF_MONTH);
            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    datePickerDialog = new DatePickerDialog(ManualEntryActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int yrCal, int mhCal, int dyCal) {
                            yr = yrCal;
                            mh = mhCal + 1;
                            dy = dyCal;
                            String dateToSet = dy + "/" + String.valueOf(Integer.valueOf(mh)) + "/" + yr;

                            date.setText(dateToSet);


                        }
                    }, yr, mh, dy);

                    datePickerDialog.updateDate(yr, mh - 1, dy);
                    datePickerDialog.show();
                }
            });
        }
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

        if (oneRecordValues != null) {
            String[] options = getResources().getStringArray(R.array.array_expense_income_options);
            //oneRecordValues[0] is the option of record selected (expense/income)
            if (oneRecordValues[0].equals(options[0])) {
                expenseIncomeOptionSpinner.setSelection(Integer.valueOf(0));

            } else {
                expenseIncomeOptionSpinner.setSelection(Integer.valueOf(1));
            }

        }
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

        if (oneRecordValues != null) {
            String[] categories = getResources().getStringArray(R.array.array_expense_income_category);
            Map<String, Integer> categoryToPosition = new HashMap<>();
            int i = 0;
            for (String category : categories) {
                categoryToPosition.put(category, i++);
            }
            //oneRecordValues[4] is the category of record selected (Bills, House, Eating out, Food, Transportation etc.. )
            expenseIncomeCategorySpinner.setSelection(categoryToPosition.get(oneRecordValues[4]));

        }

    }

}

package com.example.android.budgetapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.example.android.budgetapplication.data.ExpenseContract;
import com.example.android.budgetapplication.data.ExpenseDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.budgetapplication.data.ExpenseContract.ExpenseEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent wayIntent = new Intent(MainActivity.this, WayActivity.class);

                startActivity(wayIntent);

            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Display all database records
        displayDatabaseInfo();

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }


    private void displayDatabaseInfo() {

        ExpandableListAdapter listAdapter;
        List<String> listDataHeader = new ArrayList<>();
        HashMap<String, List<String>> listDataChild = new HashMap<>();

//      No need to get ExpenseDbHelper when using ContentResolver and ContentProvider
//        // To access our database, we instantiate our subclass of SQLiteOpenHelper
//        // and pass the context, which is the current activity.
//        ExpenseDbHelper mDbHelper = new ExpenseDbHelper(this);
//
//        // Create and/or open a database to read from it
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Columns to retrieve during query
        String[] projection = {
                ExpenseEntry._ID,
                ExpenseEntry.COLUMN_OPTION,
                ExpenseEntry.COLUMN_DAY,
                ExpenseEntry.COLUMN_MONTH,
                ExpenseEntry.COLUMN_YEAR,
                ExpenseEntry.COLUMN_AMOUNT,
                ExpenseEntry.COLUMN_DESCRIPTION,
                ExpenseEntry.COLUMN_CATEGORY,
                ExpenseEntry.COLUMN_DATE
        };


        /*
        Perform query on content provider using content resolver using URI.
        Use the {@link
         */

        //To delete row entries
//        int  id = 1;
//        for(int i = 1; i<7; i++){
//            int cursor = getContentResolver().delete(
//                    Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, String.valueOf(i)),
//                    null,
//                    null
//                    );
//        }


        Cursor cursor = getContentResolver().query(
                ExpenseEntry.CONTENT_URI,
                projection,
                null,
                null,
                ExpenseEntry.COLUMN_YEAR + " DESC,  " + ExpenseEntry.COLUMN_MONTH + " DESC, " + ExpenseEntry.COLUMN_DAY + " DESC"

        );

        //Extract all unique dates from expenses table for data header for "Day" view
        //Map each list header to a list of child data (expense/income categories)
        List<String> expenseIncomeCategory = Arrays.asList(getResources().getStringArray(R.array.array_expense_income_category));
        HashSet<String> uniqueDates = new HashSet<>();
        while (cursor.moveToNext()) {
            int dateColIdx = cursor.getColumnIndex(ExpenseEntry.COLUMN_DATE);
            String curCursorDate = cursor.getString(dateColIdx);
            if (!uniqueDates.contains(curCursorDate)) {
                listDataHeader.add(curCursorDate);
                listDataChild.put(curCursorDate, expenseIncomeCategory);
            }
            uniqueDates.add(curCursorDate);
        }


        //ExpandableListView to populate, use ExapandableListAdapter
//        ExpandableListView list = (ExpandableListView)findViewById(R.id.list);
//        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
//        list.setAdapter(listAdapter);


//        //List view to populate, use CursorAdapter
//        ListView list = (ListView)findViewById(R.id.list);
//        //Adapter to create list item view for each row of data in cursor
//        ExpenseCursorAdapter adapter = new ExpenseCursorAdapter(this, cursor);
//
//        //Attach adapter to list view
//        list.setAdapter(adapter);

        //2 level ExpandableListView to populate, use ParentLevelAdapter and SecondLevelAddapter
        Map<String, Map<String, Cursor>> dateCatExpense = getDateCategoryData(uniqueDates, expenseIncomeCategory, cursor, projection);

        // Init top level data
        //Top levl data is uniqueDate
        ExpandableListView mExpandableListView = (ExpandableListView) findViewById(R.id.list);
        if (mExpandableListView != null) {
            //pass list of unique dates as listDataHeader
            ParentLevelAdapter parentLevelAdapter = new ParentLevelAdapter(this, listDataHeader, dateCatExpense);
            mExpandableListView.setAdapter(parentLevelAdapter);
        }

    }

    private Map<String, Map<String, Cursor>> getDateCategoryData(Set<String> uniqueDates, List<String> expenseIncomeCategory, Cursor cursor, String[] projection) {

        //Get Expenses for each category for each date
        /*
        22/10/2020->map{Bills ----> Bill expense/income 1 for 22/10/2020
                            ----> Bill expense/income 2 for 22/10/2020
                            ----> Bill expense/income 3 for 22/10/2020
                        Food ----> Food expense/income 1 for 22/10/2020
                            ----> Food expense/income 2 for 22/10/2020
                            ----> Food expense/income 3 for 22/10/2020
                            .
                            .
                            .
                        //end map}

            30/10/2020->map{Bills ----> Bill expense/income 1 for 30/10/2020
                                ----> Bill expense/income 2 for 30/10/2020
                                ----> Bill expense/income 3 for 30/10/2020
                            Food ----> Food expense/income 1 for 30/10/2020
                                ----> Food expense/income 2 for 30/10/2020
                                ----> Food expense/income 3 for 30/10/2020
                                .
                                .
                                .
                            //end map}
         */

        Map<String, Map<String, Cursor>> dateCatExpense = new HashMap<>();


        //For all unique dates
        for (String uniqueDate : uniqueDates) {


            //For each expense/income category, get data from db query
            Map<String, Cursor> oneDateCategoriesExpenses = new HashMap<>();
            for (String category : expenseIncomeCategory) {
                String path = "/" + uniqueDate + "/" + category;
                cursor = getContentResolver().query(
                        Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, path),
                        projection,
                        null,
                        null,
                        ExpenseEntry.COLUMN_YEAR + " DESC,  " + ExpenseEntry.COLUMN_MONTH + " DESC, " + ExpenseEntry.COLUMN_DAY + " DESC"

                );

                if (cursor.getCount() > 0) {
                    Log.e("MainActvity", String.valueOf("num of rows" + cursor.getCount()) + " " + uniqueDate + " " + category);
                    //1st loop
                    // Bills ----> Bill expense/income 1 for 30/10/2020
//                            ----> Bill expense/income 2 for 30/10/2020
//                            ----> Bill expense/income 3 for 30/10/2020
                    //2nd loop
                    //    Food ----> Food expense/income 1 for 30/10/2020
                    //                                ----> Food expense/income 2 for 30/10/2020
                    //                                ----> Food expense/income 3 for 30/10/2020
                    //
                    oneDateCategoriesExpenses.put(category, cursor);

                }


            }

            //map date to list of categories and their respective expense/income data
            dateCatExpense.put(uniqueDate, oneDateCategoriesExpenses);
        }

        return dateCatExpense;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_manual) {

            Intent ManualEntryIntent = new Intent(MainActivity.this, ManualEntryActivity.class);
            startActivity(ManualEntryIntent);
        } else if (id == R.id.action_image_recognition) {

            Intent ImageRecognitionEntryIntent = new Intent(MainActivity.this, ImageRecognitionEntryActivity.class);
            startActivity(ImageRecognitionEntryIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } //else if (id == R.id.nav_gallery) {

//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_tools) {
//
//        }
        else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}

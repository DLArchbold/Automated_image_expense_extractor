package com.example.android.budgetapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.example.android.budgetapplication.data.ExpenseContract;
import com.example.android.budgetapplication.data.ExpenseDbHelper;
import com.example.android.budgetapplication.data.ExpenseProvider;
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
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ViewPager mSlideViewPager;
    private TextView mDotLayout;
    private SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//
//                Intent wayIntent = new Intent(MainActivity.this, WayActivity.class);
//
//                startActivity(wayIntent);
//
//            }
//        });


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
//        List<String> listDataHeader = new ArrayList<>();
//        HashMap<String, List<String>> listDataChild = new HashMap<>();

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


////        Cursor cursor = getContentResolver().query(
////                ExpenseEntry.CONTENT_URI,
////                projection,
////                null,
////                null,
////                ExpenseEntry.COLUMN_YEAR + " DESC,  " + ExpenseEntry.COLUMN_MONTH + " DESC, " + ExpenseEntry.COLUMN_DAY + " DESC"
////
////        );
//
//        Cursor cursor = getContentResolver().query(
//                Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, "Income"),
//                projection,
//                null,
//                null,
//                ExpenseEntry.COLUMN_YEAR + " ASC,  " + ExpenseEntry.COLUMN_MONTH + " ASC, " + ExpenseEntry.COLUMN_DAY + " ASC"
//
//        );
//
//        //Extract all unique dates from expenses table for data header for "Day" view
//        //Map each list header to a list of child data (expense/income categories)
//        List<String> expenseIncomeCategory = Arrays.asList(getResources().getStringArray(R.array.array_expense_income_category));
//        LinkedHashSet<String> uniqueDates = new LinkedHashSet<>();
//        while (cursor.moveToNext()) {
//            int dateColIdx = cursor.getColumnIndex(ExpenseEntry.COLUMN_DATE);
//            String curCursorDate = cursor.getString(dateColIdx);
//
//            if (!uniqueDates.contains(curCursorDate)) {
//
//                listDataHeader.add(curCursorDate);
//                listDataChild.put(curCursorDate, expenseIncomeCategory);
//            }
//            uniqueDates.add(curCursorDate);
//        }
//
//
//        //ExpandableListView to populate, use ExapandableListAdapter
////        ExpandableListView list = (ExpandableListView)findViewById(R.id.list);
////        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
////        list.setAdapter(listAdapter);
//
//
////        //List view to populate, use CursorAdapter
////        ListView list = (ListView)findViewById(R.id.list);
////        //Adapter to create list item view for each row of data in cursor
////        ExpenseCursorAdapter adapter = new ExpenseCursorAdapter(this, cursor);
////
////        //Attach adapter to list view
////        list.setAdapter(adapter);
//
//
//
////        //2 level ExpandableListView to populate, use ParentLevelAdapter and SecondLevelAddapter
////        Map<String, Map<String, Cursor>> dateCatExpense = getDateCategoryData(uniqueDates, expenseIncomeCategory, cursor, projection);
////
////        // Init top level data
////        //Top levl data is uniqueDate
////        ExpandableListView mExpandableListView = (ExpandableListView) findViewById(R.id.list);
////        if (mExpandableListView != null) {
////            //pass list of unique dates as listDataHeader
////            ParentLevelAdapter parentLevelAdapter = new ParentLevelAdapter(this, listDataHeader, dateCatExpense);
////            mExpandableListView.setAdapter(parentLevelAdapter);
////        }
//
//
//
//        /////////////////////////Adding ViewPager//////////////////////////////////////
//
//
//
//        //Array of OneLevelExpenseAdapters to store categories->expenses for 1 date and page for each array element
//        OneLevelExpenseAdapter [] pageAdapters = new OneLevelExpenseAdapter[uniqueDates.size()];
//        //dateCatExpense contains Dates->Categories->Cursors
//        LinkedHashMap<String, LinkedHashMap<String, Cursor>> dateCatExpense = getDateCategoryData(uniqueDates, expenseIncomeCategory, cursor, projection);
//        //Create an array of OneLevelExpenseAdapter objects for all dateCatExpense entries for each page's ExpandableListView
//        Iterator<LinkedHashMap.Entry<String, LinkedHashMap<String, Cursor>>> iterator = dateCatExpense.entrySet().iterator();
//        int ctr =0;
//        while(iterator.hasNext()){
//            //Move to next date->category->expenses map entry then get the categories->expenses
//            Map<String, Cursor> catsExpenses = iterator.next().getValue();
//            Object[] validCatArr = catsExpenses.keySet().toArray();
//
//            List<String> validCategories = new ArrayList<String>();
//            String valCatArray [] = Arrays.copyOf(validCatArr, validCatArr.length, String[].class);
//            List<String> validCatList = Arrays.asList(valCatArray);
//
//
//            pageAdapters[ctr] = new OneLevelExpenseAdapter(this, validCatList, catsExpenses);
//            ctr++;
//        }


        //Unique dates and their would be idx on 2d array that contains income/expense Adapters
        HashMap<String, Integer> datePosition =  mapDatesToIdx();
        HashMap<Integer, String> idxDate = mapIdxToDates(datePosition);

        //Obtain expense and income adapters corresponding to their day
        OneLevelExpenseAdapter[][] expenseIncomePageAdapters = new OneLevelExpenseAdapter[2][mapDatesToIdx().keySet().size()];
        expenseIncomePageAdapters= getIncomeOrExpenseAdapter("Expense", datePosition, expenseIncomePageAdapters);
        expenseIncomePageAdapters = getIncomeOrExpenseAdapter("Income", datePosition,expenseIncomePageAdapters );

        //Split expense and income adapters
        OneLevelExpenseAdapter[] expensePageAdapters = expenseIncomePageAdapters[0];
        OneLevelExpenseAdapter[] incomePageAdapters = expenseIncomePageAdapters[1];

        //Fill array for date balances for each page
        double[] balanceForEachDate = dateBalances(datePosition);

        //Assign SliderAdapter to ViewPager after readying data in SliderAdapter
        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        sliderAdapter = new SliderAdapter(this, expensePageAdapters, incomePageAdapters, idxDate, balanceForEachDate);
        mSlideViewPager.setAdapter(sliderAdapter);
        //view pager start on last page for
        mSlideViewPager.setCurrentItem(Math.max(expensePageAdapters.length, incomePageAdapters.length));

    }

    private HashMap<Integer, String> mapIdxToDates(HashMap<String, Integer> mapDatesToIdx){
        HashMap<Integer, String> idxToDates = new HashMap<>();
        for(String date:mapDatesToIdx.keySet()){
            idxToDates.put(mapDatesToIdx.get(date), date);
        }
        return idxToDates;
    }

    private HashMap<String, Integer> mapDatesToIdx (){

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

        Cursor cursor = getContentResolver().query(
                ExpenseEntry.CONTENT_URI,
                projection,
                null,
                null,
                ExpenseEntry.COLUMN_YEAR + " ASC,  " + ExpenseEntry.COLUMN_MONTH + " ASC, " + ExpenseEntry.COLUMN_DAY + " ASC"

        );

        //Extract all unique dates from expenses table for data header for "Day" view
        //Map each list header to a list of child data (expense/income categories)
        int pos = 0;
        HashMap<String, Integer> datePos = new HashMap<String, Integer>();
        while (cursor.moveToNext()) {
            int dateColIdx = cursor.getColumnIndex(ExpenseEntry.COLUMN_DATE);
            String curCursorDate = cursor.getString(dateColIdx);

            if (!datePos.containsKey(curCursorDate)) {
                datePos.put(curCursorDate, pos++ );
            }

        }

        return datePos;

    }

    private OneLevelExpenseAdapter[][] getIncomeOrExpenseAdapter(String expenseOrIncome, HashMap<String, Integer> datePosition, OneLevelExpenseAdapter[][] expenseIncomePageAdapters){

        List<String> listDataHeader = new ArrayList<>();
        HashMap<String, List<String>> listDataChild = new HashMap<>();
//        Cursor cursor = getContentResolver().query(
//                ExpenseEntry.CONTENT_URI,
//                projection,
//                null,
//                null,
//                ExpenseEntry.COLUMN_YEAR + " DESC,  " + ExpenseEntry.COLUMN_MONTH + " DESC, " + ExpenseEntry.COLUMN_DAY + " DESC"
//
//        );

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

        Cursor cursor = getContentResolver().query(
                Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, expenseOrIncome),
                projection,
                null,
                null,
                ExpenseEntry.COLUMN_YEAR + " ASC,  " + ExpenseEntry.COLUMN_MONTH + " ASC, " + ExpenseEntry.COLUMN_DAY + " ASC"

        );

        //Extract all unique dates from expenses table for data header for "Day" view
        //Map each list header to a list of child data (expense/income categories)
        List<String> expenseIncomeCategory = Arrays.asList(getResources().getStringArray(R.array.array_expense_income_category));
        LinkedHashSet<String> uniqueDates = new LinkedHashSet<>();
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



//        //2 level ExpandableListView to populate, use ParentLevelAdapter and SecondLevelAddapter
//        Map<String, Map<String, Cursor>> dateCatExpense = getDateCategoryData(uniqueDates, expenseIncomeCategory, cursor, projection);
//
//        // Init top level data
//        //Top levl data is uniqueDate
//        ExpandableListView mExpandableListView = (ExpandableListView) findViewById(R.id.list);
//        if (mExpandableListView != null) {
//            //pass list of unique dates as listDataHeader
//            ParentLevelAdapter parentLevelAdapter = new ParentLevelAdapter(this, listDataHeader, dateCatExpense);
//            mExpandableListView.setAdapter(parentLevelAdapter);
//        }



        /////////////////////////Adding ViewPager//////////////////////////////////////



        //Array of OneLevelExpenseAdapters to store categories->expenses for 1 date and page for each array element
        OneLevelExpenseAdapter [] pageAdapters = new OneLevelExpenseAdapter[uniqueDates.size()];
        //dateCatExpense contains Dates->Categories->Cursors
        Object[] dateCatExpense = getDateCategoryData(uniqueDates, expenseIncomeCategory, cursor, projection, expenseOrIncome);
        LinkedHashMap<String, LinkedHashMap<String, Cursor>> catExpense =  (LinkedHashMap<String, LinkedHashMap<String, Cursor>>) dateCatExpense[0];
        LinkedHashMap<String, LinkedHashMap<String, Cursor>> catSum =  (LinkedHashMap<String, LinkedHashMap<String, Cursor>>) dateCatExpense[1];
        //LinkedHashMap<String, LinkedHashMap<String, Cursor>> dateCatExpense = getDateCategoryData(uniqueDates, expenseIncomeCategory, cursor, projection, expenseOrIncome);
        //Create an array of OneLevelExpenseAdapter objects for all dateCatExpense entries for each page's ExpandableListView
        Iterator<LinkedHashMap.Entry<String, LinkedHashMap<String, Cursor>>> iteratorCatExpense = catExpense.entrySet().iterator();
        Iterator<LinkedHashMap.Entry<String, LinkedHashMap<String, Cursor>>> iteratorCatSum = catSum.entrySet().iterator();
        int ctr =0;
        while(iteratorCatExpense.hasNext() && iteratorCatSum.hasNext()){
            //Move to next date->category->expenses map entry then get the categories->expenses
            String curDate = iteratorCatExpense.next().getKey();
            LinkedHashMap<String, Cursor> catsExpenses = catExpense.get(curDate);
            Object[] validCatArr = catsExpenses.keySet().toArray();
            LinkedHashMap<String, Cursor> catsSum = catSum.get(curDate);

            //Create 1 adapter for 1 page which rep. 1 day's expense/income
            List<String> validCategories = new ArrayList<String>();
            String valCatArray [] = Arrays.copyOf(validCatArr, validCatArr.length, String[].class);
            List<String> validCatList = Arrays.asList(valCatArray);
            pageAdapters[ctr] = new OneLevelExpenseAdapter(this, validCatList, catsExpenses, catsSum, expenseOrIncome);

            //Expense pageAdapters fill column 0, while income page adapters fill column 1. Each row represents a day.
            //which may have either or both expenses/income
            int curDatePosition = datePosition.get(curDate);
            if(expenseOrIncome.equals("Expense")){
                expenseIncomePageAdapters[0][curDatePosition] = pageAdapters[ctr];
            }else{ //if it's income
                expenseIncomePageAdapters[1][curDatePosition] = pageAdapters[ctr];
            }

            ctr++;
        }
        return expenseIncomePageAdapters;
    }

    private double [] dateBalances (HashMap<String, Integer> datePosition){
        String[] projection =  { ExpenseEntry.COLUMN_DATE , ExpenseEntry.COLUMN_AMOUNT  };
        Cursor balancesCursor;
        balancesCursor = getContentResolver().query(
                ExpenseEntry.CONTENT_URI,
                projection,
                null,
                null,
                null

        );

        double[] balanceForEachDate =new double[datePosition.size()];


        balancesCursor.moveToFirst();
        int dateColIdx = balancesCursor.getColumnIndex(ExpenseEntry.COLUMN_DATE);
        int amtColIdx = balancesCursor.getColumnIndex(ExpenseEntry.COLUMN_AMOUNT);
        for( int i = 0; i<balancesCursor.getCount(); i++ ){

            String date = balancesCursor.getString(dateColIdx);
            double amt = balancesCursor.getDouble(amtColIdx);
            System.out.println("date print: " + date);
            if(date.equals("20-2-2021")){
                System.out.println("amt when 20-2-2021 " + amt);
            }
            balanceForEachDate[datePosition.get(date)] += amt;
            balancesCursor.moveToNext();
        }

        return balanceForEachDate;

    }


    private Object[]
    getDateCategoryData(Set<String> uniqueDates, List<String> expenseIncomeCategory,
                        Cursor cursor, String[] projection, String expenseOrIncome) {

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

//        String[] projection = {
//                ExpenseEntry._ID,
//                ExpenseEntry.COLUMN_OPTION,
//                ExpenseEntry.COLUMN_DAY,
//                ExpenseEntry.COLUMN_MONTH,
//                ExpenseEntry.COLUMN_YEAR,
//                ExpenseEntry.COLUMN_AMOUNT,
//                ExpenseEntry.COLUMN_DESCRIPTION,
//                ExpenseEntry.COLUMN_CATEGORY,
//                ExpenseEntry.COLUMN_DATE
//        };

        String[] sumProjection =  { "sum(" + ExpenseEntry.COLUMN_AMOUNT  + ")"};
        Cursor sumCursor;
        LinkedHashMap<String, LinkedHashMap<String, Cursor>> dateCatExpense = new  LinkedHashMap <String, LinkedHashMap<String, Cursor>>();
        LinkedHashMap<String, LinkedHashMap<String, Cursor>>  dateCatOptionSum = new LinkedHashMap<String, LinkedHashMap<String, Cursor>>();
        //For all unique dates
        for (String uniqueDate : uniqueDates) {


            //For each expense/income category, get data from db query
            LinkedHashMap<String, Cursor> oneDateCategoriesExpenses = new LinkedHashMap<>();
            LinkedHashMap<String, Cursor> oneDateCategoriesOptionSum = new LinkedHashMap<>();
            for (String category : expenseIncomeCategory) {
                String path = "/" + uniqueDate + "/" + category + "/" + expenseOrIncome;
                cursor = getContentResolver().query(
                        Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, path),
                        projection,
                        null,
                        null,
                        ExpenseEntry.COLUMN_YEAR + " DESC,  " + ExpenseEntry.COLUMN_MONTH + " DESC, " + ExpenseEntry.COLUMN_DAY + " DESC"

                );

                //Get sum for individual category and option
                sumCursor = getContentResolver().query(
                        Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, path),
                        sumProjection,
                        null,
                        null,
                        null

                );
//                sumCursor.moveToNext();
//                int sumColumnIndex = sumCursor.getColumnIndex(sumCursor.getColumnName(0));
//                System.out.println(sumCursor.getCount() + " "+ sumColumnIndex);
//                String sum = sumCursor.getString(sumColumnIndex);
//                System.out.println("sumCursor print: " +" sum: " + sum + " uniqueDate: " + uniqueDate + " category: " + category + " expenseOrIncome: "
//                + expenseOrIncome);


                if (cursor.getCount() > 0 && sumCursor.getCount()>0) {
                    Log.e("MainActvity", String.valueOf("num of rows" + cursor.getCount()) + " " + uniqueDate + " " + category);
                    //For 1 date
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

                    //For 1 date
                    //Bills(expenses/income) -> sum
                    //Transport (expenses/income)->sum
                    oneDateCategoriesOptionSum.put(category, sumCursor);
                }


            }

            //map date to list of categories and their respective expense/income data
            dateCatExpense.put(uniqueDate, oneDateCategoriesExpenses);
            //For each date
            //Date 1-> Bills(expenses/income)->sum
            //         ->Transport (expenses/income) ->sum)
            //Date 2-> Bills(expenses/income)->sum
            //         ->Transport (expenses/income) ->sum)
            dateCatOptionSum.put(uniqueDate, oneDateCategoriesOptionSum);
        }
        Object[] expenseAndSumMaps= new  Object[2];
        expenseAndSumMaps[0] = dateCatExpense ;
        expenseAndSumMaps[1] = dateCatOptionSum;
        return  expenseAndSumMaps;
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

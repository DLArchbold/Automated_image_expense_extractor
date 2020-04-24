package com.example.android.budgetapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.android.budgetapplication.data.ExpenseContract;
import com.example.android.budgetapplication.data.ExpenseDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import com.example.android.budgetapplication.data.ExpenseContract.ExpenseEntry;
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
        displayDatabaseInfo();

    }
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        ExpenseDbHelper mDbHelper = new ExpenseDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                ExpenseEntry._ID,
         ExpenseEntry.COLUMN_OPTION,
        ExpenseEntry.COLUMN_DAY,
        ExpenseEntry.COLUMN_MONTH,
        ExpenseEntry.COLUMN_YEAR,
        ExpenseEntry.COLUMN_AMOUNT,
        ExpenseEntry.COLUMN_DESCRIPTION,
        ExpenseEntry.COLUMN_CATEGORY

        };

        Cursor cursor = db.query(
                ExpenseEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );


        TextView displayView = (TextView) findViewById(R.id.text_view_main);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).

            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - name - breed - gender - weight
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The Expenses table contains " + cursor.getCount() + " expenses/income.\n\n");
            displayView.append(ExpenseEntry._ID + " - " +
                    ExpenseEntry.COLUMN_OPTION + " - " +
                    ExpenseEntry.COLUMN_DAY + " - " +
                    ExpenseEntry.COLUMN_MONTH + " - " +
                    ExpenseEntry.COLUMN_YEAR + " - " +
                    ExpenseEntry.COLUMN_AMOUNT + " - " +
                    ExpenseEntry.COLUMN_DESCRIPTION + " - " +
                    ExpenseEntry.COLUMN_CATEGORY + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(ExpenseEntry._ID);
            int optionColumnIndex = cursor.getColumnIndex(ExpenseEntry.COLUMN_OPTION);
            int dayColumnIndex = cursor.getColumnIndex(ExpenseEntry.COLUMN_DAY);
            int monthColumnIndex = cursor.getColumnIndex(ExpenseEntry.COLUMN_MONTH);
            int yearColumnIndex = cursor.getColumnIndex(ExpenseEntry.COLUMN_YEAR);
            int amountColumnIndex = cursor.getColumnIndex(ExpenseEntry.COLUMN_AMOUNT);
            int descriptionColumnIndex = cursor.getColumnIndex(ExpenseEntry.COLUMN_DESCRIPTION);
            int categoryColumnIndex = cursor.getColumnIndex(ExpenseEntry.COLUMN_CATEGORY);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentOption = cursor.getString(optionColumnIndex);
                String currentDay = cursor.getString(dayColumnIndex);
                String currentMonth = cursor.getString(monthColumnIndex);
                String currentYear = cursor.getString(yearColumnIndex);
                double currentAmount = cursor.getDouble(amountColumnIndex);
                String currentDescription = cursor.getString(descriptionColumnIndex);
                String currentCategory = cursor.getString(categoryColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentOption + " - " +
                        currentDay + " - " +
                        currentMonth + " - " +
                        currentYear+ " - " +
                        currentAmount+ " - " +
                        currentDescription+ " - " +
                        currentCategory));
            }
        } finally {

            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
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

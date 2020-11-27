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
import android.widget.ListView;
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
                this, drawer, toolbar,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        /*
        Perform query on content provider using content resolver using URI.
        Use the {@link
         */
        Cursor cursor = getContentResolver().query(
                ExpenseEntry.CONTENT_URI,
                projection,
                null,
                null,
                null

        );

        //List view to populate
        ListView list = (ListView)findViewById(R.id.list);

        //Adapter to create list item view for each row of data in cursor
        ExpenseCursorAdapter adapter = new ExpenseCursorAdapter(this, cursor);

        //Attach adapter to list view
        list.setAdapter(adapter);
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
        }else if (id == R.id.action_manual){

            Intent ManualEntryIntent = new Intent(MainActivity.this, ManualEntryActivity.class);
            startActivity(ManualEntryIntent);
        }else if (id == R.id.action_image_recognition){

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

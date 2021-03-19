package com.example.android.budgetapplication;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android.budgetapplication.adapters.BudgetAdapter;
import com.example.android.budgetapplication.data.BudgetContract;

public class BudgetListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_budget_list);

        //Toolbar toolbar = findViewById(R.id.toolbar_activity_budget_list);
        //setSupportActionBar(toolbar);

        displayBudgets();

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayBudgets();
    }
    private void displayBudgets() {

        final String[] projection = new String[]{
                BudgetContract.BudgetEntry._ID,
                BudgetContract.BudgetEntry.COLUMN_START_DAY,
                BudgetContract.BudgetEntry.COLUMN_START_MONTH,
                BudgetContract.BudgetEntry.COLUMN_START_YEAR,
                BudgetContract.BudgetEntry.COLUMN_START_DATE,
                BudgetContract.BudgetEntry.COLUMN_END_DAY,
                BudgetContract.BudgetEntry.COLUMN_END_MONTH,
                BudgetContract.BudgetEntry.COLUMN_END_YEAR,
                BudgetContract.BudgetEntry.COLUMN_END_DATE,
                BudgetContract.BudgetEntry.COLUMN_SPEND_LIMIT,
                BudgetContract.BudgetEntry.COLUMN_CATEGORY

        };



        Cursor budgetsCursor = getApplicationContext().getContentResolver().query(BudgetContract.BudgetEntry.CONTENT_URI, projection, null, null,  BudgetContract.BudgetEntry._ID + " DESC" );

        BudgetAdapter cursorAdapter = new BudgetAdapter(this, budgetsCursor, 1);

        ListView listView = findViewById(R.id.budget_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                  TextView budgetIdField = (TextView)view.findViewById(R.id.budget_id);
                  String budgetId = budgetIdField.getText().toString();
                  String path = "/" + budgetId;
                Uri uri = Uri.withAppendedPath(BudgetContract.BudgetEntry.CONTENT_URI, path);
                Cursor singleBudgetCursor = getContentResolver().query(uri, projection, null, null, null);

                singleBudgetCursor.moveToFirst();
                budgetId = budgetId;
                String startDate = singleBudgetCursor.getString(singleBudgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_START_DATE));
                String endDate = singleBudgetCursor.getString(singleBudgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_END_DATE));
                String spendLimit = singleBudgetCursor.getString(singleBudgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_SPEND_LIMIT));
                String category = singleBudgetCursor.getString(singleBudgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_CATEGORY));
                String[] oneBudgetValues = new String[]{budgetId, startDate, endDate, spendLimit, category};


                Intent manualBudgetActivityIntent = new Intent(getApplicationContext(), ManualBudgetActivity.class);
                manualBudgetActivityIntent.putExtra("oneBudgetValues", oneBudgetValues);
                getApplicationContext().startActivity(manualBudgetActivityIntent);
            }
        });
        listView.setAdapter(cursorAdapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.budget_list_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_budget) {
            Intent manualBudgetActivityIntent = new Intent(this, ManualBudgetActivity.class);
            startActivity(manualBudgetActivityIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}

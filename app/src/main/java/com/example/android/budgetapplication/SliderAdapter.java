package com.example.android.budgetapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.example.android.budgetapplication.data.ExpenseContract.ExpenseEntry;

import java.util.HashMap;

public class SliderAdapter extends PagerAdapter  {

    Context context;
    LayoutInflater layoutInflater;
    OneLevelExpenseAdapter[] expensePageAdapters;
    OneLevelExpenseAdapter[] incomePageAdapters;
    HashMap<Integer, String> idxDate;
    double[] balanceForEachDate;

    public SliderAdapter(Context context, OneLevelExpenseAdapter[] expensePageAdapters,
                         OneLevelExpenseAdapter[] incomePageAdapters, HashMap<Integer, String>idxDate,
                         double[] balanceForEachDate){
        this.context = context;
        this.expensePageAdapters = expensePageAdapters;
        this.incomePageAdapters = incomePageAdapters;
        this.idxDate = idxDate;
        this.balanceForEachDate =  balanceForEachDate;
    }



    //Changing background is like changing these headings/descriptions/icons

    @Override
    public int getCount() {
        //number of slides
        return Math.max(expensePageAdapters.length, incomePageAdapters.length);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.content_main, container, false);

//        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
//        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
//        TextView slideDescription = (TextView) view.findViewById(R.id.slide_desc);

        TextView date = (TextView) view.findViewById(R.id.date);
        date.setText(idxDate.get(position));

        TextView balance = (TextView) view.findViewById(R.id.balance);
        balance.setText(String.valueOf(balanceForEachDate[position]));


        ExpandableListView expenseExpandableListView = (ExpandableListView) view.findViewById(R.id.list);
        expenseExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Intent manIntent = new Intent(context, ManualEntryActivity.class);
                LinearLayout x = (LinearLayout)view;
                TextView t = (TextView)x.findViewById(R.id.expense_id);
                String expenseID = t.getText().toString();
                String[] projection = {
                        ExpenseEntry._ID,
                        ExpenseEntry.COLUMN_OPTION,
                        ExpenseEntry.COLUMN_DAY,
                        ExpenseEntry.COLUMN_MONTH,
                        ExpenseEntry.COLUMN_YEAR,
                        ExpenseEntry.COLUMN_AMOUNT,
                        ExpenseEntry.COLUMN_DESCRIPTION,
                        ExpenseEntry.COLUMN_CATEGORY,
                        ExpenseEntry.COLUMN_DATE,
                        ExpenseEntry.COLUMN_COORDINATES
                };
                String path = "/" + expenseID;
                Cursor singleExpense = context.getContentResolver().query(
                        Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, path),
                        projection,
                        null,
                        null,
                        null
                );
                singleExpense.moveToFirst();
                String option = singleExpense.getString(singleExpense.getColumnIndex(ExpenseEntry.COLUMN_OPTION));
                String date = singleExpense.getString(singleExpense.getColumnIndex(ExpenseEntry.COLUMN_DATE));
                String amount = singleExpense.getString(singleExpense.getColumnIndex(ExpenseEntry.COLUMN_AMOUNT));
                String description = singleExpense.getString(singleExpense.getColumnIndex(ExpenseEntry.COLUMN_DESCRIPTION));
                String category = singleExpense.getString(singleExpense.getColumnIndex(ExpenseEntry.COLUMN_CATEGORY));
                String coordinates = singleExpense.getString(singleExpense.getColumnIndex(ExpenseEntry.COLUMN_COORDINATES));
                String[] oneRecordValues = new String[]{option, date, amount, description, category, expenseID, coordinates};

                manIntent.putExtra("oneRecordValues", oneRecordValues);
                manIntent.putExtra("expenseID", expenseID);
                context.startActivity(manIntent);
                return false;
            }
        });
        TextView lbListHeaderExpenses = (TextView) view.findViewById(R.id.lblListHeaderExpenses);
        OneLevelExpenseAdapter expenseAdapter = null;
        if(expensePageAdapters[position] == null){
            expenseAdapter = null;
            expenseExpandableListView.setVisibility(View.GONE);
            lbListHeaderExpenses.setVisibility(View.GONE);
        }else{
            expenseAdapter = expensePageAdapters[position];
            lbListHeaderExpenses.setText("Expenses");
            expenseExpandableListView.setAdapter(expenseAdapter);
        }





        ExpandableListView incomeExpandableListView = (ExpandableListView) view.findViewById(R.id.income_list);
        //Open ManualEntryActivity with populated fields for editing/updating when one record is selected
        incomeExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Intent manIntent = new Intent(context, ManualEntryActivity.class);
                //incomePageAdapters[position].getChild(i, i1);
                LinearLayout x = (LinearLayout)view;
                TextView t = (TextView)x.findViewById(R.id.expense_id);
                String incomeID = t.getText().toString();
                String[] projection = {
                        ExpenseEntry._ID,
                        ExpenseEntry.COLUMN_OPTION,
                        ExpenseEntry.COLUMN_DAY,
                        ExpenseEntry.COLUMN_MONTH,
                        ExpenseEntry.COLUMN_YEAR,
                        ExpenseEntry.COLUMN_AMOUNT,
                        ExpenseEntry.COLUMN_DESCRIPTION,
                        ExpenseEntry.COLUMN_CATEGORY,
                        ExpenseEntry.COLUMN_DATE,
                        ExpenseEntry.COLUMN_COORDINATES
                };
                String path = "/" + incomeID;
                Cursor singleIncome = context.getContentResolver().query(
                        Uri.withAppendedPath(ExpenseEntry.CONTENT_URI, path),
                        projection,
                        null,
                        null,
                        null
                );
                singleIncome.moveToFirst();
                String option = singleIncome.getString(singleIncome.getColumnIndex(ExpenseEntry.COLUMN_OPTION));
                String date = singleIncome.getString(singleIncome.getColumnIndex(ExpenseEntry.COLUMN_DATE));
                String amount = singleIncome.getString(singleIncome.getColumnIndex(ExpenseEntry.COLUMN_AMOUNT));
                String description = singleIncome.getString(singleIncome.getColumnIndex(ExpenseEntry.COLUMN_DESCRIPTION));
                String category = singleIncome.getString(singleIncome.getColumnIndex(ExpenseEntry.COLUMN_CATEGORY));
                String coordinates = singleIncome.getString(singleIncome.getColumnIndex(ExpenseEntry.COLUMN_COORDINATES));
                String[] oneRecordValues = new String[]{option, date, amount, description, category, incomeID, coordinates};

                manIntent.putExtra("oneRecordValues", oneRecordValues);
                context.startActivity(manIntent);
                manIntent.putExtra("incomeID", incomeID);
                return false;
            }
        });
        TextView lbListHeaderIncome = (TextView) view.findViewById(R.id.lblListHeaderIncome);
        OneLevelExpenseAdapter incomeAdapter = null;
        if(incomePageAdapters[position] == null){
            incomeAdapter = null;
            incomeExpandableListView.setVisibility(View.GONE);
            lbListHeaderIncome.setVisibility(View.GONE);
        }else{
            incomeAdapter = incomePageAdapters[position];
            lbListHeaderIncome.setText("Income");
            incomeExpandableListView.setAdapter(incomeAdapter);
        }
//        slideImageView.setImageResource(slide_images[position]);
//        slideHeading.setText(slide_headings[position]);
//        slideDescription.setText(slide_descs[position]);

        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //Prevent creating multiple slides after last slide
        container.removeView((LinearLayout)object);
    }
}

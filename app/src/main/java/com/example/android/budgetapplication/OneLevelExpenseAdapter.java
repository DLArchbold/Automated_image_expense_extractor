package com.example.android.budgetapplication;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.android.budgetapplication.data.ExpenseContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneLevelExpenseAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private final List<String> validCategories;
//    private final Map<String, List<String>> mListData_SecondLevel_Map;
    private final Map<String, Cursor> catsExpenses;
    LayoutInflater mInflater;

    public OneLevelExpenseAdapter (Context mContext, List<String> validCategories, Map<String, Cursor> catsExpenses){
            this.mContext = mContext;
            this.validCategories = new ArrayList<>();
            this.validCategories.addAll(validCategories);
            this.catsExpenses = catsExpenses;
            // Init second level data
//            String[] mItemHeaders;
//            mListData_SecondLevel_Map = new HashMap<>();
//            int parentCount = validCategories.size();
//            for (int i = 0; i < parentCount; i++) {
//                //Get one unique date per iteration
//                String content = validCategories.get(i);
//                List<String> uniqueDatesList = new ArrayList<>();
//                uniqueDatesList.addAll(catsExpenses.get(content));
//                //Match date with categories that have data
//                mListData_SecondLevel_Map.put(content, uniqueDatesList);
//            }

    }

    @Override
    public int getGroupCount() {
        return validCategories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            //get number of categories that have data for this date

            String categoryName = this.validCategories.get(groupPosition);
            Cursor c = this.catsExpenses.get(categoryName);
            Log.e("OneLevelExpenseAdapter", String.valueOf("in OneLevelExpenseAdapter getChildrenCount():" + String.valueOf(c.getCount())  ));
            //ChildrenCount is record number for this category
            return c.getCount();
            //return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.validCategories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        //get a 1st level value (category) out of all categories for that date which have expense/income data
        String categoryName = this.validCategories.get(groupPosition);
        //Get expense/income cursor for 1 category for 1 date
        this.catsExpenses.get(categoryName).moveToNext();
        return this.catsExpenses.get(categoryName);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.drawer_list_group_second, parent, false);
            convertView = layoutInflater.inflate(R.layout.list_group, parent, false);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);
        lblListHeader.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        Log.e("OneLevelExpenseAdapter", String.valueOf("in OneLevelExpenseAdapter, getGroupView()" + headerTitle));
        lblListHeader.setTextColor(Color.CYAN);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Cursor cursor = (Cursor) getChild(groupPosition, childPosition);
        if(cursor.getPosition() == cursor.getCount()){
            Log.e("OneLevelExpenseAdapter", String.valueOf("in OneLevelExpenseAdapter getChildView() cursor.getPosition() == cursor.getCount() 1 "));
            cursor.moveToFirst();
        }

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, parent, false);
            if(cursor.getPosition() == cursor.getCount()){
                Log.e("OneLevelExpenseAdapter", String.valueOf("in OneLevelExpenseAdapter getChildView() in cursor.getPosition() == cursor.getCount() 2"));
                return convertView;
            }
        }

        Log.e("OneLevelExpenseAdapter", String.valueOf("in OneLevelExpenseAdapter getChildView()"));
        // Find individual views from the list_item layout that we want to modify
        TextView expenseIdTextView = (TextView) convertView.findViewById(R.id.expense_id);
        TextView optionTextView = (TextView) convertView.findViewById(R.id.option);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.date);
        TextView amountTextView = (TextView) convertView.findViewById(R.id.amount);
        TextView descriptionTextView = (TextView) convertView.findViewById(R.id.description);
        TextView categoryTextView = (TextView) convertView.findViewById(R.id.category);


        //Get column indices
        int expenseIDColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry._ID);
        int optionColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_OPTION);
        int dayColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_DAY);
        int monthColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_MONTH);
        int yearColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_YEAR);
        int amountColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_AMOUNT);
        int descriptionColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION);
        int categoryColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_CATEGORY);

        // Extract properties from cursor
        String expenseID = cursor.getString(expenseIDColumnIndex);
        String option = cursor.getString(optionColumnIndex);
        String day = cursor.getString(dayColumnIndex);
        String month = cursor.getString(monthColumnIndex);
        String year = cursor.getString(yearColumnIndex);
        String amount = cursor.getString(amountColumnIndex);
        String description = cursor.getString(descriptionColumnIndex);
        String category = cursor.getString(categoryColumnIndex);

        // Populate fields with extracted properties
        expenseIdTextView.setText(expenseID);
        optionTextView.setText(option);
        dateTextView.setText(day + " - " + month + " - " + year);
        amountTextView.setText(amount);
        descriptionTextView.setText(description);
        categoryTextView.setText(category);
        Log.e("OneLevelExpenseAdapter", String.valueOf("in OneLevelExpenseAdapter getChildView()" ));
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

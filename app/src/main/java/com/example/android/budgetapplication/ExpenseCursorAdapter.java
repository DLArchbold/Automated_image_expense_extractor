//package com.example.android.budgetapplication;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CursorAdapter;
//import android.widget.TextView;
//
//import com.example.android.budgetapplication.data.ExpenseContract;
//
///**
// * {@link ExpenseCursorAdapter} is an adapter for a list or grid view
// * that uses a {@link Cursor} of expenses data as its data source. This adapter knows
// * how to create list items for each row of pet data in the {@link Cursor}.
// */
//public class ExpenseCursorAdapter extends CursorAdapter {
//
//    /**
//     * Constructs a new {@link ExpenseCursorAdapter}.
//     *
//     * @param context The context
//     * @param c       The cursor from which to get the data.
//     */
//    public ExpenseCursorAdapter(Context context, Cursor c) {
//        super(context, c, 0 /* flags */);
//    }
//
//    /**
//     * Makes a new blank list item view. No data is set (or bound) to the views yet.
//     *
//     * @param context app context
//     * @param cursor  The cursor from which to get the data. The cursor is already
//     *                moved to the correct position.
//     * @param parent  The parent to which the new view is attached to
//     * @return the newly created list item view.
//     */
//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        // TODO: Fill out this method and return the list item view (instead of null)
//        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
//    }
//
//    /**
//     * This method binds the expense data (in the current row pointed to by cursor) to the given
//     * list item layout. For example, the name for the current expense can be set on the name TextView
//     * in the list item layout.
//     *
//     * @param view    Existing view, returned earlier by newView() method
//     * @param context app context
//     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
//     *                correct row.
//     */
//    @Override
//    public void bindView(View view, Context context, Cursor cursor) {
//        // TODO: Fill out this method
//        // Find indiviudal views from the list_item layout that we want to modify
//        TextView expenseIdTextView = (TextView) view.findViewById(R.id.expense_id);
//        TextView optionTextView = (TextView) view.findViewById(R.id.option);
//        TextView dateTextView = (TextView) view.findViewById(R.id.date);
//        TextView amountTextView = (TextView) view.findViewById(R.id.amount);
//        TextView descriptionTextView = (TextView) view.findViewById(R.id.description);
//        TextView categoryTextView = (TextView) view.findViewById(R.id.category);
//
//        //Get column indices
//        int expenseIDColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry._ID);
//        int optionColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_OPTION);
//        int dayColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_DAY);
//        int monthColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_MONTH);
//        int yearColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_YEAR);
//        int amountColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_AMOUNT);
//        int descriptionColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION);
//        int categoryColumnIndex = cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_CATEGORY);
//
//        // Extract properties from cursor
//        String expenseID = cursor.getString(expenseIDColumnIndex);
//        String option = cursor.getString(optionColumnIndex);
//        String day = cursor.getString(dayColumnIndex);
//        String month = cursor.getString(monthColumnIndex);
//        String year = cursor.getString(yearColumnIndex);
//        String amount = cursor.getString(amountColumnIndex);
//        String description = cursor.getString(descriptionColumnIndex);
//        String category = cursor.getString(categoryColumnIndex);
//
//        // Populate fields with extracted properties
//        expenseIdTextView.setText(expenseID);
//        optionTextView.setText(option);
//        dateTextView.setText(day + " - " + month + " - " + year);
//        amountTextView.setText(amount);
//        descriptionTextView.setText(description);
//        categoryTextView.setText(category);
//    }
//}
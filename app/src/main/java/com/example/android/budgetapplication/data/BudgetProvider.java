package com.example.android.budgetapplication.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.budgetapplication.data.ExpenseDbHelper;

import static com.example.android.budgetapplication.data.ExpenseDbHelper.LOG_TAG;

public class BudgetProvider extends ContentProvider {
    //DB helper object
    private ExpenseDbHelper mDbHelper;
    private static final int BUDGET = 1;
    private static final int BUDGET_ID = 2;
    private static final int EXPENSE_DATE_CATEGORY = 3;
    private static final int EXPENSES_OR_INCOME_ONLY = 4;
    private static final int DATE_CATEGORY_OPTION = 5;
    //private static final int INCOME_ONLY = 5;
    /** Tag for the log messages */
    public static final String LOG_TAG = BudgetProvider.class.getSimpleName();
    @Override
    public boolean onCreate() {
        mDbHelper = new ExpenseDbHelper(getContext());
        return false;
    }

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{

        sUriMatcher.addURI(BudgetContract.BudgetEntry.CONTENT_AUTHORITY, BudgetContract.BudgetEntry.PATH_BUDGET, BUDGET);
        sUriMatcher.addURI(BudgetContract.BudgetEntry.CONTENT_AUTHORITY, BudgetContract.BudgetEntry.PATH_BUDGET + "/#", BUDGET_ID);
        sUriMatcher.addURI(BudgetContract.BudgetEntry.CONTENT_AUTHORITY, BudgetContract.BudgetEntry.PATH_BUDGET + "/*" + "/*", EXPENSE_DATE_CATEGORY);
        sUriMatcher.addURI(BudgetContract.BudgetEntry.CONTENT_AUTHORITY, BudgetContract.BudgetEntry.PATH_BUDGET + "/*" , EXPENSES_OR_INCOME_ONLY);
        sUriMatcher.addURI(BudgetContract.BudgetEntry.CONTENT_AUTHORITY, BudgetContract.BudgetEntry.PATH_BUDGET + "/*" + "/*" + "/*" , DATE_CATEGORY_OPTION);
        //sUriMatcher.addURI(BudgetContract.BudgetEntry.CONTENT_AUTHORITY, BudgetContract.BudgetEntry.PATH_EXPENSES + "/*" , INCOME_ONLY);
    }



    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        String strUri = uri.toString();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BUDGET:
                // For the BUDGET code, query the expense table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the expense table.
                // TODO: Perform database query on expense table
                cursor = database.query(BudgetContract.BudgetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BUDGET_ID:
                // For the EXPENSE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.budgetapplication/expense/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BudgetContract.BudgetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the expenses table where the _id equals x to return a
                // Cursor containing that row of the table.
                cursor = database.query(BudgetContract.BudgetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case EXPENSE_DATE_CATEGORY:
                // For the EXPENSE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.budgetapplication/expense/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ExpenseContract.ExpenseEntry.COLUMN_DATE + "=? " + "AND " + ExpenseContract.ExpenseEntry.COLUMN_CATEGORY + "=?";

                //get expenses/date/category
                strUri = strUri.substring(strUri.indexOf("expenses"), strUri.length() );
                //get date/category
                strUri = strUri.substring(strUri.indexOf("/")+1);
                // {date, category}
                selectionArgs = strUri.split("/");


                // This will perform a query on the expenses table where the date and expense/income category equals the
                // selectionArgs to return a Cursor containing rows of the table.
                cursor = database.query(ExpenseContract.ExpenseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case EXPENSES_OR_INCOME_ONLY:
                selection = ExpenseContract.ExpenseEntry.COLUMN_OPTION + "=? " ;

                // {option}
                selectionArgs = new String[]{strUri.substring(strUri.lastIndexOf("/")+1)};

                //For this case, reutrn cursor of balances for each day
                if(selectionArgs[0]  == "balances"){
                    cursor = database.query(ExpenseContract.ExpenseEntry.TABLE_NAME, projection, null, null,
                            ExpenseContract.ExpenseEntry.COLUMN_DATE, null, sortOrder);
                    return cursor;
                }
                // This will perform a query on the expenses table where the date and expense/income category equals the
                // selectionArgs to return a Cursor containing rows of the table.
                cursor = database.query(ExpenseContract.ExpenseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case DATE_CATEGORY_OPTION:
                selection = ExpenseContract.ExpenseEntry.COLUMN_DATE + "=? " + "AND " + ExpenseContract.ExpenseEntry.COLUMN_CATEGORY + "=?"+ "AND " + ExpenseContract.ExpenseEntry.COLUMN_OPTION + "=?";


                //get expenses/date/category/option
                strUri = strUri.substring(strUri.indexOf("expenses"), strUri.length() );
                //get date/category/option
                strUri = strUri.substring(strUri.indexOf("/")+1);
                // {date, category, option}
                selectionArgs = strUri.split("/");
                // This will perform a query on the expenses table where the {date, expense/income category and
                // option} equals the selectionArgs to return a Cursor containing rows of the table.
                cursor = database.query(ExpenseContract.ExpenseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BUDGET:
                return insertBudget(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }



    }
    /**
     * Insert an expense into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBudget(Uri uri, ContentValues values) {




        String startDay = values.getAsString(BudgetContract.BudgetEntry.COLUMN_START_DAY);
        String startMonth = values.getAsString(BudgetContract.BudgetEntry.COLUMN_START_MONTH);
        String startYear = values.getAsString(BudgetContract.BudgetEntry.COLUMN_START_YEAR);
        String startDate = values.getAsString(BudgetContract.BudgetEntry.COLUMN_START_DATE);
        String endDay = values.getAsString(BudgetContract.BudgetEntry.COLUMN_END_DAY);
        String endMonth = values.getAsString(BudgetContract.BudgetEntry.COLUMN_END_MONTH);
        String endYear = values.getAsString(BudgetContract.BudgetEntry.COLUMN_END_YEAR);
        String endDate = values.getAsString(BudgetContract.BudgetEntry.COLUMN_END_DATE);
        double spendLimit = values.getAsDouble(BudgetContract.BudgetEntry.COLUMN_SPEND_LIMIT);
        String category = values.getAsString(BudgetContract.BudgetEntry.COLUMN_CATEGORY);
        String errorMsg = null;
        //Data validation for Date, Amount and Description field
//        if(day.equals("0")){
//            errorMsg = "Error: " + option  + " date not set.";
//            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
//            return null;
//        }



        // TODO: Insert a new pet into the pets database table with the given ContentValues
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(BudgetContract.BudgetEntry.TABLE_NAME, null, values);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BUDGET:
                // Delete all rows that match the selection and selection args
                return database.delete(ExpenseContract.ExpenseEntry.TABLE_NAME, selection, selectionArgs);
            case BUDGET_ID:
                // Delete a single row given by the ID in the URI
                selection = BudgetContract.BudgetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(BudgetContract.BudgetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }



    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BUDGET:
                return updateBudget(uri, contentValues, selection, selectionArgs);
            case BUDGET_ID:
                // For the EXPENSE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BudgetContract.BudgetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBudget(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
          }


    }

    /**
     * Update expenses in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more expenses).
     * Return the number of rows that were successfully updated.
     */
    private int updateBudget(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //Check existing row values.
        // If the {@link ExpenseContract.ExpenseEntry.COLUMN_OPTION} key is present,
        // check that the name value is not null.
//        if (values.containsKey(ExpenseContract.ExpenseEntry.COLUMN_OPTION)) {
//            String option = values.getAsString(ExpenseContract.ExpenseEntry.COLUMN_OPTION);
//            if (option == null) {
//                throw new IllegalArgumentException("Error: Entry requires an option (Income/Expense)");
//            }
//        }
//
//        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
//        // check that the gender value is valid.
//        if (values.containsKey(ExpenseContract.ExpenseEntry.COLUMN_DAY)) {
//            Integer day = values.getAsInteger(ExpenseContract.ExpenseEntry.COLUMN_DAY);
//            if (day == null ) {
//                throw new IllegalArgumentException("Error: Entry requires that you select a day");
//            }
//        }
//
//        if (values.containsKey(ExpenseContract.ExpenseEntry.COLUMN_MONTH)) {
//            Integer month = values.getAsInteger(ExpenseContract.ExpenseEntry.COLUMN_MONTH);
//            if (month == null ) {
//                throw new IllegalArgumentException("Error: Entry requires that you select a month");
//            }
//        }
//
//        if (values.containsKey(ExpenseContract.ExpenseEntry.COLUMN_YEAR)) {
//            Integer year = values.getAsInteger(ExpenseContract.ExpenseEntry.COLUMN_YEAR);
//            if (year == null) {
//                throw new IllegalArgumentException("Error: Entry requires that you select a year)");
//            }
//        }


//
//        if (values.containsKey(ExpenseContract.ExpenseEntry.COLUMN_AMOUNT)) {
//            Double amount = values.getAsDouble(ExpenseContract.ExpenseEntry.COLUMN_AMOUNT);
//            if (amount == 0.00 ) {
//                throw new IllegalArgumentException("Error: Amount cannot be 0");
//            }else if (amount == null){
//                throw new IllegalArgumentException("Error: Please enter an amount.");
//            }
//        }
//        if (values.containsKey(ExpenseContract.ExpenseEntry.COLUMN_CATEGORY)) {
//            String category = values.getAsString(ExpenseContract.ExpenseEntry.COLUMN_CATEGORY);
//            if (category == null) {
//                throw new IllegalArgumentException("Error: Entry requires that you select a category)");
//            }
//        }
//
//        if (values.containsKey(ExpenseContract.ExpenseEntry.COLUMN_DATE)) {
//            String date = values.getAsString(ExpenseContract.ExpenseEntry.COLUMN_DATE);
//            if (date == null) {
//                throw new IllegalArgumentException("Error: Entry requires that you select a date)");
//            }
//        }

//        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
//        // check that the weight value is valid.
//        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
//            // Check that the weight is greater than or equal to 0 kg
//            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
//            if (weight != null && weight < 0) {
//                throw new IllegalArgumentException("Error: Entry requires that you enter a year)");
//            }
//        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        return database.update(BudgetContract.BudgetEntry.TABLE_NAME, values, selection, selectionArgs);

    }


}

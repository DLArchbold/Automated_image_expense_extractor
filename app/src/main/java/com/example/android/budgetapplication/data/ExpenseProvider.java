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

public class ExpenseProvider extends ContentProvider {
    //DB helper object
    private ExpenseDbHelper mDbHelper;
    private static final int EXPENSE = 1;
    private static final int EXPENSE_ID = 2;
    private static final int EXPENSE_DATE_CATEGORY = 3;
    private static final int EXPENSES_OR_INCOME_ONLY = 4;
    private static final int DATE_CATEGORY_OPTION = 5;
    //private static final int INCOME_ONLY = 5;
    /** Tag for the log messages */
    public static final String LOG_TAG = ExpenseProvider.class.getSimpleName();
    @Override
    public boolean onCreate() {
        mDbHelper = new ExpenseDbHelper(getContext());
        return false;
    }

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sUriMatcher.addURI(ExpenseContract.ExpenseEntry.CONTENT_AUTHORITY, ExpenseContract.ExpenseEntry.PATH_EXPENSES, EXPENSE);
        sUriMatcher.addURI(ExpenseContract.ExpenseEntry.CONTENT_AUTHORITY, ExpenseContract.ExpenseEntry.PATH_EXPENSES + "/#", EXPENSE_ID);
        sUriMatcher.addURI(ExpenseContract.ExpenseEntry.CONTENT_AUTHORITY, ExpenseContract.ExpenseEntry.PATH_EXPENSES + "/*" + "/*", EXPENSE_DATE_CATEGORY);
        sUriMatcher.addURI(ExpenseContract.ExpenseEntry.CONTENT_AUTHORITY, ExpenseContract.ExpenseEntry.PATH_EXPENSES + "/*" , EXPENSES_OR_INCOME_ONLY);
        sUriMatcher.addURI(ExpenseContract.ExpenseEntry.CONTENT_AUTHORITY, ExpenseContract.ExpenseEntry.PATH_EXPENSES + "/*" + "/*" + "/*" , DATE_CATEGORY_OPTION);
        //sUriMatcher.addURI(ExpenseContract.ExpenseEntry.CONTENT_AUTHORITY, ExpenseContract.ExpenseEntry.PATH_EXPENSES + "/*" , INCOME_ONLY);
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
            case EXPENSE:
                // For the EXPENSE code, query the expense table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the expense table.
                // TODO: Perform database query on expense table
                cursor = database.query(ExpenseContract.ExpenseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case EXPENSE_ID:
                // For the EXPENSE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.budgetapplication/expense/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ExpenseContract.ExpenseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the expenses table where the _id equals x to return a
                // Cursor containing that row of the table.
                cursor = database.query(ExpenseContract.ExpenseEntry.TABLE_NAME, projection, selection, selectionArgs,
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
            case EXPENSE:
                return insertExpense(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }



    }
    /**
     * Insert an expense into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertExpense(Uri uri, ContentValues values) {

        String day = values.getAsString(ExpenseContract.ExpenseEntry.COLUMN_DAY);
        double amount = values.getAsDouble(ExpenseContract.ExpenseEntry.COLUMN_AMOUNT);
        String description = values.getAsString(ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION);
        String option = values.getAsString(ExpenseContract.ExpenseEntry.COLUMN_OPTION);
        String errorMsg = null;

        //Data validation for Date, Amount and Description field
        if(day.equals("0")){
            errorMsg = "Error: " + option  + " date not set.";
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
            return null;
        }

        if( amount == 0){
            errorMsg = "Error: " + option + " amount is 0.";
            Toast.makeText(this.getContext(), errorMsg ,  Toast.LENGTH_SHORT).show();
            return null;
        }

        if(description.isEmpty()){
            errorMsg = "Error: " + option + " description is empty.";
            Toast.makeText(this.getContext(), errorMsg ,  Toast.LENGTH_SHORT).show();
            return null;
        }

        // TODO: Insert a new pet into the pets database table with the given ContentValues
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ExpenseContract.ExpenseEntry.TABLE_NAME, null, values);
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
            case EXPENSE:
                // Delete all rows that match the selection and selection args
                return database.delete(ExpenseContract.ExpenseEntry.TABLE_NAME, selection, selectionArgs);
            case EXPENSE_ID:
                // Delete a single row given by the ID in the URI
                selection = ExpenseContract.ExpenseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(ExpenseContract.ExpenseEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSE:
                return updateExpense(uri, contentValues, selection, selectionArgs);
            case EXPENSE_ID:
                // For the EXPENSE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ExpenseContract.ExpenseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateExpense(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    /**
     * Update expenses in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more expenses).
     * Return the number of rows that were successfully updated.
     */
    private int updateExpense(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //Check existing row values.
//        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
//        // check that the name value is not null.
//        if (values.containsKey(ExpenseContract.ExpenseEntry)) {
//            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
//            if (name == null) {
//                throw new IllegalArgumentException("Pet requires a name");
//            }
//        }
//
//        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
//        // check that the gender value is valid.
//        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
//            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
//            if (gender == null || !PetEntry.isValidGender(gender)) {
//                throw new IllegalArgumentException("Pet requires valid gender");
//            }
//        }
//
//        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
//        // check that the weight value is valid.
//        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
//            // Check that the weight is greater than or equal to 0 kg
//            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
//            if (weight != null && weight < 0) {
//                throw new IllegalArgumentException("Pet requires valid weight");
//            }
//        }
//
//        // No need to check the breed, any value is valid (including null).
//
//        // If there are no values to update, then don't try to update the database
//        if (values.size() == 0) {
//            return 0;
//        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        return database.update(ExpenseContract.ExpenseEntry.TABLE_NAME, values, selection, selectionArgs);

    }

}

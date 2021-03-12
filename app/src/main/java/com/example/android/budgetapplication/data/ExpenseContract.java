package com.example.android.budgetapplication.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ExpenseContract {

    private ExpenseContract() {
    }



    public static final class ExpenseEntry implements BaseColumns
    {
        public static final String CONTENT_AUTHORITY = "com.example.android.budgetapplication";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_EXPENSES = "expenses";
        public static final Uri  CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EXPENSES);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of expenses.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single expense.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSES;

        //Table fields
        public final static String TABLE_NAME = "expenses";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_OPTION = "option";
        public final static String COLUMN_DAY = "day";
        public final static String COLUMN_MONTH = "month";
        public final static String COLUMN_YEAR = "year";
        public final static String COLUMN_AMOUNT = "amount";
        public final static String COLUMN_DESCRIPTION = "description";
        public final static String COLUMN_CATEGORY = "category";
        public final static String COLUMN_DATE = "date";
        public final static String COLUMN_COORDINATES = "coordinates";




    }


}

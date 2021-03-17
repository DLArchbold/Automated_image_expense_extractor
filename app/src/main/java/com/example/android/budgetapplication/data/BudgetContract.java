package com.example.android.budgetapplication.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BudgetContract {

    private BudgetContract() {
    }



    public static final class BudgetEntry implements BaseColumns
    {
        public static final String CONTENT_AUTHORITY = "com.example.android.budgetapplication.ManualBudgetActivity";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_BUDGET = "budget";
        public static final Uri  CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BUDGET);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of expenses.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUDGET;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single expense.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUDGET;

        //Table fields
        public final static String TABLE_NAME = "budget";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_START_DAY = "start_day";
        public final static String COLUMN_START_MONTH = "start_month";
        public final static String COLUMN_START_YEAR = "start_year";
        public final static String COLUMN_START_DATE = "start_date";
        public final static String COLUMN_END_DAY = "end_day";
        public final static String COLUMN_END_MONTH = "end_month";
        public final static String COLUMN_END_YEAR = "end_year";
        public final static String COLUMN_END_DATE = "end_date";
        public final static String COLUMN_SPEND_LIMIT = "spend_limit";
        public final static String COLUMN_CATEGORY = "category";

    }


}

package com.example.android.budgetapplication.data;

import android.provider.BaseColumns;

public class ExpenseContract {

    private ExpenseContract() {
    }


    public static final class ExpenseEntry implements BaseColumns
    {
        public final static String TABLE_NAME = "expenses";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_OPTION = "option";
        public final static String COLUMN_DAY = "day";
        public final static String COLUMN_MONTH = "month";
        public final static String COLUMN_YEAR = "year";
        public final static String COLUMN_AMOUNT = "amount";
        public final static String COLUMN_DESCRIPTION = "description";
        public final static String COLUMN_CATEGORY = "category";
    }


}

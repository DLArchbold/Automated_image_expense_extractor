/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.budgetapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.budgetapplication.data.ExpenseContract.ExpenseEntry;
import com.example.android.budgetapplication.data.BudgetContract.BudgetEntry;

/**
 * Database helper for BudgeApplication app. Manages database creation and version management.
 */
public class ExpenseDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ExpenseDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "expenses.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 6;

    /**
     * Constructs a new instance of {@link ExpenseDbHelper}.
     *
     * @param context of the app
     */
    public ExpenseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the expenses table
        String SQL_CREATE_EXPENSE_TABLE = "CREATE TABLE " + ExpenseEntry.TABLE_NAME + " ("
                + ExpenseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ExpenseEntry.COLUMN_OPTION + " TEXT NOT NULL, "
                + ExpenseEntry.COLUMN_DAY + " INTEGER NOT NULL, "
                + ExpenseEntry.COLUMN_MONTH + " INTEGER NOT NULL, "
                + ExpenseEntry.COLUMN_YEAR + " INTEGER NOT NULL, "
                + ExpenseEntry.COLUMN_AMOUNT + " DOUBLE(5, 2),"
                + ExpenseEntry.COLUMN_DESCRIPTION + " TEXT, "
                + ExpenseEntry.COLUMN_CATEGORY + " TEXT, "
                +ExpenseEntry.COLUMN_DATE + " TEXT, "
                +ExpenseEntry.COLUMN_COORDINATES + " TEXT);";

        String SQL_CREATE_BUDGET_TABLE = "CREATE TABLE " + BudgetEntry.TABLE_NAME + " ("
                + BudgetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BudgetEntry.COLUMN_START_DAY + " INTEGER NOT NULL, "
                + BudgetEntry.COLUMN_START_MONTH + " INTEGER NOT NULL, "
                + BudgetEntry.COLUMN_START_YEAR + " INTEGER NOT NULL, "
                + BudgetEntry.COLUMN_START_DATE + " TEXT NOT NULL, "
                + BudgetEntry.COLUMN_END_DAY + " INTEGER NOT NULL, "
                + BudgetEntry.COLUMN_END_MONTH + " INTEGER NOT NULL, "
                + BudgetEntry.COLUMN_END_YEAR + " INTEGER NOT NULL, "
                + BudgetEntry.COLUMN_END_DATE + " TEXT NOT NULL, "
                + BudgetEntry.COLUMN_SPEND_LIMIT + " DOUBLE(5, 2) NOT NULL, "
                + BudgetEntry.COLUMN_CATEGORY + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_BUDGET_TABLE);
        db.execSQL(SQL_CREATE_EXPENSE_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //db.execSQL("DROP TABLE IF EXISTS "+ExpenseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+BudgetEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ExpenseEntry.TABLE_NAME);
        onCreate(db);

    }
}
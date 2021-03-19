package com.example.android.budgetapplication.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.budgetapplication.R;
import com.example.android.budgetapplication.data.BudgetContract;
import com.example.android.budgetapplication.data.ExpenseContract;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BudgetAdapter extends CursorAdapter {
    Context context;
    Cursor cursor;
    int flags;
    private static DecimalFormat df = new DecimalFormat("0.00");

    public BudgetAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.cursor = c;
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View toReturn = LayoutInflater.from(context).inflate(R.layout.budget_item, viewGroup, false);
        //toReturn.setOnClickListener(new view.listener);
        return toReturn;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView id = view.findViewById(R.id.budget_id);
        TextView startDate = view.findViewById(R.id.start_date_budget);
        TextView endDate = view.findViewById(R.id.end_date);
        TextView limit = view.findViewById(R.id.limit);
        TextView spent = view.findViewById(R.id.spent);
        TextView category = view.findViewById(R.id.category);
        TextView amountPerDay = view.findViewById(R.id.amount_per_day);


        //Get cursor col and values
        String idText = cursor.getString(cursor.getColumnIndex(BudgetContract.BudgetEntry._ID));
        String startDateText = cursor.getString(cursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_START_DATE));
        String endDateText = cursor.getString(cursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_END_DATE));
        String spendLimitText = cursor.getString(cursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_SPEND_LIMIT));
        spendLimitText = df.format(Double.parseDouble(spendLimitText));
        String categoryText = cursor.getString(cursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_CATEGORY));
        //String categoryText = cursor.getString(cursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_CATEGORY));



        int daysInBudgetPeriod = getDaysInBudgetPeriod(startDateText, endDateText);
        String spentAmt;
        Cursor spentAmountCursor = getSpentAmountCursor(context, cursor);
        int daysLeftForSpending = 0;
        if (spentAmountCursor.moveToFirst() == true) {
            spentAmt = spentAmountCursor.getString(spentAmountCursor.getColumnIndex("sum(amount)"));
            if (spentAmt == null) {
                spentAmt = "0";
                daysLeftForSpending = daysInBudgetPeriod;
            } else {

                //+1 to include start date
                daysLeftForSpending = daysInBudgetPeriod-getDaysWithSpending(context, cursor);
            }
        } else {
            spentAmt = "0";
            daysLeftForSpending = daysInBudgetPeriod;
        }


        String limitAndSpentDifference = df.format(Double.valueOf(spendLimitText)+Double.valueOf(spentAmt));

        String avlAmtPerDay = df.format(Double.parseDouble(limitAndSpentDifference)/daysLeftForSpending);

        //set Cursor values
        id.setText(idText);
        startDate.setText(startDateText);
        endDate.setText(endDateText);
        limit.setText(spendLimitText);
        category.setText(categoryText);
        spent.setText(spentAmt);
        amountPerDay.setText(String.valueOf(avlAmtPerDay));
        String test = startDateText + " " + endDateText + " " + spendLimitText + " " + categoryText;



    }

    private int getDaysWithSpending(Context context, Cursor budgetCursor) {
        String startDay = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_START_DAY));
        String startMonth = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_START_MONTH));
        String startYear = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_START_YEAR));

        String endDay = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_END_DAY));
        String endMonth = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_END_MONTH));
        String endYear = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_END_YEAR));
        String category = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_CATEGORY));

        String path = "/" + endDay + "/" + endMonth + "/" + endYear + "/" + startDay + "/" + startMonth + "/" + startYear + "/" + category + "/" + "getDaysWithSpending";


        Cursor daysWithSpendingCursor = context.getContentResolver().query(
                Uri.withAppendedPath(ExpenseContract.ExpenseEntry.CONTENT_URI, path),
                null,
                null,
                null,
                null
        );



        return daysWithSpendingCursor.getCount();

    }

    private int getDaysInBudgetPeriod(String startDate, String endDate) {

        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        startDate = startDate.replace("-", " ");
        endDate = endDate.replace("-", " ");
        double daysBetween = 0;
        try {
            Date dateBefore = myFormat.parse(startDate);
            Date dateAfter = myFormat.parse(endDate);
            long difference = dateAfter.getTime() - dateBefore.getTime();
            daysBetween = (difference / (1000 * 60 * 60 * 24))+1;
            /* You can also convert the milliseconds to days using this method
             * float daysBetween =
             *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
             */

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) daysBetween;
    }

    private Cursor getSpentAmountCursor(Context context, Cursor budgetCursor) {

        String startDay = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_START_DAY));
        String startMonth = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_START_MONTH));
        String startYear = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_START_YEAR));

        String endDay = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_END_DAY));
        String endMonth = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_END_MONTH));
        String endYear = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_END_YEAR));
        String category = budgetCursor.getString(budgetCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_CATEGORY));

        String path = "/" + endDay + "/" + endMonth + "/" + endYear + "/" + startDay + "/" + startMonth + "/" + startYear + "/" + category;


        Cursor spentCursor = context.getContentResolver().query(
                Uri.withAppendedPath(ExpenseContract.ExpenseEntry.CONTENT_URI, path),
                null,
                null,
                null,
                null
        );

        return spentCursor;
    }

}

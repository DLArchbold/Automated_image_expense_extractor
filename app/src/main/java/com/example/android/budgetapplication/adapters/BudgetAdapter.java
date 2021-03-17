package com.example.android.budgetapplication.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.budgetapplication.R;

public class BudgetAdapter extends CursorAdapter {

    public BudgetAdapter(Context context,Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        cursor.moveToNext();
        LayoutInflater.from(context).inflate(R.layout.budget_item, viewGroup, false);
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView startDate = view.findViewById(R.id.start_date);
        TextView endDate = view.findViewById(R.id.end_date);
        TextView limit = view.findViewById(R.id.limit);
        TextView spent = view.findViewById(R.id.spent);
        TextView category = view.findViewById(R.id.category);
        TextView amountPerDay = view.findViewById(R.id.amount_per_day);


        //Get cursor col and values


        //set Cursor values

    }
}

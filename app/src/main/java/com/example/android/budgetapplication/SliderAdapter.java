package com.example.android.budgetapplication;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.android.budgetapplication.R;

import org.w3c.dom.Text;

import java.util.HashMap;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    OneLevelExpenseAdapter[] expensePageAdapters;
    OneLevelExpenseAdapter[] incomePageAdapters;
    HashMap<Integer, String> idxDate;
    public SliderAdapter(Context context, OneLevelExpenseAdapter[] expensePageAdapters,
                         OneLevelExpenseAdapter[] incomePageAdapters, HashMap<Integer, String>idxDate ){
        this.context = context;
        this.expensePageAdapters = expensePageAdapters;
        this.incomePageAdapters = incomePageAdapters;
        this.idxDate = idxDate;
    }



    //Changing background is like changing these headings/descriptions/icons

    @Override
    public int getCount() {
        //number of slides
        return Math.max(expensePageAdapters.length, incomePageAdapters.length);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.content_main, container, false);

//        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
//        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
//        TextView slideDescription = (TextView) view.findViewById(R.id.slide_desc);

        TextView date = (TextView) view.findViewById(R.id.date);
        date.setText(idxDate.get(position));

        ExpandableListView expenseExpandableListView = (ExpandableListView) view.findViewById(R.id.list);
        TextView lbListHeaderExpenses = (TextView) view.findViewById(R.id.lblListHeaderExpenses);
        OneLevelExpenseAdapter expenseAdapter = null;
        if(expensePageAdapters[position] == null){
            expenseAdapter = null;
            lbListHeaderExpenses = null;
        }else{
            expenseAdapter = expensePageAdapters[position];
            lbListHeaderExpenses.setText("Expenses");
        }
        expenseExpandableListView.setAdapter(expenseAdapter);




        ExpandableListView incomeExpandableListView = (ExpandableListView) view.findViewById(R.id.income_list);

        TextView lbListHeaderIncome = (TextView) view.findViewById(R.id.lblListHeaderIncome);
        OneLevelExpenseAdapter incomeAdapter = null;
        if(incomePageAdapters[position] == null){
            incomeAdapter = null;
            lbListHeaderIncome = null;
        }else{
            incomeAdapter = incomePageAdapters[position];
            lbListHeaderIncome.setText("Income");
        }
        incomeExpandableListView.setAdapter(incomeAdapter);
//        slideImageView.setImageResource(slide_images[position]);
//        slideHeading.setText(slide_headings[position]);
//        slideDescription.setText(slide_descs[position]);

        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //Prevent creating multiple slides after last slide
        container.removeView((LinearLayout)object);
    }
}

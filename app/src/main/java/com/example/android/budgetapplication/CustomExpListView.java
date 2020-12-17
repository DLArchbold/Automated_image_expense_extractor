package com.example.android.budgetapplication;

import android.content.Context;
import android.util.Log;
import android.widget.ExpandableListView;

public class CustomExpListView extends ExpandableListView {
    public CustomExpListView(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("[View name] onMeasure w", MeasureSpec.toString(widthMeasureSpec));
        Log.e("[View name] onMeasure h", MeasureSpec.toString(heightMeasureSpec));
//        int specMode = MeasureSpec.getMode(widthMeasureSpec);
//        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(1080, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(600, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
package com.example.android.budgetapplication;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.android.budgetapplication.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.view.LayoutInflater;

public class ParentLevelAdapter extends BaseExpandableListAdapter  {
    private final Context mContext;
    private final List<String> mListDataHeader;
    private final Map<String, List<String>> mListData_SecondLevel_Map;
    private final Map<String, Cursor> mListData_ThirdLevel_Map;
    private final Map<String, Map<String, Cursor>> dateCatExpense;
    LayoutInflater mInflater;
    public ParentLevelAdapter(Context mContext, List<String> mListDataHeader, Map<String, Map<String, Cursor>> dateCatExpense ) {
        this.mContext = mContext;
        this.mListDataHeader = new ArrayList<>();
        this.mListDataHeader.addAll(mListDataHeader);
        this.dateCatExpense = dateCatExpense;
        // Init second level data
        String[] mItemHeaders;
        mListData_SecondLevel_Map = new HashMap<>();
        int parentCount = mListDataHeader.size();
        for (int i = 0; i < parentCount; i++) {
            //Get one unique date per iteration, content is a unique date
            String content = mListDataHeader.get(i);
            //Get valid categories per unique date
            List<String> uniqueDatesList = new ArrayList<>();
            uniqueDatesList.addAll(dateCatExpense.get(content).keySet());
            //Map Date and valid categories in mListData_SecondLevel_Map
            mListData_SecondLevel_Map.put(content, uniqueDatesList);
        }
        // THIRD LEVEL
        String[] mItemChildOfChild;
        List<String> listChild;
        mListData_ThirdLevel_Map = new HashMap<>();
        //Iterate over each date--(mult)>category--(mult)>expense data map entry
//        for (Object o : mListData_SecondLevel_Map.entrySet()) {
//            Map.Entry entry = (Map.Entry) o;
//            Object object = entry.getValue();
//            //object will be a date's list of categories
//            if (object instanceof List) {
//                List<String> stringList = new ArrayList<>();
//                Collections.addAll(stringList, (String[]) ((List) object).toArray());
//                //Iterate over each date's list of categories
//                for (int i = 0; i < stringList.size(); i++){
//                    //Connect one category to one cursor
//                    Cursor c = dateCatExpense.get(entry.getKey()).get(stringList.get(i));
//                    mListData_ThirdLevel_Map.put(stringList.get(i), c);
//                }
//            }
//        }
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater = layoutInflater;
        final ExpandableListView secondLevelExpListView = (ExpandableListView)mInflater.inflate(R.layout.exp_listview, parent, false);
//        LayoutInflater layoutInflater = (LayoutInflater) this.mContext
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//       View view = layoutInflater.inflate(R.layout.content_main2, parent, false);
//
//        final ExpandableListView secondLevelExpListView     = (ExpandableListView) view.findViewById(R.id.list2);
        String parentNode = (String) getGroup(groupPosition);

        //mListData_SecondLevel_Map.get(parentNode) gets a value from the
        //hash table mListData_SecondLevel_Map of structure: date->categories with data
        secondLevelExpListView.setAdapter(new SecondLevelAdapter(this.mContext, mListData_SecondLevel_Map.get(parentNode), mListData_ThirdLevel_Map, dateCatExpense.get(mListDataHeader.get(groupPosition))));
        secondLevelExpListView.setGroupIndicator(null);
        Log.e("ParentLevelAdapter", String.valueOf("in ParentLevelAdapter, getChildView()"));
        return secondLevelExpListView;
    }
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }
    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }
    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        //execute getGroupView() for each date, then go run getChildView(), then
        //in SecondLevelAdapter run its getGroupView() for each category, then return here and
        //run getGroupView() twice
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, parent, false);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setTextColor(Color.CYAN);
        lblListHeader.setText(headerTitle);
        Log.e("ParentLevelAdapter", String.valueOf("in ParentLevelAdapter, getGroupView()" + headerTitle));
        return convertView;
    }
    @Override
    public boolean hasStableIds() {
        return true;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    } }
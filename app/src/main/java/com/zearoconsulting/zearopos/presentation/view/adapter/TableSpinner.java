package com.zearoconsulting.zearopos.presentation.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.Tables;
import java.util.List;

/**
 * Created by saravanan on 08-05-2017.
 */

public class TableSpinner extends BaseAdapter implements SpinnerAdapter {

    private final Context activity;
    private List<Tables> tablesList;

    public TableSpinner(Context context, List<Tables> tablesList) {
        this.tablesList=tablesList;
        activity = context;
    }

    @Override
    public int getCount() {
        return tablesList.size();
    }

    @Override
    public Object getItem(int position) {
        return tablesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(activity);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(14);
        txt.setGravity(Gravity.LEFT);
        txt.setText(tablesList.get(position).getTableName());
        txt.setTextColor(Color.parseColor("#000000"));
        txt.setTypeface(AndroidApplication.getGothamRoundedBook());
        return  txt;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        TextView txt = new TextView(activity);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(14);
        txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
        txt.setText(tablesList.get(i).getTableName());
        txt.setTextColor(Color.parseColor("#000000"));
        txt.setTypeface(AndroidApplication.getGothamRoundedBook());
        return  txt;
    }
}

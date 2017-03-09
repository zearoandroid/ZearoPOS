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
import com.zearoconsulting.zearopos.presentation.model.Role;
import com.zearoconsulting.zearopos.presentation.model.Warehouse;

import java.util.List;

/**
 * Created by saravanan on 25-05-2016.
 */
public class WarehouseSpinner extends BaseAdapter implements SpinnerAdapter {

    private final Context activity;
    private List<Warehouse> warehouseList;

    public WarehouseSpinner(Context context, List<Warehouse> warehouseList) {
        this.warehouseList=warehouseList;
        activity = context;
    }

    @Override
    public int getCount() {
        return warehouseList.size();
    }

    @Override
    public Object getItem(int position) {
        return warehouseList.get(position);
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
        txt.setText(warehouseList.get(position).getWarehouseName());
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
        txt.setText(warehouseList.get(i).getWarehouseName());
        txt.setTextColor(Color.parseColor("#000000"));
        txt.setTypeface(AndroidApplication.getGothamRoundedBook());
        return  txt;
    }
}



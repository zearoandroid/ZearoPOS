package com.zearoconsulting.zearopos.presentation.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.Organization;
import com.zearoconsulting.zearopos.presentation.model.Role;

import java.util.List;

/**
 * Created by saravanan on 25-05-2016.
 */
public class RoleSpinner extends BaseAdapter implements SpinnerAdapter {

    private final Context activity;
    private List<Role> roleList;

    public RoleSpinner(Context context, List<Role> roleList) {
        this.roleList=roleList;
        activity = context;
    }

    @Override
    public int getCount() {
        return roleList.size();
    }

    @Override
    public Object getItem(int position) {
        return roleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        /*TextView txt = new TextView(activity);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(18);
        txt.setGravity(Gravity.LEFT);
        txt.setText(roleList.get(position).getRoleName());
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;*/

        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = inflater.inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setTypeface(AndroidApplication.getGothamRoundedBook());
        textView.setText(roleList.get(position).getRoleName());

        return view;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        TextView txt = new TextView(activity);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(16);
        txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
        txt.setText(roleList.get(i).getRoleName());
        txt.setTextColor(Color.parseColor("#000000"));
        txt.setTypeface(AndroidApplication.getGothamRoundedBook());
        return  txt;
    }
}


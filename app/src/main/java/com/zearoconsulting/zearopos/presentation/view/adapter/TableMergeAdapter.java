package com.zearoconsulting.zearopos.presentation.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.data.DBHelper;
import com.zearoconsulting.zearopos.data.POSDataSource;
import com.zearoconsulting.zearopos.presentation.model.KOTHeader;
import com.zearoconsulting.zearopos.presentation.model.Tables;

import java.util.List;

/**
 * Created by saravanan on 14-11-2016.
 */

public class TableMergeAdapter extends
        RecyclerView.Adapter<TableMergeAdapter.ViewHolder> {

    List<KOTHeader> kotHeaderList;
    POSDataSource mDBHelper;
    private AppDataManager mAppDataManager;

    public TableMergeAdapter(List<KOTHeader> kotHeaderLists, POSDataSource dbHelper, AppDataManager appDataManager) {
        this.kotHeaderList = kotHeaderLists;
        this.mDBHelper = dbHelper;
        this.mAppDataManager = appDataManager;
    }

    // Create new views
    @Override
    public TableMergeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.table_merge_view, null);

        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final int pos = position;

        Tables table = mDBHelper.getTableData(mAppDataManager.getClientID(), mAppDataManager.getOrgID(),kotHeaderList.get(position).getTablesId());

        //+"("+String.valueOf(kotHeaderList.get(position).getTablesId())+")"

        viewHolder.tvTableId.setText(table.getTableName());

        double total = mDBHelper.sumOfKOTProductsTotalPrice(kotHeaderList.get(position).getTablesId());
        viewHolder.tvTotalAmt.setText(String.valueOf(total));

        //viewHolder.tvInvoiceNumber.setText("Inv # "+String.valueOf(kotHeaderList.get(position).getInvoiceNumber()));

        if(kotHeaderList.get(position).getSelected().equalsIgnoreCase("Y"))
            viewHolder.chkSelected.setChecked(true);
        else
            viewHolder.chkSelected.setChecked(false);

        viewHolder.chkSelected.setTag(kotHeaderList.get(position));


        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                KOTHeader kotHeader = (KOTHeader) cb.getTag();

                if(cb.isChecked() == true){
                    kotHeader.setSelected("Y");
                    kotHeaderList.get(pos).setSelected("Y");
                    mDBHelper.updateKOTSelectedStatus(kotHeaderList.get(position).getTablesId(),"Y");
                }else{
                    kotHeader.setSelected("N");
                    kotHeaderList.get(pos).setSelected("N");
                    mDBHelper.updateKOTSelectedStatus(kotHeaderList.get(position).getTablesId(),"N");
                }
            }
        });

    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return kotHeaderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTableId;
        public TextView tvTotalAmt;
        public TextView tvInvoiceNumber;
        public CheckBox chkSelected;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvTableId = (TextView) itemLayoutView.findViewById(R.id.tvTableId);
            tvTotalAmt = (TextView) itemLayoutView.findViewById(R.id.tvTotalAmt);
            tvInvoiceNumber = (TextView) itemLayoutView.findViewById(R.id.tvInvoiceNumber);
            chkSelected = (CheckBox) itemLayoutView
                    .findViewById(R.id.chkSelected);

        }

    }

    // method to access in activity after updating selection
    public List<KOTHeader> getKOTHeaderList() {
        return kotHeaderList;
    }

    public void refresh(List<KOTHeader> list) {

        if (list != null) {
            if (kotHeaderList != null) {
                kotHeaderList.clear();
                kotHeaderList.addAll(list);
            } else {
                kotHeaderList = list;
            }
            notifyDataSetChanged();
        }
    }

}

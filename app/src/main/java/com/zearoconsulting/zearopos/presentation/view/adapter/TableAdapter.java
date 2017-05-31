package com.zearoconsulting.zearopos.presentation.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.KOTHeader;
import com.zearoconsulting.zearopos.presentation.model.Product;
import com.zearoconsulting.zearopos.presentation.model.Tables;
import com.zearoconsulting.zearopos.presentation.presenter.TableSelectListener;
import com.zearoconsulting.zearopos.utils.AppConstants;

import java.util.List;

/**
 * Created by saravanan on 11-11-2016.
 */

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableItemHolder> {

    List<Tables> mKOTTableList;
    TableSelectListener listener;
    List<KOTHeader> lineItems;

    private long table1Id = 0;
    private long table2Id = 0;

    public TableAdapter(List<Tables> kotTableList, List<KOTHeader> kotHeaders) {
        this.mKOTTableList = kotTableList;
        this.lineItems = kotHeaders;
    }

    @Override
    public TableAdapter.TableItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.table_item_row, null);
        TableAdapter.TableItemHolder mh = new TableAdapter.TableItemHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(TableItemHolder holder, int pos) {

        final Tables tables = mKOTTableList.get(pos);
        try {
            if (tables.getOrderAvailable().equalsIgnoreCase("Y")) {
                holder.btnTable.setBackgroundResource(R.drawable.round_red_button);
            } else if (tables.getOrderAvailable().equalsIgnoreCase("N")) {
                holder.btnTable.setBackgroundResource(R.drawable.round_grey_button);
            }
            holder.btnTable.setText(tables.getTableName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTables(List<Tables> kotTableList) {

        mKOTTableList = kotTableList;

        for (int i = 0; i < mKOTTableList.size(); i++) {
            notifyItemChanged(i);
        }
    }

    public void tableChange(int firstPosition, int secondPosition) {
        Tables table1 = mKOTTableList.get(firstPosition);
        if (table1.getOrderAvailable().equalsIgnoreCase("Y")) {
            //call api and change the table
            table1Id = table1.getTableId();

            Tables table2 = mKOTTableList.get(secondPosition);
            table2Id = table2.getTableId();
            if (table2.getOrderAvailable().equalsIgnoreCase("N"))
                listener.OnTableChangeListener(table1Id, table2Id);
        }
    }

    @Override
    public int getItemCount() {
        return (null != mKOTTableList ? mKOTTableList.size() : 0);
    }

    public void selectedItem(int pos) {
        try {
            Tables tables = mKOTTableList.get(pos);
            listener.OnTableSelectedListener(tables);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectedTableChangeItem(int pos) {
        try {
            Tables tables = mKOTTableList.get(pos);
            listener.OnTableSelectedChangeListener(tables);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnTableSelectListener(TableSelectListener paramTableSelectListener) {
        this.listener = paramTableSelectListener;
    }

    public class TableItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public final View mView;
        protected Button btnTable;

        public TableItemHolder(View view) {
            super(view);
            mView = view;
            this.btnTable = (Button) view.findViewById(R.id.btnTable);
            this.btnTable.setOnClickListener(this);
            this.btnTable.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            selectedItem(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            selectedTableChangeItem(getAdapterPosition());
            return true;
        }
    }

}

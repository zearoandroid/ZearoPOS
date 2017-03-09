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
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;

import java.util.List;

/**
 * Created by saravanan on 13-12-2016.
 */

public class OrderSplitAdapter extends
        RecyclerView.Adapter<OrderSplitAdapter.ViewHolder> {

    List<POSLineItem> mPOSLineItems;
    POSDataSource mDBHelper;
    private AppDataManager mAppDataManager;

    public OrderSplitAdapter(List<POSLineItem> posLineItemList, POSDataSource dbHelper, AppDataManager appDataManager) {
        this.mPOSLineItems = posLineItemList;
        this.mDBHelper = dbHelper;
        this.mAppDataManager = appDataManager;
    }

    // Create new views
    @Override
    public OrderSplitAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_split_view, null);

        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OrderSplitAdapter.ViewHolder viewHolder, int position) {

        final int pos = position;

        viewHolder.tvProductName.setText(mPOSLineItems.get(pos).getProductName());

        viewHolder.tvTotalAmt.setText(mPOSLineItems.get(pos).getPosQty()+"x "+ AppConstants.currencyCode+" "+ Common.valueFormatter(mPOSLineItems.get(pos).getStdPrice()));

        viewHolder.tvTotal.setText(AppConstants.currencyCode+" "+ Common.valueFormatter(mPOSLineItems.get(pos).getTotalPrice()));

        if(mPOSLineItems.get(pos).getSelected().equalsIgnoreCase("Y"))
            viewHolder.chkSelected.setChecked(false);
        else
            viewHolder.chkSelected.setChecked(true);

        viewHolder.chkSelected.setTag(mPOSLineItems.get(position));

        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                POSLineItem lineItem = (POSLineItem) cb.getTag();

                if(cb.isChecked() == true){
                    lineItem.setSelected("N");
                    mPOSLineItems.get(pos).setSelected("N");
                    mDBHelper.updateLineItemSelectedStatus(mPOSLineItems.get(position).getPosId(),mPOSLineItems.get(position).getProductId(),"N");
                    List<POSLineItem> extraLineItems = mDBHelper.getPOSExtraLineItems(mPOSLineItems.get(position).getKotLineId());
                    if(extraLineItems.size()!=0){
                        for(int j=0; j<extraLineItems.size(); j++){
                            mDBHelper.updateLineItemSelectedStatus(extraLineItems.get(j).getPosId(),extraLineItems.get(j).getProductId(),"N");
                        }
                    }
                }else{
                    lineItem.setSelected("Y");
                    mDBHelper.updateLineItemSelectedStatus(mPOSLineItems.get(position).getPosId(), mPOSLineItems.get(position).getProductId(),"Y");
                    List<POSLineItem> extraLineItems = mDBHelper.getPOSExtraLineItems(mPOSLineItems.get(position).getKotLineId());
                    if(extraLineItems.size()!=0){
                        for(int j=0; j<extraLineItems.size(); j++){
                            mDBHelper.updateLineItemSelectedStatus(extraLineItems.get(j).getPosId(),extraLineItems.get(j).getProductId(),"Y");
                        }
                    }
                }
            }
        });

    }

    public void refresh(List<POSLineItem> list) {

        if (list != null) {
            if (mPOSLineItems != null) {
                mPOSLineItems.clear();
                mPOSLineItems.addAll(list);
            } else {
                mPOSLineItems = list;
            }
            notifyDataSetChanged();
        }
    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return mPOSLineItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvProductName;
        public TextView tvTotalAmt;
        public TextView tvTotal;
        public CheckBox chkSelected;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvProductName = (TextView) itemLayoutView.findViewById(R.id.tvProductName);
            tvTotalAmt = (TextView) itemLayoutView.findViewById(R.id.tvTotalAmt);
            tvTotal = (TextView) itemLayoutView.findViewById(R.id.tvTotal);
            chkSelected = (CheckBox) itemLayoutView
                    .findViewById(R.id.chkSelected);

        }

    }
}

package com.zearoconsulting.zearopos.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.POSOrders;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.presentation.presenter.ReprintListener;
import com.zearoconsulting.zearopos.utils.AnimationUtil;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;

import java.util.List;

/**
 * Created by saravanan on 28-08-2016.
 */
public class ViewTransactionListAdapter extends RecyclerView.Adapter<ViewTransactionListAdapter.TransListRowHolder> {

    private static List<POSOrders> mOrderList;
    private Context mContext;
    ReprintListener mListener = null;

    public ViewTransactionListAdapter(Context context, List<POSOrders> orderList) {
        this.mOrderList = orderList;
        this.mContext = context;
    }

    @Override
    public TransListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_row, null);
        TransListRowHolder mh = new TransListRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(TransListRowHolder holder, int pos) {

        try {
            final POSOrders mOrder = mOrderList.get(pos);
            if (mOrder.getPosId() != 0) {
                holder.cardTitle.setText(mOrder.getPosId()+" - "+mOrder.getOrderType());

                holder.cardCustomerName.setText(mOrder.getCustomerName());
                holder.cardTotalItems.setText(mOrder.getOrderTotalQty() + " items");
                holder.cardTotalAmount.setText(AppConstants.currencyCode + " " + Common.valueFormatter(mOrder.getOrderTotalAmt()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (null != mOrderList ? mOrderList.size() : 0);
    }


    public class TransListRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView cardTitle;
        protected TextView cardTotalItems;
        protected TextView cardTotalAmount;
        protected TextView cardCustomerName;

        public TransListRowHolder(View view) {
            super(view);
            this.cardTitle = (TextView) view.findViewById(R.id.txtPOSNumber);
            this.cardTotalItems = (TextView) view.findViewById(R.id.cardTotalItems);
            this.cardTotalAmount = (TextView) view.findViewById(R.id.cardTotalAmount);
            this.cardCustomerName = (TextView) view.findViewById(R.id.txtBusinessPartnerView);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            printOrder(getAdapterPosition());
        }
    }

    public void printOrder(int pos){
        try {
            if(mListener!=null)
            mListener.OnRePrintListener(mOrderList.get(pos));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setOnReprintListener(ReprintListener paramPrintListener)
    {
        this.mListener = paramPrintListener;
    }

}

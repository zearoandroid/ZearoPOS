package com.zearoconsulting.zearopos.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.data.DBHelper;
import com.zearoconsulting.zearopos.presentation.model.POSOrders;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.utils.AnimationUtil;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;

import java.util.List;
import java.util.Map;

/**
 * Created by saravanan on 28-08-2016.
 */
public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardListRowHolder> {

    private static List<POSOrders> mOrderList;
    private Context mContext;
    int previousPosition = 0;
    private static IPOSListeners mListener;

    public CardListAdapter(Context context, List<POSOrders> orderList, IPOSListeners listener) {
        this.mOrderList = orderList;
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public CardListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_list_row, null);
        CardListRowHolder mh = new CardListRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(CardListRowHolder holder, int pos) {

        try {
            final POSOrders mOrder = mOrderList.get(pos);
            if (mOrder.getPosId() != 0) {
                holder.cardTitle.setText(mOrder.getPosId()+" - "+mOrder.getOrderType());
                holder.mHeaderUserLayout.setVisibility(View.GONE);
                holder.mHeaderAmountLayout.setVisibility(View.VISIBLE);

                holder.cardCustomerName.setText(mOrder.getCustomerName());
                holder.cardTotalItems.setText(mOrder.getOrderTotalQty() + " items");
                holder.cardTotalAmount.setText(AppConstants.currencyCode + " " + Common.valueFormatter(mOrder.getOrderTotalAmt()));
            } else {
                holder.cardTitle.setText("+New List");
                holder.mHeaderAmountLayout.setVisibility(View.GONE);
                holder.mHeaderUserLayout.setVisibility(View.VISIBLE);
            }

            if (pos > previousPosition) {
                AnimationUtil.aimate(holder, true);
            } else {
                AnimationUtil.aimate(holder, false);
            }

            previousPosition = pos;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (null != mOrderList ? mOrderList.size() : 0);
    }


    public static class CardListRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected  LinearLayout mHeaderAmountLayout;
        protected LinearLayout mHeaderUserLayout;
        protected TextView cardTitle;
        protected TextView cardTotalItems;
        protected TextView cardTotalAmount;
        protected TextView cardCustomerName;

        public CardListRowHolder(View view) {
            super(view);
            this.mHeaderAmountLayout = (LinearLayout)view.findViewById(R.id.cardHeaderAmountLayout);
            this.mHeaderUserLayout = (LinearLayout)view.findViewById(R.id.cardHeaderUserLayout);
            this.cardTitle = (TextView) view.findViewById(R.id.card_title);
            this.cardTotalItems = (TextView) view.findViewById(R.id.cardTotalItems);
            this.cardTotalAmount = (TextView) view.findViewById(R.id.cardTotalAmount);
            this.cardCustomerName = (TextView) view.findViewById(R.id.txtBusinessPartnerView);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != mListener) {
                mListener.onSelectedOrder(mOrderList.get(getAdapterPosition()));
            }
        }
    }

    public void swapItems(List< POSOrders > todolist){
        this.mOrderList = todolist;
        notifyDataSetChanged();
    }

}

package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.POSOrders;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.presentation.presenter.ReprintListener;
import com.zearoconsulting.zearopos.presentation.view.adapter.CardListAdapter;
import com.zearoconsulting.zearopos.presentation.view.adapter.ViewTransactionListAdapter;
import com.zearoconsulting.zearopos.presentation.view.animations.SimpleDividerItemDecoration;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;

import java.util.List;

/**
 * Created by saravanan on 09-01-2017.
 */

public class ViewTransactionFragment extends AbstractDialogFragment {

    RecyclerView mTransactionView;

    ReprintListener rePrintListener = new ReprintListener() {
        @Override
        public void OnRePrintListener(POSOrders orders) {
            try {
                //update the cart count number
                //((IPOSListeners) getActivity()).onReprintOrder(orders);
                dismiss();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mReboundListener = new ReboundListener();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_transactions, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Add a listener to the spring
        mSpring.addListener(mReboundListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        //Remove a listener to the spring
        mSpring.removeListener(mReboundListener);
    }

    @Override
    public void onViewCreated(View paramView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(paramView, savedInstanceState);

        getDialog().getWindow().setSoftInputMode(3);
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().setCanceledOnTouchOutside(true);


        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode, android.view.KeyEvent event) {

                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    //Stop back event here!!!
                    return true;
                } else
                    return false;
            }
        });

        mTransactionView = (RecyclerView) paramView.findViewById(R.id.transactionList);
        mTransactionView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTransactionView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        List<POSOrders> mOrders = mDBHelper.getPostedPOSOrders();

        for(int i=0; i<mOrders.size(); i++){
            mOrders.get(i).setOrderTotalAmt(mDBHelper.sumOfProductsTotalPrice(mOrders.get(i).getPosId()));
            mOrders.get(i).setOrderTotalQty(mDBHelper.sumOfProductsTotalQty(mOrders.get(i).getPosId()));
            mOrders.get(i).setOrderType("CS");
        }

        ViewTransactionListAdapter mTransListAdapter = new ViewTransactionListAdapter(getActivity(), mOrders);
        mTransactionView.setAdapter(mTransListAdapter);
        mTransListAdapter.notifyDataSetChanged();

        //set the addcart listener
        mTransListAdapter.setOnReprintListener(rePrintListener);

    }
}

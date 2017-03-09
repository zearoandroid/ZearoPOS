package com.zearoconsulting.zearopos.presentation.presenter;

import com.zearoconsulting.zearopos.presentation.model.BPartner;

/**
 * Created by saravanan on 04-01-2017.
 */

public class OrderStatusListener {

    public interface OnOrderStateListener {
        void orderPostedSuccess(long posNumber);
        void orderPostedFailure(long posNumber);
        void creditCustomerSelected(BPartner bPartner);
        void onPrintingSuccess();
        void printFromPayment(long posNumber, double totalAmount, double finalAmount, double paidAmount, double returnAmount, double paidCash, double paidAmex, double paidGift, double paidMaster, double paidVisa, double paidOther, double amtEntered);
    }

    private static OrderStatusListener mInstance;
    private OnOrderStateListener mListener;
    private boolean mState;
    private long posNumber;

    private OrderStatusListener() {}

    public static OrderStatusListener getInstance() {
        if(mInstance == null) {
            mInstance = new OrderStatusListener();
        }
        return mInstance;
    }

    public void setListener(OnOrderStateListener listener) {
        mListener = listener;
    }

    public void orderPostedAndPrint(long posNumber, double totalAmount, double finalAmount, double paidAmount, double returnAmount, double paidCash, double paidAmex, double paidGift, double paidMaster, double paidVisa, double paidOther, double amtEntered) {
        if(mListener != null) {
            mListener.printFromPayment(posNumber, totalAmount, finalAmount, paidAmount, returnAmount, paidCash, paidAmex, paidGift, paidMaster, paidVisa, paidOther, amtEntered);
        }
    }

    public void orderPosted(long posNum, boolean state) {
        if(mListener != null) {
            mState = state;
            posNumber = posNum;
            notifyStateChange();
        }
    }

    public void kotPrinted() {
        if(mListener != null) {
            mListener.onPrintingSuccess();
        }
    }

    public void customerSelected(BPartner bPartner){
        if(mListener != null) {
            mListener.creditCustomerSelected(bPartner);
        }
    }

    public boolean getState() {
        return mState;
    }

    private void notifyStateChange() {
       if(mState)
           mListener.orderPostedSuccess(posNumber);
        else
           mListener.orderPostedFailure(posNumber);
    }
}

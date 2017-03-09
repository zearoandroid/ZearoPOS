package com.zearoconsulting.zearopos.presentation.print;

/**
 * Created by saravanan on 30-12-2016.
 */

public class WiFiPrintTaskParams {

    long posId;
    double mTotalAmt;
    double mFinalAmt;
    double mPaidAmt;
    double mReturnAmt;

    double mPaidCashAmt = 0;
    double mPaidAmexAmt = 0;
    double mPaidGiftAmt = 0;
    double mPaidMasterAmt = 0;
    double mPaidVisaAmt = 0;
    double mPaidOtherAmt = 0;

    double mAmountEntered = 0;

    public WiFiPrintTaskParams(long posNumber, double totalAmount, double finalAmount, double paidAmount, double returnAmount, double paidCash, double paidAmex, double paidGift, double paidMaster, double paidVisa, double paidOther, double amtEntered) {
        this.posId = posNumber;
        this.mTotalAmt = totalAmount;
        this.mFinalAmt = finalAmount;
        this.mPaidAmt = paidAmount;
        this.mReturnAmt = returnAmount;

        this.mPaidCashAmt = paidCash;
        this.mPaidAmexAmt = paidAmex;
        this.mPaidGiftAmt = paidGift;
        this.mPaidMasterAmt = paidMaster;
        this.mPaidVisaAmt = paidVisa;
        this.mPaidOtherAmt = paidOther;

        this.mAmountEntered = amtEntered;
    }
}

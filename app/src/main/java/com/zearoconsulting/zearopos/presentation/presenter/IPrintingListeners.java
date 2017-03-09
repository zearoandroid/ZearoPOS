package com.zearoconsulting.zearopos.presentation.presenter;

/**
 * Created by saravanan on 30-12-2016.
 */

public abstract interface IPrintingListeners {
    void onPrintTaskComplete(long orderNum);
    void onPrintTaskError(String error, long orderNum);
}

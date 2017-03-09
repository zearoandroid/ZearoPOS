package com.zearoconsulting.zearopos.presentation.presenter;

/**
 * Created by saravanan on 29-12-2016.
 */

public abstract interface ITokenListeners {
    public abstract void OnTokenReceivedListener(long tableId, long invoiceNumber);
}

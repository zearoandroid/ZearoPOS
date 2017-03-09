package com.zearoconsulting.zearopos.presentation.presenter;

import com.zearoconsulting.zearopos.presentation.model.POSOrders;

/**
 * Created by saravanan on 09-01-2017.
 */

public abstract interface ReprintListener {

    public abstract void OnRePrintListener(POSOrders order);
}

package com.zearoconsulting.zearopos.presentation.presenter;


import com.zearoconsulting.zearopos.presentation.model.Tables;

/**
 * Created by saravanan on 11-11-2016.
 */

public abstract interface TableSelectListener {

    public abstract void OnTableSelectedListener(Tables tableEntity);
    public abstract void OnTableSelectedChangeListener(Tables tableEntity);
    public abstract void OnTableChangeListener(long activeTableId, long moveTableId);
}

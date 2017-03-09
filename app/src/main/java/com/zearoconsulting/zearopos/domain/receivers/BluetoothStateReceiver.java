package com.zearoconsulting.zearopos.domain.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by saravanan on 25-08-2016.
 */
public class BluetoothStateReceiver extends BroadcastReceiver{

    BluetoothStateReceiverListener mBluetoothStateReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    public interface BluetoothStateReceiverListener {
        void onBluetoothConnectionChanged(int state);
    }
}

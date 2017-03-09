package com.zearoconsulting.zearopos.presentation.view.activity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.sewoo.port.android.BluetoothPort;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.view.dialogs.AlertView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

@SuppressLint("UseValueOf")
public class ConnectPrinterActivity extends BaseActivity {

    private static final int REQUEST_ENABLE_BT = 2;

    // BT
    private static final String TAG = "BluetoothConnectMenu";
    private ArrayAdapter<String> mBltAdapter;
    private Vector<BluetoothDevice> remoteDevices;

    // UI
    private Button mBltConnectBtn;
    private Button mBltSearchBtn;
    private EditText mEdtBltAddress;
    private ListView mBltListView;
    private Context mContext;

    // Set up Bluetooth.
    private void bluetoothSetup()
    {
        // Initialize
        clearBtDevData();

        if(bluetoothPort == null){
            bluetoothPort = BluetoothPort.getInstance();
            if (mBluetoothAdapter == null)
            {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
        }

        if (mBluetoothAdapter == null)
        {
            // Device does not support Bluetooth
            return;
        }
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "//temp";
    private static final String fileName = dir + "//BTPrinter";
    private String lastConnAddr;
    private void loadSettingFile()
    {
        int rin = 0;
        char [] buf = new char[128];
        try
        {
            FileReader fReader = new FileReader(fileName);
            rin = fReader.read(buf);
            if(rin > 0)
            {
                lastConnAddr = new String(buf,0,rin);
                mEdtBltAddress.setText(lastConnAddr);
            }
            fReader.close();
        }
        catch (FileNotFoundException e)
        {
            Log.i(TAG, "Connection history not exists.");
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void saveSettingFile()
    {
        try
        {
            File tempDir = new File(dir);
            if(!tempDir.exists())
            {
                tempDir.mkdir();
            }
            FileWriter fWriter = new FileWriter(fileName);
            if(lastConnAddr != null)
                fWriter.write(lastConnAddr);
            fWriter.close();
        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private BroadcastReceiver searchFinish;
    private BroadcastReceiver searchStart;
    private BroadcastReceiver discoveryResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_connect_printer);

        mEdtBltAddress = (EditText) findViewById(R.id.edtBltAddress);
        mBltListView = (ListView) findViewById(R.id.lstPairedDevices);
        mBltSearchBtn = (Button) findViewById(R.id.btnBltSearch);
        mBltConnectBtn = (Button) findViewById(R.id.btnBltConnect);

        mContext = this;

        // Setting
        loadSettingFile();
        bluetoothSetup();

        if(bluetoothPort.isConnected()){
            mBltConnectBtn.setText(getResources().getString(R.string.dev_disconn_btn));
            mBltListView.setEnabled(false);
            mEdtBltAddress.setEnabled(false);
            mBltSearchBtn.setEnabled(false);
        }

        mProDlg = new ProgressDialog(this);
        mProDlg.setIndeterminate(true);
        mProDlg.setCancelable(false);

        mBltAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mBltListView.setAdapter(mBltAdapter);
        addPairedDevices();

        // Connect, Disconnect -- Button
        mBltConnectBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                connectBluetoothDevice();
            }
        });
        // Search Button
        mBltSearchBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchBluetoothDevices();
            }
        });


        // Connect - click the List item.
        mBltListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                BluetoothDevice btDev = remoteDevices.elementAt(arg2);
                try
                {
                    if(mBluetoothAdapter.isDiscovering())
                    {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    mEdtBltAddress.setText(btDev.getAddress());
                    btConn(btDev);
                }
                catch (IOException e)
                {
                    AlertView.showAlert(e.getMessage(), mContext);
                    return;
                }
            }
        });

        // UI - Event Handler.
        // Search device, then add List.
        discoveryResult = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String key;
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(remoteDevice != null)
                {
                    if(remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED)
                    {
                        key = remoteDevice.getName() +"\n["+remoteDevice.getAddress()+"]";
                    }
                    else
                    {
                        key = remoteDevice.getName() +"\n["+remoteDevice.getAddress()+"] [Paired]";
                    }
                    if(bluetoothPort.isValidAddress(remoteDevice.getAddress()))
                    {
                        remoteDevices.add(remoteDevice);
                        mBltAdapter.add(key);
                    }
                }
            }
        };
        registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        searchStart = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                mBltConnectBtn.setEnabled(false);
                mEdtBltAddress.setEnabled(false);
                mBltSearchBtn.setText(getResources().getString(R.string.bt_stop_search_btn));
            }
        };
        registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        searchFinish = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                mBltConnectBtn.setEnabled(true);
                mEdtBltAddress.setEnabled(true);
                mBltSearchBtn.setText(getResources().getString(R.string.bt_search_btn));
            }
        };
        registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    // clear device data used list.
    private void clearBtDevData() {
        remoteDevices = new Vector<BluetoothDevice>();
    }

    // add paired device to list
    private void addPairedDevices() {
        try {
            BluetoothDevice pairedDevice;
            Iterator<BluetoothDevice> iter = (mBluetoothAdapter.getBondedDevices()).iterator();
            while (iter.hasNext()) {
                pairedDevice = iter.next();
                if (bluetoothPort.isValidAddress(pairedDevice.getAddress())) {
                    remoteDevices.add(pairedDevice);
                    mBltAdapter.add(pairedDevice.getName() + "\n[" + pairedDevice.getAddress() + "] [Paired]");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void searchBluetoothDevices() {
        if (!mBluetoothAdapter.isDiscovering()) {
            clearBtDevData();
            mBltAdapter.clear();
            mBluetoothAdapter.startDiscovery();
        } else {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    private void connectBluetoothDevice() {
        if (!bluetoothPort.isConnected()) // Connect routine.
        {
            try {
                if (mEdtBltAddress.getText().toString().length() != 0)
                    btConn(mBluetoothAdapter.getRemoteDevice(mEdtBltAddress.getText().toString()));
                else
                    AlertView.showAlert("Is not a valid Bluetooth address", mContext);
            } catch (IllegalArgumentException e) {
                // Bluetooth Address Format [OO:OO:OO:OO:OO:OO]
                Log.e(TAG, e.getMessage(), e);
                AlertView.showAlert(e.getMessage(), mContext);
                return;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                AlertView.showAlert(e.getMessage(), mContext);
                return;
            }
        } else // Disconnect routine.
        {
            // Always run.
            btDisconn();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // Bluetooth Connection method.
    private void btConn(final BluetoothDevice btDev) throws IOException {
        new connTask().execute(btDev);
    }

    // Bluetooth Disconnection method.
    private void btDisconn() {
        try {
            bluetoothPort.disconnect();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        if ((hThread != null) && (hThread.isAlive()))
            hThread.interrupt();
        // UI
        mBltConnectBtn.setText(getResources().getString(R.string.dev_conn_btn));
        mBltListView.setEnabled(true);
        mEdtBltAddress.setEnabled(true);
        mBltSearchBtn.setEnabled(true);

        showSnack(false);
    }

    @Override
    protected void onDestroy()
    {
        try {
            saveSettingFile();
            unregisterReceiver(searchFinish);
            unregisterReceiver(searchStart);
            unregisterReceiver(discoveryResult);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    // Bluetooth Connection Task.
    class connTask extends AsyncTask<BluetoothDevice, Void, Integer> {

        @Override
        protected void onPreExecute() {
            mProDlg.setMessage(getResources().getString(R.string.connecting_msg));
            mProDlg.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params) {
            Integer retVal = null;
            try {
                bluetoothPort.connect(params[0]);
                lastConnAddr = params[0].getAddress();
                retVal = new Integer(0);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                retVal = new Integer(-1);
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result.intValue() == 0)    // Connection success.
            {

                hThread = new Thread(rh);
                hThread.start();
                // UI
                mBltConnectBtn.setText(getResources().getString(R.string.dev_disconn_btn));
                mBltListView.setEnabled(false);
                mEdtBltAddress.setEnabled(false);
                mBltSearchBtn.setEnabled(false);
                if (mProDlg.isShowing())
                    mProDlg.dismiss();

                showSnack(true);

            } else    // Connection failed.
            {
                if (mProDlg.isShowing())
                    mProDlg.dismiss();
                AlertView.showAlert(getResources().getString(R.string.bt_conn_fail_msg),
                        getResources().getString(R.string.dev_check_msg), mContext);
            }
            super.onPostExecute(result);
        }
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = getResources().getString(R.string.bt_conn_msg);
            color = Color.WHITE;
        } else {
            message = getResources().getString(R.string.bt_disconn_msg);
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.layConnectPrinter), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();

        finish();
    }
}

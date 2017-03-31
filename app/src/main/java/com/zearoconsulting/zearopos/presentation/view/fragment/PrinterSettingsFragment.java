package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by saravanan on 29-03-2017.
 */

public class PrinterSettingsFragment extends AbstractDialogFragment {

    private FancyButton mBtnSubmit;
    private FancyButton mBtnCancel;
    private EditText mEdtIPAddress;
    private IPOSListeners mListener = null;
    private RadioGroup mRadGroupMode;
    private RadioButton mRadUSB;
    private RadioButton mRadWiFi;
    private RadioButton mRadBluetooth;

    public Dialog onCreateDialog(Bundle paramBundle) {
        Dialog localDialog = super.onCreateDialog(paramBundle);
        localDialog.getWindow().requestFeature(1);
        return localDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mReboundListener = new ReboundListener();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_printer_settings, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onViewCreated(View paramView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(paramView, savedInstanceState);

        getDialog().getWindow().setSoftInputMode(3);
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().setCanceledOnTouchOutside(false);


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

        mProDlg = new ProgressDialog(getActivity());
        mProDlg.setIndeterminate(true);
        mProDlg.setCancelable(false);
        mListener= (IPOSListeners) getActivity();

        this.mBtnSubmit = ((FancyButton) paramView.findViewById(R.id.btnSupmit));
        this.mBtnCancel = ((FancyButton) paramView.findViewById(R.id.btnCancel));
        this.mEdtIPAddress = ((EditText) paramView.findViewById(R.id.edtIPAddress));

        this.mRadGroupMode = ((RadioGroup) paramView.findViewById(R.id.radGroupMode));
        this.mRadUSB = ((RadioButton) paramView.findViewById(R.id.radUSB));
        this.mRadWiFi = ((RadioButton) paramView.findViewById(R.id.radWiFi));
        this.mRadBluetooth = ((RadioButton) paramView.findViewById(R.id.radBluetooth));

        String mPrinterMode = mAppManager.getPrinterMode();
        if(mPrinterMode.equalsIgnoreCase("USB"))
            ((RadioButton)mRadGroupMode.getChildAt(0)).setChecked(true);
        else if(mPrinterMode.equalsIgnoreCase("WiFi"))
            ((RadioButton)mRadGroupMode.getChildAt(1)).setChecked(true);
        else if(mPrinterMode.equalsIgnoreCase("Bluetooth"))
            ((RadioButton)mRadGroupMode.getChildAt(1)).setChecked(true);

        int selectedId = mRadGroupMode.getCheckedRadioButtonId();

        // find which radioButton is checked by id
        if(selectedId == mRadUSB.getId()) {
            mEdtIPAddress.setVisibility(View.INVISIBLE);
        } else if(selectedId == mRadWiFi.getId()) {
            mEdtIPAddress.setVisibility(View.VISIBLE);
            mEdtIPAddress.setText(mAppManager.getPrinterIP());
        } else if(selectedId == mRadBluetooth.getId()) {
            mEdtIPAddress.setVisibility(View.INVISIBLE);
        }

        mRadGroupMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == mRadUSB.getId()) {
                    mEdtIPAddress.setVisibility(View.INVISIBLE);
                } else if(checkedId == mRadWiFi.getId()) {
                    mEdtIPAddress.setVisibility(View.VISIBLE);
                    mEdtIPAddress.setText(mAppManager.getPrinterIP());
                } else if(checkedId == mRadBluetooth.getId()) {
                    mEdtIPAddress.setVisibility(View.INVISIBLE);
                }
            }

        });

        // Add an OnTouchListener to the root view.
        this.mBtnCancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mReboundListener.animateView(mBtnCancel);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // When pressed start solving the spring to 1.
                        mSpring.setEndValue(1);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // When released start solving the spring to 0.
                        mSpring.setEndValue(0);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PrinterSettingsFragment.this.dismiss();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });

        // Add an OnTouchListener to the root view.
        this.mBtnSubmit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mReboundListener.animateView(mBtnSubmit);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // When pressed start solving the spring to 1.
                        mSpring.setEndValue(1);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // When released start solving the spring to 0.
                        mSpring.setEndValue(0);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                String ipAddress = "";
                                int selectedId = mRadGroupMode.getCheckedRadioButtonId();
                                if(selectedId == mRadUSB.getId()) {
                                    if(mListener!=null){
                                        mListener.onPrinterConnection("USB", ipAddress);
                                    }
                                } else if(selectedId == mRadWiFi.getId()) {
                                    if (mEdtIPAddress.getText().toString().trim().length() != 0 ) {
                                        if(Common.isIpAddress(mEdtIPAddress.getText().toString().trim())) {
                                            ipAddress = mEdtIPAddress.getText().toString().trim();
                                            if (mListener != null) {
                                                mListener.onPrinterConnection("WiFi", ipAddress);
                                            }
                                        }else
                                            mEdtIPAddress.setError("Please enter valid IP Address");
                                    } else {
                                        mEdtIPAddress.setError("Please enter valid IP Address");
                                    }
                                } else if(selectedId == mRadBluetooth.getId()) {
                                    if(mListener!=null){
                                        mListener.onPrinterConnection("Bluetooth", ipAddress);
                                    }
                                }

                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });
    }
}

package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.domain.net.NetworkDataRequestThread;
import com.zearoconsulting.zearopos.presentation.view.activity.POSActivity;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;
import com.zearoconsulting.zearopos.presentation.view.dialogs.NetworkErrorDialog;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * Created by saravanan on 24-11-2016.
 */

public class DenominationFragment extends AbstractDialogFragment {

    private FancyButton mBtnSubmit;
    private FancyButton mBtnCancel;
    private TextView mTxtTotalQR1;
    private TextView mTxtTotalQR5;
    private TextView mTxtTotalQR10;
    private TextView mTxtTotalQR50;
    private TextView mTxtTotalQR100;
    private TextView mTxtTotalQR500;
    private TextView mTxtTotalDR25;
    private TextView mTxtTotalDR50;
    private TextView mTxtTotal;

    private EditText mEdtQR1;
    private EditText mEdtQR5;
    private EditText mEdtQR10;
    private EditText mEdtQR50;
    private EditText mEdtQR100;
    private EditText mEdtQR500;
    private EditText mEdtDR25;
    private EditText mEdtDR50;
    private EditText mEdtTotal;

    private double qr1Value = 0;
    private double qr5Value = 0;
    private double qr10Value = 0;
    private double qr50Value = 0;
    private double qr100Value = 0;
    private double qr500Value = 0;
    private double dr25Value = 0;
    private double dr50Value = 0;
    private double drTotalValue = 0;

    private int qr1Count = 0;
    private int qr5Count = 0;
    private int qr10Count = 0;
    private int qr50Count = 0;
    private int qr100Count = 0;
    private int qr500Count = 0;
    private int dr25Count = 0;
    private int dr50Count = 0;

    private TextView mTxtStartTime;
    private TextView mTxtEndTime;
    private TextView mTxtWorkedHours;

    private long mStartTime, mCurrentTime, mDiffTime;
    SimpleDateFormat mDFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("Type");
            String jsonStr = msg.getData().getString("OUTPUT");

            switch (type) {
                case AppConstants.CLOSE_SESSION_REQUEST:
                    mParser.parseCloseSessionResponse(jsonStr, mHandler);
                    break;
                case AppConstants.CLOSE_SESSION_RESPONSE:
                    mProDlg.dismiss();
                    mAppManager.setLoggedIn(false);
                    dismiss();
                    getActivity().finish();
                    break;
                case AppConstants.SESSION_EXPIRED:
                    mProDlg.dismiss();
                    ((POSActivity) getActivity()).showCreateSession();
                    break;
                case AppConstants.SERVER_ERROR:
                    mProDlg.dismiss();
                    //show the server error dialog
                    Toast.makeText(getActivity(), "Server data error", Toast.LENGTH_SHORT).show();
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    };

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
        return inflater.inflate(R.layout.fragment_denomination, container, false);
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

        mProDlg = new ProgressDialog(getActivity());
        mProDlg.setIndeterminate(true);
        mProDlg.setCancelable(false);

        this.mTxtStartTime = ((TextView) paramView.findViewById(R.id.sessionCreatedTime));
        this.mTxtEndTime = ((TextView) paramView.findViewById(R.id.sessionCurrentTime));
        this.mTxtWorkedHours= ((TextView) paramView.findViewById(R.id.sessionHours));

        this.mBtnSubmit = ((FancyButton) paramView.findViewById(R.id.btnSupmit));
        this.mBtnCancel = ((FancyButton) paramView.findViewById(R.id.btnCancel));

        this.mTxtTotalQR1 = ((TextView) paramView.findViewById(R.id.txtTotalQR1));
        this.mTxtTotalQR5 = ((TextView) paramView.findViewById(R.id.txtTotalQR5));
        this.mTxtTotalQR10 = ((TextView) paramView.findViewById(R.id.txtTotalQR10));
        this.mTxtTotalQR50 = ((TextView) paramView.findViewById(R.id.txtTotalQR50));
        this.mTxtTotalQR100 = ((TextView) paramView.findViewById(R.id.txtTotalQR100));
        this.mTxtTotalQR500 = ((TextView) paramView.findViewById(R.id.txtTotalQR500));
        this.mTxtTotalDR25 = ((TextView) paramView.findViewById(R.id.txtTotalDR25));
        this.mTxtTotalDR50 = ((TextView) paramView.findViewById(R.id.txtTotalDR50));
        this.mTxtTotal = ((TextView) paramView.findViewById(R.id.txtTotal));

        this.mEdtTotal = ((EditText) paramView.findViewById(R.id.edtTotal));
        this.mEdtQR1 = ((EditText) paramView.findViewById(R.id.edtQR1));
        this.mEdtQR5 = ((EditText) paramView.findViewById(R.id.edtQR5));
        this.mEdtQR10 = ((EditText) paramView.findViewById(R.id.edtQR10));
        this.mEdtQR50 = ((EditText) paramView.findViewById(R.id.edtQR50));
        this.mEdtQR100 = ((EditText) paramView.findViewById(R.id.edtQR100));
        this.mEdtQR500 = ((EditText) paramView.findViewById(R.id.edtQR500));
        this.mEdtDR25 = ((EditText) paramView.findViewById(R.id.edtDR25));
        this.mEdtDR50 = ((EditText) paramView.findViewById(R.id.edtDR50));

        this.mEdtQR1.addTextChangedListener(new CustomWatcher(mEdtQR1));
        this.mEdtQR5.addTextChangedListener(new CustomWatcher(mEdtQR5));
        this.mEdtQR10.addTextChangedListener(new CustomWatcher(mEdtQR10));
        this.mEdtQR50.addTextChangedListener(new CustomWatcher(mEdtQR50));
        this.mEdtQR100.addTextChangedListener(new CustomWatcher(mEdtQR100));
        this.mEdtQR500.addTextChangedListener(new CustomWatcher(mEdtQR500));
        this.mEdtDR25.addTextChangedListener(new CustomWatcher(mEdtDR25));
        this.mEdtDR50.addTextChangedListener(new CustomWatcher(mEdtDR50));
        this.mEdtTotal.addTextChangedListener(new CustomWatcher(mEdtTotal));

        AppConstants.URL = AppConstants.kURLHttp+mAppManager.getServerAddress()+":"+mAppManager.getServerPort()+AppConstants.kURLServiceName+ AppConstants.kURLMethodApi;

        //set the session started time
        mStartTime = mAppManager.getSessionCreatedTime();
        this.mTxtStartTime.setText(String.valueOf(mDFormat.format(new Date(mStartTime))));

        //set the current time
        Date date = new Date(System.currentTimeMillis());
        mCurrentTime = date.getTime();
        this.mTxtEndTime.setText(String.valueOf(mDFormat.format(new Date(mCurrentTime))));

        //calculate time difference
        mDiffTime = mCurrentTime - mStartTime;
        long diffHours = mDiffTime / (60 * 60 * 1000) % 24;

        long diffSeconds = mDiffTime / 1000 % 60;
        long diffMinutes = mDiffTime / (60 * 1000) % 60;
        long diffDays = mDiffTime / (24 * 60 * 60 * 1000);

        //set the total working hours
        this.mTxtWorkedHours.setText(String.valueOf(diffDays)+" Days, "+String.valueOf(diffHours)+" Hours");

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
                                DenominationFragment.this.dismiss();
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
                                //create session data to server
                                postDenominationData();

                                //DenominationFragment.this.dismiss();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });
    }

    public class CustomWatcher implements TextWatcher {

        private View view;
        public CustomWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch(view.getId()){
                case R.id.edtQR1:
                    if (mEdtQR1.getText().toString().trim().length() != 0) {
                        qr1Count = Integer.parseInt(mEdtQR1.getText().toString());
                        qr1Value = Double.parseDouble(mEdtQR1.getText().toString())*1;
                    } else {
                        qr1Count=0;
                        qr1Value = 0;
                    }
                    mTxtTotalQR1.setText(String.valueOf(qr1Value));
                    updateTotal();
                    break;
                case R.id.edtQR5:
                    if (mEdtQR5.getText().toString().trim().length() != 0) {
                        qr5Count = Integer.parseInt(mEdtQR5.getText().toString());
                        qr5Value = Double.parseDouble(mEdtQR5.getText().toString())*5;
                    } else {
                        qr5Count=0;
                        qr5Value = 0;
                    }
                    mTxtTotalQR5.setText(String.valueOf(qr5Value));
                    updateTotal();
                    break;
                case R.id.edtQR10:
                    if (mEdtQR10.getText().toString().trim().length() != 0) {
                        qr10Count = Integer.parseInt(mEdtQR10.getText().toString());
                        qr10Value = Double.parseDouble(mEdtQR10.getText().toString())*10;
                    } else {
                        qr10Count=0;
                        qr10Value = 0;
                    }
                    mTxtTotalQR10.setText(String.valueOf(qr10Value));
                    updateTotal();
                    break;
                case R.id.edtQR50:
                    if (mEdtQR50.getText().toString().trim().length() != 0) {
                        qr50Count = Integer.parseInt(mEdtQR50.getText().toString());
                        qr50Value = Double.parseDouble(mEdtQR50.getText().toString())*50;
                    } else {
                        qr50Count=0;
                        qr50Value = 0;
                    }
                    mTxtTotalQR50.setText(String.valueOf(qr50Value));
                    updateTotal();
                    break;
                case R.id.edtQR100:
                    if (mEdtQR100.getText().toString().trim().length() != 0) {
                        qr100Count = Integer.parseInt(mEdtQR100.getText().toString());
                        qr100Value = Double.parseDouble(mEdtQR100.getText().toString())*100;
                    } else {
                        qr100Count=0;
                        qr100Value = 0;
                    }
                    mTxtTotalQR100.setText(String.valueOf(qr100Value));
                    updateTotal();
                    break;
                case R.id.edtQR500:
                    if (mEdtQR500.getText().toString().trim().length() != 0) {
                        qr500Count = Integer.parseInt(mEdtQR500.getText().toString());
                        qr500Value = Double.parseDouble(mEdtQR500.getText().toString())*500;
                    } else {
                        qr500Count=0;
                        qr500Value = 0;
                    }
                    mTxtTotalQR500.setText(String.valueOf(qr500Value));
                    updateTotal();
                    break;
                case R.id.edtDR25:
                    if (mEdtDR25.getText().toString().trim().length() != 0) {
                        dr25Count = Integer.parseInt(mEdtDR25.getText().toString());
                        dr25Value = (Double.parseDouble(mEdtDR25.getText().toString())*25)/100;
                    } else {
                        dr25Count=0;
                        dr25Value = 0;
                    }
                    mTxtTotalDR25.setText(String.valueOf(dr25Value));
                    updateTotal();
                    break;
                case R.id.edtDR50:
                    if (mEdtDR50.getText().toString().trim().length() != 0) {
                        dr50Count = Integer.parseInt(mEdtDR50.getText().toString());
                        dr50Value = (Double.parseDouble(mEdtDR50.getText().toString())*50)/100;
                    } else {
                        dr50Count=0;
                        dr50Value = 0;
                    }
                    mTxtTotalDR50.setText(String.valueOf(dr50Value));
                    updateTotal();
                    break;
                case R.id.edtTotal:
                    if (mEdtTotal.getText().toString().trim().length() != 0) {
                        drTotalValue = Double.parseDouble(mEdtTotal.getText().toString());
                    } else {
                        drTotalValue = 0;
                    }
                    updateTotal();
                    break;
            }
        }
    }

    private void updateTotal(){
        try{
            double total=0;
            if (mEdtTotal.getText().toString().trim().length() != 0) {
                total = drTotalValue;
            } else {
                total = qr1Value+qr5Value+qr10Value+qr50Value+qr100Value+qr500Value+dr25Value+dr50Value;
            }

            mTxtTotal.setText(String.valueOf(total));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private JSONArray generateDenominationArray(){
        JSONArray jsonArray = new JSONArray();

        try{
            JSONObject joQR500 = new JSONObject();
            joQR500.put("type", "riyal");
            joQR500.put("name", 500);
            joQR500.put("count", qr500Count);
            joQR500.put("total", qr500Value);

            JSONObject joQR100 = new JSONObject();
            joQR100.put("type", "riyal");
            joQR100.put("name", 100);
            joQR100.put("count", qr100Count);
            joQR100.put("total", qr100Value);

            JSONObject joQR50 = new JSONObject();
            joQR50.put("type", "riyal");
            joQR50.put("name", 50);
            joQR50.put("count", qr50Count);
            joQR50.put("total", qr50Value);

            JSONObject joQR10 = new JSONObject();
            joQR10.put("type", "riyal");
            joQR10.put("name", 10);
            joQR10.put("count", qr10Count);
            joQR10.put("total", qr10Value);

            JSONObject joQR5 = new JSONObject();
            joQR5.put("type", "riyal");
            joQR5.put("name", 5);
            joQR5.put("count", qr5Count);
            joQR5.put("total", qr5Value);

            JSONObject joQR1 = new JSONObject();
            joQR1.put("type", "riyal");
            joQR1.put("name", 1);
            joQR1.put("count", qr1Count);
            joQR1.put("total", qr1Value);

            JSONObject joDR50 = new JSONObject();
            joDR50.put("type", "dirhams");
            joDR50.put("name", 50);
            joDR50.put("count", dr50Count);
            joDR50.put("total", dr50Value);

            JSONObject joDR25 = new JSONObject();
            joDR25.put("type", "dirhams");
            joDR25.put("name", 25);
            joDR25.put("count", dr25Count);
            joDR25.put("total", dr25Value);

            JSONObject joTotal = new JSONObject();
            joTotal.put("type", "total");
            joTotal.put("name", 0);
            joTotal.put("count", 0);
            joTotal.put("total", drTotalValue);

            jsonArray.put(joQR500);
            jsonArray.put(joQR100);
            jsonArray.put(joQR50);
            jsonArray.put(joQR10);
            jsonArray.put(joQR5);
            jsonArray.put(joQR1);
            jsonArray.put(joDR50);
            jsonArray.put(joDR25);
            jsonArray.put(joTotal);

        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonArray;
    }

    private void postDenominationData() {

        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {
                mProDlg.setMessage("Close session...");
                mProDlg.show();
                JSONObject mJsonObj = mParser.getParams(AppConstants.CLOSE_SESSION_REQUEST);
                JSONArray mDenominatinObj = generateDenominationArray();
                mJsonObj.put("Denominations", mDenominatinObj);

                Log.i("CLOSE SESSION", mJsonObj.toString());

                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.CLOSE_SESSION_REQUEST);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(getActivity()).show();
        }

    }
}

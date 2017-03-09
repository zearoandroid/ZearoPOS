package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.domain.net.NetworkDataRequestThread;
import com.zearoconsulting.zearopos.presentation.model.Customer;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;
import com.zearoconsulting.zearopos.presentation.view.dialogs.NetworkErrorDialog;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.NetworkUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by saravanan on 31-12-2016.
 */

public class OrderCancelFragment extends AbstractDialogFragment {

    private FancyButton mBtnSubmit;
    private FancyButton mBtnCancel;
    private EditText mEdtReason;
    private IPOSListeners mListener = null;

    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("Type");
            String jsonStr = msg.getData().getString("OUTPUT");

            switch (type) {
                case AppConstants.CALL_CANCEL_POS_ORDER:
                    mParser.parseCancelOrder(jsonStr, mHandler);
                    break;
                case AppConstants.POS_ORDER_CANCEL_SUCCESS:
                    //INFORM USER POSTING FAILURE
                    mProDlg.dismiss();
                    if(mListener!=null){
                        mListener.orderCancelSuccess();
                    }
                    dismiss();
                    break;
                case AppConstants.POS_ORDER_CANCEL_FAILURE:
                    //INFORM USER POSTING FAILURE
                    mProDlg.dismiss();
                    if(mListener!=null){
                        mListener.orderCancelFailure();
                    }
                    dismiss();
                    break;
                case AppConstants.NO_DATA_RECEIVED:
                    //INFORM USER NO DATA
                    break;
                case AppConstants.NETWORK_ERROR:
                    //INFORM USER NO DATA
                    Toast.makeText(getActivity(), "Network problem...", Toast.LENGTH_SHORT).show();
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
        return inflater.inflate(R.layout.fragment_cancel_order, container, false);
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

        AppConstants.URL = AppConstants.kURLHttp+mAppManager.getServerAddress()+":"+mAppManager.getServerPort()+AppConstants.kURLServiceName+ AppConstants.kURLMethodApi;

        mListener= (IPOSListeners) getActivity();

        this.mBtnSubmit = ((FancyButton) paramView.findViewById(R.id.btnSupmit));
        this.mBtnCancel = ((FancyButton) paramView.findViewById(R.id.btnCancel));
        this.mEdtReason = ((EditText) paramView.findViewById(R.id.edtReason));

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
                                OrderCancelFragment.this.dismiss();
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

                                if (mEdtReason.getText().toString().trim().length() != 0) {
                                    //call void api to update kot
                                    postPOSCancelOrder(mEdtReason.getText().toString());
                                } else {
                                    mEdtReason.setError("Please enter reason for cancelling the order");
                                }
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });

    }

    private void postPOSCancelOrder(String reason) {

        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {
                mProDlg.setMessage("Cancel the order...");
                mProDlg.show();
                double mPaidTotalCardAmount = 0;

                double empty = 0;
                // Create a hash map
                HashMap hm = new HashMap();
                // Put elements to the map
                hm.put("CASH", empty);
                hm.put("AMEX", empty);
                hm.put("VISA", empty);
                hm.put("MASTER", empty);
                hm.put("GIFT", empty);
                hm.put("OTHER", empty);

                Customer mCustomer = mDBHelper.getPOSCustomer(AppConstants.posID);

                List<POSLineItem> lineItem = mDBHelper.getPOSLineItems(AppConstants.posID,0);
                double mTotalBill = mDBHelper.sumOfProductsTotalPrice(AppConstants.posID);

                JSONObject mJsonObj = mParser.getParams(AppConstants.CALL_CANCEL_POS_ORDER);
                JSONObject mHeaderObj = mParser.getHeaderObj(AppConstants.posID, mCustomer, lineItem.size(), mTotalBill, 0, 0, 0, mPaidTotalCardAmount);
                mJsonObj.put("OrderHeaders", mHeaderObj);
                JSONArray mPaymentObj = mParser.getPaymentObj(hm);
                mJsonObj.put("PaymentDetails", mPaymentObj);

                lineItem = mDBHelper.getPOSLineItems(AppConstants.posID, 0);
                JSONArray mOrddersObj = mParser.getOrderItems(lineItem);
                mJsonObj.put("OrderDetails", mOrddersObj);
                mJsonObj.put("reason", reason);
                mJsonObj.put("authorizedBy", AppConstants.authorizedId);

                if (mDBHelper.isKOTAvailable(AppConstants.posID)) {
                    List<Long> kotHeaderList = mDBHelper.getKOTNumberList(AppConstants.posID);
                    JSONArray mKOTObj = mParser.getPrintedKOT(kotHeaderList);
                    mJsonObj.put("KOTNumbers", mKOTObj);
                }

                Log.i("POST CANCEL ORDER", mJsonObj.toString());
                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.CALL_CANCEL_POS_ORDER);
                thread.start();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(getActivity()).show();
        }
    }
}

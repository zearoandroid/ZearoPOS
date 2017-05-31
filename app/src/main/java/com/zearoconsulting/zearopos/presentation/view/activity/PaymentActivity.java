package com.zearoconsulting.zearopos.presentation.view.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidadvance.topsnackbar.TSnackbar;
import com.sewoo.jpos.command.ESCPOSConst;
import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.domain.net.NetworkDataRequestThread;
import com.zearoconsulting.zearopos.domain.receivers.ConnectivityReceiver;
import com.zearoconsulting.zearopos.presentation.model.BPartner;
import com.zearoconsulting.zearopos.presentation.model.Customer;
import com.zearoconsulting.zearopos.presentation.model.KOTHeader;
import com.zearoconsulting.zearopos.presentation.model.KOTLineItems;
import com.zearoconsulting.zearopos.presentation.model.Organization;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.POSOrders;
import com.zearoconsulting.zearopos.presentation.model.POSPayment;
import com.zearoconsulting.zearopos.presentation.pdf.InvoiceReport;
import com.zearoconsulting.zearopos.presentation.presenter.IPrintingListeners;
import com.zearoconsulting.zearopos.presentation.presenter.OrderStatusListener;
import com.zearoconsulting.zearopos.presentation.print.InvoiceBill;
import com.zearoconsulting.zearopos.presentation.print.InvoicePrintTask;
import com.zearoconsulting.zearopos.presentation.print.WiFiPrintTaskParams;
import com.zearoconsulting.zearopos.presentation.view.component.CustomKeyboard;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;
import com.zearoconsulting.zearopos.presentation.view.dialogs.AlertView;
import com.zearoconsulting.zearopos.presentation.view.dialogs.NetworkErrorDialog;
import com.zearoconsulting.zearopos.presentation.view.fragment.AppUpdateFragment;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;
import com.zearoconsulting.zearopos.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class PaymentActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener, View.OnKeyListener, IPrintingListeners {

    private static Context mContext;
    private CustomKeyboard mCustomKeyboard;
    private TableLayout mTblPayTypes;
    private TextView mTxtPaymentTitle;
    private TextView mOldTotalAmountView;
    private TextView mDiscountAmountView;
    private TextView mTotalAmountView;
    private TextView mReturnAmountView;
    private EditText mEdtPaidAmount;
    private EditText mEdtPaidAmex;
    private EditText mEdtPaidGift;
    private EditText mEdtPaidMaster;
    private EditText mEdtPaidVisa;
    private EditText mEdtPaidOther;
    private EditText mEdtReason;
    private Button mBtnComplete;

    //Title TextView's
    private POSPayment mPayment;
    private String mCardType;
    private Customer mCustomer;
    private List<POSLineItem> mPOSLineItemList;
    private double mTotalAmount = 0;
    private double mPaidCashAmount = 0;
    private double mPaidAmexAmount = 0;
    private double mPaidGiftAmount = 0;
    private double mPaidMasterAmount = 0;
    private double mPaidVisaAmount = 0;
    private double mReturnAmount = 0;
    private double mPaidOtherAmount = 0;
    private double mPaidTotalAmount;
    private double mPaidCardAmount;
    private int mTotalLines;
    private double mFinalAmount = 0;
    private ReboundListener mReboundListener;
    private int bltResult = 0;
    private POSOrders order;
    private BPartner bPartner;
    private RadioGroup mRGPayType;
    private RadioButton onCashCard, onComplement;

    private ImageView mImgClosePayment;
    private SharedPreferences mSharedPreferences;
    private String ConnectType = "USB";

    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("Type");
            String jsonStr = msg.getData().getString("OUTPUT");

            switch (type) {
                case AppConstants.CALL_RELEASE_POS_ORDER:
                    mParser.parseReleaseOrderJson(jsonStr, mHandler);
                    break;
                case AppConstants.POS_ORDER_RELEASED_SUCCESS:
                    mProDlg.dismiss();
                    mBtnComplete.setClickable(true);
                    //printInvoice();
                    TSnackbar.make(findViewById(R.id.layPayment), "ORDER POSTED", TSnackbar.LENGTH_LONG)
                            .show();
                    if (ConnectType.equalsIgnoreCase("Bluetooth")) {
                        printBill();
                    } else {
                        printInvoice();
                    }
                    break;
                case AppConstants.POS_ORDER_RELEASED_FAILURE:
                    //INFORM USER POSTING FAILURE
                    mProDlg.dismiss();
                    mBtnComplete.setClickable(true);
                    //POSPayment payment = mDBHelper.getPaymentDetails(AppConstants.posID);
                    TSnackbar.make(findViewById(R.id.layPayment), "ORDER POSTING ERROR", TSnackbar.LENGTH_LONG)
                            .show();
                    //finish();
                    break;
                case AppConstants.NETWORK_ERROR:
                    //INFORM USER NETWORK FAILURE
                    mProDlg.dismiss();
                    mBtnComplete.setClickable(true);
                    TSnackbar.make(findViewById(R.id.layPayment), "NETWORK ERROR...", TSnackbar.LENGTH_LONG)
                            .show();
                    finish();
                    break;
                case AppConstants.SESSION_EXPIRED:
                    //show the server error dialog
                    mProDlg.dismiss();
                    TSnackbar.make(findViewById(R.id.layPayment), "Session expired...", TSnackbar.LENGTH_LONG)
                            .show();
                    finish();
                    break;
                case AppConstants.SERVER_ERROR:
                    //show the server error dialog
                    TSnackbar.make(findViewById(R.id.layPayment), "Server data error", TSnackbar.LENGTH_LONG)
                            .show();
                    break;
                case AppConstants.UPDATE_APP:
                    mProDlg.dismiss();
                    showAppInstallDialog();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_payment);

            mContext = this;

            // Get the instance of SharedPreferences object
            //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

            //ConnectType = mSharedPreferences.getString("prefPrintOptions","USB");
            ConnectType = mAppManager.getPrinterMode();

            mTblPayTypes = (TableLayout) findViewById(R.id.layPaymentTypes);
            onCashCard = (RadioButton) findViewById(R.id.rbCashCard);
            onComplement = (RadioButton) findViewById(R.id.rbComplement);
            mTxtPaymentTitle = (TextView) findViewById(R.id.txtPaymentTitle);
            mOldTotalAmountView = (TextView) findViewById(R.id.txtOldTotalAmount);
            mDiscountAmountView = (TextView) findViewById(R.id.txtDiscountAmount);
            mTotalAmountView = (TextView) findViewById(R.id.txtTotalAmount);
            mReturnAmountView = (TextView) findViewById(R.id.txtReturnAmount);
            mEdtPaidAmount = (EditText) findViewById(R.id.edtPaidAmount);
            mEdtPaidAmount = (EditText) findViewById(R.id.edtPaidAmount);
            mEdtPaidAmex = (EditText) findViewById(R.id.edtPaidAmex);
            mEdtPaidGift = (EditText) findViewById(R.id.edtPaidGiftcard);
            mEdtPaidOther = (EditText) findViewById(R.id.edtPaidOther);
            mEdtPaidMaster = (EditText) findViewById(R.id.edtPaidMastercard);
            mEdtPaidVisa = (EditText) findViewById(R.id.edtPaidVisa);
            mEdtReason = (EditText) findViewById(R.id.edtReason);
            mBtnComplete = (Button) findViewById(R.id.btnComplete);
            mRGPayType = (RadioGroup) findViewById(R.id.rgPayment);
            mImgClosePayment = (ImageView) findViewById(R.id.imgClosePayment);

            mReboundListener = new ReboundListener();

            //AppConstants.isKOTParsing = true;

            mCustomer = mDBHelper.getPOSCustomer(AppConstants.posID);
            mPOSLineItemList = mDBHelper.getPOSLineItems(AppConstants.posID, 0);

            mTotalLines = mPOSLineItemList.size();

            mTotalAmount = mDBHelper.sumOfProductsWithoutDiscount(AppConstants.posID);
            mFinalAmount = mDBHelper.sumOfProductsTotalPrice(AppConstants.posID);

            mFinalAmount = Double.parseDouble(Common.valueFormatter(mFinalAmount));

            mTxtPaymentTitle.setText("Payment (" + AppConstants.posID + ")");
            mOldTotalAmountView.setText(Common.valueFormatter(mTotalAmount));
            mDiscountAmountView.setText(Common.valueFormatter(mTotalAmount - mFinalAmount));
            mTotalAmountView.setText(Common.valueFormatter(mFinalAmount));

            order = mDBHelper.getPosHeader(AppConstants.posID);
            bPartner = mDBHelper.getBPartner(mAppManager.getClientID(), mAppManager.getOrgID(), order.getBpId());

            AppConstants.URL = AppConstants.kURLHttp + mAppManager.getServerAddress() + ":" + mAppManager.getServerPort() + AppConstants.kURLServiceName + AppConstants.kURLMethodApi;

            //mEdtPaidAmount.setText(String.valueOf(mFinalAmount));
            mProDlg = new ProgressDialog(this);
            mProDlg.setIndeterminate(true);
            mProDlg.setCancelable(false);

            //mRGPayType.setVisibility(View.GONE);

            /*if (bPartner.getIsCredit().equalsIgnoreCase("Y")) {
                mRGPayType.setVisibility(View.VISIBLE);
                mRGPayType.check(R.id.rbCredit);
                mTblPayTypes.setVisibility(View.GONE);
            } else {

            }*/

            // set onkeyListner
            /*mEdtPaidAmount.setOnKeyListener(this);
            mEdtPaidAmex.setOnKeyListener(this);
            mEdtPaidGift.setOnKeyListener(this);
            mEdtPaidOther.setOnKeyListener(this);
            mEdtPaidMaster.setOnKeyListener(this);
            mEdtPaidVisa.setOnKeyListener(this);*/

            mEdtPaidAmount.addTextChangedListener(new CustomWatcher(mEdtPaidAmount));
            mEdtPaidAmex.addTextChangedListener(new CustomWatcher(mEdtPaidAmex));
            mEdtPaidGift.addTextChangedListener(new CustomWatcher(mEdtPaidGift));
            mEdtPaidMaster.addTextChangedListener(new CustomWatcher(mEdtPaidMaster));
            mEdtPaidVisa.addTextChangedListener(new CustomWatcher(mEdtPaidVisa));
            mEdtPaidOther.addTextChangedListener(new CustomWatcher(mEdtPaidOther));


            if (ConnectType.equalsIgnoreCase("Bluetooth")) {
                BluetoothAdapter mBltAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBltAdapter == null) {
                    // Device does not support Bluetooth
                    return;
                }
                if (!mBltAdapter.isEnabled()) {
                    AlertView.showAlert("Info", "Enable Bluetooth connection", PaymentActivity.this);
                    finish();
                }
            }

            /*mCustomKeyboard= new CustomKeyboard(this, R.id.keyboardview, R.xml.hexkbd );

            mCustomKeyboard.registerEditText(R.id.edtPaidAmount);
            mCustomKeyboard.registerEditText(R.id.edtPaidAmex);
            mCustomKeyboard.registerEditText(R.id.edtPaidGiftcard);
            mCustomKeyboard.registerEditText(R.id.edtPaidOther);
            mCustomKeyboard.registerEditText(R.id.edtPaidMastercard);
            mCustomKeyboard.registerEditText(R.id.edtPaidVisa);*/

            mRGPayType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // find which radio button is selected
                    if (checkedId == R.id.rbCashCard) {
                        mTblPayTypes.setVisibility(View.VISIBLE);
                    } else if (checkedId == R.id.rbComplement) {
                        mTblPayTypes.setVisibility(View.GONE);
                    }
                }

            });

            mImgClosePayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //AppConstants.isKOTParsing = false;
                    finish();
                }
            });

            mBtnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                    /*BluetoothAdapter mBltAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBltAdapter == null) {
                        // Device does not support Bluetooth
                        return;
                    }
                    if (!mBltAdapter.isEnabled()) {
                        AlertView.showAlert("Info", "Enable Bluetooth connection", PaymentActivity.this);
                        finish();
                    }*/

                        if (bPartner.getIsCredit().equalsIgnoreCase("Y")) {
                            if (bPartner.getCreditLimit() < mFinalAmount)
                                TSnackbar.make(findViewById(R.id.layPayment), "Please check the credit limit", TSnackbar.LENGTH_LONG).show();
                            else {
                                mBtnComplete.setClickable(false);
                                savePaymentDetail();
                                postPOSOrder();
                            }
                        } else {
                            int selectedId = mRGPayType.getCheckedRadioButtonId();
                            // find which radioButton is checked by id
                            if (selectedId == onCashCard.getId()) {
                                mReturnAmount = validateReturnAmount();
                            } else if (selectedId == onComplement.getId()) {
                                mPaidCashAmount = mTotalAmount;
                                mPaidCardAmount = 0;
                                mPaidTotalAmount = (mPaidCashAmount + mPaidCardAmount);
                                mReturnAmount = mFinalAmount - mPaidTotalAmount;

                                if(mEdtReason.getText().toString().trim().length() == 0 && mEdtReason.getText().toString().equals("")){
                                    TSnackbar.make(findViewById(R.id.layPayment), "Please enter the reason", TSnackbar.LENGTH_LONG)
                                            .show();
                                    return;
                                }
                            }

                            if (mReturnAmount <= 0) {
                                mBtnComplete.setClickable(false);
                                savePaymentDetail();
                                postPOSOrder();
                            } else {
                                TSnackbar.make(findViewById(R.id.layPayment), "Please verify the amount", TSnackbar.LENGTH_LONG)
                                        .show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // NOTE Trap the back key: when the CustomKeyboard is still visible hide it, only when it is invisible, finish activity
        /*if (mCustomKeyboard != null) {
            if (mCustomKeyboard.isCustomKeyboardVisible())
                mCustomKeyboard.hideCustomKeyboard();
            else
                this.finish();
        } else {
            this.finish();
        }*/
    }

    private void printBill() {
        try {
            if (mBluetoothAdapter != null && bluetoothPort != null) {

                if (mBluetoothAdapter.isEnabled()) {

                    if (bluetoothPort.isConnected()) {

                        if (androidMSR != null) {
                            androidMSR.cancelMSR();
                        }

                        Organization orgDetail = mDBHelper.getOrganizationDetail(mAppManager.getOrgID());
                        InvoiceBill bill = new InvoiceBill(PaymentActivity.this, AppConstants.posID, mPOSLineItemList, mTotalAmount, mFinalAmount, mPaidTotalAmount, mReturnAmount);
                        bill.setOrgDetails(orgDetail);
                        bill.setAmountDetails(mPayment);
                        bltResult = bill.billGenerateAndPrint();

                        if (bltResult != 0) {
                            switch (bltResult) {
                                case ESCPOSConst.STS_PAPEREMPTY:
                                    AlertView.showAlert("Error", "Paper Empty", PaymentActivity.this);
                                    break;
                                case ESCPOSConst.STS_COVEROPEN:
                                    AlertView.showAlert("Error", "Cover Open", PaymentActivity.this);
                                    break;
                                case ESCPOSConst.STS_PAPERNEAREMPTY:
                                    AlertView.showAlert("Warning", "Paper Near Empty", PaymentActivity.this);
                                    break;
                            }
                        } else {
                            onPrintTaskComplete(AppConstants.posID);
                        }
                    } else {
                        AlertView.showAlert("Info", "Connect Printer", PaymentActivity.this);
                    }
                }
            } else {
                AlertView.showAlert("Info", "Enable Bluetooth connection", PaymentActivity.this);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkReturnAmount() {
        try {
            mReturnAmount = validateReturnAmount();
            if (mReturnAmount <= 0) {
            } else {
                // Toast.makeText(PaymentActivity.this, "Please verify the amount", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double validateReturnAmount() {

        try {
            if (mEdtPaidAmount.getText().toString().trim().length() != 0 && !mEdtPaidAmount.getText().toString().equals("")) {
                mPaidCashAmount = Double.parseDouble(mEdtPaidAmount.getText().toString());
            } else {
                mPaidCashAmount = 0;
            }

            if (mEdtPaidAmex.getText().toString().trim().length() != 0 && !mEdtPaidAmex.getText().toString().equals("")) {
                mPaidAmexAmount = Double.parseDouble(mEdtPaidAmex.getText().toString());
            } else {
                mPaidAmexAmount = 0;
            }

            if (mEdtPaidGift.getText().toString().trim().length() != 0 && !mEdtPaidGift.getText().toString().equals("")) {
                mPaidGiftAmount = Double.parseDouble(mEdtPaidGift.getText().toString());
            } else {
                mPaidGiftAmount = 0;
            }

            if (mEdtPaidOther.getText().toString().trim().length() != 0 && !mEdtPaidOther.getText().toString().equals("")) {
                mPaidOtherAmount = Double.parseDouble(mEdtPaidOther.getText().toString());
            } else {
                mPaidOtherAmount = 0;
            }

            if (mEdtPaidMaster.getText().toString().trim().length() != 0 && !mEdtPaidMaster.getText().toString().equals("")) {
                mPaidMasterAmount = Double.parseDouble(mEdtPaidMaster.getText().toString());
            } else {
                mPaidMasterAmount = 0;
            }

            if (mEdtPaidVisa.getText().toString().trim().length() != 0 && !mEdtPaidVisa.getText().toString().equals("")) {
                mPaidVisaAmount = Double.parseDouble(mEdtPaidVisa.getText().toString());
            } else {
                mPaidVisaAmount = 0;
            }

            //mPaidTotalAmount = (mPaidCashAmount + mPaidAmexAmount + mPaidGiftAmount + mPaidMasterAmount + mPaidVisaAmount+mPaidOtherAmount);
            mPaidCardAmount = (mPaidAmexAmount + mPaidGiftAmount + mPaidMasterAmount + mPaidVisaAmount + mPaidOtherAmount);

            mPaidTotalAmount = (mPaidCashAmount + mPaidCardAmount);
            mReturnAmount = mFinalAmount - mPaidTotalAmount;
        } catch (Exception e) {
            e.printStackTrace();
            mReturnAmount = 0;
        }

        return mReturnAmount;
    }

    private void savePaymentDetail() {

        mPayment = new POSPayment();

        if (mPaidTotalAmount > mFinalAmount) {
            mPaidCashAmount = mPaidCashAmount + mReturnAmount;
        }

        mPayment.setCash(mPaidCashAmount);
        mPayment.setAmex(mPaidAmexAmount);
        mPayment.setGift(mPaidGiftAmount);
        mPayment.setMaster(mPaidMasterAmount);
        mPayment.setVisa(mPaidVisaAmount);
        mPayment.setOther(mPaidOtherAmount);
        mPayment.setChange(mReturnAmount);
        mPayment.setPosId(AppConstants.posID);

        int selectedId = mRGPayType.getCheckedRadioButtonId();
        // find which radioButton is checked by id
        if (selectedId == onCashCard.getId()) {
            mPayment.setIsComplement(0);
        } else if (selectedId == onComplement.getId()) {
            mPayment.setIsComplement(1);
        }

        mDBHelper.addPOSPayments(mPayment);
    }

    private void postPOSOrder() {
       /*if(mBluetoothAdapter!=null && bluetoothPort!=null) {
            if (mBluetoothAdapter.isEnabled()) {

                if (bluetoothPort.isConnected()) {*/
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {

            try {

                mProDlg.setMessage("Posting data...");
                mProDlg.show();
                double mPaidTotalCardAmount = mPaidAmexAmount + mPaidGiftAmount + mPaidMasterAmount + mPaidVisaAmount + mPaidOtherAmount;

                // Create a hash map
                HashMap hm = new HashMap();
                // Put elements to the map
                hm.put("CASH", mPaidCashAmount);
                hm.put("AMEX", mPaidAmexAmount);
                hm.put("VISA", mPaidVisaAmount);
                hm.put("MASTER", mPaidMasterAmount);
                hm.put("GIFT", mPaidGiftAmount);
                hm.put("OTHER", mPaidOtherAmount);

                JSONObject mJsonObj = mParser.getParams(AppConstants.CALL_RELEASE_POS_ORDER);
                JSONObject mHeaderObj = mParser.getHeaderObj(AppConstants.posID, mCustomer, mTotalLines, mFinalAmount, mPaidTotalAmount, mReturnAmount, mPaidCashAmount, mPaidTotalCardAmount);
                mHeaderObj.put("isCredit", "N");
                mHeaderObj.put("isComplement", "N");
                int selectedId = mRGPayType.getCheckedRadioButtonId();
                // find which radioButton is checked by id
                if (selectedId == onCashCard.getId()) {
                    mHeaderObj.put("isComplement", "N");
                } else if (selectedId == onComplement.getId()) {
                    mHeaderObj.put("isComplement", "Y");
                    mHeaderObj.put("IsCash", "N");
                    mHeaderObj.put("IsCard", "N");
                }

                mJsonObj.put("OrderHeaders", mHeaderObj);
                JSONArray mPaymentObj = mParser.getPaymentObj(hm);
                mJsonObj.put("PaymentDetails", mPaymentObj);

                mPOSLineItemList = mDBHelper.getPOSLineItems(AppConstants.posID, 0);
                JSONArray mOrddersObj = mParser.getOrderItems(mPOSLineItemList);
                mJsonObj.put("OrderDetails", mOrddersObj);
                mJsonObj.put("reason", mEdtReason.getText().toString());

                if (mDBHelper.isKOTAvailable(AppConstants.posID)) {
                    List<Long> kotHeaderList = mDBHelper.getKOTNumberList(AppConstants.posID);
                    JSONArray mKOTObj = mParser.getPrintedKOT(kotHeaderList);
                    mJsonObj.put("KOTNumbers", mKOTObj);
                }

                Log.i("POST ORDER", mJsonObj.toString());

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.URL, mJsonObj,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                mParser.parseReleaseOrderJson(response.toString(), mHandler);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Server connection error! Try Again...", Toast.LENGTH_SHORT).show();
                        mProDlg.dismiss();
                        mBtnComplete.setClickable(true);
                    }
                }
                );

                // Adding request to request queue
                AndroidApplication.getInstance().addToRequestQueue(jsonObjReq);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*try {
                mProDlg.setMessage("Posting data...");
                mProDlg.show();
                double mPaidTotalCardAmount = mPaidAmexAmount + mPaidGiftAmount + mPaidMasterAmount + mPaidVisaAmount + mPaidOtherAmount;

                // Create a hash map
                HashMap hm = new HashMap();
                // Put elements to the map
                hm.put("CASH", mPaidCashAmount);
                hm.put("AMEX", mPaidAmexAmount);
                hm.put("VISA", mPaidVisaAmount);
                hm.put("MASTER", mPaidMasterAmount);
                hm.put("GIFT", mPaidGiftAmount);
                hm.put("OTHER", mPaidOtherAmount);

                JSONObject mJsonObj = mParser.getParams(AppConstants.CALL_RELEASE_POS_ORDER);
                JSONObject mHeaderObj = mParser.getHeaderObj(AppConstants.posID, mCustomer, mTotalLines, mFinalAmount, mPaidTotalAmount, mReturnAmount, mPaidCashAmount, mPaidTotalCardAmount);
                mHeaderObj.put("isCredit", "N");
                int selectedId = mRGPayType.getCheckedRadioButtonId();
                // find which radioButton is checked by id
                if(selectedId == onCredit.getId()) {
                    mHeaderObj.put("isCredit", "Y");
                } else if(selectedId == onCash.getId()) {
                    mHeaderObj.put("isCredit", "N");
                }

                mJsonObj.put("OrderHeaders", mHeaderObj);
                JSONArray mPaymentObj = mParser.getPaymentObj(hm);
                mJsonObj.put("PaymentDetails", mPaymentObj);

                mPOSLineItemList = mDBHelper.getPOSLineItems(AppConstants.posID, 0);
                JSONArray mOrddersObj = mParser.getOrderItems(mPOSLineItemList);
                mJsonObj.put("OrderDetails", mOrddersObj);
                mJsonObj.put("reason", mEdtReason.getText().toString());

                if (mDBHelper.isKOTAvailable(AppConstants.posID)) {
                    List<Long> kotHeaderList = mDBHelper.getKOTNumberList(AppConstants.posID);
                    JSONArray mKOTObj = mParser.getPrintedKOT(kotHeaderList);
                    mJsonObj.put("KOTNumbers", mKOTObj);
                }

                Log.i("POST ORDER", mJsonObj.toString());
                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.CALL_RELEASE_POS_ORDER);
                thread.start();
                //mProDlg.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        } else {
            mBtnComplete.setClickable(true);
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(PaymentActivity.this).show();

            //showNetworkErrorDialog();
        }
               /* }else{
                    mBtnComplete.setClickable(true);
                    AlertView.showAlert("Info", "Connect Printer", PaymentActivity.this);
                }
            }
        }*/
    }

    private void closePayment() {
        mDBHelper.deletePOSTables(AppConstants.posID);
        AppConstants.posID = 0;
        OrderStatusListener.getInstance().orderPosted(AppConstants.posID, true);
        finish();
    }

    private void createReport() {
        InvoiceReport mReport = new InvoiceReport(PaymentActivity.this, AppConstants.posID, mPOSLineItemList, mTotalAmount, mPaidCashAmount, mReturnAmount);
        MyThread myThread = new MyThread();
        myThread.start();
        mReport.createPdf();
        //mReport.viewPdf(PaymentActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        AndroidApplication.getInstance().setConnectivityListener(this);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = getResources().getString(R.string.internet_conn_msg);
            color = Color.WHITE;
        } else {
            message = getResources().getString(R.string.internet_disconn_msg);
            color = Color.RED;
            if (mProDlg.isShowing())
                mProDlg.dismiss();
        }
        TSnackbar.make(findViewById(R.id.layPayment), message, TSnackbar.LENGTH_LONG)
                .show();
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        switch (v.getId()) {

            case R.id.edtPaidAmount:
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    checkReturnAmount();
                }
                break;
            case R.id.edtPaidAmex:
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    checkReturnAmount();
                }
                break;
            case R.id.edtPaidGiftcard:
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    checkReturnAmount();
                }
                break;
            case R.id.edtPaidMastercard:
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    checkReturnAmount();
                }
                break;
            case R.id.edtPaidVisa:
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    checkReturnAmount();
                }
            case R.id.edtPaidOther:
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    checkReturnAmount();
                }
                break;
        }
        return false;
    }

    private void updateReturnAmount() {
        if (mReturnAmount == mFinalAmount)
            mReturnAmountView.setText(Common.valueFormatter(0));
        else
            mReturnAmountView.setText(Common.valueFormatter(mReturnAmount));
    }

    private void updateProductPrices() {
        for (int i = 0; i < mPOSLineItemList.size(); i++) {
            List<POSLineItem> extraLineItems = mDBHelper.getPOSExtraLineItems(mPOSLineItemList.get(i).getKotLineId());
            if (extraLineItems.size() != 0) {

                double price = 0;
                double discVal = 0;
                double stdPrice = 0;

                for (int j = 0; j < extraLineItems.size(); j++) {
                    price = price + extraLineItems.get(j).getTotalPrice();
                    stdPrice = stdPrice + extraLineItems.get(j).getStdPrice();
                    if (mPOSLineItemList.get(i).getDiscType() == 1)
                        discVal = discVal + extraLineItems.get(j).getDiscValue();
                }

                mPOSLineItemList.get(i).setStdPrice(mPOSLineItemList.get(i).getStdPrice() + stdPrice);
                mPOSLineItemList.get(i).setDiscValue(mPOSLineItemList.get(i).getDiscValue() + discVal);
                mPOSLineItemList.get(i).setTotalPrice(mPOSLineItemList.get(i).getTotalPrice() + price);
            }

        }
    }

    private void printInvoice() {

        if (AppConstants.posID != 0) {
            mDBHelper.releasePOSOrder(AppConstants.posID);

            if (mDBHelper.isKOTAvailable(AppConstants.posID)) {

                List<Long> kotTableList = mDBHelper.getKOTTableList(AppConstants.posID);

                //deleting the KOT LineItems
                mDBHelper.deleteKOTItems(AppConstants.posID);

                for (int i = 0; i < kotTableList.size(); i++) {

                    if (kotTableList.get(i) == 0) {
                        //get the KOTNumbers of Order
                        List<KOTHeader> kotNumberList = mDBHelper.getKOTNumbersFromKOTHeader(kotTableList.get(i), AppConstants.posID);
                        for (int j = 0; j < kotNumberList.size(); j++) {
                            List<KOTLineItems> kotLineItemList = mDBHelper.getKOTLineItemFromKOTNumber(kotNumberList.get(j).getKotNumber());
                            if (kotLineItemList.size() == 0)
                                mDBHelper.deleteKOTHeaders(kotNumberList.get(j).getKotNumber());
                        }

                    } else {
                        List<KOTLineItems> kotLineItemList = mDBHelper.getKOTLineItems(kotTableList.get(i));
                        if (kotLineItemList.size() == 0)
                            mDBHelper.deleteKOTDetails(kotTableList.get(i));
                    }
                }
            }
        }

        OrderStatusListener.getInstance().orderPostedAndPrint(AppConstants.posID, mTotalAmount, mFinalAmount, mPaidTotalAmount, mReturnAmount, mPaidCashAmount, mPaidAmexAmount, mPaidGiftAmount, mPaidMasterAmount, mPaidVisaAmount, mPaidOtherAmount, mPaidTotalAmount);
        //WiFiPrintTaskParams params = new WiFiPrintTaskParams(AppConstants.posID, mTotalAmount, mFinalAmount, mPaidTotalAmount, mReturnAmount, mPaidCashAmount, mPaidAmexAmount, mPaidGiftAmount, mPaidMasterAmount, mPaidVisaAmount, mPaidOtherAmount, mPaidTotalAmount);
        //InvoicePrintTask myTask = new InvoicePrintTask(mContext);
        //myTask.execute(params);
        finish();
    }

    private void printInvoices() {

        WiFiPrintTaskParams params = new WiFiPrintTaskParams(AppConstants.posID, mTotalAmount, mFinalAmount, mPaidTotalAmount, mReturnAmount, mPaidCashAmount, mPaidAmexAmount, mPaidGiftAmount, mPaidMasterAmount, mPaidVisaAmount, mPaidOtherAmount, mPaidTotalAmount);
        InvoicePrintTask myTask = new InvoicePrintTask(mContext);
        myTask.execute(params);
    }

    @Override
    public void onPrintTaskComplete(long orderNum) {

        if (orderNum != 0) {
            mDBHelper.releasePOSOrder(orderNum);

            if (mDBHelper.isKOTAvailable(orderNum)) {

                List<Long> kotTableList = mDBHelper.getKOTTableList(orderNum);

                mDBHelper.deleteKOTItems(orderNum);

                for (int i = 0; i < kotTableList.size(); i++) {
                    List<KOTLineItems> kotLineItemList = mDBHelper.getKOTLineItems(kotTableList.get(i));
                    if (kotLineItemList.size() == 0)
                        mDBHelper.deleteKOTDetails(kotTableList.get(i));
                }
            }

            closePayment();
        }
    }

    @Override
    public void onPrintTaskError(String error, long posNumber) {

        //show error dialog if press ok call the onPrintTaskComplete();
        TSnackbar.make(findViewById(R.id.layPayment), "PRINTER NOT CONNECTED", TSnackbar.LENGTH_LONG)
                .show();

        onPrintTaskComplete(posNumber);
    }

    public class CustomWatcher implements TextWatcher {

        private View view;

        public CustomWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.edtPaidAmount:
                    validateReturnAmount();
                    updateReturnAmount();
                    break;
                case R.id.edtPaidAmex:
                    validateReturnAmount();
                    if (mReturnAmount >= 0) {
                        updateReturnAmount();
                    } else {
                        String val = mEdtPaidAmex.getText().toString();
                        if (!val.equalsIgnoreCase("")) {
                            mEdtPaidAmex.setText(val.substring(0, val.length() - 1));
                            mEdtPaidAmex.setSelection(mEdtPaidAmex.getText().length());
                        }
                    }
                    break;
                case R.id.edtPaidGiftcard:
                    validateReturnAmount();
                    if (mReturnAmount >= 0) {
                        updateReturnAmount();
                    } else {
                        String val = mEdtPaidGift.getText().toString();
                        if (!val.equalsIgnoreCase("")) {
                            mEdtPaidGift.setText(val.substring(0, val.length() - 1));
                            mEdtPaidGift.setSelection(mEdtPaidGift.getText().length());
                        }
                    }
                    break;
                case R.id.edtPaidMastercard:
                    validateReturnAmount();
                    if (mReturnAmount >= 0) {
                        updateReturnAmount();
                    } else {
                        String val = mEdtPaidMaster.getText().toString();
                        if (!val.equalsIgnoreCase("")) {
                            mEdtPaidMaster.setText(val.substring(0, val.length() - 1));
                            mEdtPaidMaster.setSelection(mEdtPaidMaster.getText().length());
                        }
                    }
                    break;
                case R.id.edtPaidVisa:
                    validateReturnAmount();
                    if (mReturnAmount >= 0) {
                        updateReturnAmount();
                    } else {
                        String val = mEdtPaidVisa.getText().toString();
                        if (!val.equalsIgnoreCase("")) {
                            mEdtPaidVisa.setText(val.substring(0, val.length() - 1));
                            mEdtPaidVisa.setSelection(mEdtPaidVisa.getText().length());
                        }
                    }
                    break;
                case R.id.edtPaidOther:
                    validateReturnAmount();
                    if (mReturnAmount >= 0) {
                        updateReturnAmount();
                    } else {
                        String val = mEdtPaidOther.getText().toString();
                        if (!val.equalsIgnoreCase("")) {
                            mEdtPaidOther.setText(val.substring(0, val.length() - 1));
                            mEdtPaidOther.setSelection(mEdtPaidOther.getText().length());
                        }
                    }
                    break;
            }
        }
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            File dir = new File(Environment.getExternalStorageDirectory() + "PDF");
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }
        }
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_MENU:
                //Toast.makeText(this, "Menu key pressed", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_SEARCH:
                //Toast.makeText(this, "Search key pressed", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                //event.startTracking();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                //Toast.makeText(this,"Volumen Down pressed", Toast.LENGTH_SHORT).show();
                return false;
        }

        return true;
    }*/

    public void showAppInstallDialog(){
        try {
            //show denomination screen
            FragmentManager localFragmentManager = PaymentActivity.this.getSupportFragmentManager();
            AppUpdateFragment appUpdateFragment = new AppUpdateFragment();
            appUpdateFragment.show(localFragmentManager, "AppUpdateFragment");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

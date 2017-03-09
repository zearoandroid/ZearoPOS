package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.domain.net.NetworkDataRequestThread;
import com.zearoconsulting.zearopos.presentation.model.Warehouse;
import com.zearoconsulting.zearopos.presentation.presenter.ILoginListeners;
import com.zearoconsulting.zearopos.presentation.view.activity.POSActivity;
import com.zearoconsulting.zearopos.presentation.view.dialogs.NetworkErrorDialog;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.NetworkUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadingDialogFragment extends AbstractDialogFragment {


    private static Context context;
    private static ILoginListeners mListener;
    private String username, password, screen;
    private TextView statusText, progressText;
    private ProgressWheel progressWheel;
    private List<Long> mDefaultIdList;
    private long mCategoryId = 0;
    private Intent mIntent;
    private List<Warehouse> mWarehouseList;

    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("Type");
            String jsonStr = msg.getData().getString("OUTPUT");

            switch (type) {
                case AppConstants.GET_ORGANIZATION_DATA:
                    mParser.parseOrgJson(jsonStr, mHandler);
                    break;
                case AppConstants.CALL_AUTHENTICATE:
                    mParser.parseLoginJson(jsonStr, mHandler);
                    break;
                case AppConstants.ORGANIZATION_DATA_RECEIVED:
                    if (!mAppManager.getIsRetail())
                        authenticate();
                    else {
                        progressWheel.stopSpinning();
                        if (mListener != null)
                            mListener.onOrganizeDataReceived();
                        dismiss();
                    }
                    break;
                case AppConstants.LOGIN_SUCCESS:
                    getDefaultCustomerData();
                    break;
                case AppConstants.LOGIN_FAILURE:
                    progressWheel.stopSpinning();
                    if (mListener != null)
                        mListener.onLoginFailure();
                    dismiss();
                    break;
                case AppConstants.GET_CASH_CUSTOMER_DATA:
                    mParser.parseCommonJson(jsonStr, mHandler);
                    break;
                case AppConstants.COMMON_DATA_RECEIVED:
                    if (mAppManager.getUserName().equalsIgnoreCase(username) && mDBHelper.getAllProduct(mAppManager.getClientID(), mAppManager.getOrgID()).size() != 0) {
                        mAppManager.setLoggedIn(true);
                        gotoPOS();
                    } else {
                        mDBHelper.deletePOSRelatedTables();
                        getBPartners();
                    }
                    break;
                case AppConstants.GET_BPARTNERS:
                    mParser.parseBPartnerJson(jsonStr, mHandler);
                    break;
                case AppConstants.BPARTNER_DATA_RECEIVED:
                    getTables();
                    break;
                case AppConstants.NO_BPARTNER_DATA_RECEIVED:
                    getTables();
                    break;
                case AppConstants.GET_TABLES:
                    mParser.parseTables(jsonStr, mHandler);
                    break;
                case AppConstants.TABLES_RECEIVED:
                    getTerminals();
                    break;
                case AppConstants.NO_TABLE_DATA_RECEIVED:
                    getTerminals();
                    break;
                case AppConstants.GET_TERMINALS:
                    mParser.parseTerminals(jsonStr, mHandler);
                    break;
                case AppConstants.TERMINALS_RECEIVED:
                    getCategory();
                    break;
                case AppConstants.NO_TERMINAL_DATA_RECEIVED:
                    getCategory();
                    break;
                case AppConstants.GET_CATEGORY:
                    mParser.parseCategorysJson(jsonStr, mHandler);
                    break;
                case AppConstants.CATEGORY_RECEIVED:
                    getAllProducts();
                    break;
                case AppConstants.NO_CATEGORY_DATA_RECEIVED:
                    progressWheel.stopSpinning();
                    dismiss();
                    Toast.makeText(context, "Category and Products not available!", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.GET_ALL_PRODUCTS:
                    mParser.parseProductJson(jsonStr, mHandler);
                    break;
                case AppConstants.PRODUCTS_RECEIVED:
                    mAppManager.setLoggedIn(true);
                    if (mAppManager.getStartNumber() == 0)
                        getPOSNumber();
                    else
                        gotoPOS();
                    break;
                case AppConstants.GET_POS_NUMBER:
                    mParser.parsePOSNumber(jsonStr, mHandler);
                    break;
                case AppConstants.POS_NUMBER_RECEIVED:
                    gotoPOS();
                    break;
                case AppConstants.NO_DATA_RECEIVED:
                    progressWheel.stopSpinning();
                    dismiss();
                    break;
                case AppConstants.SERVER_ERROR:
                    progressWheel.stopSpinning();
                    Toast.makeText(context, "Server data error", Toast.LENGTH_SHORT).show();
                    dismiss();
                    break;
                case AppConstants.DEVICE_NOT_REGISTERED:
                    progressWheel.stopSpinning();
                    Toast.makeText(context, "Device not registered to server!", Toast.LENGTH_SHORT).show();
                    dismiss();
                    break;
                case AppConstants.NETWORK_ERROR:
                    //INFORM USER NO DATA
                    Toast.makeText(getActivity(), "NETWORK ERROR...", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public LoadingDialogFragment() {
        // Required empty public constructor
    }

    public static LoadingDialogFragment newInstance(Context paramContext, String paramString1, String paramString2, String paramString3) {
        LoadingDialogFragment localLoadingDialogFragment = new LoadingDialogFragment();
        Bundle localBundle = new Bundle();
        localBundle.putString("username", paramString1);
        localBundle.putString("password", paramString2);
        localBundle.putString("screen", paramString3);
        localLoadingDialogFragment.setArguments(localBundle);
        context = paramContext;
        if (context instanceof ILoginListeners) {
            mListener = (ILoginListeners) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IPOSListeners");
        }
        return localLoadingDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading_dialog, container, false);
    }

    @Override
    public void onViewCreated(View paramView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(paramView, savedInstanceState);

        this.username = getArguments().getString("username", "");
        this.password = getArguments().getString("password", "");
        this.screen = getArguments().getString("screen", "");

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

        this.statusText = ((TextView) paramView.findViewById(R.id.status_text));
        this.progressText = ((TextView) paramView.findViewById(R.id.progress_text));
        this.progressWheel = ((ProgressWheel) paramView.findViewById(R.id.progress_wheel));
        this.progressWheel.spin();
        this.statusText.setText("Please wait");

        AppConstants.URL = AppConstants.kURLHttp+mAppManager.getServerAddress()+":"+mAppManager.getServerPort()+AppConstants.kURLServiceName+ AppConstants.kURLMethodApi;

        mDefaultIdList = mDBHelper.getDefaultIds();
        if(mDefaultIdList.size()!=0)
            mAppManager.setOrgID(mDefaultIdList.get(0));

        mWarehouseList = mDBHelper.getWarehouse(mAppManager.getOrgID());

        if (mWarehouseList.size() != 0 && mAppManager.getIsRetail() && screen.equalsIgnoreCase("LOGIN")) {
            authenticate();
        } else if (screen.equalsIgnoreCase("LOGIN")) {
            retrievePOSData();
        } else if (screen.equalsIgnoreCase("SYNC")) {
            getBPartners();
        }

    }

    private void gotoPOS() {
        progressWheel.stopSpinning();
        dismiss();
        mIntent = new Intent(context, POSActivity.class);
        startActivity(mIntent);
        getActivity().finish();
    }

    private void retrievePOSData() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {

            mDBHelper.deleteOrgTables();

            JSONObject mJsonObj = mParser.getParams(AppConstants.GET_ORGANIZATION_DATA);

            NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.GET_ORGANIZATION_DATA);
            thread.start();
        } else {
            progressWheel.stopSpinning();
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(context).show();
        }
    }

    private void authenticate() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {
                mDefaultIdList = mDBHelper.getDefaultIds();
                mAppManager.setOrgID(mDefaultIdList.get(0));
                if (mWarehouseList.size() != 0 && mAppManager.getIsRetail()) {
                    mAppManager.setWarehouseID(mAppManager.getWarehouseID());
                } else {
                    mAppManager.setWarehouseID(mDefaultIdList.get(1));
                }

                mAppManager.setRoleID(mDefaultIdList.get(2));

                //call authenticate api
                JSONObject mJsonObj = mParser.getParams(AppConstants.CALL_AUTHENTICATE);
                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.CALL_AUTHENTICATE);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
                progressWheel.stopSpinning();
            }

        } else {
            progressWheel.stopSpinning();
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(context).show();
        }
    }

    private void getDefaultCustomerData() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            JSONObject mJsonObj = mParser.getParams(AppConstants.GET_CASH_CUSTOMER_DATA);
            NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.GET_CASH_CUSTOMER_DATA);
            thread.start();
        } else {
            progressWheel.stopSpinning();
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(context).show();
        }
    }

    public void getBPartners() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            JSONObject mJsonObj = mParser.getParams(AppConstants.GET_BPARTNERS);
            NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.GET_BPARTNERS);
            thread.start();
        } else {
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(context).show();
        }
    }

    public void getTables() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            JSONObject mJsonObj = mParser.getParams(AppConstants.GET_TABLES);
            NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.GET_TABLES);
            thread.start();
        } else {
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(context).show();
        }
    }

    public void getTerminals() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            JSONObject mJsonObj = mParser.getParams(AppConstants.GET_TERMINALS);
            NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.GET_TERMINALS);
            thread.start();
        } else {
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(context).show();
        }
    }

    private void getCategory() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            //GET THE CATEGORY DATA
            JSONObject mJsonObj = mParser.getParams(AppConstants.GET_CATEGORY);
            NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.GET_CATEGORY);
            thread.start();
        } else {
            progressWheel.stopSpinning();
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(context).show();
        }
    }

    private void getAllProducts() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {
                JSONObject mJsonObj = mParser.getParams(AppConstants.GET_ALL_PRODUCTS);
                mJsonObj.put("categoryId", mCategoryId);
                mJsonObj.put("pricelistId", mAppManager.getPriceListID());
                mJsonObj.put("costElementId", mAppManager.getCostElementID());
                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.GET_ALL_PRODUCTS);
                thread.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            progressWheel.stopSpinning();
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(context).show();
        }
    }

    /**
     * GET POS NUMBER FROM ERP
     */
    public void getPOSNumber() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {
                JSONObject mJsonObj = mParser.getParams(AppConstants.GET_POS_NUMBER);
                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.GET_POS_NUMBER);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            progressWheel.stopSpinning();
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(context).show();
        }
    }
}

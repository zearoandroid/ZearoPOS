package com.zearoconsulting.zearopos.domain.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.data.DBHelper;
import com.zearoconsulting.zearopos.data.POSDataSource;
import com.zearoconsulting.zearopos.domain.parser.JSONParser;
import com.zearoconsulting.zearopos.presentation.model.BPartner;
import com.zearoconsulting.zearopos.presentation.model.Customer;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.POSOrders;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class POSSyncService extends IntentService {
    private static final String TAG = "POSSyncService";
    private AppDataManager mAppManager;
    private POSDataSource mDBHelper;
    private JSONParser mParser;
    private Timer t = new Timer();
    private Handler updateHandler = new Handler();

    public POSSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try{
            System.out.println("Service started .... \n");

            mAppManager = AndroidApplication.getInstance().getAppManager();
            mDBHelper = AndroidApplication.getInstance().getPOSDataSource();
            mParser = new JSONParser(AndroidApplication.getAppContext(), mAppManager, mDBHelper);
            AppConstants.URL = AppConstants.kURLHttp+mAppManager.getServerAddress()+":"+mAppManager.getServerPort()+AppConstants.kURLServiceName+ AppConstants.kURLMethodApi;

            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    // just call the handler every 10 Seconds
                    try {
                        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {

                            if (!AppConstants.isPOSSyncing) {

                                Runnable runnable = new Runnable() {
                                    public void run() {
                                        Runnable myThread = new POSSyncService.SyncThread();
                                        new Thread(myThread).start();
                                    }
                                };

                                updateHandler.postDelayed(runnable, 30000);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 100, 5000);
        }catch (Exception e){
            e.printStackTrace();
        }

        //throw new UnsupportedOperationException("Not yet implemented");
    }
    public class SyncThread implements Runnable {

        @Override
        public void run() {
            try {
                List<POSOrders> posOrders = mDBHelper.getNotPostedPOSOrders();
                for(int i=0; i<posOrders.size(); i++){
                    String retSrc = "";
                    StringBuilder result;
                    long posId = posOrders.get(i).getPosId();
                    JSONObject errJson = new JSONObject();

                    if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
                        try {
                            URL url = new URL(AppConstants.URL);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoOutput(true);
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json");

                            OutputStream os = conn.getOutputStream();

                            JSONObject mJsonObj = mParser.getParams(AppConstants.CALL_RELEASE_POS_ORDER);

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

                            System.out.println("Updating Syncing Order Number is: " + posId);

                            Customer mCustomer = mDBHelper.getPOSCustomer(posId);

                            POSOrders order = mDBHelper.getPosHeader(posId);
                            BPartner bPartner = mDBHelper.getBPartner(mAppManager.getClientID(), mAppManager.getOrgID(), order.getBpId());

                            List<POSLineItem> lineItem = mDBHelper.getPOSLineItems(posId, 0);
                            double mTotalBill = mDBHelper.sumOfProductsTotalPrice(posId);

                            JSONObject mHeaderObj = mParser.getHeaderObj(posId, mCustomer, lineItem.size(), mTotalBill, 0, 0, 0, mPaidTotalCardAmount);
                            mHeaderObj.put("isCredit", "N");

                            // find which radioButton is checked by id
                            if (bPartner.getIsCredit().equalsIgnoreCase("Y")) {
                                mHeaderObj.put("isCredit", "Y");
                            }

                            mJsonObj.put("OrderHeaders", mHeaderObj);
                            JSONArray mPaymentObj = mParser.getPaymentObj(hm);
                            mJsonObj.put("PaymentDetails", mPaymentObj);

                            JSONArray mOrddersObj = mParser.getOrderItems(lineItem);
                            mJsonObj.put("OrderDetails", mOrddersObj);
                            mJsonObj.put("reason", "");
                            mJsonObj.put("authorizedBy", AppConstants.authorizedId);

                            os.write(mJsonObj.toString().getBytes());
                            os.flush();

                            BufferedReader br = new BufferedReader(new InputStreamReader(
                                    (conn.getInputStream())));

                            System.out.println("Output from Server .... \n");

                            //Receive the response from the server
                            InputStream in = new BufferedInputStream(conn.getInputStream());
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            result = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }

                            retSrc = result.toString();

                            System.out.println("Updating Syncing Status Service Data: " + retSrc);

                            conn.disconnect();

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            retSrc = errJson.toString();
                        } catch (IOException e) {
                            retSrc = errJson.toString();
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                            retSrc = errJson.toString();
                        }

                        //long returnId = mParser.parseSyncOrderJson(retSrc, posId);

                        //if (returnId == posId)
                            mDBHelper.releasePOSOrderFromLocal(posId, "Y");
                    }else{
                        break;
                    }

                }//end for-loop

                AppConstants.isPOSSyncing = false;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

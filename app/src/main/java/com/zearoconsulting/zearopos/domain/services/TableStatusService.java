package com.zearoconsulting.zearopos.domain.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.data.DBHelper;
import com.zearoconsulting.zearopos.data.POSDataSource;
import com.zearoconsulting.zearopos.domain.parser.JSONParser;
import com.zearoconsulting.zearopos.presentation.model.KOTHeader;
import com.zearoconsulting.zearopos.presentation.model.KOTLineItems;
import com.zearoconsulting.zearopos.presentation.model.Tables;
import com.zearoconsulting.zearopos.presentation.model.Terminals;
import com.zearoconsulting.zearopos.presentation.presenter.OrderStatusListener;
import com.zearoconsulting.zearopos.presentation.presenter.ParsingStatusListener;
import com.zearoconsulting.zearopos.presentation.print.PublicAction;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;
import com.zearoconsulting.zearopos.utils.NetworkUtil;
import com.zearoconsulting.zearopos.utils.StringAlignUtils;

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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import HPRTAndroidSDK.HPRTPrinterHelper;
import HPRTAndroidSDK.PublicFunction;

/**
 * Created by saravanan on 15-12-2016.
 */

public class TableStatusService extends IntentService implements ParsingStatusListener.OnParsingStateListener {

    private static final String TAG = "TableStatusService";
    private AppDataManager mAppManager;
    private POSDataSource mDBHelper;
    private JSONParser mParser;
    private Timer t = new Timer();
    private HPRTPrinterHelper hprtPrinter;
    private ArrayAdapter arrPrinterList;
    private Context thisCon;
    private PublicFunction pFun = null;
    private PublicAction pAct = null;
    private String printerName = "";
    private String strPort = "9100";
    private StringAlignUtils alignUtils = new StringAlignUtils(42, StringAlignUtils.Alignment.CENTER);

    private Handler updateHandler = new Handler();
    List<KOTHeader> kotPrintList;

    public TableStatusService() {
        super(TAG);

        //register the order state listener
        ParsingStatusListener.getInstance().setListener(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            System.out.println("Service started .... \n");

            thisCon = AndroidApplication.getAppContext();
            mAppManager = AndroidApplication.getInstance().getAppManager();
            mDBHelper = AndroidApplication.getInstance().getPOSDataSource();
            mParser = new JSONParser(AndroidApplication.getAppContext(), mAppManager, mDBHelper);
            AppConstants.URL = AppConstants.kURLHttp+mAppManager.getServerAddress()+":"+mAppManager.getServerPort()+AppConstants.kURLServiceName+ AppConstants.kURLMethodApi;
            try {
                hprtPrinter = new HPRTPrinterHelper();
                pFun = new PublicFunction(thisCon);
                pAct = new PublicAction(thisCon);
                InitCombox();
            } catch (Exception e) {
                e.printStackTrace();
            }

            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    // just call the handler every 10 Seconds
                    try {
                        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {


                            if (!AppConstants.isKOTParsing) {

                                String results = getResponse();

                                mParser.parseKOTData(results, null);

                                /*Runnable runnable = new Runnable() {
                                    public void run() {
                                        Runnable myThread = new PrintThread();
                                        new Thread(myThread).start();
                                    }
                                };

                                updateHandler.postDelayed(runnable, 5000);*/
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

    }

    public String getResponse() {
        String retSrc = "";
        StringBuilder result;

        JSONObject errJson = new JSONObject();

        try {
            URL url = new URL(AppConstants.URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();

            //GET_KOT_HEADER_LINE_DATA
            JSONObject mJsonObj = mParser.getParams(AppConstants.GET_KOT_HEADER_AND_lINES);
            os.write(mJsonObj.toString().getBytes());
            os.flush();

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

            System.out.println("Polling Service Data: " + retSrc);

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            retSrc = errJson.toString();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retSrc;
    }

    @Override
    public void onParsingSuccess() {
        Runnable myThread = new PrintThread();
        new Thread(myThread).start();
    }

    public class PrintThread implements Runnable {

        @Override
        public void run() {
            try {
                //get all kot from kotHeader if Printed status = N
                //kotHeaderList = mDBHelper.getKOTHeadersNotPrinted();
                kotPrintList = mDBHelper.getKOTHeadersNotPrinted();
                long invoiceNumber = 0;
                //for loop for kot

                for (int i = 0; i < kotPrintList.size(); i++) {

                    String tableName = "Counter sale";

                    //getting table data
                    Tables table = mDBHelper.getTableData(mAppManager.getClientID(), mAppManager.getOrgID(), kotPrintList.get(i).getTablesId());

                    if (table != null) {
                        tableName = table.getTableName();
                    } else {
                        invoiceNumber = kotPrintList.get(i).getInvoiceNumber();
                    }

                    //getting terminal data
                    Terminals terminals = mDBHelper.getTerminalData(mAppManager.getClientID(), mAppManager.getOrgID(), kotPrintList.get(i).getTerminalId());

                    //getting ipAddress
                    String ipAddress = terminals.getTerminalIP();

                    Log.i("IP Address", ipAddress);

                    if (Common.isIpAddress(ipAddress)) {

                        //get kotLine data from kotLineItems
                        List<KOTLineItems> kotLineItem = mDBHelper.getKOTLineItem(kotPrintList.get(i).getKotNumber());

                        //call Wi-Fi print
                        if (hprtPrinter != null) {
                            HPRTPrinterHelper.PortClose();
                        } else {
                        }

                        hprtPrinter = new HPRTPrinterHelper(thisCon, printerName);
                        if (HPRTPrinterHelper.PortOpen("WiFi," + ipAddress + "," + strPort) != 0) {
                            //Printer Not connected
                            Log.e("POSActivity-LAN PRINTER", "PRINTER NOT CONNECTED");
                        } else {
                            try {
                                Log.i("POSActivity-LAN PRINTER", "PRINTER CONNECTED");

                                pAct.LanguageEncode();
                                //pAct.BeforePrintAction();

                                //for (int k = 0; k < 2; k++) {

                                    HPRTPrinterHelper.PrintText(alignUtils.alignFormat(terminals.getTerminalName()) + "\n", 0, 16, 0);

                                    String tableHeader = "Table# " + tableName + " | KOT# " + String.valueOf(kotPrintList.get(i).getKotNumber());
                                    HPRTPrinterHelper.PrintText(alignUtils.alignFormat(tableHeader) + "\n", 0, 16, 0);

                                    if (invoiceNumber != 0) {
                                        HPRTPrinterHelper.PrintText(alignUtils.alignFormat("Inv No: " + invoiceNumber) + "\n", 0, 16, 0);
                                    }

                                    /**FOR MR. FOOD*/
                                    /*HPRTPrinterHelper.PrintText("------------------------------------------------\n", 0, 0, 0);
                                    HPRTPrinterHelper.PrintText("Order By: " + kotPrintList.get(i).getOrderBy() + "\n", 0, 0, 0);
                                    HPRTPrinterHelper.PrintText(Common.getDate() + "                            " + Common.getTime() + "\n", 0, 0, 0);
                                    HPRTPrinterHelper.PrintText("------------------------------------------------\n", 0, 0, 0);*/
                                    //HPRTPrinterHelper.PrintText(String.valueOf(kotPrintList.get(i).getTablesId())+"\n",0,2,0);

                                    /**FOR BRIYANI HOUSE*/
                                    HPRTPrinterHelper.PrintText("------------------------------------------\n", 0, 0, 0);
                                    HPRTPrinterHelper.PrintText("Order By: " + kotPrintList.get(i).getOrderBy() + "\n", 0, 0, 0);
                                    HPRTPrinterHelper.PrintText(Common.getDate() + "                      " + Common.getTime() + "\n", 0, 0, 0);
                                    HPRTPrinterHelper.PrintText("------------------------------------------\n", 0, 0, 0);

                                    for (int j = 0; j < kotLineItem.size(); j++) {
                                        HPRTPrinterHelper.PrintText(kotLineItem.get(j).getQty() + "   " + kotLineItem.get(j).getProduct().getProdName() + "\n", 0, 0, 0);

                                        if (kotLineItem.get(j).getNotes().trim().length() != 0)
                                            HPRTPrinterHelper.PrintText("     " + kotLineItem.get(j).getNotes() + "\n", 0, 0, 0);
                                    }
                                    /**FOR MR. FOOD*/
                                    //HPRTPrinterHelper.PrintText("------------------------------------------------\n", 0, 0, 0);

                                    /**FOR BRIYANI HOUSE*/
                                    HPRTPrinterHelper.PrintText("------------------------------------------\n", 0, 0, 0);
                                    HPRTPrinterHelper.PrintText(alignUtils.alignFormat("*** " + kotPrintList.get(i).getOrderType() + " ***") + "\n", 0, 16, 0);

                                    pAct.AfterPrintAction();

                                    HPRTPrinterHelper.CutPaper(HPRTPrinterHelper.HPRT_PARTIAL_CUT_FEED, 240);

                                    //update local status to printed
                                    mDBHelper.updateKOTStatusPrinted(kotPrintList.get(i).getKotNumber(),invoiceNumber);
                                //}
                            } catch (Exception e) {
                                Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
                            }
                        }
                    }
                }

                /*for (int i = 0; i < kotPrintList.size(); i++) {
                    //update local status to printed
                    mDBHelper.updateKOTStatusPrinted(kotPrintList.get(i).getKotNumber(), kotPrintList.get(i).getInvoiceNumber());
                }*/

                //Get not invoiced tables
                List<KOTHeader> kotHeaderList = mDBHelper.getDataAvailableKOTTables();

                for (int i = 0; i < kotHeaderList.size(); i++) {
                    //update orderAvailable = Y
                    mDBHelper.updateOrderAvailableTable(kotHeaderList.get(i).getTablesId());
                }

                String response = postPrintedKOT();
                mParser.parseKOTStatus(response, null);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                OrderStatusListener.getInstance().kotPrinted();
            }
        }
    }

    private String postPrintedKOT() {
        String retSrc = "";
        StringBuilder result;

        JSONObject errJson = new JSONObject();

        try {
            URL url = new URL(AppConstants.URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();

            //GET_KOT_HEADER_LINE_DATA
            JSONObject mJsonObj = mParser.getParams(AppConstants.POST_KOT_FLAGS);
            JSONArray mKOTObj = mParser.getPOSTPrintedKOT(kotPrintList);
            mJsonObj.put("KOTNumbers", mKOTObj);

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

            System.out.println("Updating Printing Status Service Data: " + retSrc);

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            retSrc = errJson.toString();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retSrc;
    }

    private void InitCombox() {
        try {
            arrPrinterList = new ArrayAdapter<String>(AndroidApplication.getAppContext(), android.R.layout.simple_spinner_item);
            String strSDKType = thisCon.getString(R.string.sdk_type);
            if (strSDKType.equals("all"))
                arrPrinterList = ArrayAdapter.createFromResource(AndroidApplication.getAppContext(), R.array.printer_list_all, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("hprt"))
                arrPrinterList = ArrayAdapter.createFromResource(AndroidApplication.getAppContext(), R.array.printer_list_hprt, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("mkt"))
                arrPrinterList = ArrayAdapter.createFromResource(AndroidApplication.getAppContext(), R.array.printer_list_mkt, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("mprint"))
                arrPrinterList = ArrayAdapter.createFromResource(AndroidApplication.getAppContext(), R.array.printer_list_mprint, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("sycrown"))
                arrPrinterList = ArrayAdapter.createFromResource(AndroidApplication.getAppContext(), R.array.printer_list_sycrown, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("mgpos"))
                arrPrinterList = ArrayAdapter.createFromResource(AndroidApplication.getAppContext(), R.array.printer_list_mgpos, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("ds"))
                arrPrinterList = ArrayAdapter.createFromResource(AndroidApplication.getAppContext(), R.array.printer_list_ds, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("cst"))
                arrPrinterList = ArrayAdapter.createFromResource(AndroidApplication.getAppContext(), R.array.printer_list_cst, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("other"))
                arrPrinterList = ArrayAdapter.createFromResource(AndroidApplication.getAppContext(), R.array.printer_list_other, android.R.layout.simple_spinner_item);
            arrPrinterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            printerName = arrPrinterList.getItem(0).toString();
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> InitCombox ")).append(e.getMessage()).toString());
        }
    }
}
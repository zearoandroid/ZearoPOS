package com.zearoconsulting.zearopos.presentation.print;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.data.POSDataSource;
import com.zearoconsulting.zearopos.presentation.exception.AppLog;
import com.zearoconsulting.zearopos.presentation.model.KOTHeader;
import com.zearoconsulting.zearopos.presentation.model.KOTLineItems;
import com.zearoconsulting.zearopos.presentation.model.Tables;
import com.zearoconsulting.zearopos.presentation.model.Terminals;
import com.zearoconsulting.zearopos.presentation.presenter.OrderStatusListener;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;
import com.zearoconsulting.zearopos.utils.StringAlignUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import HPRTAndroidSDK.HPRTPrinterHelper;
import HPRTAndroidSDK.PublicFunction;

/**
 * Created by saravanan on 25-04-2017.
 */

public class KOTPrintTask extends AsyncTask<Void, Void, Boolean> {

    private HPRTPrinterHelper hprtPrinter;
    private ArrayAdapter arrPrinterList;
    private Context thisCon;
    private PublicFunction pFun = null;
    private PublicAction pAct = null;
    private String printerName = "";
    private String strPort = "9100";
    private StringAlignUtils alignUtils = new StringAlignUtils(42, StringAlignUtils.Alignment.CENTER);
    List<KOTHeader> kotPrintList;
    private AppDataManager mAppManager;
    private POSDataSource mDBHelper;

    public KOTPrintTask(Context context) {
        try {
            thisCon = AndroidApplication.getAppContext();
            mAppManager = AndroidApplication.getInstance().getAppManager();
            mDBHelper = AndroidApplication.getInstance().getPOSDataSource();
            hprtPrinter = new HPRTPrinterHelper();
            pFun = new PublicFunction(thisCon);
            pAct = new PublicAction(thisCon);
            InitCombox();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            //get all kot from kotHeader if Printed status = N
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
                String ipAddress = terminals.getTerminalIP().trim();

                Log.i("IP Address", ipAddress);

                if (terminals.getIsPrinter().equalsIgnoreCase("Y")) {

                    if(Common.isIpAddress(ipAddress)) {
                        //get kotLine data from kotLineItems
                        try {
                            List<KOTLineItems> kotLineItem = mDBHelper.getKOTLineItem(kotPrintList.get(i).getKotNumber());

                            if (kotLineItem.size() != 0) {

                                AppLog.e("KOT SERVICE", "PRINTING SERVICE STARTED");

                                List<String> kotLines = new ArrayList<String>();

                                kotLines.add(printTextString(alignUtils.alignFormat(terminals.getTerminalName()) + "\n"));

                                String tableHeader = "Table# " + tableName + " | KOT# " + String.valueOf(kotPrintList.get(i).getKotNumber());
                                kotLines.add(printTextString(alignUtils.alignFormat(tableHeader) + "\n"));

                                if (invoiceNumber != 0) {
                                    kotLines.add(printTextString(alignUtils.alignFormat("Inv No: " + invoiceNumber) + "\n"));
                                }

                                /**FOR BRIYANI HOUSE*/
                                kotLines.add(printTextString("------------------------------------------\n"));
                                kotLines.add(printTextString("Order By: " + kotPrintList.get(i).getOrderBy() + "\n"));
                                kotLines.add(printTextString(Common.getDate() + "                      " + Common.getTime() + "\n"));
                                kotLines.add(printTextString("------------------------------------------\n"));

                                for (int j = 0; j < kotLineItem.size(); j++) {
                                    kotLines.add(printTextString(kotLineItem.get(j).getQty() + "   " + kotLineItem.get(j).getProduct().getProdName() + "\n"));
                                    if (kotLineItem.get(j).getNotes().trim().length() != 0)
                                        kotLines.add(printTextString("     " + kotLineItem.get(j).getNotes() + "\n"));
                                }

                                kotLines.add(printTextString("------------------------------------------\n"));
                                kotLines.add(printTextString(alignUtils.alignFormat("*** " + kotPrintList.get(i).getOrderType() + " ***") + "\n"));

                                if(kotLines.size()!=0){
                                    //call Wi-Fi print
                                    if (hprtPrinter != null) {
                                        HPRTPrinterHelper.PortClose();
                                    }

                                    hprtPrinter = new HPRTPrinterHelper(thisCon, printerName);
                                    if (HPRTPrinterHelper.PortOpen("WiFi," + ipAddress + "," + strPort) != 0) {
                                        //Printer Not connected
                                        AppLog.e("KOT SERVICE", "PRINTER NOT CONNECTED");
                                        Log.e("POSActivity-LAN PRINTER", "PRINTER NOT CONNECTED");
                                    } else {
                                        try {
                                            Log.i("POSActivity-LAN PRINTER", "PRINTER CONNECTED");

                                            AppLog.e("KOT SERVICE", "PRINTER CONNECTED");

                                            pAct.LanguageEncode();
                                            pAct.BeforePrintAction();

                                            for(int j=0;j<kotLines.size();j++) {
                                                System.out.println(kotLines.get(j));
                                                HPRTPrinterHelper.PrintText(kotLines.get(j));
                                            }

                                            HPRTPrinterHelper.CutPaper(HPRTPrinterHelper.HPRT_PARTIAL_CUT_FEED, 240);

                                            pAct.AfterPrintAction();

                                            //update local status to printed
                                            mDBHelper.updateKOTStatusPrinted(kotPrintList.get(i).getKotNumber(), invoiceNumber);

                                        } catch (Exception e) {

                                            StringWriter sw = new StringWriter();
                                            e.printStackTrace(new PrintWriter(sw));
                                            String stacktrace = sw.toString();

                                            AppLog.e("KOT SERVICE", stacktrace);

                                            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
                                        }
                                    }
                                }

                            } else {
                                AppLog.e("KOT SERVICE", "KOT LINE ITEM IS EMPTY");
                            }
                        }catch (Exception e){
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            String stacktrace = sw.toString();

                            AppLog.e("KOT SERVICE", stacktrace);
                        }
                    }
                    else{
                        AppLog.e("KOT SERVICE", "PRINTER IP ADDRESS NOT VALID");
                    }
                }else{
                    //update local status to printed
                    mDBHelper.updateKOTStatusPrinted(kotPrintList.get(i).getKotNumber(),invoiceNumber);
                }
            }

            //Get not invoiced tables
            List<KOTHeader> kotHeaderList = mDBHelper.getDataAvailableKOTTables();

            for (int i = 0; i < kotHeaderList.size(); i++) {
                //update orderAvailable = Y
                mDBHelper.updateOrderAvailableTable(kotHeaderList.get(i).getTablesId());
            }

            //String response = postPrintedKOT();
            //mParser.parseKOTStatus(response, null);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    @Override
    protected void onPostExecute(Boolean result) {
        AppConstants.isKOTParsing = false;
        OrderStatusListener.getInstance().kotPrinted();
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

    private String printTextString(String printText){
        String s = printText;
        return s;
    }
}

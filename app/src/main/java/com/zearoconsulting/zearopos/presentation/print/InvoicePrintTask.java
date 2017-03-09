package com.zearoconsulting.zearopos.presentation.print;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.sewoo.jpos.printer.LKPrint;
import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.data.DBHelper;
import com.zearoconsulting.zearopos.data.POSDataSource;
import com.zearoconsulting.zearopos.domain.parser.JSONParser;
import com.zearoconsulting.zearopos.presentation.model.KOTHeader;
import com.zearoconsulting.zearopos.presentation.model.Organization;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.POSPayment;
import com.zearoconsulting.zearopos.presentation.model.Tables;
import com.zearoconsulting.zearopos.presentation.model.Terminals;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.presentation.presenter.IPrintingListeners;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;
import com.zearoconsulting.zearopos.utils.StringAlignUtils;

import java.util.List;

import HPRTAndroidSDK.HPRTPrinterHelper;
import HPRTAndroidSDK.PublicFunction;

/**
 * Created by saravanan on 29-12-2016.
 */

public class InvoicePrintTask extends AsyncTask<WiFiPrintTaskParams, String, String> {

    private Context mContext;

    //Variables for Lan/Wi-Fi Printer
    private HPRTPrinterHelper hprtPrinter;
    private String ConnectType = "";
    private String printerName = "";
    private String strPort = "9100";
    private Context thisCon = AndroidApplication.getAppContext();;
    private ArrayAdapter arrPrinterList;
    private PublicFunction pFun = null;
    private PublicAction pAct = null;

    private AppDataManager mAppManager;
    private POSDataSource mDBHelper;
    private JSONParser mParser;
    private StringAlignUtils alignUtils = new StringAlignUtils(48, StringAlignUtils.Alignment.CENTER);
    private IPrintingListeners mCallback;
    private ProgressDialog mProgress;
    private String mCashierName;

    /**Variables for print invoice amount details*/
    long mPosNumber;
    double mTotalAmount;
    double mFinalAmount;
    double mPaidAmount;
    double mReturnAmount;

    /**Variables for print product details */
    String qty;
    String name;
    String arabicName;
    double price;
    double discPrice = 0;
    String discount = "";
    int disc;
    double discVal;

    /**Variables for print payment details*/
    POSPayment payment;
    double mPaidCashAmt = 0;
    double mPaidAmexAmt = 0;
    double mPaidGiftAmt = 0;
    double mPaidMasterAmt = 0;
    double mPaidVisaAmt = 0;
    double mOtherAmt = 0;
    double mAmountEntered = 0;

    //add printer list
    private void InitCombox() {
        try {
            arrPrinterList = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item);
            String strSDKType = thisCon.getString(R.string.sdk_type);
            if (strSDKType.equals("all"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_all, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("hprt"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_hprt, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("mkt"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_mkt, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("mprint"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_mprint, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("sycrown"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_sycrown, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("mgpos"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_mgpos, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("ds"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_ds, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("cst"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_cst, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("other"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_other, android.R.layout.simple_spinner_item);
            arrPrinterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            printerName = arrPrinterList.getItem(0).toString();
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> InitCombox ")).append(e.getMessage()).toString());
        }
    }


    // Three Constructors
    public InvoicePrintTask(Context context) {
        mContext = context;
        this.mCallback = (IPrintingListeners) context;
        try {
            hprtPrinter = new HPRTPrinterHelper();
            pFun = new PublicFunction(thisCon);
            pAct = new PublicAction(thisCon);
            InitCombox();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAppManager = AndroidApplication.getInstance().getAppManager();
        mDBHelper = AndroidApplication.getInstance().getPOSDataSource();
        mParser = new JSONParser(AndroidApplication.getAppContext(), mAppManager, mDBHelper);

    }


    @Override
    public void onPreExecute() {
        mProgress = new ProgressDialog(mContext);
        mProgress.setMessage("Printing bill please wait...");
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        //mProgress.setMessage(values[0]);
    }

    @Override
    protected String doInBackground(WiFiPrintTaskParams... params) {

        String result = "";
        try {

            this.mCashierName = mAppManager.getUserName();

            this.mPosNumber = params[0].posId;
            this.mTotalAmount = params[0].mTotalAmt;
            this.mFinalAmount = params[0].mFinalAmt;
            this.mPaidAmount = params[0].mPaidAmt;
            this.mReturnAmount = params[0].mReturnAmt;

            this.mPaidCashAmt = params[0].mPaidCashAmt;
            this.mPaidAmexAmt = params[0].mPaidAmexAmt;
            this.mPaidGiftAmt = params[0].mPaidGiftAmt;
            this.mPaidMasterAmt = params[0].mPaidMasterAmt;
            this.mPaidVisaAmt = params[0].mPaidVisaAmt;
            this.mOtherAmt = params[0].mPaidOtherAmt;
            this.mAmountEntered = params[0].mAmountEntered;

            payment = mDBHelper.getPaymentDetails(mPosNumber);
            if(payment!=null) {
                this.mPaidCashAmt = payment.getCash();
                this.mPaidAmexAmt = payment.getAmex();
                this.mPaidGiftAmt = payment.getGift();
                this.mPaidMasterAmt = payment.getMaster();
                this.mPaidVisaAmt = payment.getVisa();
                this.mOtherAmt = payment.getOther();
            }

            try {

                Terminals terminals = mDBHelper.getInvoiceTerminalData(mAppManager.getClientID(), mAppManager.getOrgID());

                if(terminals==null)
                    return  "error";

                //getting ipAddress
                String ipAddress = terminals.getTerminalIP();

                if (Common.isIpAddress(ipAddress)) {

                    //call Wi-Fi print
                    if (hprtPrinter != null) {
                        HPRTPrinterHelper.PortClose();
                    } else {
                        result = "error";
                    }

                    hprtPrinter = new HPRTPrinterHelper(thisCon, printerName);
                    if (HPRTPrinterHelper.PortOpen("WiFi," + ipAddress + "," + strPort) != 0) {
                        //Printer Not connected
                        Log.e("POSActivity-LAN PRINTER", "PRINTER NOT CONNECTED");
                        result = "error";
                    } else {
                        try {
                            Log.i("POSActivity-LAN PRINTER", "PRINTER CONNECTED");

                            pAct.LanguageEncode();
                            //pAct.BeforePrintAction();

                            Organization orgDetail = mDBHelper.getOrganizationDetail(mAppManager.getOrgID());

                            List<Long> kotTableList = mDBHelper.getKOTTableList(mPosNumber);
                            String tableName;
                            int covers = 0;

                            if (kotTableList.size() == 0) {
                                tableName = "CounterSale";
                            } else if (kotTableList.size() == 1 && kotTableList.get(0) == 0) {
                                tableName = "CounterSale";
                            } else {
                                StringBuilder sb = new StringBuilder("");
                                for (int i = 0; i < kotTableList.size(); i++) {
                                    Tables table = mDBHelper.getTableData(mAppManager.getClientID(), mAppManager.getOrgID(), kotTableList.get(i));
                                    sb.append(table.getTableName() + " ");

                                    List<KOTHeader> kotHeaderList = mDBHelper.getKOTHeaders(kotTableList.get(i), true);
                                    for (int j = 0; j < kotHeaderList.size(); j++) {
                                        KOTHeader kotHeader = kotHeaderList.get(j);
                                        covers = covers + kotHeader.getCoversCount();
                                    }

                                }
                                tableName = sb.toString();
                            }

                            if(orgDetail!=null) {
                                HPRTPrinterHelper.PrintText(alignUtils.alignFormat(orgDetail.getOrgName()) + "\n", 0, 16, 0);
                                HPRTPrinterHelper.PrintText(alignUtils.alignFormat(orgDetail.getOrgAddress()) + "\n", 0, 16, 0);
                                HPRTPrinterHelper.PrintText(alignUtils.alignFormat("Tel: " + orgDetail.getOrgPhone()) + "\n", 0, 16, 0);
                            }
                            HPRTPrinterHelper.PrintText(Common.getDate() + "                            " + Common.getTime() + "\n", 0, 0, 0);
                            HPRTPrinterHelper.PrintText(alignUtils.alignFormat(String.valueOf(AppConstants.posID)) + "\n", 0, 16, 0);
                            HPRTPrinterHelper.PrintText("Cashier: "+mCashierName+"\n", 0, 0, 0);
                            HPRTPrinterHelper.PrintText("------------------------------------------------\n", 0, 0, 0);

                            if(!tableName.equalsIgnoreCase("CounterSale")){
                                String tableHeader = "Table# " + tableName + " | COVERS# " + covers;
                                HPRTPrinterHelper.PrintText(alignUtils.alignFormat(tableHeader) + "\n", 0, 16, 0);
                                HPRTPrinterHelper.PrintText("------------------------------------------------\n", 0, 0, 0);
                            }

                            HPRTPrinterHelper.PrintText(addQtyWhiteSpace("Qty")+addItemNameWhiteSpace("Item")+addPriceWhiteSpace("Price")+"\n", 0, 0, 0);


                            List<POSLineItem> lineItem = mDBHelper.getPOSLineItems(mPosNumber,0);
                            for (int j = 0; j < lineItem.size(); j++) {

                                qty = String.valueOf(lineItem.get(j).getPosQty());
                                name = lineItem.get(j).getProductName();
                                arabicName = lineItem.get(j).getProdArabicName();
                                price = lineItem.get(j).getPosQty()*lineItem.get(j).getStdPrice();
                                discPrice = 0;
                                discount = "";
                                disc=lineItem.get(j).getDiscType();
                                discVal=lineItem.get(j).getDiscValue();

                                if(disc==0 && discVal!= 0){
                                    discount = discVal+"% discount";
                                    discPrice = (price * discVal / 100);
                                }else if(disc == 1 && discVal!=0){
                                    discount = discVal+" QR discount";
                                    discPrice = discVal;
                                }

                                //String discount = lineItem.get(i).getDiscValue();
                                if(name.length()>35){
                                    //splitstring and print
                                    int len = name.length();
                                    HPRTPrinterHelper.PrintText(addQtyWhiteSpace(qty)+addItemNameWhiteSpace(name.substring(0, 34))+addPriceWhiteSpace(Common.valueFormatter(price))+"\n", 0, 0, 0);
                                    HPRTPrinterHelper.PrintText(addQtyWhiteSpace(" ")+addItemNameWhiteSpace(name.substring(35, len-1))+addPriceWhiteSpace(" ")+"\n", 0, 0, 0);
                                }else{
                                    HPRTPrinterHelper.PrintText(addQtyWhiteSpace(qty)+addItemNameWhiteSpace(name)+addPriceWhiteSpace(Common.valueFormatter(price))+"\n", 0, 0, 0);
                                }

                                if(!discount.equalsIgnoreCase(""))
                                    HPRTPrinterHelper.PrintText(addQtyWhiteSpace(" ")+addItemNameWhiteSpace(discount)+addPriceWhiteSpace("-"+Common.valueFormatter(discPrice))+"\n", 0, 0, 0);

                                //if (lineItem.get(j).getNotes().trim().length() != 0)
                                    //HPRTPrinterHelper.PrintText("     " + lineItem.get(j).getNotes() + "\n", 0, 0, 0);

                                List<POSLineItem> extraProductList = mDBHelper.getPOSExtraLineItems(lineItem.get(j).getKotLineId());
                                if (extraProductList.size() != 0){
                                    for (int k = 0; k < extraProductList.size(); k++) {

                                        qty = String.valueOf(extraProductList.get(k).getPosQty());
                                        name = extraProductList.get(k).getProductName();
                                        arabicName = extraProductList.get(k).getProdArabicName();
                                        price = extraProductList.get(k).getPosQty()*extraProductList.get(k).getStdPrice();
                                        discPrice = 0;
                                        discount = "";
                                        disc=extraProductList.get(k).getDiscType();
                                        discVal=extraProductList.get(k).getDiscValue();

                                        if(disc==0 && discVal!= 0){
                                            discount = discVal+"% discount";
                                            discPrice = (price * discVal / 100);
                                        }else if(disc == 1 && discVal!=0){
                                            discount = discVal+" QR discount";
                                            discPrice = discVal;
                                        }

                                        //String discount = lineItem.get(i).getDiscValue();
                                        if(name.length()>35){
                                            //splitstring and print
                                            int len = name.length();
                                            HPRTPrinterHelper.PrintText(addQtyWhiteSpace(" ")+addItemNameWhiteSpace("  "+name.substring(0, 34))+addPriceWhiteSpace(Common.valueFormatter(price))+"\n", 0, 0, 0);
                                            HPRTPrinterHelper.PrintText(addQtyWhiteSpace(" ")+addItemNameWhiteSpace("  "+name.substring(35, len-1))+addPriceWhiteSpace(" ")+"\n", 0, 0, 0);
                                        }else{
                                            HPRTPrinterHelper.PrintText(addQtyWhiteSpace(" ")+addItemNameWhiteSpace("  "+name)+addPriceWhiteSpace(Common.valueFormatter(price))+"\n", 0, 0, 0);
                                        }

                                        if(!discount.equalsIgnoreCase(""))
                                            HPRTPrinterHelper.PrintText(addQtyWhiteSpace(" ")+addItemNameWhiteSpace(discount)+addPriceWhiteSpace("-"+Common.valueFormatter(discPrice))+"\n", 0, 0, 0);

                                    }
                                }
                            }

                            HPRTPrinterHelper.PrintText("------------------------------------------------\n", 0, 0, 0);

                            if(mTotalAmount!=mFinalAmount) {
                                HPRTPrinterHelper.PrintText(addTotalWhiteSpace("Total") + "" + addPriceWhiteSpace(Common.valueFormatter(mTotalAmount)) + "\n", 0, 0, 0);
                                HPRTPrinterHelper.PrintText(addTotalWhiteSpace("Discount QR ") + "" + addPriceWhiteSpace("-" + Common.valueFormatter(mTotalAmount-mFinalAmount)) + "\n", 0, 0, 0);
                                HPRTPrinterHelper.PrintText(addTotalWhiteSpace("Net Total")+""+addPriceWhiteSpace(Common.valueFormatter(mFinalAmount))+"\n", 0, 0, 0);
                            }else{
                                HPRTPrinterHelper.PrintText(addTotalWhiteSpace("Total") + "" + addPriceWhiteSpace(Common.valueFormatter(mTotalAmount)) + "\n", 0, 0, 0);
                            }

                            HPRTPrinterHelper.PrintText("------------------------------------------------\n", 0, 0, 0);

                            if(mPaidCashAmt!=0)
                                HPRTPrinterHelper.PrintText(addTotalWhiteSpace("Cash")+""+addPriceWhiteSpace(Common.valueFormatter(mPaidCashAmt))+"\n", 0, 0, 0);

                            if(mPaidAmexAmt!=0)
                                HPRTPrinterHelper.PrintText(addTotalWhiteSpace("Amex Card")+""+addPriceWhiteSpace(Common.valueFormatter(mPaidAmexAmt))+"\n", 0, 0, 0);

                            if(mPaidGiftAmt!=0)
                                HPRTPrinterHelper.PrintText(addTotalWhiteSpace("Gift Card")+""+addPriceWhiteSpace(Common.valueFormatter(mPaidGiftAmt))+"\n", 0, 0, 0);

                            if(mPaidMasterAmt!=0)
                                HPRTPrinterHelper.PrintText(addTotalWhiteSpace("Master Card")+""+addPriceWhiteSpace(Common.valueFormatter(mPaidMasterAmt))+"\n", 0, 0, 0);

                            if(mPaidVisaAmt!=0)
                                HPRTPrinterHelper.PrintText(addTotalWhiteSpace("VISA Card")+""+addPriceWhiteSpace(Common.valueFormatter(mPaidVisaAmt))+"\n", 0, 0, 0);

                            if(mOtherAmt!=0)
                                HPRTPrinterHelper.PrintText(addTotalWhiteSpace("Other")+""+addPriceWhiteSpace(Common.valueFormatter(mOtherAmt))+"\n", 0, 0, 0);

                            if(mPaidCashAmt!=0 || mPaidAmexAmt!=0 || mPaidGiftAmt!=0 || mPaidMasterAmt!=0|| mPaidVisaAmt!=0 || mOtherAmt!=0 )
                                HPRTPrinterHelper.PrintText("------------------------------------------------\n", 0, 0, 0);

                            if(mReturnAmount!=0) {
                                HPRTPrinterHelper.PrintText(addTotalWhiteSpace("Paid Amount") + "" + addPriceWhiteSpace(Common.valueFormatter(mAmountEntered)) + "\n", 0, 0, 0);
                                HPRTPrinterHelper.PrintText(addTotalWhiteSpace("Change") + "" + addPriceWhiteSpace(Common.valueFormatter(mReturnAmount)) + "\n", 0, 0, 0);
                                HPRTPrinterHelper.PrintText("------------------------------------------------\n", 0, 0, 0);
                            }

                            HPRTPrinterHelper.PrintText(alignUtils.alignFormat("Thank you for choosing "+orgDetail.getOrgName()) + "\n", 0, 0, 0);
                            HPRTPrinterHelper.PrintText(alignUtils.alignFormat(orgDetail.getOrgWebUrl()) + "\n", 0, 0, 0);

                            pAct.AfterPrintAction();

                            HPRTPrinterHelper.CutPaper(HPRTPrinterHelper.HPRT_PARTIAL_CUT_FEED, 240);

                            result = "success";
                        } catch (Exception e) {
                            result = "error";
                            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
                        }
                    }
                }
            } catch (Exception e) {
                result = "error";
                e.printStackTrace();
            }
        } catch (Exception e) {
            result = "error";
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String results) {
        mProgress.dismiss();

        if(results.equalsIgnoreCase("success")){
            //This is where you return data back to caller
            mCallback.onPrintTaskComplete(this.mPosNumber);
        }else{
            //This is where you return data back to caller
            mCallback.onPrintTaskError(results, this.mPosNumber);
        }

    }

    private String addTotalWhiteSpace(String data){
        String result = String.format("%-40s", data);
        return result;
    }

    private String addQtyWhiteSpace(String data){
        String result = String.format("%-5s", data);
        return result;
    }

    private String addItemNameWhiteSpace(String data){
        String result = String.format("%-35s", data);
        return result;
    }

    private String addPriceWhiteSpace(String data){
        String result = String.format("%8s", data);
        return result;
    }
}

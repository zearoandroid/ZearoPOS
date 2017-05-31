package com.zearoconsulting.zearopos.presentation.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;
import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.data.POSDataSource;
import com.zearoconsulting.zearopos.presentation.model.KOTHeader;
import com.zearoconsulting.zearopos.presentation.model.Organization;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.POSPayment;
import com.zearoconsulting.zearopos.presentation.model.Tables;
import com.zearoconsulting.zearopos.utils.Common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by saravanan on 26-08-2016.
 */
public class InvoiceBill {

    // 0x1B
    private final char ESC = ESCPOS.ESC;
    private ESCPOSPrinter posPtr;
    private Context mContext;
    private long posId = 0;
    private List<POSLineItem> lineItem;
    private double mTotalAmt, mTotalFinalAmt, mPaidAmt, mReturnAmt;
    private int rtn;
    private int complement;
    private String mCashierName;
    public AppDataManager mAppManager;
    public POSDataSource mDBHelper;

    double mPaidCashAmt = 0;
    double mPaidAmexAmt = 0;
    double mPaidGiftAmt = 0;
    double mPaidMasterAmt = 0;
    double mPaidVisaAmt = 0;
    double mOtherAmt = 0;

    /**Variables for print product details */
    String qty;
    String name;
    String arabicName;
    double price;
    double discPrice = 0;
    String discount = "";
    int disc;
    double discVal;

    private Organization mOrgDetails = null;

    public InvoiceBill(Context context, long posid, List<POSLineItem> lineItems, double totalAmount, double finalAmount, double paidAmt, double returnAmt) {
        this.mContext = context;
        this.posId = posid;
        this.lineItem = lineItems;
        this.mTotalAmt = totalAmount;
        this.mTotalFinalAmt = finalAmount;
        this.mPaidAmt = paidAmt;
        this.mReturnAmt = returnAmt;

        posPtr = new ESCPOSPrinter();
        mAppManager = AndroidApplication.getInstance().getAppManager();
        mDBHelper = AndroidApplication.getInstance().getPOSDataSource();
        this.mCashierName = mAppManager.getUserName();
    }

    public void setAmountDetails(POSPayment payment){

        this.mPaidCashAmt = payment.getCash();
        this.mPaidAmexAmt = payment.getAmex();
        this.mPaidGiftAmt = payment.getGift();
        this.mPaidMasterAmt = payment.getMaster();
        this.mPaidVisaAmt = payment.getVisa();
        this.mOtherAmt = payment.getOther();
        this.mReturnAmt = payment.getChange();
        this.complement = payment.getIsComplement();
    }

    public void setOrgDetails(Organization orgDetails){
        this.mOrgDetails = orgDetails;
    }

    private void updateProductPrices(){
        for (int i = 0; i < lineItem.size(); i++) {
            List<POSLineItem> extraLineItems = mDBHelper.getPOSExtraLineItems(lineItem.get(i).getKotLineId());
            if(extraLineItems.size()!=0){

                double price = 0;
                double discVal = 0;
                double stdPrice = 0;

                for(int j=0; j<extraLineItems.size(); j++){
                    price = price + extraLineItems.get(j).getTotalPrice();
                    stdPrice = stdPrice + extraLineItems.get(j).getStdPrice();
                    if(lineItem.get(i).getDiscType()==1)
                        discVal = discVal+extraLineItems.get(j).getDiscValue();
                }

                lineItem.get(i).setStdPrice(lineItem.get(i).getStdPrice()+stdPrice);
                lineItem.get(i).setDiscValue(lineItem.get(i).getDiscValue()+discVal);
                lineItem.get(i).setTotalPrice(lineItem.get(i).getTotalPrice()+price);
            }

        }
    }

    public int billGenerateAndPrint() throws InterruptedException {
        try {
            // posPtr = new ESCPOSPrinter();
            posPtr.asbOff();
            rtn = posPtr.printerSts();
            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if (rtn != 0) return rtn;
        } catch (IOException e) {
            e.printStackTrace();
            return rtn;
        }

        try {

            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cclogo);
            File sd = new File(Environment.getExternalStorageDirectory() + "//temp");
            String fileName = "orglogo.jpg";
            File dest = new File(sd, fileName);
            try {
                FileOutputStream out;
                out = new FileOutputStream(dest);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = df.format(new Date());
            strDate = String.format("%-36s", strDate);

            DateFormat tf = new SimpleDateFormat("hh:mm a");
            String strTime = tf.format(new Date());

            //String orgArabicName = "شوكولا كافيه لاونج";

            List<Long> kotTableList = mDBHelper.getKOTTableList(posId);
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

                    covers = 0;

                    List<KOTHeader> kotHeaderList = mDBHelper.getKOTHeaders(kotTableList.get(i), true);
                    for (int j = 0; j < kotHeaderList.size(); j++) {
                        KOTHeader kotHeader = kotHeaderList.get(j);
                        covers = covers + kotHeader.getCoversCount();
                    }

                }
                tableName = sb.toString();
            }

            if(mOrgDetails!=null){
                if(!mOrgDetails.getOrgImage().equals("")){
                    //posPtr.printBitmap("//sdcard//temp//orglogo.jpg", LKPrint.LK_ALIGNMENT_CENTER);
                    posPtr.printBitmap(mOrgDetails.getOrgImage(), LKPrint.LK_ALIGNMENT_CENTER);
                    posPtr.printNormal("\n");
                }

                posPtr.printAndroidFont(mOrgDetails.getOrgArabicName(), 532, 50, ESCPOSConst.LK_ALIGNMENT_CENTER);
                posPtr.printText(mOrgDetails.getOrgName()+"\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);
                posPtr.printNormal(ESC + "|cA"+mOrgDetails.getOrgAddress()+"\n");
                posPtr.printNormal(ESC + "|cA"+mOrgDetails.getOrgPhone()+"\n");
            }
            posPtr.printNormal(strDate+""+strTime+"\n");
            posPtr.printNormal(ESC + "|cA"+posId+"\n");
            posPtr.printNormal(ESC + "|lACashier: "+mCashierName+"\n");
            posPtr.printNormal("------------------------------------------------\n");

            String tableHeader = "Table# " + tableName + " | COVERS# " + covers;
            posPtr.printText(tableHeader+"\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);
            posPtr.printNormal("------------------------------------------------\n");

            posPtr.printNormal(addQtyWhiteSpace("Qty")+addItemNameWhiteSpace("Item")+addPriceWhiteSpace("Price")+"\n");
            posPtr.printNormal("------------------------------------------------\n");

            for(int i =0; i<lineItem.size();i++){
                qty = String.valueOf(lineItem.get(i).getPosQty());
                name = lineItem.get(i).getProductName();
                arabicName = lineItem.get(i).getProdArabicName();
                price = lineItem.get(i).getPosQty()*lineItem.get(i).getStdPrice();
                discPrice = 0;
                discount = "";
                disc=lineItem.get(i).getDiscType();
                discVal=lineItem.get(i).getDiscValue();

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
                    posPtr.printNormal(addQtyWhiteSpace(qty)+addItemNameWhiteSpace(name.substring(0, 34))+addPriceWhiteSpace(Common.valueFormatter(price))+"\n");
                    posPtr.printNormal(addQtyWhiteSpace(" ")+addItemNameWhiteSpace(name.substring(35, len-1))+addPriceWhiteSpace(" ")+"\n");
                }else{
                    posPtr.printNormal(addQtyWhiteSpace(qty)+addItemNameWhiteSpace(name)+addPriceWhiteSpace(Common.valueFormatter(price))+"\n");
                }

                if(!discount.equalsIgnoreCase(""))
                    posPtr.printNormal(addQtyWhiteSpace(" ")+addItemNameWhiteSpace(discount)+addPriceWhiteSpace("-"+Common.valueFormatter(discPrice))+"\n");

                if(!arabicName.equalsIgnoreCase(""))
                    posPtr.printAndroidFont(arabicName, 712, 24, ESCPOSConst.LK_ALIGNMENT_RIGHT);

                List<POSLineItem> extraProductList = mDBHelper.getPOSExtraLineItems(lineItem.get(i).getKotLineId());
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
                            posPtr.printNormal(addQtyWhiteSpace(" ")+addItemNameWhiteSpace("---"+name.substring(0, 34))+addPriceWhiteSpace(Common.valueFormatter(price))+"\n");
                            posPtr.printNormal(addQtyWhiteSpace(" ")+addItemNameWhiteSpace("   "+name.substring(35, len-1))+addPriceWhiteSpace(" ")+"\n");
                        }else{
                            posPtr.printNormal(addQtyWhiteSpace(qty)+addItemNameWhiteSpace("---"+name)+addPriceWhiteSpace(Common.valueFormatter(price))+"\n");
                        }

                        if(!discount.equalsIgnoreCase(""))
                            posPtr.printNormal(addQtyWhiteSpace(" ")+addItemNameWhiteSpace(discount)+addPriceWhiteSpace("-"+Common.valueFormatter(discPrice))+"\n");

                        if(!arabicName.equalsIgnoreCase(""))
                            posPtr.printAndroidFont(arabicName, 712, 24, ESCPOSConst.LK_ALIGNMENT_RIGHT);

                    }
                }

            }

            posPtr.printNormal("------------------------------------------------\n");
            //posPtr.printNormal(addTotalWhiteSpace("Total")+""+addPriceWhiteSpace(Common.valueFormatter(totalAmount))+"\n");

            if(complement == 0){
                if(mTotalAmt!=mTotalFinalAmt) {
                    posPtr.printText(addTotalWhiteSpace("Total") + "" + addPriceWhiteSpace(Common.valueFormatter(mTotalAmt)) + "\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);
                    posPtr.printText(addTotalWhiteSpace("Discount QR ") + "" + addPriceWhiteSpace("-" + Common.valueFormatter(mTotalAmt-mTotalFinalAmt)) + "\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);
                    posPtr.printText(addTotalWhiteSpace("Net Total")+""+addPriceWhiteSpace(Common.valueFormatter(mTotalFinalAmt))+"\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);
                }else{
                    posPtr.printText(addTotalWhiteSpace("Total") + "" + addPriceWhiteSpace(Common.valueFormatter(mTotalAmt)) + "\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);
                }
            }else{
                posPtr.printText(addTotalWhiteSpace("Total") + "" + addPriceWhiteSpace(Common.valueFormatter(mTotalAmt)) + "\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);
                posPtr.printNormal("------------------------------------------------\n");
                posPtr.printNormal("--------------COMPLEMENTARY ORDER---------------\n");
            }

            posPtr.printNormal("------------------------------------------------\n");

            if(mPaidCashAmt!=0 && complement == 0){
                if(mReturnAmt!=0) {
                    posPtr.printText(addTotalWhiteSpace("Cash") + "" + addPriceWhiteSpace(Common.valueFormatter(mTotalAmt-(mReturnAmt))) + "\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);
                }else{
                    posPtr.printText(addTotalWhiteSpace("Cash")+""+addPriceWhiteSpace(Common.valueFormatter(mPaidCashAmt))+"\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);
                }
            }

            if(mPaidAmexAmt!=0)
                posPtr.printText(addTotalWhiteSpace("Amex Card")+""+addPriceWhiteSpace(Common.valueFormatter(mPaidAmexAmt))+"\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);

            if(mPaidGiftAmt!=0)
                posPtr.printText(addTotalWhiteSpace("Gift Card")+""+addPriceWhiteSpace(Common.valueFormatter(mPaidGiftAmt))+"\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);

            if(mPaidMasterAmt!=0)
                posPtr.printText(addTotalWhiteSpace("Master Card")+""+addPriceWhiteSpace(Common.valueFormatter(mPaidMasterAmt))+"\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);

            if(mPaidVisaAmt!=0)
                posPtr.printText(addTotalWhiteSpace("VISA Card")+""+addPriceWhiteSpace(Common.valueFormatter(mPaidVisaAmt))+"\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);

            if(mOtherAmt!=0)
                posPtr.printText(addTotalWhiteSpace("Other")+""+addPriceWhiteSpace(Common.valueFormatter(mOtherAmt))+"\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);

            if(mPaidCashAmt!=0 || mPaidAmexAmt!=0 || mPaidGiftAmt!=0 || mPaidMasterAmt!=0|| mPaidVisaAmt!=0 || mOtherAmt!=0 )
                posPtr.printNormal("------------------------------------------------\n");

            if(mReturnAmt!=0) {
                posPtr.printText(addTotalWhiteSpace("Paid") + "" + addPriceWhiteSpace(Common.valueFormatter(mTotalAmt-(mReturnAmt))) + "\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);
                posPtr.printText(addTotalWhiteSpace("Change") + "" + addPriceWhiteSpace(Common.valueFormatter(mReturnAmt)) + "\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_BOLD, LKPrint.LK_TXT_1WIDTH);
                posPtr.printNormal("------------------------------------------------\n");
            }

            posPtr.printNormal(ESC + "|cAThank you for choosing "+mOrgDetails.getOrgName()+"\n");
            posPtr.printNormal(ESC + "|cA"+mOrgDetails.getOrgWebUrl()+"\n");

            posPtr.lineFeed(4);
            posPtr.cutPaper();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return 0;
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

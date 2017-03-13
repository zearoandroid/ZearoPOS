package com.zearoconsulting.zearopos.domain.parser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.data.DBHelper;
import com.zearoconsulting.zearopos.data.POSDataSource;
import com.zearoconsulting.zearopos.presentation.model.BPartner;
import com.zearoconsulting.zearopos.presentation.model.Category;
import com.zearoconsulting.zearopos.presentation.model.Customer;
import com.zearoconsulting.zearopos.presentation.model.KOTHeader;
import com.zearoconsulting.zearopos.presentation.model.KOTLineItems;
import com.zearoconsulting.zearopos.presentation.model.Organization;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.Product;
import com.zearoconsulting.zearopos.presentation.model.Tables;
import com.zearoconsulting.zearopos.presentation.model.Terminals;
import com.zearoconsulting.zearopos.presentation.presenter.ITokenListeners;
import com.zearoconsulting.zearopos.presentation.presenter.ParsingStatusListener;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;
import com.zearoconsulting.zearopos.utils.FileUtils;
import com.zearoconsulting.zearopos.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by saravanan on 25-05-2016.
 */
public class JSONParser {

    private AppDataManager mAppManager;
    private POSDataSource mDBHelper;
    private Context mContext;
    private Bundle b = new Bundle();
    private static ITokenListeners mTokenListener = null;
    private static boolean mParsingState = false;

    /**
     * @param context
     * @param appDataManager
     * @param dbHelper
     */
    public JSONParser(Context context, AppDataManager appDataManager, POSDataSource dbHelper) {

        this.mContext = context;
        this.mAppManager = appDataManager;
        this.mDBHelper = dbHelper;
    }

    public void setTokeListener(ITokenListeners paramTokenListener)
    {
        this.mTokenListener = paramTokenListener;
    }

    /**
     * @param methodType
     * @return mJsonObj
     */
    public JSONObject getParams(int methodType) {

        JSONObject mJsonObj = new JSONObject();

        try {
            mJsonObj.put("macAddress", Common.getMacAddr());
            mJsonObj.put("username", mAppManager.getUserName());
            mJsonObj.put("password", mAppManager.getUserPassword());
            mJsonObj.put("userId", mAppManager.getUserID());
            mJsonObj.put("clientId", mAppManager.getClientID());
            mJsonObj.put("roleId", mAppManager.getRoleID());
            mJsonObj.put("orgId", mAppManager.getOrgID());
            mJsonObj.put("warehouseId", mAppManager.getWarehouseID());
            mJsonObj.put("businessPartnerId", mAppManager.getUserBPID());
            mJsonObj.put("sessionId", mAppManager.getSessionId());
            mJsonObj.put("version", 1.0);
            mJsonObj.put("appName", "POS");

            switch (methodType) {
                case AppConstants.GET_ORGANIZATION_DATA:
                    mJsonObj.put("operation", "POSOrganization");
                    break;
                case AppConstants.CALL_AUTHENTICATE:
                    mJsonObj.put("operation", "POSLogin");
                    break;
                case AppConstants.GET_CASH_CUSTOMER_DATA:
                    mJsonObj.put("operation", "POSCashCustomer");
                    break;
                case AppConstants.GET_POS_NUMBER:
                    mJsonObj.put("operation", "POSOrderNumber");
                    break;
                case AppConstants.GET_CATEGORY:
                    mJsonObj.put("operation", "POSCategory");
                    break;
                case AppConstants.GET_PRODUCTS:
                    mJsonObj.put("operation", "POSProducts");
                    break;
                case AppConstants.GET_ALL_PRODUCTS:
                    mJsonObj.put("operation", "POSAllProducts");
                    break;
                case AppConstants.GET_PRODUCT_PRICE:
                    mJsonObj.put("operation", "POSProductPrice");
                    break;
                case AppConstants.GET_TABLES:
                    mJsonObj.put("operation", "POSTables");
                    break;
                case AppConstants.GET_TERMINALS:
                    mJsonObj.put("operation", "POSTerminals");
                    break;
                case AppConstants.GET_KOT_HEADER_AND_lINES:
                    mJsonObj.put("operation", "GetKotDetails");
                    break;
                case AppConstants.POST_KOT_FLAGS:
                    mJsonObj.put("operation", "UpdateKOTFlags");
                    break;
                case AppConstants.CALL_RELEASE_POS_ORDER:
                    mJsonObj.put("operation", "POSReleaseOrder");
                    break;
                case AppConstants.GET_BPARTNERS:
                    mJsonObj.put("operation", "POSCustomers");
                    break;
                case AppConstants.POST_KOT_DATA:
                    mJsonObj.put("operation", "KOTData");
                    mJsonObj.put("currencyId", mAppManager.getCurrencyID());
                    mJsonObj.put("paymentTermId", mAppManager.getPaymentTermID());
                    mJsonObj.put("pricelistId", mAppManager.getPriceListID());
                    break;
                case AppConstants.CREATE_SESSION_REQUEST:
                    mJsonObj.put("operation", "createSession");
                    break;
                case AppConstants.RESUME_SESSION_REQUEST:
                    mJsonObj.put("operation", "resumeSession");
                    break;
                case AppConstants.CLOSE_SESSION_REQUEST:
                    mJsonObj.put("operation", "closeSession");
                    break;
                case AppConstants.CALL_CANCEL_POS_ORDER:
                    mJsonObj.put("operation", "POSCancelOrder");
                    break;
                case AppConstants.CALL_DRAFT_POS_ORDER:
                    mJsonObj.put("operation","POSDraftOrder");
                    break;
                case AppConstants.POST_TABLE_CHANGE:
                    mJsonObj.put("operation", "ChangeActiveTable");
                    break;
                case AppConstants.CHECK_CREDIT_LIMIT:
                    mJsonObj.put("operation", "CheckCreditLimit");
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mJsonObj;
    }

    /**
     * @param posId
     * @param customer
     * @param totalLine
     * @param totalAmount
     * @param paidAmount
     * @param dueAmount
     * @return mHeaderObj
     */
    public JSONObject getHeaderObj(long posId, Customer customer, int totalLine, double totalAmount, double paidAmount, double dueAmount, double cashAmount, double cardAmount) {

        JSONObject mHeaderObj = new JSONObject();

        try {

            mHeaderObj.put("posId", posId);
            mHeaderObj.put("clientId", mAppManager.getClientID());
            mHeaderObj.put("orgId", mAppManager.getOrgID());
            mHeaderObj.put("userId", mAppManager.getUserID());
            mHeaderObj.put("businessPartnerId", customer.getBpId());
            mHeaderObj.put("periodId", mAppManager.getPeriodID());
            mHeaderObj.put("accountSchemaId", mAppManager.getAcctSchemaID());
            mHeaderObj.put("adTableId", mAppManager.getAdTableID());
            mHeaderObj.put("totalLines", totalLine);
            mHeaderObj.put("totalAmount", totalAmount);
            mHeaderObj.put("currencyId", mAppManager.getCurrencyID());
            mHeaderObj.put("paymentTermId", mAppManager.getPaymentTermID());
            mHeaderObj.put("warehouseId", mAppManager.getWarehouseID());
            mHeaderObj.put("pricelistId", mAppManager.getPriceListID());
            mHeaderObj.put("cashbookId", mAppManager.getCashbookID());
            mHeaderObj.put("paidAmount", paidAmount);
            mHeaderObj.put("dueAmount", 0);
            mHeaderObj.put("customerName", customer.getCustomerName());
            mHeaderObj.put("cashAmount", cashAmount);
            if (cashAmount == 0)
                mHeaderObj.put("IsCash", "N");
            else
                mHeaderObj.put("IsCash", "Y");
            mHeaderObj.put("cardAmount", cardAmount);
            if (cardAmount == 0)
                mHeaderObj.put("IsCard", "N");
            else
                mHeaderObj.put("IsCard", "Y");
            if (cashAmount == 0 && cardAmount == 0)
                mHeaderObj.put("IsCash", "Y");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mHeaderObj;
    }

    public JSONArray getPaymentObj(HashMap mPayments) {

        JSONArray mArrayItems = new JSONArray();

        try {
            // Get a set of the entries
            Set set = mPayments.entrySet();
            // Get an iterator
            Iterator i = set.iterator();
            // Display elements
            while (i.hasNext()) {
                JSONObject mPaymentOhj = new JSONObject();
                Map.Entry me = (Map.Entry) i.next();
                System.out.print(me.getKey() + ": ");
                System.out.println(me.getValue());

                String key = ((String) me.getKey());
                double amount = ((Double) me.getValue()).doubleValue();

                mPaymentOhj.put("paymenttype", key);
                mPaymentOhj.put("amount", amount);

                mArrayItems.put(mPaymentOhj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mArrayItems;
    }

    /**
     * @param lineItems
     * @return
     */
    public JSONArray getOrderItems(List<POSLineItem> lineItems) {

        JSONArray mArrayItems = new JSONArray();

        try {
            for (int i = 0; i < lineItems.size(); i++) {
                JSONObject jo = new JSONObject();
                jo.put("KotLineID", lineItems.get(i).getKotLineId());
                jo.put("productId", lineItems.get(i).getProductId());
                jo.put("productCategoryId", lineItems.get(i).getCategoryId());
                jo.put("productName", lineItems.get(i).getProductName());
                jo.put("uomId", lineItems.get(i).getPosUOMId());
                jo.put("qty", lineItems.get(i).getPosQty());
                jo.put("productUOMValue", lineItems.get(i).getPosUOMValue());
                jo.put("actualPrice", lineItems.get(i).getStdPrice());
                jo.put("description", lineItems.get(i).getNotes());

                //GET the discount type and calculate the value
                int mDiscType = lineItems.get(i).getDiscType();
                double discVal = lineItems.get(i).getDiscValue();
                double prodPrice = lineItems.get(i).getStdPrice();
                double discountAmt = 0;

                if(mDiscType == 0){
                    jo.put("discountType", "P");
                    jo.put("dicountPercent", discVal);
                    discountAmt = ((prodPrice*discVal)/100)*lineItems.get(i).getPosQty();
                    jo.put("discountAmount", Common.valueFormatter(discountAmt));
                }else{
                    jo.put("discountType", "A");
                    jo.put("discountAmount", discVal);
                    discountAmt = (discVal/prodPrice*100)*lineItems.get(i).getPosQty();
                    jo.put("dicountPercent", Common.valueFormatter(discountAmt));
                }

                if(mDiscType == 0 && discVal!=0){
                    prodPrice = prodPrice - (prodPrice*discVal/100);
                }else{
                    prodPrice = prodPrice - (discVal/lineItems.get(i).getPosQty());
                }

                jo.put("price", prodPrice);
                jo.put("costPrice", lineItems.get(i).getCostPrice());

                List<POSLineItem> extraProductList = mDBHelper.getPOSExtraLineItems(lineItems.get(i).getKotLineId());

                if (extraProductList.size() != 0)
                    jo.put("relatedProductsArray", parseExtraProducts(extraProductList));

                mArrayItems.put(jo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mArrayItems;
    }

    private JSONArray parseExtraProducts(List<POSLineItem> lineItems) {
        JSONArray mArrayItems = new JSONArray();
        try {
            for (int i = 0; i < lineItems.size(); i++) {
                JSONObject jo = new JSONObject();
                jo.put("KotLineID", lineItems.get(i).getKotLineId());
                jo.put("productId", lineItems.get(i).getProductId());
                jo.put("productCategoryId", lineItems.get(i).getCategoryId());
                jo.put("productName", lineItems.get(i).getProductName());
                jo.put("uomId", lineItems.get(i).getPosUOMId());
                jo.put("qty", lineItems.get(i).getPosQty());
                jo.put("productUOMValue", lineItems.get(i).getPosUOMValue());
                jo.put("actualPrice", lineItems.get(i).getStdPrice());
                jo.put("RefKotLineID", lineItems.get(i).getRefRowId());
                jo.put("description", lineItems.get(i).getNotes());

                //GET the discount type and calculate the value
                int mDiscType = lineItems.get(i).getDiscType();
                double discVal = lineItems.get(i).getDiscValue();
                double prodPrice = lineItems.get(i).getStdPrice();
                double discountAmt = 0;

                if(mDiscType == 0){
                    jo.put("discountType", "P");
                    jo.put("dicountPercent", discVal);
                    discountAmt = ((prodPrice*discVal)/100)*lineItems.get(i).getPosQty();
                    jo.put("discountAmount", Common.valueFormatter(discountAmt));
                }else{
                    jo.put("discountType", "A");
                    jo.put("discountAmount", discVal);
                    discountAmt = (discVal/prodPrice*100)*lineItems.get(i).getPosQty();
                    jo.put("dicountPercent", Common.valueFormatter(discountAmt));
                }

                if(mDiscType == 0 && discVal!=0){
                    prodPrice = prodPrice - (prodPrice*discVal/100);
                }else{
                    prodPrice = prodPrice - discVal;
                }

                jo.put("price", prodPrice);
                jo.put("costPrice", lineItems.get(i).getCostPrice());

                mArrayItems.put(jo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mArrayItems;
    }

    /**
     *
     * @param kotHeaderList
     * @return
     */
    public  JSONArray getPOSTPrintedKOT(List<KOTHeader> kotHeaderList){
        JSONArray kotArray = new JSONArray();
        try {
            for (int i = 0; i < kotHeaderList.size(); i++) {
                JSONObject jo = new JSONObject();
                jo.put("KOTNumber", kotHeaderList.get(i).getKotNumber());
                kotArray.put(jo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  kotArray;
    }

    public  JSONArray getPrintedKOT(List<Long> kotHeaderList){
        JSONArray kotArray = new JSONArray();
        try {
            for (int i = 0; i < kotHeaderList.size(); i++) {
                JSONObject jo = new JSONObject();
                jo.put("KOTNumber", kotHeaderList.get(i));
                kotArray.put(jo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  kotArray;
    }

    /**
     * @param jsonStr
     * @param mHandler
     */
    public void parseOrgJson(String jsonStr, Handler mHandler) {

        Log.i("RESPONSE", jsonStr);
        JSONObject json;
        JSONArray orgArray;
        JSONArray roleArray;
        JSONArray roleAccessArray;
        JSONArray warehouseArray;

        Message msg = new Message();

        try {
            json = new JSONObject(jsonStr);
            if (json.getInt("responseCode") == 200) {

                //mDBHelper.deleteAllTables();

                mAppManager.setUserID(json.getLong("userId"));
                mAppManager.setUserBPID(json.getLong("businessPartnerId"));
                mAppManager.setClientID(json.getLong("clientId"));
                mAppManager.setIsRetail(json.getString("isRetail"));

                orgArray = json.getJSONArray("orgDetails");
                for (int i = 0; i < orgArray.length(); i++) {
                    JSONObject orgJson = (JSONObject) orgArray.get(i);
                    Organization mOrg = new Organization();
                    mOrg.setOrgId(orgJson.getLong("orgId"));
                    mOrg.setOrgName(orgJson.getString("orgName"));
                    mOrg.setIsDefault(orgJson.getString("isdefault"));

                    //checking org arabic name is available or not
                    if (orgJson.has("orgArabic"))
                        mOrg.setOrgArabicName(orgJson.getString("orgArabic"));
                    else
                        mOrg.setOrgArabicName("");

                    //checking org image is available or not
                    if (orgJson.has("orgImage")){
                        String imagePath = FileUtils.storeImage(orgJson.getString("orgImage"),orgJson.getLong("orgId"),null);
                        mOrg.setOrgImage(imagePath);
                    }
                    else
                        mOrg.setOrgImage("");

                    //checking org phone is available or not
                    if (orgJson.has("orgPhone"))
                        mOrg.setOrgPhone(orgJson.getString("orgPhone"));
                    else
                        mOrg.setOrgPhone("");

                    //checking org email is available or not
                    if (orgJson.has("orgEmail"))
                        mOrg.setOrgEmail(orgJson.getString("orgEmail"));
                    else
                        mOrg.setOrgEmail("");

                    //checking org address is available or not
                    if (orgJson.has("orgAddress"))
                        mOrg.setOrgAddress(orgJson.getString("orgAddress"));
                    else
                        mOrg.setOrgAddress("");

                    //checking org city is available or not
                    if (orgJson.has("orgCity"))
                        mOrg.setOrgCity(orgJson.getString("orgCity"));
                    else
                        mOrg.setOrgCity("");

                    //checking org country is available or not
                    if (orgJson.has("orgCountry"))
                        mOrg.setOrgCountry(orgJson.getString("orgCountry"));
                    else
                        mOrg.setOrgCountry("");

                    //checking org web address is available or not
                    if (orgJson.has("orgWebUrl"))
                        mOrg.setOrgWebUrl(orgJson.getString("orgWebUrl"));
                    else
                        mOrg.setOrgWebUrl("");

                    //checking org description is available or not
                    if (orgJson.has("orgDescription"))
                        mOrg.setOrgDescription(orgJson.getString("orgDescription"));
                    else
                        mOrg.setOrgDescription("");

                    if (orgJson.has("orgReceiptfootermsg"))
                        mOrg.setOrgFooter(orgJson.getString("orgReceiptfootermsg"));
                    else
                        mOrg.setOrgFooter("");

                    mOrg.setClientId(mAppManager.getClientID());

                    mDBHelper.addOrganization(mOrg);
                }

                roleArray = json.getJSONArray("roleDetails");
                for (int i = 0; i < roleArray.length(); i++) {
                    JSONObject roleJson = (JSONObject) roleArray.get(i);
                    mDBHelper.addRole(roleJson.getLong("roleId"), roleJson.getString("roleName"), roleJson.getString("isdefault"));
                }

                roleAccessArray = json.getJSONArray("roleAccessDetails");
                for (int i = 0; i < roleAccessArray.length(); i++) {
                    JSONObject roleAccessJson = (JSONObject) roleAccessArray.get(i);
                    mDBHelper.addRoleAccess(mAppManager.getClientID(),roleAccessJson.getLong("orgId"), roleAccessJson.getLong("roleId"));
                }

                warehouseArray = json.getJSONArray("warehouseDetails");
                for (int i = 0; i < warehouseArray.length(); i++) {
                    JSONObject warehouseJson = (JSONObject) warehouseArray.get(i);
                    mDBHelper.addWarehouse(mAppManager.getClientID(), warehouseJson.getLong("orgId"), warehouseJson.getLong("warehouseId"), warehouseJson.getString("warehouseName"), warehouseJson.getString("isdefault"));
                }

                b.putInt("Type", AppConstants.ORGANIZATION_DATA_RECEIVED);
                b.putString("OUTPUT", "");

            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 101) {
                b.putInt("Type", AppConstants.LOGIN_FAILURE);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 700) {
                b.putInt("Type", AppConstants.NETWORK_ERROR);
                b.putString("OUTPUT", "");
            } else {
                b.putInt("Type", AppConstants.NO_DATA_RECEIVED);
                b.putString("OUTPUT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }

        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    /**
     * @param jsonStr
     * @param mHandler
     */
    public void parseLoginJson(String jsonStr, Handler mHandler) {
        Log.i("RESPONSE", jsonStr);
        Message msg = new Message();
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            if (json.getInt("responseCode") == 200) {
                if (json.getString("isSalesRep").equalsIgnoreCase("Yes"))
                    mAppManager.setSalesRep(true);
                else
                    mAppManager.setSalesRep(false);

                b.putInt("Type", AppConstants.LOGIN_SUCCESS);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 700) {
                b.putInt("Type", AppConstants.NETWORK_ERROR);
                b.putString("OUTPUT", "");
            } else {
                b.putInt("Type", AppConstants.LOGIN_FAILURE);
                b.putString("OUTPUT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }

        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    /**
     * @param jsonStr
     * @param mHandler
     */
    public void parseCommonJson(String jsonStr, Handler mHandler) {
        Log.i("RESPONSE", jsonStr);
        Message msg = new Message();
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            if (json.getInt("responseCode") == 200) {

                BPartner bPartner = new BPartner();
                bPartner.setBpNumber(json.getLong("businessPartnerId"));
                bPartner.setBpName(json.getString("customerName"));
                bPartner.setBpId(json.getLong("businessPartnerId"));
                bPartner.setBpPriceListId(json.getLong("pricelistId"));
                bPartner.setBpEmail("");
                bPartner.setBpNumber(0);

                mAppManager.saveCustomerData(bPartner);
                //mAppManager.saveCashCustomerData(json.getString("customerName"),json.getLong("businessPartnerId"));
                mAppManager.saveCommonData(json.getLong("costElementId"), json.getLong("currencyId"), json.getLong("cashBookId"), json.getLong("periodId"), json.getLong("paymentTermId"), json.getLong("adTableId"), json.getLong("accountSchemaId"), json.getLong("pricelistId"), json.getString("currencyCode"));

                if(json.has("msrCodeDetails")){
                    JSONArray jsonArray = json.getJSONArray("msrCodeDetails");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = (JSONObject) jsonArray.get(i);
                        mDBHelper.addAuthorizeId(mAppManager.getClientID(), mAppManager.getOrgID(), obj.getLong("userId"), obj.getString("msrName"), obj.getString("msrCode"));
                    }
                }

                b.putInt("Type", AppConstants.COMMON_DATA_RECEIVED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 700) {
                b.putInt("Type", AppConstants.NETWORK_ERROR);
                b.putString("OUTPUT", "");
            } else {
                b.putInt("Type", AppConstants.NO_DATA_RECEIVED);
                b.putString("OUTPUT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }

        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    /**
     * @param jsonStr
     */
    public void parsePOSNumber(String jsonStr, Handler mHandler) {
        JSONObject json;
        Message msg = new Message();
        try {
            json = new JSONObject(jsonStr);
            if (json.getInt("responseCode") == 200) {
                //AppConstants.posID = json.getLong("posId");
                mAppManager.savePOSNumberData(json.getLong("startNo"),json.getLong("endNo"));
                b.putInt("Type", AppConstants.POS_NUMBER_RECEIVED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 700) {
                b.putInt("Type", AppConstants.NETWORK_ERROR);
                b.putString("OUTPUT", "");
            } else {
                b.putInt("Type", AppConstants.NO_DATA_RECEIVED);
                b.putString("OUTPUT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }

        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public void parseTables(String jsonStr, Handler mHandler){
        Log.i("RESPONSE", jsonStr);
        Message msg = new Message();
        JSONObject json;
        JSONArray jsonArray;
        List<Tables> tableList = null;
        int length = 0;
        try {

            json = new JSONObject(jsonStr);
            if (json.getInt("responseCode") == 200) {

                tableList = new ArrayList<Tables>();
                jsonArray = json.getJSONArray("tables");

                length = jsonArray.length();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    Tables tables = new Tables();
                    tables.setClientId(mAppManager.getClientID());
                    tables.setOrgId(mAppManager.getOrgID());
                    tables.setTableId(obj.getLong("tablesId"));
                    tables.setTableName(obj.getString("tablesName"));
                    tables.setOrderAvailable("N");

                    mDBHelper.addTables(tables);

                    tableList.add(tables);
                }
            } else if (json.getInt("responseCode") == 700) {
                b.putInt("Type", AppConstants.NETWORK_ERROR);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 101) {
                b.putInt("Type", AppConstants.NO_TABLE_DATA_RECEIVED);
                b.putString("OUTPUT", "");
            }else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        } finally {

            if (length == tableList.size()) {
                b.putInt("Type", AppConstants.TABLES_RECEIVED);
                b.putString("OUTPUT", "");

                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        }
    }

    public void parseTerminals(String jsonStr, Handler mHandler){
        Log.i("RESPONSE", jsonStr);
        Message msg = new Message();
        JSONObject json;
        JSONArray jsonArray;
        List<Terminals> terminalsList = null;
        int length = 0;
        try {

            json = new JSONObject(jsonStr);
            if (json.getInt("responseCode") == 200) {

                terminalsList = new ArrayList<Terminals>();
                jsonArray = json.getJSONArray("terminals");

                length = jsonArray.length();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    Terminals terminals = new Terminals();
                    terminals.setClientId(mAppManager.getClientID());
                    terminals.setOrgId(mAppManager.getOrgID());
                    terminals.setTerminalId(obj.getLong("terminalId"));
                    terminals.setTerminalName(obj.getString("terminalName"));

                    if(obj.has("terminalIP"))
                        terminals.setTerminalIP(obj.getString("terminalIP"));
                    else
                        terminals.setTerminalIP("");

                    if(obj.has("isPrinter"))
                        terminals.setIsPrinter(obj.getString("isPrinter"));
                    else
                        terminals.setIsPrinter("N");

                    mDBHelper.addTerminals(terminals);

                    terminalsList.add(terminals);
                }
            } else if (json.getInt("responseCode") == 101) {
                b.putInt("Type", AppConstants.NO_TERMINAL_DATA_RECEIVED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            }else if (json.getInt("responseCode") == 700) {
                b.putInt("Type", AppConstants.NETWORK_ERROR);
                b.putString("OUTPUT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        } finally {

            if (length == terminalsList.size()) {
                b.putInt("Type", AppConstants.TERMINALS_RECEIVED);
                b.putString("OUTPUT", "");

                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        }
    }

    /**
     * @param jsonStr
     * @param mHandler
     */
    public void parseReleaseOrderJson(String jsonStr, Handler mHandler) {
        Message msg = new Message();
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            if (json.length() == 0) {
                if (NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                } else {
                    b.putInt("Type", AppConstants.POS_ORDER_RELEASED_FAILURE);
                    b.putString("OUTPUT", "");
                }
            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else {
                if (json.length() != 0 && json.getInt("responseCode") == 200) {
                    b.putInt("Type", AppConstants.POS_ORDER_RELEASED_SUCCESS);
                    b.putString("OUTPUT", "");
                }else if (json.getInt("responseCode") == 401) {
                    mAppManager.setSessionId(0);
                    b.putInt("Type", AppConstants.SESSION_EXPIRED);
                    b.putString("OUTPUT", "");
                }  else if (json.getInt("responseCode") == 700) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                } else {
                    b.putInt("Type", AppConstants.POS_ORDER_RELEASED_FAILURE);
                    b.putString("OUTPUT", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    /**
     * @param jsonStr
     * @return
     */
    public void parseCategorysJson(String jsonStr, Handler mHandler) {
        Runnable myThread = new ParseCategoryThread(jsonStr, mHandler);
        new Thread(myThread).start();
    }

    /**
     * @param jsonStr
     * @return
     */
    public void parseProductJson(String jsonStr, Handler mHandler) {
        Runnable myThread = new ParseProductThread(jsonStr, mHandler);
        new Thread(myThread).start();
    }

    /**
     * @param jsonStr
     */
    public void parseBPartnerJson(String jsonStr,Handler mHandler) {
        JSONObject json;
        JSONArray jsonArray;
        String email = "";
        long number = 0;
        Message msg = new Message();
        try {
            json = new JSONObject(jsonStr);
            if (json.getInt("responseCode") == 200) {
                jsonArray = json.getJSONArray("customers");
                mDBHelper.deleteBPartner();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = (JSONObject) jsonArray.get(i);

                    if (obj.has("customerNumber"))
                        number = obj.getLong("customerNumber");

                    if (obj.has("customerEmail"))
                        email = obj.getString("customerEmail");

                    BPartner bPartner = new BPartner();
                    bPartner.setClientId(mAppManager.getClientID());
                    bPartner.setOrgId(mAppManager.getOrgID());
                    bPartner.setBpId(obj.getLong("businessPartnerId"));
                    bPartner.setBpName(obj.getString("customerName"));
                    bPartner.setBpValue(obj.getString("customerValue"));
                    bPartner.setBpPriceListId(obj.getLong("pricelistId"));
                    bPartner.setBpEmail(email);
                    bPartner.setBpNumber(number);
                    bPartner.setIsCredit(obj.getString("isCredit"));
                    bPartner.setCreditLimit(obj.getDouble("creditLimit"));

                    mDBHelper.addBPartner(bPartner);
                }

                b.putInt("Type", AppConstants.BPARTNER_DATA_RECEIVED);
                b.putString("OUTPUT", "");

            }else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 101) {
                b.putInt("Type", AppConstants.NO_BPARTNER_DATA_RECEIVED);
                b.putString("OUTPUT", "");
            }else if (json.getInt("responseCode") == 700) {
                b.putInt("Type", AppConstants.NETWORK_ERROR);
                b.putString("OUTPUT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(mHandler!=null){
                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        }
    }

    public class ParseCategoryThread implements Runnable {

        String mJsonStr;
        Handler mHandler;

        Message msg = new Message();
        JSONObject json;
        JSONArray jsonArray;
        List<Category> categoryList = null;
        int length = 0;

        public ParseCategoryThread(String jsonStr, Handler handler) {
            // store parameter for later user
            mJsonStr = jsonStr;
            mHandler = handler;
        }

        @Override
        public void run() {
            try {

                json = new JSONObject(mJsonStr);
                if (json.getInt("responseCode") == 200) {

                    categoryList = new ArrayList<Category>();
                    jsonArray = json.getJSONArray("category");

                    length = jsonArray.length();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject obj = (JSONObject) jsonArray.get(i);
                        Category category = new Category();
                        category.setCategoryId(obj.getLong("categoryId"));
                        category.setCategoryName(obj.getString("categoryName"));
                        category.setCategoryValue(obj.getString("categoryValue"));

                        //load image to sdcard and store the path to db
                        if (obj.has("categoryImage")) {
                            String imagePath = FileUtils.storeImage(obj.getString("categoryImage"),obj.getLong("categoryId"),null);
                            category.setCategoryImage(imagePath);
                        } else {
                            // Retrieve the image from the res folder
                            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                                    R.drawable.no_product);
                            String imagePath = FileUtils.storeImage("",obj.getLong("categoryId"),bitmap);
                            category.setCategoryImage(imagePath);
                        }

                        if (obj.has("showDigitalMenu")) {
                            category.setShowDigitalMenu(obj.getString("showDigitalMenu"));
                        }else{
                            category.setShowDigitalMenu("Y");
                        }

                        category.setClientId(mAppManager.getClientID());
                        category.setOrgId(mAppManager.getOrgID());

                        //add category
                        mDBHelper.addCategory(category);

                        categoryList.add(category);
                    }
                } else if (json.getInt("responseCode") == 301) {
                    b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                    b.putString("OUTPUT", "");
                } else if (json.getInt("responseCode") == 101) {
                    b.putInt("Type", AppConstants.NO_CATEGORY_DATA_RECEIVED);
                    b.putString("OUTPUT", "");
                }else if (json.getInt("responseCode") == 700) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
                b.putInt("Type", AppConstants.SERVER_ERROR);
                b.putString("OUTPUT", "");
            } finally {

                if (length == categoryList.size()) {
                    b.putInt("Type", AppConstants.CATEGORY_RECEIVED);
                    b.putString("OUTPUT", "");

                    msg.setData(b);
                    mHandler.sendMessage(msg);
                }
            }
        }
    }

    public class ParseProductThread implements Runnable {

        Message msg = new Message();
        List<Product> productList = null;
        JSONObject json;
        JSONArray jsonArray;
        long categoryId;
        int length = 0;
        String mJsonStr;
        Handler mHandler;

        public ParseProductThread(String jsonStr, Handler handler) {
            // store parameter for later user
            mJsonStr = jsonStr;
            mHandler = handler;
        }

        @Override
        public void run() {
            try {
                json = new JSONObject(mJsonStr);
                if (json.getInt("responseCode") == 200) {

                    productList = new ArrayList<Product>();
                    jsonArray = json.getJSONArray("products");
                    length = jsonArray.length();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject obj = (JSONObject) jsonArray.get(i);
                        Product product = new Product();
                        categoryId = obj.getLong("categoryId");
                        product.setProdId(obj.getLong("productId"));
                        product.setProdName(obj.getString("productName"));
                        product.setProdValue(obj.getString("productValue"));
                        product.setUomId(obj.getLong("productUOMId"));
                        product.setUomValue(obj.getString("productUOMValue"));
                        product.setSalePrice(Double.parseDouble(obj.getString("sellingPrice")));
                        product.setCostPrice(Double.parseDouble(obj.getString("costprice")));
                        product.setTerminalId(obj.getLong("terminalId"));

                        //load image to sdcard and store the path to db
                        if (obj.has("productImage")) {
                            String imagePath = FileUtils.storeImage(obj.getString("productImage"),obj.getLong("productId"),null);
                            product.setProdImage(imagePath);
                        } else {
                            // Retrieve the image from the res folder
                            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                                    R.drawable.no_product);
                            String imagePath = FileUtils.storeImage("",obj.getLong("productId"),bitmap);
                            product.setProdImage(imagePath);
                        }

                        if (obj.has("productArabicName")) {
                            product.setProdArabicName(obj.getString("productArabicName"));
                        } else {
                            product.setProdArabicName("");
                        }

                        if (obj.has("description")) {
                            product.setDescription(obj.getString("description"));
                        } else {
                            product.setDescription("");
                        }

                        if (obj.has("showDigitalMenu")) {
                            product.setShowDigitalMenu(obj.getString("showDigitalMenu"));
                        } else {
                            product.setShowDigitalMenu("Y");
                        }

                        if (obj.has("productVideoPath")) {
                            product.setProdVideoPath(obj.getString("productVideoPath"));
                        } else {
                            product.setProdVideoPath("N");
                        }

                        if (obj.has("calories")) {
                            product.setCalories(obj.getString("calories"));
                        } else {
                            product.setCalories("");
                        }

                        if (obj.has("preparationTime")) {
                            product.setPreparationTime(obj.getString("preparationTime"));
                        } else {
                            product.setPreparationTime("");
                        }

                        product.setClientId(mAppManager.getClientID());
                        product.setOrgId(mAppManager.getOrgID());

                        mDBHelper.addProduct(categoryId, product);
                        productList.add(product);
                    }

                }else if (json.getInt("responseCode") == 301) {
                    b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                    b.putString("OUTPUT", "");
                }else if (json.getInt("responseCode") == 101) {
                    b.putInt("Type", AppConstants.NO_PRODUCT_DATA_RECEIVED);
                    b.putString("OUTPUT", "");
                }else if (json.getInt("responseCode") == 700) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
                b.putInt("Type", AppConstants.SERVER_ERROR);
                b.putString("OUTPUT", "");
            } finally {
                if (length == productList.size()) {
                    b.putInt("Type", AppConstants.PRODUCTS_RECEIVED);
                    b.putString("OUTPUT", "");

                    msg.setData(b);
                    mHandler.sendMessage(msg);
                }
            }
        }
    }

    /**
     * Parse KOT DATA HEADER AND LINE ITEMS
     *
     * @param jsonStr
     * @param mHandler
     */
    public void parseKOTData(String jsonStr, Handler mHandler){
        AppConstants.isKOTParsing = true;
        Runnable myThread = new ParseKOTDataThread(jsonStr, mHandler);
        new Thread(myThread).start();
    }

    public class ParseKOTDataThread implements Runnable {

        String mJsonStr;
        Handler mHandler;

        JSONObject json;
        JSONArray jsonTableArray, jsonTokenArray, jsonProductArray;

        long kotNumber, invoiceNumber, terminalID, tableId;
        double totalAmount;
        String notes, kotType, waiterName, printed="N", orderType = "Running";
        int qty;
        Message msg = new Message();

        public ParseKOTDataThread(String jsonStr, Handler handler) {
            // store parameter for later user
            mJsonStr = jsonStr;
            mHandler = handler;
        }

        @Override
        public void run() {
            try {
                json = new JSONObject(mJsonStr);
                if (json.getInt("responseCode") == 200) {

                    if(json.has("tables")){
                        jsonTableArray = json.getJSONArray("tables");

                        //Loop for getting tables
                        for (int i = 0; i < jsonTableArray.length(); i++) {

                            JSONObject tableObj = (JSONObject) jsonTableArray.get(i);
                            tableId = tableObj.getLong("tableId");
                            jsonTokenArray = tableObj.getJSONArray("tokens");

                            //update orderAvailable = Y
                            mDBHelper.updateOrderAvailableTable(tableId);

                            //Loop for getting tokens
                            for(int j=0; j<jsonTokenArray.length(); j++){

                                JSONObject tokenObj = (JSONObject) jsonTokenArray.get(j);

                                kotNumber = tokenObj.getLong("KOTNumber");
                                invoiceNumber = tokenObj.getLong("invoiceNumber");
                                terminalID = tokenObj.getLong("terminalId");
                                totalAmount = tokenObj.getLong("totalAmount");
                                kotType = tokenObj.getString("kotType");
                                waiterName = tokenObj.getString("waiterName");

                                if(tokenObj.has("isPrinted"))
                                    printed = tokenObj.getString("isPrinted");

                                if(tokenObj.has("orderType"))
                                    orderType = tokenObj.getString("orderType");

                                KOTHeader kotHeader = new KOTHeader();
                                kotHeader.setTablesId(tableId);
                                kotHeader.setKotNumber(kotNumber);

                                /*if(tableId!=0){
                                    long invoiceNum = mDBHelper.getKOTInvoiceNumber(tableId);
                                    if (invoiceNum == 0) {
                                        invoiceNumber = invoiceNum;
                                    }else{
                                        invoiceNumber = invoiceNum;
                                    }
                                }*/

                                if(tableId==0){
                                    kotHeader.setInvoiceNumber(invoiceNumber);
                                }else{
                                    kotHeader.setInvoiceNumber(0);
                                }

                                kotHeader.setTerminalId(terminalID);
                                kotHeader.setTotalAmount(totalAmount);
                                kotHeader.setOrderBy(waiterName);
                                kotHeader.setKotType(kotType);
                                kotHeader.setOrderType(orderType);
                                kotHeader.setIsKOT("Y");
                                kotHeader.setPrinted(printed);
                                kotHeader.setPosted("N");
                                kotHeader.setSelected("N");
                                if(tokenObj.has("coversCount"))
                                    kotHeader.setCoversCount(tokenObj.getInt("coversCount"));

                                //insert kot header data to db
                                boolean inserted = mDBHelper.addKOTHeader(kotHeader);

                                Log.i("KOT-HEADER INSERTED: ", ""+inserted);

                                if(inserted){
                                    //getting products array
                                    jsonProductArray = tokenObj.getJSONArray("products");

                                    //Loop for getting products
                                    for(int k=0; k<jsonProductArray.length(); k++){

                                        JSONObject productObj = (JSONObject) jsonProductArray.get(k);

                                        Product prod = mDBHelper.getProduct(mAppManager.getClientID(), mAppManager.getOrgID(), productObj.getLong("productId"));

                                        notes = productObj.getString("description");

                                        Product product = new Product();
                                        product.setCategoryId(prod.getCategoryId());
                                        product.setProdId(productObj.getLong("productId"));
                                        product.setProdName(prod.getProdName());
                                        product.setProdArabicName(prod.getProdArabicName());
                                        product.setProdValue(prod.getProdValue());
                                        product.setUomId(productObj.getLong("productUOMId"));
                                        product.setUomValue(prod.getUomValue());
                                        product.setSalePrice(productObj.getDouble("sellingPrice"));
                                        product.setCostPrice(prod.getCostPrice());
                                        product.setTerminalId(terminalID);

                                        qty = productObj.getInt("qty");

                                        long kotLineId = productObj.getLong("KotLineID");
                                        long invNumber = productObj.getLong("invoiceNumber");

                                        if(invNumber!=0)
                                            mDBHelper.updateKOTLineIdToPOSLineItem(invNumber,kotLineId,productObj.getLong("productId"));

                                        if(tableId!=0){
                                            List<Long> invNumList = mDBHelper.getKOTInvoiceNumbers(tableId);

                                            int size = invNumList.size();

                                            if(size>0)
                                                invoiceNumber = invNumList.get(size-1);
                                        }


                                        KOTLineItems kotLineItems = new KOTLineItems();
                                        kotLineItems.setTableId(tableId);
                                        kotLineItems.setKotLineId(kotLineId);
                                        kotLineItems.setKotNumber(kotNumber);
                                        kotLineItems.setInvoiceNumber(invoiceNumber);
                                        kotLineItems.setNotes(productObj.getString("description"));
                                        kotLineItems.setRefRowId(0);
                                        kotLineItems.setIsExtraProduct("N");

                                        //insert kot line items
                                        mDBHelper.addKOTLineItems(kotLineItems, product, qty);

                                        if(productObj.has("relatedProductsArray"))
                                            parseRelatedProducts(tableId, terminalID, kotNumber, invoiceNumber, kotLineId, productObj.getJSONArray("relatedProductsArray"));

                                        if(AndroidApplication.isActivityVisible() && mTokenListener!=null && AppConstants.posID!=0){
                                            //update the cart if invoiceNumber==AppConstants.posId
                                            if(invoiceNumber==AppConstants.posID)
                                                mTokenListener.OnTokenReceivedListener(tableId,invoiceNumber);
                                        }
                                    }
                                }
                            }

                        }
                    }else if(json.has("draftedTables")){
                        mDBHelper.updateAllTableStatus();
                        JSONArray jsonDraftedTableArray = json.getJSONArray("draftedTables");
                        for (int i = 0; i < jsonDraftedTableArray.length(); i++) {
                            JSONObject tableObj = (JSONObject) jsonDraftedTableArray.get(i);
                            mDBHelper.updateOrderAvailableTable(tableObj.getLong("tableId"));
                        }
                    }

                } else if (json.getInt("responseCode") == 301) {
                    b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                    b.putString("OUTPUT", "");
                } else if (json.getInt("responseCode") == 101) {
                    mDBHelper.updateAllTableStatus();
                }else if (json.getInt("responseCode") == 700) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
                b.putInt("Type", AppConstants.SERVER_ERROR);
                b.putString("OUTPUT", "");
            } finally {
                b.putInt("Type", AppConstants.KOT_HEADER_AND_lINES_RECEIVED);
                b.putString("OUTPUT", "");

                msg.setData(b);

                if(mHandler!=null)
                mHandler.sendMessage(msg);

                ParsingStatusListener.getInstance().parsingStatus();
            }
        }
    }

    private void parseRelatedProducts(long tableId, long terminalID, long kotNumber, long invNumber, long refLineId, JSONArray mRelatedProdArray){

        try {
            //getting products array
            JSONArray jsonProductArray = mRelatedProdArray;

            //Loop for getting products
            for (int k = 0; k < jsonProductArray.length(); k++) {

                JSONObject productObj = (JSONObject) jsonProductArray.get(k);

                Product prod = mDBHelper.getProduct(mAppManager.getClientID(), mAppManager.getOrgID(), productObj.getLong("productId"));

                Product product = new Product();
                product.setCategoryId(prod.getCategoryId());
                product.setProdId(productObj.getLong("productId"));
                product.setProdName(prod.getProdName());
                product.setProdArabicName(prod.getProdArabicName());
                product.setProdValue(prod.getProdValue());
                product.setUomId(productObj.getLong("productUOMId"));
                product.setUomValue(prod.getUomValue());
                product.setSalePrice(productObj.getDouble("sellingPrice"));
                product.setCostPrice(prod.getCostPrice());
                product.setTerminalId(terminalID);

                long kotLineId = productObj.getLong("KotLineID");

                if (invNumber != 0)
                    mDBHelper.updateRelatedKOTLineIdToPOSLineItem(invNumber, kotLineId, productObj.getLong("productId"));

                KOTLineItems kotLineItems = new KOTLineItems();
                kotLineItems.setTableId(tableId);
                kotLineItems.setKotLineId(kotLineId);
                kotLineItems.setKotNumber(kotNumber);
                kotLineItems.setInvoiceNumber(invNumber);
                kotLineItems.setNotes(productObj.getString("description"));
                kotLineItems.setRefRowId(refLineId);
                kotLineItems.setIsExtraProduct("Y");

                //insert addOn kot line items
                mDBHelper.addKOTLineItems(kotLineItems, product, productObj.getInt("qty"));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void parseKOTStatus(String jsonStr, Handler mHandler){
        Message msg = new Message();
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            if (json.length() == 0) {
                if (NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                } else {
                    b.putInt("Type", AppConstants.NO_DATA_RECEIVED);
                    b.putString("OUTPUT", "");
                }
            } else {
                if (json.length() != 0 && json.getInt("responseCode") == 200) {
                    b.putInt("Type", AppConstants.KOT_FLAGS_RESPONSE_RECEIVED);
                    b.putString("OUTPUT", "");
                } else if (json.getInt("responseCode") == 301) {
                    b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                    b.putString("OUTPUT", "");
                } else if (json.getInt("responseCode") == 700) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                } else {
                    b.putInt("Type", AppConstants.NO_DATA_RECEIVED);
                    b.putString("OUTPUT", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }
        msg.setData(b);

        if(mHandler!=null)
        mHandler.sendMessage(msg);
    }

    public void parseTableChangeStatus(String jsonStr, Handler mHandler){
        Message msg = new Message();
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            if (json.length() == 0) {
                if (NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                } else {
                    b.putInt("Type", AppConstants.NO_DATA_RECEIVED);
                    b.putString("OUTPUT", "");
                }
            } else {
                if (json.length() != 0 && json.getInt("responseCode") == 200) {
                    long activeTableId = json.getLong("activeTableId");
                    long targetTableId = json.getLong("targetTableId");
                    mDBHelper.updateTableChange(activeTableId, targetTableId);
                    b.putInt("Type", AppConstants.TABLE_CHANGE_SUCCESS);
                    b.putString("OUTPUT", "");
                } else if (json.getInt("responseCode") == 301) {
                    b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                    b.putString("OUTPUT", "");
                } else if (json.getInt("responseCode") == 700) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                } else {
                    b.putInt("Type", AppConstants.TABLE_CHANGE_FAILURE);
                    b.putString("OUTPUT", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }
        msg.setData(b);

        if(mHandler!=null)
            mHandler.sendMessage(msg);
    }

    public JSONArray getCounterSaleKOTItems() {

        JSONArray mArrayTokens = new JSONArray();

        try {

            List<String> terminalList = mDBHelper.getCounterSaleKOTItemTerminals(AppConstants.posID);
            for (int i = 0; i < terminalList.size(); i++) {

                long terminalID = Long.parseLong(terminalList.get(i));
                double totalPrice = mDBHelper.sumOfCounterSalesTerminalItemsTotal(AppConstants.posID, terminalID);

                JSONObject jo = new JSONObject();

                jo.put("terminalId", terminalID);
                jo.put("totalAmount", totalPrice);
                jo.put("invoiceNumber", AppConstants.posID);

                List<Product> productList = mDBHelper.getCounterSaleKOTLineItems(AppConstants.posID, terminalID);

                JSONArray mArrayItems = new JSONArray();
                for (int j = 0; j < productList.size(); j++) {
                    JSONObject jItemObj = new JSONObject();
                    jItemObj.put("productId", productList.get(j).getProdId());
                    jItemObj.put("productName", productList.get(j).getProdName());
                    jItemObj.put("productValue", productList.get(j).getProdValue());
                    jItemObj.put("categoryId", productList.get(j).getCategoryId());
                    jItemObj.put("productUOMId", productList.get(j).getUomId());
                    jItemObj.put("productUOMValue", productList.get(j).getUomValue());
                    jItemObj.put("sellingPrice", productList.get(j).getSalePrice());
                    jItemObj.put("qty", productList.get(j).getQty());
                    jItemObj.put("total", productList.get(j).getTotalPrice());
                    jItemObj.put("description", productList.get(j).getDescription());
                    mArrayItems.put(jItemObj);
                }
                jo.put("products", mArrayItems);
                mArrayTokens.put(jo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mArrayTokens;
    }

    public void parseKOTResponse(String jsonStr, Handler mHandler) {
        Log.i("RESPONSE", jsonStr);
        Message msg = new Message();
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            if (json.getInt("responseCode") == 200) {
                b.putInt("Type", AppConstants.POST_KOT_DATA_RESPONSE);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 700) {
                b.putInt("Type", AppConstants.NETWORK_ERROR);
                b.putString("OUTPUT", "");
            } else {
                b.putInt("Type", AppConstants.SERVER_ERROR);
                b.putString("OUTPUT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }

        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public void parseCancelOrder(String jsonStr, Handler mHandler) {
        Log.i("RESPONSE", jsonStr);
        Message msg = new Message();
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            if (json.length() == 0) {
                if (NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                } else {
                    b.putInt("Type", AppConstants.POS_ORDER_CANCEL_FAILURE);
                    b.putString("OUTPUT", "");
                }
            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else {
                if (json.length() != 0 && json.getInt("responseCode") == 200) {
                    b.putInt("Type", AppConstants.POS_ORDER_CANCEL_SUCCESS);
                    b.putString("OUTPUT", "");
                }else if (json.getInt("responseCode") == 401) {
                    mAppManager.setSessionId(0);
                    b.putInt("Type", AppConstants.SESSION_EXPIRED);
                    b.putString("OUTPUT", "");
                }  else if (json.getInt("responseCode") == 700) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                } else {
                    b.putInt("Type", AppConstants.POS_ORDER_CANCEL_FAILURE);
                    b.putString("OUTPUT", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public void parseDraftOrder(String jsonStr, Handler mHandler) {
        Log.i("RESPONSE", jsonStr);
        Message msg = new Message();
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            if (json.length() == 0) {
                if (NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                } else {
                    b.putInt("Type", AppConstants.POS_ORDER_DRAFT_FAILURE);
                    b.putString("OUTPUT", "");
                }
            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else {
                if (json.length() != 0 && json.getInt("responseCode") == 200) {
                    long posId = 0;
                    b.putInt("Type", AppConstants.POS_ORDER_DRAFT_SUCCESS);
                    posId = json.getLong("posId");
                    b.putString("OUTPUT", String.valueOf(posId));
                }else if (json.getInt("responseCode") == 401) {
                    mAppManager.setSessionId(0);
                    b.putInt("Type", AppConstants.SESSION_EXPIRED);
                    b.putString("OUTPUT", "");
                } else if (json.getInt("responseCode") == 700) {
                    b.putInt("Type", AppConstants.NETWORK_ERROR);
                    b.putString("OUTPUT", "");
                }  else {
                    b.putInt("Type", AppConstants.POS_ORDER_DRAFT_FAILURE);
                    b.putString("OUTPUT", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public void parseStartSessionResponse(String jsonStr, Handler mHandler) {
        Log.i("RESPONSE", jsonStr);
        Message msg = new Message();
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            if (json.getInt("responseCode") == 200) {
                mAppManager.setSessionStatus(true);
                mAppManager.setSessionId(json.getLong("sessionId"));

                //set the current time
                Date date = new Date(System.currentTimeMillis());
                long millis = date.getTime();
                mAppManager.setSessionCreatedTime(millis);

                b.putInt("Type", AppConstants.CREATE_SESSION_RESPONSE);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 700) {
                b.putInt("Type", AppConstants.NETWORK_ERROR);
                b.putString("OUTPUT", "");
            } else {
                b.putInt("Type", AppConstants.SERVER_ERROR);
                b.putString("OUTPUT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }

        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public void parseResumeSessionResponse(String jsonStr, Handler mHandler) {
        Log.i("RESPONSE", jsonStr);
        Message msg = new Message();
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            if (json.getInt("responseCode") == 200) {
                mAppManager.setSessionStatus(true);
                mAppManager.setSessionId(json.getLong("sessionId"));
                b.putInt("Type", AppConstants.RESUME_SESSION_RESPONSE);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 401) {
                mAppManager.setSessionStatus(false);
                mAppManager.setSessionId(0);
                b.putInt("Type", AppConstants.SESSION_EXPIRED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 700) {
                b.putInt("Type", AppConstants.NETWORK_ERROR);
                b.putString("OUTPUT", "");
            } else {
                b.putInt("Type", AppConstants.SERVER_ERROR);
                b.putString("OUTPUT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }

        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public void parseCloseSessionResponse(String jsonStr, Handler mHandler) {
        Log.i("RESPONSE", jsonStr);
        Message msg = new Message();
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            if (json.getInt("responseCode") == 200) {
                mAppManager.setSessionStatus(false);
                mAppManager.setSessionId(json.getLong("sessionId"));
                b.putInt("Type", AppConstants.CLOSE_SESSION_RESPONSE);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 700) {
                b.putInt("Type", AppConstants.NETWORK_ERROR);
                b.putString("OUTPUT", "");
            } else {
                b.putInt("Type", AppConstants.SERVER_ERROR);
                b.putString("OUTPUT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }

        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public void parseCreditLimit(String jsonStr, Handler mHandler) {
        Log.i("RESPONSE", jsonStr);
        Message msg = new Message();
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            if (json.getInt("responseCode") == 200) {

                mDBHelper.updateBPartnerCreditLimit(json.getLong("businessPartnerId"),json.getDouble("creditLimit"));

                b.putInt("Type", AppConstants.CREDIT_LIMIT_RECEIVED);
                b.putString("OUTPUT", String.valueOf(json.getLong("businessPartnerId")));
            } else if (json.getInt("responseCode") == 301) {
                b.putInt("Type", AppConstants.DEVICE_NOT_REGISTERED);
                b.putString("OUTPUT", "");
            } else if (json.getInt("responseCode") == 700) {
                b.putInt("Type", AppConstants.NETWORK_ERROR);
                b.putString("OUTPUT", "");
            } else {
                b.putInt("Type", AppConstants.SERVER_ERROR);
                b.putString("OUTPUT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("Type", AppConstants.SERVER_ERROR);
            b.putString("OUTPUT", "");
        }

        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public boolean getParsingState(){
        return mParsingState;
    }
}

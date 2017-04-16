package com.zearoconsulting.zearopos.utils;

import android.os.Environment;

/**
 * Created by saravanan on 24-05-2016.
 * AppConstants for dhukan pos
 */
public class AppConstants {

    public static final String kURLHttp="http://";
    public static final String kURLServiceName="/CCLWebService/CCLPOSResource";
    public static final String kURLMethodTest="/test";
    public static final String kURLMethodApi="/api";

    /**POS PRODUCTION API URL*/
    //public static final String URL = "http://empower-erp.com:8080/ZearoPOSWebService/POSResource/api";

    /**POS UAT API URL*/
    //public static final String URL = "http://demo.empower-erp.com:8080/ZearoPOSWebService/POSResource/api";

    public static String URL = "http://demo.empower-erp.com:8080/CCLWebService/CCLPOSResource/api";

    /** call collect organization key */
    public static final int GET_ORGANIZATION_DATA = 1;

    /** received organization key */
    public static final int ORGANIZATION_DATA_RECEIVED = 2;

    /** call authenticate key */
    public static final int CALL_AUTHENTICATE = 3;

    /** authenticate success key */
    public static final int LOGIN_SUCCESS = 4;

    /** authenticate failure key */
    public static final int LOGIN_FAILURE = 5;

    /** call get cash customer data key */
    public static final int GET_CASH_CUSTOMER_DATA = 6;

    /** call business partners key */
    public static final int GET_BPARTNERS = 7;

    /** response common data key */
    public static final int COMMON_DATA_RECEIVED = 8;

    /** get category key */
    public static final int GET_CATEGORY = 9;

    /** response category key */
    public static final int CATEGORY_RECEIVED = 10;

    /** call get products key */
    public static final int GET_PRODUCTS = 11;

    /** get all products key*/
    public static final int GET_ALL_PRODUCTS = 12;

    /** response product received key */
    public static final int PRODUCTS_RECEIVED = 13;

    /** call get product price key */
    public static final int GET_PRODUCT_PRICE = 14;

    /** call get tables key */
    public static final int GET_TABLES = 15;

    /** response tables received key */
    public static final int TABLES_RECEIVED = 16;

    /** call get terminals key */
    public static final int GET_TERMINALS = 17;

    /** response terminals received key */
    public static final int TERMINALS_RECEIVED = 18;

    /** call get session request received key */
    public static final int CREATE_SESSION_REQUEST = 19;

    public static final int CREATE_SESSION_RESPONSE = 20;

    /** call get session resume request received key */
    public static final int RESUME_SESSION_REQUEST = 21;

    public static final int RESUME_SESSION_RESPONSE = 22;

    /** call get session close request received key */
    public static final int CLOSE_SESSION_REQUEST = 23;

    public static final int CLOSE_SESSION_RESPONSE = 24;

    /** get pos number key */
    public static final int GET_POS_NUMBER = 25;

    /** response pos number key */
    public static final int POS_NUMBER_RECEIVED = 26;

    /** get pos number key */
    public static final int CHECK_CREDIT_LIMIT = 27;

    /** response pos number key */
    public static final int CREDIT_LIMIT_RECEIVED = 28;

    /** call table change request key */
    public static final int POST_TABLE_CHANGE = 29;

    /** response table change success key */
    public static final int TABLE_CHANGE_SUCCESS = 30;

    /** response table change failure key */
    public static final int TABLE_CHANGE_FAILURE = 31;

    /** call get kot header key */
    public static final int GET_KOT_HEADER_AND_lINES = 32;

    /** response kot header received key */
    public static final int KOT_HEADER_AND_lINES_RECEIVED = 33;

    /** call post kot data key */
    public static final int POST_KOT_DATA = 34;

    /** response kot data key */
    public static final int POST_KOT_DATA_RESPONSE = 35;

    /** call get kot table line items key */
    public static final int POST_KOT_FLAGS = 36;

    /** response for kot table line items received key */
    public static final int KOT_FLAGS_RESPONSE_RECEIVED = 37;

    /** call get table kot key */
    public static final int GET_TABLE_KOT_DETAILS = 38;

    /** response for table kot success key */
    public static final int TABLE_KOT_DETAILS_RECEIVED_SUCCESS = 39;

    /** response for table kot failure key */
    public static final int TABLE_KOT_DETAILS_RECEIVED_FAILURE = 40;

    /** call draft pos order key */
    public static final int CALL_DRAFT_POS_ORDER = 41;

    /** response for draft work order success key */
    public static final int POS_ORDER_DRAFT_SUCCESS = 42;

    /** response for draft work order failure key */
    public static final int POS_ORDER_DRAFT_FAILURE = 43;

    /** call release work order key */
    public static final int CALL_RELEASE_POS_ORDER = 44;

    /** response for release work order success key */
    public static final int POS_ORDER_RELEASED_SUCCESS = 45;

    /** response for release work order failure key */
    public static final int POS_ORDER_RELEASED_FAILURE = 46;

    /** call cancel work order key */
    public static final int CALL_CANCEL_POS_ORDER = 47;

    /** response for cancel work order success key */
    public static final int POS_ORDER_CANCEL_SUCCESS = 48;

    /** response for cancel work order failure key */
    public static final int POS_ORDER_CANCEL_FAILURE = 49;

    /** response no data received key */
    public static final int NO_DATA_RECEIVED = 50;

    /** response server error key */
    public static final int SERVER_ERROR = 51;

    /** response network error key */
    public static final int NETWORK_ERROR = 52;

    /** response device not registered error key */
    public static final int DEVICE_NOT_REGISTERED = 53;

    /** response session expired error key */
    public static final int SESSION_EXPIRED = 54;

    /** response no bpartner data received key */
    public static final int BPARTNER_DATA_RECEIVED = 55;

    /** response no bpartner data received key */
    public static final int NO_BPARTNER_DATA_RECEIVED = 56;

    /** response no table data received key */
    public static final int NO_TABLE_DATA_RECEIVED = 57;

    /** response no terminal data received key */
    public static final int NO_TERMINAL_DATA_RECEIVED = 58;

    /** response no category data received key */
    public static final int NO_CATEGORY_DATA_RECEIVED = 59;

    /** response no product data received key */
    public static final int NO_PRODUCT_DATA_RECEIVED = 60;

    public static final int CALL_DELETE_KOT_ITEM = 61;

    public static final int DELETE_KOT_RESPONSE_SUCCESS = 62;

    public static final int DELETE_KOT_RESPONSE_ERROR = 63;

    /** notify response failure key */
    public static final String NETWORK_FAILURE = "Not connected to Internet";

    /** initializing posID */
    public static long posID = 0;

    /** initializing tableID */
    public static long tableID = 0;

    /** initializing authorizedId */
    public static long authorizedId = 0;

    /** initialized default currency code format */
    public static String currencyCode = "QR";

    /**initiated order printed default status */
    public static boolean isOrderPrinted = false;

    /**initiated device identity */
    public static boolean isMobile = false;

    /**initiated table service default */
    public static boolean isTableService = false;

    /**initiated isKOTParsing default */
    public static boolean isKOTParsing = false;

    /**initiated isKOTParsing default */
    public static boolean isPOSSyncing = false;

}

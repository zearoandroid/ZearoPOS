package com.zearoconsulting.zearopos.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zearoconsulting.zearopos.presentation.model.BPartner;
import com.zearoconsulting.zearopos.presentation.model.Category;
import com.zearoconsulting.zearopos.presentation.model.Customer;
import com.zearoconsulting.zearopos.presentation.model.KOTHeader;
import com.zearoconsulting.zearopos.presentation.model.KOTLineItems;
import com.zearoconsulting.zearopos.presentation.model.Organization;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.POSOrders;
import com.zearoconsulting.zearopos.presentation.model.POSPayment;
import com.zearoconsulting.zearopos.presentation.model.Product;
import com.zearoconsulting.zearopos.presentation.model.Role;
import com.zearoconsulting.zearopos.presentation.model.Tables;
import com.zearoconsulting.zearopos.presentation.model.Terminals;
import com.zearoconsulting.zearopos.presentation.model.Warehouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saravanan on 05-03-2017.
 */

public class POSDataSource {
    // Database fields
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    //organization table
    private static final String TABLE_ORGANIZATION = "organization";

    //warehouse table
    private static final String TABLE_WAREHOUSE = "warehouse";

    // role table
    private static final String TABLE_ROLE = "role";

    // role table
    private static final String TABLE_ROLE_ACESS = "roleAccess";

    // category table
    private static final String TABLE_CATEGORY = "category";

    // product table
    private static final String TABLE_PRODUCT = "product";

    // BPartner table
    private static final String TABLE_BPARTNERS = "businessPartner";

    // BPartner table
    private static final String TABLE_SUPERVISOR = "supervisor";

    // pos order number table
    private static final String TABLE_POS_ORDER_NUMBER = "posOrderNumber";

    // pos order header table
    private static final String TABLE_POS_ORDER_HEADER = "posOrderHeader";

    // pos line item detail table
    private static final String TABLE_POS_LINES = "posLineItems";

    // pos payment detail table
    private static final String TABLE_POS_PAYMENT_DETAIL = "posPaymentDetail";

    // kot tables table
    private static final String TABLE_KOT_TABLE = "kotTables";

    // kot terminals table
    private static final String TABLE_KOT_TERMINALS = "kotTerminals";

    // kot header table
    private static final String TABLE_KOT_HEADER = "kotHeader";

    // kot line item detail table
    private static final String TABLE_KOT_LINES = "kotLineItems";

    //COLUMNS
    private static final String KEY_ID = "_id";
    private static final String KEY_ORG_ID = "orgId";
    private static final String KEY_CLIENT_ID = "clientId";
    private static final String KEY_ORG_NAME = "orgName";
    private static final String KEY_ORG_ARABIC_NAME = "orgArabicName";
    private static final String KEY_ORG_IMAGE = "orgImage";
    private static final String KEY_ORG_PHONE = "orgPhone";
    private static final String KEY_ORG_EMAIL = "orgEmail";
    private static final String KEY_ORG_ADDRESS = "orgAddress";
    private static final String KEY_ORG_CITY = "orgCity";
    private static final String KEY_ORG_COUNTRY = "orgCountry";
    private static final String KEY_ORG_WEB_URL = "orgWebUrl";
    private static final String KEY_ORG_DESCRIPTION = "orgDescription";
    private static final String KEY_ORG_FOOTER = "orgFooter";

    private static final String KEY_WAREHOUSE_ID = "warehouseId";
    private static final String KEY_WAREHOUSE_NAME = "warehouseName";

    private static final String KEY_ROLE_ID = "roleId";
    private static final String KEY_ROLE_NAME = "roleName";

    private static final String KEY_CATEGORY_ID = "categoryId";
    private static final String KEY_CATEGORY_NAME = "categoryName";
    private static final String KEY_CATEGORY_VALUE = "categoryValue";
    private static final String KEY_CATEGORY_IMAGE = "categoryImage";
    private static final String KEY_SHOWN_DIGITAL_MENU = "shownDigitalMenu";

    private static final String KEY_POS_ID = "posId";
    private static final String KEY_BP_ID = "bpId";
    private static final String KEY_CUSTOMER_NAME = "customerName";
    private static final String KEY_PRICELIST_ID = "pricelistId";
    private static final String KEY_CUSTOMER_VALUE = "value";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NUMBER = "mobilenumber";
    private static final String KEY_IS_CASH_CUSTOMER = "isCashCustomer";
    private static final String KEY_IS_CREDIT = "isCredit";
    private static final String KEY_CREDIT_LIMIT = "creditLimit";

    private static final String KEY_PRODUCT_ID = "productId";
    private static final String KEY_PRODUCT_NAME = "productName";
    private static final String KEY_PRODUCT_ARABIC_NAME = "productArabicName";
    private static final String KEY_PRODUCT_VALE = "productValue";
    private static final String KEY_PRODUCT_UOM_ID = "uomId";
    private static final String KEY_PRODUCT_UOM_VALUE = "uomValue";
    private static final String KEY_PRODUCT_QTY = "qty";
    private static final String KEY_PRODUCT_STD_PRICE = "stdPrice";
    private static final String KEY_PRODUCT_COST_PRICE = "costPrice";
    private static final String KEY_PRODUCT_TOTAL_PRICE = "totalPrice";
    private static final String KEY_PRODUCT_IMAGE = "productImage";
    private static final String KEY_PRODUCT_DISCOUNT_TYPE = "productDiscType";
    private static final String KEY_PRODUCT_DISCOUNT_VALUE = "productDiscValue";
    private static final String KEY_TOTAL_DISCOUNT_TYPE = "totalDiscType";
    private static final String KEY_TOTAL_DISCOUNT_VALUE = "totalDiscValue";
    private static final String KEY_PRODUCT_VIDEO = "productVideo";
    private static final String KEY_PRODUCT_CALORIES = "calories";
    private static final String KEY_PRODUCT_PREPARATION_TIME = "preparationTime";
    private static final String KEY_PRODUCT_DESCRIPTION = "description";
    private static final String KEY_PRODUCT_TERMINAL_ID = "terminalId";

    private static final String KEY_IS_DEFAULT = "isDefault";
    private static final String KEY_IS_UPDATED = "isUpdated";
    private static final String KEY_IS_POSTED = "isPosted";
    private static final String KEY_IS_SERVER_POSTED = "isServerPosted";
    private static final String KEY_IS_KOT = "isKOT";
    private static final String KEY_KOT_TYPE = "kotType";
    private static final String KEY_ORDER_TYPE = "orderType";
    private static final String KEY_IS_KOT_GENERATED = "isKOTGenerated";
    private static final String KEY_IS_ORDER_AVAILABLE = "isOrderAvailable";
    private static final String KEY_IS_PRINTED = "isPrinted";
    private static final String KEY_IS_LINE_DISCOUNTED = "isLineDiscounted";
    private static final String KEY_IS_TOTAL_DISCOUNTED = "isTotalDiscounted";
    private static final String KEY_AUTHORIZE_NAME = "authorizeName";
    private static final String KEY_AUTHORIZE_CODE = "authorizeCode";
    private static final String KEY_USER_ID = "userId";

    private static final String KEY_PAYMENT_CASH = "paymentCash";
    private static final String KEY_PAYMENT_AMEX = "paymentAmex";
    private static final String KEY_PAYMENT_GIFT = "paymentGift";
    private static final String KEY_PAYMENT_MASTER = "paymentMaster";
    private static final String KEY_PAYMENT_VISA = "paymentVisa";
    private static final String KEY_PAYMENT_OTHER = "paymentOther";
    private static final String KEY_PAYMENT_RETURN = "paymentReturn";

    private static final String KEY_START_NUMBER = "startNumber";
    private static final String KEY_END_NUMBER = "endNumber";

    private static final String KEY_KOT_TABLE_ID = "kotTableId";
    private static final String KEY_KOT_LINE_ID = "kotLineId";
    private static final String KEY_KOT_TABLE_NAME = "kotTableName";

    private static final String KEY_KOT_TERMINAL_ID = "kotTerminalId";
    private static final String KEY_KOT_TERMINAL_NAME = "kotTerminalName";
    private static final String KEY_KOT_TERMINAL_IP = "kotTerminalIP";
    private static final String KEY_KOT_IS_PRINTER = "kotIsPrinter";

    private static final String KEY_KOT_NUMBER = "kotNumber";
    private static final String KEY_INVOICE_NUMBER = "invoiceNumber";
    private static final String KEY_KOT_TOTAL_AMOUNT = "kotTotalAmount";
    private static final String KEY_KOT_ORDER_BY = "kotOrderBy";
    private static final String KEY_IS_SELECTED = "isSelected";
    private static final String KEY_KOT_ITEM_NOTES = "kotItemNotes";
    private static final String KEY_KOT_REF_LINE_ID = "kotRefLineId";
    private static final String KEY_KOT_EXTRA_PRODUCT = "isExtraProduct";
    private static final String KEY_KOT_COVERS_COUNT = "kotCoversCount";

    public POSDataSource(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public SQLiteDatabase getDb(){
        return  db;
    }

    //******************** POS MASTER RELATED STUFF'S METHODS (ORG, ROLE, WAREHOUSE, BPARTNER, CATEGORY, PRODUCT, AUTHORIZE) ***********************************//

    public void addOrganization(Organization org) {

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CLIENT_ID, org.getClientId());
            values.put(KEY_ORG_ID, org.getOrgId());
            values.put(KEY_ORG_NAME, org.getOrgName());
            values.put(KEY_ORG_ARABIC_NAME, org.getOrgArabicName());
            values.put(KEY_ORG_IMAGE, org.getOrgImage());
            values.put(KEY_ORG_ADDRESS, org.getOrgAddress());
            values.put(KEY_ORG_PHONE, org.getOrgPhone());
            values.put(KEY_ORG_EMAIL, org.getOrgEmail());
            values.put(KEY_ORG_CITY, org.getOrgCity());
            values.put(KEY_ORG_COUNTRY, org.getOrgCountry());
            values.put(KEY_ORG_WEB_URL, org.getOrgWebUrl());
            values.put(KEY_ORG_DESCRIPTION, org.getOrgDescription());
            values.put(KEY_ORG_FOOTER, org.getOrgFooter());
            values.put(KEY_IS_DEFAULT, org.getIsDefault());

            // Inserting Row
            db.insert(TABLE_ORGANIZATION, null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param orgId
     * @param warehouseId
     * @param warehouseName
     */
    public void addWarehouse(long clientId, long orgId, long warehouseId, String warehouseName, String isDefault) {

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CLIENT_ID, clientId);
            values.put(KEY_ORG_ID, orgId);
            values.put(KEY_WAREHOUSE_ID, warehouseId);
            values.put(KEY_WAREHOUSE_NAME, warehouseName);
            values.put(KEY_IS_DEFAULT, isDefault);

            // Inserting Row
            db.insert(TABLE_WAREHOUSE, null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param roleId
     * @param roleName
     */
    public void addRole(long roleId, String roleName, String isDefault) {

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ROLE_ID, roleId);
            values.put(KEY_ROLE_NAME, roleName);
            values.put(KEY_IS_DEFAULT, isDefault);

            // Inserting Row
            db.insert(TABLE_ROLE, null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param roleId
     * @param orgId
     */
    public void addRoleAccess(long clientId, long orgId, long roleId) {

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CLIENT_ID, clientId);
            values.put(KEY_ORG_ID, orgId);
            values.put(KEY_ROLE_ID, roleId);

            // Inserting Row
            db.insert(TABLE_ROLE_ACESS, null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void addAuthorizeId(long clientId, long orgId, long userId, String authorizeName, String authorizeId) {

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            Cursor mCount = db.rawQuery("select authorizeCode from supervisor where authorizeCode='" + authorizeId + "'", null);
            //mCount.moveToFirst();
            String msrCardValue = "-1";

            while (mCount.moveToNext()) {
                msrCardValue = mCount.getString(0);
            }
            mCount.close();

            if (msrCardValue.equalsIgnoreCase("-1")) {
                ContentValues values = new ContentValues();
                values.put(KEY_CLIENT_ID, clientId);
                values.put(KEY_ORG_ID, orgId);
                values.put(KEY_USER_ID, userId);
                values.put(KEY_AUTHORIZE_NAME, authorizeName);
                values.put(KEY_AUTHORIZE_CODE, authorizeId);

                // Inserting Row
                db.insert(TABLE_SUPERVISOR, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param tables
     */
    public void addTables(Tables tables) {

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CLIENT_ID, tables.getClientId());
            values.put(KEY_ORG_ID, tables.getOrgId());
            values.put(KEY_KOT_TABLE_ID, tables.getTableId());
            values.put(KEY_KOT_TABLE_NAME, tables.getTableName());
            values.put(KEY_IS_ORDER_AVAILABLE, tables.getOrderAvailable());

            // Inserting Row
            db.insert(TABLE_KOT_TABLE, null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param terminals
     */
    public void addTerminals(Terminals terminals) {

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CLIENT_ID, terminals.getClientId());
            values.put(KEY_ORG_ID, terminals.getOrgId());
            values.put(KEY_KOT_TERMINAL_ID, terminals.getTerminalId());
            values.put(KEY_KOT_TERMINAL_NAME, terminals.getTerminalName());
            values.put(KEY_KOT_TERMINAL_IP, terminals.getTerminalIP());
            values.put(KEY_KOT_IS_PRINTER, terminals.getIsPrinter());

            // Inserting Row
            db.insert(TABLE_KOT_TERMINALS, null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void addCategory(Category category) {

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            Cursor mCount = db.rawQuery("select categoryId from category where categoryId='" + category.getCategoryId() + "'", null);
            //mCount.moveToFirst();
            long categoryId = 0;

            while (mCount.moveToNext()) {
                categoryId = mCount.getLong(0);
            }
            mCount.close();

            if (categoryId == 0) {

                ContentValues values = new ContentValues();
                values.put(KEY_CLIENT_ID, category.getClientId());
                values.put(KEY_ORG_ID, category.getOrgId());
                values.put(KEY_CATEGORY_ID, category.getCategoryId());
                values.put(KEY_CATEGORY_NAME, category.getCategoryName());
                values.put(KEY_CATEGORY_VALUE, category.getCategoryValue());
                values.put(KEY_CATEGORY_IMAGE, category.getCategoryImage());
                values.put(KEY_SHOWN_DIGITAL_MENU, category.getShowDigitalMenu());

                // Inserting Row
                db.insert(TABLE_CATEGORY, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param categoryId
     * @param product
     */
    public void addProduct(long categoryId, Product product) {

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            Cursor mCount = db.rawQuery("select productId  from product where categoryId='" + categoryId + "' and productId='" + product.getProdId() + "'", null);
            //mCount.moveToFirst();
            long productId = 0;

            while (mCount.moveToNext()) {
                productId = mCount.getLong(0);
            }
            mCount.close();

            if (productId == 0) {
                ContentValues values = new ContentValues();
                values.put(KEY_CLIENT_ID, product.getClientId());//KEY_CLIENT_ID
                values.put(KEY_ORG_ID, product.getOrgId());//KEY_ORG_ID
                values.put(KEY_CATEGORY_ID, categoryId);//KEY_CATEGORY_ID
                values.put(KEY_PRODUCT_ID, product.getProdId());//KEY_PRODUCT_ID
                values.put(KEY_PRODUCT_NAME, product.getProdName());//KEY_PRODUCT_NAME
                values.put(KEY_PRODUCT_VALE, product.getProdValue());//KEY_PRODUCT_VALE
                values.put(KEY_PRODUCT_UOM_ID, product.getUomId());//KEY_PRODUCT_UOM_ID
                values.put(KEY_PRODUCT_UOM_VALUE, product.getUomValue());//KEY_PRODUCT_UOM_VALUE
                values.put(KEY_PRODUCT_QTY, 1);//KEY_PRODUCT_QTY
                values.put(KEY_PRODUCT_STD_PRICE, product.getSalePrice());//KEY_PRODUCT_STD_PRICE
                values.put(KEY_PRODUCT_COST_PRICE, product.getCostPrice());//KEY_PRODUCT_COST_PRICE
                values.put(KEY_PRODUCT_IMAGE, product.getProdImage());//KEY_PRODUCT_IMAGE
                values.put(KEY_PRODUCT_ARABIC_NAME, product.getProdArabicName());//KEY_PRODUCT_ARABIC_NAME
                values.put(KEY_PRODUCT_DESCRIPTION, product.getDescription());//KEY_PRODUCT_DESCRIPTION
                values.put(KEY_SHOWN_DIGITAL_MENU, product.getShowDigitalMenu());//KEY_SHOWN_DIGITAL_MENU
                values.put(KEY_PRODUCT_VIDEO, product.getProdVideoPath());//KEY_PRODUCT_VIDEO
                values.put(KEY_PRODUCT_CALORIES, product.getCalories());//KEY_PRODUCT_CALORIES
                values.put(KEY_PRODUCT_PREPARATION_TIME, product.getPreparationTime());//KEY_PRODUCT_PREPARATION_TIME
                values.put(KEY_PRODUCT_TERMINAL_ID, product.getTerminalId());//KEY_PRODUCT_TERMINAL_ID

                // Inserting Row
                db.insert(TABLE_PRODUCT, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     *
     * @param bPartner
     */
    public void addBPartner(BPartner bPartner) {

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            Cursor mCount = db.rawQuery("select bpId  from businessPartner where bpId='" + bPartner.getBpId() + "'", null);
            //mCount.moveToFirst();
            long bPartnerId = 0;

            while (mCount.moveToNext()) {
                bPartnerId = mCount.getLong(0);
                String updateSQL = "update businessPartner set creditLimit='"+bPartner.getCreditLimit()+"', isCredit='" + bPartner.getIsCredit() +"' where bpId='" + bPartner.getBpId() + "';";
                db.execSQL(updateSQL);
            }
            mCount.close();

            if (bPartnerId == 0) {
                ContentValues values = new ContentValues();
                values.put(KEY_CLIENT_ID, bPartner.getClientId());
                values.put(KEY_ORG_ID, bPartner.getOrgId());
                values.put(KEY_BP_ID, bPartner.getBpId());
                values.put(KEY_CUSTOMER_NAME, bPartner.getBpName());
                values.put(KEY_CUSTOMER_VALUE, bPartner.getBpValue());
                values.put(KEY_PRICELIST_ID, bPartner.getBpPriceListId());
                values.put(KEY_EMAIL, bPartner.getBpEmail());
                values.put(KEY_NUMBER, bPartner.getBpNumber());
                values.put(KEY_IS_CREDIT, bPartner.getIsCredit());
                values.put(KEY_CREDIT_LIMIT, bPartner.getCreditLimit());

                // Inserting Row
                db.insert(TABLE_BPARTNERS, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    public void addPOSPayments(POSPayment payment) {

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_POS_ID, payment.getPosId());
            values.put(KEY_PAYMENT_CASH, payment.getCash());
            values.put(KEY_PAYMENT_AMEX, payment.getAmex());
            values.put(KEY_PAYMENT_GIFT, payment.getGift());
            values.put(KEY_PAYMENT_MASTER, payment.getMaster());
            values.put(KEY_PAYMENT_VISA, payment.getVisa());
            values.put(KEY_PAYMENT_OTHER, payment.getOther());
            values.put(KEY_PAYMENT_RETURN, payment.getChange());

            // Inserting Row
            db.insert(TABLE_POS_PAYMENT_DETAIL, null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public List<String> getAuthorizeId(long orgId) {

        List<String> authIds = new ArrayList<>();
        try {
            Cursor mCursor = db.rawQuery("select authorizeCode from supervisor where orgId='" + orgId + "'", null);
            while (mCursor.moveToNext()) {
                authIds.add(mCursor.getString(0));
            }
            mCursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return authIds;
    }

    public long getAuthorizedUserId(long authorizeId){

        long userId = 0;

        try {
            Cursor mCursor = db.rawQuery("select userId from supervisor where authorizeCode='" + authorizeId + "'", null);

            while (mCursor.moveToNext()) {
                userId = mCursor.getLong(0);
            }
            mCursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return  userId;
    }

    public POSPayment getPaymentDetails(long posId) {

        POSPayment payment = null;

        try {
            Cursor cursor = db.rawQuery("select * from posPaymentDetail where posId = '" + posId + "' ", null);

            while (cursor.moveToNext()) {
                payment = new POSPayment();
                payment.setPosId(cursor.getLong(1));
                payment.setCash(cursor.getDouble(2));
                payment.setAmex(cursor.getDouble(3));
                payment.setGift(cursor.getDouble(4));
                payment.setMaster(cursor.getDouble(5));
                payment.setVisa(cursor.getDouble(6));
                payment.setOther(cursor.getDouble(7));
                payment.setChange(cursor.getDouble(8));
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return payment;
    }

    public Organization getOrganizationDetail(long orgId) {

        Organization org = null;

        try {
            Cursor cursor = db.rawQuery("select * from organization where orgId ='" + orgId + "'", null);

            while (cursor.moveToNext()) {
                org = new Organization();
                org.setClientId(cursor.getLong(1));
                org.setOrgId(cursor.getLong(2));
                org.setOrgName(cursor.getString(3));
                org.setOrgArabicName(cursor.getString(4));
                org.setOrgImage(cursor.getString(5));
                org.setOrgAddress(cursor.getString(6));
                org.setOrgPhone(cursor.getString(7));
                org.setOrgEmail(cursor.getString(8));
                org.setOrgCity(cursor.getString(9));
                org.setOrgCountry(cursor.getString(10));
                org.setOrgWebUrl(cursor.getString(11));
                org.setOrgDescription(cursor.getString(12));
                org.setOrgFooter(cursor.getString(13));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return org;
    }

    /**
     * @return orgList
     */
    public List<Organization> getOrganizations() {

        List<Organization> orgList = new ArrayList<Organization>();
        Organization org = null;

        try {
            Cursor cursor = db.rawQuery("select orgId, orgName from organization ", null);

            while (cursor.moveToNext()) {
                org = new Organization();
                org.setOrgId(cursor.getLong(0));
                org.setOrgName(cursor.getString(1));

                orgList.add(org);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return orgList;
    }

    /**
     * @param roleId
     * @return mOrgIdList
     */
    public List<Long> getOrgId(long roleId) {
        long orgId = 0;
        List<Long> mOrgIdList = null;

        try {
            Cursor cursor = db.rawQuery("select orgId from roleAccess where roleId='" + roleId + "'", null);

            mOrgIdList = new ArrayList<Long>();
            while (cursor.moveToNext()) {
                mOrgIdList.add(cursor.getLong(0));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return mOrgIdList;
    }

    /**
     * @param orgId
     * @return org
     */
    public Organization getOrgDetails(long orgId) {

        Organization org = null;

        try {
            Cursor cursor = db.rawQuery("select orgId, orgName, orgFooter from organization where orgId='" + orgId + "'", null);

            while (cursor.moveToNext()) {
                org = new Organization();
                org.setOrgId(cursor.getLong(0));
                org.setOrgName(cursor.getString(1));
                org.setOrgFooter(cursor.getString(2));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return org;
    }

    /**
     * @return roleList
     */
    public List<Role> getRole() {
        List<Role> roleList = new ArrayList<Role>();
        Role role = null;

        try {
            Cursor cursor = db.rawQuery("select roleId, roleName from role", null);

            while (cursor.moveToNext()) {
                role = new Role();
                role.setRoleId(cursor.getLong(0));
                role.setRoleName(cursor.getString(1));

                roleList.add(role);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return roleList;
    }

    /**
     * @param orgId
     * @return warehouseList
     */
    public List<Warehouse> getWarehouse(long orgId) {
        List<Warehouse> warehouseList = new ArrayList<Warehouse>();
        Warehouse warehouse = null;

        try {
            Cursor cursor = db.rawQuery("select warehouseId, warehouseName from warehouse where orgId='" + orgId + "' ", null);

            while (cursor.moveToNext()) {
                warehouse = new Warehouse();
                warehouse.setWarehouseId(cursor.getLong(0));
                warehouse.setWarehouseName(cursor.getString(1));

                warehouseList.add(warehouse);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return warehouseList;
    }

    public List<Long> getDefaultIds() {

        String isDefault = "Y";
        List<Long> defaultIdList = new ArrayList<Long>();
        List<Long> mRoleIdList;

        long orgId = 0;

        try {
            //get the default orgid
            Cursor cursor = db.rawQuery("select orgId from organization where isdefault='" + isDefault + "' ", null);
            while (cursor.moveToNext()) {
                orgId = cursor.getLong(0);
                defaultIdList.add(orgId);
            }
            cursor.close();

            //get the default warehouse id
            Cursor cursor2 = db.rawQuery("select warehouseId from warehouse where orgId='" + orgId + "' and isdefault='" + isDefault + "'", null);
            while (cursor2.moveToNext()) {
                defaultIdList.add(cursor2.getLong(0));
            }
            cursor2.close();

            //get the all role id based on org
            Cursor cursor3 = db.rawQuery("select roleId from roleAccess where orgId='" + orgId + "'", null);
            mRoleIdList = new ArrayList<Long>();
            while (cursor3.moveToNext()) {
                mRoleIdList.add(cursor3.getLong(0));
            }
            cursor3.close();

            for (int i = 0; i < mRoleIdList.size(); i++) {
                //get the default role id
                Cursor cursor4 = db.rawQuery("select roleId from role where isdefault='" + isDefault + "'", null);
                while (cursor4.moveToNext()) {
                    defaultIdList.add(cursor4.getLong(0));
                }
                cursor4.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return defaultIdList;
    }

    /**
     * @return mBPList
     */
    public List<BPartner> getBPartners(long clientId, long orgId) {

        List<BPartner> mBPList = new ArrayList<BPartner>();

        BPartner bPartner = null;

        try {
            Cursor cursor = db.rawQuery("select * from businessPartner where clientId = '" + clientId + "' and orgId = '" + orgId + "'", null);

            while (cursor.moveToNext()) {
                bPartner = new BPartner();
                bPartner.setClientId(cursor.getLong(1));
                bPartner.setOrgId(cursor.getLong(2));
                bPartner.setBpId(cursor.getLong(3));
                bPartner.setBpName(cursor.getString(4));
                bPartner.setBpValue(cursor.getString(5));
                bPartner.setBpPriceListId(cursor.getLong(6));
                bPartner.setBpEmail(cursor.getString(7));
                bPartner.setBpNumber(cursor.getLong(8));
                bPartner.setIsCredit(cursor.getString(9));
                bPartner.setCreditLimit(cursor.getDouble(10));

                mBPList.add(bPartner);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return mBPList;
    }

    public BPartner getBPartner(long clientId, long orgId, long bpId) {

        BPartner bPartner = null;

        try {
            Cursor cursor = db.rawQuery("select * from businessPartner where clientId = '" + clientId + "' and orgId = '" + orgId + "' and bpId = '"+bpId+"' ", null);

            while (cursor.moveToNext()) {
                bPartner = new BPartner();
                bPartner.setClientId(cursor.getLong(1));
                bPartner.setOrgId(cursor.getLong(2));
                bPartner.setBpId(cursor.getLong(3));
                bPartner.setBpName(cursor.getString(4));
                bPartner.setBpValue(cursor.getString(5));
                bPartner.setBpPriceListId(cursor.getLong(6));
                bPartner.setBpEmail(cursor.getString(7));
                bPartner.setBpNumber(cursor.getLong(8));
                bPartner.setIsCredit(cursor.getString(9));
                bPartner.setCreditLimit(cursor.getDouble(10));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return bPartner;
    }

    /**
     * @return
     */
    public List<Category> getCategory(long clientId, long orgId) {
        List<Category> categoryList = new ArrayList<Category>();
        Category category = null;

        try {
            Cursor cursor = db.rawQuery("select categoryId, categoryName, categoryValue, categoryImage from category where clientId = '" + clientId + "' and orgId = '" + orgId + "' ORDER BY categoryName ASC", null);

            while (cursor.moveToNext()) {
                category = new Category();
                category.setCategoryId(cursor.getLong(0));
                category.setCategoryName(cursor.getString(1));
                category.setCategoryValue(cursor.getString(2));
                category.setCategoryImage(cursor.getString(3));

                Cursor cur = db.rawQuery("select * from product where categoryId = '" + cursor.getLong(0) + "' and clientId = '" + clientId + "' and orgId = '" + orgId + "' ORDER BY productName COLLATE NOCASE ASC ", null);
                int cnt = cur.getCount();
                cur.close();

                if (cnt > 0)
                    categoryList.add(category);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return categoryList;
    }

    public List<Tables> getTables(long clientId, long orgId) {
        List<Tables> tableList = new ArrayList<Tables>();
        Tables table = null;

        try {
            Cursor cursor = db.rawQuery("select kotTableId, kotTableName, isOrderAvailable from kotTables where clientId = '" + clientId + "' and orgId = '" + orgId + "' ORDER BY kotTableId ASC", null);

            while (cursor.moveToNext()) {
                table = new Tables();
                table.setTableId(cursor.getLong(0));
                table.setTableName(cursor.getString(1));
                table.setOrderAvailable(cursor.getString(2));
                tableList.add(table);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return tableList;
    }

    public List<String> getTableIDList(long clientId, long orgId) {
        List<String> tableList = new ArrayList<String>();

        try {
            Cursor cursor = db.rawQuery("select kotTableId from kotTables where clientId = '" + clientId + "' and orgId = '" + orgId + "' ORDER BY kotTableId ASC", null);

            while (cursor.moveToNext()) {
                tableList.add(String.valueOf(cursor.getLong(0)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return tableList;
    }

    public Tables getTableData(long clientId, long orgId, long tableId) {

        Tables table = null;

        try {
            Cursor cursor = db.rawQuery("select kotTableId, kotTableName, isOrderAvailable from kotTables where clientId = '" + clientId + "' and orgId = '" + orgId + "' and kotTableId = '" + tableId + "' ", null);

            while (cursor.moveToNext()) {
                table = new Tables();
                table.setTableId(cursor.getLong(0));
                table.setTableName(cursor.getString(1));
                table.setOrderAvailable(cursor.getString(2));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return table;
    }

    /**
     * @param terminalId
     * @return
     */
    public Terminals getTerminalData(long clientId, long orgId, long terminalId) {

        Terminals terminals = null;

        try {
            Cursor cursor = db.rawQuery("select kotTerminalId, kotTerminalName, kotTerminalIP, kotIsPrinter from kotTerminals where clientId = '" + clientId + "' and orgId = '" + orgId + "' and kotTerminalId = '" + terminalId + "' ", null);

            while (cursor.moveToNext()) {
                terminals = new Terminals();
                terminals.setTerminalId(cursor.getLong(0));
                terminals.setTerminalName(cursor.getString(1));
                terminals.setTerminalIP(cursor.getString(2));
                terminals.setIsPrinter(cursor.getString(3));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return terminals;
    }

    public Terminals getInvoiceTerminalData(long clientId, long orgId) {

        Terminals terminals = null;

        try {
            Cursor cursor = db.rawQuery("select kotTerminalId, kotTerminalName, kotTerminalIP from kotTerminals where clientId = '" + clientId + "' and orgId = '" + orgId + "' and kotTerminalName = 'POS' ", null);

            while (cursor.moveToNext()) {
                terminals = new Terminals();
                terminals.setTerminalId(cursor.getLong(0));
                terminals.setTerminalName(cursor.getString(1));
                terminals.setTerminalIP(cursor.getString(2));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return terminals;
    }


    public String getCategoryImage(long clientId, long orgId, long categoryId) {

        String imgPath = "";

        try {
            Cursor cursor = db.rawQuery("select productImage from product where clientId = '" + clientId + "' and orgId = '" + orgId + "' and categoryId = '" + categoryId + "' ", null);

            while (cursor.moveToNext()) {
                imgPath = cursor.getString(0);
                break;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return imgPath;

    }

    /**
     * @param categoryId
     * @return
     */
    public List<Product> getProducts(long clientId, long orgId, long categoryId) {

        List<Product> productList = new ArrayList<Product>();
        Product product = null;

        try {
            Cursor cursor = db.rawQuery("select * from product where clientId = '" + clientId + "' and orgId = '" + orgId + "' and categoryId = '" + categoryId + "' ORDER BY productName COLLATE NOCASE ASC ", null);

            while (cursor.moveToNext()) {
                product = new Product();
                product.setClientId(cursor.getLong(1)); //KEY_CLIENT_ID
                product.setOrgId(cursor.getLong(2)); //KEY_ORG_ID
                product.setCategoryId(cursor.getLong(3)); //KEY_CATEGORY_ID
                product.setProdId(cursor.getLong(4)); //KEY_PRODUCT_ID
                product.setProdName(cursor.getString(5));//KEY_PRODUCT_NAME
                product.setProdValue(cursor.getString(6));//KEY_PRODUCT_VALE
                product.setUomId(cursor.getLong(7));//KEY_PRODUCT_UOM_ID
                product.setUomValue(cursor.getString(8));//KEY_PRODUCT_UOM_VALUE
                //KEY_PRODUCT_QTY
                product.setSalePrice(cursor.getDouble(10));//KEY_PRODUCT_STD_PRICE
                product.setCostPrice(cursor.getDouble(11));//KEY_PRODUCT_COST_PRICE
                product.setProdImage(cursor.getString(12));//KEY_PRODUCT_IMAGE
                product.setProdArabicName(cursor.getString(13));//KEY_PRODUCT_ARABIC_NAME
                product.setDescription(cursor.getString(14));//KEY_PRODUCT_DESCRIPTION
                product.setShowDigitalMenu(cursor.getString(15));//KEY_SHOWN_DIGITAL_MENU
                product.setProdVideoPath(cursor.getString(16));//KEY_PRODUCT_VIDEO
                product.setCalories(cursor.getString(17));//KEY_PRODUCT_CALORIES
                product.setPreparationTime(cursor.getString(18));//KEY_PRODUCT_PREPARATION_TIME
                product.setTerminalId(cursor.getLong(19));//KEY_PRODUCT_TERMINAL_ID
                product.setDefaultQty(0);

                productList.add(product);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return productList;
    }

    public List<Long> getProductIDs(long clientId, long orgId, long categoryId) {

        List<Long> productIDList = new ArrayList<Long>();
        long productId = 0;

        try {
            Cursor cursor = db.rawQuery("select productId from product where clientId = '" + clientId + "' and orgId = '" + orgId + "' and categoryId = '" + categoryId + "' ORDER BY productName COLLATE NOCASE ASC ", null);

            while (cursor.moveToNext()) {
                productId = cursor.getLong(0);
                productIDList.add(productId);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return productIDList;
    }


    public Product getProduct(long clientId, long orgId, String prodId) {

        Product product = null;

        try {
            Cursor cursor = db.rawQuery("select * from product where clientId = '" + clientId + "' and orgId = '" + orgId + "' and productValue = '" + prodId + "' ", null);

            while (cursor.moveToNext()) {
                product = new Product();
                product.setClientId(cursor.getLong(1)); //KEY_CLIENT_ID
                product.setOrgId(cursor.getLong(2)); //KEY_ORG_ID
                product.setCategoryId(cursor.getLong(3)); //KEY_CATEGORY_ID
                product.setProdId(cursor.getLong(4)); //KEY_PRODUCT_ID
                product.setProdName(cursor.getString(5));//KEY_PRODUCT_NAME
                product.setProdValue(cursor.getString(6));//KEY_PRODUCT_VALE
                product.setUomId(cursor.getLong(7));//KEY_PRODUCT_UOM_ID
                product.setUomValue(cursor.getString(8));//KEY_PRODUCT_UOM_VALUE
                //KEY_PRODUCT_QTY
                product.setSalePrice(cursor.getDouble(10));//KEY_PRODUCT_STD_PRICE
                product.setCostPrice(cursor.getDouble(11));//KEY_PRODUCT_COST_PRICE
                product.setProdImage(cursor.getString(12));//KEY_PRODUCT_IMAGE
                product.setProdArabicName(cursor.getString(13));//KEY_PRODUCT_ARABIC_NAME
                product.setDescription(cursor.getString(14));//KEY_PRODUCT_DESCRIPTION
                product.setShowDigitalMenu(cursor.getString(15));//KEY_SHOWN_DIGITAL_MENU
                product.setProdVideoPath(cursor.getString(16));//KEY_PRODUCT_VIDEO
                product.setCalories(cursor.getString(17));//KEY_PRODUCT_CALORIES
                product.setPreparationTime(cursor.getString(18));//KEY_PRODUCT_PREPARATION_TIME
                product.setTerminalId(cursor.getLong(19));//KEY_PRODUCT_TERMINAL_ID
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return product;
    }

    /**
     * @param prodId
     * @return
     */
    public Product getProduct(long clientId, long orgId, long prodId) {

        Product product = null;

        try {
            Cursor cursor = db.rawQuery("select * from product where clientId = '" + clientId + "' and orgId = '" + orgId + "' and productId = '" + prodId + "' ", null);

            while (cursor.moveToNext()) {
                product = new Product();
                product.setClientId(cursor.getLong(1)); //KEY_CLIENT_ID
                product.setOrgId(cursor.getLong(2)); //KEY_ORG_ID
                product.setCategoryId(cursor.getLong(3)); //KEY_CATEGORY_ID
                product.setProdId(cursor.getLong(4)); //KEY_PRODUCT_ID
                product.setProdName(cursor.getString(5));//KEY_PRODUCT_NAME
                product.setProdValue(cursor.getString(6));//KEY_PRODUCT_VALE
                product.setUomId(cursor.getLong(7));//KEY_PRODUCT_UOM_ID
                product.setUomValue(cursor.getString(8));//KEY_PRODUCT_UOM_VALUE
                //KEY_PRODUCT_QTY
                product.setSalePrice(cursor.getDouble(10));//KEY_PRODUCT_STD_PRICE
                product.setCostPrice(cursor.getDouble(11));//KEY_PRODUCT_COST_PRICE
                product.setProdImage(cursor.getString(12));//KEY_PRODUCT_IMAGE
                product.setProdArabicName(cursor.getString(13));//KEY_PRODUCT_ARABIC_NAME
                product.setDescription(cursor.getString(14));//KEY_PRODUCT_DESCRIPTION
                product.setShowDigitalMenu(cursor.getString(15));//KEY_SHOWN_DIGITAL_MENU
                product.setProdVideoPath(cursor.getString(16));//KEY_PRODUCT_VIDEO
                product.setCalories(cursor.getString(17));//KEY_PRODUCT_CALORIES
                product.setPreparationTime(cursor.getString(18));//KEY_PRODUCT_PREPARATION_TIME
                product.setTerminalId(cursor.getLong(19));//KEY_PRODUCT_TERMINAL_ID
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return product;
    }

    /**
     * @return
     */
    public List<Product> getAllProduct(long clientId, long orgId) {

        List<Product> productList = new ArrayList<Product>();
        Product product = null;

        try {
            Cursor cursor = db.rawQuery("select * from product where clientId = '" + clientId + "' and orgId = '" + orgId + "' ORDER BY productName COLLATE NOCASE ASC", null);

            while (cursor.moveToNext()) {
                product = new Product();
                product.setClientId(cursor.getLong(1)); //KEY_CLIENT_ID
                product.setOrgId(cursor.getLong(2)); //KEY_ORG_ID
                product.setCategoryId(cursor.getLong(3)); //KEY_CATEGORY_ID
                product.setProdId(cursor.getLong(4)); //KEY_PRODUCT_ID
                product.setProdName(cursor.getString(5));//KEY_PRODUCT_NAME
                product.setProdValue(cursor.getString(6));//KEY_PRODUCT_VALE
                product.setUomId(cursor.getLong(7));//KEY_PRODUCT_UOM_ID
                product.setUomValue(cursor.getString(8));//KEY_PRODUCT_UOM_VALUE
                //KEY_PRODUCT_QTY
                product.setSalePrice(cursor.getDouble(10));//KEY_PRODUCT_STD_PRICE
                product.setCostPrice(cursor.getDouble(11));//KEY_PRODUCT_COST_PRICE
                product.setProdImage(cursor.getString(12));//KEY_PRODUCT_IMAGE
                product.setProdArabicName(cursor.getString(13));//KEY_PRODUCT_ARABIC_NAME
                product.setDescription(cursor.getString(14));//KEY_PRODUCT_DESCRIPTION
                product.setShowDigitalMenu(cursor.getString(15));//KEY_SHOWN_DIGITAL_MENU
                product.setProdVideoPath(cursor.getString(16));//KEY_PRODUCT_VIDEO
                product.setCalories(cursor.getString(17));//KEY_PRODUCT_CALORIES
                product.setPreparationTime(cursor.getString(18));//KEY_PRODUCT_PREPARATION_TIME
                product.setTerminalId(cursor.getLong(19));//KEY_PRODUCT_TERMINAL_ID

                productList.add(product);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return productList;
    }

    //******************** POS ORDER RELATED STUFF'S METHODS (ADD, GET, UPDATE, DELETE TO POSORDERHEADER AND POSLINEITEMS) ***********************************//

    /**
     * @param posId
     * @param bPId
     * @param name
     * @param pricelistId
     * @param value
     * @param email
     * @param mobile
     */
    public void addPOSHeader(long posId, long bPId, String name, long pricelistId, String value, String email, long mobile, int isCashCustomer, String isKOT) {

        boolean isAvail = false;

        db.beginTransaction();
        try {

            Cursor mCount = db.rawQuery("select bpId from posOrderHeader where posId='" + posId + "'", null);

            while (mCount.moveToNext()) {

                if (bPId != mCount.getLong(0)) {
                    String updateSQL = "update posOrderHeader set bpId='" + bPId + "', customerName='" + name + "' where posId='" + posId + "';";
                    db.execSQL(updateSQL);
                }
                isAvail = true;
            }
            mCount.close();

            if (isAvail) {

                Log.i("ADD POS CUSTOMER", "Already inserted");
                return;

            } else {
                ContentValues values = new ContentValues();
                values.put(KEY_POS_ID, posId);
                values.put(KEY_BP_ID, bPId);
                values.put(KEY_CUSTOMER_NAME, name);
                values.put(KEY_PRICELIST_ID, pricelistId);
                values.put(KEY_CUSTOMER_VALUE, value);
                values.put(KEY_EMAIL, email);
                values.put(KEY_NUMBER, mobile);
                values.put(KEY_TOTAL_DISCOUNT_TYPE, 0);
                values.put(KEY_TOTAL_DISCOUNT_VALUE, 0);
                values.put(KEY_IS_CASH_CUSTOMER, isCashCustomer);
                values.put(KEY_IS_TOTAL_DISCOUNTED, "N");
                values.put(KEY_IS_POSTED, "N");
                values.put(KEY_IS_PRINTED, "N");
                values.put(KEY_IS_KOT, isKOT);
                values.put(KEY_IS_SERVER_POSTED, "N");

                // Inserting Row
                db.insert(TABLE_POS_ORDER_HEADER, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void addPOSLineItem(long posId, long kotLineId, Product product, int qty) {


        db.beginTransaction();
        try {

            Cursor mCount = db.rawQuery("select qty, productDiscType, productDiscValue from posLineItems where posId='" + posId + "' and isKOTGenerated='N' and productId='" + product.getProdId() + "'", null);
            //mCount.moveToFirst();
            int exQty = 0;
            int prodDiscType = 0;
            int prodDiscValue = 0;
            String isupdated = "Y";
            double totalPrice;
            while (mCount.moveToNext()) {
                exQty = mCount.getInt(0);
                prodDiscType = mCount.getInt(1);
                prodDiscValue = mCount.getInt(2);
            }
            mCount.close();

            if (exQty != 0) {

                String updateSQL = "update posLineItems set isUpdated='N' where posId='" + posId + "';";
                db.execSQL(updateSQL);

                qty = qty + exQty;
                totalPrice = qty * product.getSalePrice();
                if (prodDiscType == 0) {
                    totalPrice = totalPrice - (totalPrice * prodDiscValue / 100);
                } else {
                    totalPrice = totalPrice - prodDiscValue;
                }

                String strSQL = "update posLineItems set isUpdated='Y', qty='" + qty
                        + "', totalPrice='" + totalPrice + "', productDiscValue='" + prodDiscValue + "' where posId='"
                        + posId + "' and isKOTGenerated='N' and productId = '" + product.getProdId() + "';";
                db.execSQL(strSQL);

            } else {
                try {
                    String updateSQL = "update posLineItems set isUpdated='N' where posId='" + posId + "';";
                    db.execSQL(updateSQL);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ContentValues values = new ContentValues();
                values.put(KEY_POS_ID, posId);
                values.put(KEY_KOT_LINE_ID, kotLineId);
                values.put(KEY_PRODUCT_TERMINAL_ID, product.getTerminalId());
                values.put(KEY_CATEGORY_ID, product.getCategoryId());
                values.put(KEY_PRODUCT_ID, product.getProdId());
                values.put(KEY_PRODUCT_NAME, product.getProdName());
                values.put(KEY_PRODUCT_ARABIC_NAME, product.getProdArabicName());
                values.put(KEY_PRODUCT_VALE, product.getProdValue());
                values.put(KEY_PRODUCT_UOM_ID, product.getUomId());
                values.put(KEY_PRODUCT_UOM_VALUE, product.getUomValue());
                values.put(KEY_PRODUCT_QTY, qty);
                values.put(KEY_PRODUCT_STD_PRICE, product.getSalePrice());
                values.put(KEY_PRODUCT_COST_PRICE, product.getCostPrice());
                values.put(KEY_PRODUCT_DISCOUNT_TYPE, 0);
                values.put(KEY_PRODUCT_DISCOUNT_VALUE, 0);
                totalPrice = qty * product.getSalePrice();
                values.put(KEY_PRODUCT_TOTAL_PRICE, totalPrice);
                values.put(KEY_IS_LINE_DISCOUNTED, "N");
                values.put(KEY_IS_UPDATED, "N");
                values.put(KEY_IS_POSTED, "N");
                values.put(KEY_IS_KOT_GENERATED, "N");
                values.put(KEY_IS_SELECTED, "Y");

                values.put(KEY_KOT_ITEM_NOTES, "");
                values.put(KEY_KOT_REF_LINE_ID, 0);
                values.put(KEY_KOT_EXTRA_PRODUCT, "N");

                // Inserting Row
                db.insert(TABLE_POS_LINES, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void addKOTtoPOSLineItem(long posId, KOTLineItems kotLineItems, Product product) {

        db.beginTransaction();
        try {

            Cursor mCount = db.rawQuery("select kotLineId from posLineItems where posId='" + posId + "' and kotLineId='" + kotLineItems.getKotLineId() + "' and productId='" + kotLineItems.getProduct().getProdId() + "'", null);
            //mCount.moveToFirst();
            long kotLineId = -1;

            while (mCount.moveToNext()) {
                kotLineId = mCount.getLong(0);
            }
            mCount.close();

            if (kotLineId == -1) {
                ContentValues values = new ContentValues();
                values.put(KEY_POS_ID, posId);
                values.put(KEY_KOT_LINE_ID, kotLineItems.getKotLineId());
                values.put(KEY_PRODUCT_TERMINAL_ID, product.getTerminalId());
                values.put(KEY_CATEGORY_ID, product.getCategoryId());
                values.put(KEY_PRODUCT_ID, product.getProdId());
                values.put(KEY_PRODUCT_NAME, product.getProdName());
                values.put(KEY_PRODUCT_ARABIC_NAME, product.getProdArabicName());
                values.put(KEY_PRODUCT_VALE, product.getProdValue());
                values.put(KEY_PRODUCT_UOM_ID, product.getUomId());
                values.put(KEY_PRODUCT_UOM_VALUE, product.getUomValue());
                values.put(KEY_PRODUCT_QTY, kotLineItems.getQty());
                values.put(KEY_PRODUCT_STD_PRICE, product.getSalePrice());
                values.put(KEY_PRODUCT_COST_PRICE, product.getCostPrice());
                values.put(KEY_PRODUCT_DISCOUNT_TYPE, 0);
                values.put(KEY_PRODUCT_DISCOUNT_VALUE, 0);
                values.put(KEY_PRODUCT_TOTAL_PRICE, kotLineItems.getTotalPrice());
                values.put(KEY_IS_LINE_DISCOUNTED, "N");
                values.put(KEY_IS_UPDATED, "N");
                values.put(KEY_IS_POSTED, "N");
                values.put(KEY_IS_KOT_GENERATED, "Y");
                values.put(KEY_IS_SELECTED, "Y");

                values.put(KEY_KOT_ITEM_NOTES, kotLineItems.getNotes());
                values.put(KEY_KOT_REF_LINE_ID, kotLineItems.getRefRowId());
                values.put(KEY_KOT_EXTRA_PRODUCT, kotLineItems.getIsExtraProduct());

                // Inserting Row
                db.insert(TABLE_POS_LINES, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public POSOrders getPosHeader(long posId) {

        POSOrders posOrder = null;

        try {
            Cursor cursor = db.rawQuery("select bpId, customerName, isCashCustomer, isPrinted, isKOT from posOrderHeader where posId='" + posId + "';", null);

            while (cursor.moveToNext()) {
                posOrder = new POSOrders();
                posOrder.setBpId(cursor.getLong(0));
                posOrder.setCustomerName(cursor.getString(1));
                posOrder.setIsCashCustomer(cursor.getInt(2));
                posOrder.setIsPrinted(cursor.getString(3));
                posOrder.setIsKOT(cursor.getString(4));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return posOrder;
    }

    public Customer getPOSCustomer(long posId) {

        Customer customer = null;

        try {
            Cursor cursor = db.rawQuery("select * from posOrderHeader where posId = '" + posId + "' ", null);

            while (cursor.moveToNext()) {
                customer = new Customer();
                customer.setPosId(cursor.getLong(1));
                customer.setBpId(cursor.getLong(2));
                customer.setCustomerName(cursor.getString(3));
                customer.setPriceListId(cursor.getLong(4));
                customer.setCustomerValue(cursor.getLong(5));
                customer.setEmail(cursor.getString(6));
                customer.setMobile(cursor.getLong(7));
                customer.setIsCashCustomer(cursor.getInt(8));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return customer;
    }

    public int getTotalDiscType(long posId) {

        int totalDiscType = 0;
        try {
            Cursor mCount = db.rawQuery("select totalDiscType from posOrderHeader where posId='" + posId + "' ", null);
            while (mCount.moveToNext()) {
                totalDiscType = mCount.getInt(0);
                break;
            }
            mCount.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalDiscType;
    }

    public int getTotalDiscValue(long posId) {

        int totalDiscValue = 0;
        try {
            Cursor mCount = db.rawQuery("select totalDiscValue from posOrderHeader where posId='" + posId + "' ", null);
            while (mCount.moveToNext()) {
                totalDiscValue = mCount.getInt(0);
                break;
            }
            mCount.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalDiscValue;
    }

    public List<POSOrders> getPOSOrders() {
        List<POSOrders> posOrdersList = new ArrayList<POSOrders>();
        POSOrders posOrders = null;

        try {
            Cursor cursor = db.rawQuery("select posId, customerName, isCashCustomer from posOrderHeader where isPosted='N' ", null);

            posOrders = new POSOrders();
            posOrders.setPosId(0);
            posOrders.setCustomerName("");
            posOrders.setIsCashCustomer(0);
            posOrders.setOrderTotalAmt(0);
            posOrders.setOrderTotalQty(0);
            posOrders.setOrderType("CounterSale");

            posOrdersList.add(posOrders);

            while (cursor.moveToNext()) {
                posOrders = new POSOrders();
                posOrders.setPosId(cursor.getLong(0));
                posOrders.setCustomerName(cursor.getString(1));
                posOrders.setIsCashCustomer(cursor.getInt(2));
                posOrders.setOrderTotalAmt(0);
                posOrders.setOrderTotalQty(0);
                posOrders.setOrderType("CounterSale");

                posOrdersList.add(posOrders);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return posOrdersList;
    }

    public List<POSOrders> getNotPostedPOSOrders() {
        List<POSOrders> posOrdersList = new ArrayList<POSOrders>();
        POSOrders posOrders = null;

        try {
            Cursor cursor = db.rawQuery("select posId, customerName, isCashCustomer from posOrderHeader where isPosted='Y' and isServerPosted = 'N'", null);

            while (cursor.moveToNext()) {
                posOrders = new POSOrders();
                posOrders.setPosId(cursor.getLong(0));
                posOrders.setCustomerName(cursor.getString(1));
                posOrders.setIsCashCustomer(cursor.getInt(2));
                posOrders.setOrderTotalAmt(0);
                posOrders.setOrderTotalQty(0);
                posOrders.setOrderType("CounterSale");

                posOrdersList.add(posOrders);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return posOrdersList;
    }

    public List<POSOrders> getPostedPOSOrders() {
        List<POSOrders> posOrdersList = new ArrayList<POSOrders>();
        POSOrders posOrders = null;

        try {
            Cursor cursor = db.rawQuery("select posId, customerName, isCashCustomer from posOrderHeader where isPosted='Y' ORDER BY posId DESC LIMIT 5", null);

            while (cursor.moveToNext()) {
                posOrders = new POSOrders();
                posOrders.setPosId(cursor.getLong(0));
                posOrders.setCustomerName(cursor.getString(1));
                posOrders.setIsCashCustomer(cursor.getInt(2));
                posOrders.setOrderTotalAmt(0);
                posOrders.setOrderTotalQty(0);
                posOrders.setOrderType("CounterSale");

                posOrdersList.add(posOrders);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return posOrdersList;
    }

    public List<POSLineItem> getAllPOSLineItems(long posId) {

        List<POSLineItem> posLineItemList = new ArrayList<POSLineItem>();
        POSLineItem posLineItems = null;
        Cursor cursor;

        try {

            cursor = db.rawQuery("select * from posLineItems where posId = '" + posId + "' ", null);

            while (cursor.moveToNext()) {
                posLineItems = new POSLineItem();
                posLineItems.setRowId(cursor.getLong(0));
                posLineItems.setPosId(cursor.getLong(1));
                posLineItems.setKotLineId(cursor.getLong(2));
                posLineItems.setTerminalId(cursor.getLong(3));
                posLineItems.setCategoryId(cursor.getLong(4));
                posLineItems.setProductId(cursor.getLong(5));
                posLineItems.setProductName(cursor.getString(6));
                posLineItems.setProdArabicName(cursor.getString(7));
                posLineItems.setProductValue(cursor.getString(8));
                posLineItems.setPosUOMId(cursor.getLong(9));
                posLineItems.setPosUOMValue(cursor.getString(10));
                posLineItems.setPosQty(cursor.getInt(11));
                posLineItems.setStdPrice(cursor.getDouble(12));
                posLineItems.setCostPrice(cursor.getDouble(13));
                posLineItems.setDiscType(cursor.getInt(14));
                posLineItems.setDiscValue(cursor.getDouble(15));
                posLineItems.setTotalPrice(cursor.getDouble(16));
                posLineItems.setIsLineDiscounted(cursor.getString(17));
                posLineItems.setIsUpdated(cursor.getString(18));
                posLineItems.setIsPosted(cursor.getString(19));
                posLineItems.setIsKOTGenerated(cursor.getString(20));
                posLineItems.setSelected(cursor.getString(21));
                posLineItems.setNotes(cursor.getString(22));
                posLineItems.setRefRowId(cursor.getLong(23));
                posLineItems.setIsExtraProduct(cursor.getString(24));

                posLineItemList.add(posLineItems);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return posLineItemList;
    }

    /**
     * @param posId
     * @return posLineItemList
     */
    public List<POSLineItem> getPOSLineItems(long posId, long categoryId) {

        List<POSLineItem> posLineItemList = new ArrayList<POSLineItem>();
        POSLineItem posLineItems = null;
        Cursor cursor;

        try {
            if (categoryId == 0)
                cursor = db.rawQuery("select * from posLineItems where posId = '" + posId + "' and isExtraProduct='N' ", null);
            else
                cursor = db.rawQuery("select * from posLineItems where posId = '" + posId + "' and categoryId = '" + categoryId + "' and isExtraProduct='N' ", null);

            while (cursor.moveToNext()) {
                posLineItems = new POSLineItem();
                posLineItems.setRowId(cursor.getLong(0));
                posLineItems.setPosId(cursor.getLong(1));
                posLineItems.setKotLineId(cursor.getLong(2));
                posLineItems.setTerminalId(cursor.getLong(3));
                posLineItems.setCategoryId(cursor.getLong(4));
                posLineItems.setProductId(cursor.getLong(5));
                posLineItems.setProductName(cursor.getString(6));
                posLineItems.setProdArabicName(cursor.getString(7));
                posLineItems.setProductValue(cursor.getString(8));
                posLineItems.setPosUOMId(cursor.getLong(9));
                posLineItems.setPosUOMValue(cursor.getString(10));
                posLineItems.setPosQty(cursor.getInt(11));
                posLineItems.setStdPrice(cursor.getDouble(12));
                posLineItems.setCostPrice(cursor.getDouble(13));
                posLineItems.setDiscType(cursor.getInt(14));
                posLineItems.setDiscValue(cursor.getDouble(15));
                posLineItems.setTotalPrice(cursor.getDouble(16));
                posLineItems.setIsLineDiscounted(cursor.getString(17));
                posLineItems.setIsUpdated(cursor.getString(18));
                posLineItems.setIsPosted(cursor.getString(19));
                posLineItems.setIsKOTGenerated(cursor.getString(20));
                posLineItems.setSelected(cursor.getString(21));
                posLineItems.setNotes(cursor.getString(22));
                posLineItems.setRefRowId(cursor.getLong(23));
                posLineItems.setIsExtraProduct(cursor.getString(24));

                posLineItemList.add(posLineItems);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return posLineItemList;
    }


    public List<POSLineItem> getPOSExtraLineItems(long refId) {

        List<POSLineItem> posLineItemList = new ArrayList<POSLineItem>();
        POSLineItem posLineItems = null;
        Cursor cursor;

        try {
            cursor = db.rawQuery("select * from posLineItems where kotRefLineId = '" + refId + "' and isExtraProduct='Y' ", null);

            while (cursor.moveToNext()) {
                posLineItems = new POSLineItem();
                posLineItems.setRowId(cursor.getLong(0));
                posLineItems.setPosId(cursor.getLong(1));
                posLineItems.setKotLineId(cursor.getLong(2));
                posLineItems.setTerminalId(cursor.getLong(3));
                posLineItems.setCategoryId(cursor.getLong(4));
                posLineItems.setProductId(cursor.getLong(5));
                posLineItems.setProductName(cursor.getString(6));
                posLineItems.setProdArabicName(cursor.getString(7));
                posLineItems.setProductValue(cursor.getString(8));
                posLineItems.setPosUOMId(cursor.getLong(9));
                posLineItems.setPosUOMValue(cursor.getString(10));
                posLineItems.setPosQty(cursor.getInt(11));
                posLineItems.setStdPrice(cursor.getDouble(12));
                posLineItems.setCostPrice(cursor.getDouble(13));
                posLineItems.setDiscType(cursor.getInt(14));
                posLineItems.setDiscValue(cursor.getDouble(15));
                posLineItems.setTotalPrice(cursor.getDouble(16));
                posLineItems.setIsLineDiscounted(cursor.getString(17));
                posLineItems.setIsUpdated(cursor.getString(18));
                posLineItems.setIsPosted(cursor.getString(19));
                posLineItems.setIsKOTGenerated(cursor.getString(20));
                posLineItems.setSelected(cursor.getString(21));
                posLineItems.setNotes(cursor.getString(22));
                posLineItems.setRefRowId(cursor.getLong(23));
                posLineItems.setIsExtraProduct(cursor.getString(24));

                posLineItemList.add(posLineItems);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return posLineItemList;
    }

    /**
     * this method used for MultiEditorFragment class for discount
     *
     * @param posId
     * @return
     */
    public List<POSLineItem> getPOSLineItems(long posId) {

        List<POSLineItem> posLineItemList = new ArrayList<POSLineItem>();
        POSLineItem posLineItems = null;
        Cursor cursor;

        try {

            cursor = db.rawQuery("select * from posLineItems where posId = '" + posId + "' and isLineDiscounted = 'N' and isExtraProduct='N' ", null);

            while (cursor.moveToNext()) {
                posLineItems = new POSLineItem();
                posLineItems.setRowId(cursor.getLong(0));
                posLineItems.setPosId(cursor.getLong(1));
                posLineItems.setKotLineId(cursor.getLong(2));
                posLineItems.setTerminalId(cursor.getLong(3));
                posLineItems.setCategoryId(cursor.getLong(4));
                posLineItems.setProductId(cursor.getLong(5));
                posLineItems.setProductName(cursor.getString(6));
                posLineItems.setProdArabicName(cursor.getString(7));
                posLineItems.setProductValue(cursor.getString(8));
                posLineItems.setPosUOMId(cursor.getLong(9));
                posLineItems.setPosUOMValue(cursor.getString(10));
                posLineItems.setPosQty(cursor.getInt(11));
                posLineItems.setStdPrice(cursor.getDouble(12));
                posLineItems.setCostPrice(cursor.getDouble(13));
                posLineItems.setDiscType(cursor.getInt(14));
                posLineItems.setDiscValue(cursor.getDouble(15));
                posLineItems.setTotalPrice(cursor.getDouble(16));
                posLineItems.setIsLineDiscounted(cursor.getString(17));
                posLineItems.setIsUpdated(cursor.getString(18));
                posLineItems.setIsPosted(cursor.getString(19));
                posLineItems.setIsKOTGenerated(cursor.getString(20));
                posLineItems.setSelected(cursor.getString(21));
                posLineItems.setNotes(cursor.getString(22));
                posLineItems.setRefRowId(cursor.getLong(23));
                posLineItems.setIsExtraProduct(cursor.getString(24));

                posLineItemList.add(posLineItems);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return posLineItemList;
    }


    public POSLineItem getPOSLineItem(long posId, long rowId) {

        POSLineItem posLineItems = null;
        Cursor cursor;

        try {

            cursor = db.rawQuery("select * from posLineItems where posId = '" + posId + "' and _id='" + rowId + "' ", null);

            while (cursor.moveToNext()) {
                posLineItems = new POSLineItem();
                posLineItems.setRowId(cursor.getLong(0));
                posLineItems.setPosId(cursor.getLong(1));
                posLineItems.setKotLineId(cursor.getLong(2));
                posLineItems.setTerminalId(cursor.getLong(3));
                posLineItems.setCategoryId(cursor.getLong(4));
                posLineItems.setProductId(cursor.getLong(5));
                posLineItems.setProductName(cursor.getString(6));
                posLineItems.setProdArabicName(cursor.getString(7));
                posLineItems.setProductValue(cursor.getString(8));
                posLineItems.setPosUOMId(cursor.getLong(9));
                posLineItems.setPosUOMValue(cursor.getString(10));
                posLineItems.setPosQty(cursor.getInt(11));
                posLineItems.setStdPrice(cursor.getDouble(12));
                posLineItems.setCostPrice(cursor.getDouble(13));
                posLineItems.setDiscType(cursor.getInt(14));
                posLineItems.setDiscValue(cursor.getDouble(15));
                posLineItems.setTotalPrice(cursor.getDouble(16));
                posLineItems.setIsLineDiscounted(cursor.getString(17));
                posLineItems.setIsUpdated(cursor.getString(18));
                posLineItems.setIsPosted(cursor.getString(19));
                posLineItems.setIsKOTGenerated(cursor.getString(20));
                posLineItems.setSelected(cursor.getString(21));
                posLineItems.setNotes(cursor.getString(22));
                posLineItems.setRefRowId(cursor.getLong(23));
                posLineItems.setIsExtraProduct(cursor.getString(24));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return posLineItems;
    }

    /**
     * @param posId
     * @param terminalId
     * @return
     */
    public List<Product> getCounterSaleKOTLineItems(long posId, long terminalId) {

        List<Product> itemList = new ArrayList<Product>();


        try {
            Cursor cursor = db.rawQuery("select * from posLineItems where posId='" + posId + "' and isKOTGenerated='N' and terminalId = '" + terminalId + "' and isExtraProduct='N' ", null);

            while (cursor.moveToNext()) {

                Product product = new Product();

                /*posLineItems.setRowId(cursor.getLong(0));
                posLineItems.setPosId(cursor.getLong(1));
                posLineItems.setKotLineId(cursor.getLong(2));
                posLineItems.setTerminalId(cursor.getLong(3));
                posLineItems.setCategoryId(cursor.getLong(4));
                posLineItems.setProductId(cursor.getLong(5));
                posLineItems.setProductName(cursor.getString(6));
                posLineItems.setProdArabicName(cursor.getString(7));
                posLineItems.setProductValue(cursor.getString(8));
                posLineItems.setPosUOMId(cursor.getLong(9));
                posLineItems.setPosUOMValue(cursor.getString(10));
                posLineItems.setPosQty(cursor.getInt(11));
                posLineItems.setStdPrice(cursor.getDouble(12));
                posLineItems.setCostPrice(cursor.getDouble(13));
                posLineItems.setDiscType(cursor.getInt(14));
                posLineItems.setDiscValue(cursor.getInt(15));
                posLineItems.setTotalPrice(cursor.getDouble(16));
                posLineItems.setIsDiscounted(cursor.getString(17));
                posLineItems.setIsUpdated(cursor.getString(18));
                posLineItems.setIsPosted(cursor.getString(19));
                posLineItems.setIsKOTGenerated(cursor.getString(20));
                posLineItems.setSelected(cursor.getString(21));
                posLineItems.setNotes(cursor.getString(22));
                posLineItems.setRefRowId(cursor.getLong(23));
                posLineItems.setIsExtraProduct(cursor.getString(24));*/

                product.setCategoryId(cursor.getLong(4));
                product.setProdId(cursor.getLong(5));
                product.setProdName(cursor.getString(6));
                product.setProdArabicName(cursor.getString(7));
                product.setProdValue(cursor.getString(8));
                product.setUomId(cursor.getLong(9));
                product.setUomValue(cursor.getString(10));
                product.setQty(cursor.getInt(11));
                product.setSalePrice(cursor.getDouble(12));
                product.setCostPrice(cursor.getDouble(13));
                product.setTotalPrice(cursor.getDouble(16));
                product.setDescription("");

                itemList.add(product);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return itemList;
    }

    public int getProdDiscType(long rowId, long posId, long productId) {


        int prodDiscType = 0;
        try {
            Cursor mCount = db.rawQuery("select productDiscType from posLineItems where _id='" + rowId + "' and posId='" + posId + "' and productId='" + productId + "'", null);
            while (mCount.moveToNext()) {
                prodDiscType = mCount.getInt(0);
            }
            mCount.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return prodDiscType;
    }

    public int getProdDiscValue(long rowId, long posId, long productId) {


        int prodDiscValue = 0;
        try {
            Cursor mCount = db.rawQuery("select productDiscValue from posLineItems where _id='" + rowId + "' posId='" + posId + "' and productId='" + productId + "'", null);
            while (mCount.moveToNext()) {
                prodDiscValue = mCount.getInt(0);
            }
            mCount.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return prodDiscValue;
    }

    public int getProdQty(long posId, long productId) {


        int qty = 0;
        try {
            Cursor mCount = db.rawQuery("select qty from posLineItems where posId='" + posId + "' and productId='" + productId + "'", null);
            while (mCount.moveToNext()) {
                qty = mCount.getInt(0);
            }
            mCount.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return qty;
    }

    /**
     * @param posId
     * @return
     */
    public Map<String, Object> sumOfProductPriceWithoutDiscount(long posId) {


        Map<String, Object> dict = null;

        try {
            Cursor cursor = db.rawQuery("select sum(qty), sum(totalPrice) from posLineItems where posId = '" + posId + "' and isLineDiscounted = 'N' ", null);

            dict = new HashMap<String, Object>();
            while (cursor.moveToNext()) {
                dict.put("QTY", cursor.getInt(0));
                dict.put("TOTAL", cursor.getDouble(1));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return dict;
    }

    /**
     * This method used for totalPrice for bill without discount
     *
     * @param posId
     * @return
     */
    public double sumOfProductsWithoutDiscount(long posId) {


        double total = 0;

        try {
            Cursor cursor = db.rawQuery("select sum(qty*stdPrice) from posLineItems where posId = '" + posId + "' GROUP BY posId ", null);

            while (cursor.moveToNext()) {
                total = cursor.getDouble(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return total;
    }

    /**
     * This method used for totalPrice of bill with discount
     *
     * @param posId
     * @return
     */
    public double sumOfProductsTotalPrice(long posId) {
        double total = 0;

        try {
            Cursor cursor = db.rawQuery("select sum(totalPrice) from posLineItems where posId = '" + posId + "' ", null);

            while (cursor.moveToNext()) {
                total = cursor.getDouble(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return total;
    }

    public double sumOfKOTProductsTotalPrice(long kotNumber, long posId) {
        double total = 0;

        try {
            Cursor cursor = db.rawQuery("select sum(totalPrice) from kotLineItems where kotNumber = '" + kotNumber + "' and invoiceNumber = '" + posId + "' GROUP BY kotNumber", null);

            while (cursor.moveToNext()) {
                total = cursor.getDouble(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return total;
    }

    public double sumOfKOTProductsTotalPrice(long tableId) {
        double total = 0;

        try {
            Cursor cursor = db.rawQuery("select sum(totalPrice) from kotLineItems where kotTableId = '" + tableId + "'", null);

            while (cursor.moveToNext()) {
                total = cursor.getDouble(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return total;
    }

    /**
     * This method is used for totalQty of bill
     * @param posId
     * @return
     */
    public int sumOfProductsTotalQty(long posId) {
        int qty = 0;

        try {
            Cursor cursor = db.rawQuery("select sum(qty) from posLineItems where posId = '" + posId + "' and isExtraProduct='N' ", null);

            while (cursor.moveToNext()) {
                qty = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return qty;
    }

    /**
     * @param posId
     * @param terminalId
     * @return
     */
    public double sumOfCounterSalesTerminalItemsTotal(long posId, long terminalId) {
        double totalPrice = 0;

        try {
            Cursor cursor = db.rawQuery("select sum(totalPrice) from posLineItems where posId = '" + posId + "' and terminalId = '" + terminalId + "' and isKOTGenerated='N' ", null);

            while (cursor.moveToNext()) {
                totalPrice = cursor.getDouble(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return totalPrice;
    }

    public Map<String, Object> sumOfProductPriceWithDiscount(long posId) {


        Map<String, Object> dict = null;

        try {
            Cursor cursor = db.rawQuery("select sum(qty), sum(totalPrice) from posLineItems where posId = '" + posId + "' and isLineDiscounted = 'Y' ", null);

            dict = new HashMap<String, Object>();
            while (cursor.moveToNext()) {
                dict.put("QTY", cursor.getInt(0));
                dict.put("TOTAL", cursor.getDouble(1));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return dict;
    }

    public List<String> getCounterSaleKOTItemTerminals(long posId) {

        List<String> kotTerminalList = new ArrayList<String>();

        try {
            Cursor cursor = db.rawQuery("select terminalId from posLineItems where posId = '" + posId + "' and isKOTGenerated='N' GROUP BY terminalId ", null);
            while (cursor.moveToNext()) {
                kotTerminalList.add(String.valueOf(cursor.getLong(0)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return kotTerminalList;
    }


    public void removePOSLineItem(long posId, long productId, double stdPrice, int qty) {

        try {

            Cursor mCount = db.rawQuery("select qty, productDiscType, productDiscValue from posLineItems where posId='" + posId + "' and productId='" + productId + "'", null);
            //mCount.moveToFirst();
            int exQty = 0;
            int prodDiscType = 0;
            int prodDiscValue = 0;
            double totalPrice = 0;
            while (mCount.moveToNext()) {
                exQty = mCount.getInt(0);
                prodDiscType = mCount.getInt(1);
                prodDiscValue = mCount.getInt(2);
            }
            mCount.close();

            if (exQty != 0) {
                qty = exQty - qty;
                if (qty == 0) {
                    db.execSQL("delete from posLineItems where posId = '" + posId + "' and productId = '" + productId + "'");
                } else {
                    totalPrice = qty * stdPrice;
                    if (prodDiscType != 0) {
                        totalPrice = totalPrice - prodDiscValue;
                    } else {
                        totalPrice = totalPrice - (totalPrice * prodDiscValue / 100);
                    }

                    String strSQL = "update posLineItems set isUpdated='Y', qty='" + qty
                            + "', totalPrice='" + totalPrice + "', productDiscValue='" + prodDiscValue + "' where posId='"
                            + posId + "' and productId = '" + productId + "';";
                    db.execSQL(strSQL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void removePOSLineItemFromCart(long posId, long rowId, long productId, double stdPrice, int qty) {

        try {

            Cursor mCount = db.rawQuery("select qty, productDiscType, productDiscValue from posLineItems where _id='" + rowId + "' and posId='" + posId + "' and productId='" + productId + "'", null);
            //mCount.moveToFirst();
            int exQty = 0;
            int prodDiscType = 0;
            int prodDiscValue = 0;
            double totalPrice = 0;
            while (mCount.moveToNext()) {
                exQty = mCount.getInt(0);
                prodDiscType = mCount.getInt(1);
                prodDiscValue = mCount.getInt(2);
            }
            mCount.close();

            if (exQty != 0) {
                qty = exQty - qty;
                if (qty == 0) {
                    db.execSQL("delete from posLineItems where _id='" + rowId + "' and posId = '" + posId + "' and productId = '" + productId + "'");
                } else {
                    totalPrice = qty * stdPrice;
                    if (prodDiscType != 0) {
                        totalPrice = totalPrice - prodDiscValue;
                    } else {
                        totalPrice = totalPrice - (totalPrice * prodDiscValue / 100);
                    }

                    String strSQL = "update posLineItems set isUpdated='Y', qty='" + qty
                            + "', totalPrice='" + totalPrice + "', productDiscValue='" + prodDiscValue + "' where _id='" + rowId + "' and posId='"
                            + posId + "' and productId = '" + productId + "';";
                    db.execSQL(strSQL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updatePOSHeader(long posId, String isKOT) {

        try {
            String updateSQL = "update posOrderHeader set isKOT='" + isKOT + "' where posId='" + posId + "';";
            db.execSQL(updateSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updatePOSHeaderCustomer(long posId, BPartner bPartner) {

        try {
            String updateSQL = "update posOrderHeader set bpId='" + bPartner.getBpId() + "', customerName='" + bPartner.getBpName() + "' where posId='" + posId + "';";
            db.execSQL(updateSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void prePrintOrder(long posId, String status) {

        try {
            String strOrderSQL = "update posOrderHeader set isPrinted='" + status + "' where posId='" + posId + "' ;";
            db.execSQL(strOrderSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void qucikPrintOrder(long posId, String status) {

        try {
            String strOrderSQL = "update posOrderHeader set isPosted='" + status + "' where posId='" + posId + "' ;";
            db.execSQL(strOrderSQL);

            String strOrderSQL2 = "update posLineItems set isPosted='" + status + "' where posId='" + posId + "' ;";
            db.execSQL(strOrderSQL2);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updatePOSTotalAmount(long posId, int totalDiscType, double totalDiscValue) {

        try {
            String strSQL = "update posOrderHeader set totalDiscType='" + totalDiscType + "', totalDiscValue='" + totalDiscValue + "' where posId='" + posId + "' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updateSplitedLineItem(long oldPosId, long newPosId) {
        String strSQL;
        try {
            strSQL = "update posLineItems set posId='" + newPosId + "', isSelected ='Y' where posId='" + oldPosId + "' and isSelected ='N' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updatePOSLineItems(long rowId, long posId, long productId, int qty, int prodDiscType, double prodDiscValue, double totalPrice, String isLineDiscounted, String isTotalDiscounted) {


        try {
            String strSQL = "update posLineItems set isUpdated='Y', qty='" + qty
                    + "', isLineDiscounted='" + isLineDiscounted + "', totalPrice='" + totalPrice + "', productDiscType='" + prodDiscType + "', productDiscValue='" + prodDiscValue + "' where _id='" + rowId + "' and posId='"
                    + posId + "' and productId = '" + productId + "' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * If generated kot from counter sales
     *
     * @param posId
     * @param productId
     * @param isKOT
     */
    public void updatePOSLineItem(long posId, long productId, String isKOT) {

        try {
            String strSQL = "update posLineItems set isUpdated='Y', isKOTGenerated='" + isKOT
                    + "' where posId='" + posId + "' and productId = '" + productId + "' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * Update the line item based on productId and posId
     *
     * @param posId
     * @param productId
     * @param qty
     * @param totalPrice
     */
    public void updatePOSLineItem(long posId, long productId, int qty, int discount, double totalPrice) {

        try {
            String strSQL = "update posLineItems set isUpdated='Y', qty='" + qty
                    + "', totalPrice='" + totalPrice + "', discount='" + discount + "' where posId='"
                    + posId + "' and productId = '" + productId + "' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updatePOSLineItemStdPrice(long posId, long productId, double stdPrice, double totalPrice) {

        try {
            String strSQL = "update posLineItems set isUpdated='Y', stdPrice='" + stdPrice + "', totalPrice='" + totalPrice + "' where posId='"
                    + posId + "' and productId = '" + productId + "' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }


    public void updateCounterSaleKOTLineItems(long posId) {

        try {
            String strSQL = "update posLineItems set isKOTGenerated='Y' where posId = '" + posId + "' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updateKOTLineIdToPOSLineItem(long posId, long kotLineId, long prodId) {

        try {
            String strSQL = "update posLineItems set kotLineId='" + kotLineId + "' where posId = '" + posId + "' and productId = '" + prodId + "' and kotLineId=0 and isExtraProduct='N' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updateRelatedKOTLineIdToPOSLineItem(long posId, long kotLineId, long prodId) {

        try {
            String strSQL = "update posLineItems set kotLineId='" + kotLineId + "' where posId = '" + posId + "' and productId = '" + prodId + "' and kotLineId=0 and isExtraProduct='Y' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updatePOSLineItem(long posId) {

        try {
            String strSQL = "update posLineItems set posId='" + posId
                    + "' where posId='"
                    + 0 + "' ;";
            db.execSQL(strSQL);

            db.execSQL("delete from posOrderHeader where posId = '" + 0 + "'");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updateLineItemSelectedStatus(long posId, long productId, String isSelect) {
        String strSQL;
        try {
            strSQL = "update posLineItems set isSelected='" + isSelect + "' where posId='" + posId + "' and productId = '" + productId + "' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updateBPartnerCreditLimit(long bpid, double creditLimit) {
        String strSQL;
        try {
            strSQL = "update businessPartner set creditLimit = '" + creditLimit + "' where bpId='" + bpid + "' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void deletePOSLineItems(List<POSLineItem> lineItems) {

        try {
            for (int i = 0; i < lineItems.size(); i++) {
                db.execSQL("delete from posLineItems where posId = '" + lineItems.get(i).getPosId() + "' and productId = '" + lineItems.get(i).getProductId() + "' ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void releasePOSOrder(long posId) {

        try {
            String strOrderSQL = "update posOrderHeader set isPosted='Y' where posId='" + posId + "' ;";
            db.execSQL(strOrderSQL);

            String strLineSQL = "update posLineItems set isPosted='Y' where posId='" + posId + "' ;";
            db.execSQL(strLineSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void releasePOSOrderFromLocal(long posId, String status) {

        try {
            String strOrderSQL = "update posOrderHeader set isServerPosted='" + status + "' where posId='" + posId + "' ;";
            db.execSQL(strOrderSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    //******************** KOT RELATED STUFF'S METHODS (ADD, GET, UPDATE, DELETE TO KOTHEADER AND KOTLINEITEMS) ***********************************//

    public boolean addKOTHeader(KOTHeader kotHeader) {

        boolean inserted = false;

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            Cursor mCount = db.rawQuery("select kotTableId from kotHeader where kotNumber='" + kotHeader.getKotNumber() + "'", null);
            //mCount.moveToFirst();
            long kotTableId = -1;

            while (mCount.moveToNext()) {
                kotTableId = mCount.getLong(0);
            }
            mCount.close();

            if (kotTableId == -1) {
                ContentValues values = new ContentValues();
                values.put(KEY_KOT_TABLE_ID, kotHeader.getTablesId());
                values.put(KEY_KOT_NUMBER, kotHeader.getKotNumber());
                values.put(KEY_INVOICE_NUMBER, kotHeader.getInvoiceNumber());
                values.put(KEY_KOT_TERMINAL_ID, kotHeader.getTerminalId());
                values.put(KEY_KOT_TOTAL_AMOUNT, kotHeader.getTotalAmount());
                values.put(KEY_KOT_ORDER_BY, kotHeader.getOrderBy());
                values.put(KEY_KOT_TYPE, kotHeader.getKotType());
                values.put(KEY_ORDER_TYPE, kotHeader.getOrderType());
                values.put(KEY_IS_KOT, kotHeader.getIsKOT());
                values.put(KEY_IS_PRINTED, kotHeader.getPrinted());
                values.put(KEY_IS_POSTED, kotHeader.getPosted());
                values.put(KEY_IS_SELECTED, kotHeader.getSelected());
                values.put(KEY_KOT_COVERS_COUNT, kotHeader.getCoversCount());

                // Inserting Row
                db.insert(TABLE_KOT_HEADER, null, values);

                inserted = true;
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            inserted = false;
        } finally {
            db.endTransaction();
        }

        return inserted;
    }

    public void addKOTLineItems(KOTLineItems kotLineItems, Product product, int qty) {

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            double totalPrice = 0;

            ContentValues values = new ContentValues();

            values.put(KEY_KOT_TABLE_ID, kotLineItems.getTableId());
            values.put(KEY_KOT_LINE_ID, kotLineItems.getKotLineId());
            values.put(KEY_KOT_NUMBER, kotLineItems.getKotNumber());
            values.put(KEY_INVOICE_NUMBER, kotLineItems.getInvoiceNumber());
            values.put(KEY_CATEGORY_ID, product.getCategoryId());
            values.put(KEY_PRODUCT_ID, product.getProdId());
            values.put(KEY_PRODUCT_NAME, product.getProdName());
            values.put(KEY_PRODUCT_ARABIC_NAME, product.getProdArabicName());
            values.put(KEY_PRODUCT_VALE, product.getProdValue());
            values.put(KEY_PRODUCT_UOM_ID, product.getUomId());
            values.put(KEY_PRODUCT_UOM_VALUE, product.getUomValue());
            values.put(KEY_PRODUCT_STD_PRICE, product.getSalePrice());
            values.put(KEY_PRODUCT_COST_PRICE, product.getCostPrice());
            values.put(KEY_KOT_TERMINAL_ID, product.getTerminalId());

            values.put(KEY_PRODUCT_QTY, qty);
            totalPrice = qty * product.getSalePrice();
            values.put(KEY_PRODUCT_TOTAL_PRICE, totalPrice);
            values.put(KEY_KOT_ITEM_NOTES, kotLineItems.getNotes());
            values.put(KEY_IS_PRINTED, "N");
            values.put(KEY_IS_POSTED, "N");
            values.put(KEY_KOT_REF_LINE_ID, kotLineItems.getRefRowId());
            values.put(KEY_KOT_EXTRA_PRODUCT, kotLineItems.getIsExtraProduct());

            // Inserting Row
            db.insert(TABLE_KOT_LINES, null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public List<KOTHeader> getKOTTables(boolean printed) {
        List<KOTHeader> kotHeaderList = new ArrayList<KOTHeader>();
        KOTHeader kotHeader = null;
        Cursor cursor = null;
        try {
            if (printed)
                cursor = db.rawQuery("select kotTableId, invoiceNumber, sum(kotTotalAmount), isSelected, kotCoversCount from kotHeader where isPrinted='Y' and kotTableId <> 0 GROUP BY kotTableId ", null);
            else
                cursor = db.rawQuery("select kotTableId, invoiceNumber, sum(kotTotalAmount), isSelected, kotCoversCount from kotHeader where isPrinted='N' and kotTableId <> 0 GROUP BY kotTableId ", null);

            while (cursor.moveToNext()) {
                kotHeader = new KOTHeader();
                kotHeader.setTablesId(cursor.getLong(0));
                kotHeader.setInvoiceNumber(cursor.getLong(1));
                kotHeader.setTotalAmount(cursor.getDouble(2));
                kotHeader.setSelected(cursor.getString(3));
                kotHeader.setCoversCount(cursor.getInt(4));
                kotHeaderList.add(kotHeader);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return kotHeaderList;
    }

    public List<KOTHeader> getDataAvailableKOTTables() {

        List<KOTHeader> kotHeaderList = new ArrayList<KOTHeader>();
        KOTHeader kotHeader = null;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select kotTableId, sum(kotTotalAmount), isSelected, kotCoversCount from kotHeader GROUP BY kotTableId ", null);
            while (cursor.moveToNext()) {
                kotHeader = new KOTHeader();
                kotHeader.setTablesId(cursor.getLong(0));
                kotHeader.setTotalAmount(cursor.getDouble(1));
                kotHeader.setSelected(cursor.getString(2));
                kotHeader.setCoversCount(cursor.getInt(3));
                kotHeaderList.add(kotHeader);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return kotHeaderList;
    }

    public List<KOTHeader> getKOTHeaders(long tableId, boolean printed) {
        List<KOTHeader> kotHeaderList = new ArrayList<KOTHeader>();
        KOTHeader kotHeader = null;
        Cursor cursor = null;
        try {
            if (tableId != 0) {
                cursor = db.rawQuery("select * from kotHeader where kotTableId='" + tableId + "' ", null);
            } else {
                if (printed)
                    cursor = db.rawQuery("select * from kotHeader where isPrinted='Y' ", null);
                else
                    cursor = db.rawQuery("select * from kotHeader where isPrinted='N' ", null);
            }
            while (cursor.moveToNext()) {
                kotHeader = new KOTHeader();

                kotHeader.setTablesId(cursor.getLong(1));
                kotHeader.setKotNumber(cursor.getLong(2));
                kotHeader.setInvoiceNumber(cursor.getLong(3));
                kotHeader.setTerminalId(cursor.getLong(4));
                kotHeader.setTotalAmount(cursor.getDouble(5));
                kotHeader.setOrderBy(cursor.getString(6));
                kotHeader.setKotType(cursor.getString(7));
                kotHeader.setOrderType(cursor.getString(8));
                kotHeader.setIsKOT(cursor.getString(9));
                kotHeader.setPrinted(cursor.getString(10));
                kotHeader.setPosted(cursor.getString(11));
                kotHeader.setSelected(cursor.getString(12));
                kotHeader.setCoversCount(cursor.getInt(13));

                kotHeaderList.add(kotHeader);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return kotHeaderList;
    }

    public List<KOTHeader> getKOTNumbersFromKOTHeader(long tableId) {

        List<KOTHeader> kotHeaderList = new ArrayList<KOTHeader>();
        KOTHeader kotHeader = null;

        try {
            Cursor cursor = db.rawQuery("select * from kotHeader where kotTableId = '" + tableId + "'", null);

            while (cursor.moveToNext()) {
                kotHeader = new KOTHeader();

                kotHeader.setTablesId(cursor.getLong(1));
                kotHeader.setKotNumber(cursor.getLong(2));
                kotHeader.setInvoiceNumber(cursor.getLong(3));
                kotHeader.setTerminalId(cursor.getLong(4));
                kotHeader.setTotalAmount(cursor.getDouble(5));
                kotHeader.setOrderBy(cursor.getString(6));
                kotHeader.setKotType(cursor.getString(7));
                kotHeader.setOrderType(cursor.getString(8));
                kotHeader.setIsKOT(cursor.getString(9));
                kotHeader.setPrinted(cursor.getString(10));
                kotHeader.setPosted(cursor.getString(11));
                kotHeader.setSelected(cursor.getString(12));
                kotHeader.setCoversCount(cursor.getInt(13));

                kotHeaderList.add(kotHeader);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return kotHeaderList;
    }

    public List<KOTHeader> getKOTNumbersFromKOTHeader(long tableId, long posId) {

        List<KOTHeader> kotHeaderList = new ArrayList<KOTHeader>();
        KOTHeader kotHeader = null;

        try {
            Cursor cursor = db.rawQuery("select * from kotHeader where kotTableId = '" + tableId + "' and invoiceNumber = '" + posId + "'", null);

            while (cursor.moveToNext()) {
                kotHeader = new KOTHeader();

                kotHeader.setTablesId(cursor.getLong(1));
                kotHeader.setKotNumber(cursor.getLong(2));
                kotHeader.setInvoiceNumber(cursor.getLong(3));
                kotHeader.setTerminalId(cursor.getLong(4));
                kotHeader.setTotalAmount(cursor.getDouble(5));
                kotHeader.setOrderBy(cursor.getString(6));
                kotHeader.setKotType(cursor.getString(7));
                kotHeader.setOrderType(cursor.getString(8));
                kotHeader.setIsKOT(cursor.getString(9));
                kotHeader.setPrinted(cursor.getString(10));
                kotHeader.setPosted(cursor.getString(11));
                kotHeader.setSelected(cursor.getString(12));
                kotHeader.setCoversCount(cursor.getInt(13));

                kotHeaderList.add(kotHeader);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return kotHeaderList;
    }

    public List<KOTHeader> getKOTHeadersNotPrinted() {
        List<KOTHeader> kotHeaderList = new ArrayList<KOTHeader>();
        KOTHeader kotHeader = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from kotHeader where isPrinted='N' ", null);
            while (cursor.moveToNext()) {
                kotHeader = new KOTHeader();

                kotHeader.setTablesId(cursor.getLong(1));
                kotHeader.setKotNumber(cursor.getLong(2));
                kotHeader.setInvoiceNumber(cursor.getLong(3));
                kotHeader.setTerminalId(cursor.getLong(4));
                kotHeader.setTotalAmount(cursor.getDouble(5));
                kotHeader.setOrderBy(cursor.getString(6));
                kotHeader.setKotType(cursor.getString(7));
                kotHeader.setOrderType(cursor.getString(8));
                kotHeader.setIsKOT(cursor.getString(9));
                kotHeader.setPrinted(cursor.getString(10));
                kotHeader.setPosted(cursor.getString(11));
                kotHeader.setSelected(cursor.getString(12));
                kotHeader.setCoversCount(cursor.getInt(13));

                kotHeaderList.add(kotHeader);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return kotHeaderList;
    }

    public long getKOTInvoiceNumber(long tableId) {
        long invoiceNumber = 0;
        try {
            Cursor cursor = db.rawQuery("select invoiceNumber from kotHeader where kotTableId = '" + tableId + "' GROUP BY kotTableId ", null);

            while (cursor.moveToNext()) {
                invoiceNumber = cursor.getLong(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return invoiceNumber;
    }

    public long getKOTCoversCount(long tableId) {
        long covers = 0;
        try {
            Cursor cursor = db.rawQuery("select kotCoversCount from kotHeader where kotTableId = '" + tableId + "' GROUP BY kotTableId ", null);

            while (cursor.moveToNext()) {
                covers = cursor.getLong(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return covers;
    }

    public long getKOTTableId(long posId) {
        long tableId = 0;
        try {
            Cursor cursor = db.rawQuery("select kotTableId from kotHeader where invoiceNumber = '" + posId + "' ", null);

            while (cursor.moveToNext()) {
                tableId = cursor.getLong(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return tableId;
    }

    public boolean isKOTAvailable(long posId) {
        boolean isKOTAvail = false;
        try {
            Cursor cursor = db.rawQuery("select kotTableId from kotLineItems where invoiceNumber = '" + posId + "' GROUP BY kotTableId ", null);
            while (cursor.moveToNext()) {
                isKOTAvail = true;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return isKOTAvail;
    }

    /**
     * This method used for get the KotLineItems for Release, Cancel an Order.
     * Merge the kotLineItems to invoice.
     * @param tableId
     * @return
     */
    public List<KOTLineItems> getKOTLineItems(long tableId) {

        List<KOTLineItems> kotLineItemList = new ArrayList<KOTLineItems>();

        KOTLineItems kotLineItems = null;

        try {
            Cursor cursor = db.rawQuery("select * from kotLineItems where kotTableId='" + tableId + "'", null);

            while (cursor.moveToNext()) {
                kotLineItems = new KOTLineItems();
                Product product = new Product();

                kotLineItems.setTableId(cursor.getLong(1));
                kotLineItems.setKotLineId(cursor.getLong(2));
                kotLineItems.setKotNumber(cursor.getLong(3));
                kotLineItems.setInvoiceNumber(cursor.getLong(4));

                product.setCategoryId(cursor.getLong(5));
                product.setProdId(cursor.getLong(6));
                product.setProdName(cursor.getString(7));
                product.setProdArabicName(cursor.getString(8));
                product.setProdValue(cursor.getString(9));
                product.setUomId(cursor.getLong(10));
                product.setUomValue(cursor.getString(11));
                product.setSalePrice(cursor.getDouble(12));
                product.setCostPrice(cursor.getDouble(13));
                product.setTerminalId(cursor.getLong(14));

                kotLineItems.setProduct(product);
                kotLineItems.setQty(cursor.getInt(15));
                kotLineItems.setTotalPrice(cursor.getDouble(16));
                kotLineItems.setNotes(cursor.getString(17));
                kotLineItems.setPrinted(cursor.getString(18));
                kotLineItems.setStatus(cursor.getString(19));
                kotLineItems.setRefRowId(cursor.getLong(20));
                kotLineItems.setIsExtraProduct(cursor.getString(21));

                kotLineItemList.add(kotLineItems);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return kotLineItemList;
    }


    public List<KOTLineItems> getKOTLineItems(long tableId, long posId) {

        List<KOTLineItems> kotLineItemList = new ArrayList<KOTLineItems>();

        KOTLineItems kotLineItems = null;

        try {
            Cursor cursor = db.rawQuery("select * from kotLineItems where kotTableId='" + tableId + "' and invoiceNumber = '" + posId + "' ", null);

            while (cursor.moveToNext()) {
                kotLineItems = new KOTLineItems();
                Product product = new Product();

                kotLineItems.setTableId(cursor.getLong(1));
                kotLineItems.setKotLineId(cursor.getLong(2));
                kotLineItems.setKotNumber(cursor.getLong(3));
                kotLineItems.setInvoiceNumber(cursor.getLong(4));

                product.setCategoryId(cursor.getLong(5));
                product.setProdId(cursor.getLong(6));
                product.setProdName(cursor.getString(7));
                product.setProdArabicName(cursor.getString(8));
                product.setProdValue(cursor.getString(9));
                product.setUomId(cursor.getLong(10));
                product.setUomValue(cursor.getString(11));
                product.setSalePrice(cursor.getDouble(12));
                product.setCostPrice(cursor.getDouble(13));
                product.setTerminalId(cursor.getLong(14));

                kotLineItems.setProduct(product);
                kotLineItems.setQty(cursor.getInt(15));
                kotLineItems.setTotalPrice(cursor.getDouble(16));
                kotLineItems.setNotes(cursor.getString(17));
                kotLineItems.setPrinted(cursor.getString(18));
                kotLineItems.setStatus(cursor.getString(19));
                kotLineItems.setRefRowId(cursor.getLong(20));
                kotLineItems.setIsExtraProduct(cursor.getString(21));

                kotLineItemList.add(kotLineItems);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return kotLineItemList;
    }

    /**
     * @param kotNumber
     * @return
     */
    public List<KOTLineItems> getKOTLineItem(long kotNumber) {

        List<KOTLineItems> kotLineItemList = new ArrayList<KOTLineItems>();

        KOTLineItems kotLineItems = null;

        try {
            Cursor cursor = db.rawQuery("select * from kotLineItems where isPrinted='N' and kotNumber ='" + kotNumber + "' and isExtraProduct='N' ", null);

            while (cursor.moveToNext()) {
                kotLineItems = new KOTLineItems();
                Product product = new Product();

                kotLineItems.setTableId(cursor.getLong(1));
                kotLineItems.setKotLineId(cursor.getLong(2));
                kotLineItems.setKotNumber(cursor.getLong(3));
                kotLineItems.setInvoiceNumber(cursor.getLong(4));

                product.setCategoryId(cursor.getLong(5));
                product.setProdId(cursor.getLong(6));
                product.setProdName(cursor.getString(7));
                product.setProdArabicName(cursor.getString(8));
                product.setProdValue(cursor.getString(9));
                product.setUomId(cursor.getLong(10));
                product.setUomValue(cursor.getString(11));
                product.setSalePrice(cursor.getDouble(12));
                product.setCostPrice(cursor.getDouble(13));
                product.setTerminalId(cursor.getLong(14));

                kotLineItems.setProduct(product);
                kotLineItems.setQty(cursor.getInt(15));
                kotLineItems.setTotalPrice(cursor.getDouble(16));
                kotLineItems.setNotes(cursor.getString(17));
                kotLineItems.setPrinted(cursor.getString(18));
                kotLineItems.setStatus(cursor.getString(19));
                kotLineItems.setRefRowId(cursor.getLong(20));
                kotLineItems.setIsExtraProduct(cursor.getString(21));

                kotLineItemList.add(kotLineItems);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return kotLineItemList;
    }

    public List<KOTLineItems> getKOTLineItemFromKOTNumber(long kotNumber) {

        List<KOTLineItems> kotLineItemList = new ArrayList<KOTLineItems>();

        KOTLineItems kotLineItems = null;

        try {
            Cursor cursor = db.rawQuery("select * from kotLineItems where kotNumber ='" + kotNumber + "' ", null);

            while (cursor.moveToNext()) {
                kotLineItems = new KOTLineItems();
                Product product = new Product();

                kotLineItems.setTableId(cursor.getLong(1));
                kotLineItems.setKotLineId(cursor.getLong(2));
                kotLineItems.setKotNumber(cursor.getLong(3));
                kotLineItems.setInvoiceNumber(cursor.getLong(4));

                product.setCategoryId(cursor.getLong(5));
                product.setProdId(cursor.getLong(6));
                product.setProdName(cursor.getString(7));
                product.setProdArabicName(cursor.getString(8));
                product.setProdValue(cursor.getString(9));
                product.setUomId(cursor.getLong(10));
                product.setUomValue(cursor.getString(11));
                product.setSalePrice(cursor.getDouble(12));
                product.setCostPrice(cursor.getDouble(13));
                product.setTerminalId(cursor.getLong(14));

                kotLineItems.setProduct(product);
                kotLineItems.setQty(cursor.getInt(15));
                kotLineItems.setTotalPrice(cursor.getDouble(16));
                kotLineItems.setNotes(cursor.getString(17));
                kotLineItems.setPrinted(cursor.getString(18));
                kotLineItems.setStatus(cursor.getString(19));
                kotLineItems.setRefRowId(cursor.getLong(20));
                kotLineItems.setIsExtraProduct(cursor.getString(21));

                kotLineItemList.add(kotLineItems);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return kotLineItemList;
    }

    public List<KOTLineItems> getKOTLineItemForInvoice(long invNumber) {

        List<KOTLineItems> kotLineItemList = new ArrayList<KOTLineItems>();

        KOTLineItems kotLineItems = null;

        try {
            Cursor cursor = db.rawQuery("select * from kotLineItems where invoiceNumber ='" + invNumber + "' and isExtraProduct='N' ", null);

            while (cursor.moveToNext()) {
                kotLineItems = new KOTLineItems();
                Product product = new Product();

                kotLineItems.setTableId(cursor.getLong(1));
                kotLineItems.setKotLineId(cursor.getLong(2));
                kotLineItems.setKotNumber(cursor.getLong(3));
                kotLineItems.setInvoiceNumber(cursor.getLong(4));

                product.setCategoryId(cursor.getLong(5));
                product.setProdId(cursor.getLong(6));
                product.setProdName(cursor.getString(7));
                product.setProdArabicName(cursor.getString(8));
                product.setProdValue(cursor.getString(9));
                product.setUomId(cursor.getLong(10));
                product.setUomValue(cursor.getString(11));
                product.setSalePrice(cursor.getDouble(12));
                product.setCostPrice(cursor.getDouble(13));
                product.setTerminalId(cursor.getLong(14));

                kotLineItems.setProduct(product);
                kotLineItems.setQty(cursor.getInt(15));
                kotLineItems.setTotalPrice(cursor.getDouble(16));
                kotLineItems.setNotes(cursor.getString(17));
                kotLineItems.setPrinted(cursor.getString(18));
                kotLineItems.setStatus(cursor.getString(19));
                kotLineItems.setRefRowId(cursor.getLong(20));
                kotLineItems.setIsExtraProduct(cursor.getString(21));

                kotLineItemList.add(kotLineItems);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return kotLineItemList;
    }

    public List<Long> getKOTInvoiceNumbers(long tableId) {
        List<Long> invoiceNumberList = new ArrayList<Long>();

        try {

            Cursor cursor = db.rawQuery("select invoiceNumber from kotLineItems where kotTableId = '" + tableId + "' and invoiceNumber <> 0 GROUP BY invoiceNumber ", null);

            while (cursor.moveToNext()) {
                invoiceNumberList.add(cursor.getLong(0));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return invoiceNumberList;
    }

    public void updateKOTLineItemInvoiceNumber(long oldInvNum, long invoiceNumber, long prodId) {

        String strSQL;
        try {
            strSQL = "update kotLineItems set invoiceNumber='" + invoiceNumber + "' where invoiceNumber ='" + oldInvNum + "' and productId = '" + prodId + "' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public List<Long> getKOTNumberList(long posId) {

        List<Long> kotNumberList = new ArrayList<Long>();

        try {

            Cursor cursor = db.rawQuery("select kotNumber from kotLineItems where invoiceNumber = '" + posId + "' GROUP BY kotNumber ", null);

            while (cursor.moveToNext()) {
                kotNumberList.add(cursor.getLong(0));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return kotNumberList;

    }

    public List<Long> getKOTTableList(long posId) {

        List<Long> kotNumberList = new ArrayList<Long>();

        try {

            Cursor cursor = db.rawQuery("select kotTableId from kotLineItems where invoiceNumber = '" + posId + "' GROUP BY kotTableId ", null);

            while (cursor.moveToNext()) {
                kotNumberList.add(cursor.getLong(0));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return kotNumberList;

    }

    /**
     * @param kotNumber
     */
    public void updateKOTStatusPrinted(long kotNumber, long invoiceNumber) {

        String strSQL;
        try {
            strSQL = "update kotHeader set isPrinted='Y' where kotNumber = '" + kotNumber + "' ;";
            db.execSQL(strSQL);

            strSQL = "update kotLineItems set isPrinted='Y' where kotNumber = '" + kotNumber + "' ;";
            db.execSQL(strSQL);

            strSQL = "update posLineItems set isKOTGenerated='Y' where posId='" + invoiceNumber + "';";
            db.execSQL(strSQL);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updateKOTSelectedStatus(long tableId, String isSelect) {
        String strSQL;
        try {
            strSQL = "update kotHeader set isSelected='" + isSelect + "' where kotTableId = '" + tableId + "' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updateKOTSelectedStatusWithPOSId(long tableId, String isSelect,long posId) {
        String strSQL;
        try {
            strSQL = "update kotHeader set isSelected='" + isSelect + "' where kotTableId = '" + tableId + "' and invoiceNumber = '" + posId + "';";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updateKOTHeaderTotal(long posId, double total) {
        String strSQL;
        try {
            strSQL = "update kotHeader set kotTotalAmount='" + total + "' where invoiceNumber = '" + posId + "' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updateKOTSelectedStatus() {
        String strSQL;
        try {
            strSQL = "update kotHeader set isSelected='N' where isPrinted='Y' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updateTableChange(long activeTableId, long targetTableId) {
        String strSQL;
        try {
            strSQL = "update kotHeader set kotTableId='" + targetTableId + "' where kotTableId = '" + activeTableId + "' ;";
            db.execSQL(strSQL);

            strSQL = "update kotLineItems set kotTableId='" + targetTableId + "' where kotTableId = '" + activeTableId + "' ;";
            db.execSQL(strSQL);

            strSQL = "update kotTables set isOrderAvailable='N' where kotTableId = '" + activeTableId + "' ;";
            db.execSQL(strSQL);

            strSQL = "update kotTables set isOrderAvailable='Y' where kotTableId = '" + targetTableId + "' ;";
            db.execSQL(strSQL);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * @param tableId
     * @param invoiceNumber
     */
    public void updateInvoiceToKOT(long tableId, long invoiceNumber) {
        String strSQL;
        try {
            if(tableId!=0) {
                strSQL = "update kotHeader set invoiceNumber='" + invoiceNumber + "' where kotTableId = '" + tableId + "' ;";
                db.execSQL(strSQL);

                strSQL = "update kotLineItems set invoiceNumber='" + invoiceNumber + "' where kotTableId = '" + tableId + "' ;";
                db.execSQL(strSQL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updateAllTableStatus(){
        try {
            String strSQL = "update kotTables set isOrderAvailable='N' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * Update the table status. Table having order
     *
     * @param tableId
     */
    public void updateOrderAvailableTable(long tableId) {
        try {
            String strSQL = "update kotTables set isOrderAvailable='Y' where kotTableId = '" + tableId + "' ;";
            db.execSQL(strSQL);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void deleteKOTDetails(long tableId) {

        db.beginTransaction();
        try {
            db.execSQL("update kotTables set isOrderAvailable='N' where kotTableId = '" + tableId + "'");
            db.execSQL("delete from kotHeader where kotTableId = '" + tableId + "'");
            db.execSQL("delete from kotLineItems where kotTableId = '" + tableId + "'");

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteKOTHeaders(long kotNumber) {

        db.beginTransaction();
        try {
            db.execSQL("delete from kotHeader where kotNumber = '" + kotNumber + "'");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteKOTItems(long posId) {

        db.beginTransaction();
        try {
            db.execSQL("delete from kotLineItems where invoiceNumber = '" + posId + "'");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param kotHeaderList
     */
    public void releaseKOTOrder(List<KOTHeader> kotHeaderList) {

        db.beginTransaction();
        try {
            for (int i = 0; i < kotHeaderList.size(); i++) {
                db.execSQL("update kotTables set isOrderAvailable='N' where kotTableId = '" + kotHeaderList.get(i).getTablesId() + "'");
                db.execSQL("delete from kotHeader where kotNumber = '" + kotHeaderList.get(i).getKotNumber() + "'");
                db.execSQL("delete from kotLineItems where kotNumber = '" + kotHeaderList.get(i).getKotNumber() + "'");
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    //******************** DELETE METHODS FOR MASTER TABLES ***********************************//

    public void deleteAllTables() {

        db.beginTransaction();
        try {
            db.execSQL("delete from organization");
            db.execSQL("delete from warehouse");
            db.execSQL("delete from role");
            db.execSQL("delete from roleAccess");

            db.execSQL("delete from businessPartner");
            db.execSQL("delete from kotTables");
            db.execSQL("delete from kotTerminals");
            db.execSQL("delete from category");
            db.execSQL("delete from product");
            db.execSQL("delete from posOrderHeader");
            db.execSQL("delete from posLineItems");
            db.execSQL("delete from posPaymentDetail");

            db.execSQL("delete from kotHeader");
            db.execSQL("delete from kotLineItems");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * DELETE ALL ORGANIZATION RELATED TABLES
     * Its used for reLogin with same user
     */
    public void deleteOrgTables() {

        db.beginTransaction();
        try {
            db.execSQL("delete from organization");
            db.execSQL("delete from warehouse");
            db.execSQL("delete from role");
            db.execSQL("delete from roleAccess");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param posId
     */
    public void deletePOSTables(long posId) {

        db.beginTransaction();
        try {
            db.execSQL("delete from posOrderHeader where posId = '" + posId + "'");
            db.execSQL("delete from posLineItems where posId = '" + posId + "'");
            db.execSQL("delete from posPaymentDetail where posId = '" + posId + "'");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Delete all POS related tables
     * It's mainly used for manual sync
     */
    public void deletePOSRelatedTables() {

        db.beginTransaction();
        try {
            db.execSQL("delete from businessPartner");
            db.execSQL("delete from kotTables");
            db.execSQL("delete from kotTerminals");
            db.execSQL("delete from category");
            db.execSQL("delete from product");
            db.execSQL("delete from posOrderHeader");
            db.execSQL("delete from posLineItems");
            db.execSQL("delete from posPaymentDetail");
            db.execSQL("delete from kotHeader");
            db.execSQL("delete from kotLineItems");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteKOTTable() {

        db.beginTransaction();
        try {
            db.execSQL("delete from posOrderHeader");
            db.execSQL("delete from posLineItems");
            db.execSQL("delete from posPaymentDetail");

            db.execSQL("delete from kotHeader");
            db.execSQL("delete from kotLineItems");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteBPartner() {

        db.beginTransaction();
        try {
            db.execSQL("delete from businessPartner");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}

package com.zearoconsulting.zearopos.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is implemented for an common utility maintenance. like get device resolution,
 * device id and checking network status
 * Created by saravanan on 6/21/2016.
 */
public class Common {

    /**
     * check availability of Internet
     *
     * @param context
     * @return true or false
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /**
     * use for getting device height
     *
     * @param mContext
     * @return height of your device
     */
    public static int getDeviceHeight(Context mContext) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    /**
     * use for getting device width
     *
     * @param mContext
     * @return width of your device
     */
    public static int getDeviceWidth(Context mContext) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    /**
     * get the version of the application
     *
     * @param mContext
     * @return version code.
     */
    public static int getAppVersionCode(Context mContext) {
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionCode;
    }

    /**
     * hide the keyboard if visible
     *
     * @param mContext
     * @param v
     */
    public static void hideKeyboard(Context mContext, View v) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * decimal value formatter
     *
     * @param value
     * @return
     */
    public static String valueFormatter(double value){

        //String val = String.valueOf(Math.floor(value * 100) / 100);
        String s1 = String.valueOf(value);
        Double f1 = Double.parseDouble(s1);
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN); // Note this extra step
        System.out.println(df.format(f1));
        return df.format(f1);
    }

    /**
     * return dp to px
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int getAbsoluteTop(View view) {
        int[] rect = new int[2];
        view.getLocationInWindow(rect);
        return rect[1];
    }

    /**
     * to decode the base64 string to bitmap
     *
     * @param input
     * @return
     */
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    /**
     * change bitmap to string
     *
     * @param bitmap
     * @return
     */
    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /**
     * get device inch size
     *
     * @param mContext
     * @return
     */
    public static double getDeviceInchSize(Context mContext){

        double size = 0;
        try {

            // Compute screen size

            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();

            float screenWidth  = dm.widthPixels / dm.xdpi;

            float screenHeight = dm.heightPixels / dm.ydpi;

            size = Math.sqrt(Math.pow(screenWidth, 2) +

                    Math.pow(screenHeight, 2));

        } catch(Throwable t) {

        }

        return size;
    }

    /**
     * STX FS [Track1] FS [Track2] FS [Track3] ETX DATAEND 0x02 0x1C [0-76
     * Bytes] 0x1C [0-37 Bytes] 0x1C [0-106 Bytes] 0x03 0x0D 0x0A 0x00
     */
    public static String[] parsingMSRData(byte[] rawData)
    {
        final byte[] FS = { (byte) 0x1C };
        final byte[] ETX = { (byte) 0x03 };

        String temp = new String(rawData);
        String trackData[] = new String[3];

        // ETX , FS
        String[] rData = temp.split(new String(ETX));
        temp = rData[0];
        String[] tData = temp.split(new String(FS));

        switch (tData.length)
        {
            case 1:
                break;
            case 2:
                trackData[0] = tData[1];
                break;
            case 3:
                trackData[0] = tData[1];
                trackData[1] = tData[2];
                break;
            case 4:
                trackData[0] = tData[1];
                trackData[1] = tData[2];
                trackData[2] = tData[3];
                break;
        }
        return trackData;
    }


    public static String getDate(){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = df.format(new Date());

        return strDate;
    }

    public static String getTime(){
        DateFormat tf = new SimpleDateFormat("hh:mm a");
        String strTime = tf.format(new Date());

        return strTime;
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public static boolean isIpAddress(String ipAddress) {
        String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

}
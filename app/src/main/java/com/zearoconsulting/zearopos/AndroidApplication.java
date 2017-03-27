package com.zearoconsulting.zearopos;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.tramsun.libs.prefcompat.Pref;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.data.DBHelper;
import com.zearoconsulting.zearopos.data.POSDataSource;
import com.zearoconsulting.zearopos.domain.receivers.ConnectivityReceiver;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by saravanan on 16-05-2016.
 */
public class AndroidApplication extends Application{

    private static AndroidApplication sInstance;
    private static AppDataManager mManager;
    private static DBHelper mDBHelper;
    private static POSDataSource mDataSource;
    private static boolean activityVisible;
    private RequestQueue mRequestQueue;
    public static final String TAG = AndroidApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        sInstance = this;
        Pref.init(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/GothamRounded-Medium.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static AndroidApplication getInstance(){
        if (sInstance == null) {
            sInstance = new AndroidApplication();
        }
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }

    public AppDataManager getAppManager(){

        if(mManager == null){
            mManager = new AppDataManager(getApplicationContext());
        }

        return mManager;
    }

    public DBHelper getDBHelper(){

        if(mDBHelper == null){
            mDBHelper = new DBHelper(getAppContext());
        }

        return mDBHelper;
    }

    public POSDataSource getPOSDataSource(){
        if(mDataSource == null){
            mDataSource = new POSDataSource(getAppContext());
            mDataSource.open();
        }

        return mDataSource;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public static Typeface getGothamRoundedLight(){
        return Typeface.createFromAsset( sInstance.getApplicationContext().getAssets(),
                "fonts/GothamRounded-Light.otf");
    }

    public static Typeface getGothamRoundedMedium(){
        return Typeface.createFromAsset( sInstance.getApplicationContext().getAssets(),
                "fonts/GothamRounded-Medium.otf");
    }

    public static Typeface getGothamRoundedBold(){
        return Typeface.createFromAsset( sInstance.getApplicationContext().getAssets(),
                "fonts/GothamRounded-Bold.otf");
    }

    public static Typeface getGothamRoundedBook(){
        return Typeface.createFromAsset( sInstance.getApplicationContext().getAssets(),
                "fonts/GothamRounded-Book.otf");
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}

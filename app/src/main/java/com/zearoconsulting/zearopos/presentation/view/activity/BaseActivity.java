package com.zearoconsulting.zearopos.presentation.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.AndroidMSR;
import com.sewoo.request.android.RequestHandler;
import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.data.DBHelper;
import com.zearoconsulting.zearopos.data.POSDataSource;
import com.zearoconsulting.zearopos.domain.parser.JSONParser;
import com.zearoconsulting.zearopos.utils.AppConstants;

/**
 * Created by saravanan on 16-05-2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public AppDataManager mAppManager;
    public POSDataSource mDBHelper;
    public JSONParser mParser;
    public ProgressDialog mProDlg;

    private SpringSystem mSpringSystem;
    public Spring mSpring;
    private static final SpringConfig ORIGAMI_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(100, 30);
    public boolean tabletSize;

    public static BluetoothPort bluetoothPort;
    public static BluetoothAdapter mBluetoothAdapter;
    public Thread hThread;
    public RequestHandler rh;
    public static AndroidMSR androidMSR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabletSize = getResources().getBoolean(R.bool.isTablet);
        if(tabletSize) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            AppConstants.isMobile = false;
        }
        else{
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            AppConstants.isMobile = true;
        }

        rh = new RequestHandler();

        mAppManager = AndroidApplication.getInstance().getAppManager();
        mDBHelper = AndroidApplication.getInstance().getPOSDataSource();

        mParser = new JSONParser(AndroidApplication.getAppContext(), mAppManager, mDBHelper);

        mSpringSystem = SpringSystem.create();
        mSpring = mSpringSystem.createSpring();
        mSpring.setSpringConfig(ORIGAMI_SPRING_CONFIG);

    }

    @Override
    protected void onPause() {
        super.onPause();
        AndroidApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidApplication.activityResumed();
    }
}


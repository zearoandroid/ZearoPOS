package com.zearoconsulting.zearopos.presentation.view.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.domain.net.NetworkDataRequestThread;
import com.zearoconsulting.zearopos.domain.receivers.ConnectivityReceiver;
import com.zearoconsulting.zearopos.presentation.model.Category;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.Warehouse;
import com.zearoconsulting.zearopos.presentation.presenter.ILoginListeners;
import com.zearoconsulting.zearopos.presentation.view.adapter.OrgSpinner;
import com.zearoconsulting.zearopos.presentation.view.adapter.RoleSpinner;
import com.zearoconsulting.zearopos.presentation.view.adapter.WarehouseSpinner;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;
import com.zearoconsulting.zearopos.presentation.view.dialogs.NetworkErrorDialog;
import com.zearoconsulting.zearopos.presentation.view.fragment.LoadingDialogFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.ServerConfigFragment;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.NetworkUtil;
import org.json.JSONObject;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends BaseActivity implements  ConnectivityReceiver.ConnectivityReceiverListener, ILoginListeners{

    private EditText mEdtUserName;
    private EditText mEdtUserPassword;
    private Button mBtnSignIn;
    private TextView mTxtAppNameView;
    private TextView mTxtLoginForm;
    private TextView mTxtRememberMe;
    private TextView mTxtForgot;
    private List<Category> mCategoryList;
    private long mCategoryId = 0;
    private ReboundListener mReboundListener;
    private List<Long> mDefaultIdList;
    private String mUsername;
    private String mPassword;
    private ImageView mImgCofig;

    private List<Warehouse> mWarehouseList = null;
    WarehouseSpinner mWarehouseAdapter;
    private Spinner mSpnWareHouse;
    private long mWarehouseId = 0;
    int mCurrentWareHouseSelection;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtAppNameView = (TextView)findViewById(R.id.textAppNameView);
        mTxtLoginForm = (TextView)findViewById(R.id.txtLoginForm);
        mEdtUserName= (EditText) findViewById(R.id.editTextUser);
        mEdtUserPassword= (EditText) findViewById(R.id.editTextPassword);
        mSpnWareHouse = (Spinner) findViewById(R.id.spinnerWarhouse);
        mTxtRememberMe= (TextView)findViewById(R.id.textRememberMe);
        mTxtForgot= (TextView)findViewById(R.id.textForgot);
        mBtnSignIn= (Button)findViewById(R.id.btnSignIn);
        mImgCofig = (ImageView)findViewById(R.id.configImgView);

        mReboundListener = new ReboundListener();

        mProDlg = new ProgressDialog(this);
        mProDlg.setIndeterminate(true);
        mProDlg.setCancelable(false);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Log.e("value", "Permission already Granted, Now you can save image.");
            } else {
                requestPermission();
            }
        } else {
            Log.e("value", "Not required for requesting runtime permission");
        }

        //check server address is already available or not
        if(mAppManager.getServerAddress().equals("")){
            showConfiguration();
        }

        mDefaultIdList = mDBHelper.getDefaultIds();

        if(mDefaultIdList.size()!=0 && mAppManager.getIsRetail()){
            mWarehouseList = mDBHelper.getWarehouse(mDefaultIdList.get(0));
            if(mWarehouseList.size()!=0)
                updateWarehouseSpinners();
        }

        mSpnWareHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentWareHouseSelection != position && mWarehouseList.size()!=0){
                    mWarehouseId = mWarehouseList.get(position).getWarehouseId();
                    mAppManager.setWarehouseID(mWarehouseId);
                }
                mCurrentWareHouseSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCurrentWareHouseSelection = mSpnWareHouse.getSelectedItemPosition();

        mImgCofig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfiguration();
            }
        });

        // Add an OnTouchListener to the root view.
        mBtnSignIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mReboundListener.animateView(mBtnSignIn);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // When pressed start solving the spring to 1.
                        mSpring.setEndValue(1);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // When released start solving the spring to 0.
                        mSpring.setEndValue(0);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                signIn();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });
    }

    private void showConfiguration()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ServerConfigFragment serverConfigFragment = new ServerConfigFragment();
        serverConfigFragment.show(fragmentManager, "ServerConfigFragment");
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
            if(mProDlg.isShowing())
                mProDlg.dismiss();
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.layLogin), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mAppManager.getUserName().equals("") && mAppManager.getUserLoggedIn()){
            mEdtUserName.setText(mAppManager.getUserName());
            mEdtUserPassword.setText(mAppManager.getUserPassword());
        }else{
            mEdtUserName.setText("");
            mEdtUserPassword.setText("");
        }
        //Add a listener to the spring
        mSpring.addListener(mReboundListener);
        // register connection status listener
        AndroidApplication.getInstance().setConnectivityListener(this);

        mParser.setTokeListener(null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Remove a listener to the spring
        mSpring.removeListener(mReboundListener);
    }

    private void signIn(){

        Crashlytics.log("Login");

        //mDBHelper.deleteKOTTable();

        //mDBHelper.updateKOTSelectedStatus();

        mUsername = mEdtUserName.getText().toString().trim();
        mPassword = mEdtUserPassword.getText().toString().trim();
        //mAppManager.setSessionId(1000001);
        if(mEdtUserName.getText().toString().trim().equals("")){
            mEdtUserName.setError("Username should not be empty");
        }else if(mEdtUserPassword.getText().toString().trim().equals("")){
            mEdtUserPassword.setError("Password should not be empty");
        }
        else if (!mAppManager.getUserName().equalsIgnoreCase(mUsername) && !mAppManager.getUserPassword().equalsIgnoreCase(mPassword)){
            mDBHelper.deleteOrgTables();
            mAppManager.setUsername(mUsername);
            mAppManager.setPassword(mPassword);
            mAppManager.setSessionStatus(false);
            showLoading();
        }else if(mWarehouseList!=null) {
            mAppManager.setSessionStatus(false);
            if(mWarehouseList.size()!=0)
                showLoading();
        }else{
            mDBHelper.deleteOrgTables();
            mAppManager.setUsername(mUsername);
            mAppManager.setPassword(mPassword);
            mAppManager.setSessionStatus(false);
            showLoading();
        }
    }

    private void updateWarehouseSpinners(){
        mSpnWareHouse.setVisibility(View.VISIBLE);
        mDefaultIdList = mDBHelper.getDefaultIds();

        mWarehouseList = mDBHelper.getWarehouse(mDefaultIdList.get(0));
        if(mWarehouseList.size()!=0) {
            mWarehouseAdapter = new WarehouseSpinner(this,mWarehouseList);
            mSpnWareHouse.setAdapter(mWarehouseAdapter);
            mWarehouseAdapter.notifyDataSetChanged();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void showLoading()
    {
        //List<POSLineItem> lineItems = mDBHelper.getPOSLineItems();
        //System.out.println("Total Count: "+lineItems.size());

        //check server address is already available or not
        if(mAppManager.getServerAddress().equals("")){
            Toast.makeText(MainActivity.this,"Please config server details...",Toast.LENGTH_SHORT).show();
        }else{
            FragmentManager localFragmentManager = getSupportFragmentManager();
            LoadingDialogFragment.newInstance(MainActivity.this, mUsername, mPassword, "LOGIN").show(localFragmentManager, "loading");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can save image .");
                } else {
                    Log.e("value", "Permission Denied, You cannot save image.");
                }
                break;
        }
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onLoginFailure() {
        YoYo.with(Techniques.Shake).playOn(mEdtUserName);
        YoYo.with(Techniques.Shake).playOn(mEdtUserPassword);
        //show the server error dialog
        Toast.makeText(MainActivity.this,"Invalid user name or password",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrganizeDataReceived() {
        updateWarehouseSpinners();
    }
}

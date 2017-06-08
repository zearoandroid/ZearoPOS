package com.zearoconsulting.zearopos.presentation.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.domain.net.NetworkDataRequestThread;
import com.zearoconsulting.zearopos.domain.receivers.ConnectivityReceiver;
import com.zearoconsulting.zearopos.presentation.model.Category;
import com.zearoconsulting.zearopos.presentation.presenter.ILoginListeners;
import com.zearoconsulting.zearopos.presentation.view.dialogs.AlertView;
import com.zearoconsulting.zearopos.presentation.view.dialogs.NetworkErrorDialog;
import com.zearoconsulting.zearopos.presentation.view.fragment.LoadingDialogFragment;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.FileUtils;
import com.zearoconsulting.zearopos.utils.NetworkUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import static com.zearoconsulting.zearopos.utils.FileUtils.deleteDir;

public class ManualSyncActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener, ILoginListeners {

    private Button mBtnSync;
    // variable to track event time
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_sync);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mProDlg = new ProgressDialog(this);
        mProDlg.setIndeterminate(true);
        mProDlg.setCancelable(false);

        mBtnSync = (Button) findViewById(R.id.btnManualSync);
        mBtnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Preventing multiple clicks, using threshold of 1 second
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                boolean status = FileUtils.deleteImages();

                if(status){
                    mDBHelper.deleteAllTables();

                    clearCache();
                    showLoading();
                }else{
                    AlertView.showAlert("Images are not deleted. Please delete all images from storage!", ManualSyncActivity.this);
                }
            }
        });
    }

    private void clearCache() {
        try {
            File dir = ManualSyncActivity.this.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    private void showLoading() {
        FragmentManager localFragmentManager = getSupportFragmentManager();
        LoadingDialogFragment.newInstance(ManualSyncActivity.this, mAppManager.getUserName(), mAppManager.getUserPassword(), "LOGIN").show(localFragmentManager, "loading");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onMenuItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
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
            if (mProDlg.isShowing())
                mProDlg.dismiss();
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.layManualSync), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        AndroidApplication.getInstance().setConnectivityListener(this);
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
    public void onBackPressed() {

        if (mProDlg != null) {
            if (mProDlg.isShowing()) {
                return;
            } else {
                Intent intent = new Intent(ManualSyncActivity.this, POSActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

    @Override
    public void onLoginFailure() {

    }

    @Override
    public void onOrganizeDataReceived() {

    }
}

package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by saravanan on 25-04-2017.
 */

public class AppUpdateFragment extends AbstractDialogFragment {

    private static Context context;
    private FancyButton mBtnYes;
    private FancyButton mBtnNo;
    File sdcard = Environment.getExternalStorageDirectory();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mReboundListener = new ReboundListener();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_update_confirmation, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Add a listener to the spring
        mSpring.addListener(mReboundListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        //Remove a listener to the spring
        mSpring.removeListener(mReboundListener);
    }

    @Override
    public void onViewCreated(View paramView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(paramView, savedInstanceState);

        getDialog().getWindow().setSoftInputMode(3);
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().setCanceledOnTouchOutside(true);


        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode, android.view.KeyEvent event) {

                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    //Stop back event here!!!
                    return true;
                } else
                    return false;
            }
        });

        this.mBtnYes = ((FancyButton) paramView.findViewById(R.id.btnYes));
        this.mBtnNo = ((FancyButton) paramView.findViewById(R.id.btnNo));

        mProDlg = new ProgressDialog(getActivity());
        mProDlg.setIndeterminate(true);
        mProDlg.setCancelable(false);

        // Add an OnTouchListener to the root view.
        this.mBtnYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mReboundListener.animateView(mBtnYes);
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
                                try {
                                    new BuildDownloadAsyncTask().execute();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                //dismiss();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });

        // Add an OnTouchListener to the root view.
        this.mBtnNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mReboundListener.animateView(mBtnNo);
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
                                mAppManager.setRemindMe("Y");
                                dismiss();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });
    }

    // The types specified here are the input data type, the progress type, and
    // the result type
    private class BuildDownloadAsyncTask extends
            AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProDlg.setMessage("Downloading application...");
            mProDlg.setCancelable(false);
            mProDlg.show();
        }

        protected String doInBackground(String... strings) {
            // Some long-running task like downloading an image.
            try {
                URL url = new URL(mAppManager.getAppDownloadPath());
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();


                File file = new File(sdcard, "pos.apk");

                if (file.exists()) {
                    file.delete();
                } else {
                    FileOutputStream fileOutput = new FileOutputStream(file);
                    InputStream inputStream = urlConnection.getInputStream();

                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                    }
                    fileOutput.close();

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            mProDlg.dismiss();
            File file = new File(sdcard, "pos.apk");
            if (file.exists()) {
                installApk();
            }

        }
    }

    private void installApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File("/sdcard/pos.apk"));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        getActivity().startActivity(intent);
    }
}

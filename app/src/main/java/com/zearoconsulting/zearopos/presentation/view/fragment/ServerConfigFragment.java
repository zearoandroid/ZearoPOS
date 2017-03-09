package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;
import com.zearoconsulting.zearopos.utils.AppConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by saravanan on 09-01-2017.
 */

public class ServerConfigFragment extends AbstractDialogFragment {

    private EditText mEdtServerAddr;
    private EditText mEdtServerPort;
    private Button mBtnConnect;
    private TextView mTxtConnection;
    private String mURL;
    private String serverAddr, serverPort;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mReboundListener = new ReboundListener();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_server_config, container, false);
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
            public boolean onKey(DialogInterface dialog, int keyCode, android.view.KeyEvent event) {

                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    //Stop back event here!!!
                    return true;
                } else
                    return false;
            }
        });

        mEdtServerAddr = ((EditText) paramView.findViewById(R.id.editTextServer));
        mEdtServerPort = ((EditText) paramView.findViewById(R.id.editTextPort));
        mBtnConnect = ((Button) paramView.findViewById(R.id.btnConnet));
        mTxtConnection = ((TextView) paramView.findViewById(R.id.txtConnection));

        //create the ProgressDialog object
        mProDlg = new ProgressDialog(getActivity());
        mProDlg.setIndeterminate(true);
        mProDlg.setCancelable(false);

        if(!mAppManager.getServerAddress().equals("")){
            mEdtServerAddr.setText(mAppManager.getServerAddress());
            mEdtServerPort.setText(""+mAppManager.getServerPort());
        }

        // Add an OnTouchListener to the root view.
        mBtnConnect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mReboundListener.animateView(mBtnConnect);
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
                                //To-do server connection
                                checkServerConnection();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });
    }

    public void checkServerConnection(){

        serverAddr = mEdtServerAddr.getText().toString().trim();
        serverPort = mEdtServerPort.getText().toString().trim();

        mURL= AppConstants.kURLHttp+serverAddr+":"+serverPort+ AppConstants.kURLServiceName+ AppConstants.kURLMethodTest;

        new TestConnection().execute();

    }

    private class TestConnection extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProDlg.setMessage("Connecting...");
            mProDlg.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String result = null;

            try {
                // Construct the URL for the ZEAROPOS query
                URL url = new URL(mURL);

                // Create the request to ZEAROPOS, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                // set the connection timeout to 5 seconds
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return "Error";
                }
                result = buffer.toString();
                return result;
            } catch (IOException e) {
                Log.e("ServerConfigFragment", "Error ", e);
                // If the code didn't successfully get the ZEAROPOS data, there's no point in attemping
                // to parse it.
                return "Error";
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ServerConfigFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProDlg.dismiss();
            if(result.contains("Server Connected")) {
                mTxtConnection.setText(result);
                mAppManager.saveServerData(serverAddr, Integer.parseInt(serverPort));
                mTxtConnection.setTextColor(getResources().getColor(R.color.black));
            }else {
                mTxtConnection.setText("Server Connection error...");
                mTxtConnection.setTextColor(getResources().getColor(R.color.red));
            }
        }
    }
}

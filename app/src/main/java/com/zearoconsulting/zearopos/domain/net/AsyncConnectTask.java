package com.zearoconsulting.zearopos.domain.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.presentation.presenter.INetworkListeners;
import com.zearoconsulting.zearopos.utils.AppConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by saravanan on 25-04-2017.
 */

public class AsyncConnectTask extends AsyncTask<Void, Void, String> {

    private INetworkListeners mListener;
    private Context mContext;

    public AsyncConnectTask(Context context, INetworkListeners mListener) {
        mContext = context;

        this.mListener  = mListener;
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

            AppDataManager mAppManager = AndroidApplication.getInstance().getAppManager();
            String mURL = AppConstants.kURLHttp+mAppManager.getServerAddress()+":"+mAppManager.getServerPort()+AppConstants.kURLServiceName+ AppConstants.kURLMethodTest;

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
                return "Error";
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

        if(result.contains("Server Connected")) {
            if (mListener != null)
                mListener.onNetworkStatus(true);
        }else {
            if (mListener != null)
                mListener.onNetworkStatus(false);
        }
    }
}

package com.zearoconsulting.zearopos.domain.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.data.DBHelper;
import com.zearoconsulting.zearopos.data.POSDataSource;
import com.zearoconsulting.zearopos.domain.parser.JSONParser;
import com.zearoconsulting.zearopos.utils.AppConstants;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class KOTService extends IntentService {

    private static final String TAG = "KOTService";
    public AppDataManager mAppManager;
    public POSDataSource mDBHelper;
    public JSONParser mParser;

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public KOTService() {
        super("KOTService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            mAppManager = AndroidApplication.getInstance().getAppManager();
            mDBHelper = AndroidApplication.getInstance().getPOSDataSource();

            mParser = new JSONParser(AndroidApplication.getAppContext(), mAppManager, mDBHelper);

            final ResultReceiver receiver = intent.getParcelableExtra("receiver");

            //timer will be declare here
            {
                Bundle bundle = new Bundle();

                /* Update UI: Download Service is Running */
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);

                try {
                    String results = getResponse();

                    /* Sending result back to activity */
                    bundle.putString("result", results);
                    receiver.send(STATUS_FINISHED, bundle);

                } catch (Exception e) {

                /* Sending error message back to activity */
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }

        }
    }

    public String getResponse()
    {
        String retSrc = "";
        StringBuilder result;

        JSONObject errJson = new JSONObject();

        try
        {
            URL url = new URL(AppConstants.URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();

            //GET_KOT_HEADER_LINE_DATA
            JSONObject mJsonObj = mParser.getParams(AppConstants.GET_KOT_HEADER_AND_lINES);
            os.write(mJsonObj.toString().getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            System.out.println("Output from Server .... \n");

            //Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            retSrc = result.toString();

            System.out.println("Polling Service Data: "+retSrc);

            conn.disconnect();

        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            retSrc = errJson.toString();
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return retSrc;
    }

}

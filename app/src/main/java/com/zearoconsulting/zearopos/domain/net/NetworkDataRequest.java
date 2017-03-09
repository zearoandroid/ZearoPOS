package com.zearoconsulting.zearopos.domain.net;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saravanan on 13-04-2016.
 */
public class NetworkDataRequest
{
    private String mURL;
    private String param;
    JSONObject errJson = new JSONObject();
    int TIMEOUT_VALUE = 1000;

    public void setUrl(String url)
    {
        this.mURL=url;
    }

    public void setBody(String params)
    {
        this.param=params;
    }

    public String getResponse()
    {
        String retSrc = "";
        StringBuilder result;

        try
        {
            URL url = new URL(mURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            //conn.setConnectTimeout(TIMEOUT_VALUE);
            //conn.setReadTimeout(TIMEOUT_VALUE);

            OutputStream os = conn.getOutputStream();
            os.write(param.getBytes());
            os.flush();

            /*if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }*/

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
            errJson.put("responseCode", "700");
            conn.disconnect();

        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }catch (SocketTimeoutException e) {
            System.out.println("More than " + TIMEOUT_VALUE + " elapsed.");
            retSrc = errJson.toString();
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

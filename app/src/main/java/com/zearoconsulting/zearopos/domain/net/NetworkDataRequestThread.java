package com.zearoconsulting.zearopos.domain.net;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.NetworkUtil;

/**
 * Created by saravanan on 25-05-2016.
 */
public class NetworkDataRequestThread extends Thread {

    private Handler mHandler;
    private String url;
    private String client;
    private String param;
    private int type;

    public NetworkDataRequestThread(String url, String client, Handler mHandler, String param, int type) {
        this.url = url;
        this.client = client;
        this.param = param;
        this.type = type;
        this.mHandler = mHandler;
    }

    @Override
    public void run() {

        NetworkDataRequest post = new NetworkDataRequest();
        post.setUrl(url);
        post.setBody(param);
        String out = post.getResponse();

        Message msg = new Message();
        Bundle b = new Bundle();
        b.putInt("Type", type);
        b.putString("OUTPUT", out);
        msg.setData(b);
        mHandler.sendMessage(msg);

    }
}


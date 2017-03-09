package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.domain.net.NetworkDataRequestThread;
import com.zearoconsulting.zearopos.presentation.view.activity.POSActivity;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;
import com.zearoconsulting.zearopos.presentation.view.dialogs.NetworkErrorDialog;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by saravanan on 23-11-2016.
 */

public class SessionFragment extends AbstractDialogFragment {

    private static Context context;
    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("Type");
            String jsonStr = msg.getData().getString("OUTPUT");

            switch (type) {
                case AppConstants.CREATE_SESSION_REQUEST:
                    mParser.parseStartSessionResponse(jsonStr, mHandler);
                    break;
                case AppConstants.CREATE_SESSION_RESPONSE:
                    mProDlg.dismiss();
                    dismiss();
                    break;
                case AppConstants.RESUME_SESSION_REQUEST:
                    mParser.parseResumeSessionResponse(jsonStr, mHandler);
                    break;
                case AppConstants.RESUME_SESSION_RESPONSE:
                    mProDlg.dismiss();
                    dismiss();
                    break;
                case AppConstants.SESSION_EXPIRED:
                    mProDlg.dismiss();
                    showCreateSession("Expired");
                    break;
                case AppConstants.SERVER_ERROR:
                    mProDlg.dismiss();
                    //show the server error dialog
                    Toast.makeText(getActivity(), "Server data error", Toast.LENGTH_SHORT).show();
                    dismiss();
                    break;
                case AppConstants.NETWORK_ERROR:
                    //INFORM USER NO DATA
                    Toast.makeText(getActivity(), "NETWORK ERROR...", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private TextView mTxtDescription;
    private FancyButton mBtnCreateSession;
    private FancyButton mBtnResumeSession;
    private FancyButton mBtnCloseSession;
    private LinearLayout mLayCreateSession, mLayCloseSession;
    private String mSessionType,mSessionDesc;

    public static SessionFragment newInstance(Context paramContext, String paramString1,String paramString2) {
        SessionFragment sessionFragment = new SessionFragment();
        Bundle localBundle = new Bundle();
        localBundle.putString("sessionType", paramString1);
        localBundle.putString("sessionDesc", paramString1);
        sessionFragment.setArguments(localBundle);
        context = paramContext;
        return sessionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mReboundListener = new ReboundListener();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_session, container, false);
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

        this.mSessionType = getArguments().getString("sessionType", "");
        this.mSessionDesc = getArguments().getString("sessionDesc", "");

        getDialog().getWindow().setSoftInputMode(3);
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().setCanceledOnTouchOutside(false);


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

        mProDlg = new ProgressDialog(getActivity());
        mProDlg.setIndeterminate(true);
        mProDlg.setCancelable(false);

        this.mTxtDescription = (TextView) paramView.findViewById(R.id.txtDescription);
        this.mBtnCreateSession = ((FancyButton) paramView.findViewById(R.id.createSession));
        this.mBtnResumeSession = ((FancyButton) paramView.findViewById(R.id.resumeSession));
        this.mBtnCloseSession = ((FancyButton) paramView.findViewById(R.id.closeSession));
        this.mLayCreateSession = (LinearLayout) paramView.findViewById(R.id.layCreateSession);
        this.mLayCloseSession = (LinearLayout) paramView.findViewById(R.id.layBottomSession);

        mLayCreateSession.setVisibility(View.GONE);
        mLayCloseSession.setVisibility(View.GONE);

        AppConstants.URL = AppConstants.kURLHttp+mAppManager.getServerAddress()+":"+mAppManager.getServerPort()+AppConstants.kURLServiceName+ AppConstants.kURLMethodApi;

        if(mSessionDesc.equalsIgnoreCase("Expired")){
            mTxtDescription.setText("Session Expired! Create new session");
        }else if(mSessionDesc.equalsIgnoreCase("Resume")){
            mTxtDescription.setText("Resume or Close session");
        }else if(mSessionDesc.equalsIgnoreCase("New")){
            mTxtDescription.setText("Create new session");
        }

        if (mSessionType.equalsIgnoreCase("CREATE")) {
            mLayCreateSession.setVisibility(View.VISIBLE);
            mLayCloseSession.setVisibility(View.GONE);
        } else {

            mLayCreateSession.setVisibility(View.GONE);
            mLayCloseSession.setVisibility(View.VISIBLE);
        }

        // Add an OnTouchListener to the root view.
        this.mBtnCloseSession.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mReboundListener.animateView(mBtnCloseSession);
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

                                //mAppManager.setSessionStatus(false);
                                //mAppManager.setSessionId("0");
                                //show denomination screen
                                FragmentManager localFragmentManager = getActivity().getSupportFragmentManager();
                                DenominationFragment denominationFragment = new DenominationFragment();
                                denominationFragment.show(localFragmentManager, "DenominationFragment");
                                dismiss();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });

        // Add an OnTouchListener to the root view.
        this.mBtnCreateSession.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mReboundListener.animateView(mBtnCreateSession);
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
                                //mAppManager.setSessionStatus(true);
                                //mAppManager.setSessionId("1234567890");
                                //SessionFragment.this.dismiss();
                                //create session data to server
                                createSession();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });

        // Add an OnTouchListener to the root view.
        this.mBtnResumeSession.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mReboundListener.animateView(mBtnResumeSession);
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
                                //SessionFragment.this.dismiss();
                                //resume session data to server
                                resumeSession();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });

    }

    private void showCreateSession(String msg){
        if(msg.equalsIgnoreCase("Expired")){
            mTxtDescription.setText("Session Expired! Create new session");
            mLayCreateSession.setVisibility(View.VISIBLE);
            mLayCloseSession.setVisibility(View.GONE);
        }else if(msg.equalsIgnoreCase("Resume")){
            mTxtDescription.setText("Resume or Close session");
            mLayCreateSession.setVisibility(View.GONE);
            mLayCloseSession.setVisibility(View.VISIBLE);
        }
    }

    private void createSession() {

        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {
                mProDlg.setMessage("Create session...");
                mProDlg.show();
                JSONObject mJsonObj = mParser.getParams(AppConstants.CREATE_SESSION_REQUEST);

                Log.i("CREATE SESSION", mJsonObj.toString());

                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.CREATE_SESSION_REQUEST);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(getActivity()).show();
        }

    }

    private void resumeSession() {

        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {
                mProDlg.setMessage("Resume session...");
                mProDlg.show();
                JSONObject mJsonObj = mParser.getParams(AppConstants.RESUME_SESSION_REQUEST);

                Log.i("RESUME SESSION", mJsonObj.toString());

                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.RESUME_SESSION_REQUEST);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(getActivity()).show();
        }
    }

    private void closeSession() {

        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {
                mProDlg.setMessage("Close session...");
                mProDlg.show();
                JSONObject mJsonObj = mParser.getParams(AppConstants.CLOSE_SESSION_REQUEST);

                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.CLOSE_SESSION_REQUEST);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(getActivity()).show();
        }

    }
}


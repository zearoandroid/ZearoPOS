package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.POSOrders;
import com.zearoconsulting.zearopos.presentation.model.Terminals;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.presentation.presenter.ReprintListener;
import com.zearoconsulting.zearopos.presentation.view.adapter.TerminalListAdapter;
import com.zearoconsulting.zearopos.presentation.view.adapter.ViewTransactionListAdapter;
import com.zearoconsulting.zearopos.presentation.view.animations.SimpleDividerItemDecoration;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;
import com.zearoconsulting.zearopos.utils.Common;

import java.net.InetAddress;
import java.util.List;

/**
 * Created by saravanan on 09-01-2017.
 */

public class TerminalPrinterFragment extends AbstractDialogFragment {

    RecyclerView mTerminalsView;
    TerminalListAdapter mTerminalAdapter;
    List<Terminals> mTerminalsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mReboundListener = new ReboundListener();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terminal_list, container, false);
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

        mProDlg = new ProgressDialog(getActivity());
        mProDlg.setIndeterminate(true);
        mProDlg.setCancelable(false);

        mTerminalsView = (RecyclerView) paramView.findViewById(R.id.terminalList);
        mTerminalsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTerminalsView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        mTerminalsList = mDBHelper.getTerminalListData(mAppManager.getClientID(),mAppManager.getOrgID());

        if(mTerminalsList.size()!=0){
            mTerminalAdapter = new TerminalListAdapter(getActivity(), mTerminalsList);
            mTerminalsView.setAdapter(mTerminalAdapter);
            mTerminalAdapter.notifyDataSetChanged();

            new TerminalPingTask().execute();
        }
    }

    private class TerminalPingTask extends AsyncTask<String, String, String> {

        private String resp="";

        @Override
        protected void onPreExecute() {
            mProDlg.setMessage("Validating terminal ip address");
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                for(int i=0; i<mTerminalsList.size();i++){
                    if(Common.isIpAddress(mTerminalsList.get(i).getTerminalIP())) {
                        InetAddress inet = InetAddress.getByName(mTerminalsList.get(i).getTerminalIP());

                        // IPAddress reachable checking.
                        boolean status = inet.isReachable(5000);
                        if (status) {
                            mTerminalsList.get(i).setTerminalStatus("Y");
                        }else{
                            mTerminalsList.get(i).setTerminalStatus("N");
                        }
                    }else{
                        mTerminalsList.get(i).setTerminalStatus("N");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {

            mProDlg.dismiss();
            mTerminalAdapter.refresh(mTerminalsList);
        }
    }
}

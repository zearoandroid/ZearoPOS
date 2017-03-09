package com.zearoconsulting.zearopos.presentation.view.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.BPartner;
import com.zearoconsulting.zearopos.presentation.presenter.OrderStatusListener;
import com.zearoconsulting.zearopos.presentation.view.adapter.BPartnerAdapter;
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
import java.util.ArrayList;
import java.util.List;

public class SelecteCustomer extends BaseActivity implements SearchView.OnQueryTextListener, BPartnerAdapter.AdapterCallback{

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mCustomerListView;
    SearchView mSearchView;

    List<BPartner> mBPList;
    private BPartnerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //super.setTheme( R.style.MyTransparentTheme );
        setContentView(R.layout.activity_selecte_customer);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_customer_swipe_refresh_layout);
        mCustomerListView = (RecyclerView)findViewById(R.id.customer_recycler_view);
        mSearchView = (SearchView)findViewById(R.id.edtSearchCustomer);

        mCustomerListView.setLayoutManager(new LinearLayoutManager(this));
        mSearchView.setOnQueryTextListener(SelecteCustomer.this);

        mBPList = mDBHelper.getBPartners(mAppManager.getClientID(),mAppManager.getOrgID());

        mProDlg = new ProgressDialog(this);
        mProDlg.setIndeterminate(true);
        mProDlg.setMessage("Getting customer...");

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        mCustomerListView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mBPList.size()!=0)
            updateCustomerListView();
        else
            new GetBPartnerTask().execute();
    }

    void refreshItems() {
        // Load items
        // ...
        new GetBPartnerTask().execute();

        // Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onMethodCallback(BPartner bPartner) {

        //AppConstants.isCashCustomer = false;
        //mAppManager.saveCustomerData(bPartner);

        //update poscustomer table based on posId
        if(bPartner.getIsCredit().equalsIgnoreCase("N")) {
            mDBHelper.updatePOSHeaderCustomer(AppConstants.posID, bPartner);
            finish();
        }else{
            //check credit api
            OrderStatusListener.getInstance().customerSelected(bPartner);
            finish();
        }


    }

    private class GetBPartnerTask extends AsyncTask<String, String, String> {

        private String response = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(mBPList!=null){
                mBPList.clear();
            }

            mProDlg.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try
            {
                URL url = new URL(AppConstants.URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject param = mParser.getParams(AppConstants.GET_BPARTNERS);

                OutputStream os = conn.getOutputStream();
                os.write(param.toString().getBytes());
                os.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                System.out.println("Output from Server .... \n");

                //Receive the response from the server
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                response = result.toString();

                if(!response.equalsIgnoreCase("")) {
                   mParser.parseBPartnerJson(response,null);
                }

                conn.disconnect();

            }catch (MalformedURLException e)
            {
                e.printStackTrace();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return response;
        }
        protected void onPostExecute(String response) {

            mProDlg.dismiss();

            try{
                if(!response.equalsIgnoreCase("")){

                    mBPList = mDBHelper.getBPartners(mAppManager.getClientID(),mAppManager.getOrgID());
                    if(mBPList.size()!=0) {
                        updateCustomerListView();
                    }else {
                        Log.i("BPARTNER", "NO BUSINESS PARTNER AVAILABLE");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void updateCustomerListView(){
        //if(mAdapter == null)
        mAdapter = new BPartnerAdapter(SelecteCustomer.this, mBPList);
        mCustomerListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<BPartner> filteredModelList = filter(mBPList, query);
        mAdapter.setFilter(filteredModelList);
        return true;
    }

    private List<BPartner> filter(List<BPartner> models, String query) {
        query = query.toLowerCase();

        final List<BPartner> filteredModelList = new ArrayList<>();
        for (BPartner model : models) {
            final String text = model.getBpName().toLowerCase();
            final String value = model.getBpValue();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }else if (value.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}

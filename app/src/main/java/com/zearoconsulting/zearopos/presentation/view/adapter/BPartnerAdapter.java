package com.zearoconsulting.zearopos.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.BPartner;
import com.zearoconsulting.zearopos.utils.Common;

/**
 * Created by saravanan on 02-06-2016.
 */
public class BPartnerAdapter extends RecyclerView.Adapter<BPartnerAdapter.BPartnerListRowHolder>  {

    private static List<BPartner> mBPartnerList;
    private static AdapterCallback mAdapterCallback;
    private Context mContext;

    public BPartnerAdapter(Context context, List<BPartner> bPartnerList) {
        this.mBPartnerList = bPartnerList;
        this.mContext = context;
        this.mAdapterCallback = ((AdapterCallback) context);
    }

    @Override
    public BPartnerListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_bpatner_row, null);
        BPartnerListRowHolder mh = new BPartnerListRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(BPartnerListRowHolder holder, int pos) {
        final BPartner model = mBPartnerList.get(pos);
        holder.name.setText(model.getBpName());
        holder.bpid.setText(""+model.getBpValue());
        holder.bpCredit.setText(""+ Common.valueFormatter(model.getCreditLimit()));
    }

    @Override
    public int getItemCount() {
        return (null != mBPartnerList ? mBPartnerList.size() : 0);
    }

    public static class BPartnerListRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView name;
        protected TextView bpid;
        protected TextView bpCredit;

        public BPartnerListRowHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name);
            this.bpid = (TextView) view.findViewById(R.id.bpid);
            this.name .setTypeface(AndroidApplication.getGothamRoundedMedium());
            this.bpCredit = (TextView) view.findViewById(R.id.bpCredit);
            this.bpid.setTypeface(AndroidApplication.getGothamRoundedMedium());
            this.bpCredit.setTypeface(AndroidApplication.getGothamRoundedMedium());

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            //String name = mBPartnerList.get(getAdapterPosition()).getBpartnerName();
            //long id = mBPartnerList.get(getAdapterPosition()).getBpartnerId();
            //Toast.makeText(MyApplication.getAppContext(),"Selected Item is "+name+"----"+id,Toast.LENGTH_SHORT).show();
            mAdapterCallback.onMethodCallback(mBPartnerList.get(getAdapterPosition()));
        }
    }

    public void setFilter(List<BPartner> bPartnerModels) {
        mBPartnerList = new ArrayList<>();
        mBPartnerList.addAll(bPartnerModels);
        notifyDataSetChanged();
    }

    public static interface AdapterCallback {
        void onMethodCallback(BPartner bPartner);
    }
}


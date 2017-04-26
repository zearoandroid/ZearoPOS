package com.zearoconsulting.zearopos.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.POSOrders;
import com.zearoconsulting.zearopos.presentation.model.Terminals;
import com.zearoconsulting.zearopos.presentation.presenter.ReprintListener;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;

import java.util.List;

/**
 * Created by saravanan on 28-08-2016.
 */
public class TerminalListAdapter extends RecyclerView.Adapter<TerminalListAdapter.TerminalListRowHolder> {

    private static List<Terminals> mTerminalList;
    private Context mContext;

    public TerminalListAdapter(Context context, List<Terminals> terminalList) {
        this.mTerminalList = terminalList;
        this.mContext = context;
    }

    @Override
    public TerminalListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.terminal_row, null);
        TerminalListRowHolder mh = new TerminalListRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(TerminalListRowHolder holder, int pos) {

        try {
            final Terminals mTerminal = mTerminalList.get(pos);

            holder.terminalName.setText(mTerminal.getTerminalName());
            holder.terminalIP.setText(mTerminal.getTerminalIP());

            if(mTerminal.getTerminalStatus().equalsIgnoreCase("Y")){
                holder.terminalStatus.setImageResource(R.drawable.ic_green);
            }else{
                holder.terminalStatus.setImageResource(R.drawable.ic_red);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (null != mTerminalList ? mTerminalList.size() : 0);
    }


    public class TerminalListRowHolder extends RecyclerView.ViewHolder {
        protected TextView terminalName;
        protected TextView terminalIP;
        protected ImageView terminalStatus;

        public TerminalListRowHolder(View view) {
            super(view);
            this.terminalName = (TextView) view.findViewById(R.id.txtTerminalName);
            this.terminalIP = (TextView) view.findViewById(R.id.txtTerminalIp);
            this.terminalStatus = (ImageView) view.findViewById(R.id.imgPrinterStatus);
        }

    }

    public void refresh(List<Terminals> list) {

        if (list != null) {
            /*if (mTerminalList != null) {
                mTerminalList.clear();
                mTerminalList.addAll(list);
            } else {
                mTerminalList = list;
            }*/
            mTerminalList = list;
            notifyDataSetChanged();
        }
    }

}

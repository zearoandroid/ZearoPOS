package com.zearoconsulting.zearopos.presentation.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.presentation.view.activity.POSActivity;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;
import java.util.List;

/**
 * Created by saravanan on 18-05-2016.
 */
public class OrderItemAdapter extends ArrayAdapter<POSLineItem> {

    private Context context;
    private List<POSLineItem> mOrderData;
    private IPOSListeners mListener;
    int updatePos = -1;

    public OrderItemAdapter(Context context, List<POSLineItem> mListItem,IPOSListeners listener) {
        super(context, R.layout.order_row_item, mListItem);
        this.context = context;
        this.mOrderData = mListItem;
        this.mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
        {
            view = inflater.inflate(R.layout.order_row_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.txtProdName = (TextView) view.findViewById(R.id.txtItemName);
            viewHolder.txtStdPrice = (TextView) view.findViewById(R.id.txtStdPrice);
            viewHolder.txtQty = (TextView) view.findViewById(R.id.txtCount);
            viewHolder.txtPrice = (TextView) view.findViewById(R.id.txtItemPrice);
            viewHolder.txtPlus = (TextView) view.findViewById(R.id.txtPlus);
            viewHolder.txtMinus = (TextView) view.findViewById(R.id.txtMinus);
            viewHolder.txtDiscIndicator = (TextView) view.findViewById(R.id.txtDiscountIndicator);
            viewHolder.btnAddNotes = (ImageView) view.findViewById(R.id.btnAddNotes);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        if(mOrderData!=null){

            holder.txtProdName.setText(mOrderData.get(position).getProductName());
            holder.txtStdPrice.setText("x "+AppConstants.currencyCode+". "+Common.valueFormatter(mOrderData.get(position).getStdPrice()));
            holder.txtQty.setText(""+mOrderData.get(position).getPosQty());
            holder.txtPrice.setText(AppConstants.currencyCode+" "+ Common.valueFormatter(mOrderData.get(position).getTotalPrice()));

            holder.txtDiscIndicator.setBackgroundResource(R.drawable.discount_selection_button_default);
            holder.txtDiscIndicator.setTextColor(Color.parseColor("#ff8000"));

            int disc=mOrderData.get(position).getDiscType();
            double discVal=mOrderData.get(position).getDiscValue();
            if(disc==0){
                holder.txtDiscIndicator.setText("- "+Common.valueFormatter(discVal)+" %");
                holder.txtDiscIndicator.setBackgroundResource(R.drawable.discount_selection_button_pressed);
                holder.txtDiscIndicator.setTextColor(Color.parseColor("#ffffff"));
            }else if(disc == 1){
                holder.txtDiscIndicator.setText("- "+Common.valueFormatter(discVal)+"");
                holder.txtDiscIndicator.setBackgroundResource(R.drawable.discount_selection_button_pressed);
                holder.txtDiscIndicator.setTextColor(Color.parseColor("#ffffff"));
            }

        }

        holder.txtPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update the row
                if (null != mListener) {
                    //if(!AppConstants.isTableService) {
                        mListener.addOrRemoveItemFromCart(mOrderData.get(position).getCategoryId(), mOrderData.get(position), true);
                    //}
                }
            }
        });

        holder.txtMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update the row
                if (null != mListener) {
                    //if(!AppConstants.isTableService) {
                        mListener.addOrRemoveItemFromCart(mOrderData.get(position).getCategoryId(), mOrderData.get(position), false);
                    //}
                }
            }
        });

        holder.txtDiscIndicator.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //show the multieditor fragment
                if(context instanceof POSActivity && !AppConstants.isOrderPrinted){
                    ((POSActivity)context).showMultiEditor(mOrderData.get(position).getCategoryId(),mOrderData.get(position));
                }else if(AppConstants.isOrderPrinted){
                    ((POSActivity)context).showPermission();
                }
            }
        });

        holder.btnAddNotes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //show the add notes fragment
                if(context instanceof POSActivity){
                    ((POSActivity)context).showAddNotes(AppConstants.posID, mOrderData.get(position).getCategoryId(),mOrderData.get(position));
                }
            }
        });

        if(view!=null)
            view.setBackgroundResource(R.drawable.orderitem_default);

        if(mOrderData!=null){
            if(mOrderData.get(position).getIsUpdated().equalsIgnoreCase("Y")){
                view.setBackgroundResource(R.drawable.orderitem_activated);
            }else {
                view.setBackgroundResource(R.drawable.orderitem_default);
            }
        }

        return view;
    }


    public void refreshItem(int pos) {
        updatePos = pos;
    }

    public void refreshListView(List<POSLineItem> orderData) {

       // this.mOrderData.clear();

        this.mOrderData = orderData;

        //get the data from database and render it...
        // this.mOrderData.add("Product");
       // this.mOrderData.addAll(orderData);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        protected TextView txtProdName;
        protected TextView txtStdPrice;
        protected TextView txtQty;
        protected TextView txtPrice;
        protected TextView txtPlus;
        protected TextView txtMinus;
        protected TextView txtDiscIndicator;
        protected ImageView btnAddNotes;
    }

}

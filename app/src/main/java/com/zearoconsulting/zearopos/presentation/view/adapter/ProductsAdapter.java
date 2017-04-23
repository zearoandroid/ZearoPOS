package com.zearoconsulting.zearopos.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.rebound.Spring;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.data.DBHelper;
import com.zearoconsulting.zearopos.data.POSDataSource;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.Product;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;
import com.zearoconsulting.zearopos.utils.AnimationUtil;
import com.zearoconsulting.zearopos.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saravanan on 02-06-2016.
 */
public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductListRowHolder>  {

    private static List<Product> mProductList;
    private Context mContext;
    private IPOSListeners mListener;
    long mCategoryId;
    private AppDataManager mAppDataManager;
    private POSDataSource mDBHelper;
    List<POSLineItem> lineItems;
    private List<Long> mProductIDList;

    public ProductsAdapter(Context context, POSDataSource dbHelper, AppDataManager appDataManager, List<Product> productList, long categoryId, IPOSListeners listener) {
        this.mProductList = productList;
        this.mContext = context;
        this.mCategoryId = categoryId;
        this.mListener = listener;
        this.mDBHelper = dbHelper;
        this.mAppDataManager = appDataManager;
        this.lineItems = mDBHelper.getPOSLineItems(AppConstants.posID,mCategoryId);
        this.mProductIDList = mDBHelper.getProductIDs(mAppDataManager.getClientID(), mAppDataManager.getOrgID(),mCategoryId);
    }

    @Override
    public ProductListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_product, null);
        ProductListRowHolder mh = new ProductListRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(ProductListRowHolder holder, int pos) {
        final Product model = mProductList.get(pos);
        holder.name.setText(model.getProdName());
        holder.name.setAlpha(.8f);
       //holder.price.setText(AppConstants.currencyCode+". "+ Common.valueFormatter(model.getSalePrice()));
        holder.mView.setBackgroundResource(R.drawable.orderitem_default);

        //holder.mLayout.setVisibility(View.INVISIBLE);
        holder.mLayout.setVisibility(View.GONE);

       /* if(!mProductList.get(pos).getProdImage().equalsIgnoreCase("")){
            //we are getting outofmemory error
            Bitmap mBitMap = Common.decodeBase64(mProductList.get(pos).getProdImage());
            holder.imageView.setImageBitmap(mBitMap);
        }*/

        Glide.with(mContext)
                .load(mProductList.get(pos).getProdImage())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into( holder.imageView);

        //AnimationUtil.aimateProduct(holder);

        //holder.txtCount.setText(String.valueOf(model.getDefaultQty()));

        if(model.getDefaultQty()!=0){
            holder.mView.setBackgroundResource(R.drawable.orderitem_pressed);
            //holder.mLayout.setVisibility(View.VISIBLE);
            holder.mLayout.setVisibility(View.GONE);
            holder.txtCount.setText(String.valueOf(model.getDefaultQty()));
        }

        for(int i=0; i<lineItems.size(); i++){
            if(lineItems.get(i).getProductId() == model.getProdId()){
                holder.mView.setBackgroundResource(R.drawable.orderitem_pressed);
                //holder.mLayout.setVisibility(View.VISIBLE);
                holder.mLayout.setVisibility(View.GONE);
                holder.txtCount.setText(String.valueOf(lineItems.get(i).getPosQty()));
                model.setDefaultQty(lineItems.get(i).getPosQty());
            }
        }

       holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener)
                {
                    //if(!AppConstants.isTableService){
                        if(!AppConstants.isOrderPrinted){

                            int count = Integer.parseInt(holder.txtCount.getText().toString());
                            count++;
                            holder.txtCount.setText(String.valueOf(count));
                            model.setDefaultQty(count);

                            holder.mView.setBackgroundResource(R.drawable.orderitem_pressed);
                            //holder.mLayout.setVisibility(View.VISIBLE);
                            holder.mLayout.setVisibility(View.GONE);
                        }

                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onUpdateProductToCart(model.getCategoryId(),model,true);
                    //}

                }
            }
        });

        holder.txtPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    //if(!AppConstants.isTableService) {
                        int count = Integer.parseInt(holder.txtCount.getText().toString());
                        count++;
                        holder.txtCount.setText(String.valueOf(count));
                        model.setDefaultQty(count);

                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onUpdateProductToCart(model.getCategoryId(), model, true);

                        //notifyDataSetChanged();
                    //}
                }
            }
        });

        holder.txtMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    //if(!AppConstants.isTableService) {
                        int count = Integer.parseInt(holder.txtCount.getText().toString());
                        count--;
                        if (count != 0) {
                            holder.txtCount.setText(String.valueOf(count));
                            model.setDefaultQty(count);
                        } else if (count == 0) {
                            holder.mView.setBackgroundResource(R.drawable.orderitem_default);
                            holder.mLayout.setVisibility(View.INVISIBLE);
                            holder.txtCount.setText(String.valueOf(count));
                            model.setDefaultQty(count);
                        }

                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onUpdateProductToCart(model.getCategoryId(), model, false);
                    //}
                }
            }
        });
    }

    public void updateItemAtPosition(int position, boolean addOrRemove) {
        if(mProductList.size()>=position) {
            try {
                Product product = mProductList.get(position);

                int val = product.getDefaultQty();

                if (addOrRemove)
                    val++;
                else
                    val--;

                product.setDefaultQty(val);
                mProductList.set(position, product);

                lineItems.clear();
                lineItems = mDBHelper.getPOSLineItems(AppConstants.posID, mCategoryId);

                notifyItemChanged(position);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void updateItemAtPosition(int position, int qty) {
        if(mProductList.size()>=position) {
            try {
                Product product = mProductList.get(position);
                product.setDefaultQty(qty);
                mProductList.set(position, product);

                lineItems.clear();
                lineItems = mDBHelper.getPOSLineItems(AppConstants.posID, mCategoryId);

                notifyItemChanged(position);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void clearAllItem() {
        try {
            lineItems.clear();
            lineItems = mDBHelper.getPOSLineItems(AppConstants.posID, mCategoryId);

            //if(lineItems.size()==0)
            // return;

            for (int i = 0; i < mProductList.size(); i++) {
                Product product = mProductList.get(i);
                product.setDefaultQty(0);
                mProductList.set(i, product);
                notifyItemChanged(i);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (null != mProductList ? mProductList.size() : 0);
    }

    public static class ProductListRowHolder extends RecyclerView.ViewHolder{
        protected TextView name;
        protected TextView txtCount;
        protected TextView txtPlus;
        protected TextView txtMinus;
        protected ImageView imageView;
        protected LinearLayout mLayout;
        public final View mView;

        public ProductListRowHolder(View view) {
            super(view);
            mView = view;
            this.name = (TextView) view.findViewById(R.id.txtProductName);
            this.txtCount = (TextView) view.findViewById(R.id.txtCount);
            this.imageView = (ImageView) view.findViewById(R.id.imgProduct);
            this.mLayout = (LinearLayout)view.findViewById(R.id.layProductActions);
            this.txtPlus = (TextView) view.findViewById(R.id.txtPlus);
            this.txtMinus = (TextView) view.findViewById(R.id.txtMinus);
        }
    }

    public void refresh(List<Product> list, long categoryId){

        if(list!=null){
            if (mProductList != null) {
                mProductList.clear();
                mProductList.addAll(list);
            }
            else {
                mProductList = list;
            }
            notifyDataSetChanged();

            mCategoryId = categoryId;

            //check the each product if its already in cart
            if(mCategoryId!=0){
                lineItems.clear();
                lineItems = mDBHelper.getPOSLineItems(AppConstants.posID,mCategoryId);

                if(lineItems.size()==0)
                    return;

                mProductIDList.clear();
                mProductIDList = mDBHelper.getProductIDs(mAppDataManager.getClientID(), mAppDataManager.getOrgID(),mCategoryId);

                if(mProductIDList.size()==0)
                    return;

                for (int i=0; i<lineItems.size();i++){

                    int position= mProductIDList.indexOf(lineItems.get(i).getProductId());

                    if(position!=-1){
                        Product product = mProductList.get(position);
                        product.setDefaultQty(lineItems.get(i).getPosQty());
                        mProductList.set(position, product);

                        notifyItemChanged(position);
                    }

                }

            }
        }
    }

}

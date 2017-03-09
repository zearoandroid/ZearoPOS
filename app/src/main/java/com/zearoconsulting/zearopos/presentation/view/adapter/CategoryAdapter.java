package com.zearoconsulting.zearopos.presentation.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.data.AppDataManager;
import com.zearoconsulting.zearopos.data.DBHelper;
import com.zearoconsulting.zearopos.data.POSDataSource;
import com.zearoconsulting.zearopos.presentation.model.Category;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.utils.AnimationUtil;

import java.util.List;

/**
 * Created by saravanan on 08-09-2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryListRowHolder> {

    private static List<Category> mCategoryList;
    private POSDataSource mDBHelper;
    private AppDataManager mAppDataManager;
    private Context mContext;
    private IPOSListeners mListener;
    int selected_position = 0;

    public static class CategoryListRowHolder extends RecyclerView.ViewHolder{
        protected TextView name;
        protected ImageView imageView;
        public final View mView;
        protected CardView mCategoryLayout;

        public CategoryListRowHolder(View view) {
            super(view);
            mView = view;
            this.mCategoryLayout = (CardView) view.findViewById(R.id.category_view);
            this.name = (TextView) view.findViewById(R.id.txtCategoryName);
            this.imageView = (ImageView) view.findViewById(R.id.imgCategory);
        }
    }

    public CategoryAdapter(Context context, POSDataSource dbHelper, AppDataManager appDataManager, List<Category> categoryList, IPOSListeners listener) {
        this.mCategoryList = categoryList;
        this.mContext = context;
        this.mListener = listener;
        this.mDBHelper = dbHelper;
        this.mAppDataManager = appDataManager;
    }

    @Override
    public CategoryListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_category, null);
        CategoryListRowHolder mh = new CategoryListRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(CategoryListRowHolder holder, int pos) {

        if(mCategoryList!=null) {

            if(selected_position == pos){
                // Here I am just highlighting the background
                holder.mCategoryLayout.setCardBackgroundColor(0xFF9CBDFF);
            }else{
                holder.mCategoryLayout.setCardBackgroundColor(Color.WHITE);
            }

            final Category model = mCategoryList.get(pos);
            holder.name.setText(model.getCategoryName());
            holder.name.setAlpha(.8f);

            //AnimationUtil.aimateCategory(holder);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Updating old as well as new positions
                    notifyItemChanged(selected_position);
                    selected_position = pos;
                    notifyItemChanged(selected_position);

                    // Do your another stuff for your onClick

                    mListener.onSelectedCategory(model);
                }
            });

           //String path =  mDBHelper.getCategoryImage(mAppDataManager.getClientID(), mAppDataManager.getOrgID(), model.getCategoryId());

            Glide.with(mContext)
                    .load(model.getCategoryImage())
                    .into( holder.imageView);


        }
    }

    @Override
    public int getItemCount() {
        return (null != mCategoryList ? mCategoryList.size() : 0);
    }
}

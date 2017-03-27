package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.Category;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.Product;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.presentation.view.activity.POSActivity;
import com.zearoconsulting.zearopos.presentation.view.adapter.ProductsAdapter;
import com.zearoconsulting.zearopos.presentation.view.component.GridSpacingItemDecoration;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;

import java.util.List;

/**
 * Created by saravanan on 08-09-2016.
 */
public class ProductListFragment extends AbstractFragment {

    private RecyclerView mProductRecyclerView;
    private List<Category> mCategoryList;
    private Context mContext;
    private IPOSListeners mListener;
    private List<Product> mProductList;
    private List<Long> mProductIDList;
    private long mCategoryId = 0;
    private ProductsAdapter mProdAdapter = null;
    private Product mProduct;

    int spanCount = 0; // NUMBER OF COLUMNS
    int spacing = 0; // SPACING ISSUE

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null)
        mCategoryId = bundle.getLong("categoryId", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        mProductRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_products);

        double inchSize = Common.getDeviceInchSize(getActivity());

        if(AppConstants.isMobile) {
            mProductRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
            spanCount = 3;
            spacing = 4;
            boolean includeEdge = true;
            mProductRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        }else {
            if(inchSize>15){
                mProductRecyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
                spanCount = 5;
                spacing = 8;
            }else{
                mProductRecyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
                spanCount = 3;
                spacing = 8;
            }

            String model = android.os.Build.MODEL;
            if(model.contains("SM-T677")){
                mProductRecyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
                spanCount = 5;
                spacing = 8;
            }

            boolean includeEdge = true;
            mProductRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        if (context instanceof IPOSListeners) {
            mListener = (IPOSListeners) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IPOSListeners");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            mCategoryList = mDBHelper.getCategory(mAppManager.getClientID(), mAppManager.getOrgID());

            if (mCategoryList != null && mCategoryList.size() != 0 && mCategoryId == 0)
                mCategoryId = mCategoryList.get(0).getCategoryId();

            mProductList = mDBHelper.getProducts(mAppManager.getClientID(), mAppManager.getOrgID(), mCategoryId);
            mProductIDList = mDBHelper.getProductIDs(mAppManager.getClientID(), mAppManager.getOrgID(), mCategoryId);

            ((POSActivity) mContext).setProductList(mProductList);

            if (mCategoryList != null && mProductList != null) {
                if (mProdAdapter == null && mProductList.size() != 0) {
                    mProdAdapter = new ProductsAdapter(mContext, mDBHelper, mAppManager, mProductList, mCategoryId, mListener);
                    mProductRecyclerView.setAdapter(mProdAdapter);
                    mProdAdapter.notifyDataSetChanged();
                } else {
                    //mProdAdapter = new ProductsAdapter(mContext, mDBHelper, mProductList, mCategoryId, mListener);
                    if (mProductList != null && mProdAdapter != null) {
                        mProductRecyclerView.setAdapter(mProdAdapter);
                        mProdAdapter.refresh(mProductList, mCategoryId);
                        mProdAdapter.notifyDataSetChanged();
                    } else {
                        mProdAdapter = new ProductsAdapter(mContext, mDBHelper, mAppManager, mProductList, mCategoryId, mListener);
                        mProductRecyclerView.setAdapter(mProdAdapter);
                        mProdAdapter.notifyDataSetChanged();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void refreshProducts(long categoryId){
        //set the categoryId
        mCategoryId = categoryId;

        try {
            //get the products based on category
            mProductList = mDBHelper.getProducts(mAppManager.getClientID(), mAppManager.getOrgID(),categoryId);

            ((POSActivity) mContext).setProductList(mProductList);

            if (mProdAdapter != null)
                mProdAdapter.refresh(mProductList, mCategoryId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateProduct(long categoryId, POSLineItem mProduct, boolean addOrRemove){
        if(categoryId != mCategoryId)
            return;
        else{
            if (mProdAdapter != null){
                mProductIDList.clear();
                mProductIDList = mDBHelper.getProductIDs(mAppManager.getClientID(), mAppManager.getOrgID(),mCategoryId);

                if(mProductIDList.size()==0)
                    return;

                int position= mProductIDList.indexOf(mProduct.getProductId());

                if(position!=-1)
                mProdAdapter.updateItemAtPosition(position, addOrRemove);
            }
        }
    }

    public void updateProduct(long categoryId, long prodId,int qty){

        if(categoryId != mCategoryId)
            return;

        if (mProdAdapter != null){

            mProductIDList.clear();
            mProductIDList = mDBHelper.getProductIDs(mAppManager.getClientID(), mAppManager.getOrgID(),mCategoryId);

            if(mProductIDList.size()==0)
                return;

            int position= mProductIDList.indexOf(prodId);

            if(position!=-1)
            mProdAdapter.updateItemAtPosition(position, qty);
        }
    }

    public void clearAllProduct(){
        if (mProdAdapter != null){
            mProdAdapter.clearAllItem();
        }
    }

    public void searchFilter(List<Product> models, long categoryId){
        if(mProdAdapter!=null){
            mProdAdapter.refresh(models, categoryId);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}

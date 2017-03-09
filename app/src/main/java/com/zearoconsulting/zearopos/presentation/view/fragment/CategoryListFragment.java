package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.Category;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.presentation.view.adapter.CategoryAdapter;
import com.zearoconsulting.zearopos.presentation.view.component.GridSpacingItemDecoration;
import com.zearoconsulting.zearopos.utils.AppConstants;

import java.util.List;

/**
 * Created by saravanan on 08-09-2016.
 */
public class CategoryListFragment extends AbstractFragment{

    private RecyclerView mCategoryRecyclerView;
    private List<Category> mCategoryList;
    private Context mContext;
    private IPOSListeners mListener;
    private CategoryAdapter mCategoryAdapter;

    int spanCount = 0; // NUMBER OF COLUMNS
    int spacing = 0; // SPACING ISSUE

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        mCategoryRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_category);

        if(AppConstants.isMobile) {
            mCategoryRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
            spanCount = 2;
            spacing = 4;
            boolean includeEdge = true;
            mCategoryRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        }else{
            mCategoryRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 1));
            spanCount = 1;
            spacing = 2;
            boolean includeEdge = true;
            mCategoryRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
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

        mCategoryList = mDBHelper.getCategory(mAppManager.getClientID(), mAppManager.getOrgID());

        if (mCategoryList != null) {
            if (mCategoryAdapter == null && mCategoryList.size() != 0) {
                mCategoryAdapter = new CategoryAdapter(mContext, mDBHelper, mAppManager, mCategoryList, mListener);
                mCategoryRecyclerView.setAdapter(mCategoryAdapter);
                mCategoryAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

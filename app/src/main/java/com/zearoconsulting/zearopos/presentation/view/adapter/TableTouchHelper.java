package com.zearoconsulting.zearopos.presentation.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.zearoconsulting.zearopos.presentation.model.Tables;

import java.util.List;

/**
 * Created by saravanan on 18-12-2016.
 */

public class TableTouchHelper extends ItemTouchHelper.SimpleCallback {
    private TableAdapter mTableAdapter;
    List<Tables> mKOTTableList;

    private Integer mFrom = null;
    private Integer mTo = null;

    public TableTouchHelper(List<Tables> kotTableList,  TableAdapter tableAdapter){
        super(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        this.mKOTTableList = kotTableList;
        this.mTableAdapter = tableAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        // remember FIRST from position
        if (mFrom == null)
            mFrom = viewHolder.getAdapterPosition();
        mTo = target.getAdapterPosition();


        // mMovieAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        //mTableAdapter.tableChange(mFrom, mTo);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
       // mMovieAdapter.remove(viewHolder.getAdapterPosition());
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (mFrom != null && mTo != null)
            mTableAdapter.tableChange(mFrom, mTo);

        // clear saved positions
        mFrom = mTo = null;
    }
}

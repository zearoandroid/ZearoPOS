package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.Tables;
import com.zearoconsulting.zearopos.presentation.view.activity.POSActivity;
import com.zearoconsulting.zearopos.presentation.view.adapter.TableSpinner;
import com.zearoconsulting.zearopos.presentation.view.adapter.WarehouseSpinner;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by saravanan on 08-05-2017.
 */

public class TableChangeFragment extends AbstractDialogFragment {

    private static Context context;
    private TextView mTxtCurrentTableName;
    private FancyButton mBtnSubmit;
    private FancyButton mBtnCancel;
    private Spinner mSpnTableChange;
    TableSpinner mTablesAdapter;
    private long mCurrentTableId=0, mChangeTableId=0;
    private List<Tables> mTableList = null;
    int mCurrentTableSelection;

    public static TableChangeFragment newInstance(Context paramContext, long tableId) {
        TableChangeFragment tableChangeFragment = new TableChangeFragment();
        Bundle localBundle = new Bundle();
        localBundle.putLong("currentTableId", tableId);
        tableChangeFragment.setArguments(localBundle);
        context = paramContext;
        return tableChangeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mReboundListener = new ReboundListener();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_table_change, container, false);
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

        Bundle bundle = getArguments();
        if(bundle!=null){
            this.mCurrentTableId = bundle.getLong("currentTableId", 0);
        }


        getDialog().getWindow().setSoftInputMode(3);
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().setCanceledOnTouchOutside(false);


        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode, android.view.KeyEvent event) {

                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    //Stop back event here!!!
                    return true;
                } else
                    return false;
            }
        });

        this.mTxtCurrentTableName = (TextView) paramView.findViewById(R.id.txtCurrentTable);
        this.mBtnSubmit = ((FancyButton) paramView.findViewById(R.id.submitTableChange));
        this.mBtnCancel = ((FancyButton) paramView.findViewById(R.id.cancelTableChange));
        this.mSpnTableChange = ((Spinner) paramView.findViewById(R.id.spnSelectTable));

        Tables table = mDBHelper.getTableData(mAppManager.getClientID(), mAppManager.getOrgID(), mCurrentTableId);
        mTxtCurrentTableName.setText(table.getTableName());

        mTableList = mDBHelper.getNonOrderTables(mAppManager.getClientID(), mAppManager.getOrgID());
        if(mTableList.size()!=0) {
            mTablesAdapter = new TableSpinner(getActivity(),mTableList);
            mSpnTableChange.setAdapter(mTablesAdapter);
            mTablesAdapter.notifyDataSetChanged();
        }

        mSpnTableChange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentTableSelection != position && mTableList.size()!=0){
                    mChangeTableId = mTableList.get(position).getTableId();
                }
                mCurrentTableSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCurrentTableSelection = mSpnTableChange.getSelectedItemPosition();
        mChangeTableId = mTableList.get(mCurrentTableSelection).getTableId();

        // Add an OnTouchListener to the root view.
        this.mBtnCancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mReboundListener.animateView(mBtnCancel);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // When pressed start solving the spring to 1.
                        mSpring.setEndValue(1);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // When released start solving the spring to 0.
                        mSpring.setEndValue(0);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });

        // Add an OnTouchListener to the root view.
        this.mBtnSubmit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mReboundListener.animateView(mBtnSubmit);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // When pressed start solving the spring to 1.
                        mSpring.setEndValue(1);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // When released start solving the spring to 0.
                        mSpring.setEndValue(0);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((POSActivity) TableChangeFragment.this.getActivity()).tableChange(mCurrentTableId,mChangeTableId);
                                dismissAllowingStateLoss();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });
    }
}

package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.view.activity.POSActivity;

import java.util.List;

/**
 * Created by saravanan on 14-12-2016.
 */

public class ShowInvoiceListFragment extends AbstractDialogFragment{

    private static Context context;
    private long tableId;
    private ListView mInvoiceListView;


    public static ShowInvoiceListFragment newInstance(Context paramContext, long paramString1) {
        ShowInvoiceListFragment invoiceListFragment = new ShowInvoiceListFragment();
        Bundle localBundle = new Bundle();
        localBundle.putLong("tableId", paramString1);
        invoiceListFragment.setArguments(localBundle);
        context = paramContext;
        return invoiceListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invoices, container, false);
    }

    @Override
    public void onViewCreated(View paramView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(paramView, savedInstanceState);

        this.tableId = getArguments().getLong("tableId", 0);

        getDialog().getWindow().setSoftInputMode(3);
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().setCanceledOnTouchOutside(true);


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

        this.mInvoiceListView= (ListView) paramView.findViewById(R.id.invoiceListView);

        List<Long> invNumList = mDBHelper.getKOTInvoiceNumbers(tableId);

        ArrayAdapter<Long> adapter = new ArrayAdapter<Long>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, invNumList);
        this.mInvoiceListView.setAdapter(adapter);

        this.mInvoiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((POSActivity) ShowInvoiceListFragment.this.getActivity()).showSelectedInvoice(invNumList.get(position));
                dismiss();
            }
        });
    }
}

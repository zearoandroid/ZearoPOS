package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.Notes;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.Product;
import com.zearoconsulting.zearopos.presentation.view.activity.POSActivity;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by saravanan on 16-05-2017.
 */

public class AddNotesFragment extends AbstractDialogFragment implements AdapterView.OnItemSelectedListener {

    private static Context context;
    private FancyButton mBtnSubmit;
    private FancyButton mBtnCancel;
    //private POSLineItem posLineItem;
    private long mPOSId=0, mRowId = 0, mProdId =0;
    private String isKOT, notes;
    ImageView imageOfFood;
    TextView foodName;
    TextView addNote;
    EditText note;
    Spinner mSpnNotes;
    List<Notes> notesList;
    List<String> mNotesList;
    private String noteToCart="";
    private String selectedNotes="";
    private LinearLayout mLayChoice;

    public static AddNotesFragment newInstance(Context paramContext, long posId, long rowId, long prodId,String isKOT,String notes) {
        AddNotesFragment addNotesFragment = new AddNotesFragment();
        Bundle localBundle = new Bundle();
        localBundle.putLong("posID", posId);
        localBundle.putLong("rowID", rowId);
        localBundle.putLong("prodID", prodId);
        localBundle.putString("isKOT", isKOT);
        localBundle.putString("notes", notes);
        addNotesFragment.setArguments(localBundle);
        context = paramContext;
        return addNotesFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mReboundListener = new ReboundListener();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addnotes, container, false);
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
            this.mPOSId = bundle.getLong("posID", 0);
            this.mRowId = bundle.getLong("rowID", 0);
            this.mProdId = bundle.getLong("prodID", 0);
            this.isKOT = bundle.getString("isKOT","N");
            this.notes = bundle.getString("notes","");
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

        this.foodName = ((TextView) paramView.findViewById(R.id.foodName));
        this.addNote = ((TextView) paramView.findViewById(R.id.addNote));
        this.note = ((EditText) paramView.findViewById(R.id.textNote));
        this.imageOfFood = ((ImageView) paramView.findViewById(R.id.foodImage));
        this.mSpnNotes = ((Spinner) paramView.findViewById(R.id.spnNotes));
        this.mLayChoice = ((LinearLayout) paramView.findViewById(R.id.choiceOfLayout));

        this.mBtnSubmit = ((FancyButton) paramView.findViewById(R.id.submitAddNotes));
        this.mBtnCancel = ((FancyButton) paramView.findViewById(R.id.cancelAddNotes));

        Product product = mDBHelper.getProduct(mAppManager.getClientID(),mAppManager.getOrgID(),mProdId);
        notesList = mDBHelper.getNotes(mAppManager.getClientID(), mAppManager.getOrgID(), product.getProdId());

        Glide.with(this)
                .load(product.getProdImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(this.imageOfFood);

        this.foodName.setText(product.getProdName());

        //posLineItem = mDBHelper.getPOSLineItem(mPOSId, mRowId);

        if (notesList.size() != 0) {

            mLayChoice.setVisibility(View.VISIBLE);

            // Spinner click listener
            mSpnNotes.setOnItemSelectedListener(this);

            mNotesList = mDBHelper.getNotesList(mAppManager.getClientID(), mAppManager.getOrgID(), product.getProdId());

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mNotesList);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            mSpnNotes.setAdapter(dataAdapter);
        }else{
            mLayChoice.setVisibility(View.GONE);
        }

        AddNotesFragment.this.note.setText(this.notes);

        if(this.isKOT.equalsIgnoreCase("N")){
            this.mBtnSubmit.setVisibility(View.VISIBLE);
        }else{
            this.mBtnSubmit.setVisibility(View.GONE);
        }

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

                                if(selectedNotes.equalsIgnoreCase("Choose one"))
                                    selectedNotes = "";

                                if (notesList.size() != 0) {
                                    if (AddNotesFragment.this.note.getText().toString().trim().length() != 0 && selectedNotes.length() != 0)
                                        noteToCart = noteToCart + selectedNotes + "," + AddNotesFragment.this.note.getText().toString();
                                    else if (AddNotesFragment.this.note.getText().toString().trim().length() == 0 && selectedNotes.length() != 0)
                                        noteToCart = noteToCart + selectedNotes;
                                    else if (AddNotesFragment.this.note.getText().toString().trim().length() != 0 && selectedNotes.length() == 0)
                                        noteToCart = noteToCart + AddNotesFragment.this.note.getText().toString();
                                }else {
                                    noteToCart = noteToCart + AddNotesFragment.this.note.getText().toString();
                                }

                                if(noteToCart.length()!=0)
                                    if (noteToCart.endsWith(","))
                                        noteToCart = noteToCart.substring(0, noteToCart.length() - 1);

                                mDBHelper.updatePOSLineItemNotes(mPOSId,mRowId,mProdId,noteToCart);

                                ((POSActivity) AddNotesFragment.this.getActivity()).showSelectedInvoice(mPOSId);
                                dismissAllowingStateLoss();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {
        selectedNotes = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}

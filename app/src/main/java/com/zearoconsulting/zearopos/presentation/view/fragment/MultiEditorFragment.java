package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.Product;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;

import java.util.List;
import java.util.Map;


public class MultiEditorFragment extends AbstractFragment implements View.OnClickListener {

    private OnMultiEditListener mListener;

    TextView mTxtCalculatedAmount;
    TextView mTxtOriginalAmount;
    TextView mDescription;
    TextView mTxtTempDiscountView;
    TextView mTxtDiscountLabel;
    Button mBtnOne;
    Button mBtnTwo;
    Button mBtnThree;
    Button mBtnFour;
    Button mBtnFive;
    Button mBtnSix;
    Button mBtnSeven;
    Button mBtnEight;
    Button mBtnNine;
    Button mBtnZero;
    ImageButton mBtnDelete;
    ImageButton mBtnSubmit;
    Button mBtnDiscountQty;
    SwitchCompat mDiscountSwitch;
    RelativeLayout mBtnBackToList;
    RelativeLayout mDiscountSwitchLayout;

    private double mTotalAmt,mRealAmt;
    private boolean mIsTotal;
    private long mPosId, mProdId, mKotLineId, mRowId;
    private int mProdQty;
    private double mProdPrice;

    private int mDiscType = 0; int mDiscValue = 0;
    private String discValue = "0";
    private Map<String, Object> mDictNoDisc,mDictDisc;
    private double mTotalAmount=0, mTotalDiscAmount=0;
    POSLineItem posLineItem;

    public MultiEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mIsTotal = bundle.getBoolean("IsTotalAmount", true);

        // IF mIsTotal == FALSE product will be edit
        if(!mIsTotal){
            mRowId = bundle.getLong("rowId");
            mPosId = bundle.getLong("posId");
            posLineItem = mDBHelper.getPOSLineItem(mPosId, mRowId);
            mKotLineId = posLineItem.getKotLineId();
            mProdId = posLineItem.getProductId();
            mProdQty = posLineItem.getPosQty();
            mProdPrice = posLineItem.getStdPrice();
            mDiscType = posLineItem.getDiscType();
            mDiscValue = (int)posLineItem.getDiscValue();
            discValue= String.valueOf(mDiscValue);
            mTotalAmt = mProdQty*mProdPrice;
            mRealAmt = mTotalAmt;
        }else {
            mPosId = bundle.getLong("posId");
            mTotalAmt = bundle.getDouble("TotalAmount", 0);
            mRealAmt = mTotalAmt;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multi_editor, container, false);
        //ButterKnife.bind(this,view);

        mTxtCalculatedAmount = (TextView) view.findViewById(R.id.txtCalculatedAmount);
        mTxtOriginalAmount = (TextView) view.findViewById(R.id.txtOriginalAmount);
        mDescription = (TextView) view.findViewById(R.id.txtDescription);
        mTxtTempDiscountView = (TextView) view.findViewById(R.id.txtTempDiscountView);
        mTxtDiscountLabel = (TextView) view.findViewById(R.id.txtLabel);
        mBtnOne = (Button) view.findViewById(R.id.btnOne);
        mBtnTwo = (Button) view.findViewById(R.id.btnTwo);
        mBtnThree = (Button) view.findViewById(R.id.btnThree);
        mBtnFour = (Button) view.findViewById(R.id.btnFour);
        mBtnFive = (Button) view.findViewById(R.id.btnFive);
        mBtnSix = (Button) view.findViewById(R.id.btnSix);
        mBtnSeven = (Button) view.findViewById(R.id.btnSeven);
        mBtnEight = (Button) view.findViewById(R.id.btnEight);
        mBtnNine = (Button) view.findViewById(R.id.btnNine);
        mBtnZero = (Button) view.findViewById(R.id.btnZero);
        mBtnDelete = (ImageButton) view.findViewById(R.id.btnClear);
        mBtnSubmit = (ImageButton) view.findViewById(R.id.btnDone);
        mBtnDiscountQty = (Button) view.findViewById(R.id.btnQtyDiscount);
        mDiscountSwitch = (SwitchCompat) view.findViewById(R.id.discount_switch);
        mBtnBackToList = (RelativeLayout) view.findViewById(R.id.layBackToList);
        mDiscountSwitchLayout = (RelativeLayout) view.findViewById(R.id.laySwitchCompat);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBtnOne.setOnClickListener(this);
        mBtnTwo.setOnClickListener(this);
        mBtnThree.setOnClickListener(this);
        mBtnFour.setOnClickListener(this);
        mBtnFive.setOnClickListener(this);
        mBtnSix.setOnClickListener(this);
        mBtnSeven.setOnClickListener(this);
        mBtnEight.setOnClickListener(this);
        mBtnNine.setOnClickListener(this);
        mBtnZero.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        mBtnDiscountQty.setOnClickListener(this);
        mBtnBackToList.setOnClickListener(this);

        mTxtOriginalAmount.setPaintFlags(mTxtOriginalAmount.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

        if(!mIsTotal){

            List<POSLineItem> extraLineItems = mDBHelper.getPOSExtraLineItems(mKotLineId);
            if(extraLineItems.size()!=0){
                double price = 0;
                for(int j=0; j<extraLineItems.size(); j++){
                    price = price + (extraLineItems.get(j).getPosQty() * extraLineItems.get(j).getStdPrice());
                }
                mTotalAmt = mTotalAmt+price;
            }

            mRealAmt = mTotalAmt;

            mTxtOriginalAmount.setText(Common.valueFormatter(mTotalAmt));
            mTxtCalculatedAmount.setText(Common.valueFormatter(mTotalAmt));
            mTxtTempDiscountView.setText(discValue);
            //set the switch to ON
            if(mDiscType == 0) {
                mDiscountSwitch.setChecked(true);
                mDescription.setText("Enter a discount percentage");
                mListener.OnUpdateDiscountView(mIsTotal, mDiscType);
                updateDiscountValue(mDiscType,discValue);
                mTotalAmt = mTotalAmt - (mTotalAmt*mDiscValue/100);
                mTxtCalculatedAmount.setText(Common.valueFormatter(mTotalAmt));
                mTxtDiscountLabel.setText("Discount by %");
            }
            else {
                mDiscountSwitch.setChecked(false);
                mDescription.setText("Enter a discount amount");
                mListener.OnUpdateDiscountView(mIsTotal, mDiscType);
                updateDiscountValue(mDiscType,discValue);
                mTotalAmt = mTotalAmt - mDiscValue;
                mTxtCalculatedAmount.setText(Common.valueFormatter(mTotalAmt));
                mTxtDiscountLabel.setText("Discount by amount");
            }
        }else {
            //invisible qty button
            mBtnDiscountQty.setVisibility(View.GONE);

            double mTotalOriginalAmt =  mDBHelper.sumOfProductsWithoutDiscount(mPosId);
            mTotalAmt = mDBHelper.sumOfProductsTotalPrice(mPosId);
            mRealAmt = mTotalAmt;

            mTxtOriginalAmount.setText(Common.valueFormatter(mTotalOriginalAmt));
            mTxtCalculatedAmount.setText(Common.valueFormatter(mTotalAmt));

            mTxtOriginalAmount.setVisibility(View.INVISIBLE);

            if(mTotalOriginalAmt!=mTotalAmt)
                mTxtOriginalAmount.setVisibility(View.VISIBLE);

            mDiscType = mDBHelper.getTotalDiscType(mPosId);
            mDiscValue = mDBHelper.getTotalDiscValue(mPosId);
            mTxtTempDiscountView.setText(String.valueOf(mDiscValue));

            //set the switch to ON
            if(mDiscType == 0) {
                mDiscountSwitch.setChecked(true);
                mDescription.setText("Enter a percentage to discount total bill");
                mListener.OnUpdateDiscountView(mIsTotal, mDiscType);
                updateDiscountValue(mDiscType,String.valueOf(mDiscValue));
                //mTotalAmt = mTotalAmt - (mTotalAmt*mDiscValue/100);
                //mTxtCalculatedAmount.setText(Common.valueFormatter(mTotalAmt));
                mTxtDiscountLabel.setText("Discount by %");
            }
            else {
                mDiscountSwitch.setChecked(false);
                mDescription.setText("Enter a amount to discount total bill");
                mListener.OnUpdateDiscountView(mIsTotal, mDiscType);
                updateDiscountValue(mDiscType,String.valueOf(mDiscValue));
                //mTotalAmt = mTotalAmt - mDiscValue;
                //mTxtCalculatedAmount.setText(Common.valueFormatter(mTotalAmt));
                mTxtDiscountLabel.setText("Discount by amount");
            }
        }

        mDiscountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    mDiscType = 0;
                    mTxtTempDiscountView.setText("0");
                    updateDescription();
                    //change to discount button background color to default
                    mBtnDiscountQty.setBackgroundResource(R.drawable.quantity_button_default);
                    mBtnDiscountQty.setTextColor(Color.parseColor("#3498db"));

                    //change to switch button background color to pressed state
                    mDiscountSwitchLayout.setBackgroundResource(R.drawable.discount_selection_button_pressed);
                    mTxtDiscountLabel.setTextColor(Color.parseColor("#ffffff"));

                    mTotalAmt = mRealAmt;
                }else{
                    mDiscType = 1;
                    mTxtTempDiscountView.setText("0");
                    updateDescription();
                    //change to discount button background color to default
                    mBtnDiscountQty.setBackgroundResource(R.drawable.quantity_button_default);
                    mBtnDiscountQty.setTextColor(Color.parseColor("#3498db"));
                    //change to switch button background color to pressed state
                    mDiscountSwitchLayout.setBackgroundResource(R.drawable.discount_selection_button_pressed);
                    mTxtDiscountLabel.setTextColor(Color.parseColor("#ffffff"));

                    mTotalAmt = mRealAmt;
                }

            }
        });
    }

    private void updateDescription(){
        if(!mIsTotal){
            //set the switch to ON
            if(mDiscType == 0) {
                mDiscountSwitch.setChecked(true);
                mDescription.setText("Enter a discount percentage");
                mTxtDiscountLabel.setText("Discount by %");
                mListener.OnUpdateDiscountView(mIsTotal, mDiscType);
                calculateDiscountValue(discValue);
            }
            else {
                mDiscountSwitch.setChecked(false);
                mDescription.setText("Enter a discount amount");
                mTxtDiscountLabel.setText("Discount by amount");
                mListener.OnUpdateDiscountView(mIsTotal, mDiscType);
                calculateDiscountValue(discValue);
            }
        }else {
            //set the switch to ON
            if(mDiscType == 0) {
                mDiscountSwitch.setChecked(true);
                mDescription.setText("Enter a percentage to discount total bill");
                mTxtDiscountLabel.setText("Discount by %");
                mListener.OnUpdateDiscountView(mIsTotal, mDiscType);
                calculateDiscountValue(discValue);
            }
            else {
                mDiscountSwitch.setChecked(false);
                mDescription.setText("Enter a amount to discount total bill");
                mTxtDiscountLabel.setText("Discount by amount");
                mListener.OnUpdateDiscountView(mIsTotal, mDiscType);
                calculateDiscountValue(discValue);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMultiEditListener) {
            mListener = (OnMultiEditListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnZero:
                calculateDiscountValue("0");
                break;
            case R.id.btnOne:
                calculateDiscountValue("1");
                break;
            case R.id.btnTwo:
                calculateDiscountValue("2");
                break;
            case R.id.btnThree:
                calculateDiscountValue("3");
                break;
            case R.id.btnFour:
                calculateDiscountValue("4");
                break;
            case R.id.btnFive:
                calculateDiscountValue("5");
                break;
            case R.id.btnSix:
                calculateDiscountValue("6");
                break;
            case R.id.btnSeven:
                calculateDiscountValue("7");
                break;
            case R.id.btnEight:
                calculateDiscountValue("8");
                break;
            case R.id.btnNine:
                calculateDiscountValue("9");
                break;
            case R.id.btnQtyDiscount:
                editQty();
                break;
            case R.id.btnClear:
                btnClearClick();
                break;
            case R.id.btnDone:
                btnDoneClick();
                break;
            case R.id.layBackToList:
                backToList();
                break;
        }
    }

    public void backToList(){
        if (mListener != null && mIsTotal) {
            mListener.OnBackToList();
        }else if(mListener != null && !mIsTotal){
            mListener.OnBackToCardView();
        }
    }

    public void editQty(){
        mDiscType = 2;
        mDescription.setText("Enter a quantity");
        mTxtTempDiscountView.setText("0");

        mListener.OnUpdateDiscountView(mIsTotal, mDiscType);

        calculateDiscountValue(String.valueOf(0));

        //change to discount button background color to pressed state
        mBtnDiscountQty.setBackgroundResource(R.drawable.quantity_button_pressed);
        mBtnDiscountQty.setTextColor(Color.parseColor("#ffffff"));

        //change to switch button background color to default
        mDiscountSwitchLayout.setBackgroundResource(R.drawable.discount_selection_button_default);
        mTxtDiscountLabel.setTextColor(Color.parseColor("#ff8000"));
    }


    public void btnClearClick(){
        if(mTxtTempDiscountView.getText().toString().length()!=0) {
            mTxtTempDiscountView.setText(removeLastChar(mTxtTempDiscountView.getText().toString()));
            updateDiscountValue(mDiscType,mTxtTempDiscountView.getText().toString());

            if(mIsTotal)
            mTotalAmt = mRealAmt;
        }
    }

    public void btnDoneClick(){

        if(mTxtTempDiscountView.getText().toString().trim().length()==0) {
            mTxtTempDiscountView.setText("0");
        }

        if(!mIsTotal && !AppConstants.isOrderPrinted)
            updateProductAmountView();
        else if(mIsTotal && !AppConstants.isOrderPrinted)
            updateTotalAmountView();
        else
            Toast.makeText(getActivity(),"You dont have permission to update data", Toast.LENGTH_LONG).show();

    }

    public interface OnMultiEditListener {
        void OnBackToList();
        void OnBackToCardView();
        void OnUpdateProduct(long prodId,int qty);
        void OnUpdateDiscountView(boolean isTotal, int discType);
        void OnUpdateDiscountValueView(boolean isTotal, int discType,String value);
    }

    private void calculateDiscountValue(String val){

        if(mTxtTempDiscountView.getText().toString().equalsIgnoreCase("0")){
            mTxtTempDiscountView.setText(val);
            updateDiscountValue(mDiscType,val);
        }else{
            //String temp = mTxtTempDiscountView.getText().toString();
            if(mDiscType == 0 && mTxtTempDiscountView.getText().toString().length()<4){

                int discVal=0;

                if(mTxtTempDiscountView.getText().toString().trim().length()!=0)
                    discVal = Integer.parseInt(mTxtTempDiscountView.getText().toString().trim());

                if(discVal<=100){
                    mTxtTempDiscountView.setText(mTxtTempDiscountView.getText().toString().concat(val));
                    //update discount percentage
                    if(mTxtTempDiscountView.getText().toString().length()<4){
                        updateDiscountValue(mDiscType,mTxtTempDiscountView.getText().toString());
                    }else{
                        mTxtTempDiscountView.setText(removeLastChar(mTxtTempDiscountView.getText().toString()));
                    }
                }
            }else if(mDiscType == 1){
                int discVal=0;

                if(mTxtTempDiscountView.getText().toString().trim().length()!=0)
                    discVal = Integer.parseInt(mTxtTempDiscountView.getText().toString().trim());

                if(discVal <= mTotalAmt){
                    mTxtTempDiscountView.setText(mTxtTempDiscountView.getText().toString().concat(val));
                    //update discount amount
                    if(Integer.parseInt(mTxtTempDiscountView.getText().toString()) <= mTotalAmt){
                        updateDiscountValue(mDiscType,mTxtTempDiscountView.getText().toString());
                    }else{
                        mTxtTempDiscountView.setText(removeLastChar(mTxtTempDiscountView.getText().toString()));
                    }
                }
            }else if(mDiscType == 2){
                mTxtTempDiscountView.setText(mTxtTempDiscountView.getText().toString().concat(val));
                //update quantity
                updateDiscountValue(mDiscType,mTxtTempDiscountView.getText().toString());
            }
        }
    }

    private void updateProductAmountView(){

        if(mDiscType == 0 ){
            //update percentage

            double price = mProdQty * mProdPrice;
            int discVal = Integer.parseInt(mTxtTempDiscountView.getText().toString().trim());

            if(discVal == 0){

                mDBHelper.updatePOSLineItems(mRowId, mPosId, mProdId, mProdQty, mDiscType, discVal, price,"N","N");
                List<POSLineItem> extraLineItems = mDBHelper.getPOSExtraLineItems(mKotLineId);
                if(extraLineItems.size()!=0){
                    for(int j=0; j<extraLineItems.size(); j++){
                        double price1 = extraLineItems.get(j).getPosQty() * extraLineItems.get(j).getStdPrice();
                        mDBHelper.updatePOSLineItems(extraLineItems.get(j).getRowId(), mPosId, extraLineItems.get(j).getProductId(), extraLineItems.get(j).getPosQty(), mDiscType, discVal, price1,"N","N");
                    }
                }

                if(mListener != null && !mIsTotal)
                    mListener.OnBackToCardView();
            }else{
                if(discVal<=100){

                    price = price - (price*discVal/100);
                    mDBHelper.updatePOSLineItems(mRowId, mPosId,mProdId,mProdQty,mDiscType,discVal,price,"Y","N");

                    List<POSLineItem> extraLineItems = mDBHelper.getPOSExtraLineItems(mKotLineId);
                    if(extraLineItems.size()!=0){
                        for(int j=0; j<extraLineItems.size(); j++){
                            double price1 = extraLineItems.get(j).getPosQty() * extraLineItems.get(j).getStdPrice();
                            price1 = price1 - (price1*discVal/100);
                            price = price + price1;
                            mDBHelper.updatePOSLineItems(extraLineItems.get(j).getRowId(), mPosId, extraLineItems.get(j).getProductId(), extraLineItems.get(j).getPosQty(), mDiscType, discVal, price1,"N","N");
                        }

                        mTxtCalculatedAmount.setText(Common.valueFormatter(price));
                    }else {
                        price = price - (price*discVal/100);
                        mTxtCalculatedAmount.setText(Common.valueFormatter(price));
                    }

                    if(mListener != null && !mIsTotal)
                        mListener.OnBackToCardView();
                }else{
                    Toast.makeText(getActivity(),"Check the discount value", Toast.LENGTH_LONG).show();
                }
            }
        }else if(mDiscType == 1){
            //update amount
            double price = mProdQty * mProdPrice;
            int discVal = Integer.parseInt(mTxtTempDiscountView.getText().toString().trim());

            if(discVal == 0){
                mDBHelper.updatePOSLineItems(mRowId, mPosId,mProdId,mProdQty,mDiscType,discVal,price,"N","N");

                List<POSLineItem> extraLineItems = mDBHelper.getPOSExtraLineItems(mKotLineId);
                if(extraLineItems.size()!=0){
                    for(int j=0; j<extraLineItems.size(); j++){
                        double price1 = extraLineItems.get(j).getPosQty() * extraLineItems.get(j).getStdPrice();
                        mDBHelper.updatePOSLineItems(extraLineItems.get(j).getRowId(), mPosId, extraLineItems.get(j).getProductId(), extraLineItems.get(j).getPosQty(), mDiscType, discVal, price1,"N","N");
                    }
                }

                if(mListener != null && !mIsTotal)
                    mListener.OnBackToCardView();
            }else{
                if(discVal<=price){
                    List<POSLineItem> extraLineItems = mDBHelper.getPOSExtraLineItems(mKotLineId);
                    if(extraLineItems.size()!=0){

                        double discprice = ((double)discVal)/(extraLineItems.size()+1);
                        price = price - discprice;
                        mDBHelper.updatePOSLineItems(mRowId, mPosId,mProdId,mProdQty,mDiscType,discprice,price,"Y","N");

                        for(int j=0; j<extraLineItems.size(); j++){
                            double price1 = extraLineItems.get(j).getPosQty() * extraLineItems.get(j).getStdPrice();
                            price1 = price1 - discprice;
                            price = price + price1;
                            mDBHelper.updatePOSLineItems(extraLineItems.get(j).getRowId(), mPosId, extraLineItems.get(j).getProductId(), extraLineItems.get(j).getPosQty(), mDiscType, discprice, price1,"Y","N");
                        }

                        mTxtCalculatedAmount.setText(Common.valueFormatter(price));
                    }else{
                        price = price - discVal;
                        mTxtCalculatedAmount.setText(Common.valueFormatter(price));
                        mDBHelper.updatePOSLineItems(mRowId, mPosId,mProdId,mProdQty,mDiscType,discVal,price,"Y","N");
                    }

                    if(mListener != null && !mIsTotal)
                        mListener.OnBackToCardView();
                }else{
                    Toast.makeText(getActivity(),"Check the discount value", Toast.LENGTH_LONG).show();
                }
            }

        }else if(mDiscType == 2){
            //update quantity
            int qty = Integer.parseInt(mTxtTempDiscountView.getText().toString().trim());
            if(qty == 0)
                qty = mProdQty;

            double price = qty * mProdPrice;
            int disctype = 0;
            int discVal = 0;

            List<POSLineItem> extraLineItems = mDBHelper.getPOSExtraLineItems(mKotLineId);
            if(extraLineItems.size()!=0){

                disctype = mDBHelper.getProdDiscType(mRowId, mPosId,mProdId);
                discVal = mDBHelper.getProdDiscValue(mRowId, mPosId,mProdId);

                if(disctype == 0)
                    price = price - (price*discVal/100);
                else
                    price = price - discVal;

                mTxtCalculatedAmount.setText(Common.valueFormatter(price));

                mDBHelper.updatePOSLineItems(mRowId, mPosId,mProdId,qty,disctype,discVal,price,"N","N");

                for(int j=0; j<extraLineItems.size(); j++){

                    disctype = mDBHelper.getProdDiscType(extraLineItems.get(j).getRowId(), mPosId,extraLineItems.get(j).getProductId());
                    discVal = mDBHelper.getProdDiscValue(extraLineItems.get(j).getRowId(), mPosId,extraLineItems.get(j).getProductId());
                    double price1 = extraLineItems.get(j).getPosQty() * extraLineItems.get(j).getStdPrice();

                    if(disctype == 0)
                        price1 = price1 - (price1*discVal/100);
                    else
                        price1 = price1 - discVal;

                    mTxtCalculatedAmount.setText(Common.valueFormatter(price));

                    mDBHelper.updatePOSLineItems(extraLineItems.get(j).getRowId(), mPosId, extraLineItems.get(j).getProductId(),extraLineItems.get(j).getPosQty(),disctype,discVal,price1,"N","N");
                }
            }else{
                disctype = mDBHelper.getProdDiscType(mRowId, mPosId,mProdId);
                discVal = mDBHelper.getProdDiscValue(mRowId, mPosId,mProdId);

                if(disctype == 0)
                    price = price - (price*discVal/100);
                else
                    price = price - discVal;

                mTxtCalculatedAmount.setText(Common.valueFormatter(price));

                mDBHelper.updatePOSLineItems(mRowId, mPosId,mProdId,qty,disctype,discVal,price,"N","N");
            }


            if(mListener != null && !mIsTotal) {
                mListener.OnBackToCardView();
                mListener.OnUpdateProduct(mProdId, qty);
            }
        }
    }

    private void updateTotalAmountView(){

        mDiscValue = Integer.parseInt(mTxtTempDiscountView.getText().toString());

        if(mDiscType == 0 && mDiscValue<=100){
            mTotalAmt = mTotalAmt - (mTotalAmt*mDiscValue/100);

            if(mDiscValue<=100){
                mTxtCalculatedAmount.setText(Common.valueFormatter(mTotalAmt));
                mDBHelper.updatePOSTotalAmount(mPosId,mDiscType,mDiscValue);

                //update each line item for discount
                List<POSLineItem> mLineItems = mDBHelper.getPOSLineItems(mPosId);
                for(int i=0;i<mLineItems.size();i++) {
                    double price = mLineItems.get(i).getPosQty() * mLineItems.get(i).getStdPrice();


                    List<POSLineItem> mExtraLineItems = mDBHelper.getPOSExtraLineItems(mLineItems.get(i).getKotLineId());
                    if(mExtraLineItems.size()!=0) {

                        price = price - (price * mDiscValue / 100);

                        mDBHelper.updatePOSLineItems(mLineItems.get(i).getRowId(), mPosId,mLineItems.get(i).getProductId(),mLineItems.get(i).getPosQty(),mDiscType,mDiscValue,price,"N","Y");

                        for (int j = 0; j < mExtraLineItems.size(); j++) {
                            double totalPriceExtraItem = mExtraLineItems.get(j).getPosQty() * mExtraLineItems.get(j).getStdPrice();
                            totalPriceExtraItem = totalPriceExtraItem - (totalPriceExtraItem * mDiscValue / 100);
                            mDBHelper.updatePOSLineItems(mExtraLineItems.get(j).getRowId(), mPosId, mExtraLineItems.get(j).getProductId(), mExtraLineItems.get(j).getPosQty(), mDiscType, mDiscValue, totalPriceExtraItem, "N", "Y");
                        }
                    }else{
                        price = price - (price * mDiscValue / 100);
                        mDBHelper.updatePOSLineItems(mLineItems.get(i).getRowId(), mPosId, mLineItems.get(i).getProductId(), mLineItems.get(i).getPosQty(), mDiscType, mDiscValue, price, "N", "Y");
                    }
                }
                mListener.OnUpdateDiscountValueView(mIsTotal,mDiscType,String.valueOf(mDiscValue));
                if (mListener != null && mIsTotal)
                    mListener.OnBackToList();
            }else{
                Toast.makeText(getActivity(),"Check the discount value", Toast.LENGTH_LONG).show();
            }

        }else if(mDiscType == 1&& mDiscValue<=mTotalAmt){
            mTotalAmt = mTotalAmt - mDiscValue;
            mTxtCalculatedAmount.setText(Common.valueFormatter(mTotalAmt));
            mDBHelper.updatePOSTotalAmount(mPosId,mDiscType,mDiscValue);

            List<POSLineItem> mLineItems = mDBHelper.getPOSLineItems(mPosId);

            double mDiscAmt = ((double) mDiscValue)/mLineItems.size();

            for(int i=0;i<mLineItems.size();i++){
                double price = mLineItems.get(i).getPosQty() * mLineItems.get(i).getStdPrice();

                List<POSLineItem> mExtraLineItems = mDBHelper.getPOSExtraLineItems(mLineItems.get(i).getKotLineId());

                if(mExtraLineItems.size()!=0){

                    double mExtraDiscAmt = mDiscAmt/(mExtraLineItems.size()+1);
                    price = price - mExtraDiscAmt;

                    mDBHelper.updatePOSLineItems(mLineItems.get(i).getRowId(), mPosId,mLineItems.get(i).getProductId(),mLineItems.get(i).getPosQty(),mDiscType,mExtraDiscAmt,price,"N","Y");

                    for(int j=0;j<mExtraLineItems.size();j++){
                        double totalPriceExtraItem = mExtraLineItems.get(j).getPosQty() * mExtraLineItems.get(j).getStdPrice();
                        totalPriceExtraItem = totalPriceExtraItem - mExtraDiscAmt;
                        mDBHelper.updatePOSLineItems(mExtraLineItems.get(j).getRowId(), mPosId,mExtraLineItems.get(j).getProductId(),mExtraLineItems.get(j).getPosQty(),mDiscType,mExtraDiscAmt,totalPriceExtraItem,"N","Y");
                    }
                }else{
                    price = price - mDiscAmt;
                    mDBHelper.updatePOSLineItems(mLineItems.get(i).getRowId(), mPosId,mLineItems.get(i).getProductId(),mLineItems.get(i).getPosQty(),mDiscType,mDiscAmt,price,"N","Y");
                }

            }

            mListener.OnUpdateDiscountValueView(mIsTotal,mDiscType,String.valueOf(mDiscValue));
            if (mListener != null && mIsTotal)
                mListener.OnBackToList();

        }else {
            Toast.makeText(getActivity(),"Check the discount value", Toast.LENGTH_LONG).show();
        }
    }

    private void updateDiscountValue(int discType, String val){
        mListener.OnUpdateDiscountValueView(mIsTotal,discType,val);
    }

    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }

}

package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.domain.net.NetworkDataRequestThread;
import com.zearoconsulting.zearopos.presentation.model.BPartner;
import com.zearoconsulting.zearopos.presentation.model.Category;
import com.zearoconsulting.zearopos.presentation.model.Customer;
import com.zearoconsulting.zearopos.presentation.model.KOTHeader;
import com.zearoconsulting.zearopos.presentation.model.KOTLineItems;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.POSOrders;
import com.zearoconsulting.zearopos.presentation.model.Product;
import com.zearoconsulting.zearopos.presentation.model.Tables;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.presentation.view.activity.MainActivity;
import com.zearoconsulting.zearopos.presentation.view.activity.POSActivity;
import com.zearoconsulting.zearopos.presentation.view.activity.PaymentActivity;
import com.zearoconsulting.zearopos.presentation.view.activity.SelecteCustomer;
import com.zearoconsulting.zearopos.presentation.view.adapter.CardListAdapter;
import com.zearoconsulting.zearopos.presentation.view.adapter.OrderItemAdapter;
import com.zearoconsulting.zearopos.presentation.view.adapter.OrderSplitAdapter;
import com.zearoconsulting.zearopos.presentation.view.adapter.TableMergeAdapter;
import com.zearoconsulting.zearopos.presentation.view.animations.SimpleDividerItemDecoration;
import com.zearoconsulting.zearopos.presentation.view.component.ExpandableTotalAmountLayout;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;
import com.zearoconsulting.zearopos.presentation.view.dialogs.NetworkErrorDialog;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;
import com.zearoconsulting.zearopos.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IPOSListeners} interface
 * to handle interaction events.
 */
public class OrderListFragment extends AbstractFragment implements View.OnClickListener, AbsListView.MultiChoiceModeListener, AdapterView.OnItemClickListener {


    ImageView mUserImgView;
    ImageView mMergeImgView;
    ImageView mSplitImgView;
    AutoCompleteTextView mAutoUserView;
    ExpandableTotalAmountLayout mExpandTotalLayout;
    ExpandableTotalAmountLayout mEditQtyLayout;
    LinearLayout mBottomLayout, mBottomMergeLayout;
    FrameLayout mSplitBtnLayout, mMergeBtnLayout;
    FrameLayout mItemEditorLayout;
    FrameLayout mLayCancel;
    FrameLayout mLayKOT;
    FrameLayout mListCountLayout;
    FrameLayout mHoldRecallLayout;
    FrameLayout mPaymentLayout;
    FrameLayout mBackFromLayout;
    FrameLayout mPrintLayout;
    RelativeLayout mEmptyLayout;
    RelativeLayout mHeaderLayout;
    RecyclerView mCardListView;
    RecyclerView mTableListView;
    RecyclerView mSplitListView;
    ImageView mImgPrintView;
    ImageView mImgPrintKotView;
    ImageView mImgOrderStatusView;

    /**
     * Total Amount header expandable views
     */
    TextView mTxtTotalQty;
    TextView mTxtTotalPrice;
    TextView mTxtTotalPriceOld;
    TextView mTxtTempTotalPrice;
    TextView mTxtBPName;
    TextView mTxtOrderName;
    Button mBtnDiscountView;
    /**
     * ListItem expandable header views
     */
    TextView mTxtProductNameView;
    TextView mTxtProductQtyView;
    TextView mTxtProductDiscountView;
    Button mBtnProductDiscountView;
    MultiEditorFragment mMultiEditorFrag;
    List<POSOrders> mOrders;
    List<KOTHeader> kotHeaderList;

    TextWatcher scanTextChange = new TextWatcher() {

        private final long DELAY = 200; // milliseconds
        private Timer timer = new Timer();

        @Override
        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable edtxt) {

            timer.cancel();
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            /*if(!mEdtBarcodeView.getText().toString().equalsIgnoreCase("")) {
                                String prodVal = edtxt.toString().trim();
                                ((POSActivity) getActivity()).onUpdateScannedItemToCart(prodVal, 0);
                            }*/
                        }
                    },
                    DELAY
            );


        }

    };

    double oldTotal;
    private List<Long> orderNumberList = new ArrayList<>();
    private TextView mTxtPOSNumber;
    private TextView mTxtTempQty;
    private TextView mTxtTotalOrder;
    private Button mBtnOrderCount;
    private ImageView mBtnPayment;
    private ImageView mBtnCancel;
    private Button mBtnBack;
    private Button mBtnSplit;
    private Button mBtnMerge;
    private Button mBtnMergeBack;
    private ListView mOrderListView;
    private OrderItemAdapter mAdapter;
    private List<Category> mCategoryList;
    private int mTotalDiscType;
    private int mTotalDiscValue;
    private CardListAdapter mCardListAdapter;
    private TableMergeAdapter mTableMergeAdapter;
    private OrderSplitAdapter mOrderSplitAdapter;
    private List<POSLineItem> lineItem, mDeleteItem;
    private Context mContext;
    private IPOSListeners mListener;
    private int nr = 0;
    private ReboundListener mReboundListener;
    private double mTotalBill = 0, totalValue = 0, totalDiscValue = 0, totalNoDiscValue = 0;
    private int totalQty = 0, totalDiscQty = 0;

    private long mBPId = 0;
    private double mCreditLimit = 0;

    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("Type");
            String jsonStr = msg.getData().getString("OUTPUT");

            switch (type) {
                case AppConstants.GET_CASH_CUSTOMER_DATA:
                    mParser.parseCommonJson(jsonStr, mHandler);
                    break;
                case AppConstants.GET_POS_NUMBER:
                    mParser.parsePOSNumber(jsonStr, mHandler);
                    break;
                case AppConstants.POS_NUMBER_RECEIVED:
                    //UPDATE THE POS NUMBER WITH CUSTOMER NAME
                    mProDlg.dismiss();
                    long posNumber = mAppManager.getStartNumber();
                    mAppManager.setStartNumber(posNumber);
                    break;
                case AppConstants.COMMON_DATA_RECEIVED:
                    //UPDATE THE CUSTOMER NAME FIELD
                    updateCustomerName(mAppManager.getCashCustomerName());
                    updatePOSNumber();
                    break;
                case AppConstants.NO_DATA_RECEIVED:
                    //INFORM USER NO DATA
                    break;
                case AppConstants.POST_KOT_DATA:
                    mParser.parseKOTResponse(jsonStr, mHandler);
                    break;
                case AppConstants.POST_KOT_DATA_RESPONSE:
                    mDBHelper.updateCounterSaleKOTLineItems(AppConstants.posID);
                    mProDlg.dismiss();
                    updateRow(AppConstants.posID, 0);
                    updateMode(AppConstants.posID);
                    break;
                case AppConstants.CALL_DRAFT_POS_ORDER:
                    mParser.parseDraftOrder(jsonStr, mHandler);
                    break;
                case AppConstants.POS_ORDER_DRAFT_SUCCESS:
                    mProDlg.dismiss();
                    sendPrint(Long.parseLong(jsonStr));
                    break;
                case AppConstants.POS_ORDER_DRAFT_FAILURE:
                    //INFORM USER POSTING FAILURE
                    mProDlg.dismiss();
                    break;
                case AppConstants.SESSION_EXPIRED:
                    mProDlg.dismiss();
                    ((POSActivity) getActivity()).showCreateSession();
                    break;
                case AppConstants.NETWORK_ERROR:
                    //INFORM USER NO DATA
                    Toast.makeText(getActivity(), "NETWORK ERROR...", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Constructor
     */
    public OrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeleteItem = new ArrayList<POSLineItem>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        mReboundListener = new ReboundListener();

        mTxtPOSNumber = (TextView) view.findViewById(R.id.txtPOSNumber);
        mTxtTempQty = (TextView) view.findViewById(R.id.txtTotalQty);
        mTxtTotalOrder = (TextView) view.findViewById(R.id.txtTotalOrder);
        mBtnOrderCount = (Button) view.findViewById(R.id.txtOrderCount);
        mBtnPayment = (ImageView) view.findViewById(R.id.btnPayment);
        mBtnCancel = (ImageView) view.findViewById(R.id.btnCancel);
        mBtnBack = (Button) view.findViewById(R.id.btnBackFromCart);
        mBtnMerge = (Button) view.findViewById(R.id.btnMergeTable);
        mBtnSplit = (Button) view.findViewById(R.id.btnSplit);
        mBtnMergeBack = (Button) view.findViewById(R.id.btnBackMergeFromCart);
        mOrderListView = (ListView) view.findViewById(R.id.listViewItems);

        mUserImgView = (ImageView) view.findViewById(R.id.imgUserView);
        mMergeImgView = (ImageView) view.findViewById(R.id.imgMergeView);
        mSplitImgView = (ImageView) view.findViewById(R.id.imgSplitView);
        mImgPrintView = (ImageView) view.findViewById(R.id.imgPrintView);
        mImgPrintKotView = (ImageView) view.findViewById(R.id.imgPrintKOTView);
        mImgOrderStatusView = (ImageView) view.findViewById(R.id.imgOrdStatus);
        mAutoUserView = (AutoCompleteTextView) view.findViewById(R.id.autoUserView);
        mExpandTotalLayout = (ExpandableTotalAmountLayout) view.findViewById(R.id.totalAmountLayout);
        mEditQtyLayout = (ExpandableTotalAmountLayout) view.findViewById(R.id.editQtyLayout);
        mBottomLayout = (LinearLayout) view.findViewById(R.id.layBottomBar);
        mBottomMergeLayout = (LinearLayout) view.findViewById(R.id.layBottomMergeBar);
        mItemEditorLayout = (FrameLayout) view.findViewById(R.id.displayItemEditor);
        mLayCancel = (FrameLayout) view.findViewById(R.id.layCancel);
        mLayKOT = (FrameLayout) view.findViewById(R.id.layKOT);
        mListCountLayout = (FrameLayout) view.findViewById(R.id.layListCount);
        mHoldRecallLayout = (FrameLayout) view.findViewById(R.id.layHold);
        mPaymentLayout = (FrameLayout) view.findViewById(R.id.layPayment);
        mBackFromLayout = (FrameLayout) view.findViewById(R.id.layBackFromCart);
        mPrintLayout = (FrameLayout) view.findViewById(R.id.layPrint);
        mEmptyLayout = (RelativeLayout) view.findViewById(R.id.emptyLayout);
        mHeaderLayout = (RelativeLayout) view.findViewById(R.id.headerLayout);
        mSplitBtnLayout = (FrameLayout) view.findViewById(R.id.laySplit);
        mMergeBtnLayout = (FrameLayout) view.findViewById(R.id.layMerge);

        mTxtTotalQty = (TextView) view.findViewById(R.id.txtTotalQtyItems);
        mTxtTotalPrice = (TextView) view.findViewById(R.id.txtTotalAmount);
        mTxtTotalPriceOld = (TextView) view.findViewById(R.id.txtTotalOld);
        mTxtTempTotalPrice = (TextView) view.findViewById(R.id.txtTempTotalAmount);
        mTxtBPName = (TextView) view.findViewById(R.id.txtBusinessPartnerView);
        mTxtOrderName = (TextView) view.findViewById(R.id.txtOrderNameView);
        mBtnDiscountView = (Button) view.findViewById(R.id.btnDiscountView);

        mTxtProductNameView = (TextView) view.findViewById(R.id.txtProductName);
        mTxtProductQtyView = (TextView) view.findViewById(R.id.txtHeaderTotalQty);
        mTxtProductDiscountView = (TextView) view.findViewById(R.id.txtProdcutDiscView);
        mBtnProductDiscountView = (Button) view.findViewById(R.id.btnProductDiscountView);

        mCardListView = (RecyclerView) view.findViewById(R.id.cardList);
        mCardListView.setLayoutManager(new LinearLayoutManager(mContext));
        mCardListView.addItemDecoration(new SimpleDividerItemDecoration(mContext));

        mTableListView = (RecyclerView) view.findViewById(R.id.tableList);
        mTableListView.setLayoutManager(new LinearLayoutManager(mContext));
        mTableListView.addItemDecoration(new SimpleDividerItemDecoration(mContext));

        mSplitListView = (RecyclerView) view.findViewById(R.id.splitItemList);
        mSplitListView.setLayoutManager(new LinearLayoutManager(mContext));
        mSplitListView.addItemDecoration(new SimpleDividerItemDecoration(mContext));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppConstants.URL = AppConstants.kURLHttp+mAppManager.getServerAddress()+":"+mAppManager.getServerPort()+AppConstants.kURLServiceName+ AppConstants.kURLMethodApi;

        updateRow(AppConstants.posID, 0);

        updateMode(AppConstants.posID);

        mOrderListView.setOnItemClickListener(this);

        mEditQtyLayout.setVisibility(View.GONE);
        mItemEditorLayout.setVisibility(View.GONE);

        try {
            FrameLayout headerTotalLayout = mExpandTotalLayout.getHeaderLayout();
            headerTotalLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //showTotalAmountDiscountView();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLayCancel.setOnClickListener(this);
        mLayKOT.setOnClickListener(this);
        mHoldRecallLayout.setOnClickListener(this);
        mPaymentLayout.setOnClickListener(this);
        mBackFromLayout.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mBtnPayment.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnSplit.setOnClickListener(this);
        mBtnMerge.setOnClickListener(this);
        mBtnMergeBack.setOnClickListener(this);
        mUserImgView.setOnClickListener(this);
        mMergeImgView.setOnClickListener(this);
        mSplitImgView.setOnClickListener(this);
        mBtnDiscountView.setOnClickListener(this);
        mImgPrintView.setOnClickListener(this);

        if(!mAppManager.getIsRetail()){
            mMergeImgView.setVisibility(View.VISIBLE);
            mSplitImgView.setVisibility(View.VISIBLE);
            mImgPrintKotView.setOnClickListener(this);
        }

        mProDlg = new ProgressDialog(getActivity());
        mProDlg.setIndeterminate(true);
        mProDlg.setCancelable(false);

        mBtnOrderCount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                holdRecall();
                return true;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = context;
        if (context instanceof IPOSListeners) {
            mListener = (IPOSListeners) context;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layCancel:
                onCancel();
                break;
            case R.id.btnCancel:
                onCancel();
                break;
            case R.id.layHold:
                //holdRecall();
                break;
            case R.id.layPayment:
                payment();
                break;
            case R.id.btnPayment:
                payment();
                break;
            case R.id.layBackFromCart:
                gotoList();
                break;
            case R.id.btnBackFromCart:
                gotoList();
                break;
            case R.id.imgMergeView:
                if (AppConstants.isTableService) {
                    kotHeaderList = mDBHelper.getKOTTables(true);
                    if (kotHeaderList.size() != 0 && kotHeaderList.size()>1)
                        showMergeView();
                }
                break;
            case R.id.imgSplitView:
                onSplit();
                break;
            case R.id.imgUserView:
                selectCustomer();
                break;
            case R.id.imgPrintView:
                sendDraftOrder();
                break;
            case R.id.imgPrintKOTView:
                //if (!AppConstants.isKOTParsing)
                    generateKOT();
                break;
            case R.id.btnDiscountView:
                showTotalAmountDiscountView();
                break;
            case R.id.btnSplit:
                splitItems();
                break;
            case R.id.btnMergeTable:
                mergeTables();
            case R.id.btnBackMergeFromCart:
                gotoList();
                break;
            default:
                break;
        }
    }

    private void onSplit() {
        if (lineItem != null) {
            if (lineItem.size() != 0 && lineItem.size()>1) {
                showSplitView();
            }
        }
    }

    public void onCancel() {

        if (AppConstants.isOrderPrinted) {
            showSnackBar("Call Supervisor to unlock the Order.");
            return;
        }

        if (lineItem != null) {

            //postPOSCancelOrder();
            if (lineItem.size() != 0) {
                confirmDialog();
            } else {
                showSnackBar("Call Supervisor to unlock the Order.");
            }
        }

    }

    private boolean isKOTGenerated() {
        boolean isKOTGenerated = false;

        for (int i = 0; i < lineItem.size(); i++) {
            if (lineItem.get(i).getIsKOTGenerated().equalsIgnoreCase("Y")) {
                isKOTGenerated = true;
                break;
            }
        }

        return isKOTGenerated;
    }

    private void showTotalAmountDiscountView() {
        try {
            if (AppConstants.isOrderPrinted) {
                showSnackBar("Call Supervisor to unlock the Order.");
                return;
            }

            if (mExpandTotalLayout.isOpened())
                return;

            if (mTotalBill != 0) {
                mMultiEditorFrag = new MultiEditorFragment();
                Bundle args = new Bundle();
                args.putLong("posId", AppConstants.posID);
                args.putBoolean("IsTotalAmount", true);

                args.putDouble("TotalAmount", totalNoDiscValue);
                mMultiEditorFrag.setArguments(args);
                getChildFragmentManager().beginTransaction().replace(R.id.displayMultiEditor, mMultiEditorFrag).commit();
                hideListBottomView();
                mExpandTotalLayout.startExpandLayoutAnim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void holdRecall() {
        if (mCardListView.getVisibility() == View.GONE || mCardListView.getVisibility() == View.INVISIBLE) {
            mHeaderLayout.setVisibility(View.INVISIBLE);
            mExpandTotalLayout.setVisibility(View.GONE);
            mOrderListView.setVisibility(View.GONE);

            //visible cardstack view
            setupAdapter();

            mLayCancel.setVisibility(View.GONE);
            mLayKOT.setVisibility(View.GONE);
            mPrintLayout.setVisibility(View.GONE);
            mListCountLayout.setVisibility(View.VISIBLE);
            mPaymentLayout.setVisibility(View.GONE);
            mBackFromLayout.setVisibility(View.VISIBLE);

            updateCountofOrder();
        }
    }

    private void showMergeView() {

        if (mTableListView.getVisibility() == View.GONE || mTableListView.getVisibility() == View.INVISIBLE) {

            mEmptyLayout.setVisibility(View.GONE);
            mExpandTotalLayout.setVisibility(View.GONE);
            mOrderListView.setVisibility(View.GONE);
            mCardListView.setVisibility(View.GONE);
            mSplitListView.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.GONE);
            mSplitBtnLayout.setVisibility(View.GONE);

            mMergeBtnLayout.setVisibility(View.VISIBLE);
            mBottomMergeLayout.setVisibility(View.VISIBLE);
            mTableListView.setVisibility(View.VISIBLE);

            if (mTableMergeAdapter == null) {
                mTableMergeAdapter = new TableMergeAdapter(kotHeaderList, mDBHelper, mAppManager);
                mTableListView.setAdapter(mTableMergeAdapter);
                mTableMergeAdapter.notifyDataSetChanged();
            } else {
                mTableListView.setAdapter(null);
                mTableMergeAdapter = new TableMergeAdapter(kotHeaderList, mDBHelper, mAppManager);
                mTableListView.setAdapter(mTableMergeAdapter);
                mTableMergeAdapter.notifyDataSetChanged();
            }
        }

    }

    private void showSplitView() {

        if (mSplitListView.getVisibility() == View.GONE || mSplitListView.getVisibility() == View.INVISIBLE) {

            mExpandTotalLayout.setVisibility(View.GONE);
            mOrderListView.setVisibility(View.GONE);
            mCardListView.setVisibility(View.GONE);
            mTableListView.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.GONE);
            mMergeBtnLayout.setVisibility(View.GONE);

            mSplitBtnLayout.setVisibility(View.VISIBLE);
            mBottomMergeLayout.setVisibility(View.VISIBLE);
            mSplitListView.setVisibility(View.VISIBLE);

            if (mOrderSplitAdapter == null) {
                mOrderSplitAdapter = new OrderSplitAdapter(lineItem, mDBHelper, mAppManager);
                mSplitListView.setAdapter(mOrderSplitAdapter);
                mOrderSplitAdapter.notifyDataSetChanged();
            } else {
                mSplitListView.setAdapter(null);
                mOrderSplitAdapter = new OrderSplitAdapter(lineItem, mDBHelper, mAppManager);
                mSplitListView.setAdapter(mOrderSplitAdapter);
                mOrderSplitAdapter.notifyDataSetChanged();
            }
        }
    }

    private void mergeTables() {

        if (AppConstants.posID == 0 && mAppManager.getStartNumber().equals(mAppManager.getEndNumber())) {
            Log.i("POSACTIVITY", "GET POS NUMBERS");
            getPOSNumbers();
            return;
        } else if (!mAppManager.getStartNumber().equals(mAppManager.getEndNumber())) {

            if (AppConstants.posID == 0) {
                long posNumber = mAppManager.getStartNumber();
                AppConstants.posID = posNumber;
                posNumber = posNumber + 1;
                mAppManager.setStartNumber(posNumber);
            }

            String data = "";
            List<KOTHeader> tableList = ((TableMergeAdapter) mTableMergeAdapter)
                    .getKOTHeaderList();

            if (tableList.size() == 0) {
                return;
            } else {
                //Delete existing data
                mDBHelper.deletePOSTables(AppConstants.posID);

                ((POSActivity) getActivity()).addPOSHeader("Y");
            }


            //check if selected state if 0 should not allow them to enter
            for (int i = 0; i < tableList.size(); i++) {

                KOTHeader kotHeader = tableList.get(i);

                long invNum = kotHeader.getInvoiceNumber();
                long selecteTableId = kotHeader.getTablesId();

                if (kotHeader.getSelected().equalsIgnoreCase("Y")) {

                    data = data + "\n" + kotHeader.getTablesId();

                    //get the invoice number from KOTHeader table
                    List<Long> invNumList = mDBHelper.getKOTInvoiceNumbers(selecteTableId);
                    for(int j=0; j<invNumList.size(); j++){
                        if (AppConstants.posID != invNumList.get(j)) {
                            //Log.i("Delete existing invoice", " " + kotHeader.getInvoiceNumber());
                            mDBHelper.deletePOSTables(invNumList.get(j));
                        }
                    }

                    /*if (AppConstants.posID != invNum) {
                        Log.i("Delete existing invoice", " " + kotHeader.getInvoiceNumber());
                        mDBHelper.deletePOSTables(invNum);
                    }*/

                    //update invoice number to kotHeader
                    mDBHelper.updateInvoiceToKOT(kotHeader.getTablesId(), AppConstants.posID);

                    //get the table id and kot numbers of table
                    kotHeaderList = mDBHelper.getKOTHeaders(kotHeader.getTablesId(), false);

                    //get the kot line items
                    List<KOTLineItems> kotLineItemList = mDBHelper.getKOTLineItems(kotHeader.getTablesId());

                    for (int j = 0; j < kotLineItemList.size(); j++) {
                        KOTLineItems kotLineItems = kotLineItemList.get(j);
                        Product product = kotLineItems.getProduct();

                        mDBHelper.addKOTtoPOSLineItem(AppConstants.posID, kotLineItems, product);
                    }
                } else {

                    if (AppConstants.posID == invNum) {
                        mDBHelper.updateInvoiceToKOT(selecteTableId, 0);
                        mDBHelper.updateKOTSelectedStatus(selecteTableId, "N");
                    }

                }
            }

            //Toast.makeText(getActivity(),"Selected Tables: \n" + data, Toast.LENGTH_LONG).show();

            //Hide mergeView and Show OrderListView
            mBottomMergeLayout.setVisibility(View.GONE);
            mTableListView.setVisibility(View.GONE);

            mCardListView.setVisibility(View.GONE);
            mOrderListView.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.VISIBLE);
            mExpandTotalLayout.setVisibility(View.VISIBLE);

            updateRow(AppConstants.posID, 0);
        }
    }

    private void splitItems() {

        //List<POSLineItem> mSplitItems = mDBHelper.getSplitItems(AppConstants.posID, "N");
        if (mAppManager.getStartNumber().equals(mAppManager.getEndNumber())) {
            Log.i("POSACTIVITY", "GET POS NUMBERS");
            getPOSNumbers();
            return;
        } else if (!mAppManager.getStartNumber().equals(mAppManager.getEndNumber())) {

            POSOrders order = mDBHelper.getPosHeader(AppConstants.posID);
            long posNumber = mAppManager.getStartNumber();
            mDBHelper.addPOSHeader(posNumber, order.getBpId(), order.getCustomerName(), mAppManager.getPriceListID(), mAppManager.getCustomerValue(), mAppManager.getCustomerEmail(), mAppManager.getCustomerNumber(), 1, "Y");
            mDBHelper.updateSplitedLineItem(AppConstants.posID, posNumber);


            //check existing order having kot
            List<Long> kotNumList = mDBHelper.getKOTNumberList(AppConstants.posID);
            long tableId = mDBHelper.getKOTTableId(AppConstants.posID);

            //get new posLineItems and update to kotLineItmes with new posNumber
            //long tableId = mDBHelper.getKOTTableId(AppConstants.posID);
            List<POSLineItem> allLineItems = mDBHelper.getAllPOSLineItems(posNumber);
            for (int i = 0; i < allLineItems.size(); i++) {
                mDBHelper.updateKOTLineItemInvoiceNumber(AppConstants.posID, posNumber, allLineItems.get(i).getProductId());
            }

            //check splited order having kot
            List<Long> kotSplitList = mDBHelper.getKOTNumberList(posNumber);
            if(kotSplitList.size()==0 && kotNumList.size()!=0){
                //add kotline items
                for (int i = 0; i < allLineItems.size(); i++) {

                    KOTLineItems kotLineItems = new KOTLineItems();
                    kotLineItems.setTableId(tableId);
                    kotLineItems.setKotLineId(0);
                    kotLineItems.setKotNumber(kotNumList.get(0));
                    kotLineItems.setInvoiceNumber(posNumber);
                    kotLineItems.setNotes("");
                    kotLineItems.setRefRowId(allLineItems.get(i).getRefRowId());
                    kotLineItems.setIsExtraProduct(allLineItems.get(i).getIsExtraProduct());

                    Product product = mDBHelper.getProduct(mAppManager.getClientID(), mAppManager.getOrgID(), allLineItems.get(i).getProductId());
                    //insert kot line items
                    mDBHelper.addKOTLineItems(kotLineItems, product, allLineItems.get(i).getPosQty());
                }
            }

            orderNumberList.clear();
            orderNumberList.add(AppConstants.posID);
            orderNumberList.add(posNumber);

            posNumber = posNumber + 1;
            mAppManager.setStartNumber(posNumber);
        }

        //Hide mergeView and Show OrderListView
        mBottomMergeLayout.setVisibility(View.GONE);
        mTableListView.setVisibility(View.GONE);
        mSplitListView.setVisibility(View.GONE);
        mCardListView.setVisibility(View.GONE);
        mOrderListView.setVisibility(View.VISIBLE);
        mBottomLayout.setVisibility(View.VISIBLE);
        mExpandTotalLayout.setVisibility(View.VISIBLE);

        //update total amount to kotHeader
        double total = mDBHelper.sumOfProductsTotalPrice(AppConstants.posID);
        /*double total = 0;
        List<Long> kotHeaderList = mDBHelper.getKOTNumberList(AppConstants.posID);

        for(int i=0; i<kotHeaderList.size(); i++){
            total = total + mDBHelper.sumOfKOTProductsTotalPrice(kotHeaderList.get(i),AppConstants.posID);
        }*/

        mDBHelper.updateKOTHeaderTotal(AppConstants.posID, total);


        updateRow(AppConstants.posID, 0);

        //for Preprint and save order to server in draft status
        postPOSDraftOrder();
    }

    public void getPOSNumbers() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            mProDlg.setMessage("Getting order numbers...");
            mProDlg.show();
            JSONObject mJsonObj = mParser.getParams(AppConstants.GET_POS_NUMBER);
            NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.GET_POS_NUMBER);
            thread.start();
        } else {
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(getActivity()).show();
        }
    }

    private void updateCountofOrder() {
        List<POSOrders> mOrders = mDBHelper.getPOSOrders();
        mTxtTotalOrder.setText(mOrders.size() - 1 + " lists");
        mBtnOrderCount.setText(String.valueOf(mOrders.size() - 1));
    }


    public void selectCustomer() {
        Intent actLog = new Intent(mContext,
                SelecteCustomer.class);
        startActivity(actLog);
    }

    public void gotoList() {
        setVisibility();
    }

    public void payment() {
        if (lineItem != null) {
            if (lineItem.size() != 0) {
                //check kotLineItemTable for any item added. but not updated in cart
                //List<KOTLineItems> kotLineItemList = mDBHelper.getKOTLineItemForInvoice(AppConstants.posID);
                //if (lineItem.size() == kotLineItemList.size() || kotLineItemList.size() == 0) {
                    Intent actLog = new Intent(mContext, PaymentActivity.class);
                    startActivity(actLog);
                //} else {
                //    ((POSActivity) getActivity()).updateKOTtoInvoice(AppConstants.posID);
                //}
            }
        }
    }

    public void updateMode(long posId) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    //here we have to check the preprinted status
                    POSOrders posOrder = mDBHelper.getPosHeader(posId);
                    if (posOrder != null) {
                        if (posOrder.getIsPrinted().equalsIgnoreCase("Y")) {
                            AppConstants.isOrderPrinted = true;
                            mTxtBPName.setText("Cashier");
                            mTxtBPName.setTextColor(Color.parseColor("#FF0000"));
                            mImgOrderStatusView.setImageResource(R.drawable.ic_lock_black);
                        } else if (isKOTGenerated()) {
                            mTxtBPName.setText("Cashier");
                            mTxtBPName.setTextColor(Color.parseColor("#FF0000"));
                            mImgOrderStatusView.setImageResource(R.drawable.ic_lock_black);
                        } else {
                            AppConstants.isOrderPrinted = false;
                            mTxtBPName.setText("Cashier");
                            mTxtBPName.setTextColor(Color.parseColor("#0099FF"));
                            mImgOrderStatusView.setImageResource(R.drawable.ic_lock_open);
                        }
                    } else {
                        AppConstants.isOrderPrinted = false;
                        mTxtBPName.setText("Cashier");
                        mTxtBPName.setTextColor(Color.parseColor("#0099FF"));
                        mImgOrderStatusView.setImageResource(R.drawable.ic_lock_open);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void sendDraftOrder(){
        if (lineItem != null) {
            if (lineItem.size() != 0) {
                orderNumberList.clear();
                orderNumberList.add(AppConstants.posID);
                //call post Draft order
                postPOSDraftOrder();
            }
        }
    }

    public void sendPrint(long posNumber) {
        //for(int i=0;i<orderNumberList.size();i++) {
            List<POSLineItem> lineItems = mDBHelper.getPOSLineItems(posNumber, 0);
            double total = mDBHelper.sumOfProductsTotalPrice(posNumber);
            ((POSActivity) getActivity()).printBill(posNumber, lineItems, total, total, 0, 0);
        //}
    }

    public void generateKOT() {
        if (lineItem != null) {
            if (lineItem.size() != 0) {

                try {

                    mProDlg.setMessage("Posting data...");
                    mProDlg.show();

                    List<Long> kotTableList = mDBHelper.getKOTTableList(AppConstants.posID);
                    if (kotTableList.size() == 0) {
                        AppConstants.tableID = 0;
                    } else {
                        AppConstants.tableID = kotTableList.get(0);
                    }

                    JSONObject mJsonObj = mParser.getParams(AppConstants.POST_KOT_DATA);
                    mJsonObj.put("tableId", AppConstants.tableID);
                    mJsonObj.put("tokens", mParser.getCounterSaleKOTItems());
                    long covers = mDBHelper.getKOTCoversCount(AppConstants.tableID);
                    mJsonObj.put("coversCount", covers);
                    if (AppConstants.tableID != 0)
                        mJsonObj.put("kotType", "SM");
                    else
                        mJsonObj.put("kotType", "CS");

                    JSONArray tokenArray = mParser.getCounterSaleKOTItems();

                    if (tokenArray.length() != 0) {
                        Log.i("CS-KOTJson", mJsonObj.toString());
                        NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.POST_KOT_DATA);
                        thread.start();
                    } else {
                        mProDlg.dismiss();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        POSLineItem mLineItem = (POSLineItem) parent.getItemAtPosition(position);
        if (null != mListener && !AppConstants.isTableService) {
            mListener.addOrRemoveItemFromCart(mLineItem.getCategoryId(), mLineItem, true);
        }

    }

    public void showDiscountView(long categoryId, POSLineItem mProduct) {

        //show multieditor
        POSLineItem mLineItem = mProduct;

        hideListBottomView();

        mExpandTotalLayout.setVisibility(View.GONE);
        mEditQtyLayout.setVisibility(View.VISIBLE);
        mItemEditorLayout.setVisibility(View.VISIBLE);

        mMultiEditorFrag = new MultiEditorFragment();
        Bundle args = new Bundle();
        args.putBoolean("IsTotalAmount", false);
        args.putLong("posId", mLineItem.getPosId());
        args.putLong("rowId", mLineItem.getRowId());
        args.putLong("productId", mLineItem.getProductId());
        args.putInt("prodQty", mLineItem.getPosQty());
        args.putDouble("prodPrice", mLineItem.getStdPrice());
        args.putInt("discType", mLineItem.getDiscType());
        args.putDouble("discValue", mLineItem.getDiscValue());
        mMultiEditorFrag.setArguments(args);

        getChildFragmentManager().beginTransaction().replace(R.id.displayItemEditor, mMultiEditorFrag).commit();

        mTxtProductNameView.setText(mLineItem.getProductName());
        mTxtProductQtyView.setText("QR " + mLineItem.getStdPrice() + " x " + mLineItem.getPosQty());

        if (mLineItem.getDiscType() == 0) {
            mTxtProductDiscountView.setText("-" + mLineItem.getDiscValue() + "%");
        } else {
            mTxtProductDiscountView.setText("- QR " + mLineItem.getDiscValue());
        }

    }

    public void backToList() {
        FrameLayout contentLayout = mExpandTotalLayout.getContentLayout();
        if (contentLayout.getVisibility() == View.VISIBLE)
            mExpandTotalLayout.collapse(contentLayout);

        showListBottomView();
    }

    public void backToCardView() {
        mEditQtyLayout.setVisibility(View.GONE);
        mItemEditorLayout.setVisibility(View.GONE);
        mExpandTotalLayout.setVisibility(View.VISIBLE);
        showListBottomView();
    }

    public void updateProductDiscountView(int discType) {
        if (discType == 0) { // % view
            mBtnProductDiscountView.setText("%");
            mBtnProductDiscountView.setTextColor(Color.parseColor("#ffffff"));
            mBtnProductDiscountView.setBackgroundResource(R.drawable.discount_selection_button_pressed);
        } else if (discType == 1) { // - view
            mBtnProductDiscountView.setText("-");
            mBtnProductDiscountView.setTextColor(Color.parseColor("#ffffff"));
            mBtnProductDiscountView.setBackgroundResource(R.drawable.discount_selection_button_pressed);
        } else if (discType == 2) { // x view
            mBtnProductDiscountView.setText("x");
            mBtnProductDiscountView.setTextColor(Color.parseColor("#ffffff"));
            mBtnProductDiscountView.setBackgroundResource(R.drawable.quantity_button_pressed);
        }
    }

    public void updateProductDiscountValueView(int discType, String value) {
        if (discType == 0) { // % view
            mTxtProductDiscountView.setText("- " + value + " %");
        } else if (discType == 1) { // - view
            mTxtProductDiscountView.setText("- " + value);
        } else if (discType == 2) { // x view
            mTxtProductDiscountView.setText("x " + value);
        }
    }

    public void updateTotalDiscountView(int discType) {
        if (discType == 0) { // % view
            mBtnDiscountView.setText("%");
            mBtnDiscountView.setTextColor(Color.parseColor("#ffffff"));
            mBtnDiscountView.setBackgroundResource(R.drawable.discount_selection_button_pressed);
        } else if (discType == 1) { // - view
            mBtnDiscountView.setText("-");
            mBtnDiscountView.setTextColor(Color.parseColor("#ffffff"));
            mBtnDiscountView.setBackgroundResource(R.drawable.discount_selection_button_pressed);
        }
    }

    public void updateTotalDiscountValueView(int discType, String value) {
        if (discType == 0) { // % view
            mTxtTotalPrice.setText("- " + value + " %");
            mTxtTempTotalPrice.setText("- " + value + " %");
        } else if (discType == 1) { // - view
            mTxtTotalPrice.setText("- QR " + value);
            mTxtTempTotalPrice.setText("- QR " + value);
        }
    }

    private void hideListBottomView() {
        mOrderListView.setVisibility(View.GONE);
        mBottomLayout.setVisibility(View.GONE);
    }

    private void showListBottomView() {
        mOrderListView.setVisibility(View.VISIBLE);
        mBottomLayout.setVisibility(View.VISIBLE);
        updateRow(AppConstants.posID, 0);
    }

    public void hideUserView() {
        mUserImgView.setVisibility(View.INVISIBLE);
        mTxtTempQty.setVisibility(View.VISIBLE);
        mTxtTempTotalPrice.setVisibility(View.VISIBLE);
        mTxtPOSNumber.setVisibility(View.INVISIBLE);
    }

    public void showUserView() {
        mTxtTempQty.setVisibility(View.INVISIBLE);
        mUserImgView.setVisibility(View.VISIBLE);
        mTxtTempTotalPrice.setVisibility(View.INVISIBLE);
        mTxtPOSNumber.setVisibility(View.VISIBLE);
    }

    private void setupAdapter() {
        try {
            mEmptyLayout.setVisibility(View.GONE);
            mCardListView.setVisibility(View.VISIBLE);
            mOrders = mDBHelper.getPOSOrders();

            if (mOrders.size() != 0) {
                //GET THE TOTAL AMOUNT OF ORDER
                for (int i = 0; i < mOrders.size(); i++) {
                    mOrders.get(i).setOrderTotalAmt(mDBHelper.sumOfProductsTotalPrice(mOrders.get(i).getPosId()));
                    mOrders.get(i).setOrderTotalQty(mDBHelper.sumOfProductsTotalQty(mOrders.get(i).getPosId()));

                    List<Long> kotTableList = mDBHelper.getKOTTableList(mOrders.get(i).getPosId());

                    if (kotTableList.size() == 0) {
                        mOrders.get(i).setOrderType("CounterSale");
                    } else if (kotTableList.size() == 1 && kotTableList.get(0) == 0) {
                        mOrders.get(i).setOrderType("CounterSale");
                    } else {
                        StringBuilder sb = new StringBuilder("");
                        for (int j = 0; j < kotTableList.size(); j++) {
                            Tables table = mDBHelper.getTableData(mAppManager.getClientID(), mAppManager.getOrgID(), kotTableList.get(j));
                            if(table!=null)
                            sb.append(table.getTableName() + " ");
                        }
                        mOrders.get(i).setOrderType(sb.toString());
                    }
                }

            }

            if (mCardListAdapter == null) {
                mCardListAdapter = new CardListAdapter(getActivity(), mOrders, mListener);
                mCardListView.setAdapter(mCardListAdapter);
                mCardListAdapter.notifyDataSetChanged();
            } else {
                mCardListAdapter.swapItems(mOrders);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVisibility() {
        mHeaderLayout.setVisibility(View.VISIBLE);
        mCardListView.setVisibility(View.GONE);
        mBottomMergeLayout.setVisibility(View.GONE);
        mTableListView.setVisibility(View.GONE);
        mSplitListView.setVisibility(View.GONE);
        mExpandTotalLayout.setVisibility(View.VISIBLE);
        mOrderListView.setVisibility(View.VISIBLE);
        mBottomLayout.setVisibility(View.VISIBLE);

        mLayCancel.setVisibility(View.VISIBLE);
        mLayKOT.setVisibility(View.VISIBLE);
        mListCountLayout.setVisibility(View.GONE);
        mPaymentLayout.setVisibility(View.VISIBLE);
        mBackFromLayout.setVisibility(View.GONE);
        mPrintLayout.setVisibility(View.VISIBLE);
    }

    public void updateRow(long posId, long lastUpdatedItem) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (mCardListView.getVisibility() == View.VISIBLE) {
                        setVisibility();
                    }

                    if (mTableListView.getVisibility() == View.VISIBLE) {
                        setVisibility();
                    }

                    if (mEditQtyLayout.getVisibility() == View.VISIBLE) {
                        mEditQtyLayout.setVisibility(View.GONE);
                    }

                    if (mItemEditorLayout.getVisibility() == View.VISIBLE) {
                        mItemEditorLayout.setVisibility(View.GONE);
                        setVisibility();
                    }

                    POSOrders posOrder = mDBHelper.getPosHeader(posId);

                    if(posOrder==null){
                        AppConstants.isOrderPrinted = false;
                    }else if(posOrder.getIsPrinted().equalsIgnoreCase("Y")){
                        AppConstants.isOrderPrinted = true;
                    }else {
                        AppConstants.isOrderPrinted = false;
                    }

                    boolean isAvail = false;
                    lineItem = mDBHelper.getPOSLineItems(posId, 0);
                    if (lineItem.size() == 0) {
                        //hide list view
                        mOrderListView.setVisibility(View.GONE);
                        //show empty view
                        mEmptyLayout.setVisibility(View.VISIBLE);
                        //shadow the bottombar view
                        mBottomLayout.setAlpha(0.5f);
                        //update totla amount
                        mTxtTotalPrice.setText(AppConstants.currencyCode + " " + Common.valueFormatter(0));
                        mTxtTempTotalPrice.setText(AppConstants.currencyCode + " " + Common.valueFormatter(0));
                        //update the quantity
                        mTxtTempQty.setText("0 items");
                        mTxtTotalQty.setText("0 items");

                    } else if (mAdapter == null && lineItem.size() != 0) {
                        updateListBottomView();
                        updateProductPrices();
                        mAdapter = new OrderItemAdapter(mContext, lineItem, mListener);
                        mOrderListView.setAdapter(mAdapter);
                    } else if (mAdapter != null) {
                        updateListBottomView();
                        updateProductPrices();
                        mAdapter.clear();
                        mAdapter = new OrderItemAdapter(mContext, lineItem, mListener);

                        //mAdapter.refreshListView(lineItem);
                        for (int i = 0; i < lineItem.size(); i++) {
                            if (lastUpdatedItem == lineItem.get(i).getProductId()) {
                                mOrderListView.setAdapter(mAdapter);
                                mAdapter.refreshItem(i);
                                mAdapter.notifyDataSetChanged();
                                mOrderListView.setSelection(i);
                                isAvail = true;
                                break;
                            } else {
                                isAvail = false;
                            }
                        }

                        if (!isAvail) {
                            mOrderListView.setAdapter(mAdapter);
                            mAdapter.refreshItem(mOrderListView.getAdapter().getCount() - 1);
                            mAdapter.notifyDataSetChanged();
                            mOrderListView.setSelection(mOrderListView.getAdapter().getCount() - 1);
                        }
                    }

                    updateTotal(posId);
                    updateOrderType(posId);
                    updateCountofOrder();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void checkAndUpdateView(long posId, long lastUpdatedItem){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(mSplitListView.getVisibility() == View.VISIBLE){
                        //update split adapter
                        lineItem = mDBHelper.getPOSLineItems(posId, 0);
                        if(lineItem.size()!=0){
                            if(mOrderSplitAdapter!=null){
                                mOrderSplitAdapter.refresh(lineItem);
                            }
                        }
                    }else if(mTableListView.getVisibility() == View.VISIBLE){
                        //update table adapter
                        kotHeaderList = mDBHelper.getKOTTables(true);
                        if (kotHeaderList.size() != 0){
                            if(mTableMergeAdapter!=null){
                                mTableMergeAdapter.refresh(kotHeaderList);
                            }
                        }

                    }else if(mEditQtyLayout.getVisibility() == View.VISIBLE || mItemEditorLayout.getVisibility() == View.VISIBLE){
                        //no updates required
                    }else{
                        updateRow(posId,lastUpdatedItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * updateProduct prices for if ParentProduct having addOn items
     */
    private void updateProductPrices() {
        for (int i = 0; i < lineItem.size(); i++) {
            List<POSLineItem> extraLineItems = mDBHelper.getPOSExtraLineItems(lineItem.get(i).getKotLineId());
            if (extraLineItems.size() != 0) {

                double price = 0;
                double discVal = 0;
                double stdPrice = 0;

                for (int j = 0; j < extraLineItems.size(); j++) {
                    price = price + extraLineItems.get(j).getTotalPrice();
                    stdPrice = stdPrice + extraLineItems.get(j).getStdPrice();
                    if (lineItem.get(i).getDiscType() == 1)
                        discVal = discVal + extraLineItems.get(j).getDiscValue();
                }

                lineItem.get(i).setStdPrice(lineItem.get(i).getStdPrice() + stdPrice);
                lineItem.get(i).setDiscValue(lineItem.get(i).getDiscValue() + discVal);
                lineItem.get(i).setTotalPrice(lineItem.get(i).getTotalPrice() + price);
            }

        }
    }

    private void updateListBottomView() {
        //hide empty view
        mEmptyLayout.setVisibility(View.GONE);
        //show list view
        mOrderListView.setVisibility(View.VISIBLE);
        //shadow the bottombar view
        mBottomLayout.setAlpha(1.0f);
    }

    public void updateTotal(long posId) {

        try {
            totalQty = mDBHelper.sumOfProductsTotalQty(posId);
            mTxtTempQty.setText(String.valueOf(totalQty) + " items");
            mTxtTotalQty.setText(String.valueOf(totalQty) + " items");

            mTotalDiscType = mDBHelper.getTotalDiscType(posId);
            mTotalDiscValue = mDBHelper.getTotalDiscValue(posId);

            updateTotalDiscountView(mTotalDiscType);

            mTotalBill = mDBHelper.sumOfProductsTotalPrice(AppConstants.posID);

            mTxtTotalPrice.setText(AppConstants.currencyCode + " " + Common.valueFormatter(mTotalBill));
            mTxtTempTotalPrice.setText(AppConstants.currencyCode + " " + Common.valueFormatter(mTotalBill));

            oldTotal = mDBHelper.sumOfProductsWithoutDiscount(AppConstants.posID);
            if (oldTotal != mTotalBill) {
                mTxtTotalPriceOld.setVisibility(View.VISIBLE);
                mTxtTotalPriceOld.setText(Common.valueFormatter(oldTotal));
                mTxtTotalPriceOld.setPaintFlags(mTxtTotalPriceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                mTxtTotalPriceOld.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateOrderType(long posId) {

        List<Long> kotTableList = mDBHelper.getKOTTableList(posId);

        if (posId == 0) {
            mTxtOrderName.setText("");
        } else if (kotTableList.size() == 0) {
            mTxtOrderName.setText("CounterSale");
        } else if (kotTableList.size() == 1 && kotTableList.get(0) == 0) {
            mTxtOrderName.setText("CounterSale");
        } else {
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < kotTableList.size(); i++) {
                Tables table = mDBHelper.getTableData(mAppManager.getClientID(), mAppManager.getOrgID(), kotTableList.get(i));
                sb.append(table.getTableName() + " ");
            }
            mTxtOrderName.setText(sb.toString());
        }
    }


    public void updateEmptyRow() {
        try {
            mOrderListView.setAdapter(null);
            mTxtTempQty.setText("0 items");
            mTxtTotalQty.setText("0 items");
            mTxtTotalPrice.setText(AppConstants.currencyCode + ". 0.00");
            mTxtTempTotalPrice.setText(AppConstants.currencyCode + ". 0.00");
            if (lineItem != null) {
                lineItem.clear();
            }

            totalValue = 0;
            mTotalBill = 0;
            mTxtOrderName.setText("");
            updateRow(AppConstants.posID, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCustomerName(String name) {
        //mTxtBPName.setText(name);
    }

    public void updatePOSNumber() {

        if (AppConstants.posID != 0)
            mTxtPOSNumber.setText("# " + AppConstants.posID);
        else
            mTxtPOSNumber.setText("#000000");
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            nr++;
            mDeleteItem.add(lineItem.get(position));
        } else {
            nr--;
            mDeleteItem.remove(lineItem.get(position));
        }
        mode.setTitle(nr + " rows selected!");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_pos,
                menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                //delete the items in local db also
                mDBHelper.deletePOSLineItems(mDeleteItem);
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        nr = 0;
        mDeleteItem.clear();
        //update the listview
        updateRow(AppConstants.posID, 0);
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 101 && resultCode == getActivity().RESULT_OK && data != null) {
            Bundle getBundle = null;
            getBundle = data.getExtras();
            long posID = getBundle.getLong("POSID");
            updateRow(posID, 0);
        }
    }

    private void confirmDialog() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());

        alertDialog.setTitle("Confirmation"); // Sets title for your alertbox

        alertDialog.setMessage("Are you sure you want to cancel the bill ?"); // Message to be displayed on alertbox

        alertDialog.setIcon(R.mipmap.ic_launcher); // Icon for your alertbox

        /* When positive (yes/ok) is clicked */
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //cancelOnOrder();
                FragmentManager localFragmentManager = getActivity().getSupportFragmentManager();
                OrderCancelFragment orderCancelFragment = new OrderCancelFragment();
                orderCancelFragment.show(localFragmentManager, "OrderCancelFragment");
            }
        });

        /* When negative (No/cancel) button is clicked*/
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }


    public void clearList() {
        try {
            lineItem.clear();
            //delete the positem table and pos customer table
            mDBHelper.deletePOSTables(AppConstants.posID);
            mTxtPOSNumber.setText("#000000");
            AppConstants.posID = 0;
            updateEmptyRow();

            ((POSActivity) getActivity()).resetProductAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // Butter Knife has an unbind method to do this automatically.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void showSnackBar(String message) {
        Snackbar snack = Snackbar.make(mHeaderLayout, message, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        snack.show();
    }

    private void postPOSDraftOrder() {

        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {
                mProDlg.setMessage("Preprint the order...");
                mProDlg.show();

                JSONObject mJsonObj = mParser.getParams(AppConstants.CALL_DRAFT_POS_ORDER);

                JSONArray mDraftedObj = new JSONArray();

                for (int i = 0; i < orderNumberList.size(); i++) {
                    double mPaidTotalCardAmount = 0;
                    double empty = 0;

                    // Create a hash map
                    HashMap hm = new HashMap();
                    // Put elements to the map
                    hm.put("CASH", empty);
                    hm.put("AMEX", empty);
                    hm.put("VISA", empty);
                    hm.put("MASTER", empty);
                    hm.put("GIFT", empty);
                    hm.put("OTHER", empty);

                    Customer mCustomer = mDBHelper.getPOSCustomer(orderNumberList.get(i));

                    List<POSLineItem> lineItem = mDBHelper.getPOSLineItems(orderNumberList.get(i), 0);
                    double mTotalBill = mDBHelper.sumOfProductsTotalPrice(orderNumberList.get(i));

                    JSONObject mOrderObj = new JSONObject();

                    JSONObject mHeaderObj = mParser.getHeaderObj(orderNumberList.get(i), mCustomer, lineItem.size(), mTotalBill, 0, 0, 0, mPaidTotalCardAmount);
                    mOrderObj.put("OrderHeaders", mHeaderObj);
                    JSONArray mPaymentObj = mParser.getPaymentObj(hm);
                    mOrderObj.put("PaymentDetails", mPaymentObj);

                    lineItem = mDBHelper.getPOSLineItems(orderNumberList.get(i), 0);
                    JSONArray mOrddersObj = mParser.getOrderItems(lineItem);
                    mOrderObj.put("OrderDetails", mOrddersObj);
                    mOrderObj.put("reason", "");
                    mOrderObj.put("authorizedBy", AppConstants.authorizedId);

                    if (mDBHelper.isKOTAvailable(orderNumberList.get(i))) {
                        List<Long> kotHeaderList = mDBHelper.getKOTNumberList(orderNumberList.get(i));
                        JSONArray mKOTObj = mParser.getPrintedKOT(kotHeaderList);
                        mOrderObj.put("KOTNumbers", mKOTObj);
                    }

                    mDraftedObj.put(mOrderObj);
                }

                //add multiple orders to array
                mJsonObj.put("DraftOrderDetails", mDraftedObj);

                Log.i("POST DRAFT ORDER", mJsonObj.toString());
                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.CALL_DRAFT_POS_ORDER);
                thread.start();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //show network failure dialog or toast
            NetworkErrorDialog.buildDialog(getActivity()).show();
        }
    }

    public List<Long> getOrderNumberList(){
        return orderNumberList;
    }

    public void updateCreditLimit(long bpId){
        BPartner bPartner = mDBHelper.getBPartner(mAppManager.getClientID(),mAppManager.getOrgID(),bpId);
        mBPId = bPartner.getBpId();
        mCreditLimit = bPartner.getCreditLimit();
        if(AppConstants.posID!=0){
            //update BusinessPartner
            mDBHelper.addPOSHeader(AppConstants.posID, bPartner.getBpId(), bPartner.getBpName(), bPartner.getBpPriceListId(), bPartner.getBpValue(), bPartner.getBpEmail(), bPartner.getBpNumber(), 1, "Y");
        }
    }

    public long getBPId(){
        return this.mBPId;
    }

    public double getCreditLimit(){
        return this.mCreditLimit;
    }

    public void setCreditLimitDefault(){
        this.mBPId = 0;
        this.mCreditLimit = 0;
    }
}

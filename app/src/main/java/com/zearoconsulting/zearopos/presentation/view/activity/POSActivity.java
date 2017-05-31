package com.zearoconsulting.zearopos.presentation.view.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.LKPrint;
import com.sewoo.request.android.AndroidMSR;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.domain.net.AsyncConnectTask;
import com.zearoconsulting.zearopos.domain.net.NetworkDataRequestThread;
import com.zearoconsulting.zearopos.domain.receivers.ConnectivityReceiver;
import com.zearoconsulting.zearopos.domain.services.TableStatusService;
import com.zearoconsulting.zearopos.presentation.exception.AppLog;
import com.zearoconsulting.zearopos.presentation.model.BPartner;
import com.zearoconsulting.zearopos.presentation.model.Category;
import com.zearoconsulting.zearopos.presentation.model.KOTHeader;
import com.zearoconsulting.zearopos.presentation.model.KOTLineItems;
import com.zearoconsulting.zearopos.presentation.model.Organization;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.POSOrders;
import com.zearoconsulting.zearopos.presentation.model.POSPayment;
import com.zearoconsulting.zearopos.presentation.model.Product;
import com.zearoconsulting.zearopos.presentation.model.Tables;
import com.zearoconsulting.zearopos.presentation.presenter.INetworkListeners;
import com.zearoconsulting.zearopos.presentation.presenter.IPOSListeners;
import com.zearoconsulting.zearopos.presentation.presenter.IPrintingListeners;
import com.zearoconsulting.zearopos.presentation.presenter.ITokenListeners;
import com.zearoconsulting.zearopos.presentation.presenter.OrderStatusListener;
import com.zearoconsulting.zearopos.presentation.presenter.TableSelectListener;
import com.zearoconsulting.zearopos.presentation.print.InvoiceBill;
import com.zearoconsulting.zearopos.presentation.print.KOTPrintTask;
import com.zearoconsulting.zearopos.presentation.print.PublicAction;
import com.zearoconsulting.zearopos.presentation.view.adapter.TableAdapter;
import com.zearoconsulting.zearopos.presentation.view.adapter.TableTouchHelper;
import com.zearoconsulting.zearopos.presentation.view.dialogs.AlertView;
import com.zearoconsulting.zearopos.presentation.view.dialogs.NetworkErrorDialog;
import com.zearoconsulting.zearopos.presentation.view.fragment.AboutUs;
import com.zearoconsulting.zearopos.presentation.view.fragment.AddNotesFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.AlertFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.AppUpdateFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.CategoryListFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.LogoutConfirmationFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.ManualSyncConfirmation;
import com.zearoconsulting.zearopos.presentation.view.fragment.MultiEditorFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.OrderListFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.PrinterSettingsFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.ProductListFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.SessionFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.ShowInvoiceListFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.TableChangeFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.TerminalPrinterFragment;
import com.zearoconsulting.zearopos.presentation.view.fragment.ViewTransactionFragment;
import com.zearoconsulting.zearopos.utils.AppConstants;
import com.zearoconsulting.zearopos.utils.Common;
import com.zearoconsulting.zearopos.utils.NetworkUtil;
import com.zearoconsulting.zearopos.utils.StringAlignUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import HPRTAndroidSDK.HPRTPrinterHelper;
import HPRTAndroidSDK.IPort;
import HPRTAndroidSDK.PublicFunction;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class POSActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener, IPOSListeners, ITokenListeners, MultiEditorFragment.OnMultiEditListener, SearchView.OnQueryTextListener, IPrintingListeners, OrderStatusListener.OnOrderStateListener {

    /**
     * Static variables
     */
    private static final String ORDER_LIST_TAG = "Order_List_Fragment";
    private static final int REQUEST_ENABLE_BT = 2;
    private static final String TAG = "POStActivity";
    private static final boolean D = true;
    private static final String ACTION_USB_PERMISSION = "com.ZearoConsulting.ZearoPOS";
    private static Context mContext;
    private static HPRTPrinterHelper HPRTPrinter = new HPRTPrinterHelper();
    private static IPort Printer = null;
    private final long DELAY = 200; // milliseconds
    boolean printType;
    SessionFragment sessionDialog = new SessionFragment();
    PrinterSettingsFragment printerSettingFragment = null;
    String strMode, strIPAddress;
    MenuItem mMenuNetwork;
    /**
     * View declaration varaiables
     */
    private LinearLayout mLayPOSType;
    private SlidingUpPanelLayout mSlidingLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private View headerLayout;
    private Toolbar mToolbar;
    private RecyclerView mTablesView;
    private Button mBtnTableService;
    private Button mBtnCounterSale;
    private ImageView mDrawerView;
    private TextView mTxtLogUser;
    private Menu optionsMenu;
    private ProductListFragment mProductFragment;
    private OrderListFragment mOrderFrag;
    /**
     * Fragment stacks and manager variables
     */
    private Stack<Fragment> fragmentStack;
    private FragmentManager fragmentManager;
    /**
     * KOT Table view and funcional usage variables
     */
    private Handler mTableUpdateHandler = new Handler();
    private Runnable mTableUpdateRunnable;
    private TableAdapter mTableAdapter;
    private List<Tables> mKOTTableList;
    private List<KOTHeader> kotHeaderList;

    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("Type");
            String jsonStr = msg.getData().getString("OUTPUT");

            switch (type) {
                case AppConstants.GET_POS_NUMBER:
                    mParser.parsePOSNumber(jsonStr, mHandler);
                    break;
                case AppConstants.NO_DATA_RECEIVED:
                    //INFORM USER NO DATA
                    break;
                case AppConstants.POS_NUMBER_RECEIVED:
                    //UPDATE THE POS NUMBER WITH CUSTOMER NAME
                    mProDlg.dismiss();
                    if (mOrderFrag != null && mOrderFrag.isVisible()) {
                        mOrderFrag.updateCustomerName(mAppManager.getCustomerName());
                        long posNum = mAppManager.getStartNumber();
                        mAppManager.setStartNumber(posNum);
                    }
                    break;
                case AppConstants.GET_KOT_HEADER_AND_lINES:
                    mParser.parseKOTData(jsonStr, mHandler);
                    break;
                case AppConstants.KOT_HEADER_AND_lINES_RECEIVED:
                    printKOTData();
                    break;
                case AppConstants.DRAFTED_TABLES_RECEIVED:
                    updateTableUI();
                    break;
                case AppConstants.POST_KOT_FLAGS:
                    mParser.parseKOTStatus(jsonStr, mHandler);
                    break;
                case AppConstants.POST_TABLE_CHANGE:
                    mParser.parseTableChangeStatus(jsonStr, mHandler);
                    break;
                case AppConstants.TABLE_CHANGE_SUCCESS:
                    mProDlg.dismiss();
                    updateTableUI();
                    break;
                case AppConstants.TABLE_CHANGE_FAILURE:
                    mProDlg.dismiss();
                    Toast.makeText(POSActivity.this, "Table change failure", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.KOT_FLAGS_RESPONSE_RECEIVED:
                    //update kot flag posted
                    break;
                case AppConstants.CHECK_CREDIT_LIMIT:
                    mParser.parseCreditLimit(jsonStr, mHandler);
                    break;
                case AppConstants.TABLE_KOT_DETAILS_RECEIVED_SUCCESS:
                    mProDlg.dismiss();
                    generateInvoice(Long.parseLong(jsonStr));
                    break;
                case AppConstants.TABLE_KOT_DETAILS_RECEIVED_FAILURE:
                    mProDlg.dismiss();
                    break;
                case AppConstants.CREDIT_LIMIT_RECEIVED:
                    //update cart to credit limit
                    mProDlg.dismiss();
                    if (mOrderFrag != null && mOrderFrag.isVisible()) {
                        mOrderFrag.updateCreditLimit(Long.parseLong(jsonStr));
                    }
                    break;
                case AppConstants.SESSION_EXPIRED:
                    mProDlg.dismiss();
                    showCreateSession();
                    break;
                case AppConstants.SERVER_ERROR:
                    //show the server error dialog
                    Toast.makeText(POSActivity.this, "Server data error", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.NETWORK_ERROR:
                    //INFORM USER NO DATA
                    Toast.makeText(POSActivity.this, "NETWORK ERROR...", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.CHECK_UPDATE_AVAILABLE:
                    mParser.parseAppUpdateAvailable(jsonStr, mHandler);
                    break;
                case AppConstants.NO_UPDATE_AVAILABLE:
                    mProDlg.dismiss();
                    Toast.makeText(POSActivity.this, "There is no update available", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.UPDATE_APP:
                    mProDlg.dismiss();
                    showAppInstallDialog();
                    break;
                default:
                    break;
            }
        }
    };
    private long selecteTableId = 0;
    /**
     * @TableSelectListener is used for user can select and generate the invoice of table.
     */
    public TableSelectListener tableSelectListener = new TableSelectListener() {
        @Override
        public void OnTableSelectedListener(Tables tableEntity) {
            try {
                selecteTableId = tableEntity.getTableId();
                generateInvoice(selecteTableId);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void OnTableSelectedChangeListener(Tables tableEntity) {
            try {
                tableToChange(tableEntity.getTableId());
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void OnTableChangeListener(long activeTableId, long targetTableId) {

            Log.i("TABLE CHANGE", activeTableId + "---" + targetTableId);
            tableChange(activeTableId, targetTableId);
        }
    };
    private LinearLayoutManager mTableLayoutManager;
    private DefaultItemAnimator mTableViewAnimator = new DefaultItemAnimator();
    private ItemTouchHelper.Callback mTableTouchCallback;
    private ItemTouchHelper mTableTouchHelper;
    /**
     * POSLineItems and Product variables
     */
    private List<Product> mProductList, mAllProductList;
    private List<POSLineItem> mPOSLineItems;
    private List<String> mAuthrizeIds;
    private Product mProduct;
    private long mCategoryId;
    private boolean mAllProduct = true;
    private double totalAmt = 0;
    private double finalAmt = 0;
    private double paidAmt = 0;
    private double returnAmt = 0;
    /**
     * ESCPOSConst bluetooth variables
     */
    private int bltResult = 0;
    private int mMSRMode = ESCPOSConst.LK_MSR_TRACK_1;
    private Handler msrHandler = new MSRHandler();
    /**
     * Timer for barcode scanning
     */
    private Timer mScanTimer = new Timer();
    private String barCode = "";
    /**
     * Device battery level capture and value update to ui variables
     */
    private BroadcastReceiver mBatteryLevelReceiver;
    private int mBatteryPercent = 0;
    /**
     * POS Number check and update variables
     */
    private Handler mPOSNumberHandler = new Handler();
    private Runnable mPOSNumberRunnable;

    private Handler mNetworkCheckHandler = new Handler();
    private Runnable mNetworkCheckRunnable;

    private ArrayAdapter arrPrinterList;
    private String ConnectType = "USB";
    private StringAlignUtils alignUtils = new StringAlignUtils(42, StringAlignUtils.Alignment.CENTER);
    private UsbManager mUsbManager = null;
    private UsbDevice device = null;
    private PendingIntent mPermissionIntent = null;
    private String printerName = "";
    private PublicFunction PFun = null;
    private PublicAction PAct = null;
    private String strPort = "9100";
    private Context thisCon = AndroidApplication.getAppContext();
    private ClipboardManager myClipboard;
    private ClipData myClip;

    private Intent mServiceIntent = null;

    /**
     * @BroadcastReceiver is used for notify the bluetooth status to application
     */
    private BroadcastReceiver mBltStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        System.out.println("STATE_OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        System.out.println("STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        System.out.println("STATE_ON");
                        displayBluetoothPrinter();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        System.out.println("STATE_TURNING_ON");
                        break;
                }

            }
        }
    };

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();

                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (HPRTPrinterHelper.PortOpen(device) != 0) {
                                HPRTPrinter = null;
                                Toast.makeText(mContext, "Printer connection error!", Toast.LENGTH_LONG).show();
                                return;
                            } else
                                Toast.makeText(mContext, "Printer connected", Toast.LENGTH_LONG).show();

                        } else {
                            return;
                        }
                    }
                }
                //断开连接
                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        HPRTPrinterHelper.PortClose();
                    }
                }
            } catch (Exception e) {
                Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> mUsbReceiver ")).append(e.getMessage()).toString());
            }
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                boolean result = bundle.getBoolean(TableStatusService.RESULT);
                showSnack(result);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posterminal);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = this;

        try {

            mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            mContext.registerReceiver(mUsbReceiver, filter);

            PFun = new PublicFunction(mContext);
            PAct = new PublicAction(mContext);
            InitCombox();

            //Register Bluetooth broadcast receiver
            registerReceiver(mBltStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

            //check storage permission is granted
            isStoragePermissionGranted();

            myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            myClip = ClipData.newPlainText("text", "");
            myClipboard.setPrimaryClip(myClip);

            //set the toolbar for search
            mToolbar = (Toolbar) findViewById(R.id.app_bar);
            setSupportActionBar(mToolbar);

            //check device is mobile and register the view for slidingUP
            if (!tabletSize) {
                mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
                mProductFragment = new ProductListFragment();
            } else {
                mProductFragment = (ProductListFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.product_fragment);
            }

            AppConstants.URL = AppConstants.kURLHttp + mAppManager.getServerAddress() + ":" + mAppManager.getServerPort() + AppConstants.kURLServiceName + AppConstants.kURLMethodApi;

            //create the OrderListFragment object
            mOrderFrag = (OrderListFragment) getSupportFragmentManager().findFragmentByTag(ORDER_LIST_TAG);

            //create the FragmentStack object
            fragmentStack = new Stack<Fragment>();

            //create the FragmentManager object
            fragmentManager = getSupportFragmentManager();

            //register the views based on Id's
            this.mTablesView = ((RecyclerView) findViewById(R.id.table_selection));
            this.mBtnTableService = (Button) findViewById(R.id.btnTableService);
            this.mBtnCounterSale = (Button) findViewById(R.id.btnCounterSale);
            this.mLayPOSType = (LinearLayout) findViewById(R.id.layPOSType);
            this.mDrawer = (NavigationView) findViewById(R.id.main_drawer);
            this.mDrawer.setNavigationItemSelectedListener(this);
            this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            this.headerLayout = mDrawer.getHeaderView(0); // 0-index header
            this.mDrawerView = (ImageView) headerLayout.findViewById(R.id.imgDrawerView);
            this.mTxtLogUser = (TextView) headerLayout.findViewById(R.id.txtLoggedUser);

            //create the ProgressDialog object
            mProDlg = new ProgressDialog(this);
            mProDlg.setIndeterminate(true);
            mProDlg.setCancelable(false);

            //update the currencyCode to AppConstant currencyCode
            AppConstants.currencyCode = mAppManager.getCurrencyCode();

            //update the userId default to AppConstant authorizedId
            AppConstants.authorizedId = mAppManager.getUserID();

            //update the view of username
            mTxtLogUser.setText("Hello " + mAppManager.getUserName());

            //set the organization imageview to mDrawerView
            Organization organization = mDBHelper.getOrganizationDetail(mAppManager.getOrgID());
            Glide.with(mContext)
                    .load(organization.getOrgImage())
                    .into(mDrawerView);

            //Get the tables from local database
            mKOTTableList = mDBHelper.getTables(mAppManager.getClientID(), mAppManager.getOrgID());

            //Get the table status from local database
            kotHeaderList = mDBHelper.getKOTHeaders(0, false);

            //create a table adapter
            mTableAdapter = new TableAdapter(mKOTTableList, kotHeaderList);
            mTableLayoutManager
                    = new LinearLayoutManager(POSActivity.this, LinearLayoutManager.HORIZONTAL, false);
            mTablesView.setLayoutManager(mTableLayoutManager);
            //mTablesView.setLayoutManager(new GridLayoutManager(this, 5));

            //add the animation to mTablesView
            mTableViewAnimator.setRemoveDuration(1000);
            mTablesView.setItemAnimator(mTableViewAnimator);
            mTablesView.setAdapter(mTableAdapter);

            //set the table select listener
            mTableAdapter.setOnTableSelectListener(tableSelectListener);

            // Setup ItemTouchHelper
            //mTableTouchCallback = new TableTouchHelper(mKOTTableList, mTableAdapter);
            //mTableTouchHelper = new ItemTouchHelper(mTableTouchCallback);
            //mTableTouchHelper.attachToRecyclerView(mTablesView);

            //mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.openDrawer, R.string.closeDrawer);
            //mDrawerLayout.addDrawerListener(mDrawerToggle);
            //mDrawerToggle.syncState();

            //Thread for checking pos number status in every 5 seconds
            mPOSNumberRunnable = new Runnable() {
                public void run() {
                    checkPOSNumber();
                    mPOSNumberHandler.postDelayed(this, 5 * 1000);
                }
            };

            mNetworkCheckRunnable = new Runnable() {
                @Override
                public void run() {
                    checkNetworkStatus();
                    mNetworkCheckHandler.postDelayed(this, 60 * 1000);
                }
            };

            if (savedInstanceState == null) {
                if (findViewById(R.id.displayOrderList) != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    OrderListFragment listFragment = new OrderListFragment();
                    ft.replace(R.id.displayOrderList, listFragment, ORDER_LIST_TAG);
                    ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                    ft.commit();
                }
            }

            // Check whether the Activity is using the layout verison with the fragment_container
            // FrameLayout and if so we must add the first fragment
            if (findViewById(R.id.fragment_product_container) != null) {

                // However if we are being restored from a previous state, then we don't
                // need to do anything and should return or we could end up with overlapping Fragments
                if (savedInstanceState != null) {
                    return;
                }

                // Create an Instance of Fragment
                CategoryListFragment mCategoryFragment = new CategoryListFragment();
                mCategoryFragment.setArguments(getIntent().getExtras());
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment_product_container, mCategoryFragment);
                fragmentStack.push(mCategoryFragment);
                fragmentTransaction.commit();
            }

            if (!tabletSize) {
                mSlidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {
                        Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                    }

                    @Override
                    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                        Log.i(TAG, "onPanelStateChanged " + newState);
                        if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {

                        } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                            if (mOrderFrag != null && mOrderFrag.isVisible()) {
                                mOrderFrag.showUserView();
                            }
                        } else {
                            if (mOrderFrag != null && mOrderFrag.isVisible()) {
                                mOrderFrag.hideUserView();
                            }
                        }
                    }
                });
            }

            //check the app for Retail or Restaurant
            if (!mAppManager.getIsRetail()) {
                mLayPOSType.setVisibility(View.VISIBLE);
                this.mTablesView.setVisibility(View.VISIBLE);

                //Thread for checking table status in every 5 seconds
                mTableUpdateRunnable = new Runnable() {
                    public void run() {
                        getTableDataService(); // some action(s)
                        mTableUpdateHandler.postDelayed(this, 5 * 1000);
                    }
                };

                //mServiceIntent = new Intent(this, TableStatusService.class);
                //this.startService(mServiceIntent);

                AppConstants.isTableService = true;
            }

            //check the app for isTableService and update the views
            if (AppConstants.isTableService)
                changeTableServiceBtnImage();
            else
                changeCounterSaleBtnImage();


            //CounterSale button onClick event
            mBtnCounterSale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppConstants.isTableService) {
                        AppConstants.posID = 0;

                        mOrderFrag.updatePOSNumber();
                        mOrderFrag.updateEmptyRow();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTablesView.setVisibility(View.GONE);
                                AppConstants.isTableService = false;

                            }
                        });

                        resetProductAdapter();

                        mBtnCounterSale.setBackgroundResource(R.drawable.tab_pressed);
                        mBtnCounterSale.setTextColor(Color.parseColor("#ffffff"));

                        mBtnTableService.setBackgroundResource(R.drawable.tab);
                        mBtnTableService.setTextColor(Color.parseColor("#000000"));
                    }
                }
            });

            //TableService button onClick event
            mBtnTableService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!AppConstants.isTableService) {
                        AppConstants.posID = 0;

                        mOrderFrag.updatePOSNumber();
                        mOrderFrag.updateEmptyRow();

                        mTablesView.setVisibility(View.VISIBLE);
                        AppConstants.isTableService = true;

                        resetProductAdapter();

                        mBtnTableService.setBackgroundResource(R.drawable.tab_pressed);
                        mBtnTableService.setTextColor(Color.parseColor("#ffffff"));

                        mBtnCounterSale.setBackgroundResource(R.drawable.tab);
                        mBtnCounterSale.setTextColor(Color.parseColor("#000000"));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_product, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(POSActivity.this);

        mMenuNetwork = menu.findItem(R.id.action_network_status);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        mAllProduct = false;
                        if (mProductList != null && mProductList.size() != 0 && mProductFragment != null && mProductFragment.isVisible()) {
                            mProductFragment.searchFilter(mProductList, mCategoryId);
                        }
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        mAllProduct = true;
                        mAllProductList = mDBHelper.getAllProduct(mAppManager.getClientID(), mAppManager.getOrgID());
                        if (mAllProductList != null && mAllProductList.size() != 0 && mProductFragment != null && mProductFragment.isVisible()) {
                            mProductFragment.searchFilter(mAllProductList, 0);
                        }
                        return true; // Return true to expand action view
                    }
                });

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        switch (item.getItemId()) {
            case R.id.action_card_reader:
                readMSRData();
                return true;
            case R.id.action_Refresh:
                restartTableService();
                return true;
            case R.id.action_settings:
                displayPrinterSettings();
                return true;
            default:
                return false;
        }
    }

    private void restartTableService() {

        if (AppConstants.isKOTParsing)
            AppConstants.isKOTParsing = false;

        if (mServiceIntent != null) {
            stopService(mServiceIntent);

            startService(mServiceIntent);
        }
    }

    private void displayPrinterSettings() {
        try {
            printerSettingFragment = new PrinterSettingsFragment();
            printerSettingFragment.show(fragmentManager, "OrderCancelFragment");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * GET POS NUMBER FROM ERP
     */
    public void getPOSNumber() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            mProDlg.setMessage("Getting order numbers...");
            mProDlg.show();
            JSONObject mJsonObj = mParser.getParams(AppConstants.GET_POS_NUMBER);
            NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.GET_POS_NUMBER);
            thread.start();
        } else {
            //show network failure dialog or toast
            showErrorDialog("Please Check Your Internet Connection!");
        }
    }

    @Override
    protected void onResume() {

        try {
            mPOSNumberHandler.postDelayed(mPOSNumberRunnable, 5000);
            mTableUpdateHandler.postDelayed(mTableUpdateRunnable, 5000);
            mNetworkCheckHandler.postDelayed(mNetworkCheckRunnable, 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onResume();

        //register receiver
        registerReceivers();

        registerReceiver(receiver, new IntentFilter(
                TableStatusService.NOTIFICATION));

        // register connection status listener
        AndroidApplication.getInstance().setConnectivityListener(this);

        //register the token listener to parser
        mParser.setTokeListener(POSActivity.this);

        //register the order state listener
        OrderStatusListener.getInstance().setListener(this);

        //register the @OrderListFragment fragment object
        mOrderFrag = (OrderListFragment) getSupportFragmentManager().findFragmentByTag(ORDER_LIST_TAG);

        //Get the authorized id's for giving permission to order delete, cancel and edit
        mAuthrizeIds = mDBHelper.getAuthorizeId(mAppManager.getOrgID());

        if (String.valueOf(mAppManager.getSessionId()).equalsIgnoreCase("0")) {
            //show create session
            try {

                if (sessionDialog != null && sessionDialog.getDialog() != null
                        && sessionDialog.getDialog().isShowing()) {
                    //dialog is showing so do something
                    Log.i("SESSION", "It's Showing");
                } else {
                    //dialog is not showing
                    Bundle bundle = new Bundle();
                    bundle.putString("sessionType", "CREATE");
                    bundle.putString("sessionDesc", "New");
                    sessionDialog.setArguments(bundle);
                    sessionDialog.show(fragmentManager, "SESSION");
                }
                //SessionFragment.newInstance(POSActivity.this, "CREATE", "New").show(fragmentManager, "SESSION");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!mAppManager.getSessionStatus()) {
            //show resume session with close session
            try {
                if (sessionDialog != null && sessionDialog.getDialog() != null
                        && sessionDialog.getDialog().isShowing()) {
                    //dialog is showing so do something
                    Log.i("SESSION", "It's Showing");
                } else {
                    //dialog is not showing
                    Bundle bundle = new Bundle();
                    bundle.putString("sessionType", "RESUME");
                    bundle.putString("sessionDesc", "Resume");
                    sessionDialog.setArguments(bundle);
                    sessionDialog.show(fragmentManager, "SESSION");
                }
                //SessionFragment.newInstance(POSActivity.this, "RESUME", "Resume").show(fragmentManager, "SESSION");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ConnectType = mAppManager.getPrinterMode();
            if (ConnectType.equalsIgnoreCase("Bluetooth")) {
                //check bluetoothport is null
                if (mBluetoothAdapter != null) {
                    if (mBluetoothAdapter.isEnabled() && bluetoothPort == null) {
                        displayBluetoothPrinter();
                        return;
                    }
                }
            }
        }

        hideSoftKeyboard();
    }

    /**
     * check the pos number every 5 seconds.
     * If pos number reaches end no. this method will trigger and get
     * another array of pos numbers
     */
    private void checkPOSNumber() {
        Log.i("POSACTIVITY", "GET POS NUMBERS CHECKING");

        long lastTransTime = mDBHelper.lastTransactionTime();

        long currentTime = System.currentTimeMillis();

        long diff = currentTime - lastTransTime;

        if (mAppManager.getStartNumber().equals(mAppManager.getEndNumber())) {
            Log.i("POSACTIVITY", "GET POS NUMBERS");
            getPOSNumber();
        }
    }

    private void checkNetworkStatus() {
        AsyncConnectTask task = new AsyncConnectTask(POSActivity.this, new INetworkListeners() {
            @Override
            public void onNetworkStatus(boolean result) {
                showSnack(result);
                /*if (result == true) {
                    Toast.makeText(POSActivity.this, "Connection Succesful",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(POSActivity.this, "Connection Failed", Toast.LENGTH_LONG).show();
                }*/
            }
        });

        task.execute();
    }

    /**
     * Reset the products if order cancelled or completed
     */
    public void resetProductAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProductFragment != null && mProductFragment.isVisible()) {
                    mProductFragment.clearAllProduct();
                }
            }
        });
    }

    /**
     * This method is used for update the product to cart.
     * The product selection using Barcode scanner
     *
     * @param prodVal
     * @param categoryId
     */
    public void onUpdateScannedItemToCart(String prodVal, long categoryId) {
        try {
            mProduct = mDBHelper.getProduct(mAppManager.getClientID(), mAppManager.getOrgID(), prodVal);
            if (mProduct != null)
                updateProductToCart(mProduct.getCategoryId(), mProduct, true);
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui
                        Toast.makeText(POSActivity.this, "PRODUCT NOT AVAILABLE", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * All IPOSListeners methods are implemented
     */
    @Override
    public void onUpdateProductToCart(long categoryId, Product mProduct, boolean addOrRemove) {
        updateProductToCart(categoryId, mProduct, addOrRemove);
    }

    @Override
    public void addOrRemoveItemFromCart(long categoryId, final POSLineItem mProduct, boolean addOrRemove) {
        if (!AppConstants.isOrderPrinted) {
            if (addOrRemove) {
                Product product = mDBHelper.getProduct(mAppManager.getClientID(), mAppManager.getOrgID(), mProduct.getProductId());
                mDBHelper.addPOSLineItem(AppConstants.posID, 0, product, 1);
            } else if (mProduct.getIsKOTGenerated().equalsIgnoreCase("N")) {
                mDBHelper.removePOSLineItemFromCart(AppConstants.posID, mProduct.getRowId(), mProduct.getProductId(), mProduct.getStdPrice(), 1);
            } else if (mProduct.getIsKOTGenerated().equalsIgnoreCase("Y")) {
                mOrderFrag.showSnackBar("Cannot delete the kot generated item...");
            } /*else if (AppConstants.permissionGranted) {
                mDBHelper.removePOSLineItemFromCart(AppConstants.posID, mProduct.getRowId(), mProduct.getProductId(), mProduct.getStdPrice(), 1);
                AppConstants.permissionGranted = false;
            }*/

            if (mOrderFrag != null && mOrderFrag.isVisible()) {
                //DO STUFF
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui
                        mOrderFrag.updateRow(AppConstants.posID, mProduct.getProductId());
                    }
                });
            }

            if (mProductFragment != null && mProductFragment.isVisible()) {
                mProductFragment.updateProduct(categoryId, mProduct, addOrRemove);
            }

        } else if (AppConstants.isOrderPrinted) {
            mOrderFrag.showSnackBar("Call Supervisor to unlock the Order.");
        }
    }

    /**
     * OnClick of List of orders available from stackview
     *
     * @param orders
     */
    @Override
    public void onSelectedOrder(POSOrders orders) {

        //update the customer name and pos number
        AppConstants.posID = orders.getPosId();
        if (AppConstants.posID != 0) {
            mOrderFrag.updateCustomerName(orders.getCustomerName());
            mOrderFrag.updatePOSNumber();
        } else {
            //update pos number
            AppConstants.isOrderPrinted = false;

            mOrderFrag.setCreditLimitDefault();
        }

        //update the cart items
        mOrderFrag.updatePOSNumber();
        mOrderFrag.setVisibility();
        mOrderFrag.updateRow(orders.getPosId(), 0);

        //here we have to check the preprinted status
        mOrderFrag.updateMode(orders.getPosId());

        resetProductAdapter();

        boolean isKOTAvail = mDBHelper.isKOTAvailable(AppConstants.posID);

        if (isKOTAvail) {
            changeTableServiceBtnImage();
            long tableId = mDBHelper.getKOTTableId(AppConstants.posID);
            generateInvoice(tableId);
        } else {
            changeCounterSaleBtnImage();
        }
    }

    @Override
    public void onSelectedCategory(Category category) {

        if (mProductFragment != null && tabletSize) {
            // If description is available, we are in two pane layout
            // so we call the method in DescriptionFragment to update its content
            mCategoryId = category.getCategoryId();
            mProductFragment.refreshProducts(category.getCategoryId());
        } else {
            try {
                mCategoryId = category.getCategoryId();
                Bundle args = new Bundle();
                args.putLong("categoryId", mCategoryId);
                mProductFragment.setArguments(args);

                // Replace whatever is in the fragment_product_container view with this fragment
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment_product_container, mProductFragment);
                fragmentTransaction.hide(fragmentStack.lastElement());
                fragmentStack.lastElement().onPause();
                fragmentStack.push(mProductFragment);
                fragmentTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void orderCancelSuccess() {
        mDBHelper.releasePOSOrder(AppConstants.posID);
        if (mDBHelper.isKOTAvailable(AppConstants.posID)) {

            List<Long> kotTableList = mDBHelper.getKOTTableList(AppConstants.posID);
            mDBHelper.deleteKOTItems(AppConstants.posID);
            for (int i = 0; i < kotTableList.size(); i++) {
                List<KOTLineItems> kotLineItemList = mDBHelper.getKOTLineItems(kotTableList.get(i));
                if (kotLineItemList.size() == 0)
                    mDBHelper.deleteKOTDetails(kotTableList.get(i));
            }
        }
        mOrderFrag.clearList();
        mOrderFrag.setCreditLimitDefault();
    }

    @Override
    public void orderCancelFailure() {
        mOrderFrag.showSnackBar("Order cancel failure...");
    }

    @Override
    public void onReprintOrder(POSOrders orders) {

        double mTotalAmount = mDBHelper.sumOfProductsWithoutDiscount(orders.getPosId());
        double mFinalAmount = mDBHelper.sumOfProductsTotalPrice(orders.getPosId());

        POSPayment payment = mDBHelper.getPaymentDetails(orders.getPosId());
        if (payment != null) {
            printFromPayment(orders.getPosId(), mTotalAmount, mFinalAmount, mFinalAmount, 0, payment.getCash(), payment.getAmex(), payment.getGift(), payment.getMaster(), payment.getVisa(), payment.getOther(), 0);
        } else {
            printFromPayment(orders.getPosId(), mTotalAmount, mFinalAmount, mFinalAmount, 0, 0, 0, 0, 0, 0, 0, 0);
        }

    }

    @Override
    public void onPrinterConnection(String mode, String ipAddress) {

        if (mode.equalsIgnoreCase("USB")) {
            mAppManager.savePrinterData(mode, ipAddress);
            connectPrinter();

            if (printerSettingFragment != null)
                printerSettingFragment.dismissAllowingStateLoss();

            AppLog.e("PRINTER CONNECTION MODE ", mode);

        } else if (mode.equalsIgnoreCase("WiFi")) {

            strMode = mode;
            strIPAddress = ipAddress;

            new WiFiPrinteConnectionTask().execute();

            AppLog.e("PRINTER CONNECTION MODE ", mode);

        } else if (mode.equalsIgnoreCase("Bluetooth")) {
            mAppManager.savePrinterData(mode, ipAddress);
            if (printerSettingFragment != null)
                printerSettingFragment.dismissAllowingStateLoss();

            AppLog.e("PRINTER CONNECTION MODE ", mode);

            connectBluetoothPrinter();
        }
    }

    /**
     * @param categoryId
     * @param mProduct
     * @param addOrRemove
     */
    private void updateProductToCart(long categoryId, final Product mProduct, boolean addOrRemove) {

        if (mProduct.getProdId() == 0) {
            Toast.makeText(POSActivity.this, "PRODUCT NOT AVAILABLE", Toast.LENGTH_SHORT).show();
        } else if (mProduct.getCostPrice() == 0) {
            Toast.makeText(POSActivity.this, "PRODUCT COST PRICE CAN NOT BE 0 !", Toast.LENGTH_SHORT).show();
        } else if (mProduct.getSalePrice() == 0) {
            Toast.makeText(POSActivity.this, "PRODUCT SALES PRICE CAN NOT BE 0 !", Toast.LENGTH_SHORT).show();
        } else if (!AppConstants.isOrderPrinted) {
            if (AppConstants.posID == 0) {

                //GET THE POS Start number from AppDataManager and assign to posId constants and header to table
                if (mAppManager.getStartNumber().equals(mAppManager.getEndNumber())) {
                    Log.i("POSACTIVITY", "GET POS NUMBERS");
                    getPOSNumber();
                    return;
                } else if (!mAppManager.getStartNumber().equals(mAppManager.getEndNumber())) {
                    changeCounterSaleBtnImage();
                    long posNumber = mAppManager.getStartNumber();
                    AppConstants.posID = posNumber;
                    posNumber = posNumber + 1;
                    mAppManager.setStartNumber(posNumber);
                    mOrderFrag.updatePOSNumber();
                } else {
                    Log.i("POSACTIVITY", "GET POS NUMBERS");
                    //getPOSNumbers();
                }
            }

            addPOSHeader("N");

            if (addOrRemove)
                mDBHelper.addPOSLineItem(AppConstants.posID, 0, mProduct, 1);
            else
                mDBHelper.removePOSLineItem(AppConstants.posID, mProduct.getProdId(), mProduct.getSalePrice(), 1);

            if (mOrderFrag != null && mOrderFrag.isVisible()) {
                //DO STUFF
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui
                        mOrderFrag.updateRow(AppConstants.posID, mProduct.getProdId());
                    }
                });

            } else {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                mOrderFrag = new OrderListFragment();
                ft.replace(R.id.displayOrderList, mOrderFrag, ORDER_LIST_TAG);
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                ft.commit();
            }
        } else if (AppConstants.isOrderPrinted) {
            mOrderFrag.showSnackBar("Call Supervisor to unlock the Order.");
        }
    }

    /**
     * add header of order and isKOT or CounterSale
     *
     * @param isKOT
     */
    public void addPOSHeader(String isKOT) {

        POSOrders order = mDBHelper.getPosHeader(AppConstants.posID);

        if (order == null) {
            if (mOrderFrag.getBPId() == 0)
                mDBHelper.addPOSHeader(AppConstants.posID, mAppManager.getCustomerBPId(), mAppManager.getCustomerName(), mAppManager.getPriceListID(), mAppManager.getCustomerValue(), mAppManager.getCustomerEmail(), mAppManager.getCustomerNumber(), 1, isKOT);
            else {
                BPartner bPartner = mDBHelper.getBPartner(mAppManager.getClientID(), mAppManager.getOrgID(), mOrderFrag.getBPId());
                mDBHelper.addPOSHeader(AppConstants.posID, bPartner.getBpId(), bPartner.getBpName(), bPartner.getBpPriceListId(), bPartner.getBpValue(), bPartner.getBpEmail(), bPartner.getBpNumber(), 1, isKOT);
            }
        } else {
            if (mAppManager.getCustomerBPId() == order.getBpId())
                return;
            else
                mDBHelper.addPOSHeader(AppConstants.posID, order.getBpId(), order.getCustomerName(), mAppManager.getPriceListID(), mAppManager.getCustomerValue(), mAppManager.getCustomerEmail(), mAppManager.getCustomerNumber(), 1, isKOT);
        }
    }

    /**
     * Generate the invoice for selected table
     * If table already having the invoice number in lines, it will
     * update the rows otherwise it will create a order header and
     * order line items with flag KOT_ORDER_GENERATED
     *
     * @param tableId
     */
    private void generateInvoice(long tableId) {

        try {

            long invoicNumber = 0;
            //get the table id and kot numbers of table
            kotHeaderList = mDBHelper.getKOTHeaders(tableId, false);
            List<KOTLineItems> kotLineItemList = mDBHelper.getKOTLineItems(tableId);
            Tables table = mDBHelper.getTableData(mAppManager.getClientID(), mAppManager.getOrgID(), tableId);

            if (table.getOrderAvailable().equalsIgnoreCase("N")) {
                return;
            } else if ((kotHeaderList.size() == 0 || kotLineItemList.size() == 0) && table.getOrderAvailable().equalsIgnoreCase("Y")) {
                //call the getTable Data
                getTableKOTData(tableId);
            } else if (kotHeaderList.size() == 0) {
                return;
            } else {
                if (kotLineItemList.size() == 0) {
                    return;
                } else {
                    //get the invoice number from KOTHeader table
                    List<Long> invNumList = mDBHelper.getKOTInvoiceNumbers(tableId);

                    if (invNumList.size() > 1) {
                        //show popup dialog and select the invoice number
                        ShowInvoiceListFragment.newInstance(POSActivity.this, tableId).show(fragmentManager, "INVOICE");
                        return;
                    } else if (invNumList.size() == 0) {
                        invoicNumber = 0;
                    } else {
                        invoicNumber = invNumList.get(0);
                    }

                    if (invoicNumber == 0) {
                        if (mAppManager.getStartNumber().equals(mAppManager.getEndNumber())) {
                            Log.i("POSACTIVITY", "GET POS NUMBERS");
                            getPOSNumber();
                            return;
                        } else if (!mAppManager.getStartNumber().equals(mAppManager.getEndNumber())) {
                            long posNumber = mAppManager.getStartNumber();
                            AppConstants.posID = posNumber;
                            posNumber = posNumber + 1;
                            mAppManager.setStartNumber(posNumber);
                            mOrderFrag.updatePOSNumber();
                            updateKOTtoInvoice(AppConstants.posID, tableId);
                        }
                    } else {
                        AppConstants.posID = invoicNumber;
                        mOrderFrag.updatePOSNumber();
                        //mDBHelper.deletePOSTables(AppConstants.posID);
                        updateKOTtoInvoice(AppConstants.posID, tableId);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Create a orderHeader and orderLineItems from selected
     * table
     *
     * @param posId
     */
    public void updateKOTtoInvoice(long posId, long tableId) {

        //get the table id and kot numbers of table
        kotHeaderList = mDBHelper.getKOTHeaders(tableId, false);

        //assign the pos number to kot's
        mDBHelper.updateInvoiceToKOT(tableId, posId);

        //List<KOTLineItems> listitem = mDBHelper.getKOTLineItems(selecteTableId, posId);

        mDBHelper.updateKOTSelectedStatusWithPOSId(tableId, "Y", posId);

        //get the kot line items
        List<KOTLineItems> kotLineItemList = mDBHelper.getKOTLineItems(tableId, posId);

        //insert to invoice table
        addPOSHeader("Y");

        for (int i = 0; i < kotLineItemList.size(); i++) {
            KOTLineItems kotLineItems = kotLineItemList.get(i);
            Product product = kotLineItems.getProduct();

            mDBHelper.addKOTtoPOSLineItem(posId, kotLineItems, product);
        }

        mOrderFrag.checkAndUpdateView(posId, 0);
        mOrderFrag.updateMode(posId);
    }

    private void tableToChange(long tableId){

        Tables table = mDBHelper.getTableData(mAppManager.getClientID(), mAppManager.getOrgID(), tableId);
        if (table.getOrderAvailable().equalsIgnoreCase("N")) {
            return;
        } else{
            //show tableChange fragment dialog
            TableChangeFragment.newInstance(POSActivity.this, tableId).show(fragmentManager, "TableChange");
        }
    }

    private void getTableKOTData(long tableId) {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {

                mProDlg.setMessage("Getting tables details...");
                mProDlg.show();

                AppConstants.URL = AppConstants.kURLHttp + mAppManager.getServerAddress() + ":" + mAppManager.getServerPort() + AppConstants.kURLServiceName + AppConstants.kURLMethodApi;
                JSONObject mJsonObj = mParser.getParams(AppConstants.GET_TABLE_KOT_DETAILS);
                mJsonObj.put("tableId", tableId);
                Log.i("KOTJson", mJsonObj.toString());

                //NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.GET_TABLE_KOT_DETAILS);
                //thread.start();

                AndroidApplication.getInstance().cancelPendingRequests(this);

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.URL, mJsonObj,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                mParser.parseTableKOTDataResponse(response.toString(), mHandler);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Server Connection error...", Toast.LENGTH_SHORT).show();
                        mProDlg.dismiss();
                    }
                }
                );

                // Adding request to request queue
                AndroidApplication.getInstance().addToRequestQueue(jsonObjReq);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //show network failure dialog or toast
            showErrorDialog("Please Check Your Internet Connection!");
        }
    }

    private void getTableDataService() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            JSONObject mJsonObj = mParser.getParams(AppConstants.GET_KOT_HEADER_AND_lINES);
            //NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.GET_KOT_HEADER_AND_lINES);
            //thread.start();

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConstants.URL, mJsonObj,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            mParser.parseKOTData(response.toString(), mHandler);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Server connection error! Try Again...", Toast.LENGTH_SHORT).show();
                }
            }
            );

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    20 * 1000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Adding request to request queue
            AndroidApplication.getInstance().addToRequestQueue(jsonObjReq);
        } else {
            //show network failure dialog or toast
            showSnack(false);
        }
    }

    private void postKOTPrintedData() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {

                List<KOTHeader> kotPrintList = mDBHelper.getKOTHeadersNotPrinted();
                JSONObject mJsonObj = mParser.getParams(AppConstants.POST_KOT_FLAGS);
                JSONArray mKOTObj = mParser.getPOSTPrintedKOT(kotPrintList);
                mJsonObj.put("KOTNumbers", mKOTObj);
                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.POST_KOT_FLAGS);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //show network failure dialog or toast
            showSnack(false);
        }
    }

    /**
     * This method is used for selected Invoice from @ShowInvoiceListFragment
     *
     * @param invoiceNum
     */
    public void showSelectedInvoice(long invoiceNum) {

        AppConstants.posID = invoiceNum;

        List<KOTLineItems> kotLineItemList = mDBHelper.getKOTLineItems(selecteTableId, invoiceNum);

        for (int i = 0; i < kotLineItemList.size(); i++) {
            KOTLineItems kotLineItems = kotLineItemList.get(i);
            Product product = kotLineItems.getProduct();

            mDBHelper.addKOTtoPOSLineItem(AppConstants.posID, kotLineItems, product);
        }

        mOrderFrag.updatePOSNumber();

        mOrderFrag.updateRow(AppConstants.posID, 0);
    }

    //set the selected category products
    public void setProductList(List<Product> models) {
        this.mProductList = models;
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mSlidingLayout != null &&
                (mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            //super.onBackPressed();

            if (fragmentStack.size() == 2) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                fragmentStack.lastElement().onPause();
                ft.remove(fragmentStack.pop());
                fragmentStack.lastElement().onResume();
                ft.show(fragmentStack.lastElement());
                ft.commit();
            }
        }
    }

    //add printer list
    private void InitCombox() {
        try {
            arrPrinterList = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item);
            String strSDKType = mContext.getString(R.string.sdk_type);
            if (strSDKType.equals("all"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_all, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("hprt"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_hprt, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("mkt"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_mkt, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("mprint"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_mprint, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("sycrown"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_sycrown, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("mgpos"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_mgpos, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("ds"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_ds, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("cst"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_cst, android.R.layout.simple_spinner_item);
            if (strSDKType.equals("other"))
                arrPrinterList = ArrayAdapter.createFromResource(mContext, R.array.printer_list_other, android.R.layout.simple_spinner_item);
            arrPrinterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            printerName = arrPrinterList.getItem(0).toString();
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> InitCombox ")).append(e.getMessage()).toString());
        }
    }

    private void connectBluetoothPrinter() {

        // Initialize
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            displayBluetoothPrinter();
        }
    }

    private void connectPrinter() {

        if (HPRTPrinter != null) {
            HPRTPrinterHelper.PortClose();
        }

        HPRTPrinter = new HPRTPrinterHelper(mContext, printerName);
        //USB not need call "iniPort"
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        boolean HavePrinter = false;
        while (deviceIterator.hasNext()) {
            device = deviceIterator.next();
            int count = device.getInterfaceCount();
            for (int i = 0; i < count; i++) {
                UsbInterface intf = device.getInterface(i);
                //Class ID为7表示该USB设备为打印机设备
                if (intf.getInterfaceClass() == 7) {
                    HavePrinter = true;
                    mUsbManager.requestPermission(device, mPermissionIntent);
                }
            }
        }

        //ConnectType = mSharedPreferences.getString("prefPrintOptions", "USB");
        ConnectType = mAppManager.getPrinterMode();
        if (!HavePrinter && ConnectType.equalsIgnoreCase("USB"))
            Toast.makeText(mContext, "Please connect usb printer", Toast.LENGTH_LONG).show();

    }

    /**
     * All SEARCH KEY INTERFACE methods are implemented here
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        List<Product> mSearchList;
        if (mAllProduct) {
            mSearchList = mAllProductList;
        } else {
            mSearchList = mProductList;
        }

        if (mSearchList != null && mSearchList.size() != 0 && mProductFragment != null && mProductFragment.isVisible()) {
            final List<Product> filteredModelList = filter(mSearchList, query);
            mProductFragment.searchFilter(filteredModelList, 0);
        }

        return true;
    }

    private List<Product> filter(List<Product> models, String query) {
        query = query.toLowerCase();

        final List<Product> filteredModelList = new ArrayList<>();
        for (Product model : models) {
            final String name = model.getProdName().toLowerCase();
            final String value = model.getProdValue().toLowerCase();
            if (name.contains(query)) {
                filteredModelList.add(model);
            } else if (value.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    /**
     * All OnMultiEditListener methods are implemented here
     */
    @Override
    public void OnBackToList() {
        if (mOrderFrag != null && mOrderFrag.isVisible()) {
            mOrderFrag.backToList();
        }
    }

    @Override
    public void OnBackToCardView() {
        if (mOrderFrag != null && mOrderFrag.isVisible()) {
            mOrderFrag.backToCardView();
        }
    }

    @Override
    public void OnUpdateProduct(long prodId, int qty) {
        if (mProductFragment != null && mProductFragment.isVisible()) {
            mProductFragment.updateProduct(mCategoryId, prodId, qty);
        }
    }

    @Override
    public void OnUpdateDiscountView(boolean isTotal, int discType) {
        if (mOrderFrag != null && mOrderFrag.isVisible()) {
            if (isTotal)
                mOrderFrag.updateTotalDiscountView(discType);
            else
                mOrderFrag.updateProductDiscountView(discType);
        }
    }

    @Override
    public void OnUpdateDiscountValueView(boolean isTotal, int discType, String value) {
        if (mOrderFrag != null && mOrderFrag.isVisible()) {
            if (isTotal)
                mOrderFrag.updateTotalDiscountValueView(discType, value);
            else
                mOrderFrag.updateProductDiscountValueView(discType, value);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        char pressedKey = (char) event.getUnicodeChar();
        barCode += pressedKey;

        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            mScanTimer.cancel();
            mScanTimer = new Timer();
            mScanTimer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            // Do work
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onUpdateScannedItemToCart(barCode.trim(), 0);
                                    barCode = "";
                                }
                            });
                        }
                    },
                    DELAY
            );
        }

        return super.onKeyDown(keyCode, event);
    }

    //CHECK THE STORAGE PERMISSION
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.navigation_item_1) {
            mDrawerLayout.closeDrawer(GravityCompat.START);

            AboutUs aboutUs = new AboutUs();
            aboutUs.show(fragmentManager, "AboutUs");
        }

        if (menuItem.getItemId() == R.id.navigation_item_2) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            /*Intent mIntent = new Intent(this, ManualSyncActivity.class);
            startActivity(mIntent);*/

            ManualSyncConfirmation manualSyncConfirmation = new ManualSyncConfirmation();
            manualSyncConfirmation.show(fragmentManager, "ManualSyncConfirmation");

            //return true;
        }

        if (menuItem.getItemId() == R.id.navigation_item_3) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            displayBluetoothPrinter();
        }

        if (menuItem.getItemId() == R.id.navigation_item_4) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            TerminalPrinterFragment terminalPrinterFragment = new TerminalPrinterFragment();
            terminalPrinterFragment.show(fragmentManager, "TerminalPrinterFragment");
        }

        if (menuItem.getItemId() == R.id.navigation_item_5) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            ViewTransactionFragment viewTransactionFragment = new ViewTransactionFragment();
            viewTransactionFragment.show(fragmentManager, "ViewTransactionFragment");
        }

        if (menuItem.getItemId() == R.id.navigation_item_6) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            checkUpdateAvailable();
        }

        if (menuItem.getItemId() == R.id.navigation_item_7) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            //logout();
            LogoutConfirmationFragment logoutConfirmationFragment = new LogoutConfirmationFragment();
            logoutConfirmationFragment.show(fragmentManager, "LogoutConfirmationFragment");
        }

        return false;
    }

    /**
     * SHOW THE MULTIEDITOR WINDOW FOR DISCOUNT
     *
     * @param categoryId
     * @param mProduct
     */
    public void showMultiEditor(long categoryId, POSLineItem mProduct) {

        mOrderFrag.showDiscountView(categoryId, mProduct);
    }

    /**
     * SHOW THE TOAST IF USER TRYING TO DELETE AND EDIT THE ORDER. IF ORDER ALREADY IS PREPRINTED
     */
    public void showPermission() {
        mOrderFrag.showSnackBar("Call Supervisor to unlock the Order.");
    }

    public void showAddNotes(long posId, long categoryId, POSLineItem mPoslineItem){
        if(mPoslineItem.getIsKOTGenerated().equalsIgnoreCase("N")){
            try {
                AddNotesFragment.newInstance(POSActivity.this, posId,mPoslineItem.getRowId(),mPoslineItem.getProductId()).show(fragmentManager, "TableChange");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{

        }
    }

    /**
     * DISPLAY THE BLUETOOTH PRINTER
     */
    private void displayBluetoothPrinter() {
        try {
            Intent actLog = new Intent(mContext,
                    ConnectPrinterActivity.class);
            startActivity(actLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printBill(long orderNum, List<POSLineItem> lineItem, double totalAmt, double finalAmt, double paidAmt, double returnAmt) {

        try {
            //ConnectType = mSharedPreferences.getString("prefPrintOptions", "USB");
            ConnectType = mAppManager.getPrinterMode();
            if (totalAmt != 0 && !ConnectType.equalsIgnoreCase("Bluetooth")) {
                printPreInvoice(orderNum, lineItem, totalAmt, finalAmt, paidAmt, returnAmt);
                //return;
            } else {
                if (bluetoothPort == null) {
                    AlertView.showAlert("Info", "Connect Printer", mContext);
                } else if (bluetoothPort.isConnected() && mBluetoothAdapter.isEnabled()) {

                    if (androidMSR != null) {
                        androidMSR.cancelMSR();
                    }

                    try {
                        if (mBluetoothAdapter.isEnabled()) {
                            Organization orgDetail = mDBHelper.getOrganizationDetail(mAppManager.getOrgID());
                            lineItem = mDBHelper.getPOSLineItems(orderNum, 0);
                            InvoiceBill bill = new InvoiceBill(mContext, orderNum, lineItem, totalAmt, finalAmt, paidAmt, returnAmt);
                            bill.setOrgDetails(orgDetail);
                            bltResult = bill.billGenerateAndPrint();
                        } else {
                            connectBluetoothPrinter();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (bltResult != 0) {
                        switch (bltResult) {
                            case ESCPOSConst.STS_PAPEREMPTY:
                                AlertView.showAlert("Error", "Paper Empty", mContext);
                                break;
                            case ESCPOSConst.STS_COVEROPEN:
                                AlertView.showAlert("Error", "Cover Open", mContext);
                                break;
                            case ESCPOSConst.STS_PAPERNEAREMPTY:
                                AlertView.showAlert("Warning", "Paper Near Empty", mContext);
                                break;
                        }
                    } else {
                        //printing success and update the poscustomer table isPrinted = Y
                        mDBHelper.prePrintOrder(orderNum, "Y");
                        AppConstants.isOrderPrinted = true;

                        //make lock the order
                        if (orderNum == AppConstants.posID)
                            mOrderFrag.updateMode(AppConstants.posID);

                        mOrderFrag.showSnackBar("Order Printing");
                    }
                } else {
                    AlertView.showAlert("Info", "Connect Printer", mContext);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printPreInvoice(long orderNum, List<POSLineItem> mLineItem, double mTotalAmt, double mFinalAmt, double mPaidAmt, double mReturnAmt) {

        mPOSLineItems = mLineItem;
        totalAmt = mTotalAmt;
        finalAmt = mFinalAmt;
        paidAmt = mPaidAmt;
        returnAmt = mReturnAmt;

        //ConnectType = mSharedPreferences.getString("prefPrintOptions", "USB");
        ConnectType = mAppManager.getPrinterMode();
        if (ConnectType.equalsIgnoreCase("USB")) {

            Log.i("POSActivity-LAN PRINTER", "PRINTER CONNECTED");
            if (HPRTPrinterHelper.PortOpen(device) != 0) {
                HPRTPrinter = null;
                Toast.makeText(mContext, "Printer connection error!", Toast.LENGTH_LONG).show();
                updatePrintStatus("QUICK", orderNum);
            } else {
                AppLog.e("PRE-PRINTING", orderNum + " CALLING ");
                //connectPrinter();
                USBPrintTaskParams params1 = new USBPrintTaskParams("QUICK", orderNum, totalAmt, finalAmt, paidAmt, returnAmt, 0, 0, 0, 0, 0, 0, 0);
                WiFiPrintInvoice prePrintTask = new WiFiPrintInvoice();
                prePrintTask.execute(params1);
            }
        } else if (ConnectType.equalsIgnoreCase("WiFi")) {
            USBPrintTaskParams params1 = new USBPrintTaskParams("QUICK", orderNum, totalAmt, finalAmt, paidAmt, returnAmt, 0, 0, 0, 0, 0, 0, 0);
            WiFiPrintInvoice prePrintTask = new WiFiPrintInvoice();
            prePrintTask.execute(params1);
        }
    }

    /**
     * UPDATE THE TABLE VIEWS. IF TABLE HAVING ORDER TABLE COLOR SHOULD BE RED, OTHERWISE IT WILL BE GRAY
     */
    private void updateTableUI() {
        if (AndroidApplication.isActivityVisible()) {
            mKOTTableList = mDBHelper.getTables(mAppManager.getClientID(), mAppManager.getOrgID());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTableAdapter.updateTables(mKOTTableList);
                }
            });

        }
    }

    private void printKOTData() {

        List<KOTHeader> kotPrintList = mDBHelper.getKOTHeadersNotPrinted();
        if (kotPrintList.size() != 0) {
            KOTPrintTask task = new KOTPrintTask(POSActivity.this);
            task.execute();
        } else {
            AppConstants.isKOTParsing = false;
            updateTableUI();
        }

    }

    @Override
    protected void onPause() {
        try {
            mTableUpdateHandler.removeCallbacks(mTableUpdateRunnable);
            mPOSNumberHandler.removeCallbacks(mPOSNumberRunnable);
            mNetworkCheckHandler.removeCallbacks(mNetworkCheckRunnable);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        super.onPause();

        //unregister receiver
        unregisterReceivers();

        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {

        try {

            mTableUpdateHandler.removeCallbacks(mTableUpdateRunnable);
            mPOSNumberHandler.removeCallbacks(mPOSNumberRunnable);
            mNetworkCheckHandler.removeCallbacks(mNetworkCheckRunnable);

            if (bluetoothPort != null) {
                bluetoothPort.disconnect();
                if (androidMSR != null)
                    androidMSR.releaseInstance();
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        if ((hThread != null) && (hThread.isAlive())) {
            hThread.interrupt();
            hThread = null;
        }

        unregisterReceiver(mBltStateReceiver);

        super.onDestroy();
    }

    //READ THE MSR CARD DATA and VALIDATE THE LOCAL DATABASE TO WHO IS SWIPED
    private void readMSRData() {
        try {
            if (bluetoothPort == null) {
                AlertView.showAlert("Info", "Connect Printer", mContext);
            } else {
                //for msr card reader implementation
                androidMSR = AndroidMSR.getInstance();
                androidMSR.setHandler(msrHandler);

                if (bluetoothPort.isConnected()) {

                    mOrderFrag.showSnackBar("Swipe the card for unlock the order.");

                    try {
                        if (androidMSR.readMSR(mMSRMode) == LKPrint.LK_STS_MSR_READ) {
                            Log.e(TAG, "It's in msr mode ");
                        } else {
                            //AlertView.showError("Fail to change MSR mode.", mContext);
                            FragmentManager localFragmentManager = getSupportFragmentManager();
                            AlertFragment.newInstance(mContext, "MSR Connection", "Fail to change MSR mode").show(localFragmentManager, "MSR");
                        }
                    } catch (InterruptedException e1) {
                        Log.e(TAG, "msrTestListener " + e1.getMessage());
                    } catch (IOException e2) {
                        Log.e(TAG, "msrTestListener " + e2.getMessage());
                    } catch (NullPointerException e) {
                        Log.e(TAG, "msrTestListener " + e.getMessage());
                    }
                } else {
                    FragmentManager localFragmentManager = getSupportFragmentManager();
                    AlertFragment.newInstance(mContext, "Info", "Connect MSR reader").show(localFragmentManager, "MSR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    // change table service btn image
    private void changeTableServiceBtnImage() {

        if (!mAppManager.getIsRetail()) {
            mTablesView.setVisibility(View.VISIBLE);
            AppConstants.isTableService = true;
        }
        resetProductAdapter();

        mBtnTableService.setBackgroundResource(R.drawable.tab_pressed);
        mBtnTableService.setTextColor(Color.parseColor("#ffffff"));

        mBtnCounterSale.setBackgroundResource(R.drawable.tab);
        mBtnCounterSale.setTextColor(Color.parseColor("#000000"));
    }

    // change countersale btn image
    private void changeCounterSaleBtnImage() {

        mTablesView.setVisibility(View.GONE);
        AppConstants.isTableService = false;

        resetProductAdapter();

        mBtnCounterSale.setBackgroundResource(R.drawable.tab_pressed);
        mBtnCounterSale.setTextColor(Color.parseColor("#ffffff"));

        mBtnTableService.setBackgroundResource(R.drawable.tab);
        mBtnTableService.setTextColor(Color.parseColor("#000000"));
    }

    //Register the BatteryLevelReceiver
    public void registerReceivers() {

        this.mBatteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context paramAnonymousContext, Intent intent) {

                // Get the battery scale
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                // get the battery level
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);


                // Calculate the battery charged percentage
                float percentage = level / (float) scale;
                mBatteryPercent = (int) ((percentage) * 100);


                //check if connected to power
                int status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

                if (status != 0) {
                    if (mBatteryPercent < 20) {
                        showErrorDialog("Battery is going to down!");
                    }
                } else {
                    if (mBatteryPercent < 20) {
                        showErrorDialog("Battery is going to down!");
                    }
                }


            }
        };

        IntentFilter localIntentFilter1 = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        registerReceiver(this.mBatteryLevelReceiver, localIntentFilter1);
    }

    //UnRegister the BatteryLevelReceiver
    public void unregisterReceivers() {
        try {
            unregisterReceiver(this.mBatteryLevelReceiver);
        } catch (Exception localException) {
            localException.printStackTrace();
            //Crashlytics.log("MainActivity - unregisterReceiver");
        }
    }


    /**
     * POST the tableChange update to server. It will affect to all SmartMenu devices
     *
     * @param activeTableId
     * @param targetTableId
     */
    public void tableChange(long activeTableId, long targetTableId) {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {

                mProDlg.setMessage("Change the table...");
                mProDlg.show();

                List<KOTHeader> kotHeaderList = mDBHelper.getKOTNumbersFromKOTHeader(activeTableId);
                JSONObject mJsonObj = mParser.getParams(AppConstants.POST_TABLE_CHANGE);
                JSONArray mKOTObj = mParser.getPOSTPrintedKOT(kotHeaderList);
                mJsonObj.put("KOTNumbers", mKOTObj);
                mJsonObj.put("activeTableId", activeTableId);
                mJsonObj.put("targetTableId", targetTableId);

                Log.i("CHANGE TABLE DATA", mJsonObj.toString());
                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.POST_TABLE_CHANGE);
                thread.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //show network failure dialog or toast
            showErrorDialog("Please Check Your Internet Connection!");
        }
    }

    /**
     * This @OnTokenReceivedListener used for if any token received from server and update the table.
     * If table order visible in cart otherwise this method will not trigger.
     *
     * @param tableId
     * @param invoiceNumber
     */
    @Override
    public void OnTokenReceivedListener(long tableId, long invoiceNumber) {

        if (AppConstants.posID != 0 && invoiceNumber != 0 && invoiceNumber == AppConstants.posID)
            updateKOTtoInvoice(invoiceNumber, tableId);
    }

    /**
     * If Preprint or Order saved in draft status to server then printing the bill.
     * The printing bill process completed this method will be trigger and update the
     * order and lock the order
     *
     * @param orderNum
     */
    @Override
    public void onPrintTaskComplete(long orderNum) {

        if (orderNum != 0) {
            Log.i("DRAFT PRINT", "" + orderNum);

            //printing success and update the poscustomer table isPrinted = Y
            mDBHelper.prePrintOrder(orderNum, "Y");
            AppConstants.isOrderPrinted = true;
            //make lock the order
            mOrderFrag.updateMode(orderNum);

            //mOrderFrag.showSnackBar("Order Printing");
        }
    }

    /**
     * if Preprint the order success but printing machine not connected means,
     * this method will be trigger.
     *
     * @param error
     */
    @Override
    public void onPrintTaskError(String error, long posNumber) {
        Log.i("DRAFT PRINT", error);
        onPrintTaskComplete(posNumber);
    }

    /**
     * OrderStateListener for order posted, failure and change the
     * Business partner of order
     *
     * @param posNumber
     */
    @Override
    public void orderPostedSuccess(long posNumber) {

        updateTableUI();

        mOrderFrag = (OrderListFragment) getSupportFragmentManager().findFragmentByTag(ORDER_LIST_TAG);
        if (mOrderFrag != null && mOrderFrag.isVisible()) {
            //DO STUFF
            if (AppConstants.posID == 0) {

                AppConstants.isOrderPrinted = false;

                mOrderFrag.updateEmptyRow();
                mOrderFrag.updateCustomerName(mAppManager.getCustomerName());
                mOrderFrag.updateMode(AppConstants.posID);

                //get all kot from kotHeader if Printed status = N
                kotHeaderList = mDBHelper.getDataAvailableKOTTables();

                for (int i = 0; i < kotHeaderList.size(); i++) {
                    //update orderAvailable = Y
                    mDBHelper.updateOrderAvailableTable(kotHeaderList.get(i).getTablesId());
                }

                //update UI
                //updateTableUI();

                resetProductAdapter();

            } else {
                POSOrders orders = mDBHelper.getPosHeader(AppConstants.posID);
                if (orders != null)
                    mOrderFrag.updateCustomerName(orders.getCustomerName());
            }
            mOrderFrag.updatePOSNumber();

            mOrderFrag.setCreditLimitDefault();
        }

        //AppConstants.isKOTParsing = false;
        hideSoftKeyboard();
    }

    @Override
    public void orderPostedFailure(long posNumber) {

    }

    @Override
    public void creditCustomerSelected(BPartner bPartner) {

        //check credit limit
        checkCreditLimit(bPartner.getBpId());
    }

    @Override
    public void onPrintingSuccess() {
        updateTableUI();
        postKOTPrintedData();
    }

    public void checkCreditLimit(long bPartnerId) {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            try {
                mProDlg.setMessage("Veryfying credit limit...");
                mProDlg.show();
                JSONObject mJsonObj = mParser.getParams(AppConstants.CHECK_CREDIT_LIMIT);
                mJsonObj.put("businessPartnerId", bPartnerId);
                NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.CHECK_CREDIT_LIMIT);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //show network failure dialog or toast
            showErrorDialog("Please Check Your Internet Connection!");
        }
    }

    @Override
    public void printFromPayment(long posNumber, double totalAmount, double finalAmount, double paidAmount, double returnAmount, double paidCash, double paidAmex, double paidGift, double paidMaster, double paidVisa, double paidOther, double amtEntered) {

        //ConnectType = mSharedPreferences.getString("prefPrintOptions", "USB");
        ConnectType = mAppManager.getPrinterMode();
        if (ConnectType.equalsIgnoreCase("USB")) {
        /*if (device != null) {
            HPRTPrinterHelper.PortClose();
        }*/

            Log.i("POSActivity-LAN PRINTER", "PRINTER CONNECTED");
            if (HPRTPrinterHelper.PortOpen(device) != 0) {
                HPRTPrinter = null;
                Toast.makeText(mContext, "Printer connection error!", Toast.LENGTH_LONG).show();
                updatePrintStatus("COMPLETE", posNumber);
            } else {
                AppLog.e("PRINTING", posNumber + " CALLING ");
                //connectPrinter();
                USBPrintTaskParams params1 = new USBPrintTaskParams("COMPLETE", posNumber, totalAmt, finalAmt, paidAmt, returnAmt, paidCash, paidAmex, paidGift, paidMaster, paidVisa, paidOther, amtEntered);
                WiFiPrintInvoice printTask = new WiFiPrintInvoice();
                printTask.execute(params1);
            }
        } else if (ConnectType.equalsIgnoreCase("WiFi")) {
            USBPrintTaskParams params1 = new USBPrintTaskParams("COMPLETE", posNumber, totalAmt, finalAmt, paidAmt, returnAmt, paidCash, paidAmex, paidGift, paidMaster, paidVisa, paidOther, amtEntered);
            WiFiPrintInvoice printTask = new WiFiPrintInvoice();
            printTask.execute(params1);
        }
    }

    private void updatePrintStatus(String printFrom, long posNumber) {
        if (printFrom.equalsIgnoreCase("QUICK")) {
            onPrintTaskComplete(posNumber);
        } else {
            //mDBHelper.deletePOSTables(posNumber);
            AppConstants.posID = 0;
            OrderStatusListener.getInstance().orderPosted(AppConstants.posID, true);
        }
    }

    public void showCreateSession() {
        if (sessionDialog != null) {
            try {
                if (sessionDialog.getDialog().isShowing()) {
                    //dialog is not showing
                    Bundle bundle = new Bundle();
                    bundle.putString("sessionType", "CREATE");
                    bundle.putString("sessionDesc", "Expired");
                    sessionDialog.setArguments(bundle);
                    sessionDialog.show(fragmentManager, "SESSION");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeActivity() {
        mAppManager.setRemindMe("N");
        finish();
    }

    public void checkPrinterConnection() {
        if (mAppManager.getPrinterMode().equalsIgnoreCase("")) {
            FragmentManager localFragmentManager = getSupportFragmentManager();
            AlertFragment.newInstance(mContext, "Printing Option", "Please select the printer mode from settings").show(localFragmentManager, "PRINTER");
        } else {
            onPrinterConnection(mAppManager.getPrinterMode(), mAppManager.getPrinterIP());
        }
    }

    private String addTotalWhiteSpace(String data) {
        String result = "";
        try {
            result = String.format("%-33s", data);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            AppLog.e("AddTotalWhiteSpace", stacktrace);
        }
        return result;
    }

    private String addQtyWhiteSpace(String data) {
        String result = "";
        try {
            result = String.format("%-5s", data);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            AppLog.e("AddQtyWhiteSpace", stacktrace);
        }
        return result;
    }

    private String addItemNameWhiteSpace(String data) {
        String result = "";
        try {
            result = String.format("%-28s", data);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            AppLog.e("AddItemNameWhiteSpace", stacktrace);
        }
        return result;
    }

    private String addPriceWhiteSpace(String data) {
        String result = "";
        try {
            result = String.format("%8s", data);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            AppLog.e("AddPriceWhiteSpace", stacktrace);
        }
        return result;
    }

    private String printTextString(String printText) {
        String s = printText;
        return s;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            message = "Good! Connected to Internet";
            //Toast.makeText(mContext,message, Toast.LENGTH_LONG).show();
            mMenuNetwork.setIcon(getResources().getDrawable(R.drawable.ic_green));
        } else {
            message = "Sorry! Not connected to internet";
            //showErrorDialog("Please Check Your Internet Connection!");
            mMenuNetwork.setIcon(getResources().getDrawable(R.drawable.ic_red));
        }
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showErrorDialog(String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(POSActivity.this);

                    // set title
                    alertDialogBuilder.setTitle("Warning");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }
        });
    }

    private void checkUpdateAvailable() {
        if (!NetworkUtil.getConnectivityStatusString().equals(AppConstants.NETWORK_FAILURE)) {
            mProDlg.setMessage("Check update available");
            mProDlg.show();
            JSONObject mJsonObj = mParser.getParams(AppConstants.CHECK_UPDATE_AVAILABLE);
            NetworkDataRequestThread thread = new NetworkDataRequestThread(AppConstants.URL, "", mHandler, mJsonObj.toString(), AppConstants.CHECK_UPDATE_AVAILABLE);
            thread.start();
        } else {
            //show network failure dialog or toast
            showErrorDialog("Please Check Your Internet Connection!");
        }
    }

    public void showAppInstallDialog() {
        if (!mAppManager.getRemindMeStatus().equalsIgnoreCase("")) {
            try {
                //show denomination screen
                FragmentManager localFragmentManager = POSActivity.this.getSupportFragmentManager();
                AppUpdateFragment appUpdateFragment = new AppUpdateFragment();
                appUpdateFragment.show(localFragmentManager, "AppUpdateFragment");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class WiFiPrinteConnectionTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            mProDlg.setMessage("Connecting printer please wait...");
            mProDlg.setCancelable(false);
            mProDlg.setCanceledOnTouchOutside(false);
            mProDlg.show();
        }

        protected String doInBackground(Void... urls) {
            String result = "";
            try {

                if (HPRTPrinter != null) {
                    HPRTPrinter.PortClose();
                }
                HPRTPrinter = new HPRTPrinterHelper(thisCon, printerName);

                if (HPRTPrinter.PortOpen("WiFi," + strIPAddress + "," + strPort) != 0)
                    result = "error";
                else {
                    result = "success";
                }

            } catch (Exception e) {
                result = "error";
            }

            return result;
        }

        protected void onPostExecute(String result) {

            mProDlg.dismiss();

            if (result.equalsIgnoreCase("success")) {
                Toast.makeText(thisCon, "WiFi Printer connected successfully", Toast.LENGTH_LONG).show();
                mAppManager.savePrinterData(strMode, strIPAddress);

                if (printerSettingFragment != null)
                    printerSettingFragment.dismissAllowingStateLoss();
            } else {
                mAppManager.savePrinterData(strMode, strIPAddress);
                Toast.makeText(thisCon, "WiFi Printer connection error! Try Again...", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * This msr class will be useful for reading MSR CARD details.
     */
    public class MSRHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = (Bundle) msg.obj;
            if (D)
                Log.d(TAG, "bundle.size() = " + bundle.size());
            if (bundle.size() > 1) {
                // rawData - MSR Data.
                byte[] rawData = bundle.getByteArray("RawData");
                int rawLength = bundle.getInt("RawDataSize");
                String[] track = Common.parsingMSRData(rawData);
                String result = "";
                if (track.length >= 3) {
                    result = track[0];
                }
                Log.i(TAG, "RawDATA == " + new String(rawData));
                Log.i(TAG, "RawDATA Buffer Size == " + rawData.length);
                Log.i(TAG, "RawDATA Size == " + rawLength);

                result = result.replaceAll("[^a-zA-Z0-9]", "");

                if (result != null) {

                    //check authorization code
                    for (int i = 0; i < mAuthrizeIds.size(); i++) {
                        if (mAuthrizeIds.get(i).contains(result)) {

                            AppConstants.authorizedId = mDBHelper.getAuthorizedUserId(Long.parseLong(mAuthrizeIds.get(i)));

                            AppConstants.isOrderPrinted = false;

                            mDBHelper.prePrintOrder(AppConstants.posID, "N");
                            //remove lock
                            mOrderFrag.updateMode(AppConstants.posID);

                            mOrderFrag.showSnackBar("Successfully unlocked the order.");

                            break;
                        }
                    }
                }

                // Normal mode restore command.
                if (androidMSR != null) {
                    try {
                        androidMSR.cancelMSR();
                    } catch (InterruptedException e) {
                        Log.e(TAG, "msrTestListener " + e.getMessage());
                    }
                }
            } else {
                // Fail to read MSR.
                Log.e(TAG, "RawDATA == " + new String(bundle.getByteArray("RawData")));
                FragmentManager localFragmentManager = getSupportFragmentManager();
                AlertFragment.newInstance(mContext, "Info", "Invalid MSR data").show(localFragmentManager, "MSR");
            }
        }
    }

    private class WiFiPrintInvoice extends AsyncTask<USBPrintTaskParams, String, String> {

        /**
         * Variables for print product details
         */
        String printFrom = "COMPLETE";
        String qty;
        String name;
        String arabicName;
        double price;
        double discPrice = 0;
        String discount = "";
        int disc;
        double discVal;
        String result = "";
        long mPosNumber = 0;
        List<String> receiptLines;
        private ProgressDialog mProgress;
        private int complement = 0;

        // Show Progress bar before downloading Music
        @Override
        protected void onPreExecute() {
            mProgress = new ProgressDialog(mContext);
            mProgress.setMessage("Printing bill please wait...");
            mProgress.setCancelable(false);
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();
        }

        // Download Music File from Internet
        @Override
        protected String doInBackground(USBPrintTaskParams... params) {

            try {

                String mCashierName = mAppManager.getUserName();

                printFrom = params[0].printFrom;
                mPosNumber = params[0].posId;
                double mTotalAmount = params[0].mTotalAmt;
                double mFinalAmount = params[0].mFinalAmt;
                double mPaidAmount = params[0].mPaidAmt;
                double mReturnAmount = params[0].mReturnAmt;

                double mPaidCashAmt = params[0].mPaidCashAmt;
                double mPaidAmexAmt = params[0].mPaidAmexAmt;
                double mPaidGiftAmt = params[0].mPaidGiftAmt;
                double mPaidMasterAmt = params[0].mPaidMasterAmt;
                double mPaidVisaAmt = params[0].mPaidVisaAmt;
                double mOtherAmt = params[0].mPaidOtherAmt;
                double mAmountEntered = params[0].mAmountEntered;

                mTotalAmount = mDBHelper.sumOfProductsWithoutDiscount(mPosNumber);
                mFinalAmount = mDBHelper.sumOfProductsTotalPrice(mPosNumber);

                POSPayment payment = mDBHelper.getPaymentDetails(mPosNumber);
                if (payment != null) {
                    mPaidCashAmt = payment.getCash();
                    mPaidAmexAmt = payment.getAmex();
                    mPaidGiftAmt = payment.getGift();
                    mPaidMasterAmt = payment.getMaster();
                    mPaidVisaAmt = payment.getVisa();
                    mOtherAmt = payment.getOther();
                    mReturnAmount = payment.getChange();
                    complement = payment.getIsComplement();
                }

                try {

                    receiptLines = new ArrayList<String>();

                    /**Get the lineItems*/
                    List<POSLineItem> lineItem = mDBHelper.getPOSLineItems(mPosNumber, 0);

                    if (lineItem.size() != 0) {

                        Organization orgDetail = mDBHelper.getOrganizationDetail(mAppManager.getOrgID());

                        if (orgDetail == null)
                            AppLog.e("ORG DETAIL", "ORG NULL");


                        List<Long> kotTableList = mDBHelper.getKOTTableList(mPosNumber);
                        String tableName = "CounterSale";
                        int covers = 0;

                        if (kotTableList.size() == 0) {
                            tableName = "CounterSale";
                        } else if (kotTableList.size() == 1 && kotTableList.get(0) == 0) {
                            tableName = "CounterSale";
                        } else {
                            StringBuilder sb = new StringBuilder("");
                            for (int i = 0; i < kotTableList.size(); i++) {
                                Tables table = mDBHelper.getTableData(mAppManager.getClientID(), mAppManager.getOrgID(), kotTableList.get(i));
                                if (table != null) {
                                    sb.append(table.getTableName() + " ");

                                    List<KOTHeader> kotHeaderList = mDBHelper.getKOTHeaders(kotTableList.get(i), true);
                                    for (int j = 0; j < kotHeaderList.size(); j++) {
                                        KOTHeader kotHeader = kotHeaderList.get(j);
                                        covers = covers + kotHeader.getCoversCount();
                                    }
                                } else {
                                    sb.append("CounterSale");
                                }

                            }
                            tableName = sb.toString();
                        }

                        if (printFrom.equalsIgnoreCase("QUICK"))
                            receiptLines.add(printTextString(alignUtils.alignFormat("PreBill") + "\n"));

                        if (orgDetail != null) {

                            receiptLines.add(printTextString(alignUtils.alignFormat(orgDetail.getOrgName()) + "\n"));
                            receiptLines.add(printTextString(alignUtils.alignFormat(orgDetail.getOrgAddress()) + "\n"));
                            receiptLines.add(printTextString(alignUtils.alignFormat("Tel: " + orgDetail.getOrgPhone()) + "\n"));
                        }

                        receiptLines.add(printTextString(Common.getDate() + "                        " + Common.getTime() + "\n"));
                        receiptLines.add(printTextString(alignUtils.alignFormat(String.valueOf(mPosNumber)) + "\n"));
                        receiptLines.add(printTextString("Cashier: " + mCashierName + "\n"));
                        receiptLines.add(printTextString("------------------------------------------\n"));

                        if (!tableName.equalsIgnoreCase("CounterSale")) {
                            String tableHeader = "Table# " + tableName + " | COVERS# " + covers;
                            receiptLines.add(printTextString(alignUtils.alignFormat(tableHeader) + "\n"));
                            receiptLines.add(printTextString("------------------------------------------\n"));
                        }

                        receiptLines.add(printTextString(addQtyWhiteSpace("Qty") + addItemNameWhiteSpace("Item") + addPriceWhiteSpace("Price") + "\n"));

                        for (int j = 0; j < lineItem.size(); j++) {

                            try {
                                qty = String.valueOf(lineItem.get(j).getPosQty());
                                name = lineItem.get(j).getProductName();
                                //arabicName = lineItem.get(j).getProdArabicName();
                                price = lineItem.get(j).getPosQty() * lineItem.get(j).getStdPrice();
                                discPrice = 0;
                                discount = "";
                                disc = lineItem.get(j).getDiscType();
                                discVal = lineItem.get(j).getDiscValue();

                                if (disc == 0 && discVal != 0) {
                                    discount = discVal + "% discount";
                                    discPrice = (price * discVal / 100);
                                } else if (disc == 1 && discVal != 0) {
                                    discount = discVal + " QR discount";
                                    discPrice = discVal;
                                }

                                //String discount = lineItem.get(i).getDiscValue();
                                if (name.length() > 28) {
                                    //splitstring and print
                                    int len = name.length();
                                    receiptLines.add(printTextString(addQtyWhiteSpace(qty) + addItemNameWhiteSpace(name.substring(0, 28)) + addPriceWhiteSpace(Common.valueFormatter(price)) + "\n"));
                                    receiptLines.add(printTextString(addQtyWhiteSpace(" ") + addItemNameWhiteSpace(name.substring(29, len - 1)) + addPriceWhiteSpace(" ") + "\n"));
                                } else {
                                    receiptLines.add(printTextString(addQtyWhiteSpace(qty) + addItemNameWhiteSpace(name) + addPriceWhiteSpace(Common.valueFormatter(price)) + "\n"));
                                }
                                if (!discount.equalsIgnoreCase(""))
                                    receiptLines.add(printTextString(addQtyWhiteSpace(" ") + addItemNameWhiteSpace(discount) + addPriceWhiteSpace("-" + Common.valueFormatter(discPrice)) + "\n"));


                                List<POSLineItem> extraProductList = mDBHelper.getPOSExtraLineItems(lineItem.get(j).getKotLineId());
                                if (extraProductList.size() != 0) {
                                    for (int k = 0; k < extraProductList.size(); k++) {

                                        qty = String.valueOf(extraProductList.get(k).getPosQty());
                                        name = extraProductList.get(k).getProductName();
                                        //arabicName = extraProductList.get(k).getProdArabicName();
                                        price = extraProductList.get(k).getPosQty() * extraProductList.get(k).getStdPrice();
                                        discPrice = 0;
                                        discount = "";
                                        disc = extraProductList.get(k).getDiscType();
                                        discVal = extraProductList.get(k).getDiscValue();

                                        if (disc == 0 && discVal != 0) {
                                            discount = discVal + "% discount";
                                            discPrice = (price * discVal / 100);
                                        } else if (disc == 1 && discVal != 0) {
                                            discount = discVal + " QR discount";
                                            discPrice = discVal;
                                        }

                                        //String discount = lineItem.get(i).getDiscValue();
                                        if (name.length() > 28) {
                                            //splitstring and print
                                            int len = name.length();
                                            receiptLines.add(printTextString(addQtyWhiteSpace(" ") + addItemNameWhiteSpace("  " + name.substring(0, 28)) + addPriceWhiteSpace(Common.valueFormatter(price)) + "\n"));
                                            receiptLines.add(printTextString(addQtyWhiteSpace(" ") + addItemNameWhiteSpace("  " + name.substring(29, len - 1)) + addPriceWhiteSpace(" ") + "\n"));
                                        } else {
                                            receiptLines.add(printTextString(addQtyWhiteSpace(" ") + addItemNameWhiteSpace("  " + name) + addPriceWhiteSpace(Common.valueFormatter(price)) + "\n"));
                                        }

                                        if (!discount.equalsIgnoreCase("")) {
                                            receiptLines.add(printTextString(addQtyWhiteSpace(" ") + addItemNameWhiteSpace(discount) + addPriceWhiteSpace("-" + Common.valueFormatter(discPrice)) + "\n"));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                StringWriter sw = new StringWriter();
                                e.printStackTrace(new PrintWriter(sw));
                                String stacktrace = sw.toString();

                                AppLog.e("PRINTING LINE ITEM", stacktrace);
                            }
                        }

                        receiptLines.add(printTextString("------------------------------------------\n"));
                        try {
                            if(complement == 0){
                                if (mTotalAmount != mFinalAmount) {
                                    receiptLines.add(printTextString(addTotalWhiteSpace("Total") + "" + addPriceWhiteSpace(Common.valueFormatter(mTotalAmount)) + "\n"));
                                    receiptLines.add(printTextString(addTotalWhiteSpace("Discount QR ") + "" + addPriceWhiteSpace("-" + Common.valueFormatter(mTotalAmount - mFinalAmount)) + "\n"));
                                    receiptLines.add(printTextString(addTotalWhiteSpace("Net Total") + "" + addPriceWhiteSpace(Common.valueFormatter(mFinalAmount)) + "\n"));
                                } else {
                                    receiptLines.add(printTextString(addTotalWhiteSpace("Total") + "" + addPriceWhiteSpace(Common.valueFormatter(mTotalAmount)) + "\n"));
                                }
                            }else {
                                receiptLines.add(printTextString(addTotalWhiteSpace("Total") + "" + addPriceWhiteSpace(Common.valueFormatter(mTotalAmount)) + "\n"));
                                receiptLines.add(printTextString("------------------------------------------\n"));
                                receiptLines.add(printTextString("--------------COMPLEMENTARY ORDER---------------\n"));
                            }


                            receiptLines.add(printTextString("------------------------------------------\n"));

                            if (mPaidCashAmt != 0 && complement == 0) {
                                receiptLines.add(printTextString(addTotalWhiteSpace("Cash") + "" + addPriceWhiteSpace(Common.valueFormatter(mPaidCashAmt)) + "\n"));
                            }
                            if (mPaidAmexAmt != 0) {
                                receiptLines.add(printTextString(addTotalWhiteSpace("Amex Card") + "" + addPriceWhiteSpace(Common.valueFormatter(mPaidAmexAmt)) + "\n"));
                            }

                            if (mPaidGiftAmt != 0) {
                                receiptLines.add(printTextString(addTotalWhiteSpace("Gift Card") + "" + addPriceWhiteSpace(Common.valueFormatter(mPaidGiftAmt)) + "\n"));
                            }

                            if (mPaidMasterAmt != 0) {
                                receiptLines.add(printTextString(addTotalWhiteSpace("Master Card") + "" + addPriceWhiteSpace(Common.valueFormatter(mPaidMasterAmt)) + "\n"));
                            }

                            if (mPaidVisaAmt != 0) {
                                receiptLines.add(printTextString(addTotalWhiteSpace("VISA Card") + "" + addPriceWhiteSpace(Common.valueFormatter(mPaidVisaAmt)) + "\n"));
                            }

                            if (mOtherAmt != 0) {
                                receiptLines.add(printTextString(addTotalWhiteSpace("Other") + "" + addPriceWhiteSpace(Common.valueFormatter(mOtherAmt)) + "\n"));
                            }

                            if (mPaidCashAmt != 0 || mPaidAmexAmt != 0 || mPaidGiftAmt != 0 || mPaidMasterAmt != 0 || mPaidVisaAmt != 0 || mOtherAmt != 0) {
                                receiptLines.add(printTextString("------------------------------------------\n"));
                            }

                            if (mReturnAmount != 0) {
                                receiptLines.add(printTextString(addTotalWhiteSpace("Paid Amount") + "" + addPriceWhiteSpace(Common.valueFormatter(mAmountEntered)) + "\n"));
                                receiptLines.add(printTextString(addTotalWhiteSpace("Change") + "" + addPriceWhiteSpace(Common.valueFormatter(mReturnAmount)) + "\n"));
                                receiptLines.add(printTextString("------------------------------------------\n"));
                            }
                        } catch (Exception e) {
                            // GET THE STACK TRACE AS A STRING
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            String stacktrace = sw.toString();

                            AppLog.e("PRINTING PAYMENT", stacktrace);
                        }

                        receiptLines.add(printTextString(alignUtils.alignFormat("Thank you for choosing " + orgDetail.getOrgName()) + "\n"));
                        receiptLines.add(printTextString(alignUtils.alignFormat(orgDetail.getOrgWebUrl()) + "\n"));
                    }

                    if (receiptLines.size() != 0) {

                        String ipAddress = mAppManager.getPrinterIP();
                        String mode = mAppManager.getPrinterMode();

                        if (mode.equalsIgnoreCase("WiFi") && Common.isIpAddress(ipAddress)) {

                            if (HPRTPrinter != null) {
                                HPRTPrinter.PortClose();
                            }

                            HPRTPrinter = new HPRTPrinterHelper(thisCon, printerName);

                            if (HPRTPrinter.PortOpen("WiFi," + ipAddress + "," + strPort) != 0)
                                result = "printerError";
                            else {

                                PAct.LanguageEncode();
                                PAct.BeforePrintAction();

                                for (int i = 0; i < receiptLines.size(); i++) {
                                    System.out.println(receiptLines.get(i));
                                    HPRTPrinterHelper.PrintText(receiptLines.get(i));
                                }

                                HPRTPrinterHelper.CutPaper(HPRTPrinter.HPRT_PARTIAL_CUT_FEED, 140);

                                PAct.AfterPrintAction();

                                result = "success";
                            }

                        } else if (mode.equalsIgnoreCase("USB")) {
                            PAct.LanguageEncode();
                            PAct.BeforePrintAction();

                            for (int i = 0; i < receiptLines.size(); i++) {
                                System.out.println(receiptLines.get(i));
                                HPRTPrinterHelper.PrintText(receiptLines.get(i));
                            }

                            HPRTPrinterHelper.CutPaper(HPRTPrinter.HPRT_PARTIAL_CUT_FEED, 140);

                            if (printFrom.equalsIgnoreCase("QUICK")) {
                                //no need to open cash-drawer
                            } else {
                                try {
                                    int iRtn = HPRTPrinterHelper.OpenCashdrawer(0);
                                    if (iRtn == 0)
                                        Log.i("POSActivity", "OpenCashDrawer");
                                } catch (Exception e) {
                                    StringWriter sw = new StringWriter();
                                    e.printStackTrace(new PrintWriter(sw));
                                    String stacktrace = sw.toString();
                                    AppLog.e("CASHDRAWER", stacktrace);
                                    Log.d("HPRTSDKSample", (new StringBuilder("Activity_Cashdrawer --> onClickOpen1 ")).append(e.getMessage()).toString());
                                }
                            }

                            PAct.AfterPrintAction();
                        } else {
                            result = "printerError";
                        }
                    }
                    result = "success";
                } catch (Exception e) {
                    result = "error";
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String stacktrace = sw.toString();
                    AppLog.e("PRINTING", "FIRST ERROR - >" + stacktrace);
                    Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
                }
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String stacktrace = sw.toString();
                AppLog.e("PRINTING", "SECOND ERROR -> " + stacktrace);
                result = "error";
                e.printStackTrace();
            }
            return result;
        }

        // Once Music File is downloaded
        @Override
        protected void onPostExecute(String results) {
            mProgress.dismiss();
            if (results.equalsIgnoreCase("success")) {
                //This is where you return data back to caller
                if (printFrom.equalsIgnoreCase("QUICK")) {
                    onPrintTaskComplete(mPosNumber);
                } else {
                    //mDBHelper.deletePOSTables(mPosNumber);
                    AppConstants.posID = 0;
                    OrderStatusListener.getInstance().orderPosted(AppConstants.posID, true);
                }
            } else if (results.equalsIgnoreCase("error")) {
                //This is where you return data back to caller
                if (printFrom.equalsIgnoreCase("QUICK")) {
                    onPrintTaskError(results, mPosNumber);
                } else {
                    //mDBHelper.deletePOSTables(mPosNumber);
                    AppConstants.posID = 0;
                    OrderStatusListener.getInstance().orderPosted(AppConstants.posID, true);
                }
            } else if (results.equalsIgnoreCase("printerError")) {
                Toast.makeText(mContext, "Printer not connected...", Toast.LENGTH_LONG).show();

                if (printFrom.equalsIgnoreCase("QUICK")) {
                    onPrintTaskError(results, mPosNumber);
                } else {
                    //mDBHelper.deletePOSTables(mPosNumber);
                    AppConstants.posID = 0;
                    OrderStatusListener.getInstance().orderPosted(AppConstants.posID, true);
                }
            }
        }
    }
}

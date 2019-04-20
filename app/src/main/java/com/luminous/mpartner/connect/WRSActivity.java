package com.luminous.mpartner.connect;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.BaseActivity;
import com.luminous.mpartner.BuildConfig;
import com.luminous.mpartner.R;
import com.luminous.mpartner.activities.CreateTicketActivity;
import com.luminous.mpartner.activities.ExitActivity;
import com.luminous.mpartner.activities.SimpleScannerActivity;
import com.luminous.mpartner.adapters.CityDropdownAdapter;
import com.luminous.mpartner.adapters.DealersDropdownAdapter;
import com.luminous.mpartner.adapters.StateDropdownAdapter;
import com.luminous.mpartner.adapters.ViewPagerAdapter;
import com.luminous.mpartner.connect.City;
import com.luminous.mpartner.connect.Dealer;
import com.luminous.mpartner.connect.State;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.MSerWRDistListByDlrEntity;
import com.luminous.mpartner.network.entities.MSerWRDlrListByDistEntity;
import com.luminous.mpartner.network.entities.Product;
import com.luminous.mpartner.network.entities.Scheme;
import com.luminous.mpartner.network.entities.SerWRGetCityNameEntity;
import com.luminous.mpartner.network.entities.SerWRStateListEntity;
import com.luminous.mpartner.network.entities.SubmitResponse;
import com.luminous.mpartner.network.entities.TicketList;
import com.luminous.mpartner.utilities.AppConstants;
import com.luminous.mpartner.utilities.CameraDialogMenu;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.FilePath;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

//import com.luminous.mpartner.ui.fragments.EntryFragment;

public class WRSActivity extends BaseActivity implements View.OnClickListener {

    //Firebase Analytics
//    private FirebaseAnalytics mFirebaseAnalytics;

    private final int REQUEST_CAMERA_USE = 200;
    private static final int PICK_FILE_REQUEST = 1;
    private static final int PICK_FILE_REQUEST_Gallery = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private String TAG;
    private Context context;

    //Check for version
    String currentVersion, latestVersion;
    String attachment = ServerConfig.BY_DEFALT;
    Dialog dialog;
    private String selectedFilePath = "";
    String attach_type;
    private List<Product> mLabel;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    LinearLayout llButtons;
    int but_id;
    public String categoryId[], productName[], levelOneId, cat_name = "", selected_tab = "";
    TextView title;
    //EntryAdapter adapter = null;
    ViewPagerAdapter adapter = null;
    ScrollView entry, schemeStatus;

    //Scheme Status
    private Spinner spSchemes;
    private ArrayList<Scheme> mSchemes;
    public String mSelectedScheme = "";
    String Mtype;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    //Entry
    // private View vRootView;
    private EditText etSerialNumber, etName, etPhone, etLandline, etAddress, uploadPic;
    //private static EntryFragment instance;
    private Spinner spDealers, spState, spCity;
    private LinearLayout llForm;

    //Assist Part by Anusha 26/06/2018 TASK#4009
    private RelativeLayout llAssist;
    private LinearLayout llTicList;
    TextView tvMonth, textviewUploadLabel;
    Date currentDate;
    private Calendar mMonth;
    private android.app.DatePickerDialog DatePickerDialog;
    String date;
    private SimpleDateFormat dateFormatter1, outputFormat;

    private android.app.DatePickerDialog startDatePickerDialog;
    private Calendar mStartDate, mCurrentDate;
    private TextView tvSalesDate;
    private SimpleDateFormat dateFormatter;
    private Button btSubmit;
    private ArrayList<Dealer> dealerList;
    private ArrayList<City> cityList;
    private ArrayList<State> stateList;
    private TextView tvDealerAddress;
    String addressFormatter = "<font color=\"black\"><b>%s<br><br>Address: </b></font><font color=\"grey\">%s</font>";
    private TextView tvDealer;

    Typeface tfRegular;
    public List<Product> mWRSLabel;
    String isNew = "";
    private ImageView imgScanner;
    private boolean updateSerialNoManual;

    String pagename;
    String fileName = ServerConfig.BY_DEFALT;

    ArrayList<TicketList> mTicketList;
    private SharedPrefsManager sharedPrefsManager;
    private RetrofitInterface apiInterface;
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.luminous.mpartner.R.layout.activity_wrs);

        context = WRSActivity.this;
        sharedPrefsManager = new SharedPrefsManager(this);
        userId = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);

        // Obtain the FirebaseAnalytics instance.
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        //Screenviews
        Bundle params = new Bundle();
        params.putString("screen_name", "com.luminous.mpartner.ui.activities.WRSActivity");
//        mFirebaseAnalytics.logEvent("WRSActivity", params);
        /*Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "com.luminous.mpartner.ui.activities.WRSActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);*/

//        tfRegular = FontProvider.getInstance().tfRegular;

        //Check current version
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;

        //Check Play Store Version
//        VersionChecker versionChecker = new VersionChecker();
//        latestVersion = null;
//        try {
//            latestVersion = versionChecker.execute().get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        //Compare Current Version with Playstore Version
        if (latestVersion != null) {
            //if (!currentVersion.equalsIgnoreCase(latestVersion)) {
            if (Float.parseFloat(currentVersion) < Float.parseFloat(latestVersion) && !currentVersion.equalsIgnoreCase(latestVersion)) {
                if (!isFinishing()) { //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                    showUpdateDialog();
                }
            }
        }

        //Anusha 15/03/2018 TASK#3299
        Intent in = getIntent();
        isNew = in.getStringExtra("New");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstWRS", false)) {
            // <---- run your one time code here
            getWRSLabel();
            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstWRS", true);
            editor.commit();
            /*setupSearchFunctionality();*/
        } else {

            //mLabel = (ArrayList<Product>) getIntent().getSerializableExtra("labels");
            SharedPreferences mPrefs = getSharedPreferences("Product_List", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = mPrefs.getString("wrs", "");
            Type type = new TypeToken<ArrayList<Product>>() {
            }.getType();
            mLabel = gson.fromJson(json, type);

            createButton();
        }

        //TODO Garvit remove it
        createButton();
        cameraPermissionVia();

    }

    public void createButton() {

        mLabel = new ArrayList<>();

        Product product1 = new Product("1", "Entry");
        Product product2 = new Product("2", "Report");
        Product product3 = new Product("3", "Assist");

        mLabel.add(product1);
        mLabel.add(product2);
        mLabel.add(product3);

        entry = (ScrollView) findViewById(R.id.entry);
        schemeStatus = (ScrollView) findViewById(R.id.schemeStatus);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Assist Part by Anusha 26/06/2018 TASK#4009
        llAssist = (RelativeLayout) findViewById(R.id.llAssist);
        llTicList = (LinearLayout) findViewById(R.id.llTicList);

        /*setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);*/

        prepareToolbar();

        llButtons = (LinearLayout) findViewById(R.id.llButtons);
        llButtons.removeAllViews();
        int i = 1;
        TextView button1 = null;
        if (mLabel != null && !mLabel.isEmpty()) {  //Check if arraylist is empty by Anusha 22/08/2018 TASK#4436
            categoryId = new String[mLabel.size()];
            productName = new String[mLabel.size()];

            //Handle exception by Anusha 22/08/2018 TASK#4436
            try {
                for (Product product : mLabel)
                //  for (int i = 0; i < mProducts.size(); i++)
                {
                    button1 = new TextView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    params.weight = 0.33f; //Assist Part by Anusha 26/06/2018 TASK#4009
                    //params.weight = 0.5f;
                    button1.setLayoutParams(params);
                    button1.setText(product.Name);
                    button1.setGravity(Gravity.CENTER);
                    button1.setTextColor(ContextCompat.getColor(this, R.color.tab_selected));
//                    button1.setTypeface(FontProvider.getInstance().tfBold);
                    //  button1.setPaintFlags(button1.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                    button1.setTag(i);
                    categoryId[i - 1] = product.Id;
                    productName[i - 1] = product.Name;
                    i = i + 1;
                    llButtons.addView(button1);
                    //llButtons.getChildAt(0).setAlpha(0.5f);
                    ((TextView) llButtons.getChildAt(0)).
                            setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    llButtons.getChildAt(0).setEnabled(false);
//                    title.setText(productName[0]);
            /*if (Categoryid.equalsIgnoreCase("")) {
                Categoryid = categoryId[0];
                ProductName = productName[0];
            }*/

                    button1.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            but_id = Integer.parseInt(v.getTag().toString().trim());
                            int n = llButtons.getChildCount();
                            // if(but_id == 1){
                            for (int i = 0; i < llButtons.getChildCount(); i++) {
                                if (but_id == i + 1) {
                                    //  v.setAlpha(0.5f);
                                    v.setEnabled(false);
                                    ((TextView) llButtons.getChildAt(i)).
                                            setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                    if (productName[i].equalsIgnoreCase("Entry")) {
//                                        title.setText(productName[i]);
                                        tabLayout.setVisibility(View.GONE);
                                        viewPager.setVisibility(View.GONE);
                                        entry.setVisibility(View.VISIBLE);
                                        schemeStatus.setVisibility(View.GONE);
                                        llAssist.setVisibility(View.GONE);
                                        viewPager.removeAllViews();
                                        viewPager.setAdapter(null);
                                        tabLayout.removeAllTabs();
                                        //Page count by Anusha 21/06/2018 TASK#3967
                                        pagename = "Connect Entry";
                                        getPageVisit(pagename);
                                        executeEntry();
                                    } else if (productName[i].equalsIgnoreCase("Scheme Status")) {
//                                        title.setText(productName[i]);
                                        tabLayout.setVisibility(View.GONE);
                                        viewPager.setVisibility(View.GONE);
                                        entry.setVisibility(View.GONE);
                                        schemeStatus.setVisibility(View.VISIBLE);
                                        llAssist.setVisibility(View.GONE);
                                        viewPager.removeAllViews();
                                        viewPager.setAdapter(null);
                                        tabLayout.removeAllTabs();
                                        executeSchemeStatus();
                                        //Assist Part by Anusha 26/06/2018 TASK#4009
                                    } else if (productName[i].equalsIgnoreCase("Assist")) {
//                                        title.setText(productName[i]);
                                        tabLayout.setVisibility(View.GONE);
                                        viewPager.setVisibility(View.GONE);
                                        entry.setVisibility(View.GONE);
                                        schemeStatus.setVisibility(View.GONE);
                                        llAssist.setVisibility(View.VISIBLE);
                                        viewPager.removeAllViews();
                                        viewPager.setAdapter(null);
                                        tabLayout.removeAllTabs();
                                        executeAssist();
                                    } else {
                                        tabLayout.setVisibility(View.VISIBLE);
                                        viewPager.setVisibility(View.VISIBLE);
                                        entry.setVisibility(View.GONE);
                                        schemeStatus.setVisibility(View.GONE);
                                        llAssist.setVisibility(View.GONE);
//                                        title.setText(productName[i]);
                                        //Page count by Anusha 21/06/2018 TASK#3967
                                        pagename = "Connect Report";
                                        getPageVisit(pagename);
                                        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                                        setupViewPager(viewPager);
                                        tabLayout.setupWithViewPager(viewPager);
                                        changeTabsFont();
                                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                            @Override
                                            public void onTabSelected(TabLayout.Tab tab) {
                                                viewPager.setCurrentItem(tab.getPosition());
                                            }

                                            @Override
                                            public void onTabUnselected(TabLayout.Tab tab) {

                                            }

                                            @Override
                                            public void onTabReselected(TabLayout.Tab tab) {

                                            }
                                        });

                                    }
                                } else {
                                    llButtons.getChildAt(i).setEnabled(true);
                                }
                            }

                            for (int j = 0; j < llButtons.getChildCount(); j++) {
                                if (but_id != j + 1) {
                                    if (llButtons.getChildAt(j) instanceof TextView)
                                        ((TextView) llButtons.getChildAt(j)).
                                                setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tab_selected));
                                }
                            }


                        }

                    });

                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        llButtons.post(new Runnable() {
            @Override
            public void run() {
                int linearLayoutWidth = llButtons.getWidth();

                DisplayMetrics metrics = new DisplayMetrics();
                WRSActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int deviceWidth = metrics.widthPixels;

                if (linearLayoutWidth > deviceWidth) {
                    HorizontalScrollView scroll = new HorizontalScrollView(WRSActivity.this);
                    scroll.addView(llButtons);
                }
            }
        });

        if (productName != null) {
            //Handle exception by Anusha 22/08/2018 TASK#4436
            try {
                for (int j = 0; j < productName.length; j++) {
                    if (productName[j].equalsIgnoreCase("Entry")) {
//                        title.setText(productName[j]);
                        tabLayout.setVisibility(View.GONE);
                        viewPager.setVisibility(View.GONE);
                        entry.setVisibility(View.VISIBLE);
                        schemeStatus.setVisibility(View.GONE);
                        llAssist.setVisibility(View.GONE);
                        viewPager.removeAllViews();
                        viewPager.setAdapter(null);
                        tabLayout.removeAllTabs();
                        //Page count by Anusha 21/06/2018 TASK#3967
                        pagename = "Connect Entry";
                        getPageVisit(pagename);
                        executeEntry();
                        break;
                    } else if (productName[j].equalsIgnoreCase("Scheme Status")) {
//                        title.setText(productName[j]);
                        tabLayout.setVisibility(View.GONE);
                        viewPager.setVisibility(View.GONE);
                        entry.setVisibility(View.GONE);
                        schemeStatus.setVisibility(View.VISIBLE);
                        llAssist.setVisibility(View.GONE);
                        viewPager.removeAllViews();
                        viewPager.setAdapter(null);
                        tabLayout.removeAllTabs();
                        executeSchemeStatus();
                        break;
                        //Assist Part by Anusha 26/06/2018 TASK#4009
                    } else if (productName[j].equalsIgnoreCase("Assist")) { //Fix bug by Anusha 24/08/2018 TASK#4436
//                        title.setText(productName[j]);
                        tabLayout.setVisibility(View.GONE);
                        viewPager.setVisibility(View.GONE);
                        entry.setVisibility(View.GONE);
                        schemeStatus.setVisibility(View.GONE);
                        llAssist.setVisibility(View.VISIBLE);
                        viewPager.removeAllViews();
                        viewPager.setAdapter(null);
                        tabLayout.removeAllTabs();
                        break;
                    } else {
                        tabLayout.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.VISIBLE);
                        entry.setVisibility(View.GONE);
                        schemeStatus.setVisibility(View.GONE);
                        llAssist.setVisibility(View.GONE);
//                        title.setText(productName[j]);
                        viewPager.removeAllViews();
                        viewPager.setAdapter(null);
                        tabLayout.removeAllTabs();
                        setupViewPager(viewPager);
                        tabLayout.setupWithViewPager(viewPager);
                        changeTabsFont();
                        break;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //tabLayout.setupWithViewPager(viewPager);

        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.colorAccent));
        drawable.setSize(2, 1);
        linearLayout.setDividerPadding(0);
        linearLayout.setDividerDrawable(drawable);

    }

    private void prepareToolbar() {

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        title = (TextView) toolbar.findViewById(R.id.tv_title);
        //title.setText("WRS");
        /*title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_wrs, 0, 0, 0);
        title.setCompoundDrawablePadding(10);*/
//        title.setTypeface(FontProvider.getInstance().tfRegular);
//        ImageView ivHome = (ImageView) toolbar.findViewById(R.id.toolbar_report_iv_home);

//        ivHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent in = new Intent(getApplicationContext(), DashboardActivity.class);
//                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(in);
//                finish();
//            }
//        });
    }

    //Page count by Anusha 21/06/2018 TASK#3967
    private void getPageVisit(String pagename) {
//        UserController userController = new UserController(this, "getcount");
//        userController.getPageVisits(pagename);
    }

    public void getcount(Object object) {
    }

    private void getWRSLabel() {

        if (!CommonUtility.isNetworkAvailable(context))
            return;

        showProgressDialog();
        String url = ServerConfig.newUrl(ServerConfig.GetWRSLabelsURL(), context, WRSActivity.class.getSimpleName());

        apiInterface.getWRSLabel(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        dismissProgressDialog();

                        try {
                            String data = response.string();
                            JSONArray jsonArray = new JSONArray(data);
                            mWRSLabel = new Gson().fromJson(data, new TypeToken<List<Product>>() {
                            }.getType());

                            if (mWRSLabel == null) {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("firstWRS", false);
                                editor.commit();
                            } else {
                                Type type = new TypeToken<ArrayList<Product>>() {
                                }.getType();
                                SharedPreferences mPrefs = getSharedPreferences("Product_List", MODE_PRIVATE);
                                SharedPreferences.Editor editor = mPrefs.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(mWRSLabel, type);
                                editor.putString("wrs", json);
                                editor.commit();

                                mLabel = mWRSLabel;
                                createButton();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                    }
                });

    }


    /***************************************************Entry*************************************************/

    public void executeEntry() {
        etSerialNumber = (EditText) findViewById(R.id.fragment_entry_et_srno);
        imgScanner = (ImageView) findViewById(R.id.imgScanner);
        llForm = (LinearLayout) findViewById(R.id.fragment_entry_ll_form);
        spDealers = (Spinner) findViewById(R.id.fragment_entry_sp_dealer);
        tvSalesDate = (TextView) findViewById(R.id.fragment_entry_tv_sale_date);
        tvDealer = (TextView) findViewById(R.id.fragment_entry_tv_dealer);
        tvDealerAddress = (TextView) findViewById(R.id.fragment_entry_tv_dealer_address);
        etName = (EditText) findViewById(R.id.fragment_entry_et_name);
        //Accept only Alphabets by Anusha 29/03/2018 TASK#3299
        etName.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z ]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });
        etPhone = (EditText) findViewById(R.id.fragment_entry_et_phone);
        etLandline = (EditText) findViewById(R.id.fragment_entry_et_landline);
        etAddress = (EditText) findViewById(R.id.fragment_entry_et_address);
        spState = (Spinner) findViewById(R.id.fragment_entry_sp_state);
        spCity = (Spinner) findViewById(R.id.fragment_entry_sp_city);
        uploadPic = (EditText) findViewById(R.id.uploadPic);
        textviewUploadLabel = (TextView) findViewById(R.id.UploadLabel);

        btSubmit = (Button) findViewById(R.id.fragment_entry_bt_submit);

        etSerialNumber.setTypeface(tfRegular);
        tvSalesDate.setTypeface(tfRegular);
        tvDealer.setTypeface(tfRegular);
        tvDealerAddress.setTypeface(tfRegular);
        etName.setTypeface(tfRegular);
        etPhone.setTypeface(tfRegular);
        etLandline.setTypeface(tfRegular);
        etAddress.setTypeface(tfRegular);
        btSubmit.setTypeface(tfRegular);
        uploadPic.setTypeface(tfRegular);
        uploadPic.setOnClickListener(this);

        mCurrentDate = Calendar.getInstance();
        mStartDate = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
        // Initialize Date Picker


        startDatePickerDialog = new DatePickerDialog(this,
                new android.app.DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mStartDate.set(year, monthOfYear, dayOfMonth);
                        tvSalesDate.setText(dateFormatter.format(mStartDate
                                .getTime()));
                    }

                }, mCurrentDate.get(Calendar.YEAR),
                mCurrentDate.get(Calendar.MONTH),
                mCurrentDate.get(Calendar.DAY_OF_MONTH));
        //startDatePickerDialog.getDatePicker().setMaxDate(new Date().getTime());

        //Anusha 15/03/2018 TASK#3299
        if (isNew.equalsIgnoreCase("No")) {
            try {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //c.setTime(sdf.parse(AppSharedPrefrences.getInstance(this).getWRSDateOld()));
                if (sharedPrefsManager.getString(SharedPreferenceKeys.KEY_WRS_MAX) != null) {
                    c.setTime(sdf.parse(sharedPrefsManager.getString(SharedPreferenceKeys.KEY_WRS_MAX))); //Max and Min Date Issue resolved by Anusha 02/04/2018 TASK#3299
                    //c.set(2018, 2, 31);//Year,Month -1,Day
                    startDatePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (isNew.equalsIgnoreCase("Yes")) {
            try {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //c.setTime(sdf.parse(AppSharedPrefrences.getInstance(this).getWRSDateNew()));
                if (sharedPrefsManager.getString(SharedPreferenceKeys.KEY_WRS_MIN) != null) {
                    c.setTime(sdf.parse(sharedPrefsManager.getString(SharedPreferenceKeys.KEY_WRS_MIN))); //Max and Min Date Issue resolved by Anusha 02/04/2018 TASK#3299
                    //c.set(2018, 3, 1);//Year,Month -1,Day
                    startDatePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

                    //To set max date
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date strDate = null;
                    try {
                        //strDate = format.parse(AppSharedPrefrences.getInstance(this).getWRSDateNew());
                        strDate = format.parse(sharedPrefsManager.getString(SharedPreferenceKeys.KEY_WRS_MIN));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (new Date().after(strDate))
                        startDatePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //startDatePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        }

        etSerialNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    getSerialNumberValidation();

//                    textviewUploadLabel.setVisibility(View.VISIBLE);
//                    uploadPic.setVisibility(View.VISIBLE);

                    cameraPermissionVia();
                    return true;
                }
                return false;
            }
        });

        tvSalesDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEntry();
            }
        });

//        uploadPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//            }
//        });


        if (sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE).equalsIgnoreCase(AppConstants.USER_DISTRIBUTOR)) {
            tvDealer.setText("Dealer");
            //Disable Dealer dropdown for Distributor login by Anusha on 04/08/2018 TASK#4310
            spDealers.setEnabled(false);
            //getDealers();
        } else {
            tvDealer.setText("Distributor");
            getDistributers();
        }

        getStates();
    }


    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

    }

    /*******************************************uploadImage*************************************/


    public void camera_Permission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Toast.makeText(getApplicationContext(),"Camera permission",Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        } else if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(getApplicationContext(),"External storage permission",Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


        } else {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case com.luminous.mpartner.R.id.uploadPic:
                //***********Camera Dialogmenu by shashank***********
                CameraDialogMenu dialogMenu = new CameraDialogMenu(this);
                dialogMenu.setListener(new CameraDialogMenu.OnDialogMenuListener() {
                    @Override
                    public void onPDFPress() {
                        // Intent intent = new Intent();


                        camera_Permission();

                    }

                    @Override
                    public void onGalleryPress() {
                        gallery();
                    }
                });
                break;

        }


    }


    private void gallery() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(intent, PICK_FILE_REQUEST_Gallery);
        }
    }


//*********Files pic from gallery and camera Request by shashank*********

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST_Gallery) {

                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(getApplicationContext(),"External storage permission",Toast.LENGTH_SHORT).show();

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


                }

                uploadPic.setText(fileName);

                Uri selectedFileUri = data.getData();
                if (selectedFileUri == null) {
                    return;
                }

                if (selectedFileUri != null) {
                    selectedFilePath = FilePath.getPath(this, selectedFileUri);
//                    uploadFileile(selectedFilePath);

                }


            }

            if (requestCode == PICK_FILE_REQUEST) {


                if (mCurrentPhotoPath.equalsIgnoreCase("")) {
                    //no data present
                    return;
                }

                uploadPic.setText(mCurrentPhotoPath);


                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {


                    Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    Log.e("imageBitmap", "" + imageBitmap);


                    if (imageBitmap == null) {
                        return;
                    }


                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //*********Check if bitmap is null by shashank*********
                    String image_str = "";
                    if (imageBitmap != null) {
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream); //compress to which format you want.
                        byte[] byte_arr = stream.toByteArray();
//                        image_str = org.kobjects.base64.Base64.encode(byte_arr);//TODO GARVIT
                        Log.e("image_str---", image_str + "___________" + imageBitmap.getAllocationByteCount());
                        attachment = image_str;
                    }


                }
            } else if (requestCode == AppConstants.ZBAR_SCANNER_REQUEST) { //Scanner by Anusha 27/06/2018 TASK#4009
                try {
                        /*EntryFragment.getInstance().updateSerialNumber(data
                                .getStringExtra(AppConstants.SCAN_RESULT));*/
                    updateSerialNumber(data.getStringExtra(AppConstants.SCAN_RESULT));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    //*********android upload file to server by shashank*********

/*
    public void uploadFile(final String selectedFilePath) {


        File selectedFile = Compressor.getDefault(this).compressToFile(new File(selectedFilePath));

        //File compressedImageFile = Compressor.getDefault(this).compressToFile(selectedFile);
        //String str = FileUtils.readFileToString(selectedFile);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            //dialog1.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uploadPic.setText("Upload Pic" + selectedFilePath);
                }
            });
        } else {
            try {


                byte[] encodedBytes = Base64.encodeBase64(loadFileAsBytesArray(selectedFilePath));
                attachment = new String(encodedBytes, "UTF-8"); // for UTF-8 encoding
                uploadPic.setText(selectedFilePath);

//                byte[] originalBytes = encodedBytes;
//
//                Deflater deflater = new Deflater();
//                deflater.setInput(originalBytes);
//                deflater.finish();
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                byte[] buf = new byte[8192];
//                while (!deflater.finished()) {
//                    int byteCount = deflater.deflate(buf);
//                    baos.write(buf, 0, byteCount);
//                }
//                deflater.end();
//
//                byte[] compressedBytes = baos.toByteArray();

                //dialog1.dismiss();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        UserController userController = new UserController(WRSActivity.this, "createNewTicket");
//                        //userController.saveTicket("attachment." + attach_type, attachment, etSerial.getText().toString(), etDesc.getText().toString());
//                    }
//                });

            } catch (Exception e) {
                e.printStackTrace();
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        //Make Length Long for Toast by Anusha 01/08/2018 TASK#4307
                        Toast.makeText(WRSActivity.this, "Cannot Read/Write File!", Toast.LENGTH_LONG).show();
                    }
                };
                //dialog1.dismiss();
            }
        }

        // Toast.makeText(getApplicationContext(),"Compressed Image String Format"+attachment,Toast.LENGTH_SHORT).show();

    }
*/

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {//L7I216E1034302
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, AUTHORITY, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PICK_FILE_REQUEST);
            }
        }
    }

    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider";


    /**
     * This method loads a file from file system and returns the byte array of the content.
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static byte[] loadFileAsBytesArray(String fileName) throws Exception {

        File file = new File(fileName);
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;

    }


    public void executeSchemeStatus() {
        spSchemes = (Spinner) findViewById(R.id.fragment_reports_sp_schemes);
        getSchemes();
    }

    public void getSchemes() {
//        UserController userController = new UserController(this, "updateSchemes2");
//        userController.getSchemes();
    }

    public void updateSchemes2(Object object) {
//        mSchemes = ((UserController) object).mSchemeList;
//        if (mSchemes != null && !mSchemes.isEmpty()) {
//            spSchemes.setAdapter(new SchemesDropdownAdapter(this, mSchemes));
//            spSchemes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    mSelectedScheme = position > 0 ? mSchemes.get(position - 1).schemeName : "";
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//        }
    }


    private void getDealers() {


        if (!CommonUtility.isNetworkAvailable(context))
            return;

//        showProgressDialog();
        String url = ServerConfig.mSerWRDlrListByDist(userId);

        apiInterface.mSerWRDlrListByDist(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        dismissProgressDialog();

                        try {
                            String data = response.string();
                            JSONArray jsonArray = new JSONArray(data);

                            dealerList = new Gson().fromJson(data, new TypeToken<List<MSerWRDlrListByDistEntity>>() {
                            }.getType());

                            if (dealerList != null && !dealerList.isEmpty()) {
//                                spDealers.setAdapter(new DealersDropdownAdapter(context, dealerList));
                                spDealers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position > 0) {
//                                            UserController urlserController = new UserController(WRSActivity.this, "updateDealerAddress");
//                                            userController.getDealerAddress(dealerList.get(position - 1).getDealerCode());
                                        } else {
                                            tvDealerAddress.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                    }
                });

    }

    private void getDistributers() {


        if (!CommonUtility.isNetworkAvailable(context))
            return;

//        showProgressDialog();
        String url = ServerConfig.mSerWRDistListByDlr(userId);

        apiInterface.mSerWRDistListByDlr(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        dismissProgressDialog();

                        try {
                            String data = response.string();
                            JSONArray jsonArray = new JSONArray(data);

                            dealerList = new Gson().fromJson(data, new TypeToken<List<MSerWRDistListByDlrEntity>>() {
                            }.getType());


                            if (dealerList != null && !dealerList.isEmpty()) {
//                                spDealers.setAdapter(new DealersDropdownAdapter(context, dealerList));
                                spDealers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position > 0) {
//                                            UserController userController = new UserController(WRSActivity.this, "updateDealerAddress");
//                                            userController.getDealerAddress(dealerList.get(position - 1).getDealerCode());
                                        } else {
                                            tvDealerAddress.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                    }
                });

    }

    private void getStates() {

        if (!CommonUtility.isNetworkAvailable(context))
            return;

//        showProgressDialog();
        String url = ServerConfig.SerWRStateList();

        apiInterface.SerWRStateList(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        dismissProgressDialog();

                        try {
                            String data = response.string();
                            JSONArray jsonArray = new JSONArray(data);

                            stateList = new Gson().fromJson(data, new TypeToken<List<SerWRStateListEntity>>() {
                            }.getType());

                            if (stateList != null && !stateList.isEmpty()) {
//                                spState.setAdapter(new StateDropdownAdapter(context, stateList));
                            }
                            spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (position > 0) {
                                        getCities(stateList.get(position - 1).getStateId());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                    }
                });
    }

    private void getCities(String stateid) {

        if (!CommonUtility.isNetworkAvailable(context))
            return;

//        showProgressDialog();
        String url = ServerConfig.SerWRGetCityName(stateid);

        apiInterface.SerWRGetCityName(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        dismissProgressDialog();

                        try {
                            String data = response.string();
                            JSONArray jsonArray = new JSONArray(data);

                            cityList = new Gson().fromJson(data, new TypeToken<List<SerWRGetCityNameEntity>>() {
                            }.getType());
                            if (cityList != null) {
//                                spCity.setAdapter(new CityDropdownAdapter(context, cityList));
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                    }
                });

    }


    private void getSerialNumberValidation() {
        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        if (!TextUtils.isEmpty(etSerialNumber.getText().toString())) {
            updateSerialNoManual = true;

            String url = ServerConfig.newUrl(ServerConfig.mSerWRSrNoExistanceUpdate(etSerialNumber.getText().toString()), context, WRSActivity.class.getSimpleName());
            apiInterface.mSerWRSrNoExistanceUpdate(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(ResponseBody response) {
                            dismissProgressDialog();

                            try {
                                String data = response.string();
                                CommonUtility.printLog("okHttp", data);
//                                JSONArray jsonArray = new JSONArray(data);
//                                mWRSLabel = new Gson().fromJson(data, new TypeToken<List<Product>>() {
//                                }.getType());

                                if (mWRSLabel == null) {
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("firstWRS", false);
                                    editor.commit();
                                } else {
                                    Type type = new TypeToken<ArrayList<Product>>() {
                                    }.getType();
                                    SharedPreferences mPrefs = getSharedPreferences("Product_List", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = mPrefs.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(mWRSLabel, type);
                                    editor.putString("wrs", json);
                                    editor.commit();

                                    mLabel = mWRSLabel;
                                    createButton();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onError(Throwable e) {
                            dismissProgressDialog();
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                            CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                        }
                    });

        } else
            Toast.makeText(getApplicationContext(), "Please enter Serial Number", Toast.LENGTH_LONG).show();
    }

    //*********Getting direct serial number through scanner by shashank**********
    private void getSerialNumberValidationByDirectScanner() {
//        if (!TextUtils.isEmpty(etSerialNumber.getText().toString())) {
//            updateSerialNoManual = false;
//            UserController userController = new UserController(WRSActivity.this, "updateSerialNumberValidation");
//            userController.getSerialNumberValidation(etSerialNumber.getText().toString());
//        } else
//            Toast.makeText(getApplicationContext(), "Please enter Serial Number", Toast.LENGTH_LONG).show();
    }


    //*********Putting tting serial number   by shashank**********

    public void updateSerialNumberValidation(Object object) {
//        boolean isSerialNumberValid = ((UserController) object).isSerialValid;
//        boolean disableSerialNoInput = ((UserController) object).disableSerialNoInput;
//        boolean showUploadImageOption = ((UserController) object).showUploadImageOption;

//        llForm.setVisibility(isSerialNumberValid ? View.VISIBLE : View.VISIBLE);
//        if (!isSerialNumberValid) {
//            Global.showToast("" + ((UserController) object).mMessage);
//            llForm.setVisibility(View.GONE);
//        } else {
//            Global.showToast("" + ((UserController) object).mMessage);
//        }

//        if (showUploadImageOption) {
//            uploadPic.setVisibility(View.VISIBLE);
//            textviewUploadLabel.setVisibility(View.VISIBLE);
//            attachment = UrlConstants.BY_DEFALT;
//        } else {
//            attachment = "";
//            if (!updateSerialNoManual) {
//                uploadPic.setVisibility(View.GONE);
//                textviewUploadLabel.setVisibility(View.GONE);
//            } else {
//                uploadPic.setVisibility(View.VISIBLE);
//                textviewUploadLabel.setVisibility(View.VISIBLE);
//            }
//
//            textviewUploadLabel.setVisibility(View.GONE);
//    }
//
//        if(disableSerialNoInput)
//
//    {
//        etSerialNumber.setEnabled(false);
//        etSerialNumber.setClickable(false);
//        imgScanner.setClickable(false);
//        imgScanner.setEnabled(false);
//    } else
//
//    {
//        etSerialNumber.setEnabled(true);
//        etSerialNumber.setClickable(true);
//        imgScanner.setClickable(true);
//        imgScanner.setEnabled(true);
//    }

    }

    //*********Getting direct serial number through scanner by shashank**********
    public void directScannerSerialNumberValidation(Object object) {
//        boolean isSerialNumberValid = ((UserController) object).isSerialValid;
//        llForm.setVisibility(isSerialNumberValid ? View.VISIBLE : View.VISIBLE);
    }


    public void updateDealerByDist(Object object) {
//        dealerList = ((UserController) object).mDealersList;
//        if (dealerList != null && !dealerList.isEmpty()) {
//            spDealers.setAdapter(new DealersDropdownAdapter(this, dealerList));
//            spDealers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    if (position > 0) {
//                        UserController userController = new UserController(WRSActivity.this, "updateDealerAddress");
//                        userController.getDealerAddress(dealerList.get(position - 1).getDealerCode());
//                    } else {
//                        tvDealerAddress.setVisibility(View.GONE);
//                    }
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//        }
    }

    public void updateDistByDealer(Object object) {
//        dealerList = ((UserController) object).mDealersList;
//        if (dealerList != null && !dealerList.isEmpty()) {
//            spDealers.setAdapter(new DealersDropdownAdapter(this, dealerList));
//            spDealers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    if (position > 0) {
//                        UserController userController = new UserController(WRSActivity.this, "updateDealerAddress");
//                        userController.getDealerAddress(dealerList.get(position - 1).getDealerCode());
//                    } else {
//                        tvDealerAddress.setVisibility(View.GONE);
//                    }
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//        }
    }

    public void updateDealerAddress(Object object) {
//        DealerAddress mDealerAddress = ((UserController) object).mDealerAddress;
//        if (mDealerAddress != null) {
//            tvDealerAddress.setText(Html.fromHtml(String.format(addressFormatter,
//                    mDealerAddress.getDealerName(),
//                    mDealerAddress.getDealerAddress())));
//            tvDealerAddress.setVisibility(View.VISIBLE);
//        }
    }

    public void updateStates(Object object) {
//        stateList = ((UserController) object).mStateList;
//        if (stateList != null && !stateList.isEmpty()) {
//            spState.setAdapter(new StateDropdownAdapter(this, stateList));
//        }
//        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position > 0) {
//                    getCities(stateList.get(position - 1).getStateId());
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
    }

    public void updateCities(Object object) {
//        cityList = ((UserController) object).mCityList;
//        if (cityList != null) {
//            spCity.setAdapter(new CityDropdownAdapter(this, cityList));
//        }
    }

    public void saveEntry() {
        //Disable Dealer dropdown for Distributor login by Anusha on 04/08/2018 TASK#4310 updated 06/09/2018
        //  if (!AppSharedPrefrences.getInstance(this).getUserType().equalsIgnoreCase(AppConstants.USER_DISTRIBUTER)) {
        if (spDealers.isEnabled() && spDealers.getSelectedItemPosition() == 0) {
            if (!sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE).equalsIgnoreCase(AppConstants.USER_DISTRIBUTOR)) {
                //  Global.showToast("Please select a Dealer");
                // } else {
                Toast.makeText(WRSActivity.this, "Please select a Distributor", Toast.LENGTH_SHORT).show();
            }
            // }
        } else if (tvSalesDate.getText().toString().equalsIgnoreCase("Sale Date")) {
            Toast.makeText(WRSActivity.this, "Please select a sale date", Toast.LENGTH_SHORT).show();

        } else if (etName.getText().toString().trim().length() == 0) {
            Toast.makeText(WRSActivity.this, "Please enter customer name", Toast.LENGTH_SHORT).show();

            //Minimum Name length by Anusha 29/03/2018 TASK#3299
        } else if (etName.getText().toString().length() < 3) {
            Toast.makeText(WRSActivity.this, "Customer Name invaild", Toast.LENGTH_SHORT).show();

        } else if (etPhone.getText().toString().trim().length() == 0) {
            Toast.makeText(WRSActivity.this, "Please enter customer phone", Toast.LENGTH_SHORT).show();

        } else if (etPhone.getText().toString().length() < 10) {
            Toast.makeText(WRSActivity.this, "Mobile Number invaild", Toast.LENGTH_SHORT).show();

        } else if (etPhone.getText().toString().startsWith("0")) {
            Toast.makeText(WRSActivity.this, "Mobile Number invaild", Toast.LENGTH_SHORT).show();

        } else if (etPhone.getText().toString().startsWith("1")) {
            Toast.makeText(WRSActivity.this, "Mobile Number invaild", Toast.LENGTH_SHORT).show();

        } else if (etPhone.getText().toString().startsWith("2")) {
            Toast.makeText(WRSActivity.this, "Mobile Number invaild", Toast.LENGTH_SHORT).show();

        } else if (etPhone.getText().toString().startsWith("3")) {
            Toast.makeText(WRSActivity.this, "Mobile Number invaild", Toast.LENGTH_SHORT).show();

        } else if (etPhone.getText().toString().startsWith("4")) {
            Toast.makeText(WRSActivity.this, "Mobile Number invaild", Toast.LENGTH_SHORT).show();

        } else if (etPhone.getText().toString().startsWith("5")) {
            Toast.makeText(WRSActivity.this, "Mobile Number invaild", Toast.LENGTH_SHORT).show();

        } else if (etPhone.getText().toString().equalsIgnoreCase("0000000000")) {
            Toast.makeText(WRSActivity.this, "Mobile Number invaild", Toast.LENGTH_SHORT).show();

            //Remove landline mandatory check by Anusha 28/03/2018 TASK#3299
        /*} else if (isNew.equalsIgnoreCase("Yes") && etLandline.getText().toString().trim().length() == 0){    //Anusha 15/03/2018 TASK#3378
            Global.showToast("Please enter customer landline");*/
        } else if (etAddress.getText().toString().trim().length() == 0) {
            Toast.makeText(WRSActivity.this, "Please enter customer address", Toast.LENGTH_SHORT).show();


            //Minimum Address length by Anusha 29/03/2018 TASK#3299 updated on 24/07/2018
        } else if (etAddress.getText().toString().length() < 5) {
            Toast.makeText(WRSActivity.this, "Customer Address invaild", Toast.LENGTH_SHORT).show();


        } else if ((etAddress.getText().toString().matches("[0-9+$,!-#$%'()*-.:;+]*"))) {
            Toast.makeText(WRSActivity.this, "Customer Address invaild", Toast.LENGTH_SHORT).show();


        }
//        else if ((etAddress.getText().toString().matches("^[^<>{}\\\"/|;:.,~!?@#$%^=&*\\\\]\\\\\\\\()\\\\[¿§«»ω⊙¤°℃℉€¥£¢¡®©_+]*$"))) {
//            Global.showToast("Customer Address should  be   numbers");
//
//        }
//        else if ((!etAddress.getText().toString().matches("^.*[a-zA-Z].*$"))) {
//            Global.showToast("Customer Address should  be contain letters");

        //}
        else if (spState.getSelectedItemPosition() == 0) {
            Toast.makeText(WRSActivity.this, "Please select a State", Toast.LENGTH_SHORT).show();
        } else if (spCity.getSelectedItemPosition() == 0) {
            Toast.makeText(WRSActivity.this, "Please select a City", Toast.LENGTH_SHORT).show();

        } else {

            //TODO API CALL GARVIT

            String distCode = "";
            String dlrCode = "";

            if (sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE).equalsIgnoreCase(AppConstants.USER_DISTRIBUTOR)) {
                distCode = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);
                //Disable Dealer dropdown for Distributor login by Anusha on 04/08/2018 TASK#4310
                dlrCode = "";
                //dlrCode = dealerList.get((spDealers.getSelectedItemPosition() - 1)).getDealerCode();
            } else {
                dlrCode = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);
                //Handle exception by Anusha 22/08/2018 TASK#4436
                if (dealerList != null && !dealerList.isEmpty()) {
                    try {
                        distCode = dealerList.get((spDealers.getSelectedItemPosition() - 1)).getDealerCode();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }

            Mtype = etSerialNumber.getText().toString();
            if (Mtype != null && !Mtype.isEmpty()) {
                Mtype = "yes";
            } else {
                Mtype = "no";
            }

            if (attachment.equalsIgnoreCase("")) {
                Mtype = "no";
            }
            //uploadFile(selectedFilePath);

            if (!CommonUtility.isNetworkAvailable(context))
                return;

            showProgressDialog();

            String url = ServerConfig.mSerWRSaveEntryUpdatedSchemeImgpath(etSerialNumber.getText().toString(), distCode, dlrCode,
                    tvSalesDate.getText().toString(), etName.getText().toString(),
                    etPhone.getText().toString(), etLandline.getText().toString(),
                    stateList.get(spState.getSelectedItemPosition() - 1).getStateName(),
                    cityList.get(spCity.getSelectedItemPosition() - 1).getCityName(),
                    etAddress.getText().toString(), sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID), Mtype, attachment);
//            String url = ServerConfig.mSerWRSaveEntryUpdatedSchemeImgpath();

            apiInterface.mSerWRSaveEntryUpdatedSchemeImgpath(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SubmitResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(SubmitResponse submitResponse) {
                            dismissProgressDialog();
                            if (submitResponse.getStatus().equalsIgnoreCase("200")) {

                                Toast.makeText(getApplicationContext(), "" + submitResponse.getMessage(), Toast.LENGTH_LONG).show();
                                refreshScreen();

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dismissProgressDialog();
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                            CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                        }
                    });


        }
    }

    public void updateEntryStatus(Object object) {
        //Increase toast message time by Anusha 21/06/2018 TASK#3967
//        Toast.makeText(getApplicationContext(), "" + ((UserController) object).mMessage, Toast.LENGTH_LONG).show();
        //Global.showToast("" + ((UserController) object).mMessage);
        refreshScreen();
    }

    private void refreshScreen() {
        etSerialNumber.setText("");
        llForm.setVisibility(View.GONE);
        uploadPic.setVisibility(View.GONE);
        textviewUploadLabel.setVisibility(View.GONE);
        spDealers.setSelection(0);
        tvSalesDate.setText("Sale Date");
        etName.setText("");
        uploadPic.setText("");
        etPhone.setText("");
        etLandline.setText("");
        etAddress.setText("");
        spState.setSelection(0);
        spCity.setAdapter(null);

        etSerialNumber.setEnabled(true);
        etSerialNumber.setClickable(true);
        imgScanner.setClickable(true);
        imgScanner.setEnabled(true);

        attachment = ServerConfig.BY_DEFALT;
    }

    public void updateSerialNumber(String serialNumber) {
        etSerialNumber.setText("" + serialNumber);
        getSerialNumberValidationByDirectScanner();
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new WRSStatusFragment(), "Connect+ Status"); //Renamed by Anusha 4/06/2018 TASK#3783
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update");
        builder.setMessage("A New Update is Available");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id=com.luminous.mpartner")));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExitActivity.exitApplication(getApplicationContext());
            }
        });

        builder.setCancelable(false);
        dialog = builder.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void goBack(View v) {
        finish();
    }

    /*public void getSchemes() {
        UserController userController = noti UserController(this, "updateSchemes");
        userController.getSchemes();
    }

    public void updateSchemes(Object object) {
        //ReportsFragment.getInstance().updateSchemes(object);
        WRSPayOffActivity.getInstance().updateSchemes(object);
    }*/

    public void openScanner(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_USE);

            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            if (isCameraAvailable()) {
                Intent intent = new Intent(this, SimpleScannerActivity.class);
                startActivityForResult(intent, AppConstants.ZBAR_SCANNER_REQUEST);
            } else {
                //Make Length Long for Toast by Anusha 01/08/2018 TASK#4307
                Toast.makeText(this, "Rear Facing Camera Unavailable",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_USE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Permission Granted");
                } else {
                    Log.e(TAG, "Permission Denied");
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


//    public void updateReports(Object object) {
//        WRSPayOffFragment.getInstance().updateReports(object);
//    }
//
//    public void updateSchemes(Object object) {
//        WRSPayOffFragment.getInstance().updateSchemes(object);
//    }

    public void updateSchemes1(Object object) {
//        WRSStatusFragmentMain.getInstance().updateSchemes1(object);
    }

    //Download PDF by Anusha 28/06/2018 TASK#4009
    public void getPDF(Object object) {
//        WRSStatusFragmentMain.getInstance().getPDF(object);
    }

    //Add new report by Anusha 1/06/2018 TASK#3783
    public void getReport(Object object) {
//        WRSStatusFragmentMain.getInstance().getReport(object);
    }

    //Add new report by Anusha 4/06/2018 TASK#3783
    public void getPoint(Object object) {
//        WRSStatusFragmentMain.getInstance().getPoint(object);
    }

    //Comment out code for old reports by Anusha 31/05/2018 TASK#3783
    /*public void updateSerialNumberValidation1(Object object)
    {
        WRSStatusFragmentMain.getInstance().updateSerialNumberValidation1(object);
    }*/

    private void changeTabsFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
//                    ((TextView) tabViewChild).setTypeface(FontProvider.getInstance().tfBold);
                }
            }
        }
    }

    //Assist Part by Anusha 26/06/2018 TASK#4009
    public void createNew(View view) {
        Intent in = new Intent(this, CreateTicketActivity.class);
        startActivity(in);
    }

    //Assist Part by Anusha 26/06/2018 TASK#4009
    private void executeAssist() {
        tvMonth = (TextView) findViewById(R.id.tvmonth);
        Date current = Calendar.getInstance().getTime();
        SimpleDateFormat Dateformat = new SimpleDateFormat("yyyy-MM-dd");
        date = Dateformat.format(current);
        //date = date.substring(0, 10);
        dateFormatter = new SimpleDateFormat("MM/yyyy", Locale.US);
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        outputFormat = new SimpleDateFormat("MM/yyyy");
        try {
            Date date1 = inputFormat.parse(date);
            String outputDateStr = outputFormat.format(date1);
            currentDate = outputFormat.parse(outputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mCurrentDate = Calendar.getInstance();
        mCurrentDate.setTime(currentDate);
        mMonth = Calendar.getInstance();
        mMonth.setTime(currentDate);
        //String outputDateStr = outputFormat.format(date1);


        tvMonth.setText(dateFormatter.format(mMonth
                .getTime()));


        String date = tvMonth.getText().toString();
        String month = date.substring(0, 2);
        String year = date.substring(3);
        getTicketList(month, year);

        Date maxDate = new Date();
        maxDate.setMonth(currentDate.getMonth() + 3);

        // Min date Validator
        Date minDate = new Date();
        minDate.setMonth(currentDate.getMonth() - 3);

        DatePickerDialog = createDialogWithoutDateField(new android.app.DatePickerDialog.OnDateSetListener() {

                                                            @Override
                                                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                                mMonth.set(year, monthOfYear, dayOfMonth);
                                                                tvMonth.setText(dateFormatter.format(mMonth
                                                                        .getTime()));
                                                                getTicketList(String.valueOf(monthOfYear + 1), String.valueOf(year));
                                                            }
                                                        }, mCurrentDate.get(Calendar.YEAR),
                mCurrentDate.get(Calendar.MONTH),
                mCurrentDate.get(Calendar.DAY_OF_MONTH));

        DatePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        DatePickerDialog.getDatePicker().setMinDate(minDate.getTime());
    }

    //Assist Part by Anusha 27/06/2018 TASK#4009
    private void getTicketList(String month, String year) {
//        UserController userController = new UserController(WRSActivity.this, "getticketlist");
//        userController.getTicketList(month, year);
    }

    public void getticketlist(Object object) {
//        mTicketList = ((UserController) object).mTicketList;
//        if (mTicketList != null) {
//            llTicList.removeAllViews();
//            for (final TicketList list : mTicketList) {
//                LayoutInflater inflater = null;
//                inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View convertView = inflater.inflate(R.layout.item_ticket, null);
//                LinearLayout llTicket = (LinearLayout) convertView.findViewById(R.id.llTicket);
//                TextView tvdate = (TextView) convertView.findViewById(R.id.tvdate);
//                TextView tvserial = (TextView) convertView.findViewById(R.id.tvserial);
//                TextView tvstatus = (TextView) convertView.findViewById(R.id.tvstatus);
//
//                String created_on = list.CreatedOn.substring(0, 10);
//                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
//                String outputDateStr = "";
//                try {
//                    Date date1 = inputFormat.parse(created_on);
//                    outputDateStr = outputFormat.format(date1);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                tvdate.setText(outputDateStr);
//                tvserial.setText(list.SerialNo);
//                tvstatus.setText(list.Status);
//
//                llTicList.addView(convertView);
//
//                llTicket.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent in = new Intent(WRSActivity.this, UpdateTicketActivity.class);
//                        in.putExtra("Tic_id", list.Tic_Id);  //by Anusha 06/07/2018 TASK#4009
//                        in.putExtra("Status", list.Status);  //by Anusha 13/07/2018 TASK#4009
//                        startActivity(in);
//                    }
//                });
//            }
//        }
    }

    private android.app.DatePickerDialog createDialogWithoutDateField(android.app.DatePickerDialog.OnDateSetListener dsl, int year, int month, int date) {
        android.app.DatePickerDialog dpd = new DatePickerDialog(this, dsl, year, month, date);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return dpd;
    }

    public void selectDate(View v) {
        tvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.show();
            }
        });

    }


    /*****************camera permission changes by shashank on ankur command***************/


    public void cameraPermissionVia() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Toast.makeText(getApplicationContext(),"Camera permission",Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        } else if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(getApplicationContext(),"External storage permission",Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

}
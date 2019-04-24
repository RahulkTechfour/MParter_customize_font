package com.luminous.mpartner.home_page.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.luminous.mpartner.BaseActivity;
import com.luminous.mpartner.MyApplication;
import com.luminous.mpartner.R;
import com.luminous.mpartner.activities.LoginActivity;
import com.luminous.mpartner.activities.ProfileActivity;
import com.luminous.mpartner.activities.TermsAndConditionsActivity;
import com.luminous.mpartner.adapters.AutoSuggestAdapter;
import com.luminous.mpartner.adapters.NavDrawerRecyclerViewAdapter;
import com.luminous.mpartner.connect.ConnectFragment;
import com.luminous.mpartner.databinding.ActivityHomePageBinding;
import com.luminous.mpartner.dealer.CreateDealerFirmAddressDeatailsFragment;
import com.luminous.mpartner.dealer.DealerMainFragment;
import com.luminous.mpartner.dealer.ViewDealerListFragment;
import com.luminous.mpartner.events.ChangeLanguageEvent;
import com.luminous.mpartner.events.ContactUsUriEvent;
import com.luminous.mpartner.events.HomePageVisibleEvent;
import com.luminous.mpartner.events.OpenLuck7Event;
import com.luminous.mpartner.fragments.CameraPermissionFragment;
import com.luminous.mpartner.fragments.ContactUsFragment;
import com.luminous.mpartner.fragments.EscalationMatrixFragment;
import com.luminous.mpartner.fragments.FAQFragment;
import com.luminous.mpartner.fragments.LoadCalculatorFragment;
import com.luminous.mpartner.fragments.NotificationFragment;
import com.luminous.mpartner.gallery.GalleryTabFragment;
import com.luminous.mpartner.home_page.dialogs.DialogLanguage;
import com.luminous.mpartner.home_page.fragments.HomePageFragment;
import com.luminous.mpartner.lucky7.Lucky7Activity;
import com.luminous.mpartner.lucky7.Lucky7DealerActivity;
import com.luminous.mpartner.media.MediaFragment;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.CustomerPermissionEntity;
import com.luminous.mpartner.network.entities.PermissionDatum;
import com.luminous.mpartner.network.entities.ProductSearch;
import com.luminous.mpartner.network.entities.ProfileEntity;
import com.luminous.mpartner.network.entities.SearchResponseEntity;
import com.luminous.mpartner.pricelist.PriceListTabFragment;
import com.luminous.mpartner.product_service.ProductServiceFragment;
import com.luminous.mpartner.reports.ReportFragment;
import com.luminous.mpartner.sales.CreateSalesOrderFragment;
import com.luminous.mpartner.sales.SalesMainFragment;
import com.luminous.mpartner.sales.SalesViewOrderFragment;
import com.luminous.mpartner.schemes.SchemeFragment;
import com.luminous.mpartner.survey.SurveyFragment;
import com.luminous.mpartner.survey.SurveyQuestionFragment;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.OnRecyclerViewItemClickListener;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomePageActivity extends BaseActivity implements View.OnClickListener, CameraPermissionFragment.CameraFilePermissionCallback, OnRecyclerViewItemClickListener {

    private ActivityHomePageBinding binding;

    private CameraPermissionFragment cameraPermissionFragment;
    private Fragment fragment;
    private String tag;
    private RetrofitInterface apiInterface;
    private List<PermissionDatum> permissionDatumList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private SharedPrefsManager sharedPrefsManager;
    private ProfileEntity profileEntity;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;
    private List<ProductSearch> searchResponse;
    String fragmentTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_page);
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);
        sharedPrefsManager = new SharedPrefsManager(this);
        profileEntity = (ProfileEntity) sharedPrefsManager.getObject(SharedPreferenceKeys.USER_DATA, ProfileEntity.class);
        initToolbar();
        setDrawer();
        initViews();
        setBottomNavigation();

        getCustomerPermissionData();

        handleDeepLinkClick(getIntent());

        //add headless fragment for camera permission check
        cameraPermissionFragment = (CameraPermissionFragment) getSupportFragmentManager().findFragmentByTag("CAMERA");
        if (cameraPermissionFragment == null) {
            cameraPermissionFragment = CameraPermissionFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(cameraPermissionFragment, "CAMERA")
                    .commit();
        }


        MyApplication.getApplication()
                .bus()
                .toObservable()
                .subscribe(object -> {
                    if (object instanceof ChangeLanguageEvent) {
                        getCustomerPermissionData();
                    } else if (object instanceof OpenLuck7Event) {
                        openLucky7Activity();
                    } else if (object instanceof HomePageVisibleEvent) {
                        supportInvalidateOptionsMenu();
                    }
                });

        if (CommonUtility.isNewVersionAvailable(this))
            CommonUtility.showUpdateDialog(this);

    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    private void initViews() {

        binding.autoCompleteEditText.setVisibility(View.GONE);
        autoSuggestAdapter = new AutoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        binding.autoCompleteEditText.setThreshold(1);
        binding.autoCompleteEditText.setAdapter(autoSuggestAdapter);
        binding.autoCompleteEditText.setOnItemClickListener(
                (parent, view, position, id) -> {

                    hideKeyboard();

                    Fragment fragment = ProductServiceFragment.newInstance(searchResponse.get(position).getId(), "");
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "CatalogFragment").commit();

//                    MyApplication.getApplication()
//                            .bus()
//                            .send(new ProductCatalogClickEvent(searchResponse.get(position).getId()));
                });

        binding.autoCompleteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(msg -> {
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(binding.autoCompleteEditText.getText()) && binding.autoCompleteEditText.getText().length() > 0) {
                    callSearchProductApi(binding.autoCompleteEditText.getText().toString());
                }
            }
            return false;
        });


    }

    private void callSearchProductApi(String toString) {

        if (!CommonUtility.isNetworkAvailable(this)) {
            return;
        }

        String url = ServerConfig.newUrl(String.format(ServerConfig.getSearchProductUrl(), toString), this, HomePageFragment.class.getSimpleName());
        apiInterface.callSearchProductApi(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchResponseEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(SearchResponseEntity searchProductEntity) {
                        if (searchProductEntity.getStatus().equalsIgnoreCase("200")) {

                            searchResponse = searchProductEntity.getProductSearch();
                            List<String> productNameList = new ArrayList<>();
                            for (ProductSearch productSearch : searchResponse) {
                                productNameList.add(productSearch.getProductioncatalogName());
                            }
                            autoSuggestAdapter.setData(productNameList);
                            autoSuggestAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("HomeData", "onComplete");
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.isPermissionDialogShow) {
            cameraPermissionFragment.checkCameraAndFilePermissions();
            MyApplication.isPermissionDialogShow = true;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleDeepLinkClick(intent);
    }

    private void handleDeepLinkClick(Intent intent) {
        fragment = HomePageFragment.newInstance("", "");
        tag = intent.getStringExtra("tag");

        if (TextUtils.isEmpty(tag))
            tag = "HomePageFragment";
        binding.autoCompleteEditText.setVisibility(View.GONE);
        switch (tag) {
            case "contact-us":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = ContactUsFragment.newInstance("", "");
                break;
            case "Media":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = MediaFragment.newInstance("", "");
                break;
            case "KnowMore":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = ContactUsFragment.newInstance("", "");
                break;
            case "Catalog":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = ProductServiceFragment.newInstance(-1, "");
                break;
            case "PriceList":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = PriceListTabFragment.newInstance("", "");
                break;
            case "Scheme":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = SchemeFragment.newInstance("", "");
                break;
            case "Connect":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = ConnectFragment.newInstance("", "");
                break;
            case "Notification":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = NotificationFragment.newInstance("", "");
                break;
            case "LoadCalculator":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = LoadCalculatorFragment.newInstance("", "");
                break;
            case "Gallery":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = GalleryTabFragment.newInstance("", "");
                break;
            case "Lucky7":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = Lucky7Activity.newInstance("", "");
                break;
            case "Lucky7Dealer":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = Lucky7DealerActivity.newInstance("", "");
                break;
            case "ServiceEscalation":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = EscalationMatrixFragment.newInstance("", "");
                break;
            case "Faqs":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = FAQFragment.newInstance("", "");
                break;
            case "Reports":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = ReportFragment.newInstance("", "");
                break;
            case "HomePageFragment":
                binding.autoCompleteEditText.setVisibility(View.GONE);
                fragment = HomePageFragment.newInstance("", "");
                break;
            default:
                fragment = HomePageFragment.newInstance("", "");
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, tag).commit();


//        if (!TextUtils.isEmpty(tag) && tag.equalsIgnoreCase("contact-us")) {
//
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "ContactEntity").commit();
//
//        } else {
//            fragment = HomePageFragment.newInstance("", "");
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "HomePageFragment").commit();
//
//        }
    }

    private void setBottomNavigation() {

        fragmentTag = "HomePageFragment";
        binding.bottomNavigation.getMenu().setGroupCheckable(0, false, true);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                binding.bottomNavigation.getMenu().setGroupCheckable(0, true, true);
                switch (menuItem.getItemId()) {
                    case R.id.nav_catalog:
                        binding.autoCompleteEditText.setVisibility(View.GONE);
                        fragment = ProductServiceFragment.newInstance(-1, "");
                        fragmentTag = "CatalogFragment";
                        break;
                    case R.id.nav_connect:
                        binding.autoCompleteEditText.setVisibility(View.GONE);
                        fragment = ConnectFragment.newInstance("", "");
                        fragmentTag = "ConnectFragment";
                        break;
                    case R.id.nav_pricelist:
                        binding.autoCompleteEditText.setVisibility(View.GONE);
                        fragment = PriceListTabFragment.newInstance("", "");
                        fragmentTag = "PriceListFragment";
                        break;
                    case R.id.nav_scheme:
                        binding.autoCompleteEditText.setVisibility(View.GONE);
                        fragment = SchemeFragment.newInstance("", "");
                        fragmentTag = "SchemeFragment";
                        break;
                    default:
                        fragmentTag = "HomePageFragment";
                        fragment = HomePageFragment.newInstance("", "");
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, fragmentTag).commit();
                return true;
            }
        });

    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbarLayout.toolbar);

        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.toolbarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.toolbarLayout.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uncheckNavigationItem(binding.bottomNavigation);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, HomePageFragment.newInstance("", ""), "HomePageFragment").commit();
            }
        });
    }


    @SuppressLint("RestrictedApi")
    private void uncheckNavigationItem(BottomNavigationView navigation) {

        try {
            for (int i = 0; i < navigation.getMenu().size(); i++) {
                navigation.getMenu().getItem(i).setCheckable(false);
                try {
                    BottomNavigationItemView item = (BottomNavigationItemView) navigation.getMenu().getItem(i);
                    item.setShifting(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }

    private void setDrawer() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbarLayout.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
           /* Drawable drawable = getResources().getDrawable(R.drawable.ic_nav_icon);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(drawable);*/

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_nav);

        }
        // toggle.setDrawerIndicatorEnabled(false); //disable "hamburger to arrow" drawable
        //  toggle.setHomeAsUpIndicator(R.drawable.img_drawer); //set your own

       /* if(getSupportActionBar()!=null)
        {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_navigation_icon);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(drawable);

        }*/


        if (profileEntity != null) {
            binding.navDrawer.tvFirmName.setText(profileEntity.getName());
            binding.navDrawer.tvSapCode.setText(profileEntity.getSapCode());
        }

        binding.navDrawer.tvSeeProfile.setOnClickListener(this);
        binding.navDrawer.tvChangeLanguage.setOnClickListener(this);
//        binding.navDrawer.navCalculator.setOnClickListener(this);
//        binding.navDrawer.navCatalog.setOnClickListener(this);
//        binding.navDrawer.navConnect.setOnClickListener(this);
//        binding.navDrawer.navContactUs.setOnClickListener(this);
//        binding.navDrawer.navContest.setOnClickListener(this);
//        binding.navDrawer.navFaq.setOnClickListener(this);
//        binding.navDrawer.navGallery.setOnClickListener(this);
//        binding.navDrawer.navPricelist.setOnClickListener(this);
//        binding.navDrawer.navProductService.setOnClickListener(this);
//        binding.navDrawer.navScheme.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Fragment fragment = null;

        switch (v.getId()) {
            case R.id.tvSeeProfile:
                binding.drawerLayout.closeDrawers();
                startActivity(new Intent(HomePageActivity.this, ProfileActivity.class));
                break;
            case R.id.tv_change_language:
                binding.drawerLayout.closeDrawers();
                DialogLanguage dialogLanguage = new DialogLanguage(this);
                dialogLanguage.show();
                break;
//            case R.id.nav_calculator:
//                fragment = LoadCalculatorFragment.newInstance("", "");
//                break;
//            case R.id.nav_catalog:
//                break;
//            case R.id.nav_connect:
//                break;
//            case R.id.nav_contact_us:
//                fragment = ContactUsFragment.newInstance("", "");
//                break;
//            case R.id.nav_contest:
//                break;
//            case R.id.nav_faq:
//                fragment = FAQFragment.newInstance("", "");
//                break;
//            case R.id.nav_gallery:
//                fragment = GalleryTabFragment.newInstance("", "");
//                break;
//            case R.id.nav_pricelist:
//                fragment = PriceListTabFragment.newInstance("", "");
//                break;
//            case R.id.nav_product_service:
//                fragment = ProductServiceFragment.newInstance(-1, "");
//                break;
//            case R.id.nav_scheme:
//                fragment = SchemeFragment.newInstance("", "");
//                break;
            default:
                fragment = HomePageFragment.newInstance("", "");
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            binding.drawerLayout.closeDrawer(Gravity.START, true);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            HomePageFragment homePageFragment = (HomePageFragment) getSupportFragmentManager().findFragmentByTag("HomePageFragment");
            ProductServiceFragment productServiceFragment = (ProductServiceFragment) getSupportFragmentManager().findFragmentByTag("ProductServiceFragment");
            SalesViewOrderFragment salesViewOrderFragment = (SalesViewOrderFragment) getSupportFragmentManager().findFragmentByTag("SalesViewOrderFragment");
            CreateSalesOrderFragment createSalesOrderFragment = (CreateSalesOrderFragment) getSupportFragmentManager().findFragmentByTag("CreateSalesOrderFragment");
            CreateDealerFirmAddressDeatailsFragment createDealerFirmAddressDeatailsFragment =
                    (CreateDealerFirmAddressDeatailsFragment) getSupportFragmentManager()
                            .findFragmentByTag("CreateDealerFirmAddressDeatailsFragment");

            ViewDealerListFragment viewDealerListFragment = (ViewDealerListFragment) getSupportFragmentManager()
                    .findFragmentByTag("ViewDealerListFragment");

            SurveyQuestionFragment surveyQuestionFragment = (SurveyQuestionFragment) getSupportFragmentManager()
                    .findFragmentByTag("SurveyQuestionFragment");

            if (homePageFragment != null && homePageFragment.isVisible()) {
                super.onBackPressed();
            } else if (salesViewOrderFragment != null) {

                if (salesViewOrderFragment.isVisible()) {
                    /*Move to Sales Main Fragment*/
                    Fragment sales = SalesMainFragment.newInstance("", "");
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, sales, "SalesMainFragment").commit();
                }
            } else if (createSalesOrderFragment != null) {

                if (createSalesOrderFragment.isVisible()) {
                    /*Move to Sales Main Fragment*/
                    Fragment sales = SalesMainFragment.newInstance("", "");
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, sales, "SalesMainFragment").commit();
                }

            } else if (viewDealerListFragment != null) {

                if (viewDealerListFragment.isVisible()) {
                    Fragment sales = DealerMainFragment.newInstance("", "");
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, sales, "SalesMainFragment").commit();
                }
            } else if (createDealerFirmAddressDeatailsFragment != null) {

                if (createDealerFirmAddressDeatailsFragment.isVisible()) {
                    Fragment sales = DealerMainFragment.newInstance("", "");
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, sales, "SalesMainFragment").commit();
                }


            } else if (surveyQuestionFragment != null) {

                if (surveyQuestionFragment.isVisible()) {
                    Fragment sales = SurveyFragment.newInstance("", "");
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, sales, "SurveyFragment").commit();
                }

            } else if (productServiceFragment != null) {
                if (productServiceFragment.isVisible()) {
                    Fragment fragment = ProductServiceFragment.newInstance(-1, "");
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "HomePageFragment").commit();
                }
            } else {

                binding.bottomNavigation.getMenu().setGroupCheckable(0, false, true);

                HomePageFragment fragment = HomePageFragment.newInstance("", "");
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "HomePageFragment").commit();

                supportInvalidateOptionsMenu();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCameraFilePermissionGranted() {
    }

    @Override
    public void onCameraFilePermissionDenied() {
        Toast.makeText(this, getString(R.string.permission_denied_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        MenuItem itemSearch = menu.findItem(R.id.nav_search);

       /* HomePageFragment homePageFragment = (HomePageFragment) getSupportFragmentManager().findFragmentByTag("HomePageFragment");
        if (homePageFragment != null && homePageFragment.isVisible()) {
            itemSearch.setVisible(true);
        } else {
            itemSearch.setVisible(false);
            binding.autoCompleteEditText.setVisibility(View.GONE);
        }*/

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (f instanceof HomePageFragment) {
            itemSearch.setVisible(true);

        } else {
            itemSearch.setVisible(false);
            binding.autoCompleteEditText.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem itemSearch = menu.findItem(R.id.nav_search);

//        HomePageFragment homePageFragment = (HomePageFragment) getSupportFragmentManager().findFragmentByTag("HomePageFragment");
//        if (homePageFragment != null && homePageFragment.isVisible()) {
//            itemSearch.setVisible(true);
//        } else {
//            itemSearch.setVisible(false);
//            binding.autoCompleteEditText.setVisibility(View.GONE);
//        }

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);
//        if (f instanceof HomePageFragment) {
        itemSearch.setVisible(true);

//        } else {
//            itemSearch.setVisible(false);
//            binding.autoCompleteEditText.setVisibility(View.GONE);
//        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_search:
                binding.autoCompleteEditText.setText("");
                binding.autoCompleteEditText.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_profile:
                startActivity(new Intent(HomePageActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_notification:
                openNotification();
                break;
            case R.id.language:
                DialogLanguage dialogLanguage = new DialogLanguage(this);
                dialogLanguage.show();
                dismissProgressDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openNotification() {
        SurveyFragment fragment = SurveyFragment.newInstance("", "");
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "notification").commit();
        binding.drawerLayout.closeDrawer(Gravity.START, true);
    }

    public void getCustomerPermissionData() {

        if (!CommonUtility.isNetworkAvailable(this)) {
            return;
        }

        showProgressDialog();
        String url = ServerConfig.newUrl(ServerConfig.getCustomerPermissionDataUrl(), this, HomePageActivity.class.getSimpleName() + ".class");

        Log.d("YASH", "getCustomerPermissionData: " + url);

        /*TODO : remove*/
        //url = "http://166.62.100.102/Api/MpartnerApi/Customer_permission_data?user_id=9999998&token=a60957aab979dfb2a6b0218c5c6b386a&app_version=3.9&appversion_code=28&device_id=d039734abac691a1&device_name=ONEPLUS%20A6000&os_type=Android&os_version_code=28&os_version_name=9&ip_address=192.168.0.20&language=EN&screen_name=HomePageActivity.class&network_type=wifi&network_operator=airtel&time_captured=1552505909002&channel=M\n";


        apiInterface.getCustomerPermissionData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CustomerPermissionEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(CustomerPermissionEntity customerPermissionEntity) {
                        dismissProgressDialog();

                        if (customerPermissionEntity.getStatus().equalsIgnoreCase("200") &&
                                customerPermissionEntity.getPermissionData() != null && customerPermissionEntity.getPermissionData().size() > 0) {

                            permissionDatumList = customerPermissionEntity.getPermissionData();
                            initDrawerList();
                        } else {
                            Log.d("YASH", "null response");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(HomePageActivity.this, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });

    }

    private void initDrawerList() {

        NavDrawerRecyclerViewAdapter adapter = new NavDrawerRecyclerViewAdapter(HomePageActivity.this, permissionDatumList, this);
        binding.navDrawer.navDrawerList.setAdapter(adapter);
    }

    private void loadReport() {
        fragment = ReportFragment.newInstance("", "");
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        binding.drawerLayout.closeDrawer(Gravity.START, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @Override
    public void onItemCLicked(View view, int position) {

    }

    @Override
    public void onItemCLicked(View view, int position, String source) {

        String fTag = "";

        switch (source) {
            case "Secondary Sales":
                break;
            case "Luminous Care":
                break;
            case "Media":
                fragment = MediaFragment.newInstance("", "");
                fTag = "MediaFragment";
                break;
            case "Load Calculator":
                fragment = LoadCalculatorFragment.newInstance("", "");
                fTag = "LoadCalculatorFragment";
                break;
            case "Catalog":
                fragment = ProductServiceFragment.newInstance(-1, "");
                fTag = "ProductServiceFragment";
                break;
            case "Connect+":
                fragment = ConnectFragment.newInstance("", "");
                fTag = "ConnectFragment";
                break;
            case "Contact Us":
                fragment = ContactUsFragment.newInstance("", "");
                fTag = "ContactUsFragment";

                break;
            case "Lucky 7":
                fTag = "Lucky7Fragment";
                if (!sharedPrefsManager.getBoolean(SharedPreferenceKeys.IsOTPVerified)) {
                    fragment = TermsAndConditionsActivity.newInstance("", "");
                } else {
                    if (sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE).equalsIgnoreCase("Distributor")) {
                        fragment = Lucky7Activity.newInstance("", "");

                    } else {
                        fragment = Lucky7DealerActivity.newInstance("", "");

                    }
                }
                break;
            case "FAQs":
                fragment = FAQFragment.newInstance("", "");
                fTag = "FAQFragment";
                break;
            case "Gallery":
                fragment = GalleryTabFragment.newInstance("", "");
                fTag = "GalleryTabFragment";
                break;
            case "Price List":
                fragment = PriceListTabFragment.newInstance("", "");
                fTag = "PriceListTabFragment";
                break;
            case "Schemes":
                fragment = SchemeFragment.newInstance("", "");
                fTag = "SchemeFragment";
                break;
            case "Dealer Management":
                //fragment = CreateDealerFragment.newInstance("", "");
                fragment = DealerMainFragment.newInstance("", "");
                fTag = "DealerMainFragment";
                break;
            case "Reports":
                fragment = ReportFragment.newInstance("", "");
                fTag = "ReportFragment";
                break;
            case "Service Escalation":
                fragment = EscalationMatrixFragment.newInstance("", "");
                fTag = "EscalationMatrixFragment";
                break;
            case "Sales":
                fragment = SalesMainFragment.newInstance(CommonUtility.getPastSeventhDate(), CommonUtility.getTodayDate());
                fTag = "SalesMainFragment";
                break;

            default:
                fragment = HomePageFragment.newInstance("", "");
                fTag = "HomePageFragment";

        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, fTag).commit();
            binding.drawerLayout.closeDrawer(Gravity.START, true);
        }
    }

    public void openLucky7Activity() {

        if (!sharedPrefsManager.getBoolean(SharedPreferenceKeys.IsOTPVerified)) {
            fragment = TermsAndConditionsActivity.newInstance("", "");
        } else {
            if (sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE).equalsIgnoreCase("Distributor")) {
                fragment = Lucky7Activity.newInstance("", "");

            } else {
                fragment = Lucky7DealerActivity.newInstance("", "");

            }
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "Lucky7Fragment").commit();
            binding.drawerLayout.closeDrawer(Gravity.START, true);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getApplicationContext(),"External storage permission",Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        ContactUsFragment.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


            }

            Uri selectedFileUri = data.getData();
            if (selectedFileUri == null) {
                return;
            } else {
                MyApplication.getApplication()
                        .bus()
                        .send(new ContactUsUriEvent(selectedFileUri));
            }
        }
    }


}

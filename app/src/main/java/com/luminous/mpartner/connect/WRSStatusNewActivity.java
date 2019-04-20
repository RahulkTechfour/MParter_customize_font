package com.luminous.mpartner.connect;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.BuildConfig;
import com.luminous.mpartner.R;
import com.luminous.mpartner.activities.CreateTicketActivity;
import com.luminous.mpartner.activities.SimpleScannerActivity;
import com.luminous.mpartner.adapters.CityDropdownAdapter;
import com.luminous.mpartner.adapters.DealersDropdownAdapter;
import com.luminous.mpartner.adapters.StateDropdownAdapter;
import com.luminous.mpartner.databinding.FragmentWrsstatusNewBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.ConnectEntryEntity;
import com.luminous.mpartner.network.entities.MSerWRDistListByDlrEntity;
import com.luminous.mpartner.network.entities.Notification.NotificationResponseEntity;
import com.luminous.mpartner.network.entities.SaveAssistEntityResponse;
import com.luminous.mpartner.network.entities.SaveConnectEntryEntity;
import com.luminous.mpartner.network.entities.SaveContactUsSuggestionResponse;
import com.luminous.mpartner.network.entities.SerWRGetCityNameEntity;
import com.luminous.mpartner.network.entities.SerWRStateListEntity;
import com.luminous.mpartner.utilities.AppConstants;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.FilePath;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class WRSStatusNewActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private static final String TAG = "WRSStatusNewActivity";
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_FILE_REQUEST_Gallery = 2;
    private static final int PICK_FILE_REQUEST = 1;
    private final int REQUEST_CAMERA_USE = 200;
    String attachment = "";
    String mCurrentPhotoPath;
    private FragmentWrsstatusNewBinding binding;
    private RetrofitInterface apiInterface;
    private List<SerWRStateListEntity> stateList;
    private List<SerWRGetCityNameEntity> cityList;
    private Context context = WRSStatusNewActivity.this;
    private String selectedState, selectedCity;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<MSerWRDistListByDlrEntity> dealerList;
    private String selectedDealer = "";
    private ProgressDialog progressDialog;
    private String Mtype;
    private boolean isSerialValid = false, disableSerialNoInput = false, showUploadImageOption = false;
    private String selectedFilePath = "";
    private boolean updateSerialNoManual;

    public static byte[] loadFileAsBytesArray(String fileName) throws Exception {

        File file = new File(fileName);
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;

    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_wrsstatus_new);
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        initToolbar();
        initUI();


    }

    private void initUI() {
        if (new SharedPrefsManager(this).getString(SharedPreferenceKeys.USER_TYPE).equalsIgnoreCase(AppConstants.USER_DISTRIBUTOR)) {
            binding.txtDistributor.setText("Distributor");
            binding.spinnerSelectDistributer.setEnabled(false);
        } else {
            binding.txtDistributor.setText("Distributor");
            getDistributers();
        }
        getStates();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        binding.btnSubmit.setOnClickListener(this);
        binding.ivScanBarcode.setOnClickListener(this);
        binding.tvSaleDate.setOnClickListener(this);
        binding.tvUploadPic.setOnClickListener(this);

        binding.edtSerialNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    getSerialNumberValidation();
                    updateSerialNoManual = true;
                    return true;
                }
                return false;
            }
        });
    }

    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void openDatePicker() {

        Calendar today = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, WRSStatusNewActivity.this, today.get(Calendar.YEAR),
                today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void submit() {
        if (validation()) {
            ConnectEntryEntity entryEntity = new ConnectEntryEntity();
            entryEntity.setSerialNo(binding.edtSerialNumber.getText().toString());

            if (selectedDealer != "" && !selectedDealer.isEmpty()) {
                //

                entryEntity.setDlrCode(new SharedPrefsManager(context).getString(SharedPreferenceKeys.USER_ID));
                entryEntity.setDisCode(selectedDealer);
            } else {
                //
                entryEntity.setDlrCode(selectedDealer);
                entryEntity.setDisCode(new SharedPrefsManager(context).getString(SharedPreferenceKeys.USER_ID));
            }

            entryEntity.setSaleDate(binding.tvSaleDate.getText().toString());
            entryEntity.setCustomerName(binding.edtCustomerName.getText().toString());
            entryEntity.setCustomerPhone(binding.edtCustomerPhone.getText().toString());
            entryEntity.setCustomerLandLineNumber(binding.edtCustomerLandline.getText().toString());
            entryEntity.setCustomerState(selectedState);
            entryEntity.setCustomerCity(selectedCity);
            entryEntity.setCustomerAddress(binding.edtCustomerAddress.getText().toString());
            entryEntity.setLogDistyId(new SharedPrefsManager(context).getString(SharedPreferenceKeys.USER_ID));
            entryEntity.setIsmtype(Mtype);
            entryEntity.setImg(attachment);
            entryEntity.setToken("pass@1234");

            if (!CommonUtility.isNetworkAvailable(context)) {
                return;
            }

            showProgressDialog();
            String url = ServerConfig.getWRSaveEntryUpdatedSchemeImgpath();
            apiInterface.getWRSaveEntryUpdatedSchemeImgpath(url, entryEntity)

                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            dismissProgressDialog();
                            ArrayList<SaveAssistEntityResponse> responseEntity = null;
                            try {
                                responseEntity = new Gson().fromJson(responseBody.string(), new TypeToken<ArrayList<SaveAssistEntityResponse>>() {
                                }.getType());

                                if (responseEntity.get(0).getMessage().equalsIgnoreCase("Assist")) {
                                    sendData(entryEntity, responseEntity.get(0).getStatus());
                                } else if (responseEntity.get(0).getStatus().contains("Entry Failed")) {
                                    Toast.makeText(getApplicationContext(), responseEntity.get(0).getStatus(), Toast.LENGTH_LONG).show();
                                } else {
                                    setClearData();
                                    binding.svForm.setVisibility(View.INVISIBLE);
                                    binding.btnSubmit.setVisibility(View.INVISIBLE);
                                    binding.edtSerialNumber.setEnabled(true);
                                    binding.edtSerialNumber.setClickable(true);
                                    binding.ivScanBarcode.setClickable(true);
                                    binding.ivScanBarcode.setEnabled(true);
                                    Toast.makeText(getApplicationContext(), responseEntity.get(0).getStatus(), Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dismissProgressDialog();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }


    }

    private void sendData(ConnectEntryEntity entryEntity, String desc) {
        String appVersionName = "", appVersionCode = "";
        final List<String> versionList = CommonUtility.appVersionDetails(context);
        if (versionList != null && versionList.size() == 2) {
            appVersionName = versionList.get(0);
            appVersionCode = versionList.get(1);
        }
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
        String userId = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);
        String accessToken = sharedPrefsManager.getString(SharedPreferenceKeys.TOKEN);

        SaveConnectEntryEntity saveEntryEntity = new SaveConnectEntryEntity();
        saveEntryEntity.setUser_id(userId);
        saveEntryEntity.setSerialno(binding.edtSerialNumber.getText().toString());

        if (selectedDealer != "" && !selectedDealer.isEmpty()) {
            // dist
            saveEntryEntity.setDlrCode(userId);
            saveEntryEntity.setDiscode(selectedDealer);

        } else {
            //dist
            saveEntryEntity.setDlrCode(selectedDealer);
            saveEntryEntity.setDiscode(userId);
        }


        //  saveEntryEntity.setDlrCode(userId);
        // saveEntryEntity.setDiscode(selectedDealer);
        saveEntryEntity.setSaledate(binding.tvSaleDate.getText().toString());
        saveEntryEntity.setCustomername(binding.edtCustomerName.getText().toString());
        saveEntryEntity.setCustomerphone(binding.edtCustomerPhone.getText().toString());
        saveEntryEntity.setCustomerlandLinenumber(binding.edtCustomerLandline.getText().toString());
        saveEntryEntity.setCustomerstate(selectedState);
        saveEntryEntity.setCustomercity(selectedCity);
        saveEntryEntity.setCustomeraddress(binding.edtCustomerAddress.getText().toString());
        saveEntryEntity.setIsmtype(Mtype);
        saveEntryEntity.setConnectplusimage(attachment);
        saveEntryEntity.setConnectplusimage_name("connectplus.jpg");
        saveEntryEntity.setToken(accessToken);
        saveEntryEntity.setApp_version(appVersionName);
        saveEntryEntity.setAppversion_code(appVersionCode);
        saveEntryEntity.setDevice_id(CommonUtility.getDeviceId(context));
        saveEntryEntity.setDevice_name(CommonUtility.getDeviceName());
        saveEntryEntity.setOs_type("Android");
        saveEntryEntity.setOs_version_name("3.2.0");
        saveEntryEntity.setOs_version_code("12");
        saveEntryEntity.setIp_address(CommonUtility.getIpAddress(context));

        saveEntryEntity.setLanguage(CommonUtility.getAppLanguage(context));
        saveEntryEntity.setScreen_name("WRSStatusNewActivity.class");
        saveEntryEntity.setNetwork_type("4G");
        saveEntryEntity.setNetwork_operator("Airtel");
        saveEntryEntity.setTime_captured(System.currentTimeMillis() + "");
        saveEntryEntity.setChannel("M");

      /*  entryEntity.setToken(new SharedPrefsManager(context).getString(SharedPreferenceKeys.TOKEN));
        entryEntity.setApp_version(appVersionName);
        entryEntity.setAppversion_code(appVersionCode);
        entryEntity.setDevice_id(CommonUtility.getDeviceId(context));
        entryEntity.setDevice_name(CommonUtility.getDeviceName());
        entryEntity.setOs_type("Android");
        entryEntity.setIp_address(CommonUtility.getIpAddress(context));
        entryEntity.setLanguage(CommonUtility.getAppLanguage(context));
        entryEntity.setTime_captured(System.currentTimeMillis() + "");
        entryEntity.setChannel("M");*/

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }
        showProgressDialog();
        String url = ServerConfig.saveConnectData();
        apiInterface.getWRSaveEntryUpdatedSchemeImgpath_(url, saveEntryEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String response = "";
                        dismissProgressDialog();
                        try {
                            response = responseBody.string();


                            SaveContactUsSuggestionResponse entity = new Gson().fromJson(response, new TypeToken<SaveContactUsSuggestionResponse>() {
                            }.getType());

                            if (entity.getStatus().equalsIgnoreCase("200")) {
                                Bundle bundle = new Bundle();
                                bundle.putString("serial_number", binding.edtSerialNumber.getText().toString());
                                bundle.putString("desc", desc);
                                Intent intent = new Intent(WRSStatusNewActivity.this, CreateTicketActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                                setClearData();
                            } else
                                Toast.makeText(getApplicationContext(), entity.getMessage(), Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    // clear data
    private void setClearData() {
        binding.edtSerialNumber.setText("");
        binding.tvSaleDate.setText("Sale Date");
        binding.edtCustomerName.setText("");
        binding.edtCustomerPhone.setText("");
        binding.edtCustomerLandline.setText("");
        binding.edtCustomerAddress.setText("");
        binding.spinnerSelectDistributer.setSelection(0);
        binding.spinnerCustomerCity.setSelection(0);
        binding.spinnerCustomerState.setSelection(0);
        binding.tvUploadPic.setText("Upload Pic");
        binding.tvUploadPic.setVisibility(View.VISIBLE);
        binding.UploadLabel.setVisibility(View.GONE);
        attachment = "";

    }

    private boolean validation() {
        if (binding.spinnerSelectDistributer.isEnabled() && binding.spinnerSelectDistributer.getSelectedItemPosition() == 0) {
            if (!new SharedPrefsManager(this).getString(SharedPreferenceKeys.USER_TYPE).equalsIgnoreCase(AppConstants.USER_DISTRIBUTOR)) {
                showToast("Please select a Distributor");
                return false;
            }
        } else if (binding.tvSaleDate.getText().toString().equalsIgnoreCase("Sale Date")) {
            showToast("Please select a sale date");
            return false;
        } else if (binding.edtCustomerName.getText().toString().trim().length() == 0) {
            showToast("Please enter customer name");
            return false;
        } else if (binding.edtCustomerName.getText().toString().length() < 3) {
            showToast("Customer Name invaild");
            return false;
        } else if (binding.edtCustomerPhone.getText().toString().trim().length() == 0) {
            showToast("Please enter customer phone");
            return false;
        } else if (binding.edtCustomerPhone.getText().toString().length() < 10
                || binding.edtCustomerPhone.getText().toString().startsWith("0")
                || binding.edtCustomerPhone.getText().toString().startsWith("1")
                || binding.edtCustomerPhone.getText().toString().startsWith("2")
                || binding.edtCustomerPhone.getText().toString().startsWith("3")
                || binding.edtCustomerPhone.getText().toString().startsWith("4")
                || binding.edtCustomerPhone.getText().toString().startsWith("5")
                || binding.edtCustomerPhone.getText().toString().equalsIgnoreCase("0000000000")) {
            showToast("Mobile Number invaild");
            return false;
        } else if (binding.edtCustomerAddress.getText().toString().trim().length() == 0) {
            showToast("Please enter customer address");
            return false;
        } else if (binding.edtCustomerAddress.getText().toString().length() < 5
                || binding.edtCustomerAddress.getText().toString().matches("[0-9+$,!-#$%'()*-.:;+]*")) {
            showToast("Customer Address invaild");
            return false;
        } else if (binding.spinnerCustomerState.getSelectedItemPosition() == 0) {
            showToast("Please select a State");
            return false;
        } else if (binding.spinnerCustomerCity.getSelectedItemPosition() == 0) {
            showToast("Please select a City");
            return false;
        } else if ((attachment.equalsIgnoreCase("") && showUploadImageOption)) {
            showToast("Please upload image");
            return false;
        } else if (attachment.equalsIgnoreCase("") && updateSerialNoManual) {
            showToast("Please upload image");
            return false;
        } else {
            Mtype = binding.edtSerialNumber.getText().toString();
            if (Mtype != null && !Mtype.isEmpty()) {
                Mtype = "yes";
            } else {
                Mtype = "no";
            }

            if (attachment.equalsIgnoreCase("")) {
                Mtype = "no";
            }

            if (Mtype.equalsIgnoreCase("NO")) {
                if (attachment.equalsIgnoreCase("") && showUploadImageOption) {
                    showToast("Please upload image");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void getSerialNumberValidation() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        if (!TextUtils.isEmpty(binding.edtSerialNumber.getText().toString())) {

            String url = ServerConfig.mSerWRSrNoExistanceUpdate(binding.edtSerialNumber.getText().toString());
            Log.d(TAG, "url: " + url);

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

                            try {
                                String data = response.string();
                                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                                if (data.contains("ok")) {
                                    isSerialValid = true;
                                    disableSerialNoInput = true;
                                    showUploadImageOption = false;
                                } else if (data.contains("image_required")) {
                                    isSerialValid = true;
                                    disableSerialNoInput = true;
                                    showUploadImageOption = true;
                                } else if (data.contains("Already_Entered")) {
                                    isSerialValid = false;
                                    disableSerialNoInput = false;
                                    showUploadImageOption = false;
                                } else if (data.contains("Invalid")) {
                                    isSerialValid = false;
                                    disableSerialNoInput = false;
                                    showUploadImageOption = false;
                                } else {
                                    return;
                                }
                                updateSerialNumberValidation(isSerialValid, disableSerialNoInput, showUploadImageOption, data, updateSerialNoManual);

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                            CommonUtility.printLog(TAG, "onComplete");
                        }
                    });

        } else
            Toast.makeText(getApplicationContext(), "Please enter Serial Number", Toast.LENGTH_LONG).show();
    }

    public void updateSerialNumberValidation(boolean isSerialNumberValid, boolean disableSerialNoInput, boolean showUploadImageOption, String data, boolean updateSerialNoManual) {

        if (!isSerialNumberValid) {
            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
            binding.svForm.setVisibility(View.INVISIBLE);
            binding.btnSubmit.setVisibility(View.INVISIBLE);
            return;
        }

        binding.svForm.setVisibility(View.VISIBLE);
        binding.btnSubmit.setVisibility(View.VISIBLE);
        if (showUploadImageOption) {
            binding.tvUploadPic.setVisibility(View.VISIBLE);
            binding.UploadLabel.setVisibility(View.VISIBLE);
        } else {
            if (updateSerialNoManual) {
                binding.tvUploadPic.setVisibility(View.VISIBLE);
                binding.UploadLabel.setVisibility(View.VISIBLE);
            } else {
                if (!updateSerialNoManual && data.contains("ok")) {
                    binding.tvUploadPic.setVisibility(View.GONE);
                    binding.UploadLabel.setVisibility(View.GONE);
                } else {
                    binding.tvUploadPic.setVisibility(View.VISIBLE);
                    binding.UploadLabel.setVisibility(View.VISIBLE);
                }
            }
        }

        if (disableSerialNoInput) {
            binding.edtSerialNumber.setEnabled(false);
            binding.edtSerialNumber.setClickable(false);
            binding.ivScanBarcode.setClickable(false);
            binding.ivScanBarcode.setEnabled(false);
        } else {
            binding.edtSerialNumber.setEnabled(true);
            binding.edtSerialNumber.setClickable(true);
            binding.ivScanBarcode.setClickable(true);
            binding.ivScanBarcode.setEnabled(true);
        }
    }

    /*TODO : check for each field*/
    /*String serailNo, String selectedDealer, String salesDate, String customerName, String customerPhone, String customerLandline, String customerAddress, String selectedState, String selectedCity*/
    private boolean validateForm(String... params) {

        for (String s : params) {

            if (s.isEmpty()) {
                Log.d(TAG, "validateForm, empty: " + s);
                return false;
            }
        }

        return true;
    }

    private void getDistributers() {

        if (!CommonUtility.isNetworkAvailable(context))
            return;

//        showProgressDialog();
        String url = ServerConfig.mSerWRDistListByDlr(new SharedPrefsManager(context).getString(SharedPreferenceKeys.USER_ID));

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
                            dealerList = new Gson().fromJson(data, new TypeToken<List<MSerWRDistListByDlrEntity>>() {
                            }.getType());


                            if (dealerList != null && !dealerList.isEmpty()) {
                                binding.spinnerSelectDistributer.setAdapter(new DealersDropdownAdapter(context, dealerList));
                                binding.spinnerSelectDistributer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position > 0)
                                            selectedDealer = dealerList.get(position - 1).getDistCode();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
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
                        CommonUtility.printLog(TAG, "onComplete");
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

                            stateList = new Gson().fromJson(data, new TypeToken<List<SerWRStateListEntity>>() {
                            }.getType());

                            if (stateList != null && !stateList.isEmpty()) {
                                binding.spinnerCustomerState.setAdapter(new StateDropdownAdapter(context, stateList));

                                binding.spinnerCustomerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position > 0) {
                                            selectedState = stateList.get(position - 1).getStateName();
                                            getCities(stateList.get(position - 1).getStateId());
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            }
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
                        CommonUtility.printLog(TAG, "onComplete");
                    }
                });
    }

    private void getCities(String stateid) {

        if (!CommonUtility.isNetworkAvailable(context))
            return;

        showProgressDialog();
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
                            cityList = new Gson().fromJson(data, new TypeToken<List<SerWRGetCityNameEntity>>() {
                            }.getType());
                            if (cityList != null && cityList.size() > 0) {
                                binding.spinnerCustomerCity.setAdapter(new CityDropdownAdapter(context, cityList));
                                binding.spinnerCustomerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position > 0)
                                            selectedCity = cityList.get(position - 1).getDistName();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                            }

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
                        CommonUtility.printLog(TAG, "onComplete");
                    }
                });

    }

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void openScanner() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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

                binding.tvUploadPic.setText("");

                Uri selectedFileUri = data.getData();
                if (selectedFileUri == null) {
                    return;
                }

                if (selectedFileUri != null) {
                    selectedFilePath = FilePath.getPath(this, selectedFileUri);
                    uploadFile(selectedFilePath);
                }

            }

            if (requestCode == PICK_FILE_REQUEST) {


                if (mCurrentPhotoPath.equalsIgnoreCase("")) {
                    //no data present
                    return;
                }

                binding.tvUploadPic.setText(mCurrentPhotoPath);


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
                        image_str = org.kobjects.base64.Base64.encode(byte_arr);
                        Log.e("image_str---", image_str + "___________" + imageBitmap.getAllocationByteCount());
                        attachment = image_str;
                    }
                }
            } else if (requestCode == AppConstants.ZBAR_SCANNER_REQUEST) { //Scanner by Anusha 27/06/2018 TASK#4009
                try {
                    updateSerialNumber(data.getStringExtra(AppConstants.SCAN_RESULT));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateSerialNumber(String serialNumber) {
        updateSerialNoManual = false;
        binding.edtSerialNumber.setText("" + serialNumber);
        getSerialNumberValidation();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        binding.tvSaleDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
    }

    public void uploadFile(final String selectedFilePath) {
        File selectedFile = null;
        try {
            selectedFile = new File(selectedFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!selectedFile.isFile()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.tvUploadPic.setText("Upload Pic" + selectedFilePath);
                }
            });
        } else {
            try {


                byte[] encodedBytes = Base64.encodeBase64(loadFileAsBytesArray(selectedFilePath));
                attachment = new String(encodedBytes, "UTF-8"); // for UTF-8 encoding
                binding.tvUploadPic.setText(selectedFilePath);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Cannot Read/Write File!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.btnSubmit:
                submit();
                break;

            case R.id.tvUploadPic:
                CameraDialogMenu dialogMenu = new CameraDialogMenu(this);
                dialogMenu.setListener(new CameraDialogMenu.OnDialogMenuListener() {
                    @Override
                    public void onPDFPress() {
                        camera_Permission();
                    }

                    @Override
                    public void onGalleryPress() {
                        gallery();
                    }
                });
                break;


            case R.id.tvSaleDate:
                openDatePicker();
                break;

            case R.id.ivScanBarcode:
                openScanner();
                break;


        }


    }

    public void camera_Permission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        } else if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        } else {
            dispatchTakePictureIntent();
        }
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

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();
        if (v != null &&
                (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + v.getLeft() - scrcoords[0];
            float y = event.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                Activity activity = this;
                if (activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}

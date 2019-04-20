package com.luminous.mpartner.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.luminous.mpartner.BaseActivity;
import com.luminous.mpartner.BuildConfig;
import com.luminous.mpartner.R;
import com.luminous.mpartner.customviews.VersionChecker;
import com.luminous.mpartner.databinding.ActivityLoginBinding;
import com.luminous.mpartner.home_page.activities.HomePageActivity;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.CreateOtpEntity;
import com.luminous.mpartner.network.entities.ProfileEntity;
import com.luminous.mpartner.network.entities.SubmitResponse;
import com.luminous.mpartner.network.entities.VerifyOtpEntity;
import com.luminous.mpartner.reports.fragments.PrimaryReportFragment;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    //Firebase Analytics
//    private FirebaseAnalytics mFirebaseAnalytics;

    private String mUserId;

    //Check for version
    String currentVersion, latestVersion;
    Dialog dialog;

    String otpPin;
    private ProfileEntity mProfileDetail;

    private ActivityLoginBinding binding;
    private SharedPrefsManager sharedPrefsManager;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);


        Fabric.with(this, new Crashlytics());
        FirebaseApp.initializeApp(this);

        sharedPrefsManager = new SharedPrefsManager(this);

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        binding.btnLogin.setOnClickListener(this);
        binding.btnOTP.setOnClickListener(this);
        binding.tvResend.setOnClickListener(this);

//        // Obtain the FirebaseAnalytics instance.
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Screenviews
//        Bundle params = new Bundle();
//        params.putString("screen_name", "com.luminous.mpartner.ui.activities.SplashActivity");
//        mFirebaseAnalytics.logEvent("Login", params);

        if (sharedPrefsManager.getString(SharedPreferenceKeys.TOKEN).trim().length() > 0 && (sharedPrefsManager.getString(SharedPreferenceKeys.OTP_PIN).trim().length() > 0)) {
            startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
            finish();
        } else {


            if (CommonUtility.isNewVersionAvailable(this))
                CommonUtility.showUpdateDialog(this);

            /**************Change by Anusha for GCM Push Notification****************************/
//        if (checkPlayServices()) {
//            // Start IntentService to register this application with GCM.
//            Intent intent = new Intent(this, RegistrationIntentService.class);
//            startService(intent);
//        }
        }
    }

    private void showUpdateDialog() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
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

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
//                        .show();
//            } else {
//                Log.i(TAG, "This device is not supported.");
//                finish();
//            }
//            return false;
//        }
        return true;
    }

    /***************************End of change********************************************/

    public void createOTP() {
        mUserId = binding.activitySplashEtId.getText().toString();
        mUserId = mUserId.replaceFirst("^0+(?!$)", "");  //Edited by Anusha to remove leading zeros
        if (mUserId.trim().length() > 0) {
            binding.activitySplashEtId.setError(null);
            /****************Change by Anusha to get Device Reg Id for GCM Push Notification by Anusha 30/07/2018 TASK#4129*************/
//            String token = AppSharedPrefrences.getInstance(getApplicationContext()).getRegistrationId();


            getDataAndLoginUser();
        } else {
            binding.activitySplashEtId.setError("Enter Distributer/Dealer Id");     //Edited by Anusha
        }
    }

    public void attemptLogin() {
        otpPin = binding.activityVerifyEtId.getText().toString();
        if (otpPin.trim().length() > 0) {
            binding.activityVerifyEtId.setError(null);
            getDataAndValidateOtp();
        } else {
            Toast.makeText(this, "OTP field cannot be empty", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Get device informations and send to user login.
     */
    private void getDataAndLoginUser() {

        if (!CommonUtility.isNetworkAvailable(this)) {
            return;
        }

        showProgressDialog();
        String Imei = getIMEI(LoginActivity.this);
        if (Imei == null)
            Imei = CommonUtility.getDeviceId(LoginActivity.this);
        String deviceVersion = Build.VERSION.RELEASE;
        String appVersion = BuildConfig.VERSION_NAME;

        String url = String.format(ServerConfig.createOTPUrl(), mUserId, Imei, deviceVersion, "Android", appVersion);

        apiInterface.createOtp(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CreateOtpEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(CreateOtpEntity createOtpEntity) {
                        dismissProgressDialog();

                        if (createOtpEntity.getStatus().equalsIgnoreCase("SUCCESS")) {

                            binding.tvOtpSent.setText(createOtpEntity.getMessage());
                            binding.activitySplashEtId.setEnabled(false);
                            binding.activityVerifyEtId.setEnabled(true);
                            binding.activityVerifyEtId.setAlpha(0.5f);
                            binding.btnOTP.setVisibility(View.GONE);
                            binding.activityVerifyEtId.setVisibility(View.VISIBLE);
                            binding.btnLogin.setVisibility(View.VISIBLE);
                            binding.tvResend.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(LoginActivity.this, createOtpEntity.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });

    }

    private void getDataAndValidateOtp() {
        if (!CommonUtility.isNetworkAvailable(this)) {
            return;
        }

        showProgressDialog();
        String Imei = getIMEI(LoginActivity.this);
        if (Imei == null)
            Imei = CommonUtility.getDeviceId(LoginActivity.this);
        String deviceVersion = Build.VERSION.RELEASE;

        String url = String.format(ServerConfig.verifyOTPUrl(), mUserId, Imei, deviceVersion, "Android", otpPin);

        apiInterface.verifyOtp(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VerifyOtpEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(VerifyOtpEntity verifyOtpEntity) {
                        dismissProgressDialog();

                        if (verifyOtpEntity.getStatus().equalsIgnoreCase("SUCCESS")) {

                            sharedPrefsManager.putString(SharedPreferenceKeys.USER_ID, mUserId);
                            sharedPrefsManager.putString(SharedPreferenceKeys.OTP_PIN, otpPin);


                            verifyUser();

                        } else {
                            Toast.makeText(LoginActivity.this, verifyOtpEntity.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });


    }

    //Alert Dialog incase permission not granted
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create()
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOTP:
                createOTP();
                break;
            case R.id.btn_login:
                attemptLogin();
                break;
            case R.id.tvResend:
                getDataAndLoginUser();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void getProfileData() {

        if (!CommonUtility.isNetworkAvailable(this)) {
            return;
        }

        showProgressDialog();
        String url = ServerConfig.sscPartnerDetailst(mUserId);

        apiInterface.sscPartnerDetailst(url)
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
                            ProfileEntity profileEntity = new Gson().fromJson(jsonArray.getString(0), ProfileEntity.class);

                            if (profileEntity != null) {
                                sharedPrefsManager.putObject(SharedPreferenceKeys.USER_DATA, profileEntity);
                                sharedPrefsManager.putString(SharedPreferenceKeys.USER_TYPE, profileEntity.getUserType());
                            }

                            startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                            finish();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });

    }

    public void verifyUser() {

        if (!CommonUtility.isNetworkAvailable(this)) {
            return;
        }

        showProgressDialog();

        String url = ServerConfig.newUrl(String.format(ServerConfig.userverificationURL()), getApplicationContext(), LoginActivity.class.getSimpleName());

        apiInterface.getToken(url)
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
                            JSONObject jsonArray = new JSONObject(data);
                            SubmitResponse submitResponse = new Gson().fromJson(jsonArray.toString(), SubmitResponse.class);

                            if (submitResponse != null && submitResponse.getStatus().equalsIgnoreCase("200")) {
                                getProfileData();
                                sharedPrefsManager.putString(SharedPreferenceKeys.TOKEN, submitResponse.getToken());
                            } else {
                                Toast.makeText(getApplicationContext(), submitResponse.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });

    }

    public String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return telephonyManager.getDeviceId();
    }

}

package com.luminous.mpartner.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.luminous.mpartner.BuildConfig;
import com.luminous.mpartner.MyApplication;
import com.luminous.mpartner.R;
import com.luminous.mpartner.events.OpenLuck7Event;
import com.luminous.mpartner.events.ProductCatalogClickEvent;
import com.luminous.mpartner.fragments.BaseFragment;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.CreateOtpEntity;
import com.luminous.mpartner.network.entities.TermsAndConditions;
import com.luminous.mpartner.network.entities.VerifyOtpEntity;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TermsAndConditionsActivity extends BaseFragment {

    String mUserId, otpPin;
    TextView txtDealer, txtDistributor, tvOTPSent;
    private SharedPrefsManager sharedPrefsManager;

    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Dialog dialog;

    public TermsAndConditionsActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GalleryTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TermsAndConditionsActivity newInstance(String param1, String param2) {
        TermsAndConditionsActivity fragment = new TermsAndConditionsActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_terms_and_conditions, container, false);
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);


        sharedPrefsManager = new SharedPrefsManager(getContext());

        final CheckBox checkBox = rootView.findViewById(R.id.checkbox);
        Button submitButton = rootView.findViewById(R.id.btn_submit);
        txtDealer = rootView.findViewById(R.id.txt_dealer);
        txtDistributor = rootView.findViewById(R.id.txt_distributor);


        mUserId = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    createOTP();
                    showSubmitOTPDialog();

//                    sharedPrefsManager.putBoolean(SharedPreferenceKeys.IsOTPVerified, true);
//                    MyApplication.getApplication()
//                            .bus()
//                            .send(new OpenLuck7Event());
                } else {
                    Toast.makeText(getContext(), "Please acccept terms & conditions.", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        getTermsAndConditions();
        setTermsUI(null);

        return rootView;

    }


    public void setTermsUI(TermsAndConditions termsAndConditions) {
        if (sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE).equalsIgnoreCase("Distributor")) {
            txtDistributor.setVisibility(View.VISIBLE);
//            txtDistributor.setText(termsAndConditions.getTermsCondition());
            txtDealer.setVisibility(View.GONE);
        } else {
            txtDistributor.setVisibility(View.GONE);
            txtDealer.setVisibility(View.VISIBLE);
//            txtDealer.setText(termsAndConditions.getTermsCondition());
        }
    }

    public void showOTPMessage(String message) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create()
                .show();
    }

    public void showSubmitOTPDialog() {
        // custom dialog
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.otp_dialog_layput);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        final EditText editText = dialog.findViewById(R.id.activity_verify_et_id);
        Button submitButton = dialog.findViewById(R.id.btn_submit);
        tvOTPSent = dialog.findViewById(R.id.tvOtpSent);
        TextView tvResend = dialog.findViewById(R.id.tvResend);
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOTP();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().length() > 0) {
                    otpPin = editText.getText().toString().trim();
                    getDataAndValidateOtp();
                }
            }
        });

        dialog.show();

    }

    public void createOTP() {

        mUserId = mUserId.replaceFirst("^0+(?!$)", "");  //Edited by Anusha to remove leading zeros
        if (mUserId.trim().length() > 0) {
//            binding.activitySplashEtId.setError(null);
            /****************Change by Anusha to get Device Reg Id for GCM Push Notification by Anusha 30/07/2018 TASK#4129*************/
//            String token = AppSharedPrefrences.getInstance(getApplicationContext()).getRegistrationId();


            getDataAndLoginUser();
        } else {
        }
    }

    /**
     * Get device informations and send to user login.
     */
    private void getDataAndLoginUser() {

        if (!CommonUtility.isNetworkAvailable(getActivity())) {
            return;
        }

        showProgressDialog();
        String Imei = CommonUtility.getDeviceId(getContext());

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

                            tvOTPSent.setText(createOtpEntity.getMessage());

                        } else {
                            Toast.makeText(getActivity(), createOtpEntity.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });

    }

    private void getDataAndValidateOtp() {
        if (!CommonUtility.isNetworkAvailable(getActivity())) {
            return;
        }

        showProgressDialog();
        String Imei = CommonUtility.getDeviceId(getActivity());
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

                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }

                            Toast.makeText(getActivity(), verifyOtpEntity.getMessage(), Toast.LENGTH_LONG).show();

                            sharedPrefsManager.putBoolean(SharedPreferenceKeys.IsOTPVerified, true);

                            MyApplication.getApplication()
                                    .bus()
                                    .send(new OpenLuck7Event());
                        } else {
                            Toast.makeText(getActivity(), verifyOtpEntity.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });

        /****************Change by Anusha to get Device Reg Id for GCM Push Notification by Anusha 30/07/2018 TASK#4129*************/
//            String token = AppSharedPrefrences.getInstance(getApplicationContext()).getRegistrationId();
//            UserController userController1 = new UserController(SplashActivity.this, "deviceId");
//            userController1.updateDeviceId(mUserId, token, Imei);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }


}

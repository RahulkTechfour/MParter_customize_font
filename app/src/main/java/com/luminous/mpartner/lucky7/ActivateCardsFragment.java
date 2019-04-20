package com.luminous.mpartner.lucky7;


import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentActivateCardsBinding;
import com.luminous.mpartner.fragments.BaseFragment;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.ActivatedCouponReportEntity;
import com.luminous.mpartner.network.entities.GetDistributorCount;
import com.luminous.mpartner.network.entities.GoldActivatedCouponReportEntity;
import com.luminous.mpartner.network.entities.LsdCouponCountDatum;
import com.luminous.mpartner.network.entities.LsdOpenReimbursmentDatum;
import com.luminous.mpartner.network.entities.OpenReimbursementReportEntity;
import com.luminous.mpartner.network.entities.RedeemReportEntity;
import com.luminous.mpartner.network.entities.SubmitResponse;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivateCardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ActivateCardsFragment extends BaseFragment implements View.OnClickListener, ZBarScannerView.ResultHandler {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ActivateCardsRecyclerviewAdapter adapter;
    private Context context;
    private List<LsdOpenReimbursmentDatum> claimReports;
    //    private ZBarScannerView scannerView;
    private String qrCode = "";
    public static final int REQUEST_CAMERA_PERMISSION = 225;
    public static final int STORAGE_PERMISSION_CODE_1 = 125;
    public static final int STORAGE_PERMISSION_CODE_2 = 124;
    public static final int STORAGE_PERMISSION_CODE_3 = 123;
    public boolean isDownload = false;

    private FragmentActivateCardsBinding binding;
    private SharedPrefsManager sharedPrefsManager;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ActivateCardsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivateCardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivateCardsFragment newInstance(String param1, String param2) {
        ActivateCardsFragment fragment = new ActivateCardsFragment();
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

    public void getDistributorCount() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();
        String url = ServerConfig.newUrl(ServerConfig.getDistributorCountUrl(), context, ActivateCardsFragment.class.getSimpleName() + ".class");
//        String url = ServerConfig.getDistributorCountUrl();

        apiInterface.getDistributorCountData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetDistributorCount>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(GetDistributorCount distributorCount) {
                        dismissProgressDialog();
                        if (distributorCount.getStatus().equalsIgnoreCase("200")) {
                            setUIData(distributorCount.getLsdCouponCountData().get(0));
                        }else
                            Toast.makeText(getActivity(), distributorCount.getMessage(), Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });

    }


    public void saveQRCode() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        showProgressDialog();
        String url = ServerConfig.newUrl(ServerConfig.saveQRCodeUrl(qrCode), context, ActivateCardsFragment.class.getSimpleName() + ".class");
//        String url = ServerConfig.saveQRCodeUrl();

        apiInterface.saveQrCOde(url)
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
                        Toast.makeText(context, submitResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_activate_cards, container, false);
        View view = binding.getRoot();

        sharedPrefsManager = new SharedPrefsManager(context);

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        if (CommonUtility.isNetworkAvailable(context)) {
            getDistributorCount();
        }

        binding.llOpenReimbursement.setOnClickListener(this);
        binding.llReimbursement.setOnClickListener(this);
        binding.refreshLayout.setOnClickListener(this);
        binding.ivScanQr.setOnClickListener(this);
        binding.btnClick.setOnClickListener(this);
        binding.llActivatedCoupon.setOnClickListener(this);
        binding.llGoldActivatedCoupon.setOnClickListener(this);

        return view;
    }

    public void openScanner() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            binding.ivScanQr.setVisibility(View.GONE);
            binding.scannerview.setVisibility(View.VISIBLE);
            if (isCameraAvailable()) {
                binding.scannerview.stopCamera();
                binding.scannerview.setResultHandler(this);
                binding.scannerview.startCamera(false);
            } else {
                Toast.makeText(context, "Rear Facing Camera Unavailable", Toast.LENGTH_LONG).show();
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

    }

    public void showSubmitClaimDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.submit_claim_layout);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        RecyclerView recyclerView = dialog.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

        if (claimReports != null) {
            adapter = new ActivateCardsRecyclerviewAdapter(context, claimReports);
            recyclerView.setAdapter(adapter);
        }

        final CheckBox checkBox = dialog.findViewById(R.id.checkbox);
        Button submitButton = dialog.findViewById(R.id.btn_submit);
        TextView et_distributor_code = dialog.findViewById(R.id.et_distributor_code);
        TextView et_distributor_name = dialog.findViewById(R.id.et_distributor_name);

        et_distributor_code.setText(sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID));
        et_distributor_name.setText(sharedPrefsManager.getString(SharedPreferenceKeys.USER_NAME));
        submitButton.setOnClickListener(view -> {
            if (checkBox.isChecked()) {
                saveClaimData();
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Please acccept terms & conditions.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

    }

    public void getActivatedCouponReports() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE_3);
        } else {

            if (!CommonUtility.isNetworkAvailable(context)) {
                return;
            }

            showProgressDialog();
            String url = ServerConfig.newUrl(ServerConfig.getDistOpenReimbursementReportUrl(), context, ActivateCardsFragment.class.getSimpleName() + ".class");
//            String url = ServerConfig.getActivatedCouponReportUrl();

            apiInterface.getActivatedCouponReportData(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ActivatedCouponReportEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(ActivatedCouponReportEntity activatedCouponReportEntity) {
                            dismissProgressDialog();

                            if (activatedCouponReportEntity.getStatus().equalsIgnoreCase("200")) {
                                CommonUtility.convertJsonToCSV(new Gson().toJson(activatedCouponReportEntity.getLsdDistActivatedData()), context, "activated_coupons_reports.csv");
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
                            CommonUtility.printLog("ContactUsDetailData", "onComplete");
                        }
                    });
        }

    }

    public void getGoldActivatedCouponReports() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE_3);
        } else {

            if (!CommonUtility.isNetworkAvailable(context)) {
                return;
            }

            showProgressDialog();
            String url = ServerConfig.newUrl(ServerConfig.getGoldDistOpenReimbursementReportUrl(), context, ActivateCardsFragment.class.getSimpleName() + ".class");
//            String url = ServerConfig.getActivatedCouponReportUrl();

            apiInterface.getGoldActivatedCouponReportData(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<GoldActivatedCouponReportEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(GoldActivatedCouponReportEntity activatedCouponReportEntity) {
                            dismissProgressDialog();

                            if (activatedCouponReportEntity.getStatus().equalsIgnoreCase("200")) {
                                CommonUtility.convertJsonToCSV(new Gson().toJson(activatedCouponReportEntity.getLsdDistActivatedData()), context, "gold_activated_coupons_reports.csv");
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
                            CommonUtility.printLog("ContactUsDetailData", "onComplete");
                        }
                    });
        }

    }

    public void getOpenReimbursementReports() {
        if (isDownload && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE_1);
        } else {

            if (!CommonUtility.isNetworkAvailable(context)) {
                return;
            }

            showProgressDialog();
            String url = ServerConfig.newUrl(ServerConfig.getDistOpenReimbursementReportUrl(), context, ActivateCardsFragment.class.getSimpleName() + ".class");
//            String url = ServerConfig.getDistOpenReimbursementReportUrl();

            apiInterface.getOpenReimbursementReportData(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<OpenReimbursementReportEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(OpenReimbursementReportEntity openReimbursementReportEntity) {
                            dismissProgressDialog();
                            if (openReimbursementReportEntity.getStatus().equalsIgnoreCase("200")) {

                                claimReports = openReimbursementReportEntity.getLsdOpenReimbursmentData();
                                if (isDownload) {
                                    CommonUtility.convertJsonToCSV(new Gson().toJson(openReimbursementReportEntity.getLsdOpenReimbursmentData()), context, "reimbursementReports.csv");
                                } else {
                                    showSubmitClaimDialog();
                                }
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
                            CommonUtility.printLog("ContactUsDetailData", "onComplete");
                        }
                    });

        }

    }

    public void getRedeemedReports() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE_2);
        } else {
            if (isDownload && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE_1);
            } else {

                if (!CommonUtility.isNetworkAvailable(context)) {
                    return;
                }

                showProgressDialog();
                String url = ServerConfig.newUrl(ServerConfig.getDistOpenReimbursementReportUrl(), context, ActivateCardsFragment.class.getSimpleName() + ".class");
//                String url = ServerConfig.getRedeemedReportUrl();

                apiInterface.getRedeemReportData(url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<RedeemReportEntity>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onNext(RedeemReportEntity redeemReportEntity) {
                                dismissProgressDialog();

                                if (redeemReportEntity.getStatus().equalsIgnoreCase("200")) {
                                    CommonUtility.convertJsonToCSV(new Gson().toJson(redeemReportEntity.getLsdDealerRedeemedData()), context, "redeemedReports.csv");
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
                                CommonUtility.printLog("ContactUsDetailData", "onComplete");
                            }
                        });

            }
        }
    }

    public boolean isCameraAvailable() {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_open_reimbursement:
                isDownload = true;
                getOpenReimbursementReports();
                break;
            case R.id.ll_reimbursement:
                getRedeemedReports();
                break;
            case R.id.ll_activated_coupon:
                getActivatedCouponReports();
                break;
            case R.id.ll_gold_activated_coupon:
                getGoldActivatedCouponReports();
                break;
            case R.id.btn_click:
                isDownload = false;
                getOpenReimbursementReports();
                break;
            case R.id.iv_scan_qr:
                openScanner();
                break;
            case R.id.refresh_layout:
                getDistributorCount();
                break;
        }
    }

    public void setUIData(LsdCouponCountDatum lsdCouponCountDatum) {

        binding.tvOpenReimbursement.setText(lsdCouponCountDatum.getOpenReimbursment() + "");
        binding.tvEligibleCoupons.setText(lsdCouponCountDatum.getEligibleCouponCount() + "");
        binding.tvBalanceCoupons.setText(lsdCouponCountDatum.getBalanceCouponCount() + "");
        binding.tvCouponReimbursement.setText(lsdCouponCountDatum.getCouponReimbursment() + "");
        binding.tvActivatedCoupons.setText(lsdCouponCountDatum.getActivatedCouponCount() + "");

        binding.tvGoldActivatedCoupons.setText(lsdCouponCountDatum.getGold_ActivatedCouponCount() + "");
        binding.tvGoldEligibleCoupons.setText(lsdCouponCountDatum.getGold_EligibleCouponCount() + "");
        binding.tvGoldBalanceCoupons.setText(lsdCouponCountDatum.getGold_BalanceCouponCount() + "");
    }


    @Override
    public void handleResult(Result rawResult) {
        binding.ivScanQr.setVisibility(View.VISIBLE);
        binding.scannerview.setVisibility(View.GONE);
        qrCode = rawResult.getContents();
        Log.e("RESPONSE>>>>>>>>>>>>>>", "QRCode===" + qrCode);
        saveQRCode();
    }

// change by Rahul
    public void saveClaimData() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
        String userId = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);
        String token = sharedPrefsManager.getString(SharedPreferenceKeys.TOKEN);
        String appVersionName = "";
        String appVersionCode = "";
        final List<String> versionList = CommonUtility.appVersionDetails(context);
        if (versionList != null && versionList.size() == 2) {
            appVersionName = versionList.get(0);
            appVersionCode = versionList.get(1);
        }
        String appVersion = appVersionName;
        String deviceId = CommonUtility.getDeviceId(context);;
        String deviceName= CommonUtility.getDeviceName();
        String osType= "Android";
        String os_version_name = ""+ Build.VERSION.RELEASE;
        String OsVersionCode =""+Build.VERSION.SDK_INT;
        String ipAddress=CommonUtility.getIpAddress(context);
        String lang = CommonUtility.getAppLanguage(context);


        String screen_name = ActivateCardsFragment.class.getSimpleName() + ".class";
        String networkType = CommonUtility.getNetworkType(context);
        String netOperator = CommonUtility.getNetworkOperator(context);
        String timeStamp = "" + System.currentTimeMillis();
        String channel = "M";

        showProgressDialog();
        String url = ServerConfig.saveClaimDataUrl(userId,token, appVersion, deviceId, deviceName, osType, os_version_name, OsVersionCode, ipAddress, lang, screen_name, networkType, netOperator, timeStamp, channel);

        apiInterface.saveClaimData(url)
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
                        Toast.makeText(context, submitResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("ContactUsDetailData", "onComplete");
                    }
                });
    }

    public String concatStringsWSep() {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (LsdOpenReimbursmentDatum lsdOpenReimbursmentDatum : claimReports) {
            sb.append(sep).append(lsdOpenReimbursmentDatum.getAlphanumericCode());
            sep = ",";
        }
        return sb.toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ActivateCardsFragment.REQUEST_CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openScanner();
                } else {
                    Toast.makeText(context, "Camera Permission Required", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case ActivateCardsFragment.STORAGE_PERMISSION_CODE_1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getOpenReimbursementReports();
                } else {
                    Toast.makeText(context, "Storage Permission Required", Toast.LENGTH_SHORT).show();
                }
                break;
            case ActivateCardsFragment.STORAGE_PERMISSION_CODE_2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getRedeemedReports();
                } else {
                    Toast.makeText(context, "Storage Permission Required", Toast.LENGTH_SHORT).show();
                }
                break;
            case ActivateCardsFragment.STORAGE_PERMISSION_CODE_3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getActivatedCouponReports();
                } else {
                    Toast.makeText(context, "Storage Permission Required", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

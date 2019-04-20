package com.luminous.mpartner.reports.fragments.WarrantyReport;


import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.WRDealerAdapter;
import com.luminous.mpartner.databinding.FragmentDealerWrBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.WarrantyReport.DealerListEntity;
import com.luminous.mpartner.network.entities.WarrantyReport.DealerResonseEntity;
import com.luminous.mpartner.network.entities.WarrantyReport.SchemeEntity;
import com.luminous.mpartner.reports.fragments.PrimaryBillingReportFragment;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class DealerWRFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String TAG = "DealerWRFragment";
    ArrayList<SchemeEntity> schemeList;
    ArrayList<DealerListEntity> dealerListEntities;
    private FragmentDealerWrBinding binding;
    private String dealer, scheme, downloadReport;
    private int STORAGE_PERMISSION_CODE_3 = 7;
    private String disCode, userType;
    private SharedPrefsManager sharedPrefsManager;

    public DealerWRFragment() {
    }

    public static DealerWRFragment newInstance(String param1, String param2) {
        DealerWRFragment fragment = new DealerWRFragment();
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
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);
        sharedPrefsManager = new SharedPrefsManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dealer_wr, container, false);
        disCode = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);
        userType = sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE);


        getSchemeList();
        getDealerList();

        binding.spDealer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (dealerListEntities != null) {
                    if (position > 0)
                        dealer = dealerListEntities.get(position).getDlrCode();
                    else
                        dealer = "0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dealer = null;
            }
        });

        binding.spScheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (schemeList != null) {
                    if (position != 0)
                        scheme = schemeList.get(position).getSchemeValue();
                    else
                        scheme = null;
                } else scheme = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                scheme = null;
            }
        });

        binding.btnSubmit.setOnClickListener(this);
        binding.tvDownload.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);

        return binding.getRoot();
    }

    private void dataLoading(boolean loading, boolean datafound) {

        if (loading) {
            binding.llReport.setVisibility(View.GONE);
            binding.llPgTv.setVisibility(View.VISIBLE);
            binding.pg.setVisibility(View.VISIBLE);
            binding.tvNoDataFound.setVisibility(View.GONE);
        } else {

            if (datafound) {
                binding.llReport.setVisibility(View.VISIBLE);
                binding.llPgTv.setVisibility(View.GONE);
            } else {
                binding.llReport.setVisibility(View.GONE);
                binding.llPgTv.setVisibility(View.VISIBLE);
                binding.pg.setVisibility(View.GONE);
                binding.tvNoDataFound.setVisibility(View.VISIBLE);
            }

        }

    }


    private void getSchemeList() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.getWRSchemeList();
        Log.d(TAG, "url: " + url);

        apiInterface.baseRetrofitInterface(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        String response = null;

                        try {
                            response = responseBody.string();
                            Log.d(TAG, "onNext: " + response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        schemeList = new Gson().fromJson(response,
                                new TypeToken<ArrayList<SchemeEntity>>() {
                                }.getType());
                        if (schemeList == null) {
                            Log.d(TAG, "onNext: list null");
                        } else if (schemeList.size() == 0) {
                            Log.d(TAG, "onNext: list zero");
                        } else {
                            schemeList.add(0, null);
                            String[] sName = new String[schemeList.size()];
                            sName[0] = "Scheme";

                            for (int i = 1; i < schemeList.size(); i++) {
                                sName[i] = schemeList.get(i).getSchemeName();
                            }

                            ArrayAdapter<CharSequence> allItemSpinnerAdapter =
                                    new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, sName);
                            allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                            binding.spScheme.setAdapter(allItemSpinnerAdapter);

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void getDealerList() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.getDealerList(disCode);
        //url = "http://mpartner.luminousintranet.com:81//nonsapservices/api/nonsapservice/mSerWRDlrListByDist?dist_code=1101745&token=pass@1234";

        Log.d(TAG, "url: " + url);

        apiInterface.baseRetrofitInterface(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        String response = null;

                        try {
                            response = responseBody.string();
                            Log.d(TAG, "onNext: " + response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        dealerListEntities = new Gson().fromJson(response,
                                new TypeToken<ArrayList<DealerListEntity>>() {
                                }.getType());
                        if (dealerListEntities == null) {
                            Log.d(TAG, "onNext: list null");
                        } else if (dealerListEntities.size() == 0) {
                            Log.d(TAG, "onNext: list zero");
                        } else {
                            dealerListEntities.add(0, null);
                            String[] dName = new String[dealerListEntities.size()];
                            dName[0] = "All Dealers";

                            for (int i = 1; i < dealerListEntities.size(); i++) {
                                dName[i] = dealerListEntities.get(i).getDlrName() + " (" + dealerListEntities.get(i).getDlrCode() + ")";
                            }

                            ArrayAdapter<CharSequence> allItemSpinnerAdapter =
                                    new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, dName);
                            allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                            binding.spDealer.setAdapter(allItemSpinnerAdapter);

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnSubmit:
                if (scheme == null) {
                    Toast.makeText(getContext(), "Select Scheme", Toast.LENGTH_SHORT).show();
                } else if (dealer == null) {
                    Toast.makeText(getContext(), "Select Dealer", Toast.LENGTH_SHORT).show();
                } else submit();
                break;
            case R.id.tv_download:
                if (downloadReport != null)
                    getReport();
                else
                    Toast.makeText(getContext(), "Unable to download report", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnReset:
                reset();
                break;
        }

    }

    private void reset() {

        binding.spScheme.setSelection(0);
        binding.spDealer.setSelection(0);
        dataLoading(false, false);
        scheme = null;
        dealer = null;
        binding.tvNoDataFound.setText("");

    }

    private void submit() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        dataLoading(true, false);
        String url = ServerConfig.getWRDealerSummary(disCode, dealer, scheme);
        //url = "http://mpartner.luminousintranet.com:81//nonsapservices/api/nonsapservice/SerWrReportDlrWiseSchemeConnect?dist_code=1101745&dlr_code=1000069279&Scheme=connect%20plus%201&token=pass@1234&way=m\n";

        apiInterface.baseRetrofitInterface(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        String response = null;
                        try {
                            response = responseBody.string();
                            downloadReport = response;
                        } catch (Exception e) {
                            downloadReport = null;
                            Log.d(TAG, "onNext: " + e.getMessage());
                            dataLoading(false, true);
                        }

                        ArrayList<DealerResonseEntity> list = new Gson().fromJson(response,
                                new TypeToken<ArrayList<DealerResonseEntity>>() {
                                }.getType());

                        if (list == null) {
                            dataLoading(false, false);
                        } else if (list.size() == 0) {
                            dataLoading(false, false);
                        } else {
                            dataLoading(false, true);
                            WRDealerAdapter adapter = new WRDealerAdapter(getContext(), list);
                            binding.recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        downloadReport = null;
                        Log.d(TAG, "onError: " + e.getMessage());
                        dataLoading(false, false);


                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");

                    }
                });
    }

    public void getReport() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE_3);
        } else {

            if (!CommonUtility.isNetworkAvailable(getContext())) {
                Toast.makeText(getContext(), "No internet", Toast.LENGTH_SHORT).show();
                return;
            }

            getWebReport();
        }

    }

    private void getWebReport() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        CommonUtility.showProgressDialog(getActivity());
        String url = ServerConfig.getWRDealerSummaryWeb(disCode, dealer, scheme);
        Log.d(TAG, "url: " + url);
        apiInterface.baseRetrofitInterface(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        CommonUtility.dismissProgressDialog();

                        String response = null;
                        try {
                            response = responseBody.string();
                            Log.d(TAG, "onNext: " + response);
                        } catch (Exception e) {
                            Log.d(TAG, "onNext: " + e.getMessage());
                        }

                        if (response != null)
                            CommonUtility.convertJsonToCSV(response, getContext(), "wr_dlr_summary.csv");
                        else
                            Toast.makeText(getContext(), "No Data", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PrimaryBillingReportFragment.STORAGE_PERMISSION_CODE_3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getReport();
                } else {
                    Toast.makeText(getContext(), "Storage Permission Required", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

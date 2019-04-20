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
import com.luminous.mpartner.adapters.WRDistSummaryAdapter;
import com.luminous.mpartner.databinding.FragmentDistributorWrBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.WarrantyReport.DistWREntity;
import com.luminous.mpartner.network.entities.WarrantyReport.SchemeEntity;
import com.luminous.mpartner.reports.fragments.PrimaryBillingReportFragment;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import org.w3c.dom.Entity;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class DistributorWRFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String selectedScheme;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String TAG = "DistributorWRFragment";
    ArrayList<SchemeEntity> schemeList;
    private SharedPrefsManager sharedPrefsManager;
    private String disId, userType;
    private static final int STORAGE_PERMISSION_CODE_3 = 3;
    private String downloadReport;


    public DistributorWRFragment() {
        // Required empty public constructor
    }

    private FragmentDistributorWrBinding binding;

    public static DistributorWRFragment newInstance(String param1, String param2) {
        DistributorWRFragment fragment = new DistributorWRFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_distributor_wr, container, false);

        disId = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);
        userType = sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE);

        binding.spScheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (schemeList != null) {
                    if (position != 0) {
                        selectedScheme = schemeList.get(position).getSchemeValue();
                    } else {
                        selectedScheme = null;
                    }
                } else selectedScheme = null;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedScheme = null;
            }
        });
        binding.tvDownload.setOnClickListener(this);

        binding.btnSubmit.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);
        binding.tvDownload.setOnClickListener(this);
        getSchemeList();

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnSubmit:
                //getDistSummary();
                submit();
                break;
            case R.id.btnReset:
                reset();
                break;
            case R.id.tv_download:
                getReport();
                break;
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

                    }

                    @Override
                    public void onComplete() {

                    }
                });

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


    private void getDistSummary() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        dataLoading(true, false);
        String url = ServerConfig.getWRDistSummary(disId, selectedScheme, userType);
        Log.d(TAG, "Dynamic url : " + url);
        //url = "http://mpartner.luminousintranet.com:81//nonsapservices/api/nonsapservice/SerWRReportConnect_Card?dist_code=1000000891&Scheme=connect%20plus%201&user_type=dlr&token=pass@1234";

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
                            Log.d(TAG, "onNext: " + response);
                        } catch (Exception e) {
                            downloadReport = null;
                            e.printStackTrace();
                        }

                        ArrayList<DistWREntity> entities = new Gson().fromJson(response,
                                new TypeToken<ArrayList<DistWREntity>>() {
                                }.getType());

                        if (entities == null) {
                            Log.d(TAG, "onNext: null");
                            dataLoading(false, false);

                        } else if (entities.size() == 0) {
                            Log.d(TAG, "onNext: zero");
                            dataLoading(false, false);

                        } else {

                            dataLoading(false, true);

                            String[][] data = new String[4][4];
                            int i = 0;
                            for (DistWREntity entity : entities) {
                                data[i] = entity.toStringArray();
                            }

                            WRDistSummaryAdapter adapter = new WRDistSummaryAdapter(getContext(), entities);
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

    private void submit() {

        if (selectedScheme != null) {
            if (!selectedScheme.isEmpty()) {
                getDistSummary();
                return;
            }
        }

        Toast.makeText(getContext(), "Select Scheme", Toast.LENGTH_SHORT).show();
    }

    private void reset() {

        selectedScheme = null;
        downloadReport = null;
        binding.spScheme.setSelection(0);
        binding.tvNoDataFound.setText("");
        dataLoading(false, false);

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

            if (downloadReport != null)
                CommonUtility.convertJsonToCSV(downloadReport, getContext(), "wr_dist_summary.csv");
            else
                Toast.makeText(getContext(), "No Data", Toast.LENGTH_SHORT).show();

        }

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

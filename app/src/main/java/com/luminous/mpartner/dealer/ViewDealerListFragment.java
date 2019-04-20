package com.luminous.mpartner.dealer;


import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.ViewDealerListApapter;
import com.luminous.mpartner.databinding.FragmentViewDealerListBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.CustomerLedgerReportEntity;
import com.luminous.mpartner.network.entities.DealerListEntity;
import com.luminous.mpartner.sales.SalesViewOrderFragment;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class ViewDealerListFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String toDate;
    private String fromDate;
    private String selectedStatus;

    private FragmentViewDealerListBinding binding;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private boolean isFromDate;
    private String[] listStatus = {"All", "Inactive", "Active"};

    private static final String TAG = "DealerList123";


    public ViewDealerListFragment() {
    }


    public static ViewDealerListFragment newInstance(String param1, String param2) {
        ViewDealerListFragment fragment = new ViewDealerListFragment();
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
            fromDate = getArguments().getString(ARG_PARAM1);
            toDate = getArguments().getString(ARG_PARAM2);
        }

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_dealer_list, container, false);
        binding.fromTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                today.add(Calendar.DAY_OF_MONTH, -7);
                Date result = today.getTime();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), ViewDealerListFragment.this, today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                isFromDate = true;
                datePickerDialog.show();

            }
        });
        binding.toTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), ViewDealerListFragment.this, today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//                datePickerDialog.getDatePicker().setMinDate(getDateObject(fromDate).getTime());
                isFromDate = false;
                datePickerDialog.show();
            }
        });

        binding.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedStatus = listStatus[position];
                getDealerList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.fromTvDate.setText(fromDate);
        binding.toTvDate.setText(toDate);

        setStatusAdapter();
        return binding.getRoot();
    }

    private void setStatusAdapter() {
        ArrayAdapter<CharSequence> allItemSpinnerAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, listStatus);
        allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        binding.spinnerStatus.setAdapter(allItemSpinnerAdapter);
        selectedStatus = "All";
    }

    private String getStatus() {
        if (selectedStatus.equalsIgnoreCase("All"))
            return "A";
        else if (selectedStatus.equalsIgnoreCase("Inactive"))
            return "X";
        else if (selectedStatus.equalsIgnoreCase("Active"))
            return "";
        return "A";
    }


    private void getDealerList() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        CommonUtility.showProgressDialog(getActivity());

        SharedPrefsManager manager = new SharedPrefsManager(getContext());
        String userID = manager.getString(SharedPreferenceKeys.USER_ID);
        String url = ServerConfig.getDealerList(getStatus(), fromDate, toDate, userID);
        //url = "http://mpartner.luminousintranet.com:81/nonsapservices/api/sapservice/DEALER_LIST_REPORT?I_STAUTS=I&I_DATE_FROM=01/01/2017&I_DATE_TO=01/01/2018&I_LOGIN_ID=1100025&token=pass@1234&way=m";
        Log.d(TAG, "getDealerList: " + url);

        apiInterface.getViewDealerList(url)
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
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ArrayList<DealerListEntity> entities = new Gson().fromJson(response, new TypeToken<ArrayList<DealerListEntity>>() {
                        }.getType());

                        if (entities == null) {
                            Log.d(TAG, "onNext: null");
                            binding.hs.setVisibility(View.GONE);
                            binding.tvNoDataFound.setVisibility(View.VISIBLE);
                        } else if (entities.size() == 0) {
                            Log.d(TAG, "onNext: size 0");
                            binding.hs.setVisibility(View.GONE);
                            binding.tvNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            binding.hs.setVisibility(View.VISIBLE);
                            binding.tvNoDataFound.setVisibility(View.GONE);
                            setAdapter(entities);
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

    private void setAdapter(ArrayList<DealerListEntity> entities) {

        ViewDealerListApapter apapter = new ViewDealerListApapter(getContext(), entities);
        binding.recyclerView.setAdapter(apapter);
        apapter.notifyDataSetChanged();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (isFromDate) {
            fromDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            binding.fromTvDate.setText(fromDate);
            getDealerList();
        } else {
            toDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            binding.toTvDate.setText(toDate);
            getDealerList();
        }
    }

    private Date getDateObject(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date1 = sdf.parse(s);
            return date1;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}

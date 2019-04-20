package com.luminous.mpartner.reports.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.Feature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.CreditDebitReportAdapter;
import com.luminous.mpartner.databinding.FragmentCreditDebitBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.CreditDebitReportEntity;
import com.luminous.mpartner.network.entities.Notification.NotificationReadResponseEntity;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.ResponseBody;


public class CreditDebitFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int STORAGE_PERMISSION_CODE_3 = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean isFromDate;
    private RetrofitInterface apiInterface;
    FragmentCreditDebitBinding binding;
    private static final String TAG = "CreditDebitFragment";
    private String fromdate, todate;
    private String primaryReportString;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    List<CreditDebitReportEntity> list;
    private String iFrom, iTo;


    public CreditDebitFragment() {
    }


    public static CreditDebitFragment newInstance(String param1, String param2) {
        CreditDebitFragment fragment = new CreditDebitFragment();
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
            fromdate = getArguments().getString(ARG_PARAM1);
            todate = getArguments().getString(ARG_PARAM2);
            iFrom = fromdate;
            iTo = todate;
        }
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
        String url = ServerConfig.getCreditDebitUrlWeb(new SharedPrefsManager(getContext()).getString(SharedPreferenceKeys.USER_ID), fromdate, todate);

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
                            CommonUtility.convertJsonToCSV(response, getContext(), "credit_debit_report.csv");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_credit_debit, container, false);
        View view = binding.getRoot();

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        Log.d(TAG, "onCreateView: to:" + todate + " from:" + fromdate);

        binding.fromTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (todate.isEmpty() == false) {

                    Calendar today = Calendar.getInstance();
                    today.setTime(CommonUtility.convertDate(fromdate));
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            getContext(), CreditDebitFragment.this, today.get(Calendar.YEAR),
                            today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
                    isFromDate = true;
                    datePickerDialog.getDatePicker().setMaxDate(getDateObject(todate).getTime());
                    datePickerDialog.show();
                }

            }
        });

        binding.toTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                today.setTime(CommonUtility.convertDate(todate));
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), CreditDebitFragment.this, today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
                isFromDate = false;
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(getDateObject(fromdate).getTime());
                datePickerDialog.show();
            }
        });

        binding.fromTvDate.setText(fromdate);
        binding.toTvDate.setText(todate);

        binding.tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReport();
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                long diff = getDateObject(todate).getTime() - getDateObject(fromdate).getTime();
                int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
                Log.d(TAG, "to -  from : " + diffDays);

                if (diffDays > 7) {
                    promptConfirmationDialog();
                } else {
                    getCreditDebit();
                }

            }
        });

        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });


        return view;
    }

    private void promptConfirmationDialog() {

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_language);
        dialog.setCancelable(false);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        tvTitle.setText("Confirmation");

        TextView tvMessage = (TextView) dialog.findViewById(R.id.tv_message);
        tvMessage.setText("This report will be sent to your registered email address, do you wish to continue?");

        Button btnYes = (Button) dialog.findViewById(R.id.txt_english);
        Button btnNo = (Button) dialog.findViewById(R.id.txt_hindi);

        btnYes.setText("Yes");
        btnNo.setText("No");

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!CommonUtility.isNetworkAvailable(getContext())) {
                    return;
                }

                String url = ServerConfig.newUrl2(ServerConfig.get21DaysReportUrl(fromdate, todate), getContext(), "CreditDebitFragment.class");
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
                                    Log.d(TAG, "onNext: " + e.getMessage());
                                }

                                NotificationReadResponseEntity responseEntity = new Gson().fromJson(response, new TypeToken<NotificationReadResponseEntity>() {
                                }.getType());

                                if (responseEntity != null) {
                                    hideHSandRL();
                                    binding.tvNoDataFound.setText(responseEntity.getMessage());
                                } else {
                                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
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

                dialog.cancel();

            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();

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


    private void reset() {
        binding.fromTvDate.setText(iFrom);
        binding.toTvDate.setText(iTo);
        binding.hs.setVisibility(View.GONE);
        binding.rl.setVisibility(View.GONE);
        binding.tvNoDataFound.setVisibility(View.GONE);

    }

    private void getCreditDebit() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }
        binding.llProgressBar.setVisibility(View.VISIBLE);
        String url = ServerConfig.getCreditDebitUrl(new SharedPrefsManager(getContext()).getString(SharedPreferenceKeys.USER_ID), fromdate, todate);
        //String url = "http://mpartner.luminousintranet.com:81/nonsapservices/api/sapservice/DISTRIBUTOR_CRDR?Discode=1100735&FROMDATE=01/01/2017&TODATE=27/01/2017&token=pass@1234&way=m";


        Log.d(TAG, "getCustomerLedgerReport: " + url);
        apiInterface.getCreditDebitReport(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        try {

                            primaryReportString = responseBody.string();
                            Log.d(TAG, "onNext: " + primaryReportString);

                            Gson gson = new GsonBuilder().create();

                            list = gson.fromJson(primaryReportString, new TypeToken<ArrayList<CreditDebitReportEntity>>() {
                            }.getType());

                            if (primaryReportString.trim().equals("[]")) {
                                hideHSandRL();
                            } else {
                                showHSandRL();
                                CreditDebitReportAdapter adapter = new CreditDebitReportAdapter(getContext(), list);
                                binding.recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                showHSandRL();
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        binding.llProgressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        binding.llProgressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private void hideHSandRL() {

        binding.hs.setVisibility(View.GONE);
        binding.rl.setVisibility(View.GONE);
        binding.tvNoDataFound.setVisibility(View.VISIBLE);
    }

    private void showHSandRL() {

        binding.hs.setVisibility(View.VISIBLE);
        binding.rl.setVisibility(View.VISIBLE);
        binding.tvNoDataFound.setVisibility(View.GONE);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (isFromDate) {

            fromdate = dayOfMonth + "/" + (month + 1) + "/" + year;
            binding.fromTvDate.setText(fromdate);
        } else {

            String orignalTo = todate;

            todate = dayOfMonth + "/" + (month + 1) + "/" + year;

            if (getDateObject(todate).compareTo(getDateObject(fromdate)) < 0) {

                /*TO date less than from date - invalid query*/
                Toast.makeText(getContext(), "Invalid Date Selection", Toast.LENGTH_SHORT).show();
                todate = orignalTo;
                return;
            }

            binding.toTvDate.setText(todate);

        }

        Log.d(TAG, String.format("From : %s\nTo: %s", fromdate, todate));

        /*if (fromdate != null && todate != null) {
            if (!fromdate.isEmpty() && !todate.isEmpty()) {
                getCreditDebit();
            }
        }*/
    }

    private boolean toIsGreaterThanFromDate() {

        if (todate.isEmpty() || fromdate.isEmpty()) {
            Toast.makeText(getContext(), "Select Date", Toast.LENGTH_SHORT).show();
            return false;
        } else {

            SimpleDateFormat sdf = new SimpleDateFormat("DD/MM/YYYY");
            try {
                Date to = sdf.parse(todate);
                Date from = sdf.parse(fromdate);

                if (to.compareTo(from) > 0) {
                    return true;
                } else {

                    return false;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return false;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

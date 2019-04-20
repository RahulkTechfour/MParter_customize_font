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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.CustomerLedgerReportAdapter;
import com.luminous.mpartner.databinding.FragmentCustomerLedgerBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.CustomerLedgerReportEntity;
import com.luminous.mpartner.network.entities.ItemListEntity;
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
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomerLedgerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerLedgerFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int STORAGE_PERMISSION_CODE_3 = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RetrofitInterface apiInterface;
    private FragmentCustomerLedgerBinding binding;
    private static final String TAG = "CustomerLedgerFragment";
    private boolean isFromDate;
    private String fromdate, todate;
    private String primaryReportString;
    List<CustomerLedgerReportEntity> list;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String iFrom, iTo;


    public CustomerLedgerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerLedgerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerLedgerFragment newInstance(String param1, String param2) {
        CustomerLedgerFragment fragment = new CustomerLedgerFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_ledger, container, false);
        View view = binding.getRoot();
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        binding.fromTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                today.add(Calendar.DAY_OF_MONTH, -7);
                Date result = today.getTime();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), CustomerLedgerFragment.this, today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));

/*                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(result.getTime());*/

                isFromDate = true;
                datePickerDialog.getDatePicker().setMaxDate(getDateObject(todate).getTime());
                datePickerDialog.show();

            }
        });

        binding.toTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), CustomerLedgerFragment.this, today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(today.getTimeInMillis());

                isFromDate = false;
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(getDateObject(fromdate).getTime());

                datePickerDialog.show();
            }
        });

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
                } else
                    getCustomerLedgerReport();

            }
        });

        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        binding.fromTvDate.setText(fromdate);
        binding.toTvDate.setText(todate);

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


    private void reset() {

        addApadter(null);
        binding.fromTvDate.setText(iFrom);
        binding.toTvDate.setText(iTo);
        binding.hs.setVisibility(View.GONE);
        binding.rl.setVisibility(View.GONE);
        binding.llProgressBar.setVisibility(View.GONE);
        binding.tvNoDataFound.setVisibility(View.GONE);
    }

    private void hideHSandRL() {

        binding.hs.setVisibility(View.GONE);
        binding.rl.setVisibility(View.GONE);
        binding.llProgressBar.setVisibility(View.GONE);
        binding.tvNoDataFound.setVisibility(View.VISIBLE);
    }

    private void showHSandRL() {

        binding.hs.setVisibility(View.VISIBLE);
        binding.rl.setVisibility(View.VISIBLE);
        binding.tvNoDataFound.setVisibility(View.GONE);
        binding.llProgressBar.setVisibility(View.GONE);

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
                getCustomerLedgerReport();
            }
        }*/

    }


    private void getCustomerLedgerReport() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        binding.llProgressBar.setVisibility(View.VISIBLE);

        String url = ServerConfig.getCustomerLedgerUrl(new SharedPrefsManager(getContext()).getString(SharedPreferenceKeys.USER_ID), fromdate, todate);
//        url = "http://mpartner.luminousintranet.com:81/nonsapservices/api/sapservice/DISTRIBUTOR_LEDGER?Discode=1100735&FROMDATE=01/01/2017&TODATE=27/01/2017&token=pass@1234&way=m";
        Log.d(TAG, "getCustomerLedgerReport: " + url);
        apiInterface.getCustomerLedgerReport(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        binding.llProgressBar.setVisibility(View.GONE);

                        try {

                            primaryReportString = responseBody.string();

                            if (primaryReportString.trim().equals("[]")) {
                                hideHSandRL();
                                return;
                            }
                            Log.d(TAG, "onNext: " + primaryReportString);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new GsonBuilder().create();
                        list = gson.fromJson(primaryReportString, new TypeToken<ArrayList<CustomerLedgerReportEntity>>() {
                        }.getType());

                        Log.d(TAG, "onNext: " + list.size());
                        addApadter(list);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        hideHSandRL();
                        binding.llProgressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private void getWebReport() {
        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }
        CommonUtility.showProgressDialog(getActivity());
        String url = ServerConfig.getCustomerLedgerUrlWeb(new SharedPrefsManager(getContext()).getString(SharedPreferenceKeys.USER_ID), fromdate, todate);

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
                            CommonUtility.convertJsonToCSV(response, getContext(), "customer_ledger_report.csv");
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


    private void addApadter(List<CustomerLedgerReportEntity> list) {

        if (list == null) {
            hideHSandRL();
            return;
        } else if (list.size() == 0) {
            hideHSandRL();
            return;
        }
        showHSandRL();
        CustomerLedgerReportAdapter adapter = new CustomerLedgerReportAdapter(getContext(), list);
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.PrimaryBillingReportAdapter;
import com.luminous.mpartner.databinding.FragmentPrimaryBillingReportBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.ItemListEntity;
import com.luminous.mpartner.network.entities.Notification.NotificationReadResponseEntity;
import com.luminous.mpartner.network.entities.PrimaryBillingReportEntity;
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

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class PrimaryBillingReportFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FragmentPrimaryBillingReportBinding binding;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ArrayList<ItemListEntity> list;
    private String[] allItemList;
    private List<PrimaryBillingReportEntity> listPrimaryBReport;
    private static final String TAG = "PrimaryBillingReport";
    private boolean isFromDate;
    private String fromdate, todate;
    public static final int STORAGE_PERMISSION_CODE_3 = 123;
    private String iFrom, iTo;
    private TextView mTxtReportName;

    public PrimaryBillingReportFragment() {
    }


    public static PrimaryBillingReportFragment newInstance(String param1, String param2) {
        PrimaryBillingReportFragment fragment = new PrimaryBillingReportFragment();
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

    private void reset() {

        binding.fromTvDate.setText(iFrom);
        binding.toTvDate.setText(iTo);
        binding.llProgressBar.setVisibility(View.GONE);
        binding.tvNoDataFound.setVisibility(View.GONE);
        binding.hs.setVisibility(View.GONE);
        binding.rl.setVisibility(View.GONE);
        //getItemType();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTxtReportName = view.findViewById(R.id.txt_report_name);
        mTxtReportName.setText("Primary Billing Report");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_primary_billing_report, container, false);
        View view = binding.getRoot();
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        binding.fromTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                today.setTime(CommonUtility.convertDate(fromdate));
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), PrimaryBillingReportFragment.this, today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));

                isFromDate = true;

                datePickerDialog.getDatePicker().setMaxDate(getDateObject(todate).getTime());


                datePickerDialog.show();

            }
        });

        binding.toTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                today.setTime(CommonUtility.convertDate(todate));
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), PrimaryBillingReportFragment.this, today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));


                isFromDate = false;
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(getDateObject(fromdate).getTime());

                datePickerDialog.show();
            }
        });

        binding.spinnerAllitems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (list != null) {
                    String text = list.get(position).getItemName();
                    if (!text.equals("All"))
                        itemTypeSelected(text);
                    else {
                        if (listPrimaryBReport != null)
                            updatePrimaryReportAdapter(listPrimaryBReport);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                    getPrimaryReport();
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

        getItemType();
        binding.tvNoDataFound.setVisibility(View.GONE);


        return view;
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
        String url = ServerConfig.getPrimaryBillingReportUrlWeb(
                new SharedPrefsManager(getContext()).getString(SharedPreferenceKeys.USER_ID),
                fromdate, todate);

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
                            CommonUtility.convertJsonToCSV(response, getContext(), "primary_billing_report.csv");
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

    private void hideHSandRL() {

        binding.hs.setVisibility(View.GONE);
        binding.rl.setVisibility(View.GONE);
        binding.tvNoDataFound.setVisibility(View.VISIBLE);
        binding.llProgressBar.setVisibility(View.GONE);
    }

    private void showHSandRL() {

        binding.hs.setVisibility(View.VISIBLE);
        binding.rl.setVisibility(View.VISIBLE);
        binding.tvNoDataFound.setVisibility(View.GONE);
        binding.llProgressBar.setVisibility(View.GONE);

    }

    private void itemTypeSelected(String text) {

        List<PrimaryBillingReportEntity> list = new ArrayList<>();

        if (listPrimaryBReport != null) {
            for (PrimaryBillingReportEntity entity : listPrimaryBReport) {
                if (entity.getDIVISION().equals(text)) {
                    list.add(entity);
                } else if (text.contains("UPS") && entity.getDIVISION().contains("UPS")) {
                    list.add(entity);
                }
            }
        }
        updatePrimaryReportAdapter(list);
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


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    String primaryReportString;

    private void getPrimaryReport() {
        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        binding.llProgressBar.setVisibility(View.VISIBLE);
        binding.tvNoDataFound.setVisibility(View.GONE);
        hideHSandRL();

        String url = ServerConfig.getPrimaryBillingReportUrl(
                new SharedPrefsManager(getContext()).getString(SharedPreferenceKeys.USER_ID),
                fromdate, todate);

        //url = "http://mpartner.luminousintranet.com:81/nonsapservices/api/sapservice/PRIMARY_RECV_REPORT?Discode=1100735&FROMDATE=01/01/2017&TODATE=27/01/2017&token=pass@1234&way=m&Item=1";

        Log.d(TAG, "getPrimaryReport: " + url);

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

                        primaryReportString = null;
                        try {
                            primaryReportString = responseBody.string();
                            /*primaryReportString = "[{\n" +
                                    "\t\"INV_NO\": \"0090005122\",\n" +
                                    "\t\"INVOICE_DA\": \"2017-01-27\",\n" +
                                    "\t\"ITEM_CODE\": \"T30000000085\",\n" +
                                    "\t\"MATERIAL_DESC\": \"Solar Pack 40L BAT 1+40Wp PPNL 1+10ACC1\",\n" +
                                    "\t\"INV_QTY\": \"0.000\",\n" +
                                    "\t\"AMOUNT\": \"59627.95\",\n" +
                                    "\t\"DIVISION\": \"\"\n" +
                                    "}]";*/
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        listPrimaryBReport = new Gson().fromJson(primaryReportString,
                                new TypeToken<List<PrimaryBillingReportEntity>>() {
                                }.getType());
                        Log.d(TAG, "pBR: " + primaryReportString);
                        binding.llProgressBar.setVisibility(View.GONE);
                        updatePrimaryReportAdapter(listPrimaryBReport);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        binding.llProgressBar.setVisibility(View.GONE);
                        hideHSandRL();
                        binding.tvNoDataFound.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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


    private void getItemType() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.getItemTypeURL();
        apiInterface.baseRetrofitInterface(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }


                    @Override
                    public void onNext(ResponseBody response) {

                        String itemTypeString = null;
                        try {
                            itemTypeString = response.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        list = new Gson().fromJson(itemTypeString, new TypeToken<List<ItemListEntity>>() {
                        }.getType());
                        allItemList = new String[list.size()];

                        for (int i = 0; i < list.size(); i++) {
                            allItemList[i] = list.get(i).getItemName();
                        }
                        ArrayAdapter<CharSequence> allItemSpinnerAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, allItemList);
                        allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                        binding.spinnerAllitems.setAdapter(allItemSpinnerAdapter);


                    }


                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        //hideHSandRL();

                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog(TAG, "onComplete");
                    }
                });
    }

    private void updatePrimaryReportAdapter(List<PrimaryBillingReportEntity> listPrimaryBReport) {

        if (listPrimaryBReport == null) {
            hideHSandRL();
            return;
        } else if (listPrimaryBReport.size() == 0) {
            hideHSandRL();
            return;
        }

        Log.d(TAG, "updatePrimaryReportAdapter: not null");

        showHSandRL();
        PrimaryBillingReportAdapter adapter = new PrimaryBillingReportAdapter(getContext(), listPrimaryBReport);
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

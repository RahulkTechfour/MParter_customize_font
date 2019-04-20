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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.SecondaryDispatchReportAdapter;
import com.luminous.mpartner.databinding.FragmentSecondaryDispatchReportBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.ItemListEntity;
import com.luminous.mpartner.network.entities.Notification.NotificationReadResponseEntity;
import com.luminous.mpartner.network.entities.SecondaryDispatchReportEntity;
import com.luminous.mpartner.network.entities.WarrantyReport.DealerListEntity;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

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
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SecondaryDispatchReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondaryDispatchReportFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int STORAGE_PERMISSION_CODE_3 = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RetrofitInterface apiInterface;
    private String[] allItemList;
    private ArrayList<ItemListEntity> allItem;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FragmentSecondaryDispatchReportBinding binding;
    private Context context;
    private boolean isFromDate;
    private String fromdate, todate;
    private static final String TAG = "SecondaryDispatchReport";
    private String userId;
    private String itemId, dealer;
    private SharedPrefsManager sharedPrefsManager;
    private String primaryReportString;
    List<SecondaryDispatchReportEntity> dispatchList;
    private String iFrom, iTo;
    private ArrayList<DealerListEntity> dealerListEntities;


    public SecondaryDispatchReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondaryDispatchReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondaryDispatchReportFragment newInstance(String param1, String param2) {
        SecondaryDispatchReportFragment fragment = new SecondaryDispatchReportFragment();
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

            String[] fd = fromdate.split("/");
            String[] td = todate.split("/");
            iFrom = fromdate;
            iTo = todate;
            fromdate = fd[1] + "/" + fd[0] + "/" + fd[2];
            todate = td[1] + "/" + td[0] + "/" + td[2];
        }
    }

    private void reset() {
        binding.fromTvDate.setText(iFrom);
        binding.toTvDate.setText(iTo);
        updateRVAdpater(null);
        binding.tvNoDataFound.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_secondary_dispatch_report, container, false);
        View view = binding.getRoot();
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);
        sharedPrefsManager = new SharedPrefsManager(context);
        userId = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);
        itemId = "0";

        getItemTypeData();
        getDealerList();
        binding.llProgressBar.setVisibility(View.GONE);

        binding.fromTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                today.add(Calendar.DAY_OF_MONTH, -7);
                Date result = today.getTime();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, SecondaryDispatchReportFragment.this, today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));

                /*datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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
                        context, SecondaryDispatchReportFragment.this, today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(today.getTimeInMillis());

                isFromDate = false;
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(getDateObject(fromdate).getTime());

                datePickerDialog.show();
            }
        });

        binding.spinnerAllitems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                itemId = String.valueOf(position);
                //getSecondaryDispatchReport(userId, "", itemId, fromdate, todate);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        binding.tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReport();
            }
        });

        binding.fromTvDate.setText(iFrom);
        binding.toTvDate.setText(iTo);

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long diff = getDateObject(todate).getTime() - getDateObject(fromdate).getTime();
                int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
                Log.d(TAG, "to -  from : " + diffDays);

                if (diffDays > 7) {
                    promptConfirmationDialog();
                } else
                    getSecondaryDispatchReport(sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID), dealer, itemId, fromdate, todate);
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

    private void getDealerList() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.getDealerList(new SharedPrefsManager(getContext()).getString(SharedPreferenceKeys.USER_ID));
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

    public void getItemTypeData() {

        String url1 = ServerConfig.getItemTypeURL();

        Log.d(TAG, "getItemTypeData: " + url1);
        apiInterface.getItemTypeData(url1)
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

                            allItem = new Gson().fromJson(responseBody.string(), new TypeToken<List<ItemListEntity>>() {
                            }.getType());
                            allItemList = new String[allItem.size()];

                            for (int i = 0; i < allItem.size(); i++) {
                                allItemList[i] = allItem.get(i).getItemName();
                            }

                            ArrayAdapter<CharSequence> allItemSpinnerAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, allItemList);
                            allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                            binding.spinnerAllitems.setAdapter(allItemSpinnerAdapter);


                        } catch (Exception e) {
                            e.printStackTrace();
                            hideHSandRL();
                            Log.d(TAG, "getItemTypeData: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideHSandRL();
                        Log.d(TAG, "getItemTypeData: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private Date getDateObject(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
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
            fromdate = (month + 1) + "/" + dayOfMonth + "/" + year;
            binding.fromTvDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        } else {
            String orignalTo = todate;

            todate = (month + 1) + "/" + dayOfMonth + "/" + year;
            if (getDateObject(todate).compareTo(getDateObject(fromdate)) < 0) {
                /*TO date less than from date - invalid query*/
                Toast.makeText(getContext(), "Invalid Date Selection", Toast.LENGTH_SHORT).show();
                todate = orignalTo;
                return;
            }

            binding.toTvDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }

        Log.d(TAG, String.format("From : %s\nTo: %s", fromdate, todate));

        /*if (fromdate != null && todate != null) {
            if (!fromdate.isEmpty() && !todate.isEmpty()) {
                getSecondaryDispatchReport(userId, "", itemId, fromdate, todate);
            }
        }*/
    }

    private void getSecondaryDispatchReport(String userId, String dealer, String itemId, String from, String to) {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        String url = ServerConfig.getSecondaryDispatchReport(userId, dealer, itemId, from, to);
        //url = "http://mpartner.luminousintranet.com:81/nonsapservices/api/nonsapservice/sscDistachedReportDataPopUp?SessionLoginDisID=001108220&DlrID=0&ItemID=0&DispatchedDateFrom=1/1/2019&DispatchedDateTo=1/31/2019&token=pass@1234&way=m";

        binding.llProgressBar.setVisibility(View.VISIBLE);

        Log.d(TAG, "getSecondaryDispatchReport: " + url);


        Log.d(TAG, "SecondaryDispatchReport : " + url);
        apiInterface.getSecondaryDispatchReport(url)
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


                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "onNext: " + e.getMessage());
                        }


                        Gson gson = new GsonBuilder().create();
                        dispatchList = gson.fromJson(primaryReportString, new TypeToken<ArrayList<SecondaryDispatchReportEntity>>() {
                        }.getType());

                        Log.d(TAG, "onNext: " + dispatchList.size());
                        updateRVAdpater(dispatchList);

                        binding.llProgressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.llProgressBar.setVisibility(View.GONE);
                        hideHSandRL();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

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

        CommonUtility.showProgressDialog(context);
        String url = ServerConfig.getSecondaryDispatchReportWeb(sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID), dealer, itemId, fromdate, todate);

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
                            CommonUtility.convertJsonToCSV(response, getContext(), "secondary_dispatch_report.csv");
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

    private void updateRVAdpater(List<SecondaryDispatchReportEntity> dispatchList) {

        if (dispatchList == null) {
            hideHSandRL();
            return;
        } else if (dispatchList.size() == 0) {
            hideHSandRL();
            return;
        } else {
            Log.d(TAG, "updateRVAdpater: not null");
        }

        showHSandRL();
        SecondaryDispatchReportAdapter adapter = new SecondaryDispatchReportAdapter(context, dispatchList);
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        binding.llProgressBar.setVisibility(View.GONE);

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

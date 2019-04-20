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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.PrimaryReportsAdapter;
import com.luminous.mpartner.databinding.FragmentPrimaryReportBinding;
import com.luminous.mpartner.fragments.BaseFragment;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.ItemListEntity;
import com.luminous.mpartner.network.entities.Notification.NotificationReadResponseEntity;
import com.luminous.mpartner.network.entities.PrimaryReportEntity;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static android.provider.Settings.System.DATE_FORMAT;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrimaryReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrimaryReportFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PrimaryReportFragment";
    private static final int STORAGE_PERMISSION_CODE_3 = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentPrimaryReportBinding binding;
    private Context context;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    String[] allItemList;
    List<ItemListEntity> list;
    String clickedItem = "0";
    String fromdate, todate;
    boolean isFromDate = false;
    List<PrimaryReportEntity> listPrimaryReport;
    private String primaryReportString;

    public PrimaryReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrimaryReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PrimaryReportFragment newInstance(String param1, String param2) {
        PrimaryReportFragment fragment = new PrimaryReportFragment();
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
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_primary_report, container, false);
        View view = binding.getRoot();

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        binding.fromTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                today.add(Calendar.DAY_OF_MONTH, -7);
                Date result = today.getTime();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, PrimaryReportFragment.this, today.get(Calendar.YEAR),
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
                        context, PrimaryReportFragment.this, today.get(Calendar.YEAR),
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

                String text = list.get(position).getItemName();
                if (!text.equals("All"))
                    itemTypeSelected(text);
                else {
                    updatePrimaryReportAdapter(listPrimaryReport);
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

        binding.fromTvDate.setText(fromdate);
        binding.toTvDate.setText(todate);



        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getItemTypeAndPrimaryReport();
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



   /* private void promptConfirmationDialog() {

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

                String url = ServerConfig.newUrl2(ServerConfig.get21DaysReportUrl(fromdate, todate), getContext(), "CreditDebitFragment.class");
                Log.d(TAG, "url: "+url);
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
                                try{
                                    response = responseBody.string();
                                    Log.d(TAG, "onNext: "+response);
                                }catch (Exception e){
                                    Log.d(TAG, "onNext: "+e.getMessage());
                                }


                                NotificationReadResponseEntity responseEntity = new Gson().fromJson(response, new TypeToken<NotificationReadResponseEntity>(){}.getType());
                                if (responseEntity != null){
                                    hideHSandRL();
                                    binding.tvNoDataFound.setText(responseEntity.getMessage());

                                } else{
                                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: "+e.getMessage());
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

    }*/

    private String iFrom, iTo;

    private void reset() {

        updatePrimaryReportAdapter(null);
        binding.fromTvDate.setText(iFrom);
        binding.toTvDate.setText(iTo);
        getItemTypeAndPrimaryReport();

    }


    private void itemTypeSelected(String type) {

        List<PrimaryReportEntity> list = new ArrayList<>();

        for (PrimaryReportEntity item : listPrimaryReport) {

            Log.d(TAG, "itemTypeSelected: " + item.getDIVISION());

            if (item.getDIVISION().equals(type)) {
                list.add(item);
            }
        }

        updatePrimaryReportAdapter(list);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

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

       /* if (fromdate != null && todate != null) {
            if (!fromdate.isEmpty() && !todate.isEmpty()) {
                getItemTypeAndPrimaryReport();
            }
        }*/

    }

    public Observable<Pair<ResponseBody, ResponseBody>> getItemTypeAndProductReport(String todate, String fromdate) {

        String url1 = ServerConfig.getItemTypeURL();
        String url2 = ServerConfig.getPrimaryReport(new SharedPrefsManager(getContext()).getString(SharedPreferenceKeys.USER_ID), fromdate, todate);

        Log.d(TAG, "url1: " + url1);
        Log.d(TAG, "url2: " + url2);

        //url2 = "http://mpartner.luminousintranet.com:81/nonsapservices/api/sapservice/PRIMARY_RECV_REPORT?Discode=1100735&FROMDATE=01/01/2017&TODATE=27/01/2017&token=pass@1234&way=m";

        Observable<ResponseBody> itemObservable = apiInterface.getItemTypeData(url1);
        Observable<ResponseBody> primaryReportObservable = apiInterface.getPrimaryReport(url2);

        return itemObservable.zipWith(primaryReportObservable, Pair::new);
    }

    private void getItemTypeAndPrimaryReport() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        binding.llProgressBar.setVisibility(View.VISIBLE);

        getItemTypeAndProductReport(todate, fromdate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<ResponseBody, ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }


                    @Override
                    public void onNext(Pair<ResponseBody, ResponseBody> response) {
                        dismissProgressDialog();

                        String itemTypeString = null;
                        String primaryReportString = null;
                        try {
                            itemTypeString = response.first.string();
                            primaryReportString = response.second.string();

                            Log.d(TAG, "onNext: " + primaryReportString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        Gson gson = new GsonBuilder().create();
                        list = gson.fromJson(itemTypeString, new TypeToken<ArrayList<ItemListEntity>>() {
                        }.getType());
                        allItemList = new String[list.size()];

                        for (int i = 0; i < list.size(); i++) {
                            allItemList[i] = list.get(i).getItemName();
                        }
                        ArrayAdapter<CharSequence> allItemSpinnerAdapter = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text, allItemList);
                        allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                        binding.spinnerAllitems.setAdapter(allItemSpinnerAdapter);

                        gson = new GsonBuilder().create();
                        PrimaryReportFragment.this.primaryReportString = primaryReportString;
                        listPrimaryReport = gson.fromJson(primaryReportString, new TypeToken<ArrayList<PrimaryReportEntity>>() {
                        }.getType());

                        updatePrimaryReportAdapter(listPrimaryReport);
                        binding.llProgressBar.setVisibility(View.GONE);

                    }


                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        CommonUtility.printLog(TAG, "onError");
                        Log.d(TAG, "onError: " + e.getMessage());
                        e.printStackTrace();
                        binding.llProgressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog(TAG, "onComplete");
                    }
                });

    }

    private void updatePrimaryReportAdapter(List<PrimaryReportEntity> listPrimaryReport) {
        PrimaryReportsAdapter adapter = new PrimaryReportsAdapter(context, listPrimaryReport);
        binding.recyclerView.setAdapter(adapter);

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
        String url = ServerConfig.getPrimaryReportWeb(new SharedPrefsManager(getContext()).getString(SharedPreferenceKeys.USER_ID), fromdate, todate);
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
                            CommonUtility.convertJsonToCSV(response, getContext(), "primary_report.csv");
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

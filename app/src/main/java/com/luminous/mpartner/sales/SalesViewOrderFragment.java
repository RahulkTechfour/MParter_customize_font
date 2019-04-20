package com.luminous.mpartner.sales;


import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.ViewSalesOrderAdapter;
import com.luminous.mpartner.databinding.FragmentSalesViewOrderBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.ChannelListEntity;
import com.luminous.mpartner.network.entities.DivisionListEntity;
import com.luminous.mpartner.network.entities.ViewSalesOrderResponseEntity;
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

public class SalesViewOrderFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FragmentSalesViewOrderBinding binding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String TAG = "SalesViewOrderFragment";
    private ArrayList<ViewSalesOrderResponseEntity> list;
    private String fromDate, toDate;
    private RetrofitInterface apiInterface;
    private boolean isFromDate;
    private String selectedDiv, selectedCh;
    List<DivisionListEntity> divList;
    List<ChannelListEntity> chList;


    public SalesViewOrderFragment() {
    }


    public static SalesViewOrderFragment newInstance(String param1, String param2) {
        SalesViewOrderFragment fragment = new SalesViewOrderFragment();
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

            fromDate = mParam1;
            toDate = mParam2;
        }

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales_view_order, container, false);
        getChannelDivList();


        binding.fromTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                today.add(Calendar.DAY_OF_MONTH, -7);
                Date result = today.getTime();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), SalesViewOrderFragment.this, today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMaxDate(getDateObject(toDate).getTime());

                isFromDate = true;
                datePickerDialog.show();

            }
        });
        binding.toTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), SalesViewOrderFragment.this, today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(getDateObject(fromDate).getTime());
                isFromDate = false;
                datePickerDialog.show();
            }
        });

        binding.fromTvDate.setText(fromDate);
        binding.toTvDate.setText(toDate);

        binding.spinnerDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (divList != null)
                        selectedDiv = divList.get(position).getDivisionName();
                    else
                        selectedDiv = null;
                }catch(Exception ex){

                }


                if (divList != null && position > 0)
                    selectedDiv = divList.get(position).getDivisionName();
                else
                    selectedDiv = null;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spinnerChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (selectedCh != null)

                if (selectedCh != null && position > 0)

                    selectedCh = chList.get(position).getChannelName();
                else
                    selectedCh = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSalesViewOrder();
                binding.llPreOrder.setVisibility(View.VISIBLE);
                binding.tvFromToDate.setText(fromDate + " - " + toDate);
            }
        });

        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView mTxtOrderView = view.findViewById(R.id.txt_view_order);
        SpannableString mySpannableString = new SpannableString("View Orders");
        mySpannableString.setSpan(new UnderlineSpan(), 0, 4, 0);
        mTxtOrderView.setText(mySpannableString);
    }

    private void reset() {

        binding.spinnerChannel.setSelection(0);
        binding.spinnerDivision.setSelection(0);

        binding.toTvDate.setText(mParam2);
        binding.fromTvDate.setText(mParam1);
        binding.llPreOrder.setVisibility(View.GONE);
        binding.tvFromToDate.setText("");
        dataFound(false, false);
        binding.tvNoDataFound.setText("");

    }

    private void getChannelDivList() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url1 = ServerConfig.getChannelList();
        String url2 = ServerConfig.getDivisionList();
        Observable<ResponseBody> channelObs = apiInterface.getChannelList(url1);
        Observable<ResponseBody> divisionObs = apiInterface.getDivisonList(url2);


        channelObs.zipWith(divisionObs, Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<ResponseBody, ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }


                    @Override
                    public void onNext(Pair<ResponseBody, ResponseBody> response) {

                        String channelListString = null;
                        String divisionListString = null;
                        try {
                            channelListString = response.first.string();
                            divisionListString = response.second.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!channelListString.isEmpty())
                            inflateChannelListSpinner(channelListString);
                        else Log.d(TAG, "Channel String : null");

                        if (!divisionListString.isEmpty())
                            inflateDivisionListSpinner(divisionListString);
                        else Log.d(TAG, "Division String : null");

                    }


                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog(TAG, "onComplete");
                    }
                });
    }


    private void inflateChannelListSpinner(String channelListString) {

        chList = new Gson().fromJson(channelListString, new TypeToken<List<ChannelListEntity>>() {
        }.getType());
        String[] chListArray = new String[chList.size() + 1];
        chListArray[0] = "Select Channel";
        chList.add(0, null);

        for (int i = 1; i < chList.size(); i++) {
            chListArray[i] = chList.get(i).getChannelName();
        }

        ArrayAdapter<CharSequence> allItemSpinnerAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, chListArray);
        allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        binding.spinnerChannel.setAdapter(allItemSpinnerAdapter);

    }


    private void inflateDivisionListSpinner(String divListString) {

        divList = new Gson().fromJson(divListString, new TypeToken<List<DivisionListEntity>>() {
        }.getType());
        String[] divListArray = new String[divList.size() + 1];

        divListArray[0] = "Select Division";
        divList.add(0, null);
        for (int i = 1; i < divList.size(); i++) {
            divListArray[i] = divList.get(i).getDivisionName();
        }


        ArrayAdapter<CharSequence> allItemSpinnerAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, divListArray);
        allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        binding.spinnerDivision.setAdapter(allItemSpinnerAdapter);

    }

    private void getSalesViewOrder() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        dataFound(true, false);
        String url = ServerConfig.getSalesViewOrder(new SharedPrefsManager(getContext()).getString(SharedPreferenceKeys.USER_ID), fromDate, toDate);
        Log.d(TAG, "getSalesViewOrder: " + url);
        //String url = "http://mpartner.luminousintranet.com:81/nonsapservices/api/sapservice/DISTRIBUTOR_SALES_ORDER?Discode=1100735&FROMDATE=01/01/2017&TODATE=27/01/2017&token=pass@1234&way=m";

        apiInterface.getSalesViewOrder(url)
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

                            String response = responseBody.string();
                            Log.d(TAG, "onNext: " + response);

                            //response = "[{\"SALES_ORDE\":\"0000578100\",\"ORDER_DATE\":\"2017-01-23\",\"SALE\":\"LPTL\",\"SALE1\":\"ZTRV\",\"DI\":\"06\",\"DI1\":\"35\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F04220015001\",\"MATERIAL_DESC\":\"S/W UPS 2000/24V LUMINOUS CRUZE REV.01\",\"QUANTITY\":\"10.000\",\"NET_PRICE\":\"7400.00\",\"NET_VALUE\":\"73999.9900\",\"STATUS\":\"Completed\",\"DELIVERY_D\":\"2017-01-24\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"},{\"SALES_ORDE\":\"0000578100\",\"ORDER_DATE\":\"2017-01-23\",\"SALE\":\"LPTL\",\"SALE1\":\"ZTRV\",\"DI\":\"06\",\"DI1\":\"35\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F03225111251\",\"MATERIAL_DESC\":\"S/Q UPS LUM PRO UPS 2250 AL TX. 1 SOCKET\",\"QUANTITY\":\"5.000\",\"NET_PRICE\":\"5271.11\",\"NET_VALUE\":\"26355.5500\",\"STATUS\":\"Completed\",\"DELIVERY_D\":\"2017-01-24\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"},{\"SALES_ORDE\":\"0000578099\",\"ORDER_DATE\":\"2017-01-23\",\"SALE\":\"LPTL\",\"SALE1\":\"ZTRV\",\"DI\":\"10\",\"DI1\":\"30\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F03110650551\",\"MATERIAL_DESC\":\"S/Q WAVE UPS 1065 LUMINOUS ELECTRA V1\",\"QUANTITY\":\"33.000\",\"NET_PRICE\":\"3631.11\",\"NET_VALUE\":\"119826.6500\",\"STATUS\":\"Completed\",\"DELIVERY_D\":\"2017-01-24\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"},{\"SALES_ORDE\":\"0000578099\",\"ORDER_DATE\":\"2017-01-23\",\"SALE\":\"LPTL\",\"SALE1\":\"ZTRV\",\"DI\":\"10\",\"DI1\":\"30\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F03110650551\",\"MATERIAL_DESC\":\"S/Q WAVE UPS 1065 LUMINOUS ELECTRA V1\",\"QUANTITY\":\"7.000\",\"NET_PRICE\":\"0.00\",\"NET_VALUE\":\"0.0000\",\"STATUS\":\"Completed\",\"DELIVERY_D\":\"2017-01-24\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"},{\"SALES_ORDE\":\"0000578096\",\"ORDER_DATE\":\"2017-01-23\",\"SALE\":\"LPTL\",\"SALE1\":\"ZORT\",\"DI\":\"05\",\"DI1\":\"10\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F1139BZA0150\",\"MATERIAL_DESC\":\"LUM 12V150AH EC 18036 FNSD BTY V16\",\"QUANTITY\":\"10.000\",\"NET_PRICE\":\"9245.01\",\"NET_VALUE\":\"92450.1200\",\"STATUS\":\"Completed\",\"DELIVERY_D\":\"2017-01-24\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"},{\"SALES_ORDE\":\"0000578096\",\"ORDER_DATE\":\"2017-01-23\",\"SALE\":\"LPTL\",\"SALE1\":\"ZORT\",\"DI\":\"05\",\"DI1\":\"10\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F1102CZM0150\",\"MATERIAL_DESC\":\"LUM 12V150AH ILTT 18048N FNSD BTY V16\",\"QUANTITY\":\"10.000\",\"NET_PRICE\":\"9590.01\",\"NET_VALUE\":\"95900.1300\",\"STATUS\":\"Completed\",\"DELIVERY_D\":\"2017-01-24\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"},{\"SALES_ORDE\":\"0000577164\",\"ORDER_DATE\":\"2017-01-17\",\"SALE\":\"LPTL\",\"SALE1\":\"ZTRV\",\"DI\":\"06\",\"DI1\":\"35\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F04220015001\",\"MATERIAL_DESC\":\"S/W UPS 2000/24V LUMINOUS CRUZE REV.01\",\"QUANTITY\":\"20.000\",\"NET_PRICE\":\"7400.00\",\"NET_VALUE\":\"147999.9800\",\"STATUS\":\"Not Delivered\",\"DELIVERY_D\":\"0000-00-00\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"},{\"SALES_ORDE\":\"0000577163\",\"ORDER_DATE\":\"2017-01-17\",\"SALE\":\"LPTL\",\"SALE1\":\"ZTRV\",\"DI\":\"05\",\"DI1\":\"30\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F03110515701\",\"MATERIAL_DESC\":\"S/Q UPS 1050/12V LUMINOUS ECO WATT UPS\",\"QUANTITY\":\"33.000\",\"NET_PRICE\":\"3586.67\",\"NET_VALUE\":\"118359.9800\",\"STATUS\":\"Not Delivered\",\"DELIVERY_D\":\"0000-00-00\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"},{\"SALES_ORDE\":\"0000577163\",\"ORDER_DATE\":\"2017-01-17\",\"SALE\":\"LPTL\",\"SALE1\":\"ZTRV\",\"DI\":\"05\",\"DI1\":\"30\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F03110515701\",\"MATERIAL_DESC\":\"S/Q UPS 1050/12V LUMINOUS ECO WATT UPS\",\"QUANTITY\":\"7.000\",\"NET_PRICE\":\"0.00\",\"NET_VALUE\":\"0.0000\",\"STATUS\":\"Not Delivered\",\"DELIVERY_D\":\"0000-00-00\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"},{\"SALES_ORDE\":\"0000577162\",\"ORDER_DATE\":\"2017-01-17\",\"SALE\":\"LPTL\",\"SALE1\":\"ZORT\",\"DI\":\"05\",\"DI1\":\"10\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F1139BZA0150\",\"MATERIAL_DESC\":\"LUM 12V150AH EC 18036 FNSD BTY V16\",\"QUANTITY\":\"10.000\",\"NET_PRICE\":\"9220.00\",\"NET_VALUE\":\"92200.0200\",\"STATUS\":\"Not Delivered\",\"DELIVERY_D\":\"0000-00-00\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"},{\"SALES_ORDE\":\"0000577162\",\"ORDER_DATE\":\"2017-01-17\",\"SALE\":\"LPTL\",\"SALE1\":\"ZORT\",\"DI\":\"05\",\"DI1\":\"10\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F1102CZM0150\",\"MATERIAL_DESC\":\"LUM 12V150AH ILTT 18048N FNSD BTY V16\",\"QUANTITY\":\"10.000\",\"NET_PRICE\":\"9565.00\",\"NET_VALUE\":\"95650.0300\",\"STATUS\":\"Not Delivered\",\"DELIVERY_D\":\"0000-00-00\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"},{\"SALES_ORDE\":\"0000577162\",\"ORDER_DATE\":\"2017-01-17\",\"SALE\":\"LPTL\",\"SALE1\":\"ZORT\",\"DI\":\"05\",\"DI1\":\"10\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F1103NXT0150\",\"MATERIAL_DESC\":\"LUM 12V 150AH ILST 1842 FNSD BTY\",\"QUANTITY\":\"10.000\",\"NET_PRICE\":\"7490.00\",\"NET_VALUE\":\"74900.0200\",\"STATUS\":\"Not Delivered\",\"DELIVERY_D\":\"0000-00-00\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"},{\"SALES_ORDE\":\"0000577162\",\"ORDER_DATE\":\"2017-01-17\",\"SALE\":\"LPTL\",\"SALE1\":\"ZORT\",\"DI\":\"05\",\"DI1\":\"10\",\"PLAN\":\"1010\",\"CUSTOMER\":\"0001100735\",\"CUS_NAME\":\"KAMAL ELECTRICAL TRADERS\",\"PO_NUMBER\":\"email\",\"PO_ITE\":\"\",\"MATERIAL_NUMBER\":\"F1103AYK0135\",\"MATERIAL_DESC\":\"LUM 12V135AH IL16039FP FNSD BTY V16\",\"QUANTITY\":\"100.000\",\"NET_PRICE\":\"6790.00\",\"NET_VALUE\":\"679000.1800\",\"STATUS\":\"Not Delivered\",\"DELIVERY_D\":\"0000-00-00\",\"CREATED\":\"10905489\",\"CREATED_NAME\":\"PINKY GUPTA\"}]";


                            Gson gson = new GsonBuilder().create();

                            list = gson.fromJson(response, new TypeToken<ArrayList<ViewSalesOrderResponseEntity>>() {
                            }.getType());

                            if (list == null) {

                                dataFound(false, false);

                            } else if (list.size() == 0) {
                                dataFound(false, false);
                            } else {
                                dataFound(false, true);
                                ViewSalesOrderAdapter adapter = new ViewSalesOrderAdapter(getContext(), list);
                                binding.recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        dataFound(false, false);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

    }

    private void dataFound(boolean loading, boolean datafound) {

        if (loading) {
            binding.llPreOrder.setVisibility(View.GONE);
            binding.llPgTv.setVisibility(View.VISIBLE);
            binding.tvNoDataFound.setVisibility(View.GONE);
            binding.pg.setVisibility(View.VISIBLE);
        } else {
            if (datafound) {
                binding.llPreOrder.setVisibility(View.VISIBLE);
                binding.llPgTv.setVisibility(View.GONE);
            } else {
                binding.llPreOrder.setVisibility(View.GONE);
                binding.llPgTv.setVisibility(View.VISIBLE);
                binding.pg.setVisibility(View.GONE);
                binding.tvNoDataFound.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        if (isFromDate) {
            fromDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            binding.fromTvDate.setText(fromDate);
        } else {
            toDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            binding.toTvDate.setText(toDate);
        }
        Log.d(TAG, String.format("From : %s\nTo: %s", fromDate, toDate));

    }
}

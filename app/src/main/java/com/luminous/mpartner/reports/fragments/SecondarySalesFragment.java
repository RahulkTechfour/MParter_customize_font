package com.luminous.mpartner.reports.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.SecondarySalesReportAdapter;
import com.luminous.mpartner.databinding.FragmentSecondarySalesBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.ItemListEntity;
import com.luminous.mpartner.network.entities.Notification.NotificationReadResponseEntity;
import com.luminous.mpartner.network.entities.PrimaryReportEntity;
import com.luminous.mpartner.network.entities.SecondarySalesQuantitySummaryEntity;
import com.luminous.mpartner.network.entities.SecondarySalesReportEntity;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
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
 * Use the {@link SecondarySalesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondarySalesFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int STORAGE_PERMISSION_CODE_3 = 1;

    private SharedPrefsManager db;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentSecondarySalesBinding binding;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String TAG = "SecondarySalesFragment";
    private List<SecondarySalesReportEntity> list;
    private String[] allItemList;
    private String itemId = "1";
    private List<ItemListEntity> allItem;
    private String primaryReportString;

    public SecondarySalesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondarySalesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondarySalesFragment newInstance(String param1, String param2) {
        SecondarySalesFragment fragment = new SecondarySalesFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_secondary_sales, container, false);
        View view = binding.getRoot();
        db = new SharedPrefsManager(getContext());

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        getItemTypeData();

        binding.spinnerAllitems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (allItemList != null) {
                    String text = allItemList[position];
                    if (!text.equals("All"))
                        itemTypeSelected(text);
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

        binding.btnSubmit.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);


        return view;
    }

    private void hideLayout() {
        binding.llStock.setVisibility(View.GONE);
        binding.hs.setVisibility(View.GONE);
        binding.tvNoDataFound.setVisibility(View.VISIBLE);
        binding.rl.setVisibility(View.GONE);
    }

    private void showLayout() {
        binding.llStock.setVisibility(View.VISIBLE);
        binding.hs.setVisibility(View.VISIBLE);
        binding.tvNoDataFound.setVisibility(View.GONE);
        binding.rl.setVisibility(View.VISIBLE);
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

    private void itemTypeSelected(String text) {

        for (ItemListEntity item : allItem) {
            if (item.getItemName().equals(text)) {
                itemId = item.getItem();
                getSecQSummaryAndReport();
            }
        }

    }


    public void getItemTypeData() {

        String url1 = ServerConfig.getItemTypeURL();
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


                            //getSecQSummaryAndReport();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        hideLayout();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<Pair<ResponseBody, ResponseBody>> getSecQSummaryAndReport(String userId, String itemId) {

        //userId = "001108220";
        String url1 = String.format(ServerConfig.getSecSaleQuantitySummary(), userId);
        String url2 = String.format(ServerConfig.getSecondarySalesReport(), userId, itemId);
        Log.d(TAG, "url1 : " + url1);
        Log.d(TAG, "url2 : " + url2);
        //url2 = "http://mpartner.luminousintranet.com:81/nonsapservices/api/nonsapservice/sscDispatchableStockListPopUp?SessionLoginDisID=001108220&ItemID=1&token=pass@1234";

        Observable<ResponseBody> itemObservable = apiInterface.getSecondarySaleQuantitySummaryReport(url1);
        Observable<ResponseBody> secondarySaleReport = apiInterface.getSecondarySaleReport(url2);

        return itemObservable.zipWith(secondarySaleReport, Pair::new);
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

            if (primaryReportString != null)
                CommonUtility.convertJsonToCSV(primaryReportString, getContext(), "secondary_sales_report.csv");
            else
                Toast.makeText(getContext(), "No Data", Toast.LENGTH_SHORT).show();


        }

    }

    private void getWebReport() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        CommonUtility.showProgressDialog(getActivity());
//        String url = ServerConfig.getPrimaryReportWeb(new SharedPrefsManager(getContext()).getString(SharedPreferenceKeys.USER_ID), fromdate, todate);

        String url = "http://mpartner.luminousintranet.com:81/nonsapservices/api/nonsapservice/sscStrsscDistachedReportRawData?SessionLoginDisID=001108220&DlrID=0&ItemID=0&DispatchedDateFrom=1/1/2019&DispatchedDateTo=1/31/2019&token=pass@1234";
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
                            CommonUtility.convertJsonToCSV(response, getContext(), "secondary_sales_report.csv");
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

    private void getSecQSummaryAndReport() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        binding.llProgressBar.setVisibility(View.VISIBLE);

        getSecQSummaryAndReport(db.getString(SharedPreferenceKeys.USER_ID), itemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<ResponseBody, ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }


                    @Override
                    public void onNext(Pair<ResponseBody, ResponseBody> response) {

                        String qString = null;
                        String secondaryString = null;
                        try {
                            qString = response.first.string();
                            Log.d(TAG, "onNext: qString" + qString);

                            List<SecondarySalesQuantitySummaryEntity> quantity = new Gson()
                                    .fromJson(qString, new TypeToken<List<SecondarySalesQuantitySummaryEntity>>() {
                                    }.getType());

                            if (quantity.size() != 0) {
                                binding.tvTotalBatt.setText(String.format("Total %s Products: %s",
                                        allItemList[Integer.parseInt(quantity.get(0).getItemID())], quantity.get(0).getTAvlQuantity())
                                );

                                binding.tvTotalHomeUps.setText(String.format("Total %s Products: %s",
                                        allItemList[Integer.parseInt(quantity.get(1).getItemID())], quantity.get(1).getTAvlQuantity())
                                );

                                binding.tvTotalSolarProducts.setText(String.format("Total %s Products: %s",
                                        allItemList[Integer.parseInt(quantity.get(2).getItemID())], quantity.get(2).getTAvlQuantity())
                                );
                            }


                            secondaryString = response.second.string();
                            primaryReportString = secondaryString;
                            Log.d(TAG, "secondaryString: " + secondaryString);

                            list = new Gson().fromJson(secondaryString, new TypeToken<List<SecondarySalesReportEntity>>() {
                            }.getType());

                            updateSecReportAdapter(list);
                            binding.llProgressBar.setVisibility(View.GONE);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }


                    @Override
                    public void onError(Throwable e) {
                        CommonUtility.printLog(TAG, "onError");
                        e.printStackTrace();
                        binding.llProgressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog(TAG, "onComplete");
                    }
                });
    }

    private void updateSecReportAdapter(List<SecondarySalesReportEntity> list) {

        if (list == null) {
            hideLayout();
            return;
        } else if (list.size() == 0) {
            hideLayout();
            return;
        } else {
            showLayout();
        }

        SecondarySalesReportAdapter adapter = new SecondarySalesReportAdapter(getContext(), list, allItemList);
        binding.recyclerView.setAdapter(adapter);

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
    public void onClick(View v) {

        if (v.getId() == R.id.btnSubmit) {
            getSecQSummaryAndReport();
        } else if (v.getId() == R.id.btnReset) {
            hideLayout();
            binding.tvNoDataFound.setVisibility(View.GONE);
        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

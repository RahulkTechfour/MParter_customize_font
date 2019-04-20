package com.luminous.mpartner.sales;


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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.CreateSalesOrderApadter;
import com.luminous.mpartner.databinding.FragmentCreateSalesOrderBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.ChannelListEntity;
import com.luminous.mpartner.network.entities.DivisionListEntity;
import com.luminous.mpartner.network.entities.MessageEntity;
import com.luminous.mpartner.network.entities.ProductAndDescResponseEntity;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.ResponseBody;

public class CreateSalesOrderFragment extends Fragment implements CreateSalesOrderApadter.OnClickHandler, View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final String TAG = "CreateSalesOrder";
    private RetrofitInterface apiInterface;
    FragmentCreateSalesOrderBinding binding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String selectedDiv, selectedCh, selectedProduct, selectedDesc;
    List<DivisionListEntity> divList;
    List<ChannelListEntity> chList;
    List<ProductAndDescResponseEntity> pList;
    private static final String PRODUCT_SPINNER = "product";
    private static final String PDESC_SPINNER = "desc";
    private ArrayList<OrderEntity> orderEntities;

    public CreateSalesOrderFragment() {

    }

    public static CreateSalesOrderFragment newInstance(String param1, String param2) {
        CreateSalesOrderFragment fragment = new CreateSalesOrderFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_sales_order, container, false);


        getChannelDivList();

        binding.spinnerDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    if (divList != null) {
                        selectedDiv = divList.get(position).getDivision();
                        Log.d(TAG, "selectedDiv: " + selectedDiv);
                    } else selectedDiv = null;

                    if (selectedCh != null && selectedDiv != null)
                        getProductDesc(selectedCh, selectedDiv);
                    else {
                        Log.d(TAG, "Unable to call getProductDesc");
                    }
                } else selectedDiv = null;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDiv = null;
            }
        });
        binding.spinnerChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    if (chList != null) {
                        selectedCh = chList.get(position).getChannel();
                        Log.d(TAG, "selectedCh: " + selectedCh);
                    } else {
                        selectedCh = null;
                    }

                    if (selectedCh != null && selectedDiv != null)
                        getProductDesc(selectedCh, selectedDiv);
                    else {
                        Log.d(TAG, "Unable to call getProductDesc");
                    }
                } else selectedCh = null;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCh = null;
            }
        });
        binding.spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                setPositionOfPandPDSpinner(position, PRODUCT_SPINNER);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedProduct = null;
            }
        });
        binding.spinnerDesc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                setPositionOfPandPDSpinner(position, PDESC_SPINNER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDesc = null;
            }
        });

        orderEntities = new ArrayList<>();

        binding.ivAdd.setOnClickListener(this);
        binding.btnCreateOrder.setOnClickListener(this);

        binding.llPlaceOrder.setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView mTxt = view.findViewById(R.id.create_sales_order);
        SpannableString mySpannableString = new SpannableString("Create Sale Order");
        mySpannableString.setSpan(new UnderlineSpan(), 0, 6, 0);
        mTxt.setText(mySpannableString);

    }

    private int checkIfProductAlreadyAdded(OrderEntity orderEntity) {

        int pos = -1;
        for (OrderEntity entity : orderEntities) {
            pos++;
            if (entity.getpId().equals(orderEntity.getpId())) {
                return pos;
            } else
                pos = -1;

        }

        return pos;
    }

    private void updateAdapter() {

        CreateSalesOrderApadter adapter = new CreateSalesOrderApadter(getContext(), orderEntities, this);
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (orderEntities.size() == 0) {
            viewVisibility(View.GONE);
            binding.spinnerChannel.setEnabled(true);
            binding.spinnerDivision.setEnabled(true);
        } else {
            viewVisibility(View.VISIBLE);
            binding.spinnerChannel.setEnabled(false);
            binding.spinnerDivision.setEnabled(false);
        }


    }

    private void setPositionOfPandPDSpinner(int position, String spinner) {

        try {
            if (position == 0 && spinner.equals(PRODUCT_SPINNER)) {
                binding.spinnerProduct.setSelection(0);
                binding.spinnerDesc.setSelection(0);
                binding.spinnerDesc.setEnabled(false);
                return;
            } else if (position > 0 && spinner.equals(PRODUCT_SPINNER)) {
                binding.spinnerDesc.setEnabled(true);
            }


            selectedProduct = pList.get(position).getMaterialCode();
            selectedDesc = pList.get(position).getMaterialDescription();
            if (spinner.equals(PRODUCT_SPINNER)) {
                binding.spinnerDesc.setSelection(position);
                binding.spinnerProduct.setSelection(position);
            } else if (spinner.equals(PDESC_SPINNER)) {
                binding.spinnerProduct.setSelection(position);
                binding.spinnerDesc.setSelection(position);
            }
        } catch (Exception ex) {
            ex.getMessage();
        }

    }

    private void getProductDesc(String ch, String div) {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.getProductAndPDesc(ch, div);
        Log.d(TAG, "url: " + url);

        apiInterface.getProductAndPDesc(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }


                    @Override
                    public void onNext(ResponseBody response) {

                        String prodDescString = null;
                        try {
                            prodDescString = response.string();
                            Log.d(TAG, "prodDesc : " + prodDescString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!prodDescString.isEmpty() && !prodDescString.equals("[]")) {
                            inflateProductAndPDescSpinner(prodDescString);
                            binding.llPlaceOrder.setVisibility(View.VISIBLE);
                        } else {
                            binding.llPlaceOrder.setVisibility(View.GONE);
                        }

                    }


                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        binding.llPlaceOrder.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog(TAG, "onComplete");
                    }
                });
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

    private void inflateProductAndPDescSpinner(String s) {

        pList = new Gson().fromJson(s, new TypeToken<List<ProductAndDescResponseEntity>>() {
        }.getType());
        String[] pListArray = new String[pList.size() + 1];
        String[] pDescListArray = new String[pList.size() + 1];

        pListArray[0] = "Select Product";
        pDescListArray[0] = "Description";
        pList.add(0, null);

        for (int i = 1; i < pList.size(); i++) {
            pListArray[i] = pList.get(i).getMaterialCode();
        }
        ArrayAdapter<CharSequence> productAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, pListArray);
        productAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        binding.spinnerProduct.setAdapter(productAdapter);


        // desc


        for (int i = 1; i < pList.size(); i++) {
            pDescListArray[i] = pList.get(i).getMaterialDescription();
        }
        ArrayAdapter<CharSequence> productDescAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, pDescListArray);
        productDescAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        binding.spinnerDesc.setAdapter(productDescAdapter);

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

    @Override
    public void onClickItem(int i) {
        binding.edtQty.setText("");
        orderEntities.remove(i);
        updateAdapter();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ivAdd:

                if (binding.edtQty.getText().equals("0")) {
                    Toast.makeText(getContext(), "Quantity cannot be zero", Toast.LENGTH_SHORT).show();
                }

                if (binding.spinnerProduct.getSelectedItemPosition() != 0) {
                    add();
                } else {
                    Toast.makeText(getContext(), "Select Product", Toast.LENGTH_SHORT).show();
                }

                binding.spinnerProduct.setSelection(0);
                binding.spinnerDesc.setSelection(0);
                binding.edtQty.setText(null);


                break;
            case R.id.btnCreateOrder:
                if (orderEntities != null && !orderEntities.isEmpty()) {
                    createOrder();
                } else {
                    Toast.makeText(getContext(), "Please add products", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private void createOrder() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        CommonUtility.showProgressDialog(getActivity());
        String url = ServerConfig.getCreateSalesOrder(new SharedPrefsManager(getActivity()).getString(SharedPreferenceKeys.USER_ID), selectedCh, selectedDiv, orderEntities);
        Log.d(TAG, "createOrder: " + url);

        apiInterface.getCreateSalesOrder(url)
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

                        String response = "";
                        try {
                            response = responseBody.string();
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }

                        List<MessageEntity> entities = new Gson().fromJson(response, new TypeToken<ArrayList<MessageEntity>>() {
                        }.getType());

                        if (entities != null && entities.get(0).getMESSAGE() != null
                                && !entities.get(0).getMESSAGE().isEmpty()) {
                            Toast.makeText(getContext(), entities.get(0).getMESSAGE() + "", Toast.LENGTH_SHORT).show();
                            orderEntities = new ArrayList<OrderEntity>();
                            updateAdapter();
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

    }

    private void viewVisibility(int isVisible) {
        binding.llData.setVisibility(isVisible);
        binding.llPlaceOrder.setVisibility(isVisible);
        binding.btnCreateOrder.setVisibility(isVisible);
    }


    private void add() {

        OrderEntity orderEntity = new OrderEntity();

        if (binding.edtQty.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
        } else {
            if (selectedProduct != null && selectedDesc != null) {
                orderEntity.setpDesc(selectedDesc);
                orderEntity.setpId(selectedProduct);
                orderEntity.setpQty(binding.edtQty.getText().toString());

                int res = checkIfProductAlreadyAdded(orderEntity);

                if (res == -1) {
                    orderEntities.add(orderEntity);
                } else {
                    orderEntity.setpQty(Integer.parseInt(orderEntities.get(res).getpQty()) +
                            Integer.parseInt(binding.edtQty.getText().toString()) + "");
                    orderEntities.set(res, orderEntity);
                }
                updateAdapter();
            }
        }
    }
}

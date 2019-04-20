package com.luminous.mpartner.dealer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.luminous.mpartner.databinding.FragmentRegisterDealerBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.CityEntity;
import com.luminous.mpartner.network.entities.ProductDealerTypeEntity;
import com.luminous.mpartner.network.entities.StateEntity;
import com.luminous.mpartner.utilities.CommonUtility;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class CreateDealerFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CreateDealerFragment";
    private RetrofitInterface apiInterface;
    FragmentRegisterDealerBinding binding;
    private List<StateEntity> stateList;
    private String[] stateNameList;

    private List<CityEntity> cityList;
    private String[] cityNameList;

    private List<ProductDealerTypeEntity> pdList;
    private String[] pdNameList;
    private String selectedState;
    private String selectedCity;

    public CreateDealerFragment() {
    }


    public static CreateDealerFragment newInstance(String param1, String param2) {
        CreateDealerFragment fragment = new CreateDealerFragment();
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

        }

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_dealer, container, false);
        View view = binding.getRoot();

        getStateList();

        binding.spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (stateList != null) {
                    selectedState = stateList.get(position).getStateID();
                    getCityList(selectedState);
                } else
                    selectedState = null;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        binding.spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (cityList != null) {
                    selectedCity = cityList.get(position).getCityCode();
                } else selectedCity = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnCreateDealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRegisterUser();
            }
        });

        return view;
    }

    private void getStateList() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.getStateUrl();
        Log.d(TAG, "state url: " + url);
        apiInterface.getStateList(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        try {

                            stateList = new Gson().fromJson(responseBody.string(), new TypeToken<List<StateEntity>>() {
                            }.getType());
                            stateNameList = new String[stateList.size()];

                            for (int i = 0; i < stateList.size(); i++) {
                                stateNameList[i] = stateList.get(i).getStateName();
                            }
                            ArrayAdapter<CharSequence> allItemSpinnerAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, stateNameList);
                            allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                            binding.spState.setAdapter(allItemSpinnerAdapter);

                            binding.spState.setSelection(1);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private void getCityList(String stateId) {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = String.format(ServerConfig.getCityUrl(), stateId);

        Log.d(TAG, "city url: " + url);
        apiInterface.getCityList(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        try {

                            cityList = new Gson().fromJson(responseBody.string(), new TypeToken<List<CityEntity>>() {
                            }.getType());
                            cityNameList = new String[cityList.size()];

                            for (int i = 0; i < cityList.size(); i++) {
                                cityNameList[i] = cityList.get(i).getCityName();
                            }
                            ArrayAdapter<CharSequence> allItemSpinnerAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, cityNameList);
                            allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                            binding.spCity.setAdapter(allItemSpinnerAdapter);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getRegisterUser() {


        String firm = binding.txtDealerFirmName.getText().toString();
//        String name = binding.txtDealerName.getText().toString();
        String ownerName = binding.txtOwnerName.getText().toString();
        String address1 = binding.txtAddressFirst.getText().toString();
        String address2 = binding.txtAddressSecond.getText().toString();
        String postalCode = binding.txtPostalCode.getText().toString();
        String mobile = binding.txtMobNo.getText().toString();
        String telephone = binding.txtTelePhoneNo.getText().toString();
        String email = binding.txtEmailId.getText().toString();
        String city = selectedCity;
        String state = selectedState;

        if (firm.isEmpty() && ownerName.isEmpty() && address1.isEmpty() &&
                postalCode.isEmpty() && mobile.isEmpty() && email.isEmpty() && city == null && state == null) {
            Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (postalCode.equalsIgnoreCase("000000") || postalCode.startsWith("0")) {
            Toast.makeText(getContext(), "Pin Code Invalid", Toast.LENGTH_SHORT).show();
            return;
        } else if (mobile.equalsIgnoreCase("0000000000") || mobile.startsWith("0")) {
            Toast.makeText(getContext(), "Mobile Number Invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = String.format(ServerConfig.getCreateDealerUrl(), firm,
                ownerName,
                address1, address2,
                city, state,
                postalCode, mobile,
                telephone, email);


        apiInterface.getCreateDealer(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        try {

                            Log.d(TAG, "onNext: " + responseBody.string());


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    /*private void getProductDealerList(){

        String url =  "http://mpartner.luminousintranet.com:81/nonsapservices/api/nonsapservice/Product_DealerType?Typeset=DLRTYP&token=pass@1234";

        Log.d(TAG, "getProductDealerList: "+url);
        apiInterface.getProductDealerType(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        try {

                            pdList = new Gson().fromJson(responseBody.string(), new TypeToken<List<ProductDealerTypeEntity>>() {
                            }.getType());
                            pdNameList = new String[pdList.size()];

                            for (int i = 0; i < pdList.size(); i++) {
                                pdNameList[i] = pdList.get(i).getTypesName();
                            }
                            *//*ArrayAdapter<CharSequence> allItemSpinnerAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, pdNameList);
                            allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                            binding..setAdapter(allItemSpinnerAdapter);*//*


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }*/

}

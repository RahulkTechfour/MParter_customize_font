package com.luminous.mpartner.dealer;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentCreateDealerFirmDeatailsBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.CityEntity;
import com.luminous.mpartner.network.entities.MessageEntity;
import com.luminous.mpartner.network.entities.ProfileEntity;
import com.luminous.mpartner.network.entities.StateEntity;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class CreateDealerFirmAddressDeatailsFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FragmentCreateDealerFirmDeatailsBinding binding;
    String firm, name, ownerName, address1, address2, postalCode, mobile, telephone, email, selectedCity, selectedState, selectedCountry;
    private RetrofitInterface apiInterface;
    private static final String TAG = "CreateDealer";
    private List<StateEntity> stateList;
    private String[] stateNameList;

    private List<CityEntity> cityList;
    private String[] cityNameList;
    String emptyError = "Field cannot be empty";
    String invalidError = "Invalid ";
    private boolean cityFound;
    private Spinner spCountry;

    public CreateDealerFirmAddressDeatailsFragment() {
    }

    public static CreateDealerFirmAddressDeatailsFragment newInstance(String param1, String param2) {
        CreateDealerFirmAddressDeatailsFragment fragment = new CreateDealerFirmAddressDeatailsFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_dealer_firm_deatails, container, false);

        setBtnOnCLickListener();
        getStateList();
        binding.spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    if (stateList != null) {

                        // change
                    //    binding.relSp2.setVisibility(View.VISIBLE);
                       String state_id = stateList.get(position).getStateID();
                        selectedState = state_id;
                        getCityList(state_id);
                     //   binding.spState.setEnabled(false);
                    } else {
                        selectedState = null;
                    }

                } else {
                    // change
                    //binding.relSp2.setVisibility(View.GONE);
                    selectedState = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedState = null;
            }
        });
        binding.spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0)
                    if (cityList != null)
                        selectedCity = cityList.get(position).getCityCode();
                    else selectedCity = null;
                else
                    selectedCity = null;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCity = null;
            }
        });






        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spCountry = view.findViewById(R.id.spCountry);
        List<String> cat = new ArrayList<String>();
        cat.add("Select Country");
        cat.add("India");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cat);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCountry.setAdapter(dataAdapter);
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {

                    selectedCountry = spCountry.getSelectedItem().toString();
                 //   Object item = parent.getItemAtPosition(position);

                }catch(Exception ex){
                    ex.getMessage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setBtnOnCLickListener() {
        binding.next.setOnClickListener(this);
        binding.submit.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.next:
                next();
                break;

            case R.id.back:
                back();
                break;

            case R.id.submit:
                submit();
                break;
            case R.id.cancel:
                cancel();
                break;
        }
    }


    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void next() {


        boolean valid = true;

        firm = binding.txtDealerFirmName2.getText().toString();
//        name = binding.txtDealerName2.getText().toString();
        ownerName = binding.txtOwnerName2.getText().toString();
        mobile = binding.txtMobNo2.getText().toString();
        telephone = binding.txtTelePhoneNo2.getText().toString();
        email = binding.txtEmailId2.getText().toString();

        /**/
        if (firm.isEmpty()) {
            binding.tilFirm.setError(emptyError);
            valid = false;
        } else {
            binding.tilFirm.setError(null);

        }

        if (ownerName.isEmpty()) {
            binding.tilOwnerName.setError(emptyError);
            valid = false;
        } else {
            binding.tilOwnerName.setError(null);

        }

        if (mobile.isEmpty()) {
            binding.tilMobile.setError(emptyError);
            valid = false;
        } else if (mobile.length() != 10) {
            binding.tilMobile.setError(invalidError + "Mobile No");
            valid = false;
        } else if (mobile.equalsIgnoreCase("0000000000") || mobile.startsWith("0")) {
            binding.tilMobile.setError(invalidError + "Mobile No");
            valid = false;
        } else {
            binding.tilMobile.setError(null);

        }

        if (email.isEmpty()) {
            binding.tilEmail.setError(emptyError);
            valid = false;
        } else if (CommonUtility.validEmail(email) == false) {
            binding.tilEmail.setError(invalidError + "Email");
            valid = false;
        } else {
            binding.tilEmail.setError(null);

        }

        if (valid) {
            showAddressDetailsForm();
        } else {
            Log.d(TAG, "next: invalid entries");
        }

    }


    private void showAddressDetailsForm() {
        binding.llFirmDetails.setVisibility(View.GONE);
        binding.llAddressDetails.setVisibility(View.VISIBLE);
        binding.next.setVisibility(View.GONE);
        binding.submit.setVisibility(View.VISIBLE);
        binding.back.setVisibility(View.VISIBLE);

    }

    private void cancel() {
        /*TODO : warning*/
        Fragment fragment = DealerMainFragment.newInstance("", "");
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

    }

    private void submit() {

        address1 = binding.txtAddressFirst.getText().toString();
        address2 = binding.txtAddressSecond.getText().toString();
        postalCode = binding.txtPostalCode.getText().toString();
        boolean valid = true;

        if (address1.isEmpty()) {
            valid = false;
            binding.tilAddress1.setError(emptyError);
        } else {
            binding.tilAddress1.setError(null);
        }

        if (postalCode.isEmpty()) {
            valid = false;
            binding.tilPincode.setError(emptyError);
        } else if (postalCode.length() != 6) {
            valid = false;
            binding.tilPincode.setError(invalidError + "Postal Code");
        } else if (postalCode.equalsIgnoreCase("000000") || postalCode.startsWith("0")) {
            valid = false;
            binding.tilPincode.setError(invalidError + "Postal Code");
        } else {
            binding.tilPincode.setError(null);
            binding.tilPincode.setError(invalidError + "Postal Code");
        }

        if (selectedCity == null && cityFound == true) {
            valid = false;
            Toast.makeText(getContext(), "Select City", Toast.LENGTH_SHORT).show();
        }

        if (selectedState == null) {
            valid = false;
            Toast.makeText(getContext(), "Select State", Toast.LENGTH_SHORT).show();
        }
        if (selectedCountry.equalsIgnoreCase("Select Country")) {
            valid = false;
            Toast.makeText(getContext(), "Select Country", Toast.LENGTH_SHORT).show();
        }

        SharedPrefsManager manager = new SharedPrefsManager(getContext());

        if (valid) {

            String url = String.format(ServerConfig.getCreateDealerUrl(),
                    manager.getString(SharedPreferenceKeys.USER_ID),
                    firm,
                    ownerName,
                    address1,
                    address2,
                    selectedCity,
                    selectedState,
                    postalCode,
                    mobile,
                    telephone,
                    email);

            Log.d(TAG, "submit: " + url);

            if (!CommonUtility.isNetworkAvailable(getContext())) {
                return;
            }


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

                                ArrayList<MessageEntity> responseEntity = new Gson().fromJson(responseBody.string(), new TypeToken<ArrayList<MessageEntity>>() {
                                }.getType());

                                if (responseEntity != null) {
                                    String message = "";
                                    for (int i = 0; i < responseEntity.size(); i++) {
                                        message += responseEntity.get(i).getMESSAGE() + "\n";
                                    }

                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d(TAG, "Null response");
                                }

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
                            cancel();
                        }
                    });
        }


    }

    private void back() {
        binding.llFirmDetails.setVisibility(View.VISIBLE);
        binding.llAddressDetails.setVisibility(View.GONE);
        binding.next.setVisibility(View.VISIBLE);
        binding.submit.setVisibility(View.GONE);
        binding.back.setVisibility(View.GONE);
    }

    int selectedPosition;

    private void getStateList() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(getActivity());
        ProfileEntity mProfileDetail = (ProfileEntity) sharedPrefsManager.getObject(SharedPreferenceKeys.USER_DATA, ProfileEntity.class);


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
                            stateList.add(0, null);

                            stateNameList = new String[stateList.size() + 1];
                            stateNameList[0] = "State";

                            for (int i = 1; i < stateList.size(); i++) {
                                stateNameList[i] = stateList.get(i).getStateName();
                               // if (stateNameList[i].equalsIgnoreCase(mProfileDetail.getState())) {
                                    selectedState = stateList.get(i).getStateID();
                                    selectedPosition = i;
                              //  }

                            }
                            ArrayAdapter<CharSequence> allItemSpinnerAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, stateNameList);
                            allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                            binding.spState.setAdapter(allItemSpinnerAdapter);
                           // binding.spState.setSelection(selectedPosition);

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

                            if (cityList == null) {
                                cityFound = false;
                            } else if (cityList.size() == 0) {
                                cityFound = false;
                            } else {
                                cityFound = true;
                            }
                            cityNameList = new String[cityList.size() + 1];
                            cityList.add(0, null);
                            cityNameList[0] = "City";
                            for (int i = 1; i < cityList.size(); i++) {
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


}

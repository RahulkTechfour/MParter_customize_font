package com.luminous.mpartner.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.adapters.EscalationMatrixAdapter;
import com.luminous.mpartner.databinding.FragmentEscalationMatrixBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.EscalationMatrix.EscalationMatrix;
import com.luminous.mpartner.network.entities.EscalationMatrix.EscalationMatrixResponseEntity;
import com.luminous.mpartner.network.entities.StateEntity;
import com.luminous.mpartner.utilities.CommonUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class EscalationMatrixFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RetrofitInterface apiInterface;
    private FragmentEscalationMatrixBinding binding;
    private static final String TAG = "EscalationMatrix";
    private List<StateEntity> stateList;
    private String[] stateNameList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    public EscalationMatrixFragment() {
    }

    public static EscalationMatrixFragment newInstance(String param1, String param2) {
        EscalationMatrixFragment fragment = new EscalationMatrixFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_escalation_matrix, container, false);
        getStateList();

        binding.spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    getEscalationMatrix(stateList.get(position).getStateID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return binding.getRoot();
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
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        try {

                            stateList = new Gson().fromJson(responseBody.string(), new TypeToken<List<StateEntity>>() {
                            }.getType());
                            stateNameList = new String[stateList.size() + 1];
                            stateNameList[0] = "Select State";
                            stateList.add(0, null);

                            for (int i = 1; i < stateList.size(); i++) {
                                stateNameList[i] = stateList.get(i).getStateName();
                            }
                            ArrayAdapter<CharSequence> allItemSpinnerAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.spinner_text, stateNameList);
                            allItemSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                            binding.spinnerState.setAdapter(allItemSpinnerAdapter);

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

    private void getEscalationMatrix(String state) {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.newUrl(ServerConfig.getEscalationMatrix() + "stateid=" + state, getContext(), "EscalationMatrixFragment.class");
        Log.d(TAG, "url : " + url);

        apiInterface.getEscalationMatrix(url)
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

                            if (response.trim().equals("\"\"")) {
                                setAdapter(null);
                                Log.d(TAG, "onNext: true");
                            } else {
                                Gson gson = new GsonBuilder().create();
                                EscalationMatrixResponseEntity responseEntity = gson.fromJson(response, new TypeToken<EscalationMatrixResponseEntity>() {
                                }.getType());

                                if (responseEntity != null) {

                                    if (responseEntity.getStatus().equals("200")) {
                                        List<EscalationMatrix> list = responseEntity.getEscalationMatrix();
                                        setAdapter(list);
                                    } else {
                                        setAdapter(null);
                                        Log.d(TAG, "Matrix Response Status : " + responseEntity.getStatus());
                                    }
                                } else {
                                    setAdapter(null);
                                    Log.d(TAG, "Null");
                                }
                            }


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
                        Log.d(TAG, "onComplete");
                    }
                });


    }

    private void setAdapter(List<EscalationMatrix> list) {

        EscalationMatrixAdapter adapter = new EscalationMatrixAdapter(getContext(), list);
        binding.listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


}

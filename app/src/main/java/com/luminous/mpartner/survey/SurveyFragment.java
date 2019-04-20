package com.luminous.mpartner.survey;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentSurveyBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.survey.SurveyListResponseEntity;
import com.luminous.mpartner.network.entities.survey.SurveyNotification;
import com.luminous.mpartner.utilities.CommonUtility;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class SurveyFragment extends Fragment implements LvSurveyAdater.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentSurveyBinding binding;
    private RetrofitInterface apiInterfavce;
    private static final String TAG = "SurveyFragment123";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    public SurveyFragment() {
        // Required empty public constructor
    }


    public static SurveyFragment newInstance(String param1, String param2) {
        SurveyFragment fragment = new SurveyFragment();
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
        apiInterfavce = RetrofitClient.getInstance().create(RetrofitInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_survey, container, false);
        binding.fragmentLabel.tvLabel.setText(getString(R.string.survey));
        getSurveyList();
        return binding.getRoot();
    }

    private void getSurveyList() {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.newUrl(ServerConfig.getSurveyList(), getContext(), "SurveyFragment.class");
        Log.d(TAG, "url : " + url);
        apiInterfavce.baseRetrofitInterface(url)
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
                            SurveyListResponseEntity surveyNotification = new Gson().fromJson(response, new TypeToken<SurveyListResponseEntity>() {
                            }.getType());

                            if (surveyNotification != null && surveyNotification.getStatus().equals("200")) {
                                List<SurveyNotification> list = surveyNotification.getSurveyNotification();
                                LvSurveyAdater adapter = new LvSurveyAdater(getContext(), list, SurveyFragment.this);
                                binding.lvSurvey.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
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

    /*Called on list item click*/
    @Override
    public void onClick(SurveyNotification surveyNotification) {
        Fragment fragment = SurveyQuestionFragment.newInstance(surveyNotification.getSurvey(), String.valueOf(surveyNotification.getSurveyId()));
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "SurveyQuestionFragment").commit();
    }
}

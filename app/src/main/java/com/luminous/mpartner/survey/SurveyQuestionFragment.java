package com.luminous.mpartner.survey;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentSurveyQuestionBinding;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.survey.MessageEntity;
import com.luminous.mpartner.network.entities.survey.SurveyQuestion.SurveyNotificationQue;
import com.luminous.mpartner.network.entities.survey.SurveyQuestion.SurveyQuestionEntity;
import com.luminous.mpartner.utilities.CommonUtility;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.luminous.mpartner.survey.SurveyQuestionAdapter.submitEntities;

public class SurveyQuestionFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String surveyText;
    private String surveyId;

    private FragmentSurveyQuestionBinding binding;
    private static final String TAG = "SurveyQuestionFragment";
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    SurveyQuestionEntity entity;
    int currentQ = 0;
    private String option, optionText;
    private List<SurveyNotificationQue> list;


    public SurveyQuestionFragment() {
    }

    public static SurveyQuestionFragment newInstance(String param1, String param2) {
        SurveyQuestionFragment fragment = new SurveyQuestionFragment();
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
            surveyText = getArguments().getString(ARG_PARAM1);
            surveyId = getArguments().getString(ARG_PARAM2);
        }
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_survey_question, container, false);
        binding.llSurvey.setVisibility(View.GONE);

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(optionText))
                    submit(surveyId, option, optionText);
                else
                    Toast.makeText(getActivity(), "Please select option", Toast.LENGTH_LONG).show();
            }
        });


        binding.rgOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = group.findViewById(checkedId);
                switch (checkedId) {
                    case R.id.radioA:
                        optionText = radioButton.getText().toString();
                        option = "optionA";
                        break;
                    case R.id.radioB:
                        optionText = radioButton.getText().toString();
                        option = "optionB";
                        break;
                    case R.id.radioC:
                        optionText = radioButton.getText().toString();
                        option = "optionC";
                        break;
                    case R.id.radioD:
                        optionText = radioButton.getText().toString();
                        option = "optionD";
                        break;
                    case R.id.radioE:
                        optionText = radioButton.getText().toString();
                        option = "optionE";
                        break;
                }
            }
        });

        getSurveyQuestion(surveyId);
        return binding.getRoot();
    }

    private void submit(String surveyId, String option, String optionText) {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }
        String url = ServerConfig.newUrl(ServerConfig.getSubmitSurveyResponse(surveyId, option, optionText)
                , getContext(), "SurveyQuestionFragment.class");

        Log.d(TAG, "url : " + url);

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

                        String response = "";

                        try {
                            response = responseBody.string();
                            Log.d(TAG, "onNext: " + response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MessageEntity entity = new Gson().fromJson(response, new TypeToken<MessageEntity>() {
                        }.getType());
                        if (entity != null && entity.getStatus().equals("200")) {
                            binding.llSurvey.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), entity.getMessage(), Toast.LENGTH_SHORT).show();
                            Fragment fragment = SurveyFragment.newInstance("", "");
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                        } else {
                            Log.d(TAG, "Unable to send response");
                            binding.llSurvey.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError : " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

    }

    private void getSurveyQuestion(String surveyId) {

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.newUrl(ServerConfig.getSurveyQuestion(surveyId), getContext(), "SurveyQuestionFragment.class");
        Log.d(TAG, "url : " + url);
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

                        String response = "";

                        try {
                            response = responseBody.string();
                            Log.d(TAG, "onNext: " + response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        SurveyQuestionEntity entity = new Gson().fromJson(response, new TypeToken<SurveyQuestionEntity>() {
                        }.getType());
                        if (entity != null && entity.getStatus().equals("200")) {
                            binding.llSurvey.setVisibility(View.VISIBLE);
                            list = entity.getSurveyNotificationQues();

                            if (list.get(0).getQuestionType().equalsIgnoreCase("MCQ")) {
                                binding.rgOptions.setVisibility(View.VISIBLE);
                                if (!list.get(0).getOptionA().equalsIgnoreCase("0"))
                                    binding.radioA.setText(list.get(0).getOptionA());
                                else
                                    binding.radioA.setVisibility(View.GONE);

                                if (!list.get(0).getOptionB().equalsIgnoreCase("0"))
                                    binding.radioB.setText(list.get(0).getOptionB());
                                else
                                    binding.radioB.setVisibility(View.GONE);

                                if (!list.get(0).getOptionC().equalsIgnoreCase("0"))
                                    binding.radioC.setText(list.get(0).getOptionC());
                                else
                                    binding.radioC.setVisibility(View.GONE);

                                if (!list.get(0).getOptionD().equalsIgnoreCase("0"))
                                    binding.radioD.setText(list.get(0).getOptionD());
                                else
                                    binding.radioD.setVisibility(View.GONE);

                                if (!list.get(0).getOptionE().equalsIgnoreCase("0"))
                                    binding.radioE.setText(list.get(0).getOptionE());
                                else
                                    binding.radioE.setVisibility(View.GONE);
                            } else
                                binding.etSelect.setVisibility(View.VISIBLE);

                            binding.tvQ.setText(list.get(0).getQuestionTitle());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError : " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });


    }

}

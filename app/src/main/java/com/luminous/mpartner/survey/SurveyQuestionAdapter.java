package com.luminous.mpartner.survey;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.RowLvSurveyQuestionBinding;
import com.luminous.mpartner.network.entities.survey.SurveyQuestion.SurveyNotificationQue;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.List;

public class SurveyQuestionAdapter extends BaseAdapter {

    private List<SurveyNotificationQue> list;
    private Context context;
    private RowLvSurveyQuestionBinding binding;
    private String optionText, option;
    private String sId;
    public static List<SubmitSurveryEntity> submitEntities;

    public SurveyQuestionAdapter(Context context, List<SurveyNotificationQue> list) {
        this.list = list;
        this.context = context;
        submitEntities = new ArrayList<>();

    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater, R.layout.row_lv_survey_question, parent, false);

        SurveyNotificationQue entity = list.get(position);
        SubmitSurveryEntity submit = new SubmitSurveryEntity();




        binding.tvQ.setText((position + 1) + ". " + entity.getQuestionTitle());





        return binding.getRoot();
    }
}

package com.luminous.mpartner.survey;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.RowLvSurveyBinding;
import com.luminous.mpartner.network.entities.survey.SurveyNotification;

import java.util.List;

public class LvSurveyAdater extends BaseAdapter {

    private Context context;
    private List<SurveyNotification> list;
    private RowLvSurveyBinding binding;
    OnClickListener clickListener;

    public interface OnClickListener {
        public void onClick(SurveyNotification surveyNotification);
    }

    public LvSurveyAdater(Context context, List<SurveyNotification> list, OnClickListener clickListener) {
        this.context = context;
        this.list = list;
        this.clickListener = clickListener;
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

        SurveyNotification entity = list.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater, R.layout.row_lv_survey, parent, false);
        binding.tvSurvey.setText(entity.getSurvey());
        binding.tvSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(entity);
            }
        });
        return binding.getRoot();
    }
}

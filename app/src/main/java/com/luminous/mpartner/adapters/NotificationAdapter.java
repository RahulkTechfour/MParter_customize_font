package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.RowLvNotificationBinding;
import com.luminous.mpartner.network.entities.Notification.SurveyNotificationAlert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends BaseAdapter {

    List<SurveyNotificationAlert> list;
    Context context;
    RowLvNotificationBinding binding;

    public NotificationAdapter(Context context, List<SurveyNotificationAlert> list){

        this.context = context;
        this.list = list;
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
        binding = DataBindingUtil.inflate(inflater, R.layout.row_lv_notification, parent, false);

        SurveyNotificationAlert entity = list.get(position);

        Glide.with(context).load(entity.getImagepath()).into(binding.ivImg);

        String sD = entity.getDate();
        String date = "";



        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
        try {
            Date d = dateFormat.parse(sD);
            String x = newDateFormat.format(d);
            binding.tvDate.setText(x);


        } catch (ParseException e) {
            e.printStackTrace();
        }



        binding.tvN.setText(entity.getText());
        return binding.getRoot();
    }
}

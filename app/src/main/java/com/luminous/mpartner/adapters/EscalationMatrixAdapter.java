package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.RowLvEscalationMatrixBinding;
import com.luminous.mpartner.network.entities.EscalationMatrix.EscalationMatrix;

import java.util.List;

public class EscalationMatrixAdapter extends BaseAdapter {

    private Context context;
    private List<EscalationMatrix> list;
    private RowLvEscalationMatrixBinding binding;


    public EscalationMatrixAdapter(Context context, List<EscalationMatrix> list){

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

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater, R.layout.row_lv_escalation_matrix, parent, false);

        EscalationMatrix matrix = list.get(position);

        String udata=matrix.getServiceCenterName();
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        binding.tvHeader.setText(content);

        binding.tvAddress.setText(""+matrix.getAddress());
        binding.tvPhone.setText("Phone : "+matrix.getPhoneno());

        return binding.getRoot();
    }
}

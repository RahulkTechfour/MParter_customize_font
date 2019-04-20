package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ReportRowBinding;
import com.luminous.mpartner.network.entities.PrimaryReportEntity;

import java.util.List;

public class PrimaryReportsAdapter extends RecyclerView.Adapter<PrimaryReportsAdapter.ItemRowHolder> {

    private Context context;
    private List<PrimaryReportEntity> itemList;

    public PrimaryReportsAdapter(Context context, List<PrimaryReportEntity> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public PrimaryReportsAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ReportRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.report_row, parent, false);
        return new PrimaryReportsAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final PrimaryReportsAdapter.ItemRowHolder holder, int position) {

        PrimaryReportEntity entity = itemList.get(position);
        if(entity !=null){
            holder.binding.invNo.setText(entity.getINVNO());
            holder.binding.tvDate.setText(entity.getINVOICEDA());
            holder.binding.tvItem.setText(entity.getDIVISION());
            holder.binding.tvItemCode.setText(entity.getITEMCODE());

        }
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ReportRowBinding binding;

        ItemRowHolder(ReportRowBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}


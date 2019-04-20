package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ReportSecDispatchRowBinding;
import com.luminous.mpartner.network.entities.SecondaryDispatchReportEntity;

import java.util.List;

public class SecondaryDispatchReportAdapter extends RecyclerView.Adapter<SecondaryDispatchReportAdapter.ItemRowHolder>{

    private Context context;
    private List<SecondaryDispatchReportEntity> itemList;

    public SecondaryDispatchReportAdapter(Context context, List<SecondaryDispatchReportEntity> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public SecondaryDispatchReportAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ReportSecDispatchRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.report_sec_dispatch_row, parent, false);
        return new SecondaryDispatchReportAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SecondaryDispatchReportAdapter.ItemRowHolder holder, int i) {

        SecondaryDispatchReportEntity entity = itemList.get(i);

        holder.binding.tvDate.setText(entity.getDispatchDate());
        holder.binding.tvDealerCode.setText(entity.getDlrCode());
        holder.binding.tvDealerName.setText(entity.getDlrName());
        holder.binding.tvQuantity.setText(entity.getQuantity());
        holder.binding.tvSkumodel.setText(entity.getSKUModelName());
        holder.binding.tvWithoutSn.setText(entity.getWithoutSerialNumber());
        holder.binding.tvWithSn.setText(entity.getWithSerialNumber());
        
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        ReportSecDispatchRowBinding binding;

        public ItemRowHolder(ReportSecDispatchRowBinding binding ) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}

package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ReportPrimaryBillingBinding;
import com.luminous.mpartner.network.entities.PrimaryBillingReportEntity;
import com.luminous.mpartner.network.entities.PrimaryReportEntity;

import java.util.List;

public class PrimaryBillingReportAdapter extends RecyclerView.Adapter<PrimaryBillingReportAdapter.ItemViewHolder> {

    private Context context;
    private List<PrimaryBillingReportEntity> itemList;

    public PrimaryBillingReportAdapter(Context context, List<PrimaryBillingReportEntity> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ReportPrimaryBillingBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.report_primary_billing, parent, false);
        return new PrimaryBillingReportAdapter.ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {

        PrimaryBillingReportEntity entity = itemList.get(i);

        holder.binding.tvInvoiceNo.setText(entity.getINVNO());
        holder.binding.tvAmount.setText(entity.getAMOUNT());
        holder.binding.tvDate.setText(entity.getINVOICEDA());
        holder.binding.tvDiv.setText(entity.getDIVISION());
        holder.binding.tvItemCode.setText(entity.getITEMCODE());
        holder.binding.tvItemName.setText(entity.getMATERIALDESC());
        holder.binding.tvQuantity.setText(entity.getINVQTY());

    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ReportPrimaryBillingBinding binding;

        public ItemViewHolder(@NonNull ReportPrimaryBillingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

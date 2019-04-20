package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ReportCustomerLedgerBinding;
import com.luminous.mpartner.network.entities.CustomerLedgerReportEntity;

import java.util.List;

public class CustomerLedgerReportAdapter extends RecyclerView.Adapter<CustomerLedgerReportAdapter.ItemHolder> {

    private Context context;
    private List<CustomerLedgerReportEntity> entityList;

    public CustomerLedgerReportAdapter(Context context, List<CustomerLedgerReportEntity> entityList){
        this.context = context;
        this.entityList = entityList;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ReportCustomerLedgerBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.report_customer_ledger, parent, false);
        return new CustomerLedgerReportAdapter.ItemHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int i) {

        CustomerLedgerReportEntity entity = entityList.get(i);
        holder.binding.tvCdrc.setText(entity.getCDRC());
        holder.binding.tvChannelTxt.setText(entity.getCHANNELTXT());
        holder.binding.tvCrAmt.setText(entity.getCREDITAMOUNT());
        holder.binding.tvDivTxt.setText(entity.getDIVISIONTXT());
        holder.binding.tvDocDate.setText(entity.getDOCDATE());
        holder.binding.tvDocNo.setText(entity.getDOCNO());
        holder.binding.tvDrAmt.setText(entity.getDEBITAMOUNT());
        holder.binding.tvRefOrgUn.setText(entity.getREFORGUN());
        holder.binding.tvTotalCr.setText(entity.getTOTALCREDIT());


    }

    @Override
    public int getItemCount() {
        return entityList == null ? 0 : entityList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        ReportCustomerLedgerBinding binding;

        public ItemHolder(ReportCustomerLedgerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

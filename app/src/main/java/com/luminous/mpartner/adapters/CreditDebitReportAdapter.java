package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ReportCreditDebitBinding;
import com.luminous.mpartner.network.entities.CreditDebitReportEntity;

import java.util.List;

public class CreditDebitReportAdapter extends RecyclerView.Adapter<CreditDebitReportAdapter.ItemViewHolder> {

    private Context context;
    private List<CreditDebitReportEntity> list;

    public CreditDebitReportAdapter(Context context, List<CreditDebitReportEntity> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ReportCreditDebitBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.report_credit_debit, parent, false);
        return new CreditDebitReportAdapter.ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {

        CreditDebitReportEntity entity = list.get(i);
        holder.binding.tvDocNo.setText(entity.getAccountingDocumentNumber());
        holder.binding.tvDocType.setText(entity.getDocumenttype());
        holder.binding.tvItemTxt.setText(entity.getItemText());
        holder.binding.tvLocalCurr.setText(entity.getLocalcurrency());
        holder.binding.tvPostingDate.setText(entity.getPostingDate());
        holder.binding.tvAssignmentNumber.setText(entity.getAssignmentnumber());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ReportCreditDebitBinding binding;

        public ItemViewHolder(ReportCreditDebitBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

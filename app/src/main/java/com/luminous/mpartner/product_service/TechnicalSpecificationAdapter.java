package com.luminous.mpartner.product_service;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.TechnicalSpecificationRowItemBinding;
import com.luminous.mpartner.network.entities.TechSpecification;

import java.util.List;

public class TechnicalSpecificationAdapter extends RecyclerView.Adapter<TechnicalSpecificationAdapter.ItemRowHolder> {

    private Context context;
    private List<TechSpecification> specificationList;

    public TechnicalSpecificationAdapter(Context context, List<TechSpecification> specificationList) {
        this.context = context;
        this.specificationList = specificationList;
    }

    @NonNull
    @Override
    public TechnicalSpecificationAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TechnicalSpecificationRowItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.technical_specification_row_item, parent, false);
        return new TechnicalSpecificationAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TechnicalSpecificationAdapter.ItemRowHolder holder, int position) {
        TechSpecification techSpecification = specificationList.get(position);
        if (techSpecification != null && !TextUtils.isEmpty(techSpecification.getColumnName())) {
            holder.binding.nameTv.setText(techSpecification.getColumnName());
        }
        if (techSpecification != null && !TextUtils.isEmpty(techSpecification.getValue())) {
            holder.binding.valueTv.setText(techSpecification.getValue());
        }
    }

    @Override
    public int getItemCount() {
        return specificationList != null ? specificationList.size() : 0;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TechnicalSpecificationRowItemBinding binding;

        ItemRowHolder(TechnicalSpecificationRowItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}

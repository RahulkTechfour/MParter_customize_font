package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.RowRvViewSalesOrderBinding;
import com.luminous.mpartner.network.entities.ViewSalesOrderResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class ViewSalesOrderAdapter extends RecyclerView.Adapter<ViewSalesOrderAdapter.ItemViewHolder> {

    private Context context;
    private ArrayList<ViewSalesOrderResponseEntity> list;

    public ViewSalesOrderAdapter(Context context, ArrayList<ViewSalesOrderResponseEntity> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RowRvViewSalesOrderBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.row_rv_view_sales_order, parent, false);
        return new ViewSalesOrderAdapter.ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {

        ViewSalesOrderResponseEntity entity = list.get(i);
        holder.binding.tvProductCode.setText(entity.getMATERIALNUMBER());
        holder.binding.tvQty.setText(entity.getQUANTITY());
        holder.binding.tvNetPrice.setText(entity.getNETPRICE());
        holder.binding.tvNetValue.setText(entity.getNETVALUE());
        holder.binding.tvOrderDate.setText(entity.getORDERDATE());
        holder.binding.tvSalesOrder.setText(entity.getSALESORDE());
        holder.binding.tvStatus.setText(entity.getSTATUS());

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private final RowRvViewSalesOrderBinding binding;

        public ItemViewHolder(RowRvViewSalesOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}

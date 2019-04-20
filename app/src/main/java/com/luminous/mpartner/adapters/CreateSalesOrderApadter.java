package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.RowRvCreateSaleOrderBinding;
import com.luminous.mpartner.sales.OrderEntity;

import java.util.ArrayList;

public class CreateSalesOrderApadter extends RecyclerView.Adapter<CreateSalesOrderApadter.ItemViewHolder> {


    private Context context;
    private ArrayList<OrderEntity> list;

    public interface OnClickHandler{
        public void onClickItem(int i);
    }

    OnClickHandler handler;

    public CreateSalesOrderApadter(Context context, ArrayList<OrderEntity> list, OnClickHandler handler){
        this.context =context;
        this.list = list;
        this.handler = handler;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RowRvCreateSaleOrderBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.row_rv_create_sale_order, parent, false);
        return new CreateSalesOrderApadter.ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {

        OrderEntity entity = list.get(i);
        holder.binding.tvProductCode.setText(entity.getpId());
        holder.binding.tvDescription.setText(entity.getpDesc());
        holder.binding.tvQty.setText(entity.getpQty());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private final RowRvCreateSaleOrderBinding binding;

        public ItemViewHolder(RowRvCreateSaleOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.ivRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = getAdapterPosition();
                    handler.onClickItem(i);
                }
            });
        }
    }

}

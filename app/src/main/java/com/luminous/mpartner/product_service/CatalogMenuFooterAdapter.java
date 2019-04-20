package com.luminous.mpartner.product_service;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luminous.cardslibrary.listeners.RecyclerItemClickListener;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.CatalogFooterItemBinding;
import com.luminous.mpartner.network.entities.ProductCategoryEntity;
import com.luminous.mpartner.utilities.OnRecyclerViewItemClickListener;

import java.util.List;

public class CatalogMenuFooterAdapter extends RecyclerView.Adapter<CatalogMenuFooterAdapter.ItemRowHolder> {
    private Context context;
    private List<ProductCategoryEntity> catalogFooterList;
    private OnRecyclerViewItemClickListener itemClickListener;
    private int clickablePosition = 0;

    public CatalogMenuFooterAdapter(Context context, List<ProductCategoryEntity> catalogFooterList, OnRecyclerViewItemClickListener itemClickListener) {
        this.context = context;
        this.catalogFooterList = catalogFooterList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public CatalogMenuFooterAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CatalogFooterItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.catalog_footer_item, parent, false);
        return new CatalogMenuFooterAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogMenuFooterAdapter.ItemRowHolder holder, int position) {

        ProductCategoryEntity productCategoryEntity = catalogFooterList.get(position);
        if (productCategoryEntity != null && !TextUtils.isEmpty(productCategoryEntity.getProductCatagoryName())) {
            holder.binding.tvTitle.setText(productCategoryEntity.getProductCatagoryName());
        }

        if (position == clickablePosition) {
            holder.binding.tvTitle.setTextColor(context.getResources().getColor(R.color.square_wave));
        } else {
            holder.binding.tvTitle.setTextColor(context.getResources().getColor(R.color.gray_black));
        }

        holder.itemView.setOnClickListener(v -> itemClickListener.onItemCLicked(holder.itemView, holder.getAdapterPosition(), "footer"));
    }

    @Override
    public int getItemCount() {
        return catalogFooterList == null ? 0 : catalogFooterList.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        CatalogFooterItemBinding binding;

        ItemRowHolder(CatalogFooterItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }

    public void setClickablePosition(int position) {
        clickablePosition = position;
    }

    public int getClickablePosition() {
        return clickablePosition;
    }
}
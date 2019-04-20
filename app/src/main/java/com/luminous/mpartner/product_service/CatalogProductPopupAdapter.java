package com.luminous.mpartner.product_service;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.luminous.mpartner.MyApplication;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.CatalogProductRowItemBinding;
import com.luminous.mpartner.events.ProductCatalogClickEvent;
import com.luminous.mpartner.network.entities.ProductCatalog;

import java.util.List;

public class CatalogProductPopupAdapter extends RecyclerView.Adapter<CatalogProductPopupAdapter.ItemRowHolder> {
    private Context context;
    private List<ProductCatalog> catalogProductList;
    private PopupWindow popupWindow;

    public CatalogProductPopupAdapter(Context context, List<ProductCatalog> catalogProductList, PopupWindow popupWindow) {
        this.context = context;
        this.catalogProductList = catalogProductList;
        this.popupWindow = popupWindow;
    }

    @NonNull
    @Override
    public CatalogProductPopupAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CatalogProductRowItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.catalog_product_row_item, parent, false);
        return new CatalogProductPopupAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogProductPopupAdapter.ItemRowHolder holder, int position) {

        ProductCatalog productCatalog = catalogProductList.get(position);
        if (productCatalog != null && !TextUtils.isEmpty(productCatalog.getProductioncatalogName())) {
            holder.binding.tvTitle.setText(productCatalog.getProductioncatalogName());
        }

        holder.binding.tvTitle.setOnClickListener(v -> {
            MyApplication.getApplication()
                    .bus()
                    .send(new ProductCatalogClickEvent(productCatalog.getId()));
            if (popupWindow != null && popupWindow.isShowing())
                popupWindow.dismiss();
        });


    }

    @Override
    public int getItemCount() {
        return catalogProductList == null ? 0 : catalogProductList.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        CatalogProductRowItemBinding binding;

        ItemRowHolder(CatalogProductRowItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}
package com.luminous.mpartner.product_service;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ProductDetailsRowItemBinding;
import com.luminous.mpartner.network.entities.ProductCatalog;
import com.luminous.mpartner.network.entities.ProductCatalogDetail;
import com.luminous.mpartner.utilities.OnRecyclerViewItemClickListener;

import java.util.HashMap;
import java.util.List;

public class CatalogProductRecyclerViewAdapter extends RecyclerView.Adapter<CatalogProductRecyclerViewAdapter.ItemRowHolder> {
    private Context context;
    private List<ProductCatalog> productCatalogList;
    private OnRecyclerViewItemClickListener itemClickListener;
    private HashMap<Integer, ProductCatalogDetail> productCatalogHashMap;
    private int clickablePosition = -1;
    private boolean isFiltered;


    public CatalogProductRecyclerViewAdapter(Context context, List<ProductCatalog> productCatalogList, OnRecyclerViewItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.productCatalogList = productCatalogList;
    }

    @NonNull
    @Override
    public CatalogProductRecyclerViewAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ProductDetailsRowItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.product_details_row_item, parent, false);
        return new CatalogProductRecyclerViewAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final CatalogProductRecyclerViewAdapter.ItemRowHolder holder, int position) {


        if (position%2 == 0){
            holder.binding.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.blue_catalog_item));
        } else
            holder.binding.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.green_catalog_item));



        ProductCatalog productCatalog = productCatalogList.get(position);
        if (productCatalog != null && !TextUtils.isEmpty(productCatalog.getProductcatalogImageUrl())) {
            Glide.with(context)
                    .load(productCatalog.getProductcatalogImageUrl())
                    .into(holder.binding.imageView);

//            holder.binding.imageView.setOnClickListener(v -> {
//                itemClickListener.onItemCLicked(v, holder.getAdapterPosition());
//            });
        }

        holder.binding.tvName.setText(productCatalog.getProductioncatalogName());
        if(productCatalog.getProductioncatalogRating()!=null && productCatalog.getProductioncatalogRating()!="")
        holder.binding.tvRatings.setText("Rating - " + productCatalog.getProductioncatalogRating());

        //
        if (productCatalog != null && !TextUtils.isEmpty(productCatalog.getKeyfeature())) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                holder.binding.tvKeyfeature.setText(Html.fromHtml(productCatalog.getKeyfeature(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.binding.tvKeyfeature.setText(Html.fromHtml(productCatalog.getKeyfeature()));
            }
        }
        if (productCatalog != null && !TextUtils.isEmpty(productCatalog.getWarrenty())) {
            holder.binding.tvWarranty.setText("- " + productCatalog.getWarrenty());
        }

        if (productCatalog != null && productCatalog.getTechSpecification() != null &&
                productCatalog.getTechSpecification().size() > 0) {
            DividerItemDecoration verticalDecoration = new DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL);
            Drawable verticalDivider = ContextCompat.getDrawable(context, R.drawable.vertical_divider_white);
            verticalDecoration.setDrawable(verticalDivider);
            holder.binding.specificationRecycleview.addItemDecoration(verticalDecoration);
            TechnicalSpecificationAdapter technicalSpecificationAdapter = new TechnicalSpecificationAdapter(context, productCatalog.getTechSpecification());
            holder.binding.specificationRecycleview.setAdapter(technicalSpecificationAdapter);
        }
        if (isFiltered || clickablePosition == position) {
            holder.binding.featureContainer.setVisibility(View.VISIBLE);
            holder.binding.ivRotate.setImageDrawable(context.getResources().getDrawable(R.drawable.up_arrow));
        } else {
            holder.binding.featureContainer.setVisibility(View.GONE);
            holder.binding.ivRotate.setImageDrawable(context.getResources().getDrawable(R.drawable.down_arrow));
        }

    }

    @Override
    public int getItemCount() {
        return productCatalogList == null ? 0 : productCatalogList.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ProductDetailsRowItemBinding binding;

        ItemRowHolder(ProductDetailsRowItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;

            binding.linearLayout.setOnClickListener(v -> {
                if (binding.featureContainer.getVisibility() == View.VISIBLE) {
                    binding.featureContainer.setVisibility(View.GONE);
                    binding.ivRotate.setImageDrawable(context.getResources().getDrawable(R.drawable.down_arrow));
                } else {
                    binding.featureContainer.setVisibility(View.VISIBLE);
                    binding.ivRotate.setImageDrawable(context.getResources().getDrawable(R.drawable.up_arrow));
                }
            });
        }

    }

    //to check if filtered item or not
    public void setClickablePosition(int clickablePosition) {
        this.clickablePosition = clickablePosition;
    }

    public void setIsFiltered(boolean isFiltered) {
        this.isFiltered = isFiltered;
    }
}
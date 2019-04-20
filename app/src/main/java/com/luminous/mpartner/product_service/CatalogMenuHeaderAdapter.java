package com.luminous.mpartner.product_service;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.CatalogHeaderItemBinding;
import com.luminous.mpartner.network.entities.ProductCatalog;
import com.luminous.mpartner.network.entities.ProductCatalogUpper;
import com.luminous.mpartner.utilities.OnRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CatalogMenuHeaderAdapter extends RecyclerView.Adapter<CatalogMenuHeaderAdapter.ItemRowHolder> {
    private Context context;
    private List<ProductCatalogUpper> catalogHeaderList;
    private List<ProductCatalog> productCatalogList;
    private int clickablePosition = -1;
    private OnRecyclerViewItemClickListener itemClickListener;
    private PopupWindow popupWindow;

    public CatalogMenuHeaderAdapter(Context context, List<ProductCatalogUpper> catalogHeaderList, List<ProductCatalog> productCatalogList, OnRecyclerViewItemClickListener itemClickListener) {
        this.context = context;
        this.catalogHeaderList = catalogHeaderList;
        this.itemClickListener = itemClickListener;
        this.productCatalogList = productCatalogList;
    }

    @NonNull
    @Override
    public CatalogMenuHeaderAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CatalogHeaderItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.catalog_header_item, parent, false);
        return new CatalogMenuHeaderAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogMenuHeaderAdapter.ItemRowHolder holder, int position) {

        ProductCatalogUpper productCatalogUpper = catalogHeaderList.get(position);
        if (productCatalogUpper != null && !TextUtils.isEmpty(productCatalogUpper.getCatalogMenuUpperName())) {
            holder.binding.tvTitle.setText(productCatalogUpper.getCatalogMenuUpperName());


            if (position == clickablePosition) {
                holder.binding.llHeader.setBackground(context.getResources().getDrawable(R.color.square_wave));
                holder.binding.tvTitle.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                holder.binding.llHeader.setBackground(context.getResources().getDrawable(R.color.gray_1));
                holder.binding.tvTitle.setTextColor(context.getResources().getColor(R.color.square_wave));
            }

            holder.binding.tvTitle.setOnClickListener(v -> {

                clickablePosition = position;
                showProductCatalogPopup(holder.itemView, productCatalogUpper.getCatalogMenuUpperName(), productCatalogUpper.getFilterKey());
                itemClickListener.onItemCLicked(v, holder.getAdapterPosition());
                notifyDataSetChanged();
            });
        }
    }

    @Override
    public int getItemCount() {
        return catalogHeaderList == null ? 0 : catalogHeaderList.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        CatalogHeaderItemBinding binding;

        ItemRowHolder(CatalogHeaderItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }

    public void setClickablePosition(int position) {
        clickablePosition = position;
        notifyDataSetChanged();
    }
// open list
    private void showProductCatalogPopup(View anchorView, String catalogName, String filterKey) {

        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.catalog_products_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = 800;
        boolean focusable = true; // lets taps outside the popup also dismiss it if true
        popupWindow = new PopupWindow(popupView, width, height, focusable);


        popupWindow.setOnDismissListener(() -> {
            clickablePosition = -1;
            notifyDataSetChanged();
            itemClickListener.onItemCLicked(null, -1);
        });
        // show the popup window
        popupWindow.showAsDropDown(anchorView);

        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        List<ProductCatalog> filtered = new ArrayList<>();
        if (filterKey.equalsIgnoreCase("attribute_name"))
            for (ProductCatalog productCatalog : productCatalogList) {
                if (productCatalog.getAttribute_name().equalsIgnoreCase(catalogName)) {
                    filtered.add(productCatalog);
                }
            }
        else
            for (ProductCatalog productCatalog : productCatalogList) {
                if (productCatalog.getProductleveltwo().equalsIgnoreCase(catalogName)) {
                    filtered.add(productCatalog);
                }
            }

        CatalogProductPopupAdapter catalogProductPopupAdapter = new CatalogProductPopupAdapter(context, filtered, popupWindow);
        recyclerView.setAdapter(catalogProductPopupAdapter);

    }
}
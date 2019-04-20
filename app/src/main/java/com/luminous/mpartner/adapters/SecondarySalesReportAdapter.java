package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ReportSecSalesRowBinding;
import com.luminous.mpartner.network.entities.SecondarySalesReportEntity;

import java.util.List;

public class SecondarySalesReportAdapter extends RecyclerView.Adapter<SecondarySalesReportAdapter.ItemRowHolder> {

    private Context context;
    private List<SecondarySalesReportEntity> itemList;
    private String [] itemTypes;

    public SecondarySalesReportAdapter(Context context, List<SecondarySalesReportEntity> itemList, String [] itemTypes) {
        this.context = context;
        this.itemList = itemList;
        this.itemTypes = itemTypes;
    }


    @NonNull
    @Override
    public SecondarySalesReportAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ReportSecSalesRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.report_sec_sales_row, parent, false);
        return new SecondarySalesReportAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SecondarySalesReportAdapter.ItemRowHolder holder, int i) {

        SecondarySalesReportEntity entity = itemList.get(i);
        holder.binding.tvModelName.setText(entity.getSKUModelName());

        int itemId = Integer.parseInt(entity.getItemID());

        holder.binding.tvProductCategory.setText(itemTypes[itemId]);
        holder.binding.tvQuantity.setText(entity.getTAvlQuantity());
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        ReportSecSalesRowBinding binding;

        public ItemRowHolder( ReportSecSalesRowBinding reportSecSalesBinding) {
            super(reportSecSalesBinding.getRoot());
            binding = reportSecSalesBinding;

        }
    }
}

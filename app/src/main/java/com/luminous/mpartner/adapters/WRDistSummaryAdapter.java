package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.RowLvDistWrBinding;
import com.luminous.mpartner.network.entities.WarrantyReport.DistWREntity;

import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;

public class WRDistSummaryAdapter extends RecyclerView.Adapter<WRDistSummaryAdapter.ItemHolder> {

    Context context;
    ArrayList<DistWREntity>  list;
    String [] particulars = {"No of cards Submitted","No of cards Accepted","Moved to Next WRS","No of card Rejected"};
    int total, accepted, rejected, wrs;

    public WRDistSummaryAdapter(Context context, ArrayList<DistWREntity> list){

        this.context = context;
        this.list = list;
        total = 0;
        accepted = 0;
        rejected = 0;
        wrs = 0;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RowLvDistWrBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.row_lv_dist_wr, parent, false);
        return new WRDistSummaryAdapter.ItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {

        if (i<list.size()) {
            DistWREntity entity = list.get(i);

            itemHolder.binding.product.setText(entity.getProduct());
            itemHolder.binding.submitted.setText(entity.getTotal());
            itemHolder.binding.accepted.setText(entity.getAccepted());
            itemHolder.binding.nextwrs.setText(entity.getWRSPoint());
            itemHolder.binding.rejected.setText(entity.getRejected());

            total += Integer.parseInt(entity.getTotal());
            rejected += Integer.parseInt(entity.getRejected());
            accepted += Integer.parseInt(entity.getAccepted());
            wrs += Integer.parseInt(entity.getWRSPoint());
        } else {
            itemHolder.binding.product.setText("Grand Total");
            itemHolder.binding.submitted.setText(total + "");
            itemHolder.binding.accepted.setText(accepted + "");
            itemHolder.binding.nextwrs.setText(wrs + "");
            itemHolder.binding.rejected.setText(rejected + "");
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size()+1;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        RowLvDistWrBinding binding;

        public ItemHolder(RowLvDistWrBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}

package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.RowViewDealaerListBinding;
import com.luminous.mpartner.network.entities.DealerListEntity;
import com.luminous.mpartner.network.entities.DealerListResponseEntity;

import java.util.ArrayList;

public class ViewDealerListApapter extends RecyclerView.Adapter<ViewDealerListApapter.ItemViewHolder> {

    private ArrayList<DealerListEntity> entities;
    private Context context;


    public ViewDealerListApapter(Context context, ArrayList<DealerListEntity> entities){
        this.context = context;
        this.entities = entities;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RowViewDealaerListBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.row_view_dealaer_list, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {

        DealerListEntity entity = entities.get(i);

        holder.binding.tvCity.setText(entity.getCITYDESC());
        holder.binding.tvState.setText(entity.getSTATEDESC());
        holder.binding.tvStatus.setText(entity.getSTATUSDES());
        holder.binding.tvDealerCode.setText(entity.getDEALERCOD());
        holder.binding.tvDealerName.setText(entity.getDEALERNAME());
        holder.binding.tvMobile.setText(entity.getMOBILE1());


        if (i%2 != 0){
            holder.binding.row.setBackgroundColor(context.getResources().getColor(R.color.gray_1));
        }


    }

    @Override
    public int getItemCount() {
        return entities == null ? 0 : entities.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private final RowViewDealaerListBinding  binding;

        public ItemViewHolder(RowViewDealaerListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

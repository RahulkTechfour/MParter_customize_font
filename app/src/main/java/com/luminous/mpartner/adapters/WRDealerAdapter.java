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
import com.luminous.mpartner.databinding.RowRvWrDealerBinding;
import com.luminous.mpartner.network.entities.DealerListEntity;
import com.luminous.mpartner.network.entities.WarrantyReport.DealerResonseEntity;

import java.util.ArrayList;

public class WRDealerAdapter extends RecyclerView.Adapter<WRDealerAdapter.ItemHolder> {

    Context context;
    ArrayList<DealerResonseEntity> listEntities;

    public WRDealerAdapter(Context context, ArrayList<DealerResonseEntity> listEntities){
        this.context = context;
        this.listEntities = listEntities;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RowRvWrDealerBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.row_rv_wr_dealer, parent, false);
        return new WRDealerAdapter.ItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int i) {

        DealerResonseEntity entity = listEntities.get(i);
        holder.binding.tvDealerCode.setText(entity.getDlrCode());
        holder.binding.tvDealerName.setText(entity.getDlrName());
        holder.binding.tvHkvaS.setText(entity.getSubHKVA());
        holder.binding.tvHkvaA.setText(entity.getSubHKVA());
        holder.binding.tvHomeA.setText(entity.getAccptHomeUps());
        holder.binding.tvHomeS.setText(entity.getSubHomeUps());
        holder.binding.tvHomePanelA.setText(entity.getAccptHomePanel());
        holder.binding.tvHomePanelS.setText(entity.getSubHomePanel());
        holder.binding.tvHomeStabA.setText(entity.getAccptHomeStabl());
        holder.binding.tvHomeStabS.setText(entity.getSubHomeStabl());
        holder.binding.tvIBA.setText(entity.getAccptBty());
        holder.binding.tvIBS.setText(entity.getSubBty());
        holder.binding.sGrand.setText(entity.getSubTotal());
        holder.binding.aGrand.setText(entity.getAccptTotal());
    }

    @Override
    public int getItemCount() {
        return listEntities == null ? 0 : listEntities.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        RowRvWrDealerBinding binding;
        public ItemHolder(RowRvWrDealerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

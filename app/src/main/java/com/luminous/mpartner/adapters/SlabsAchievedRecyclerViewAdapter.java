package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.SlabAchievedRowBinding;
import com.luminous.mpartner.network.entities.WRStatusReportCardEntity;

import java.util.ArrayList;


public class SlabsAchievedRecyclerViewAdapter extends RecyclerView.Adapter<SlabsAchievedRecyclerViewAdapter.ItemRowHolder> {

    private Context context;
    private ArrayList<WRStatusReportCardEntity> priceList;


    public SlabsAchievedRecyclerViewAdapter(Context context, ArrayList<WRStatusReportCardEntity> priceList) {
        this.context = context;
        this.priceList = priceList;


    }


    @NonNull
    @Override
    public SlabsAchievedRecyclerViewAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SlabAchievedRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.slab_achieved_row, parent, false);
        return new SlabsAchievedRecyclerViewAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SlabsAchievedRecyclerViewAdapter.ItemRowHolder holder, int position) {


        WRStatusReportCardEntity wrStatusReportCardEntity = priceList.get(position);
//        holder.binding.product.setText(WRStatusReportCardEntity.keys[position]);
//        String acc = priceList.get(1).toString();
//        String rej = priceList.get(2).toString();
//
//        String [] accA = acc.split(",");
//        for (int i=0; i<accA.length; i++){
//            String obj = accA[i].trim();
//            String [] objA = obj.split(" ");
//            if (i == position)
//                holder.binding.accpeted.setText(objA[1]);
//        }
//
//
//        String [] rejA = rej.split(",");
//        for (int i=0; i<rejA.length; i++){
//            String obj = rejA[i].trim();
//            String [] objA = obj.split(" ");
//            if (i == position)
//                holder.binding.rejected.setText(objA[1]);
//        }

        holder.binding.product.setText(wrStatusReportCardEntity.getProduct());
        holder.binding.total.setText(wrStatusReportCardEntity.getTotal());
        holder.binding.accpeted.setText(wrStatusReportCardEntity.getAccepted());
        holder.binding.rejected.setText(wrStatusReportCardEntity.getRejected());
        holder.binding.points.setText(wrStatusReportCardEntity.getWRSPoint());

    }

    @Override
    public int getItemCount() {
        if (priceList == null)
            return 0;
        else
            return priceList.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        SlabAchievedRowBinding binding;

        ItemRowHolder(SlabAchievedRowBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}

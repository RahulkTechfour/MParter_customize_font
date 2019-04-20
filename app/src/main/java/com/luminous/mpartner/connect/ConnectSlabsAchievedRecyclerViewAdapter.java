package com.luminous.mpartner.connect;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ConnectSlabAchievedRowBinding;
import com.luminous.mpartner.network.entities.SerWRReportConnect2CardRewardPonitsEntity;

import java.util.List;


public class ConnectSlabsAchievedRecyclerViewAdapter extends RecyclerView.Adapter<ConnectSlabsAchievedRecyclerViewAdapter.ItemRowHolder> {

    private Context context;
    private List<SerWRReportConnect2CardRewardPonitsEntity> list;

    public ConnectSlabsAchievedRecyclerViewAdapter(Context context, List<SerWRReportConnect2CardRewardPonitsEntity> priceList) {
        this.context = context;
        this.list = priceList;
    }

    @NonNull
    @Override
    public ConnectSlabsAchievedRecyclerViewAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ConnectSlabAchievedRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.connect_slab_achieved_row, parent, false);
        return new ConnectSlabsAchievedRecyclerViewAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ConnectSlabsAchievedRecyclerViewAdapter.ItemRowHolder holder, int position) {

        SerWRReportConnect2CardRewardPonitsEntity data = list.get(position);
        if (data != null && data.getPointNextSlab().equalsIgnoreCase("Slab")) {
            holder.binding.tvCountryName.setText(data.getCurrentSlab());
            holder.binding.tvCount.setText(data.getPointsEarned());
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ConnectSlabAchievedRowBinding binding;

        ItemRowHolder(ConnectSlabAchievedRowBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}

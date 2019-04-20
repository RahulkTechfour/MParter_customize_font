package com.luminous.mpartner.connect;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.PontsAchievedRowBinding;
import com.luminous.mpartner.network.entities.SerWRReportConnect2CardRewardPonitsEntity;

import java.util.List;

public class ConnectRewardPointAdapter extends RecyclerView.Adapter<ConnectRewardPointAdapter.ItemRowHolder> {
    private Context context;
    private List<SerWRReportConnect2CardRewardPonitsEntity> itemList;
    int manPosition;

    public ConnectRewardPointAdapter(Context context, List<SerWRReportConnect2CardRewardPonitsEntity> itemList, int manPosition) {
        this.context = context;
        this.itemList = itemList;
        this.manPosition = manPosition;
    }

    @NonNull
    @Override
    public ConnectRewardPointAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PontsAchievedRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.ponts_achieved_row, parent, false);
        return new ConnectRewardPointAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectRewardPointAdapter.ItemRowHolder holder, int position) {

        SerWRReportConnect2CardRewardPonitsEntity entity = itemList.get(position);

        if (position == manPosition) {
            holder.binding.relativeLayout.setVisibility(View.VISIBLE);
            holder.binding.btn1.setImageDrawable(context.getResources().getDrawable(R.drawable.man));
            holder.binding.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.dark_blue_bubble));
        } else if (position < manPosition) {
            holder.binding.relativeLayout.setVisibility(View.VISIBLE);
            holder.binding.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.bubble));
            holder.binding.btn1.setImageDrawable(context.getResources().getDrawable(R.drawable.unlock_icon));
        } else {
            holder.binding.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.grey_bubble));
            holder.binding.relativeLayout.setVisibility(View.VISIBLE);
            holder.binding.btn1.setImageDrawable(context.getResources().getDrawable(R.drawable.lock_icon));
        }

        holder.binding.text.setText(entity.getCurrentSlab());

//        if (position == itemList.size() - 1) {
//            holder.binding.view.setVisibility(View.GONE);
//        } else {
//            holder.binding.view.setVisibility(View.VISIBLE);
//        }


    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        PontsAchievedRowBinding binding;

        ItemRowHolder(PontsAchievedRowBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}

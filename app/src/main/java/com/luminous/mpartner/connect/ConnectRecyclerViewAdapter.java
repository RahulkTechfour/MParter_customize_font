package com.luminous.mpartner.connect;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.ConnectHorizontalImageRowBinding;
import com.luminous.mpartner.dynamic_home.entities.ImageEntity;
import com.luminous.mpartner.utilities.OnRecyclerViewItemClickListener;

import java.util.List;

public class ConnectRecyclerViewAdapter extends RecyclerView.Adapter<ConnectRecyclerViewAdapter.ItemRowHolder> {
    private Context context;
    private List<ImageEntity> itemList;

    public ConnectRecyclerViewAdapter(Context context, List<ImageEntity> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ConnectRecyclerViewAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ConnectHorizontalImageRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.connect_horizontal_image_row, parent, false);
        return new ConnectRecyclerViewAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectRecyclerViewAdapter.ItemRowHolder holder, int position) {

        ImageEntity imageEntity = itemList.get(position);
        holder.binding.tvTitle.setText(imageEntity.getTitle());

        Glide.with(context).load(imageEntity.getBackgroundImage()).into(holder.binding.imageView);

    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ConnectHorizontalImageRowBinding binding;

        ItemRowHolder(ConnectHorizontalImageRowBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}

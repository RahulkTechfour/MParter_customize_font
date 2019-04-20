package com.luminous.mpartner.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.NavDrawerRowItemBinding;
import com.luminous.mpartner.network.entities.PermissionDatum;
import com.luminous.mpartner.utilities.OnRecyclerViewItemClickListener;

import java.util.List;

public class NavDrawerRecyclerViewAdapter extends RecyclerView.Adapter<NavDrawerRecyclerViewAdapter.ItemRowHolder> {

    private Context context;
    private List<PermissionDatum> itemList;
    private OnRecyclerViewItemClickListener itemClickListener;

    public NavDrawerRecyclerViewAdapter(Context context, List<PermissionDatum> itemList, OnRecyclerViewItemClickListener itemClickListener) {
        this.context = context;
        this.itemList = itemList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public NavDrawerRecyclerViewAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        NavDrawerRowItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.nav_drawer_row_item, parent, false);
        return new NavDrawerRecyclerViewAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final NavDrawerRecyclerViewAdapter.ItemRowHolder holder, int position) {

        PermissionDatum permissionDatum = itemList.get(position);


        holder.binding.navText.setText(permissionDatum.getModuleText());
        Glide.with(context).load(permissionDatum.getModuleImage()).into(holder.binding.imageView);

        holder.itemView.setOnClickListener(v -> itemClickListener.onItemCLicked(v, holder.getAdapterPosition(), permissionDatum.getModuleName()));

    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        NavDrawerRowItemBinding binding;

        ItemRowHolder(NavDrawerRowItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}


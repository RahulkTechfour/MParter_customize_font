package com.luminous.mpartner.lucky7;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luminous.mpartner.R;
import com.luminous.mpartner.network.entities.LsdDealerReport;

import java.util.List;

public class ReportsRecyclerViewAdapter extends RecyclerView.Adapter<ReportsRecyclerViewAdapter.MyViewHolder> {

    private List<LsdDealerReport> reportsList;
    private Context context;


    public ReportsRecyclerViewAdapter(Context context, List<LsdDealerReport> reportsList) {
        this.reportsList = reportsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dealer_row_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        LsdDealerReport dealerReport = reportsList.get(position);
        if (position % 2 == 0) {
            holder.tv_barcode.setBackgroundColor(context.getResources().getColor(R.color.grey_400));
            holder.tv_secret_code.setBackgroundColor(context.getResources().getColor(R.color.grey_400));
            holder.tv_gift.setBackgroundColor(context.getResources().getColor(R.color.grey_400));
            holder.tv_distributor_name.setBackgroundColor(context.getResources().getColor(R.color.grey_400));
            holder.tv_ReimbursmentDate_Time.setBackgroundColor(context.getResources().getColor(R.color.grey_400));
        } else {
            holder.tv_barcode.setBackgroundColor(context.getResources().getColor(R.color.grey_300));
            holder.tv_secret_code.setBackgroundColor(context.getResources().getColor(R.color.grey_300));
            holder.tv_gift.setBackgroundColor(context.getResources().getColor(R.color.grey_300));
            holder.tv_distributor_name.setBackgroundColor(context.getResources().getColor(R.color.grey_300));
            holder.tv_ReimbursmentDate_Time.setBackgroundColor(context.getResources().getColor(R.color.grey_300));
        }
        holder.tv_barcode.setText(dealerReport.getBarcode());
        holder.tv_secret_code.setText(dealerReport.getSecretCode());
        holder.tv_gift.setText(dealerReport.getGift());
        holder.tv_distributor_name.setText(dealerReport.getActivatedDistName());
        holder.tv_ReimbursmentDate_Time.setText(dealerReport.getReimbursmentDateTime());
    }

    @Override
    public int getItemCount() {
        return reportsList == null ? 0 : reportsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_barcode, tv_secret_code, tv_gift, tv_distributor_name, tv_ReimbursmentDate_Time;

        public MyViewHolder(View view) {
            super(view);

            tv_ReimbursmentDate_Time = view.findViewById(R.id.tv_ReimbursmentDate_Time);
            tv_barcode = view.findViewById(R.id.tv_barcode);
            tv_secret_code = view.findViewById(R.id.tv_secret_code);
            tv_gift = view.findViewById(R.id.tv_gift);
            tv_distributor_name = view.findViewById(R.id.tv_distributor_name);
        }
    }
}

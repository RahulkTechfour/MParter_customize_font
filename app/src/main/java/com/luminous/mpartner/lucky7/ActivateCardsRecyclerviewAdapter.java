package com.luminous.mpartner.lucky7;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luminous.mpartner.R;
import com.luminous.mpartner.network.entities.LsdOpenReimbursmentDatum;

import java.util.List;

public class ActivateCardsRecyclerviewAdapter extends RecyclerView.Adapter<ActivateCardsRecyclerviewAdapter.MyViewHolder> {

    private List<LsdOpenReimbursmentDatum> claimReportList;
    private Context context;


    public ActivateCardsRecyclerviewAdapter(Context context, List<LsdOpenReimbursmentDatum> claimReportList) {
        this.claimReportList = claimReportList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activate_card_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        LsdOpenReimbursmentDatum claimReport = claimReportList.get(position);
        if (position % 2 == 0) {
            holder.tv_dealer_name.setBackgroundColor(context.getResources().getColor(R.color.grey_400));
            holder.tv_dealer_code.setBackgroundColor(context.getResources().getColor(R.color.grey_400));
            holder.tv_coupon_number.setBackgroundColor(context.getResources().getColor(R.color.grey_400));
            holder.tv_activation_date.setBackgroundColor(context.getResources().getColor(R.color.grey_400));
            holder.tv_gift_value.setBackgroundColor(context.getResources().getColor(R.color.grey_400));
            holder.tv_redemption_date.setBackgroundColor(context.getResources().getColor(R.color.grey_400));
        } else {
            holder.tv_dealer_name.setBackgroundColor(context.getResources().getColor(R.color.grey_300));
            holder.tv_dealer_code.setBackgroundColor(context.getResources().getColor(R.color.grey_300));
            holder.tv_coupon_number.setBackgroundColor(context.getResources().getColor(R.color.grey_300));
            holder.tv_activation_date.setBackgroundColor(context.getResources().getColor(R.color.grey_300));
            holder.tv_gift_value.setBackgroundColor(context.getResources().getColor(R.color.grey_300));
            holder.tv_redemption_date.setBackgroundColor(context.getResources().getColor(R.color.grey_300));
        }
        holder.tv_dealer_name.setText(claimReport.getDealerName());
        holder.tv_dealer_code.setText(claimReport.getDealerCode());
        holder.tv_coupon_number.setText(claimReport.getAlphanumericCode());
        holder.tv_activation_date.setText(claimReport.getActivationDateAndTime());
        holder.tv_gift_value.setText(claimReport.getGiftName());
        holder.tv_redemption_date.setText(claimReport.getDealerredemptionDateTime());
    }

    @Override
    public int getItemCount() {
        return claimReportList == null ? 0 : claimReportList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_dealer_name, tv_dealer_code, tv_coupon_number, tv_gift_value, tv_activation_date, tv_redemption_date;

        public MyViewHolder(View view) {
            super(view);

            tv_dealer_name = view.findViewById(R.id.tv_dealer_name);
            tv_dealer_code = view.findViewById(R.id.tv_dealer_code);
            tv_coupon_number = view.findViewById(R.id.tv_coupon_number);
            tv_gift_value = view.findViewById(R.id.tv_gift_value);
            tv_activation_date = view.findViewById(R.id.tv_activation_date);
            tv_redemption_date = view.findViewById(R.id.tv_redemption_date);
        }
    }
}

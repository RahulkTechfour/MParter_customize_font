package com.luminous.mpartner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itextpdf.text.FontProvider;
import com.luminous.mpartner.R;
import com.luminous.mpartner.connect.Dealer;
import com.luminous.mpartner.network.entities.MSerWRDistListByDlrEntity;
import com.luminous.mpartner.network.entities.MSerWRDlrListByDistEntity;
import com.luminous.mpartner.utilities.AppConstants;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import java.util.List;


public class DealersDropdownAdapter extends BaseAdapter {

    private final Context mContext;
    private List<MSerWRDistListByDlrEntity> dealerList;
    private LayoutInflater mInflator;

    public DealersDropdownAdapter(Context context, List<MSerWRDistListByDlrEntity> dealerList) {
        this.dealerList = dealerList;
        mContext = context;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflator.inflate(R.layout.textview, null);
            holder = new ViewHolder();
            holder.entryName = (TextView) convertView
                    .findViewById(R.id.TextView01);
//            holder.entryName.setTypeface(FontProvider.getInstance().tfRegular);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.entryName.setText(position == 0 ? "Select Distributer" : dealerList
                .get(position - 1).getDistName());


        return convertView;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCount() {
        return dealerList.size() + 1;
    }

    class ViewHolder {
        TextView entryName;
    }

}

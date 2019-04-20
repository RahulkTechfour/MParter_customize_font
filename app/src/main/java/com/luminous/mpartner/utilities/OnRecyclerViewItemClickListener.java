package com.luminous.mpartner.utilities;

import android.view.View;

public interface OnRecyclerViewItemClickListener {

    void onItemCLicked(View view, int position);

    void onItemCLicked(View view, int position, String source);
}

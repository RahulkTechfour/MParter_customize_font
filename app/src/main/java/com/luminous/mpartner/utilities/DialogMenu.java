package com.luminous.mpartner.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.luminous.mpartner.R;

/**
 * Created by Anusha 04/07/2018 TASK#4009
 */

public class DialogMenu implements View.OnClickListener {
    private Dialog dialog;
    private OnDialogMenuListener listener;

    public DialogMenu(Context context) {
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_item);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();


        TextView pdf = (TextView) dialog.findViewById(R.id.tvpdf);
        TextView gallery = (TextView) dialog.findViewById(R.id.gallery);
        pdf.setOnClickListener(this);
        gallery.setOnClickListener(this);

    }

    public void setListener(OnDialogMenuListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvpdf:
                listener.onPDFPress();
                dialog.dismiss();
                break;
            case R.id.gallery:
                listener.onGalleryPress();
                dialog.dismiss();
                break;
        }
    }

    public interface OnDialogMenuListener{
        void onPDFPress();
        void onGalleryPress();
    }
}

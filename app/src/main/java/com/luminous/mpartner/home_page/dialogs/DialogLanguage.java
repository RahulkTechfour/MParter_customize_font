package com.luminous.mpartner.home_page.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.luminous.mpartner.MyApplication;
import com.luminous.mpartner.R;
import com.luminous.mpartner.events.ChangeLanguageEvent;
import com.luminous.mpartner.events.ProductCatalogClickEvent;
import com.luminous.mpartner.home_page.activities.HomePageActivity;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.RxBus;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

public class DialogLanguage extends Dialog implements View.OnClickListener {

    private Activity activity;


    public DialogLanguage(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        setContentView(R.layout.dialog_language);
        setCancelable(false);
        TextView txtHindi = findViewById(R.id.txt_hindi);
        txtHindi.setOnClickListener(this);

        TextView txtEnglish = findViewById(R.id.txt_english);
        txtEnglish.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_english: {
                new SharedPrefsManager(activity).putString(SharedPreferenceKeys.USER_LANG, "EN");
                try {
                    MyApplication.getApplication()
                            .bus()
                            .send(new ChangeLanguageEvent());

                   /* if (CommonUtility.canShowOrDismissDialog(activity))
                        MyApplication.getApplication()
                                .bus()
                                .send(new ChangeLanguageEvent());*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismiss();
            }
            break;

            case R.id.txt_hindi: {
                new SharedPrefsManager(activity).putString(SharedPreferenceKeys.USER_LANG, "HI");
                try {

                    MyApplication.getApplication()
                            .bus()
                            .send(new ChangeLanguageEvent());

                  /*  if (CommonUtility.canShowOrDismissDialog(activity))
                        MyApplication.getApplication()
                                .bus()
                                .send(new ChangeLanguageEvent());*/

                } catch (Exception e) {
                    e.printStackTrace();
                }

                dismiss();
            }
            break;
        }
    }

    @Override
    public void show() {
        if (CommonUtility.canShowOrDismissDialog(activity)) {
            super.show();
        }
    }

    @Override
    public void dismiss() {
        if (CommonUtility.canShowOrDismissDialog(activity)) {
            super.dismiss();
        }
    }

}

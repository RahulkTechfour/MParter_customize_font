package com.luminous.mpartner;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private SharedPrefsManager sharedPrefsManager;
    public String userId = "", userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefsManager = new SharedPrefsManager(BaseActivity.this);
        userId = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);
        userType = sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE);


    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }


    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}

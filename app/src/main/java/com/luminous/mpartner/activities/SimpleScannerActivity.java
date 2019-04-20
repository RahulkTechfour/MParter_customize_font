package com.luminous.mpartner.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.luminous.mpartner.R;
import com.luminous.mpartner.utilities.AppConstants;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class SimpleScannerActivity extends Activity implements
        ZBarScannerView.ResultHandler {

    //Firebase Analytics
//    private FirebaseAnalytics mFirebaseAnalytics;

    private ZBarScannerView mScannerView;
    private String mSerialNumber = "";

    //Check for version
    String currentVersion, latestVersion;
    Dialog dialog;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        // Obtain the FirebaseAnalytics instance.
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Screenviews
        Bundle params = new Bundle();
        params.putString("screen_name", "com.luminous.mpartner.ui.activities.SimpleScannerActivity");
//        mFirebaseAnalytics.logEvent("SimpleScannerActivity", params);
        /*Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "com.luminous.mpartner.ui.activities.SimpleScannerActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);*/

        //Check current version
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;

        //Check Play Store Version
//        VersionChecker versionChecker = new VersionChecker();
//        latestVersion = null;
//        try {
//            latestVersion = versionChecker.execute().get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        //Compare Current Version with Playstore Version
        if (latestVersion != null) {
            //if (!currentVersion.equalsIgnoreCase(latestVersion)) {
            if (Float.parseFloat(currentVersion) < Float.parseFloat(latestVersion) && !currentVersion.equalsIgnoreCase(latestVersion)) {
                if (!isFinishing()) { //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                    showUpdateDialog();
                }
            }
        }

        if (!isCameraAvailable()) {
            // Cancel request if there is no rear-facing camera.
            cancelRequest();
            return;
        }

        setContentView(R.layout.activity_simple_scanner);
        mScannerView = (ZBarScannerView) findViewById(R.id.activity_scanner_scannerview);
    }

    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update");
        builder.setMessage("A New Update is Available");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id=com.luminous.mpartner")));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExitActivity.exitApplication(getApplicationContext());
            }
        });

        builder.setCancelable(false);
        dialog = builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler
        // for scan results.
        mScannerView.startCamera(false); // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera(); // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) { // etc.)
        mSerialNumber = rawResult.getContents();
        returnBackResult();
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void cancelRequest() {
        Intent dataIntent = new Intent();
        dataIntent.putExtra(AppConstants.ERROR_INFO, "Camera unavailable");
        setResult(Activity.RESULT_CANCELED, dataIntent);
        finish();
    }

    private void returnBackResult() {
        Intent dataIntent = new Intent();
        dataIntent.putExtra(AppConstants.SCAN_RESULT,
                mSerialNumber);
        setResult(Activity.RESULT_OK, dataIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent dataIntent = new Intent();
        dataIntent.putExtra(AppConstants.SCAN_RESULT,
                mSerialNumber);
        setResult(Activity.RESULT_OK, dataIntent);
        finish();
    }
}
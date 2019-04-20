package com.luminous.mpartner.connect;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.luminous.mpartner.R;

import org.apache.http.client.ClientProtocolException;
import org.ksoap2.serialization.SoapObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CommonAsyncTask extends AsyncTask<String, Void, SoapObject> {

    protected Context context;
    private String methodName;
    public Dialog customProgressDialog;
    public SoapObject soapResult;
    public static boolean networkerror, servererror;
    public boolean progressbarEnable; // if 'true' progressbar is enable
    private HashMap<String, String> properties;
    private HashMap<String, ArrayList<String>> arrayProperties;
    private ProgressDialog progressDialog;

    public CommonAsyncTask(Context context, HashMap<String, String> properties) {
        this.context = context;
        this.progressbarEnable = true;
        this.properties = properties;
    }

    public CommonAsyncTask(Context context, HashMap<String, String> properties,
                           boolean progressbarEnable) {
        this.context = context;
        this.properties = properties;
        this.progressbarEnable = progressbarEnable;
    }

    public CommonAsyncTask(Context context, HashMap<String, String> properties, HashMap<String, ArrayList<String>> arrayProperties,
                           boolean progressbarEnable) {
        this.context = context;
        this.properties = properties;
        this.arrayProperties  = arrayProperties;
        this.progressbarEnable = progressbarEnable;
    }

    @Override
    protected void onPreExecute() {
        if (progressbarEnable) {
            //SHOW PROGRESSBAR
            showProgressDialog();
        }
    }

    @Override
    protected SoapObject doInBackground(String... params) {

        // Cheking if device is connected to Internet
        if (CheckNetworkStatus.getInstance(context).isOnline()) {
            methodName = params[0];

            // / prepare an url for hitting the server
            RequestBuilder requestBuilder = new RequestBuilder();
            requestBuilder.setContext(context);
            requestBuilder.setMethodName(methodName);
            requestBuilder.setProperties(properties);
            requestBuilder.setArrayProperties(arrayProperties);

            try {
                // / calling to the service class which directly hits the server
                try {
                    soapResult = new HttpRequestService().getRequestData(
                            requestBuilder, context);
                } catch (ClassCastException e) {
                    Log.e("", "==> " + e.getMessage());
                }

            } catch (ClientProtocolException e) {

                networkerror = false;
                servererror = true;
                Log.e("", "==> " + e.getMessage());
            } catch (CustomException e) {

                servererror = false;
                networkerror = true;
                Log.e("", "==> " + e.getMessage());

            } catch (IOException e) {

                networkerror = false;
                servererror = true;
                Log.e("", "==> " + e.getMessage());
            } catch (Exception e) {

                e.printStackTrace();
            }
            return soapResult;
        } else {
            soapResult = null;
            return soapResult;
        }
    }

    @Override
    protected void onPostExecute(SoapObject result) {
        dismissProgressDialog();
        if (result == null) {
            //Make Length Long for Toast by Anusha 01/08/2018 TASK#4307
            //	Toast.makeText(context, "No internet connection available.",Toast.LENGTH_LONG).show();
        }
    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(context);
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
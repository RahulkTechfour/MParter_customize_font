package com.luminous.mpartner;

import android.app.Application;
import android.os.StrictMode;

import com.luminous.mpartner.utilities.RxBus;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {

    public static boolean isPermissionDialogShow = false;
    private static MyApplication myApplication;

    private RxBus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        myApplication = this;
        bus = new RxBus();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    public static MyApplication getApplication() {
        return myApplication;
    }

    public RxBus bus() {
        return bus;
    }
}

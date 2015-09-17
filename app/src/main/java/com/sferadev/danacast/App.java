package com.sferadev.danacast;

import android.app.Application;
import android.content.Context;

import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.player.VideoCastControllerActivity;
import com.instabug.library.Instabug;

public class App extends Application {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        VideoCastManager.initialize(this, getString(R.string.app_id), VideoCastControllerActivity.class, null)
                .enableFeatures(VideoCastManager.FEATURE_NOTIFICATION |
                        VideoCastManager.FEATURE_LOCKSCREEN |
                        VideoCastManager.FEATURE_WIFI_RECONNECT |
                        VideoCastManager.FEATURE_DEBUGGING);

        Instabug.initialize(this, "9491098bea83ae038a065edf7284ecee");
    }

}

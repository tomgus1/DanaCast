package com.sferadev.danacast;

import android.support.multidex.MultiDexApplication;

import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.player.VideoCastControllerActivity;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        String applicationId = getString(R.string.app_id);

        VideoCastManager.
                initialize(this, applicationId, VideoCastControllerActivity.class, null)
                .enableFeatures(VideoCastManager.FEATURE_NOTIFICATION |
                        VideoCastManager.FEATURE_LOCKSCREEN |
                        VideoCastManager.FEATURE_WIFI_RECONNECT |
                        VideoCastManager.FEATURE_DEBUGGING);
    }

}

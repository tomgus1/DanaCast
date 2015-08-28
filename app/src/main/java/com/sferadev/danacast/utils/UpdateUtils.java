package com.sferadev.danacast.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.github.snowdream.android.app.AbstractUpdateListener;
import com.github.snowdream.android.app.DownloadTask;
import com.github.snowdream.android.app.UpdateFormat;
import com.github.snowdream.android.app.UpdateInfo;
import com.github.snowdream.android.app.UpdateManager;
import com.github.snowdream.android.app.UpdateOptions;
import com.github.snowdream.android.app.UpdatePeriod;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateUtils {
    private static final String URL = "https://raw.githubusercontent.com/SferaDev/DanaCast/master/updates/update.json";

    public static void checkUpdates(final Context context) {
        UpdateManager manager = new UpdateManager(context);
        if (!getRemoteVersionCode().equals(getLocalVersionCode(context))) {
            manager.check(context, updateOptions(context), new AbstractUpdateListener() {
                @Override
                public void onShowUpdateUI(UpdateInfo updateInfo) {
                    Toast.makeText(context, "Downloading DanaCast update", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onShowNoUpdateUI() {
                    //no-op
                }

                @Override
                public void onShowUpdateProgressUI(UpdateInfo updateInfo, DownloadTask downloadTask, int i) {
                    //no-op
                }

                @Override
                public void ExitApp() {
                    //no-op
                }
            });
        }
    }

    private static UpdateOptions updateOptions(Context context) {
        return new UpdateOptions.Builder(context)
                .checkUrl(URL)
                .updateFormat(UpdateFormat.JSON)
                .updatePeriod(new UpdatePeriod(UpdatePeriod.EACH_TIME))
                .checkPackageName(true)
                .build();
    }

    private static String getRemoteVersionCode() {
        try {
            return new JSONObject(NetworkUtils.getURLOutput(URL)).getJSONObject("updateInfo").getString("versionCode");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getLocalVersionCode(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

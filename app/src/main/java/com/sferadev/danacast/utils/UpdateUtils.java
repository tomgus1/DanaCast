package com.sferadev.danacast.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class UpdateUtils {
    private static final String URL = "https://raw.githubusercontent.com/SferaDev/DanaCast/master/updates/update.json";

    public static void checkUpdates(final Context context) {
        if (getRemoteVersionCode() > getLocalVersionCode(context)) {
            final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(getRemoteApkURL()));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    File.separator + "DanaCast" + File.separator + "Updates" + File.separator + getRemoteVersionCode() + ".apk");
            request.setVisibleInDownloadsUi(false);
            final long id = downloadManager.enqueue(request);
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    installApk(context, downloadManager.getUriForDownloadedFile(id));
                }
            }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } else {
            ContentUtils.removeLocalFiles("Updates");
        }
    }

    private static int getRemoteVersionCode() {
        try {
            JSONObject jsonObject = new JSONObject(NetworkUtils.getURLOutput(URL)).getJSONObject("updateInfo");
            return Integer.parseInt(jsonObject.getString("versionCode"));
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static int getLocalVersionCode(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static String getRemoteApkURL() {
        try {
            return new JSONObject(NetworkUtils.getURLOutput(URL)).getJSONObject("updateInfo").getString("apkUrl");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void installApk(Context context, Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

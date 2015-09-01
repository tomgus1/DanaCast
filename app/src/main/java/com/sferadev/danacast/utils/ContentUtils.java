package com.sferadev.danacast.utils;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.sferadev.danacast.helpers.Constants;
import com.sferadev.danacast.helpers.Server;
import com.sferadev.danacast.models.EntryModel;

import java.io.File;

public class ContentUtils {
    private static final String[] dialogOptions = {"Chromecast", "Download", "Copy link to the clipboard", "Open in Browser", "Open with..."};

    public static void loadIntentDialog(final Context context, final String lastContent, final EntryModel entry, final String url) {
        final String[] finalUrl = new String[1];
        final ProgressDialog dialog = new ProgressDialog(context);
        if (!Server.isSupported(url)) {
            NetworkUtils.openChromeTab(context, url);
            return;
        }
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Loading. Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                finalUrl[0] = Server.getVideoPath(url);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    if (finalUrl[0] == null) {
                        Toast.makeText(context, "The requested link doesn't work", Toast.LENGTH_LONG).show();
                        return;
                    }
                    loadOptionsDialog(context, lastContent, entry, finalUrl[0]);
                }
            }

        };
        task.execute();
    }

    private static void loadOptionsDialog(final Context context, final String lastContent, final EntryModel entry, final String url) {
        AlertDialog dialog = new AlertDialog.Builder(context,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                .setTitle(url)
                .setItems(dialogOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isSong = entry.getType() == Constants.TYPE_SONG;
                        switch (which) {
                            case 0:
                                loadFileChromecast(context, isSong, lastContent, url);
                                break;
                            case 1:
                                loadFileDownload(context, isSong, lastContent, url);
                                break;
                            case 2:
                                addToClipboard(context, url);
                                break;
                            case 3:
                                NetworkUtils.openChromeTab(context, url);
                                break;
                            case 4:
                                loadFileExternal(context, isSong, url);
                                break;
                        }
                    }
                })
                .create();
        dialog.show();
    }

    private static void loadFileChromecast(Context context, boolean song, String lastContent, String url) {
        if (!VideoCastManager.getInstance().isConnected()) return;
        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_TV_SHOW);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, lastContent);
        mediaMetadata.putString(MediaMetadata.KEY_SUBTITLE, url);
        MediaInfo mSelectedMedia = new MediaInfo.Builder(url)
                .setContentType(song ? "audio/mp3" : "video/mp4")
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(mediaMetadata)
                .build();
        VideoCastManager.getInstance().startVideoCastControllerActivity(context, mSelectedMedia, 0, true);
    }

    private static void loadFileDownload(Context context, boolean song, String lastContent, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                File.separator + "DanaCast" + File.separator + lastContent + (song ? ".mp3" : ".mp4"));
        downloadManager.enqueue(request);
    }

    private static void loadFileExternal(Context context, boolean song, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), song ? "audio/mp3" : "video/mp4");
        context.startActivity(intent);
    }

    public static void addToClipboard(Context context, String string) {
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("simple text", string));
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_LONG).show();
    }
}

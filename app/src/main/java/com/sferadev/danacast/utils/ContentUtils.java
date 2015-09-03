package com.sferadev.danacast.utils;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.github.sv244.torrentstream.Torrent;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.sferadev.danacast.helpers.Constants;
import com.sferadev.danacast.helpers.Server;
import com.sferadev.danacast.models.EntryModel;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class ContentUtils {
    private static final String[] dialogOptions = {"Chromecast", "Download", "Copy link to the clipboard", "Open in Browser", "Open with..."};
    private static final String[] supportedExtensions = {"mp4", "mp3", "avi", "m3u8", "aac", "wav"};

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

    private static void loadOptionsDialog(final Context context, final String title, final EntryModel entry, final String url) {
        AlertDialog dialog = new AlertDialog.Builder(context,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                .setTitle(url)
                .setItems(dialogOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                loadFileChromecast(context, entry.getType(), title, url);
                                break;
                            case 1:
                                loadFileDownload(context, entry.getType(), title, url);
                                break;
                            case 2:
                                addToClipboard(context, url);
                                break;
                            case 3:
                                NetworkUtils.openChromeTab(context, url);
                                break;
                            case 4:
                                loadFileExternal(context, entry.getType(), url);
                                break;
                        }
                    }
                })
                .create();
        dialog.show();
    }

    public static void loadOptionsDialog(final Context context, final Torrent torrent) {
        AlertDialog dialog = new AlertDialog.Builder(context,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                .setTitle(torrent.getSaveLocation().toString())
                .setItems(dialogOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                try {
                                    new WebServer(torrent.getVideoFile().toString()).start();
                                    loadFileChromecast(context, Constants.TYPE_TORRENT,
                                            torrent.getSaveLocation().toString(), "http://" +
                                                    NetworkUtils.getIPAddress(context) + ":8080");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 1:
                                Toast.makeText(context, "Feature not available yet", Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                addToClipboard(context, torrent.getVideoFile().getPath());
                                break;
                            case 3:
                                Toast.makeText(context, "Feature not available", Toast.LENGTH_LONG).show();
                                break;
                            case 4:
                                loadFileExternal(context, Constants.TYPE_TORRENT, Uri.fromFile(torrent.getVideoFile()));
                                break;
                        }
                    }
                })
                .create();
        dialog.show();
    }

    private static void loadFileChromecast(Context context, int type, String title, String url) {
        if (!VideoCastManager.getInstance().isConnected()) return;
        MediaMetadata mediaMetadata = new MediaMetadata(type == Constants.TYPE_SONG ?
                MediaMetadata.MEDIA_TYPE_MUSIC_TRACK : MediaMetadata.MEDIA_TYPE_MOVIE);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, title);
        mediaMetadata.putString(MediaMetadata.KEY_SUBTITLE, url);
        MediaInfo mSelectedMedia = new MediaInfo.Builder(url)
                .setContentType((type == Constants.TYPE_SONG ? "audio/" : "video/") +
                        FilenameUtils.getExtension(url))
                .setStreamType(type == Constants.TYPE_TORRENT || type == Constants.TYPE_LIVE ? MediaInfo.STREAM_TYPE_LIVE
                        : MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(mediaMetadata)
                .build();
        VideoCastManager.getInstance().startVideoCastControllerActivity(context, mSelectedMedia, 0, true);
    }

    private static void loadFileDownload(Context context, final int type, String title, String url) {
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String fileName = !FilenameUtils.isExtension(url, supportedExtensions) ? title +
                (type == Constants.TYPE_SONG ? ".mp3" : ".mp4") : title + "." + FilenameUtils.getExtension(url);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator + "DanaCast" +
                File.separator + "Downloads" + File.separator + fileName);
        final long id = downloadManager.enqueue(request);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadFileExternal(context, type, downloadManager.getUriForDownloadedFile(id));
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private static void loadFileExternal(Context context, int type, String url) {
        loadFileExternal(context, type, Uri.parse(url));
    }

    private static void loadFileExternal(Context context, int type, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, type == Constants.TYPE_SONG ? "audio/*" : "video/*");
        context.startActivity(intent);
    }

    public static void addToClipboard(Context context, String string) {
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("simple text", string));
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_LONG).show();
    }
}

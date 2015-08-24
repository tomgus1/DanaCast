package com.sferadev.danacast.utils;

import android.content.Context;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.sferadev.danacast.servers.Streamcloud;

public class CastUtils {
    public static void castVideo(Context context, String url) {
        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_TV_SHOW);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, url);
        //mediaMetadata.addImage(new WebImage(Uri.parse(uriString)));
        MediaInfo mSelectedMedia = new MediaInfo.Builder(Streamcloud.getVideoPath(url))
                .setContentType("video/mp4")
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(mediaMetadata)
                .build();
        VideoCastManager.getInstance().startVideoCastControllerActivity(context, mSelectedMedia, 0, true);
    }
}

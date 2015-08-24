package com.sferadev.danacast.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ContentUtils {
    public static void loadVideoExternal(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "video/*");
        context.startActivity(intent);
    }
}

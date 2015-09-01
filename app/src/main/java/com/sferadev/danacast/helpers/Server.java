package com.sferadev.danacast.helpers;

import com.sferadev.danacast.servers.Nowvideo;
import com.sferadev.danacast.servers.Streamcloud;

public class Server {
    public static boolean isSupported(String url) {
        return url.contains("streamcloud") ||
                url.contains("nowvideo") ||
                url.contains("jkmedia") ||
                url.contains("music");
    }

    public static String getVideoPath(String url) {
        try {
            if (url.contains("streamcloud")) {
                return Streamcloud.getVideoPath(url);
            } else if (url.contains("nowvideo")) {
                return Nowvideo.getVideoPath(url);
            } else return url;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}

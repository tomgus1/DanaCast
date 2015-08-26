package com.sferadev.danacast.servers;

public class Server {
    public static boolean isSupported(String url) {
        return url.contains("streamcloud") || url.contains("nowvideo") || url.contains("jkmedia");
    }

    public static String getVideoPath(String url) {
        try {
            if (url.contains("streamcloud")) {
                return Streamcloud.getVideoPath(url);
            } else if (url.contains("nowvideo")) {
                return Nowvideo.getVideoPath(url);
            } else if (url.contains("jkmedia")) {
                return url;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}

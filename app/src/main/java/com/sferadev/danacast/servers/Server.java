package com.sferadev.danacast.servers;

public class Server {
    public static String getVideoPath(String url) {
        if (url.contains("streamcloud")) return Streamcloud.getVideoPath(url);
        if (url.contains("nowvideo")) return Nowvideo.getVideoPath(url);
        return null;
    }
}

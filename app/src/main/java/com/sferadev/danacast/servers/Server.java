package com.sferadev.danacast.servers;

public class Server {
    public static String getVideoPath(String url) {
        try {
            if (url.contains("streamcloud")) {
                return Streamcloud.getVideoPath(url);
            } else if (url.contains("nowvideo")) {
                return Nowvideo.getVideoPath(url);
            } else if (url.contains("rocvideo")) {
                return Rocvideo.getVideoPath(url);
            } else return url.contains(".") && url.substring(0,
                    url.lastIndexOf('.')).equals("mp4")
                    ? url : null;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}

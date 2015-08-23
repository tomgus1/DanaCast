package com.sferadev.danacast.servers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Nowvideo {
    public String getVideoPath(String url) {
        try {
            String id = url.substring(url.lastIndexOf("/") + 1);
            Document doc = Jsoup.connect("http://www.nowvideo.sx/mobile/video.php?id=" + id)
                    .timeout(3000)
                    .get();
            return doc.select("source[type=video/mp4]").attr("src");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.sferadev.danacast.servers;

import android.os.SystemClock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Streamcloud {
    public String getVideoPath(String url) {
        Pattern pattern = Pattern.compile("http:\\/\\/streamcloud\\.eu\\/(.*)\\/(.*)\\.html");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find() && matcher.groupCount() == 2) {
            String link = parseLink(url, matcher.group(1), matcher.group(2));
            if (link != null) return link; //1st Attempt
            SystemClock.sleep(11 * 1000);
            link = parseLink(url, matcher.group(1), matcher.group(2)); //2nd Attempt
            return link;
        }
        return null;
    }

    private String parseLink(String url, String fileId, String fileName){
        try {
            Document doc = Jsoup.connect(url)
                    .data("op", "download1")
                    .data("id", fileId)
                    .data("fname", fileName)
                    .data("imhuman", "Watch+video+now")
                    .data("usr_login", "")
                    .data("referer", "")
                    .data("hash", "")
                    .timeout(3000)
                    .post();
            Pattern pattern = Pattern.compile("file: \\\"(.*)\\/video\\.mp4\\\",");
            Matcher matcher = pattern.matcher(doc.html());
            if (matcher.find()) return matcher.group(1) + "/video.mp4";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

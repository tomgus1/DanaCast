package com.sferadev.danacast.servers;

import android.os.SystemClock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Streamcloud {
    public static String getVideoPath(String url) {
        Pattern pattern = Pattern.compile("http:\\/\\/streamcloud\\.eu\\/(.*)\\/(.*)\\.html");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find() && matcher.groupCount() == 2) {
            String link = parseLink(url, matcher.group(1), matcher.group(2));
            if (link != null) {
                return link; //1st Attempt
            }
            SystemClock.sleep(11 * 1000);
            link = parseLink(url, matcher.group(1), matcher.group(2)); //2nd Attempt
            return link;
        }
        return null;
    }

    private static String parseLink(final String url, final String fileId, final String fileName) {
        final String[] response = {null};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
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
                    if (matcher.find()) {
                        response[0] = matcher.group(1) + "/video.mp4";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return response[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}

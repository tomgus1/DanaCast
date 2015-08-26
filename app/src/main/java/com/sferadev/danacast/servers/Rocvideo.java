package com.sferadev.danacast.servers;

import android.os.SystemClock;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rocvideo {
    public static String getVideoPath(String url) {
        Pattern pattern = Pattern.compile("http:\\/\\/rocvideo\\.tv\\/(.*)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find() && matcher.groupCount() == 1) {
            String link = parseLink(url, matcher.group(1));
            if (link != null) {
                return link; //1st Attempt
            }
            SystemClock.sleep(11 * 1000);
            link = parseLink(url, matcher.group(1)); //2nd Attempt
            return link;
        }
        return null;
    }

    private static String parseLink(final String url, final String fileId) {
        final String[] result = {null};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document response = Jsoup.connect(url).get();
                    String fileName = response.getElementsByAttributeValue("name", "fname").attr("value");
                    Document doc = Jsoup.connect(url)
                            .data("F1", "")
                            .data("op", "download1")
                            .data("usr_login", "")
                            .data("hash", "")
                            .data("referer", "")
                            .data("id", fileId)
                            .data("fname", fileName)
                            .data("method_free", "Continue+video")
                            .timeout(3000)
                            .post();
                    Pattern pattern = Pattern.compile("src=\"http:\\/\\/(.*)\\.mp4");
                    Matcher matcher = pattern.matcher(doc.html());
                    if (matcher.find()) {
                        result[0] = matcher.group(1) + ".mp4";
                        Log.d("Dana", result[0]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return result[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFileName(final String url) {
        final String[] response = {null};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(url).get();
                    response[0] = doc.getElementsByTag("form").first().select("[name=fname]").first().attr("value");
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

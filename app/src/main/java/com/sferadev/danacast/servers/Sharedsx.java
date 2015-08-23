package com.sferadev.danacast.servers;

import android.os.SystemClock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Sharedsx {
    public String getVideoPath(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(3000)
                    .get();
            String hash = doc.select("form > input[name=hash]").val();
            String expires = doc.select("form > input[name=expires]").val();
            String timestamp = doc.select("form > input[name=timestamp]").val();
            SystemClock.sleep(7 * 1000);
            doc = Jsoup.connect(url)
                    .data("hash", hash)
                    .data("expires", expires)
                    .data("timestamp", timestamp)
                    .timeout(3000)
                    .post();
            return doc.select("div.stream-content").attr("data-url");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.sferadev.danacast.providers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Seriesblanco {
    public static Elements getSearchResults(String query) {
        try {
            Document document = Jsoup.connect("http://seriesblanco.com/finder.php")
                    .data("query", query)
                    .post();
            return document.select("a");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Elements getPopularContent() {
        try {
            Document document = Jsoup.connect("http://www.seriesblanco.com/")
                    .get();
            return document.select("#PopularPosts1 li");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

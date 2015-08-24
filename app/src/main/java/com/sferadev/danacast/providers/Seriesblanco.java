package com.sferadev.danacast.providers;

import com.sferadev.danacast.model.EntryModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Seriesblanco {
    public static ArrayList<EntryModel> getSearchResults(final String query) {
        final ArrayList<EntryModel> result = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("http://seriesblanco.com/search.php" +
                            "?q1=" + query.replace(" ", "+")).get();
                    Elements elements = document.getElementsByClass("post-header").first()
                            .getElementsByTag("a");
                    for (Element element : elements) {
                        if (element.hasText()) {
                            String title = element.text();
                            String url = element.attr("abs:href");
                            result.add(new EntryModel(title, url, null));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<EntryModel> getPopularContent() {
        final ArrayList<EntryModel> result = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("http://www.seriesblanco.com/").get();
                    Elements elements = document.select("#PopularPosts1").first()
                            .getElementsByTag("li");
                    for (Element element : elements) {
                        String title = element.getElementsByTag("img").first().attr("title");
                        String url = element.getElementsByTag("a").first().attr("abs:href");
                        String pic = element.getElementsByTag("img").first().attr("src");
                        result.add(new EntryModel(title, url, pic));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<EntryModel> getEpisodeList(final String url) {
        final ArrayList<EntryModel> result = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(url).get();
                    Elements elements = document.getElementsByClass("zebra").first()
                            .getElementsByTag("tbody").first()
                            .getElementsByTag("a");
                    for (Element element : elements) {
                        if (element.hasText()) {
                            String title = element.text();
                            String url = element.attr("abs:href");
                            result.add(new EntryModel(title, url, null));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}

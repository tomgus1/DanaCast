package com.sferadev.danacast.providers;

import com.sferadev.danacast.helpers.Constants;
import com.sferadev.danacast.models.EntryModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Yts {
    public static ArrayList<EntryModel> getSearchResults(final String query) {
        final ArrayList<EntryModel> result = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("https://yts.to/browse-movies/" +
                            query.replace(" ", "%20") + "/all/all/0/latest").get();
                    Elements elements = document.getElementsByClass("browse-movie-bottom");
                    for (Element element : elements) {
                        String title = element.getElementsByTag("a").first().text();
                        String showUrl = element.getElementsByTag("a").first().attr("abs:href");
                        result.add(new EntryModel(Constants.TYPE_MOVIE, title, showUrl, null));
                    }
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return result;
        } catch (InterruptedException | NullPointerException e) {
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
                    Document document = Jsoup.connect("https://yts.to").get();
                    Elements elements = document.getElementsByClass("browse-movie-wrap");
                    for (Element element : elements) {
                        String title = element.getElementsByTag("div").get(1)
                                .getElementsByTag("a").first().text();
                        String showUrl = element.getElementsByTag("div").get(1)
                                .getElementsByTag("a").first().attr("abs:href");
                        if (showUrl.contains("yts"))
                            result.add(new EntryModel(Constants.TYPE_MOVIE, title, showUrl, null));
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
        } catch (InterruptedException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<EntryModel> getMovieLinks(final String url) {
        final ArrayList<EntryModel> result = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(url).get();
                    Elements elements = document.getElementsByClass("bottom-info").first()
                            .getElementsByTag("p").first().getElementsByTag("a");
                    for (Element element : elements) {
                        String title = element.attr("title");
                        String linkUrl = element.attr("abs:href");
                        result.add(new EntryModel(Constants.TYPE_TORRENT, title + " (English)", linkUrl, null));
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
        } catch (InterruptedException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}

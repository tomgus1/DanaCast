package com.sferadev.danacast.providers;

import com.sferadev.danacast.model.EntryModel;
import com.sferadev.danacast.utils.ContentUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Jkanime {
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
                            result.add(new EntryModel(ContentUtils.TYPE_SHOW, title, url, null));
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
                    Document document = Jsoup.connect("http://jkanime.net/").get();
                    Elements elements = document.select("div.publibox").first()
                            .getElementsByTag("span");
                    for (Element element : elements) {
                        result.add(new EntryModel(ContentUtils.TYPE_SHOW,
                                element.getElementsByTag("a").first().text(),
                                element.getElementsByTag("a").first().attr("abs:href"), null));
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
                    String[] lastPage = document.getElementsByClass("listnavi").first().lastElementSibling().text().split(" ");
                    int episodes = Integer.parseInt(lastPage[lastPage.length - 1]);
                    for (int i = 1; i <= episodes; i++) {
                        result.add(new EntryModel(ContentUtils.TYPE_EPISODE, "Episode " + i, url + i + "/", null));
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

    public static ArrayList<EntryModel> getEpisodeLinks(final String url) {
        final ArrayList<EntryModel> result = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(url).get();
                    Elements titleElements = document.getElementsByClass("video_option").first().getElementsByTag("a");
                    Elements videoElements = document.getElementsByClass("player_conte");
                    for (int i = 0; i < videoElements.size(); i++) {
                        String title = titleElements.get(i).text();
                        String linkUrl = videoElements.get(i).attr("src");
                        result.add(new EntryModel(ContentUtils.TYPE_LINK, title, linkUrl, null));
                    }
                } catch (IOException | IndexOutOfBoundsException e) {
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

    public static String getExternalLink(final String url) {
        if (url.contains("jkmedia")) {
            return url.replace("jk.php?u=", "");
        } else {
            return url;
        }
    }
}

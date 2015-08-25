package com.sferadev.danacast.providers;

import com.sferadev.danacast.model.EntryModel;
import com.sferadev.danacast.utils.ContentUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Pordede {
    public static ArrayList<EntryModel> getSearchResults(final String query) {
        final ArrayList<EntryModel> result = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("http://www.pordede.com/search/" +
                            query.replace(" ", "-"))
                            .cookies(loginResponse().cookies())
                            .referrer("http://www.google.com")
                            .get();
                    Elements elements = document.select("div.listContainer.listCovers.clearfix").first()
                            .select("div.ddItemContainer.modelContainer");
                    for (Element element : elements) {
                        String title = element.getElementsByClass("title").text();
                        String url = element.select("a.defaultLink.extended").first().attr("abs:href");
                        result.add(new EntryModel(ContentUtils.TYPE_SHOW, title, url, null));
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
                    Document document = Jsoup.connect("http://www.pordede.com/series/index/showlist/viewed")
                            .cookies(loginResponse().cookies())
                            .referrer("http://www.google.com")
                            .get();
                    Elements elements = document.getElementsByClass("listContainer").first().getElementsByClass("ddItemContainer");
                    for (Element element : elements) {
                        String title = element.getElementsByClass("title").first().text();
                        String showUrl = element.getElementsByClass("extended").first().attr("abs:href");
                        String picUrl = element.getElementsByClass("centeredPicFalse").first().attr("src");
                        result.add(new EntryModel(ContentUtils.TYPE_SHOW, title, showUrl, picUrl));
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
                    Document document = Jsoup.connect(url)
                            .cookies(loginResponse().cookies())
                            .referrer("http://www.google.com")
                            .get();
                    Elements elements = document.select("div.modelContainer.defaultPopup");
                    for (Element element : elements) {
                        String title = element.getElementsByClass("title").first().text();
                        String episodeUrl = element.getElementsByClass("title").first().attr("abs:href");
                        result.add(new EntryModel(ContentUtils.TYPE_EPISODE, title, episodeUrl, null));
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
                    Document document = Jsoup.connect(url)
                            .cookies(loginResponse().cookies())
                            .referrer("http://www.google.com")
                            .get();
                    Elements elements = document.select("div.linksContainer.online.tabContent").first()
                            .select("a.a.aporteLink.done");
                    for (Element element : elements) {
                        String title = WordUtils.capitalize(element.getElementsByClass("hostimage").first()
                                .child(0).attr("src").replace("http://www.pordede.com/images/hosts/popup_", "").split("\\.")[0]);
                        String linkUrl = element.attr("abs:href");
                        String language = WordUtils.capitalize(element.getElementsByClass("flags").first()
                                .child(0).classNames().toArray()[1].toString());
                        result.add(new EntryModel(ContentUtils.TYPE_LINK, title + " (" + language + ")", linkUrl, null));
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

    public static String getExternalLink(final String url) {
        final String[] result = {null};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(url)
                            .cookies(loginResponse().cookies())
                            .referrer("http://www.google.com")
                            .get();
                    Element element = document.select("p.links").first().child(0);
                    result[0] = Jsoup.connect(element.attr("abs:href"))
                            .cookies(loginResponse().cookies())
                            .followRedirects(true)
                            .execute().url().toExternalForm();
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

    private static Connection.Response loginResponse() throws IOException {
        return Jsoup.connect("http://www.pordede.com/site/login")
                .data("LoginForm[username]", "username")
                .data("LoginForm[password]", "password")
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .method(Connection.Method.POST).timeout(0).execute();
    }
}

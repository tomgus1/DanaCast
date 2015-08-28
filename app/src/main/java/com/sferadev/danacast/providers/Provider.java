package com.sferadev.danacast.providers;

import android.content.Context;

import com.sferadev.danacast.model.EntryModel;
import com.sferadev.danacast.utils.ContentUtils;

import java.util.ArrayList;

public class Provider {
    private static final int PROVIDER_SERIESBLANCO = 0;
    private static final int PROVIDER_SERIESYONKIS = 1;
    private static final int PROVIDER_WATCHSERIES = 2;
    private static final int PROVIDER_PORDEDE = 3;
    private static final int PROVIDER_JKANIME = 4;
    private static final int PROVIDER_MUSIC163 = 5;

    public static ArrayList<EntryModel> getProviders() {
        ArrayList<EntryModel> items = new ArrayList<>();
        items.add(new EntryModel(ContentUtils.TYPE_PROVIDER, "SeriesBlanco", null, null));
        items.add(new EntryModel(ContentUtils.TYPE_PROVIDER, "SeriesYonkis", null, null));
        items.add(new EntryModel(ContentUtils.TYPE_PROVIDER, "Watchseries", null, null));
        items.add(new EntryModel(ContentUtils.TYPE_PROVIDER, "Pordede", null, null));
        items.add(new EntryModel(ContentUtils.TYPE_PROVIDER, "JKAnime", null, null));
        items.add(new EntryModel(ContentUtils.TYPE_PROVIDER, "Music163", null, null));
        return items;
    }

    public static ArrayList<EntryModel> getSearchResults(Context context, int provider, String query) {
        switch (provider) {
            case PROVIDER_SERIESBLANCO:
                return Seriesblanco.getSearchResults(query);
            case PROVIDER_SERIESYONKIS:
                return Seriesyonkis.getSearchResults(query);
            case PROVIDER_WATCHSERIES:
                return Watchseries.getSearchResults(query);
            case PROVIDER_PORDEDE:
                if (!Pordede.isLoggedInCredentials(context)) {
                    Pordede.loginWithCredentials(context);
                    return getProviders();
                }
                return Pordede.getSearchResults(context, query);
            case PROVIDER_JKANIME:
                return Jkanime.getSearchResults(query);
            case PROVIDER_MUSIC163:
                return Music163.getSearchResults(query);
        }
        return getProviders();
    }

    public static ArrayList<EntryModel> getPopularContent(Context context, int provider) {
        switch (provider) {
            case PROVIDER_SERIESBLANCO:
                return Seriesblanco.getPopularContent();
            case PROVIDER_SERIESYONKIS:
                return Seriesyonkis.getPopularContent();
            case PROVIDER_WATCHSERIES:
                return Watchseries.getPopularContent();
            case PROVIDER_PORDEDE:
                if (!Pordede.isLoggedInCredentials(context)) {
                    Pordede.loginWithCredentials(context);
                    return getProviders();
                }
                return Pordede.getPopularContent(context);
            case PROVIDER_JKANIME:
                return Jkanime.getPopularContent();
            case PROVIDER_MUSIC163:
                return Music163.getPopularContent();
        }
        return getProviders();
    }

    public static ArrayList<EntryModel> getEpisodeList(Context context, int provider, String url) {
        switch (provider) {
            case PROVIDER_SERIESBLANCO:
                return Seriesblanco.getEpisodeList(url);
            case PROVIDER_SERIESYONKIS:
                return Seriesyonkis.getEpisodeList(url);
            case PROVIDER_WATCHSERIES:
                return Watchseries.getEpisodeList(url);
            case PROVIDER_PORDEDE:
                if (!Pordede.isLoggedInCredentials(context)) {
                    Pordede.loginWithCredentials(context);
                    return getProviders();
                }
                return Pordede.getEpisodeList(context, url);
            case PROVIDER_JKANIME:
                return Jkanime.getEpisodeList(url);
        }
        return getProviders();
    }

    public static ArrayList<EntryModel> getEpisodeLinks(Context context, int provider, String url) {
        switch (provider) {
            case PROVIDER_SERIESBLANCO:
                return Seriesblanco.getEpisodeLinks(url);
            case PROVIDER_SERIESYONKIS:
                return Seriesyonkis.getEpisodeLinks(url);
            case PROVIDER_WATCHSERIES:
                return Watchseries.getEpisodeLinks(url);
            case PROVIDER_PORDEDE:
                if (!Pordede.isLoggedInCredentials(context)) {
                    Pordede.loginWithCredentials(context);
                    return getProviders();
                }
                return Pordede.getEpisodeLinks(context, url);
            case PROVIDER_JKANIME:
                return Jkanime.getEpisodeLinks(url);
        }
        return getProviders();
    }

    public static ArrayList<EntryModel> getMovieLinks(Context context, int provider, String url) {
        switch (provider) {
            case PROVIDER_PORDEDE:
                if (!Pordede.isLoggedInCredentials(context)) {
                    Pordede.loginWithCredentials(context);
                    return getProviders();
                }
                return Pordede.getMovieLinks(context, url);
        }
        return getProviders();
    }

    public static String getExternalLink(Context context, int provider, String url) {
        switch (provider) {
            case PROVIDER_SERIESBLANCO:
                return Seriesblanco.getExternalLink(url);
            case PROVIDER_SERIESYONKIS:
                return Seriesyonkis.getExternalLink(url);
            case PROVIDER_WATCHSERIES:
                return Watchseries.getExternalLink(url);
            case PROVIDER_PORDEDE:
                if (!Pordede.isLoggedInCredentials(context)) {
                    Pordede.loginWithCredentials(context);
                    return null;
                }
                return Pordede.getExternalLink(context, url);
            case PROVIDER_JKANIME:
                return Jkanime.getExternalLink(url);
            case PROVIDER_MUSIC163:
                return Music163.getExternalLink(url);
        }
        return null;
    }
}

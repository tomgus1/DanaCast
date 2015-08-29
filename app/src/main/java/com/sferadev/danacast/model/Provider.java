package com.sferadev.danacast.model;

import android.content.Context;

import com.sferadev.danacast.providers.Gnula;
import com.sferadev.danacast.providers.Jkanime;
import com.sferadev.danacast.providers.Music163;
import com.sferadev.danacast.providers.Pordede;
import com.sferadev.danacast.providers.Seriesblanco;
import com.sferadev.danacast.providers.Seriesyonkis;
import com.sferadev.danacast.providers.Watchseries;

import java.util.ArrayList;

public class Provider {
    public static final int PROVIDER_SERIESBLANCO = 0;
    public static final int PROVIDER_SERIESYONKIS = 1;
    public static final int PROVIDER_WATCHSERIES = 2;
    public static final int PROVIDER_PORDEDE = 3;
    public static final int PROVIDER_GNULA = 4;
    public static final int PROVIDER_JKANIME = 5;
    public static final int PROVIDER_MUSIC163 = 6;

    public static String[] providerNames = new String[]{
            "SeriesBlanco",
            "SeriesYonkis",
            "Watchseries",
            "Pordede",
            "Gnula",
            "JKAnime",
            "Music163"
    };

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
                    return Category.getCategories();
                }
                return Pordede.getSearchResults(context, query);
            case PROVIDER_GNULA:
                return Gnula.getSearchResults(query);
            case PROVIDER_JKANIME:
                return Jkanime.getSearchResults(query);
            case PROVIDER_MUSIC163:
                return Music163.getSearchResults(query);
        }
        return Category.getCategories();
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
                    return Category.getCategories();
                }
                return Pordede.getPopularContent(context);
            case PROVIDER_GNULA:
                return Gnula.getPopularContent();
            case PROVIDER_JKANIME:
                return Jkanime.getPopularContent();
            case PROVIDER_MUSIC163:
                return Music163.getPopularContent();
        }
        return Category.getCategories();
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
                    return Category.getCategories();
                }
                return Pordede.getEpisodeList(context, url);
            case PROVIDER_JKANIME:
                return Jkanime.getEpisodeList(url);
        }
        return Category.getCategories();
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
                    return Category.getCategories();
                }
                return Pordede.getEpisodeLinks(context, url);
            case PROVIDER_JKANIME:
                return Jkanime.getEpisodeLinks(url);
        }
        return Category.getCategories();
    }

    public static ArrayList<EntryModel> getMovieLinks(Context context, int provider, String url) {
        switch (provider) {
            case PROVIDER_PORDEDE:
                if (!Pordede.isLoggedInCredentials(context)) {
                    Pordede.loginWithCredentials(context);
                    return Category.getCategories();
                }
                return Pordede.getMovieLinks(context, url);
            case PROVIDER_GNULA:
                return Gnula.getMovieLinks(url);
        }
        return Category.getCategories();
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
            case PROVIDER_GNULA:
                return Gnula.getExternalLink(url);
            case PROVIDER_JKANIME:
                return Jkanime.getExternalLink(url);
            case PROVIDER_MUSIC163:
                return Music163.getExternalLink(url);
        }
        return null;
    }
}

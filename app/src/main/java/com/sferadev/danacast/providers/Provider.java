package com.sferadev.danacast.providers;

import com.sferadev.danacast.model.EntryModel;
import com.sferadev.danacast.utils.ContentUtils;

import java.util.ArrayList;

public class Provider {
    public static final int PROVIDER_SERIESBLANCO = 0;
    public static final int PROVIDER_PORDEDE = 1;

    public static ArrayList<EntryModel> getProviders() {
        ArrayList<EntryModel> items = new ArrayList<>();
        items.add(new EntryModel(ContentUtils.TYPE_PROVIDER, "SeriesBlanco", null, null));
        items.add(new EntryModel(ContentUtils.TYPE_PROVIDER, "Pordede", null, null));
        return items;
    }

    public static ArrayList<EntryModel> getSearchResults(int provider, String query) {
        switch (provider) {
            case PROVIDER_SERIESBLANCO:
                return Seriesblanco.getSearchResults(query);
            case PROVIDER_PORDEDE:
                //TODO
                break;
        }
        return null;
    }

    public static ArrayList<EntryModel> getPopularContent(int provider) {
        switch (provider) {
            case PROVIDER_SERIESBLANCO:
                return Seriesblanco.getPopularContent();
            case PROVIDER_PORDEDE:
                //TODO
                break;
        }
        return null;
    }

    public static ArrayList<EntryModel> getEpisodeList(int provider, String url) {
        switch (provider) {
            case PROVIDER_SERIESBLANCO:
                return Seriesblanco.getEpisodeList(url);
            case PROVIDER_PORDEDE:
                //TODO
                break;
        }
        return null;
    }

    public static ArrayList<EntryModel> getEpisodeLinks(int provider, String url) {
        switch (provider) {
            case PROVIDER_SERIESBLANCO:
                return Seriesblanco.getEpisodeLinks(url);
            case PROVIDER_PORDEDE:
                //TODO
                break;
        }
        return null;
    }

    public static String getExternalLink(int provider, String url) {
        switch (provider) {
            case PROVIDER_SERIESBLANCO:
                return Seriesblanco.getExternalLink(url);
            case PROVIDER_PORDEDE:
                //TODO
                break;
        }
        return null;
    }
}

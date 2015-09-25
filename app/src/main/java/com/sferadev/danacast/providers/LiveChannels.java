package com.sferadev.danacast.providers;

import com.sferadev.danacast.helpers.Constants;
import com.sferadev.danacast.models.EntryModel;
import com.sferadev.danacast.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LiveChannels {
    public static ArrayList<EntryModel> getSearchResults(final String query) {
        return query.startsWith("http") && query.contains("json")
                ? getJSONContent(query) : getPopularContent();
    }

    public static ArrayList<EntryModel> getPopularContent() {
        return getJSONContent("https://raw.githubusercontent.com/SferaDev/DanaCast/master/server/channels.json");
    }

    public static ArrayList<EntryModel> getJSONContent(final String url) {
        final ArrayList<EntryModel> result = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray response = new JSONArray(NetworkUtils.getURLOutput(url));
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject station = response.getJSONObject(i);
                        result.add(new EntryModel(Constants.TYPE_LIVE, station.getString("name"),
                                station.getString("url"), null));
                    }
                } catch (JSONException e) {
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
        }
        return null;
    }

    public static String getExternalLink(final String url) {
        return url;
    }

}

package com.sferadev.danacast.model;

public class FeedModel {

    private String mTitle, mURL, mDescription;

    public FeedModel(String title, String url, String description) {
        mTitle = title;
        mURL = url;
        mDescription = description;
    }

    public FeedModel() {
        //empty constructor
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getLink() {
        return mURL;
    }

    public void setLink(String link) {
        mURL = link;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
}
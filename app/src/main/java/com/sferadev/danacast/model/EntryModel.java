package com.sferadev.danacast.model;

public class EntryModel {

    private String mTitle, mURL, mPic;

    public EntryModel(String title, String url, String picUrl) {
        mTitle = title;
        mURL = url;
        mPic = picUrl;
    }

    public EntryModel() {
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

    public String getPictureUrl() {
        return mPic;
    }

    public void setPictureUrl(String picUrl) {
        mPic = picUrl;
    }
}
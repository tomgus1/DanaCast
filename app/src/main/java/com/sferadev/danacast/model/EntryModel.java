package com.sferadev.danacast.model;

public class EntryModel {

    private int mType;
    private String mTitle, mURL, mPic;

    public EntryModel(int type, String title, String url, String picUrl) {
        mType = type;
        mTitle = title;
        mURL = url;
        mPic = picUrl;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
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
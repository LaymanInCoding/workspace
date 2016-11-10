package com.witmoon.xmb.model;

import java.util.ArrayList;

/**
 * Created by de on 2015/11/20.
 */
public class EvaluateBean extends  BaseBean {
    public ArrayList<ImageInfo> getInfo() {
        return info;
    }

    public void setInfo(ArrayList<ImageInfo> info) {
        this.info = info;
    }

    private ArrayList<ImageInfo> info;
    private double mRating;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getmRating() {
        return mRating;
    }

    public void setmRating(double mRating) {
        this.mRating = mRating;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    private String content;
    private String author;

}

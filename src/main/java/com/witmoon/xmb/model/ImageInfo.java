package com.witmoon.xmb.model;

import java.io.Serializable;

public class ImageInfo implements Serializable {

    public String url;
    public float width;
    public float height;

    @Override
    public String toString() {
        return "ImageInfo{" +
                "url='" + url + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}

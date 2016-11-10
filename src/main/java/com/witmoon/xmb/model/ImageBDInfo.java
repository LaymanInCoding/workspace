package com.witmoon.xmb.model;

import java.io.Serializable;

/**
 * Created by DavidWang on 15/8/31.
 */
public class ImageBDInfo implements Serializable{
    // STOPSHIP: 2016/1/6
    public float x;
    public float y;
    public float width;
    public float height;

    @Override
    public String toString() {
        return "ImageBDInfo{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}

package com.witmoon.xmb.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Network;
import com.duowan.mobile.netroid.Request;
import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.cache.BitmapImageCache;
import com.duowan.mobile.netroid.cache.DiskCache;
import com.duowan.mobile.netroid.image.NetworkImageView;
import com.duowan.mobile.netroid.request.ImageRequest;
import com.duowan.mobile.netroid.stack.HurlStack;
import com.duowan.mobile.netroid.toolbox.BasicNetwork;
import com.duowan.mobile.netroid.toolbox.FileDownloader;
import com.duowan.mobile.netroid.toolbox.ImageLoader;
import com.witmoon.xmb.R;
import com.witmoon.xmb.base.Const;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Netroid入口
 * Netroid是一个基于Volley实现的Android Http库。提供执行网络请求、缓存返回结果、批量图片加载、
 * 大文件断点下载的常见Http交互功能。致力于避免每个项目重复开发基础Http功能，实现显著地缩短开发周期的愿景。
 * 文档: http://netroid.cn/
 *
 * Created by zhyh on 2015/4/29.
 */
public class Netroid {
    // Netroid入口，私有该实例，提供方法对外服务
    private static RequestQueue mRequestQueue;
    // 图片加载管理器，私有该实例，提供方法对外服务
    private static ImageLoader mImageLoader;
    // 文件下载管理器，私有该实例，提供方法对外服务
    private static FileDownloader mFileDownloader;

    private Netroid() {
    }

    /**
     * 初始化Netroid, 主要操作是创建请求队列及图片加载器
     * @param context Context
     */
    public static void initialize(Context context) {
        if (mRequestQueue != null) throw new IllegalStateException("initialized");

        // 创建Netroid主类，指定硬盘缓存方案
        Network network = new BasicNetwork(new HurlStack(Const.USER_AGENT, null), "UTF-8");
        mRequestQueue = new RequestQueue(network, 4, new DiskCache(
                new File(context.getCacheDir(), Const.HTTP_DISK_CACHE_DIR_NAME), Const
                .HTTP_DISK_CACHE_SIZE));

        // 创建ImageLoader实例，指定内存缓存方案
        // 注：ImageLoader及FileDownloader不是必须初始化的组件，如果没有用处，不需要创建实例
        mImageLoader = new ImageLoader(
                mRequestQueue, new BitmapImageCache(Const.HTTP_MEMORY_CACHE_SIZE)) {
            @Override
            public ImageRequest buildRequest(String requestUrl, int maxWidth, int maxHeight) {
                ImageRequest request = new ImageRequest(requestUrl, maxWidth, maxHeight);
                request.setCacheExpireTime(TimeUnit.MINUTES, 20);
                return request;
            }
        };

        mFileDownloader = new FileDownloader(mRequestQueue, 1);

        mRequestQueue.start();
    }

    public static <T> void addRequest(Request<T> request) {
        addRequest(request, null);
    }

    public static <T> void addRequest(Request<T> request, Object tag) {
        if (tag != null) request.setTag(tag);
        mRequestQueue.add(request);
    }

    /**
     * 取消正在请求队列中的请求
     * @param tag 需要取消的请求TAG
     */
    public static void cancelRequest(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    // 加载单张图片
    public static Bitmap displayImage(String url, ImageView imageView) {
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, 0, 0);
        mImageLoader.get(url, listener, 0, 0);
        return null;
    }

    // 加载单张图片
    public static Bitmap displayAdImage(String url, ImageView imageView) {
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.mipmap.pic_market_placeholder, 0);
        mImageLoader.get(url, listener, 0, 0);
        return null;
    }

    // 加载单张图片
    public static Bitmap displayBabyImage(String url, ImageView imageView) {
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.mipmap.pic_goods_placeholder, 0);
        mImageLoader.get(url, listener, 0, 0);
        return null;
    }

    // 批量加载图片
    public static void displayImage(String url, NetworkImageView imageView) {
        imageView.setImageUrl(url, mImageLoader);
    }

    public static ImageLoader getImageLoader() {
        return mImageLoader;
    }

    // 执行文件下载请求
    public static FileDownloader.DownloadController addFileDownload(String storeFilePath, String
            url, Listener<Void> listener) {
        return mFileDownloader.add(storeFilePath, url, listener);
    }


}

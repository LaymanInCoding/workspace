package com.witmoon.xmb.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * SD卡工具类
 * Created by zhyh on 2015/4/29.
 */
public class SDCardUtils {
    private SDCardUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("SDCardUtils不允许实例化");
    }

    // 判断SDCard是否可用
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // 获取SD卡路径
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    // 获取SD卡的剩余容量 单位byte
    public static long getSDCardSize() {
        if (isSDCardEnable()) {
            StatFs statFs = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = statFs.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = statFs.getFreeBlocks();

            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }
}

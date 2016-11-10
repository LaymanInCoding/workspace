package com.witmoon.xmb.activity.friendship;

import com.witmoon.xmb.R;

/**
 * 用户级别枚举
 * Created by zhyh on 2015/8/2.
 */
public enum UserLevel {
    LEVEL_1(1, R.mipmap.level_1),
    LEVEL_2(2, R.mipmap.level_2),
    LEVEL_3(3, R.mipmap.level_3),
    LEVEL_4(4, R.mipmap.level_4),
    LEVEL_5(5, R.mipmap.level_5),
    LEVEL_6(6, R.mipmap.level_6),
    LEVEL_7(7, R.mipmap.level_7),
    LEVEL_8(7, R.mipmap.level_8),;

    private int resId;
    private int rank;

    UserLevel(int rank, int resId) {
        this.rank = rank;
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    public static UserLevel getLevel(int rank) {
        switch (rank) {
            case 1:
                return LEVEL_1;
            case 2:
                return LEVEL_2;
            case 3:
                return LEVEL_3;
            case 4:
                return LEVEL_4;
            case 5:
                return LEVEL_5;
            case 6:
                return LEVEL_6;
            case 7:
                return LEVEL_7;
            case 8:
                return LEVEL_8;
        }
        return LEVEL_1;
    }
}

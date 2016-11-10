package com.witmoon.xmb;

import com.witmoon.xmb.activity.babycenter.BabyCenterFragment;
import com.witmoon.xmb.activity.me.MeFragment;
import com.witmoon.xmb.activity.shopping.MabaoFragment;
import com.witmoon.xmb.activity.mbq.fragment.MbqFragment;
import com.witmoon.xmb.activity.service.ServiceIndexFragment;

/**
 * App主Tab页枚举类
 * Created by zhyh on 2015/6/15.
 */
public enum MainTab {
    SPECIAL_MB(0, R.string.text_special_mb, R.drawable.main_tab_special_baby,
            BabyCenterFragment.class),
    MB_SERVICE(1, R.string.text_mb_service, R.drawable.main_tab_mbfw,
            ServiceIndexFragment.class),
    TAX_EXEMPTION(2, R.string.text_tax_exemption, R.drawable.main_tab_tax_exemption,
            MabaoFragment.class),
    CANULA_CIRCLE(3,R.string.text_circle,R.drawable.main_tab_friendship, MbqFragment.class),
    ME(4, R.string.text_me, R.drawable.main_tab_me, MeFragment.class);
    private int idx;
    private int resName;
    private int resIcon;
    private Class<?> clz;

    MainTab(int idx, int resName, int resIcon, Class<?> clz) {
        this.idx = idx;
        this.resName = resName;
        this.resIcon = resIcon;
        this.clz = clz;
    }

    public int getIdx() {
        return idx;
    }

    public int getResName() {
        return resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public Class<?> getClz() {
        return this.clz;
    }
}

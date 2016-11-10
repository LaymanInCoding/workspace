package com.witmoon.xmb.activity.me;

import android.support.v4.app.Fragment;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.fragment.OrderFragment;
import com.witmoon.xmb.activity.me.fragment.Out_PriceFragment;

/**
 * Created by de on 2015/11/24.
 */
public enum Out_price {
    ALL_("all", R.string.text_all, Out_PriceFragment.class),
    PERSONAL("personal", R.string.text_wait_for_payment, Out_PriceFragment.class),
            ;

    private String type;
    private int resName;
    private Class<? extends Fragment> clz;

    Out_price(String type, int resName, Class<? extends Fragment> clz) {
        this.type = type;
        this.resName = resName;
        this.clz = clz;
    }

    public String getType() {
        return type;
    }

    public int getResName() {
        return resName;
    }

    public Class<? extends Fragment> getClz() {
        return clz;
    }
}
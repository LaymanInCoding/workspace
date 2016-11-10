package com.witmoon.xmb.activity.me;

import android.support.v4.app.Fragment;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.fragment.OrderFragment;

/**
 * 订单Tab页定义枚举
 * Created by zhyh on 2015/6/15.
 */
public enum OrderTab {

    ALL("all", R.string.text_all, OrderFragment.class),
    WAIT_PAYMENT("await_pay", R.string.text_wait_for_payment, OrderFragment.class),
    WAIT_SEND("await_ship", R.string.text_wait_for_send, OrderFragment.class),
    WAIT_RECEIVE("shipped", R.string.text_shipped, OrderFragment.class),
    FINISHED("finished", R.string.text_finished, OrderFragment.class),
    ;

    private String type;
    private int resName;
    private Class<? extends Fragment> clz;

    OrderTab(String type, int resName, Class<? extends Fragment> clz) {
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

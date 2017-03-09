package com.witmoon.xmb.model.event;

/**
 * Created by de on 2017/3/2.
 */
public class CartRefreshCd {
    private boolean isRefresh;

    public CartRefreshCd(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean getIsRefresh() {
        return isRefresh;
    }
}

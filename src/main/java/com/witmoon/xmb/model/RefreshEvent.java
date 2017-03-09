package com.witmoon.xmb.model;

/**
 * Created by de on 2017/2/27.
 */
public class RefreshEvent {
    private boolean isRefresh;

    public RefreshEvent(boolean isRefresh){
        this.isRefresh = isRefresh;
    }
    public boolean getIsRefresh(){
        return isRefresh;
    }
}

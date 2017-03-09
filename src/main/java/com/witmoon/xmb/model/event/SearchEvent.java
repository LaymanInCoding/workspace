package com.witmoon.xmb.model.event;

/**
 * Created by de on 2017/2/27.
 */
public class SearchEvent {
    private String searchKey;

    public SearchEvent(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }
}

package com.witmoon.xmb.model;

import java.util.ArrayList;

/**
 * Created by de on 2016/1/28
 */
public class Record extends BaseBean{
    private String data;
    private ArrayList<RecordDetails> recordList;
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ArrayList<RecordDetails> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<RecordDetails> recordList) {
        this.recordList = recordList;
    }
}

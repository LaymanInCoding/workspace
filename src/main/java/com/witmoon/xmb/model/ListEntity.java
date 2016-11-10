package com.witmoon.xmb.model;

import java.io.Serializable;
import java.util.List;

public abstract class ListEntity implements Serializable {

    public abstract List<?> getList();

    public boolean hasMoreData() {
        return true;
    }
}

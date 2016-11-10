package com.witmoon.xmb.model;

/**
 * 商品分类
 * Created by zhyh on 2015-07-11.
 */
public class Category extends BaseBean {
    private String id;
    private String name;
    private Category parent;

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getParent() {
        return parent;
    }
}

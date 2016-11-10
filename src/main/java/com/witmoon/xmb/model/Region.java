package com.witmoon.xmb.model;

/**
 * 行政区实体
 * Created by zhyh on 2015/7/16.
 */
public class Region {
    private String id;
    private String parentId;
    private String name;
    private String type;
    private String agencyId;

    public void setId(String id) {
        this.id = id;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAgencyId() {
        return agencyId;
    }
}

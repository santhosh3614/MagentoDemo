package com.fourbuy.android;

/**
 * Created by 3164 on 09-12-2015.
 */
public class StoreInfo {

    private int storeId;

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    private int groupId;
    private String code;
    private String website;
    private String name;
    private int orderId;
    private boolean isActive;
    private int sortOrder;

    public StoreInfo(){
    }

    public StoreInfo(int storeId, int groupId, String code, String website, String name, int orderId, boolean isActive, int sortOrder) {
        this.storeId = storeId;
        this.groupId = groupId;
        this.code = code;
        this.website = website;
        this.name = name;
        this.orderId = orderId;
        this.isActive = isActive;
        this.sortOrder = sortOrder;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getName() {
        return name;
    }

    public String getWebsite() {
        return website;
    }

    public String getCode() {
        return code;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getStoreId() {
        return storeId;
    }


}

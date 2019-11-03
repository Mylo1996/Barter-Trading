package com.barter_trading_app;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UploadedItem {
    public String itemUserId;
    public String itemName;
    public String itemImageUrl;
    public String itemCategory;
    public String itemDescription;
    public String itemKey;
    public String itemVideoUrl;
    public boolean agreed;

    public Map<String,Integer> reviews = new HashMap<String,Integer>();

    public UploadedItem() {
    }

    public UploadedItem(String itemUserId,String itemName, String itemImageUrl, String itemCategory, String itemDescription) {
        this.itemUserId = itemUserId;
        this.itemName = itemName;
        this.itemImageUrl = itemImageUrl;
        this.itemCategory = itemCategory;
        this.itemDescription = itemDescription;
        this.agreed = false;
    }

    @Exclude
    public String getItemKey() {
        return itemKey;
    }

    @Exclude
    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }
}

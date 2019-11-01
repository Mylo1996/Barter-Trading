package com.barter_trading_app;

public class UploadedItem {
    public String itemUserId;
    public String itemName;
    public String itemImageUrl;
    public String itemCategory;
    public String itemDescription;

    public UploadedItem() {
    }

    public UploadedItem(String itemUserId,String itemName, String itemImageUrl, String itemCategory, String itemDescription) {
        this.itemUserId = itemUserId;
        this.itemName = itemName;
        this.itemImageUrl = itemImageUrl;
        this.itemCategory = itemCategory;
        this.itemDescription = itemDescription;
    }
}

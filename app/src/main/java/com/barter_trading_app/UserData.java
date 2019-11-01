package com.barter_trading_app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserData {
    public String firstName;
    public String sureName;
    public String phoneNumber;
    public String profileImageUrl;
    public Map<String,Integer> rating = new HashMap<String,Integer>();
    public List<String> flagList = new ArrayList<String>();

    public UserData(){

    }

    public UserData(String firstName, String sureName, String phoneNumber) {
        this.firstName = firstName;
        this.sureName = sureName;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/barter-trading-app.appspot.com/o/userimages%2Funknown.jpg?alt=media&token=471dfabe-1e2b-40e9-9b21-ef56ae115be2";
    }
}

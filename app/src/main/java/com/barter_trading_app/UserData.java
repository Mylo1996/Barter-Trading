package com.barter_trading_app;

public class UserData {
    public String firstName;
    public String sureName;
    public String phoneNumber;
    public int rating;
    public String profileImageUrl;

    public UserData(){

    }

    public UserData(String firstName, String sureName, String phoneNumber) {
        this.firstName = firstName;
        this.sureName = sureName;
        this.phoneNumber = phoneNumber;
        this.rating = 0;
        this.profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/barter-trading-app.appspot.com/o/userimages%2Funknown.jpg?alt=media&token=471dfabe-1e2b-40e9-9b21-ef56ae115be2";
    }
}

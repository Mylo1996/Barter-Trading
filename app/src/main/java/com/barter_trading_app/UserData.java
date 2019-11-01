package com.barter_trading_app;

public class UserData {
    public String firstName;
    public String sureName;
    public String phoneNumber;
    public int rating;

    public UserData(){

    }

    public UserData(String firstName, String sureName, String phoneNumber) {
        this.firstName = firstName;
        this.sureName = sureName;
        this.phoneNumber = phoneNumber;
        this.rating = 0;
    }
}

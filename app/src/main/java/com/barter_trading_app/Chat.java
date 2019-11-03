package com.barter_trading_app;

public class Chat {
    public String sender;
    public String receiver;
    public String message;

    public Chat() {
    }

    public Chat(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }
}

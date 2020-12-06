package com.example.firebaseappdemo;

public class Chats {

    long timestamp;

    Chats(){

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Chats(long timestamp) {
        this.timestamp = timestamp;
    }
}

package com.example.firebaseappdemo;

public class Requests {

    String req_type;

    public Requests(){

    }

    public Requests(String req_type) {
        this.req_type = req_type;
    }

    public String getReq_type() {
        return req_type;
    }

    public void setReq_type(String req_type) {
        this.req_type = req_type;
    }
}

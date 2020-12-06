package com.example.firebaseappdemo;

public class groups {

  String groupname;

  String groupimage;

    public String getGroupimage() {
        return groupimage;
    }

    public void setGroupimage(String groupimage) {
        this.groupimage = groupimage;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname,String groupimage) {
        this.groupname = groupname;
        this.groupimage = groupimage;
    }

    public groups() {
    }

    public groups(String groupname) {
        this.groupname = groupname;
    }
}

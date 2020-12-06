package com.example.firebaseappdemo;

class Users {


    String username;

    String Image;

    String status;





   public Users()
    {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username,String Image,String status) {
        this.username = username;
        this.Image=Image;
        this.status = status;
    }

    public Users(String username) {
        this.username = username;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

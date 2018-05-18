package com.androiders.walknearn.model;

public class User {

    // Details of user on local database
    private String Username, Email, Password, photoUrl;
    private int WalkCoins;

    // Getter and setter methods to set and retrieve user data
    public int getWalkCoins() {
        return WalkCoins;
    }

    public void setWalkCoins(int walkCoins) {
        WalkCoins = walkCoins;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

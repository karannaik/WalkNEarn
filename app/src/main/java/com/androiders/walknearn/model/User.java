package com.androiders.walknearn.model;

public class User {

    // Details of user on local database
    private String Username, Email, Password, photoUrl;
    private double WalkCoins;

    // Getter and setter methods to set and retrieve user data
    public double getWalkCoins() {
        return WalkCoins;
    }

    public void setWalkCoins(double walkCoins) {
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

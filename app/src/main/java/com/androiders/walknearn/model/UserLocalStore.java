package com.androiders.walknearn.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

// Works as the temporary database using shared preferences
public class UserLocalStore {

    public static final String SP_Name = "UserDetails";
    SharedPreferences UserLocalDatabase;

    // Constructor
    public UserLocalStore(Context context){
        this.UserLocalDatabase = context.getSharedPreferences(SP_Name,0);
    }

    // Method updates the data
    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = UserLocalDatabase.edit();
        spEditor.putString("UserName",user.getUsername());
        spEditor.putString("Email",user.getEmail());
        spEditor.putString("Password",user.getPassword());
        spEditor.putLong("StepCount",Double.doubleToRawLongBits(user.getWalkCoins()));
        spEditor.putString("PhotoURL",user.getPhotoUrl());
        spEditor.commit();
    }

    // Method returns the details of the user
    public User getLoggedInUser(){
        User loggedUser = new User();
        loggedUser.setEmail(UserLocalDatabase.getString("Email",""));
        loggedUser.setUsername(UserLocalDatabase.getString("UserName",""));
//        if (UserLocalDatabase.getInt("StepCount",0)!=0) {//temp if statment as it was crashing, @jyotsna please look into this
//            loggedUser.setWalkCoins(Double.longBitsToDouble(UserLocalDatabase.getLong("StepCount",0)));
//        }
        loggedUser.setPassword(UserLocalDatabase.getString("Password",""));
        loggedUser.setPhotoUrl(UserLocalDatabase.getString("PhotoURL",""));
        return loggedUser;
    }

    // Method updates the password
    public void updateUserPassword(String password){
        SharedPreferences.Editor spEditor = UserLocalDatabase.edit();
        spEditor.putString("Password",password);
        spEditor.commit();
    }

    // Method updates the display name
    public void updateDisplayName(String name){
        SharedPreferences.Editor spEditor = UserLocalDatabase.edit();
        spEditor.putString("UserName",name);
        spEditor.commit();
    }

    // Method sets if the user is logged in or not
    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = UserLocalDatabase.edit();
        spEditor.putBoolean("LoggedIn",loggedIn);
        spEditor.commit();
    }

    // Method helps in checking if the user is logged in or not
    public boolean isUserLoggedIn(){
        if(UserLocalDatabase.getBoolean("LoggedIn",false))
            return true;
        else
            return false;
    }

    // Method helps in clearing the entire data
    public void clearUserData(){
        SharedPreferences.Editor spEditor = UserLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

}

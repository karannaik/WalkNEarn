package com.androiders.walknearn.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.androiders.walknearn.model.User;

public class UserLocalStore {

    public static final String SP_Name = "UserDetails";
    SharedPreferences UserLocalDatabase;

    public UserLocalStore(Context context){
        this.UserLocalDatabase = context.getSharedPreferences(SP_Name,0);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = UserLocalDatabase.edit();
        spEditor.putString("UserName",user.getUsername());
        spEditor.putString("Email",user.getEmail());
        spEditor.putString("Password",user.getPassword());
        spEditor.putInt("StepCount",user.getStepCount());
        spEditor.commit();
    }

    public User getLoggedInUser(){

        User loggedUser = new User();
        loggedUser.setEmail(UserLocalDatabase.getString("Email",""));
        loggedUser.setUsername(UserLocalDatabase.getString("UserName",""));
        loggedUser.setStepCount(UserLocalDatabase.getInt("StepCount",0));
        loggedUser.setPassword(UserLocalDatabase.getString("Password",""));
        return loggedUser;
    }

    public void updateUserPassword(String password){
        SharedPreferences.Editor spEditor = UserLocalDatabase.edit();
        spEditor.putString("Password",password);
        spEditor.commit();
    }

    public void updateDisplayName(String name){
        SharedPreferences.Editor spEditor = UserLocalDatabase.edit();
        spEditor.putString("UserName",name);
        spEditor.commit();
    }

    public void setUserLggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = UserLocalDatabase.edit();
        spEditor.putBoolean("LoggedIn",loggedIn);
        spEditor.commit();
    }

    public boolean isUserLoggedIn(){
        if(UserLocalDatabase.getBoolean("LoggedIn",false))
            return true;
        else
            return false;
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor = UserLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

}

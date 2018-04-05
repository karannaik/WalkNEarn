package com.androiders.walknearn;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLocalStore {

    public static final String SP_Name = "UserDetails";
    SharedPreferences UserLocalDatabase;

    public UserLocalStore(Context context){
        this.UserLocalDatabase = context.getSharedPreferences(SP_Name,0);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = UserLocalDatabase.edit();
        spEditor.putString("UserName",user.Username);
        spEditor.putString("Email",user.Email);
        spEditor.putString("Password",user.Password);
        spEditor.putInt("StepCount",user.StepCount);
        spEditor.commit();
    }

    public User getLoggedInUser(){
        String UserName = UserLocalDatabase.getString("UserName","");
        String Email = UserLocalDatabase.getString("Email","");
        String Password = UserLocalDatabase.getString("Password","");
        int StepCount = UserLocalDatabase.getInt("StepCount",0);
        User LoggedUser = new User(UserName,Email,Password,StepCount);
        return LoggedUser;
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

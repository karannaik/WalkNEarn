package com.androiders.walknearn;

public class User {

    String Username,Email,Password;
    int StepCount;

    public User(String name,String email,String password,int stepCount){
        this.Username = name;
        this.Email = email;
        this.Password = password;
        this.StepCount = stepCount;
    }

    public User(String email,String password,String name){
        this.Username = name;
        this.Email = email;
        this.Password = password;
    }

}

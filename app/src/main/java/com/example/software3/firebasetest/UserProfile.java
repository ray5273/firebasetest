package com.example.software3.firebasetest;

public class UserProfile {
    public String emailAddress;
    public String name;
    public int age;
    public String location;
    public boolean isDisable;
    public String category;


    public UserProfile(String emailAddress,String name, int age, String location, boolean isDisable, String category) {
        this.emailAddress = emailAddress;
        this.name = name;
        this.age = age;
        this.location = location;
        this.isDisable = isDisable;
        if(isDisable){
            this.category = category;
        }else
            this.category = null;

    }
}

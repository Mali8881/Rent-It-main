package com.example.rent_it.Model;

import android.content.Context;

import java.util.List;

public class User {
    private  String id;
    private String name;
    private  String fullName;
    private String email;
    private  String imageUrl;
    private  String bio;
    private String phone;


    private Context context;
    private List<User> users;

    public User(String id, String userName, String fullName, String email) {
        this.id = id;
        this.name = userName;
        this.fullName = fullName;
        this.email = email;
        this.imageUrl = imageUrl;
        this.bio = bio;
        this.context = context;
        this.users = users;
        this.phone = phone;

    }

    public  User(){
    }


    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return name;
    }

    public void setUserName(String userName) {
        this.name = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return null;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}

package com.example.connekt.model;

public class User {
    private String id;
    private String full_name;
    private String email;
    private String user_name;
    private String bio;
    private String image_url;
    private String phone_number;
    private String dob;
    private String status;
    private String last_seen;

    public User() {
    }

    public User(String id, String full_name, String email, String user_name, String bio, String image_url, String phone_number, String dob, String status, String last_seen) {
        this.id = id;
        this.full_name = full_name;
        this.email = email;
        this.user_name = user_name;
        this.bio = bio;
        this.image_url = image_url;
        this.phone_number = phone_number;
        this.dob = dob;
        this.status = status;
        this.last_seen = last_seen;
    }

    public User(String id, String full_name, String email, String username, String bio, String image_url, String phone_number, String dob) {
        this.id = id;
        this.full_name = full_name;
        this.email = email;
        this.user_name = username;
        this.bio = bio;
        this.image_url = image_url;
        this.phone_number = phone_number;
        this.dob = dob;
    }

    public String getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(String last_seen) {
        this.last_seen = last_seen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}

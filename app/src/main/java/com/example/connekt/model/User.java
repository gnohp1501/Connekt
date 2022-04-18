package com.example.connekt.model;

public class User {
    private String id;
    private String full_name;
    private String email;
    private String username;
    private String bio;
    private String image_url;
    private String phone_number;
    private String dob;

    public User() {
    }

    public User(String id, String full_name, String email, String username, String bio, String image_url, String phone_number, String dob) {
        this.id = id;
        this.full_name = full_name;
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.image_url = image_url;
        this.phone_number = phone_number;
        this.dob = dob;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

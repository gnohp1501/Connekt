package com.example.connekt.model;

import com.example.connekt.constant.Constant;
import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName(Constant.USER_ID)
    private String user_id;
    @SerializedName(Constant.TITLE)
    private String title;
    @SerializedName(Constant.POST_ID)
    private String post_id;
    @SerializedName(Constant.IS_POST)
    private boolean isPost;

    public Notification() {
    }

    public Notification(String user_id, String title, String post_id, boolean isPost) {
        this.user_id = user_id;
        this.title = title;
        this.post_id = post_id;
        this.isPost = isPost;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}

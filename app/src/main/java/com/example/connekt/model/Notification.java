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
    @SerializedName(Constant.TIME_CREATED)
    private String time_created;
    @SerializedName(Constant.IS_POST)
    private boolean isPost;

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    public Notification(String user_id, String title, String post_id, String time_created, boolean isPost) {
        this.user_id = user_id;
        this.title = title;
        this.post_id = post_id;
        this.time_created = time_created;
        this.isPost = isPost;
    }

    public Notification(String user_id, String title, String post_id, boolean isPost) {
        this.user_id = user_id;
        this.title = title;
        this.post_id = post_id;
        this.isPost = isPost;
    }

    public Notification() {
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

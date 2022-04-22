package com.example.connekt.model;

import com.example.connekt.constant.Constant;
import com.google.gson.annotations.SerializedName;
public class Post {
    @SerializedName(Constant.DESCRIPTION)
    private String description;
    @SerializedName(Constant.IMAGE_URL)
    private String image_url;
    @SerializedName(Constant.POST_ID)
    private String post_id;
    @SerializedName(Constant.PUBLISHER)
    private String publisher;
    @SerializedName(Constant.TIME_CREATED)
    private String time_created;

    public Post() {
    }

    public Post(String description, String image_url, String post_id, String publisher, String time_created) {
        this.description = description;
        this.image_url = image_url;
        this.post_id = post_id;
        this.publisher = publisher;
        this.time_created = time_created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }
}

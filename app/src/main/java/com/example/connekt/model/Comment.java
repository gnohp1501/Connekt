package com.example.connekt.model;

import com.example.connekt.constant.Constant;
import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName(Constant.ID)
    private String id;
    @SerializedName(Constant.COMMENT)
    private String comment;
    @SerializedName(Constant.PUBLISHER)
    private String publisher;
    @SerializedName(Constant.TIME_CREATED)
    private String time_created;

    public Comment() {
    }

    public Comment(String id, String comment, String publisher, String time_created) {
        this.id = id;
        this.comment = comment;
        this.publisher = publisher;
        this.time_created = time_created;
    }

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}

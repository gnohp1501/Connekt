package com.example.connekt.model;

import com.example.connekt.constant.Constant;
import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName(Constant.ID)
    private  String id;
    @SerializedName(Constant.COMMENT)
    private String comment;
    @SerializedName(Constant.PUBLISHER)
    private String publisher;

    public Comment(){}

    public Comment(String id, String comment, String publisher) {
        this.id = id;
        this.comment = comment;
        this.publisher = publisher;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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

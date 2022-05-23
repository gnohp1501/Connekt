package com.example.connekt.constant;

import java.util.HashMap;

public class Constant {
    public static final String ID = "id";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String USERS = "users";
    public static final String FULL_NAME = "full_name";
    public static final String USER_NAME = "user_name";
    public static final String BIO = "bio";
    public static final String IMAGE_URL = "image_url";
    public static final String PHONE = "phone";
    public static final String BOD = "BOD";
    public static final String POST_ID = "post_id";
    public static final String PUBLISHER_ID = "publisher_id";
    public static final String DESCRIPTION = "description";
    public static final String PUBLISHER = "publisher";
    public static final String TIME_CREATED = "time_created";
    public static final String USER_ID = "user_id";
    public static final String TITLE = "title";
    public static final String IS_POST = "is_post";
    public static final String FOLLOW_YOU = " start follow you.";
    public static final String COMMENT = "comment";
    public static final String COMMENTS = "comments";
    public static final String SAVES = "saves";
    public static final String LIKE = "like";
    public static final String LIKES = "likes";
    public static final String POSTS = "posts";
    public static final String FOLLOW = "follow";
    public static final String FOLLOWING = "following";
    public static final String FOLLOWERS = "followers";
    public static final String DEFAULT = "default";


    public static final String AUTHOR_ID = "author_id";
    public static final String SAVE = "save";
    public static final String PROFILE = "profile";
    public static final String PROFILE_ID = "profile_id";
    public static final String SAVED = "saved";
    public static final String LIKED = "liked";
    public static final String NOTIFICATIONS = "notifications";
    public static final String FOLLOWINGS = "followings";
    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String MESS = "mess";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String IS_SEEN = "is_seen";
    public static final String CHATS = "chats";
    public static final String CHAT_LIST = "chat_list";
    public static final String STATUS = "status";
    public static final String LAST_SEEN = "last_seen";
    public static final String ONLINE = "online";
    public static final String NONE = "none";
    public static final String FCM_KEY = "fcm_token";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String NO_CREATE = "no_create";
    public static final String CREATED = "created";

    public static HashMap<String, String> remoteMsgHeaders = null;

    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAr1o--Q0:APA91bGpQTjBQ14NzbkYpZGufPtCTIJCOAFrhfdVOmugrwWFey9BPOzCJDkngE3OkEN1lTbE8jlC2M-M2qLIrh03TOHEx18-QevbClWkekpN_GFEkZx6jUNdesss-xDTyJWvRcXYdDN2"

            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}

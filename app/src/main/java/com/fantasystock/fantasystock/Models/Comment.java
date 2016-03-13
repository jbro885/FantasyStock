package com.fantasystock.fantasystock.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by wilsonsu on 3/13/16.
 */
public class Comment {
    @SerializedName("objectId")
    public String commentId;

    @SerializedName("serverData")
    public DataEntity data;
    public Date updatedAt;

    public static class DataEntity {
        @SerializedName("user_id")
        public String userId;
        public User user;
        public String comment;
        public String symbol;
    }
}

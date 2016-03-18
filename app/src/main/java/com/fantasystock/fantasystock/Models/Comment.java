package com.fantasystock.fantasystock.Models;

import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        public String comment;
        public String symbol;
    }




    public static Comment parseComment(ParseObject object) {
        Gson gson = Utils.gsonForParseQuery();
        JsonElement json = gson.toJsonTree(object);
        String jsonString =json.getAsJsonObject().get("state").toString();
        Comment comment = gson.fromJson(jsonString, Comment.class);
        return comment;
    }

    public static Comment parseAddingComment(ParseObject object) {
        Comment comment = new Comment();
        comment.data = new DataEntity();
        comment.updatedAt = new Date();
        comment.data.comment = object.getString(USER_COMMENT_TEXT);
        comment.data.symbol = object.getString(USER_COMMENT_SYMBOL);
        comment.data.userId = object.getString(USER_COMMENT_USER_ID);
        return comment;
    }




    /**
     * Comments in Parse
     */
    private static final String USER_COMMENT = "user_comment";
    private static final String USER_COMMENT_TEXT = "comment";
    private static final String USER_COMMENT_USER_ID = "user_id";
    private static final String USER_COMMENT_SYMBOL = "symbol";
    private static final String USER_COMMENT_ID = "objectId";

    public static void addComment(String text, String symbol, final CallBack callBack) {
        if (text==null || symbol == null || User.currentUser == null) {
            return;
        }
        final ParseObject comment = new ParseObject(USER_COMMENT);
        comment.put(USER_COMMENT_TEXT, text);
        comment.put(USER_COMMENT_SYMBOL, symbol);
        comment.put(USER_COMMENT_USER_ID, User.currentUser.id);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    callBack.onFail(e.toString());
                } else {
                    callBack.commentCallBack(parseAddingComment(comment));
                }
            }
        });
    }

    public static void getComments(String symbol, final CallBack callback) {
        if (callback==null) return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_COMMENT);
        if (symbol!=null) {
            query.whereEqualTo(USER_COMMENT_SYMBOL, symbol);
        }
        queryComments(query, callback);
    }

    public static void getComment(String commentId, final CallBack callback) {
        if (callback==null) return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_COMMENT);
        if (commentId!=null) {
            query.whereEqualTo(USER_COMMENT_ID, commentId);
        }
        queryComments(query, callback);
    }

    private static void queryComments(ParseQuery<ParseObject> query, final CallBack callback) {
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e != null) callback.onFail(e.toString());
                int len = scoreList.size();
                ArrayList<Comment> comments = new ArrayList<>();
                for (int i = len - 1; i >= 0; --i) {
                    comments.add(parseComment(scoreList.get(i)));
                }
                callback.commentsCallBack(comments);
            }
        });
    }
}

package com.example.login;

import android.util.Log;


public class PostComment {
    private String text;
    private int postId;
    private int commentId;
    private int userId;
    private String commentDate;

    public PostComment(String text, int postId, int commentId, int userId, String commentDate) {
        this.text = text;
        this.postId = postId;
        this.commentId = commentId;
        this.userId = userId;
        this.commentDate = commentDate;
    }

    public int getUserId() { return this.userId; }

    public String getText() {
        return text;
    }
    public String getDate() { return this.commentDate; }
    public int getPostId() {
        return postId;
    }
    public void setCommentId(int a) {
        this.commentId = a;
        Log.d("set comment id", String.valueOf(this.commentId));
    }
    public int getCommentId() {
        Log.d("get comment id", String.valueOf(this.commentId));
        return this.commentId;
    }
}
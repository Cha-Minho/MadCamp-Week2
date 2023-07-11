package com.example.login;

import android.util.Log;

public class PostComment {
    private String text;
    private int postId;
    private int commentId;

    public PostComment(String text, int postId, int commentId) {
        this.text = text;
        this.postId = postId;
        this.commentId = commentId;
    }

    public String getText() {
        return text;
    }

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
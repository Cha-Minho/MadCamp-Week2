package com.example.login;

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

    public int getCommentId() {
        return commentId;
    }
}
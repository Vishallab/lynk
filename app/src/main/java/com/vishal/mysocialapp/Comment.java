package com.vishal.mysocialapp;

public class Comment {
    private String userId;
    private String commentText;
    private String avatarUrl;
    private String username;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String userId, String commentText, String avatarUrl, String username) {
        this.userId = userId;
        this.commentText = commentText;
        this.avatarUrl = avatarUrl;
        this.username = username;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

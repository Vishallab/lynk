package com.vishal.mysocialapp;

public class ChatUser {
    private String avatarUrl;
    private String email;
    private String name;
    private String username;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public ChatUser() { }

    public ChatUser(String avatarUrl, String email, String name, String username) {
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.name = name;
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

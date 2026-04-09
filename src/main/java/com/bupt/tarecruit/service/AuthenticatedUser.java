package com.bupt.tarecruit.service;

public class AuthenticatedUser {
    private final String userId;
    private final String role;
    private final String name;

    public AuthenticatedUser(String userId, String role, String name) {
        this.userId = userId;
        this.role = role;
        this.name = name;
    }

    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public String getName() { return name; }
}

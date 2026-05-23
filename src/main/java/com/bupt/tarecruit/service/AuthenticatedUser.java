package com.bupt.tarecruit.service;

/**
 * Immutable session payload produced after successful authentication.
 * Only the fields required by the web layer are exposed here.
 */
public class AuthenticatedUser {
    private final String userId;
    private final String role;
    private final String name;

    /**
     * Creates the authenticated session view of a user account.
     *
     * @param userId canonical account ID
     * @param role application role used for access control
     * @param name display name shown in the UI
     */
    public AuthenticatedUser(String userId, String role, String name) {
        this.userId = userId;
        this.role = role;
        this.name = name;
    }

    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public String getName() { return name; }
}

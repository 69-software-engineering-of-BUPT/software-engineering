package com.bupt.tarecruit.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bupt.tarecruit.service.AuthenticationException;

public class RoleHomeUtilTest {

    @Test
    public void resolvesHomePathForSupportedRoles() {
        assertEquals("/ta/home", RoleHomeUtil.resolveHomePath("TA"));
        assertEquals("/mo/home", RoleHomeUtil.resolveHomePath(" mo "));
        assertEquals("/ad/accounts", RoleHomeUtil.resolveHomePath("admin"));
    }

    @Test
    public void rejectsUnsupportedRoles() {
        expectUnsupportedRole(null);
        expectUnsupportedRole("guest");
    }

    private void expectUnsupportedRole(String role) {
        try {
            RoleHomeUtil.resolveHomePath(role);
        } catch (AuthenticationException ex) {
            assertEquals("Unsupported user role.", ex.getMessage());
            return;
        }
        throw new AssertionError("Expected AuthenticationException");
    }
}

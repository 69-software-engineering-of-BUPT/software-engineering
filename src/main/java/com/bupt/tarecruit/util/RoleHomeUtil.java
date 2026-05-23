package com.bupt.tarecruit.util;

import java.util.Locale;

import com.bupt.tarecruit.service.AuthenticationException;

public final class RoleHomeUtil {
    private RoleHomeUtil() {
    }

    public static String resolveHomePath(String role) {
        String normalizedRole = normalizeRole(role);
        if ("TA".equals(normalizedRole)) {
            return "/ta/home";
        }
        if ("MO".equals(normalizedRole)) {
            return "/mo/home";
        }
        if ("ADMIN".equals(normalizedRole)) {
            return "/ad/accounts";
        }
        throw new AuthenticationException("Unsupported user role.");
    }

    public static String normalizeRole(String role) {
        if (role == null) {
            throw new AuthenticationException("Unsupported user role.");
        }
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        if ("TA".equals(normalized) || "MO".equals(normalized) || "ADMIN".equals(normalized)) {
            return normalized;
        }
        throw new AuthenticationException("Unsupported user role.");
    }
}

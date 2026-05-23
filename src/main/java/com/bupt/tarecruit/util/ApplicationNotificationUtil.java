package com.bupt.tarecruit.util;

import java.util.Locale;

public final class ApplicationNotificationUtil {

    private ApplicationNotificationUtil() {}

    public static String buildStatusUpdateContent(String status, String moduleName, String feedback, String moMessage) {
        String module = isBlank(moduleName) ? "the position" : moduleName.trim();
        String normalizedStatus = isBlank(status) ? "UPDATED" : status.trim().toUpperCase(Locale.ROOT);

        String content;
        switch (normalizedStatus) {
            case "APPROVED":
                content = "Congratulations! Your application for " + module + " has been approved.";
                break;
            case "REJECTED":
                content = "Your application for " + module + " has not been successful at this time.";
                break;
            case "INTERVIEW":
                content = "You have been invited to an interview for " + module + ".";
                break;
            case "PENDING":
                content = "Your application for " + module + " has been moved back to pending review.";
                break;
            default:
                content = "Your application status for " + module + " has been updated to " + normalizedStatus + ".";
                break;
        }

        if (!isBlank(feedback)) {
            content += " Feedback: " + feedback.trim();
        }
        if (!isBlank(moMessage)) {
            content += " Message: " + moMessage.trim();
        }
        return content;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

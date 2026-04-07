package com.bupt.tarecruit.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TimeUtil {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private TimeUtil() {
    }

    public static String nowDateTime() {
        return LocalDateTime.now().format(DATE_TIME);
    }

    public static String today() {
        return LocalDate.now().format(DATE);
    }

    public static boolean isExpired(String deadline) {
        if (deadline == null || deadline.trim().isEmpty()) {
            return false;
        }
        return LocalDate.parse(deadline.trim(), DATE).isBefore(LocalDate.now());
    }
}

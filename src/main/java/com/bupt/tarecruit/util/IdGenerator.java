package com.bupt.tarecruit.util;

import java.util.UUID;

public final class IdGenerator {
    private IdGenerator() {
    }

    public static String newId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}

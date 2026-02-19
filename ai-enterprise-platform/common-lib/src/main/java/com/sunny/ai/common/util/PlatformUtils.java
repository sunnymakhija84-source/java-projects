package com.sunny.ai.common.util;

import java.util.UUID;

/**
 * Utility methods for the AI Platform.
 */
public final class PlatformUtils {

    private PlatformUtils() {
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }

    public static String truncate(String text, int maxLength) {
        if (text == null)
            return null;
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }
}

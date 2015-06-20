package com.kenai.reminder;

public final class UrlUtil {
    
    private UrlUtil() {
        throw new AssertionError("not instantiable");
    }
    
    public static String toUrlSafe(String timeZoneId) {
        return timeZoneId.replace('/', '!');
    }
    
    public static String fromUrlSafe(String timeZoneId) {
        return timeZoneId.replace('!', '/');
    }

}

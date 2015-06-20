package com.kenai.reminder;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class Constants {
    
    /**
     * Private default constructor to prevent instantiation.
     */
    private Constants() {
        throw new AssertionError("not instantiable");
    }

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
    
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("HH:mm");

}

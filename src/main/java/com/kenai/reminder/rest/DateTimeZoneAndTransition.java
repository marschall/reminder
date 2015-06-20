package com.kenai.reminder.rest;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

final class DateTimeZoneAndTransition {

    private final DateTimeZone zone;

    private final DateTime currentInstant;
    
    private final DateTime previousInstant;

    public DateTimeZoneAndTransition(DateTimeZone zone, long instant) {
        this.zone = zone;
        this.currentInstant = new DateTime(instant, zone);
        this.previousInstant = new DateTime(zone.previousTransition(instant), zone);
    }

    DateTimeZone getZone() {
        return this.zone;
    }

    DateTime getCurrentTransition() {
        return this.currentInstant;
    }
    
    DateTime getPreviousTransition() {
        return this.previousInstant;
    }

}

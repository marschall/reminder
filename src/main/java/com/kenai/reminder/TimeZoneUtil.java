package com.kenai.reminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for dealing with time zones and transitions.
 * 
 * @author Philippe Marschall
 */
public final class TimeZoneUtil {
    
    public static final Logger LOG = LoggerFactory.getLogger(TimeZoneUtil.class);
    
    private static final Comparator<DateTimeZone> ZONE_ID_COMPARATOR = new DateTimeZoneIdComparator();
    
    private static final Locale ZONE_ID_LOCALE = Locale.US;
    
    private static final List<DateTimeZone> ZONES;
    
    static {
        Set<?> ids = DateTimeZone.getAvailableIDs();

        Set<DateTimeZone> zoneSet = new HashSet<DateTimeZone>();
        long now = System.currentTimeMillis();
        for (Object object : ids) {
            DateTimeZone zone = DateTimeZone.forID((String) object);
            if (!zone.isFixed() && zone.nextTransition(now) != now) {
                // exclude fixed time zones
                // and ones that don't have any transitions anymore
                zoneSet.add(zone);
            }
        }

        List<DateTimeZone> zoneList = new ArrayList<DateTimeZone>(zoneSet);
        Collections.sort(zoneList, ZONE_ID_COMPARATOR);

        ZONES = Collections.unmodifiableList(zoneList);
    }
    
    private TimeZoneUtil() {
        throw new AssertionError("not instantiable");
    }
    
    public static String feedUrlFor(DateTimeZone zone, String contextPath) {
        String urlSafeTimeZoneId = UrlUtil.toUrlSafe(zone.getID());
        return contextPath + "/feed/" + urlSafeTimeZoneId + ".atom";
    }
    
    public static String calendarUrlFor(DateTimeZone zone, String contextPath) {
        String urlSafeTimeZoneId = UrlUtil.toUrlSafe(zone.getID());
        return contextPath + "/feed/" + urlSafeTimeZoneId + ".ics";
    }
    
    public static List<DateTimeZone> getAllTimeZones() {
        return ZONES;
    }
    
    
    public static Iterator<DateTimeZone> getChoices(String input, int max) {
        List<DateTimeZone> zones = new ArrayList<DateTimeZone>(max);
        for (DateTimeZone each : ZONES) {
            if (each.getID().toLowerCase(ZONE_ID_LOCALE).contains(input.toLowerCase(ZONE_ID_LOCALE))) {
                zones.add(each);
            }
            if (zones.size() == max) {
                break;
            }
        }
        return zones.iterator();
    }
    
    static final class DateTimeZoneIdComparator implements Comparator<DateTimeZone>, Serializable {

        private static final long serialVersionUID = 4144390577591054282L;

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(DateTimeZone o1, DateTimeZone o2) {
            return o1.getID().compareToIgnoreCase(o2.getID());
        }
    }

    /**
     * Returns a given amount of DST offset transitions for a given time zone
     * starting at a given instant.
     * 
     * @param zone the time zone from which the transitions should be returned,
     *  must not be fixed, must not be {@literal null}
     * @param startInstant the start instant from with {@code n} transitions in
     *  {@code zone} should be returned. Amount of milliseconds since the UNIX
     *  epoch
     * @param n the number of transitions to return must be positive
     * @return
     * @throws IllegalArgumentException if {@code n} is negative or {@code zone} is fixed
     * @throws NullPointerException if {@code zone} is {@literal null}
     */
    public static Iterable<DateTime> nextTransitionsFor(DateTimeZone zone, long startInstant, int n) throws IllegalArgumentException {
        if (n < 0) {
            String message = "n must be bigger than zero but was: " + n;
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
        if (zone.isFixed()) {
            String message = "time zone " + zone.getID() + " is fixed";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
        List<DateTime> result = new ArrayList<DateTime>(n);
        long current = startInstant;
        for (int i = 0; i < n; ++i) {
            long next = zone.nextTransition(current);
            if (next != current) {
                result.add(new DateTime(next, zone));
                current = next;
            } else {
                // can happen if there are no more transitions
                String message = "no more transtions after: " + next + " for " + zone.getID();
                LOG.warn(message);
                break;
            }
        }
        return result;
    }
}

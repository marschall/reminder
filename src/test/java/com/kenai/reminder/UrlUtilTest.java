package com.kenai.reminder;

import com.kenai.reminder.UrlUtil;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTimeZone;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test case for {@link UrlUtil}.
 */
public class UrlUtilTest {
    
    private static final String URL_SAFE;
    
    static {
        StringBuilder buffer = new StringBuilder();
        for (char c = 'A'; c <= 'Z'; ++c) {
            buffer.append(c);
            buffer.append(Character.toLowerCase(c));
        }
        for (char c = '0'; c <= '9'; ++c) {
            buffer.append(c);
        }
        buffer.append("$-_.+!*'(),");
        URL_SAFE = buffer.toString();
    }
    
    private static boolean isUrlSafe(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (URL_SAFE.indexOf(s.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }
    
    private Iterable<DateTimeZone> getNotFixedDateTimeZones() {
        List<DateTimeZone> zones = new ArrayList<DateTimeZone>();
        for (Object id : DateTimeZone.getAvailableIDs()) {
            DateTimeZone zone = DateTimeZone.forID((String) id);
            if (!zone.isFixed()) {
                zones.add(zone);
            }
        }
        return zones;
    }
    
    /**
     * Makes sure all {@link String}s returned by
     * {@link UrlUtil#toUrlSafe(String)} are URL safe and can be decoded by
     * {@link UrlUtil#fromUrlSafe(String)} to the same value again.
     * 
     * <p>Runs this test for all non fixed time zone ids returned by
     * {@link DateTimeZone#getAvailableIDs()} .</p>
     */
    @Test
    public void allSafe() {
        for (DateTimeZone each : this.getNotFixedDateTimeZones()) {
            String id = each.getID();
            String encoded = UrlUtil.toUrlSafe(id);
            assertTrue(isUrlSafe(encoded));
            assertEquals(id, UrlUtil.fromUrlSafe(encoded));
        }
    }

}

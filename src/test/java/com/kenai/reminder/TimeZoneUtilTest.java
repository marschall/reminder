package com.kenai.reminder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.junit.Test;


/**
 * Test for {@link TimeZoneUtil}.
 * 
 * @author Philippe Marschall
 */
public class TimeZoneUtilTest {
    
    /**
     * Test for
     * {@link TimeZoneUtil#nextTransitionsFor(org.joda.time.DateTimeZone, long, int)}.
     */
    @Test
    public void nextTransitionsFor() {
        DateTimeZone sidney = DateTimeZone.forID("Australia/Sydney");
        List<DateTime> transitions = asList(TimeZoneUtil.nextTransitionsFor(sidney, 1240080168066L, 10));
        assertEquals(10, transitions.size());
        DateTime transition = transitions.get(0);
        assertEquals("2009-10-04", transition.toString(Constants.DATE_FORMAT));
        LocalTime time = transition.toLocalTime();
        assertEquals("03:00", time.toString(Constants.TIME_FORMAT));
    }
    
    private <T> List<T> asList(Iterable<T> iterable) {
        return asList(iterable.iterator());
    }
    
    @Test
    public void zurich() {
        Iterator<DateTimeZone> choices = TimeZoneUtil.getChoices("uric", 12);
        assertTrue(choices.hasNext());
        DateTimeZone next = choices.next();
        assertEquals("Europe/Zurich", next.getID());
        assertFalse(choices.hasNext());
    }
    
    @Test
    public void mendoza() {
        Iterator<DateTimeZone> choices = TimeZoneUtil.getChoices("argentina", 100);
        while (choices.hasNext()) {
            DateTimeZone each = choices.next();
            assertFalse(each.getID().contains("Mendoza"));
        }
    }
    
    @Test
    public void limit() {
        Iterator<DateTimeZone> choices = TimeZoneUtil.getChoices("a", 3);
        assertEquals(3, asList(choices).size());
    }
    
    private <T> List<T> asList(Iterator<T> iterator) {
        List<T> list = new ArrayList<T>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

}

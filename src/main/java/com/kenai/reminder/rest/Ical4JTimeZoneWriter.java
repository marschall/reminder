package com.kenai.reminder.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.kenai.reminder.TimeZoneUtil;

/**
 * A {@link javax.ws.rs.ext.MessageBodyWriter} that produces
 * <a href="http://tools.ietf.org/html/rfc5545">iCalendar</a> using
 * <a href="http://ical4j.sourceforge.net">iCal4j</a>.
 * 
 * @author Philippe Marschall
 */
@Provider
@Produces("text/calendar")
public final class Ical4JTimeZoneWriter extends DateTimeZoneWriter {
    
    private static final String TEXT_CALENDAR = "text/calendar";
    private static final MediaType TEXT_CALENDAR_TYPE = MediaType.valueOf(TEXT_CALENDAR);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return TEXT_CALENDAR_TYPE.equals(mediaType)
          && DateTimeZone.class.isAssignableFrom(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo(DateTimeZone zone, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        long nowMillis = System.currentTimeMillis();
        DateTime nowDate = new DateTime(nowMillis, DateTimeZone.getDefault());
        String timeZoneName = zone.getName(nowMillis, this.getLanguage());
        
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//DST Change Reminder//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        //TODO set title
//        calendar.getProperties().add(new Description("DST Offset Changes for " + timeZoneName));
        
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone utc = registry.getTimeZone("UTC");


        if (!zone.isFixed()) {
            DateTime previous = nowDate;
            for (DateTime transition : TimeZoneUtil.nextTransitionsFor(zone, nowMillis, NUMBER_OF_TRANSITIONS)) {
                Component event = this.createEventFor(utc, previous, transition, zone);
                calendar.getComponents().add(event);
                previous = transition;
            }
        }
        
        CalendarOutputter outputter = new CalendarOutputter();
        try {
            outputter.output(calendar, entityStream);
        } catch (ValidationException e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    private Component createEventFor(TimeZone utc, DateTime previousTransition, DateTime currentTransition, DateTimeZone dateTimeZone) {
        net.fortuna.ical4j.model.DateTime start = new net.fortuna.ical4j.model.DateTime(true);
        net.fortuna.ical4j.model.DateTime end = new net.fortuna.ical4j.model.DateTime(true);
        long millis = currentTransition.getMillis();
        start.setTime(millis - 1000L);
        end.setTime(currentTransition.getMillis() + 1001L);
        
        String description = fromatTransition(previousTransition, currentTransition, dateTimeZone);
        VEvent event = new VEvent(start, end, description);
        event.getProperties().add(new Description(description));
        event.getProperties().add(new Uid(dateTimeZone.getID() + '-' + currentTransition.getMillis()));
        
        return event;
    }

}

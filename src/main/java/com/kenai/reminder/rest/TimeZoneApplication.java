package com.kenai.reminder.rest;

import java.util.Set;

import javax.ws.rs.core.Application;

import com.google.common.collect.ImmutableSet;

/**
 * Defines the components of the feed REST application.
 * 
 * @author Philippe Marschall
 */
public final class TimeZoneApplication extends Application {

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Class<?>> getClasses() {
        return ImmutableSet.<Class<?>>of(
                DateTimeZoneResource.class,
                AbderaDateTimeZoneWriter.class,
                Ical4JTimeZoneWriter.class,
                HtmlDateTimeZoneAndTransitionWriter.class,
                PlainDateTimeZoneAndTransitionWriter.class);
    }
    
}

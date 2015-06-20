package com.kenai.reminder.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Locale;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Abstract base class for {@link DateTimeZoneAndTransition}
 * {@link MessageBodyWriter}s.
 * 
 * @author Philippe Marschall
 */
abstract class DateTimeZoneAndTransitionWriter implements MessageBodyWriter<DateTimeZoneAndTransition> {
    
    @Context
    private HttpHeaders httpHeaders;
    
    /**
     * String containing the value for {@literal ";charset=utf-8"}.
     */
    static final String CHARSET_UTF_8 = ";charset=utf-8";

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize(DateTimeZoneAndTransition t, Class<?> type,
            Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return this.getMediaType().equals(mediaType)
            && DateTimeZoneAndTransition.class.isAssignableFrom(type);
    }
    
    /**
     * Returns the language of the current request.
     * 
     * @return the language of the current request
     */
    Locale getLanguage() {
        return this.httpHeaders.getLanguage();
    }
    
    abstract MediaType getMediaType();

}

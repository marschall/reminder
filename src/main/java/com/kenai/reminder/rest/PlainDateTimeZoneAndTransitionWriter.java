package com.kenai.reminder.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Write a single DST offset change transition as plain text.
 * 
 * @author Philippe Marschall
 */
@Provider
@Produces(MediaType.TEXT_PLAIN)
public class PlainDateTimeZoneAndTransitionWriter extends DateTimeZoneAndTransitionWriter {
    
    /**
     * String containing the value for {@literal "text/plain;charset=utf-8"}.
     */
    static final String TEXT_PLAIN_UTF8 = MediaType.TEXT_PLAIN + CHARSET_UTF_8;
    
    /**
     * {@link MediaType} for {@link #TEXT_PLAIN_UTF8}.
     */
    private static final MediaType TEXT_PLAIN_UTF8_TYPE = MediaType.valueOf(TEXT_PLAIN_UTF8);

    /**
     * {@inheritDoc}
     */
    @Override
    MediaType getMediaType() {
        return TEXT_PLAIN_UTF8_TYPE;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo(DateTimeZoneAndTransition entity, Class<?> type,
            Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        
        DateTime previousTransition = entity.getPreviousTransition();
        DateTime currentTransition = entity.getCurrentTransition();
        DateTimeZone zone = entity.getZone();
        
        String message = DateTimeZoneWriter.fromatTransition(previousTransition, currentTransition, zone);
        
        Writer writer = new OutputStreamWriter(entityStream, "UTF-8");
        writer.write(message);
        writer.flush();
    }

}

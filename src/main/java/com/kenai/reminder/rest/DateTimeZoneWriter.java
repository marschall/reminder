package com.kenai.reminder.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Locale;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import com.kenai.reminder.Constants;
import com.kenai.reminder.UrlUtil;

/**
 * Abstract base class for {@link MessageBodyWriter}s that process a
 * {@link DateTimeZone}.
 * 
 * @author Philippe Marschall
 */
abstract class DateTimeZoneWriter implements MessageBodyWriter<DateTimeZone> {

    protected static final int NUMBER_OF_TRANSITIONS = 10;
    
    @Context
    private UriInfo uriInfo;
   
    
    @Context
    private HttpHeaders httpHeaders;

    protected static String fromatTransition(DateTime previousTransition, DateTime currentTransition,
            DateTimeZone zone) {
        LocalTime time = currentTransition.toLocalTime();
        Period previousOffset = Period.millis(zone.getOffset(previousTransition)).normalizedStandard();
        Period currentOffset = Period.millis(zone.getOffset(currentTransition)).normalizedStandard();
        Period delta = currentOffset.minus(previousOffset);

        return String.format("%s \u2192 %s", time.minus(delta).toString(Constants.TIME_FORMAT), time.toString(Constants.TIME_FORMAT));
    }

    UriBuilder getApplicationBaseUri() {
        //FIXME report bug
//        return this.getFeedBaseUri().replacePath(null);
        return this.getFeedBaseUri().replacePath("");
    }

    UriBuilder getFeedBaseUri() {
        return this.uriInfo.getBaseUriBuilder();
    }

    UriBuilder getTimezoneUri(DateTimeZone dateTimeZone) {
        String urlSafeTimeZoneId = UrlUtil.toUrlSafe(dateTimeZone.getID());
        return this.getFeedBaseUri().path(urlSafeTimeZoneId);
    }
    
    protected UriBuilder getTransitionUri(DateTimeZone zone, DateTime transition) {
        return this.getTransitionUri(zone, transition, null);
    }

    UriBuilder getTransitionUri(DateTimeZone zone, DateTime transition, String extension) {
        String millisString = Long.toString(transition.getMillis());
        String fileName;
        if (extension != null) {
            fileName = millisString + '.' + extension;
        } else {
            fileName = millisString;
        }
        return this.getTimezoneUri(zone).path(fileName);
    }

    protected String getLogo32Url() {
        return this.getApplicationBaseUri().path("_img").path("appointment-new-32.png").
                build().toASCIIString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize(DateTimeZone t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }
    
    
    /**
     * Returns the language of the current request.
     * 
     * @return the language of the current request
     */
    Locale getLanguage() {
        return this.httpHeaders.getLanguage();
    }

}

package com.kenai.reminder.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTimeZone;

import com.kenai.reminder.UrlUtil;

import static com.kenai.reminder.rest.PlainDateTimeZoneAndTransitionWriter.TEXT_PLAIN_UTF8;
import static com.kenai.reminder.rest.HtmlDateTimeZoneAndTransitionWriter.TEXT_HTML_UTF8;

/**
 * JAX-RS resource for handling time zones.
 * 
 * @author Philippe Marschall
 */
@Path("/") 
public final class DateTimeZoneResource {

    @GET
    @Produces(MediaType.APPLICATION_ATOM_XML)
    @Path("/{zone_id}.atom") 
    public DateTimeZone getTimeZoneFeed(@PathParam("zone_id") String id) {
        return getTimeZoneFor(id);
    }
    
    @GET
    @Produces("text/calendar")
    @Path("/{zone_id}.ics") // unfortunately content negotiation won't work
    public DateTimeZone getTimeZoneCalendar(@PathParam("zone_id") String id) {
        return getTimeZoneFor(id);
    }

    private static DateTimeZone getTimeZoneFor(String id) {
        return DateTimeZone.forID(UrlUtil.fromUrlSafe(id));
    }
    
    @GET
    @Path("/{zone_id}/{transition}")
    @Produces(TEXT_PLAIN_UTF8)
    public DateTimeZoneAndTransition getTransition(@PathParam("zone_id") String id, @PathParam("transition") long transition) {
        return this.getTransitionText(id, transition);
    }

    @GET
    @Path("/{zone_id}/{transition}.txt")
    @Produces(TEXT_PLAIN_UTF8)
    public DateTimeZoneAndTransition getTransitionText(@PathParam("zone_id") String id, @PathParam("transition") long transition) {
        return new DateTimeZoneAndTransition(getTimeZoneFor(id), transition);
    }
    
    @GET
    @Path("/{zone_id}/{transition}.html")
    @Produces(TEXT_HTML_UTF8)
    public DateTimeZoneAndTransition getTransitionHtml(@PathParam("zone_id") String id, @PathParam("transition") long transition) {
        return new DateTimeZoneAndTransition(getTimeZoneFor(id), transition);
    }
    

}


package com.kenai.reminder.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.kenai.reminder.Constants;
import com.kenai.reminder.TimeZoneUtil;
import com.kenai.reminder.UrlUtil;

/**
 * A {@link javax.ws.rs.ext.MessageBodyWriter} that produces
 * <a href="http://www.ietf.org/rfc/rfc4287.txt">Atom</a> using
 * <a href="http://abdera.apache.org/">Abdera</a>.
 * 
 * @author Philippe Marschall
 */
@Provider
@Produces(MediaType.APPLICATION_ATOM_XML)
public class AbderaDateTimeZoneWriter extends DateTimeZoneWriter {
    
    private static final LocalDate ID_START_DATE = new LocalDate(2009, 9, 10);
    
    private static final DateTimeFormatter ID_START_DATE_FORMAT =  DateTimeFormat.forPattern("YYYY-MM-dd");

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return MediaType.APPLICATION_ATOM_XML_TYPE.equals(mediaType)
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
        Abdera abdera = new Abdera();
        Feed feed = abdera.newFeed();

        // TODO read from request
        // FIXME
        feed.setId("tag:dst-reminder.appspot.com," + ID_START_DATE_FORMAT.print(ID_START_DATE) + ":feed/");
        feed.setTitle(timeZoneName);
        feed.setSubtitle("DST Offset Changes for " + timeZoneName);
        feed.setLogo(this.getLogo32Url());
        feed.setUpdated(nowDate.toDate());
        feed.addAuthor(this.getAuthor(feed.getFactory()));
        String applicationUri = this.getApplicationBaseUri().build().toASCIIString();
        feed.addLink(applicationUri);
        String link = this.getFeedBaseUri().path(UrlUtil.toUrlSafe(zone.getID())). build().toASCIIString();
        feed.addLink(link, "self");

        if (!zone.isFixed()) {
            DateTime previous = nowDate;
            for (DateTime transition : TimeZoneUtil.nextTransitionsFor(zone, nowMillis, NUMBER_OF_TRANSITIONS)) {
                Entry entry = feed.addEntry();
                this.updateEntryFor(entry, previous, transition, zone);
                previous = transition;
            }
        }
        feed.writeTo(entityStream);
    }
    
    private Link getHtmlLinkForEntry(DateTime transition, DateTimeZone zone, Factory factory) {
        Link link = factory.newLink();
        link.setHref(getTransitionUri(zone, transition, "html").build().toASCIIString());
        link.setMimeType(MediaType.TEXT_HTML);
        return link;
    }
    
    private Link getPlainTextLinkForEntry(DateTime transition, DateTimeZone zone, Factory factory) {
        Link link = factory.newLink();
        link.setHref(getTransitionUri(zone, transition, "txt").build().toASCIIString());
        link.setMimeType(MediaType.TEXT_PLAIN);
        return link;
    }
    
    private void updateEntryFor(Entry entry, DateTime previousTransition, DateTime currentTransition, DateTimeZone dateTimeZone) {
        entry.setTitle(currentTransition.toString(Constants.DATE_FORMAT));
        Factory factory = entry.getFactory();
        entry.addLink(this.getHtmlLinkForEntry(currentTransition, dateTimeZone, factory));
        entry.addLink(this.getPlainTextLinkForEntry(currentTransition, dateTimeZone, factory));
        entry.addAuthor(this.getAuthor(factory));
        entry.setId("tag:dst-reminder.appspot.com,"
                + ID_START_DATE_FORMAT.print(ID_START_DATE)
                + ":feed/" + currentTransition.getMillis());
//        DateTime now = new DateTime();
        entry.setUpdated(currentTransition.toDate());
        entry.setPublished(currentTransition.toDate());

        String content = fromatTransition(previousTransition, currentTransition, dateTimeZone);

        entry.setContent(content);
        entry.setSummary(content);
    }

    private Person getAuthor(Factory factory) {
        Person author = factory.newAuthor();
        author.setName("Philippe Marschall");
        author.setEmail("philippe.marschall@gmail.com");
        return author;
    }

}

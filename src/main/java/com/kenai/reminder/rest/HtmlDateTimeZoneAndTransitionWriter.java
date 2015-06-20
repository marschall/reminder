package com.kenai.reminder.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.MessageFormat;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.kenai.reminder.Constants;

/**
 * Write a single DST offset change transition as html.
 * 
 * @author Philippe Marschall
 */
@Provider
@Produces(MediaType.TEXT_HTML)
public class HtmlDateTimeZoneAndTransitionWriter extends DateTimeZoneAndTransitionWriter {
    
    /**
     * String containing the value for {@literal "text/html;charset=utf-8"}.
     */
    static final String TEXT_HTML_UTF8 = MediaType.TEXT_HTML + CHARSET_UTF_8;
    
    /**
     * {@link MediaType} for {@link #TEXT_HTML_UTF8}.
     */
    private static final MediaType TEXT_HTML_UTF8_TYPE = MediaType.valueOf(TEXT_HTML_UTF8);
    
    private static final DateTimeFormatter VERBOSE_DATE_FORMAT = new DateTimeFormatterBuilder()
        .appendDayOfMonth(1)
        .appendLiteral(' ')
        .appendMonthOfYearText()
        .appendLiteral(' ')
        .appendYear(4, 4)
        .toFormatter();
    
    private static final String TEMPLATE =
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
        "  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n" + 
        "<head>\n" + 
        "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" + 
        "  <title>{0}</title>\n" + 
        "</head>\n" + 
        "<body>\n" + 
        "  <h1 title=\"{1}\">{0}</h1>\n" + 
        "  <p>{2} &rarr; {3}</p>\n" + 
        "</body>\n" + 
        "</html>";
    
    /**
     * {@inheritDoc}
     */
    @Override
    MediaType getMediaType() {
        return TEXT_HTML_UTF8_TYPE;
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
        
        DateTimeZone dateTimeZone = entity.getZone();
        DateTime currentTransition = entity.getCurrentTransition();
        DateTime previousTransition = entity.getPreviousTransition();
        
        LocalTime time = currentTransition.toLocalTime();
        Period previousOffset = Period.millis(dateTimeZone.getOffset(previousTransition)).normalizedStandard();
        Period currentOffset = Period.millis(dateTimeZone.getOffset(currentTransition)).normalizedStandard();
        Period delta = currentOffset.minus(previousOffset);
        
        DateTimeFormatter verboseFormat = VERBOSE_DATE_FORMAT.withLocale(this.getLanguage());

        
        String message = MessageFormat.format(TEMPLATE,
                currentTransition.toString(verboseFormat),
                currentTransition.toString(Constants.DATE_FORMAT),
                time.minus(delta).toString(Constants.TIME_FORMAT),
                time.toString(Constants.TIME_FORMAT));
        Writer writer = new OutputStreamWriter(entityStream, "UTF-8");
        writer.write(message);
        writer.flush();
    }

}

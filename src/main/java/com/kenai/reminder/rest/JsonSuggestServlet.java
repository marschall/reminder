package com.kenai.reminder.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTimeZone;

import com.kenai.reminder.TimeZoneUtil;

/**
 * This servlet renders time zone suggestions in JSON.
 * 
 * @author Philippe Marschall
 */
public final class JsonSuggestServlet extends HttpServlet {
    
    private static final long serialVersionUID = -4893615204349883986L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON);
        String contextPath = request.getContextPath();
        long now = System.currentTimeMillis();
        
        PrintWriter writer = response.getWriter();
        writer.write('[');
        boolean first = true;
        for (DateTimeZone each : TimeZoneUtil.getAllTimeZones()) {
            if (!first) {
                writer.write(',');
            } else {
                first = false;
            }
            writer.write('{');
            writePropery("id", each.getID(), writer);
            writer.write(',');
            writePropery("name", each.getName(now, Locale.US), writer);
            writer.write(',');
            writePropery("atomUrl", TimeZoneUtil.feedUrlFor(each, contextPath), writer);
            writer.write(',');
            writePropery("icalUrl", TimeZoneUtil.calendarUrlFor(each, contextPath), writer);
            writer.write('}');
        }
        writer.write(']');
    }
    
    private static void writePropery(String key, String value, PrintWriter writer) {
        writer.write('"');
        writer.write(key);
        writer.write("\":\"");
        writer.write(value);
        writer.write('"');
    }
    

}

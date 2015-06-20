package com.kenai.reminder.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTimeZone;

import com.kenai.reminder.TimeZoneUtil;

/**
 * This servlet renders time zone suggestions for the IPhone.
 * 
 * @author Philippe Marschall
 */
public class IPhoneTimeZoneSuggestServlet extends HttpServlet {
    
    private static final long serialVersionUID = 2439793482567107675L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {
        this.sendResponse(request, response);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.sendResponse(request, response);
    }

    private void sendResponse(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String input = request.getParameter("timeZone");
        if (input != null) {
            Iterator<DateTimeZone> choices = TimeZoneUtil.getChoices(input, 12);
            PrintWriter output = response.getWriter();
            output.write("<ul title=\"Possible Time Zones\">");
            while (choices.hasNext()) {
                DateTimeZone zone = choices.next();
                output.write("<li><a target=\"_self\" href=\"");
                output.write(TimeZoneUtil.feedUrlFor(zone, request.getContextPath()));
                output.write("\">");
                output.write(zone.getID());
                output.write("</a></li>");
            }
            output.write("</ul>");
        }
    }

}

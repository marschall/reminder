package com.kenai.reminder.rest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class IPhoneFilter implements Filter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (this.isEmpty(request) && this.isFromIPhone(request)) {
            RequestDispatcher dispatcher = req.getRequestDispatcher("iphone.html");
            dispatcher.forward(req, resp);
        } else {
            chain.doFilter(req, resp);
        } 
    }
    
    private boolean isEmpty(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String relativePath = requestURI.substring(contextPath.length());
        return relativePath.isEmpty() || relativePath.equals("/");
    }
    
    private boolean isFromIPhone(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null
            && userAgent.contains("iPhone");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

}

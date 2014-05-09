package com.talient.football.servlets;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import com.talient.football.jdbc.JDBCHomes;

public class PropertiesContextListener implements ServletContextListener {
    public void contextDestroyed(ServletContextEvent e) {
        final ServletContext context = e.getServletContext();

        context.log("husker -- contextDestroyed()");
    }

    public void contextInitialized(ServletContextEvent e) {
        final ServletContext context = e.getServletContext();

        for (Enumeration ee = context.getInitParameterNames();
             ee.hasMoreElements(); ) {
          final String name = (String)ee.nextElement();
          final String value = context.getInitParameter(name);
          context.log("Parameter name=" + name + " value=" + value);
        }

        for (Enumeration ee = context.getAttributeNames();
             ee.hasMoreElements(); ) {
          final String name = (String)ee.nextElement();
          final Object value = context.getAttribute(name);
          context.log("Attribute name=" + name + " value=" + value);
        }

        context.log("Context name:" + context.getServletContextName());
        context.log("Context path:" + context.getRealPath("/SignIn.jsp"));

        final String filename = context.getInitParameter(PROPERTY_FILENAME);

        if (filename == null || filename.length() == 0) {
            context.log(PROPERTY_FILENAME + " property not set in context");
//            throw new ServletException(PROPERTY_FILENAME +
//                " property not set in servlet context");
        } else {
          com.talient.util.Properties.setFilename(filename);
        }

        JDBCHomes.setHomes();

        context.log("husker -- contextInitialized()");
    }

    private static final String PROPERTY_FILENAME = "util.property.filename";
}

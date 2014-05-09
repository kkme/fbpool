package com.talient.football.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.talient.football.jdbc.JDBCHomes;


public final class Init extends HttpServlet {


    public void init() throws ServletException {
        final ServletConfig config = getServletConfig();

        final ServletContext context = config.getServletContext();

        for (Enumeration e = config.getInitParameterNames();
             e.hasMoreElements(); ) {
          final String name = (String)e.nextElement();
          final String value = config.getInitParameter(name);
          context.log("Init.Config name=" + name + " value=" + value);
        }

        for (Enumeration e = context.getInitParameterNames();
             e.hasMoreElements(); ) {
          final String name = (String)e.nextElement();
          final String value = context.getInitParameter(name);
          context.log("Init.Context name=" + name + " value=" + value);
        }

        String filename =
            getServletConfig().
            getServletContext().
            getInitParameter("util.property.filename");

        if (filename == null || filename.length() == 0) {
            throw new ServletException("util.property.filename not set in servlet init parameters ");
        }
        com.talient.util.Properties.setFilename(filename);
        JDBCHomes.setHomes();
    }

    /**
     * Respond to a GET request for the content produced by
     * this servlet.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are producing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
      throws IOException, ServletException {
          // Do nothing
    }
}

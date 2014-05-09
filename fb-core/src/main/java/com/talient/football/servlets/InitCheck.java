package com.talient.football.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public final class InitCheck extends HttpServlet {


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

    ServletContext context = request.getSession().getServletContext();

	response.setContentType("text/html");
	PrintWriter writer = response.getWriter();

	writer.println("<html>");
	writer.println("<head>");
	writer.println("<title>Initialize football pool check</title>");
	writer.println("</head>");
	writer.println("<body bgcolor=white>");

	writer.println("<h1>Init Application Servlet Checker</h1>");

	writer.println("<table border=\"0\" width=\"100%\">");
    writer.println("<tr>");
    writer.println("  <td><pre>");
    com.talient.util.Properties.list(writer);
    writer.println(" </pre> </td>");
    writer.println("</tr>");
	writer.println("</table>");

	writer.println("</body>");
	writer.println("</html>");

    }


}

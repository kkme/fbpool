// CVS ID: $Id: PublishYearToDate.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.football.publish;

import java.io.PrintWriter;
import java.io.FileWriter; 
import java.io.File; 
import java.io.IOException;
import java.text.DecimalFormat;
import java.net.URL;

import com.talient.util.Properties;
import com.talient.util.MissingPropertyException;
import com.talient.util.MissingResourceException;
import com.talient.util.XsltProcess;
import com.talient.football.reports.YearToDate;

/**
 * @author Steve Schmadeke
 * @version $Revision: 1.1.1.1 $
 */
public class PublishYearToDate {

    private static final String resource =
            "com/talient/football/view/html/xslt/YearToDate.xslt";

    private PublishYearToDate() {}

    public static final String publish(int year, int week)
       throws IOException, MissingPropertyException,
                Exception, MissingResourceException {

        final String filename = getFilename(year, week);
        URL xsl = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (xsl == null) {
            throw new MissingResourceException("Missing resource: " + resource);
        }
        XsltProcess process = new XsltProcess(xsl);

        YearToDate ytd = new YearToDate(year, week);

        try {
            process.transform(ytd.getJDOM(), new File(filename));
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return filename;
    }

    public static final String publish(YearToDate ytd)
       throws IOException, MissingPropertyException,
                Exception, MissingResourceException {

        final String filename = getFilename(ytd.getYear(), ytd.getWeek());
        URL xsl = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (xsl == null) {
            throw new MissingResourceException("Missing resource: " + resource);
        }
        XsltProcess process = new XsltProcess(xsl);

        try {
            process.transform(ytd.getJDOM(), new File(filename));
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return filename;
    }

    static private String getFilename(int year, int week)
            throws MissingPropertyException {

        final String root =
            Properties.getProperty("football.pool.DocumentRoot");

        if (root == null) {
            throw new MissingPropertyException(
                "The football.pool.DocumentRoot property has not been set");
        }
        String seperator = "/";
        if (root.endsWith("/")) {
            seperator = "";
        }

        // The filename should look like
        //   "<DocumentRoot>/2002/ytd0201.htm".
        // If week is 0 then filename should look like this
        //   "<DocumentRoot>/2002/ytd2002.htm".
        File base = new File(root + seperator + year);
        if (! base.isDirectory()) {
            base.mkdirs();
        }
        String filename;
        if (week > 0) {
            filename =
                root +
                seperator +
                year +
                "/ytd" +
                decimalFormat.format(year % 100) +
                decimalFormat.format(week) +
                ".htm";
        }
        else {
            filename =
                root +
                seperator +
                year +
                "/ytd" +
                year +
                ".htm";
        }
        return filename;
    }

    private static final DecimalFormat decimalFormat =
        new DecimalFormat("00");

    static public void main(String argv[]) {
        com.talient.football.jdbc.JDBCHomes.setHomes();
        try {
            // YearToDate ytd = new YearToDate(2002, 5);
            String fn = PublishYearToDate.publish(2002, 11);
            System.out.println(fn);
            // fn = PublishYearToDate.publish(2002, 0);
            // System.out.println(fn);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

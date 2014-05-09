// CVS ID: $Id: PublishSeason.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

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
import com.talient.football.reports.Season;
import com.talient.football.reports.Crosstable;

/**
 * @author Steve Schmadeke
 * @version $Revision: 1.1.1.1 $
 */
public class PublishSeason {

    private PublishSeason() {}

    public static final String publish(int year)
       throws IOException, MissingPropertyException,
                           MissingResourceException {

        final String root =
            Properties.getProperty("football.pool.DocumentRoot");

        if (root == null) {
            throw new MissingPropertyException(
                "The football.pool.DocumentRoot property has not been set");
        }

        // The filename should look like
        //   "<DocumentRoot>/2002/weeklystats0201.htm".
        File base = new File(root + "/" + year);
        if (! base.isDirectory()) {
            base.mkdirs();
        }
        final String filename =
            root +
            "/" +
            year +
            "/index.htm";
        String resource =
            "com/talient/football/view/html/xslt/Season.xslt";
        URL xsl = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (xsl == null) {
            throw new MissingResourceException("Missing resource: " + resource);
        }
        XsltProcess process = new XsltProcess(xsl);

        Season season = Season.getHome().findByYear(year);

        org.jdom.Document doc = season.getJDOM();

        process.transform(doc, new File(filename));

        return filename;
    }

    private static final DecimalFormat decimalFormat =
        new DecimalFormat("00");

    static public void main(String argv[]) {
        com.talient.football.jdbc.JDBCHomes.setHomes();
        try {
            String fn = PublishSeason.publish(2002);
            System.out.println(fn);
        }
        catch (Exception e) {
            System.out.println("Exception from publish: " + e.toString());
            e.printStackTrace();
        }
    }
}

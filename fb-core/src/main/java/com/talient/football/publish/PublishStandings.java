//
// CVS ID: $Id: PublishStandings.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $
//

package com.talient.football.publish;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import com.talient.util.Properties;
import com.talient.util.MissingPropertyException;
import com.talient.football.reports.YearToDate;
import com.talient.football.view.text.TextYearToDate;

public class PublishStandings {

    private PublishStandings() {}

    public static final String publish(int year, int week)
        throws IOException, MissingPropertyException {
            
        final String root =
            Properties.getProperty("football.pool.DocumentRoot");
            
        if (root == null) {
            throw new MissingPropertyException(
                "The football.pool.DocumentRoot property has not been set");
        }

        // The filename should look like
        // "<DocumentRoot>/2002/standings0201.txt".
        File base = new File(root + "/" + year);
        if (! base.isDirectory()) {
            base.mkdirs();
        }
        final String filename =
            root +
            "/" +
            year +
            "/standings" +
            decimalFormat.format(year % 100) +
            decimalFormat.format(week) +
            ".txt";

        final PrintWriter pw = new PrintWriter(new FileWriter(filename));

        final TextYearToDate tytd =
            new TextYearToDate(new YearToDate(year, week));
        pw.print(tytd.formatStandings());
        pw.print(tytd.formatStandingsAlphabetically());

        pw.flush();
        pw.close();

        return filename;
    }

    private static final DecimalFormat decimalFormat =
        new DecimalFormat("00");
}

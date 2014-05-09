//
// CVS ID: $Id: PublishRecap.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $
//

package com.talient.football.publish;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import com.talient.util.Properties;
import com.talient.util.MissingPropertyException;
import com.talient.football.reports.WeeklyStats;
import com.talient.football.reports.Crosstable;
import com.talient.football.view.text.TextRecap;

public class PublishRecap {

    private PublishRecap() {}

    public static final String publish(int year, int week)
        throws IOException, MissingPropertyException {
            
        final String root =
            Properties.getProperty("football.pool.DocumentRoot");
            
        if (root == null) {
            throw new MissingPropertyException(
                "The football.pool.DocumentRoot property has not been set");
        }

        // The filename should look like
        // "<DocumentRoot>/2002/recap.txt".
        File base = new File(root + "/" + year);
        base.mkdirs();
        final String filename =
            root +
            "/" +
            year +
            "/recap" +
            decimalFormat.format(year % 100) +
            decimalFormat.format(week) +
            ".txt";

        final PrintWriter pw = new PrintWriter(new FileWriter(filename));

        final TextRecap tr = new TextRecap(new WeeklyStats(
          Crosstable.getHome().findByYearWeek(year, week)));

        pw.print(tr.formatTitle());
        pw.print(tr.formatTeams());
        pw.print(tr.formatEntries());
        pw.print(tr.formatWeights());
        pw.print(tr.formatFavorites());
        pw.print(tr.formatConsensus());
        pw.print(tr.formatChoicesAgainstConsensus());

        pw.flush();
        pw.close();

        return filename;
    }

    private static final DecimalFormat decimalFormat =
        new DecimalFormat("00");
}

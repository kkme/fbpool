//
// CVS ID: $Id: EmailStandings.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $
//

package com.talient.football.util.email;

import com.talient.util.MissingPropertyException;
import com.talient.football.jdbc.JDBCHomes;
import com.talient.football.reports.YearToDate;
import com.talient.football.view.text.TextYearToDate;

public class EmailStandings {

    private EmailStandings() {};

    private static final String className =
        new EmailStandings().getClass().getName();

    public static void main(String[] args) {

        int year = 0;
        int week = 0;

        final String usage = "Usage: " + className + " <year> <week>";

        if (args.length != 2) {
            System.err.println(usage);
            System.exit(1);
        }

        try {
            year = Integer.parseInt(args[0]);
            week = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println(usage);
            System.exit(1);
        }

        if (year < 1986 || year > 2050) {
            System.err.println(usage);
            System.exit(1);
        }

        JDBCHomes.setHomes();

        TextYearToDate tytd = new TextYearToDate(new YearToDate(year, week));
        try {
            System.out.print(tytd.formatHeader());
            System.out.print(tytd.formatStandings());
        } catch (MissingPropertyException e) {
            System.err.println(e.toString());
        }
        System.out.print(tytd.formatStandingsAlphabetically());

        System.out.flush();
    }
}

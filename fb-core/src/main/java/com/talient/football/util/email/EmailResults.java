// CVS ID: $Id: EmailResults.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.football.util.email;

import com.talient.util.MissingPropertyException;
import com.talient.football.entities.WeeklySchedule;
import com.talient.football.reports.WeeklyStats;
import com.talient.football.reports.Crosstable;
import com.talient.football.view.text.TextResults;
import com.talient.football.jdbc.JDBCHomes;

/**
 * <p>
 * @author Steve Schmadeke
 * @version $Revision: 1.1.1.1 $
 */
public class EmailResults {

    private EmailResults() {};

    public static void main(String[] args) {
        String usage =
            "Usage: " +
            (new EmailResults()).getClass().getName() + 
            "<year> <week>";

        if (args.length != 2) {
            System.err.println(usage);
            System.exit(1);
        }

        int year = 0;
        int week = 0;

        try {
            year = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid year:" + args[0]);
            System.err.println(usage);
            System.exit(1);
        }

        try {
            week = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid week:" + args[1]);
            System.err.println(usage);
            System.exit(1);
        }

        JDBCHomes.setHomes();

        final WeeklySchedule weeklySchedule =
            WeeklySchedule.getHome().findByYearWeek(year, week);

        final TextResults tr =
            new TextResults(
                new WeeklyStats(
                    Crosstable.getHome().findByYearWeek(year, week)));

        try {
            System.out.print(tr.format());
        } catch (MissingPropertyException e) {
            System.err.println(e.toString());
        }
    }
}

// CVS ID: $Id: EmailEntry.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.football.util.email;

import com.talient.util.MissingPropertyException;
import com.talient.football.jdbc.JDBCHomes;
import com.talient.football.entities.WeeklySchedule;
import com.talient.football.view.text.TextEntry;

/**
 * <p>
 * @author Steve Schmadeke
 * @version $Revision: 1.1.1.1 $
 */
public class EmailEntry {

    private EmailEntry() {};

    public static void main(String[] args) {
        final String usage =
            "Usage: " + 
            (new EmailEntry()).getClass().getName() +
            " <year> <week>";

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

        final TextEntry ee = new TextEntry(weeklySchedule);

        try {
            System.out.print(ee.formatHeader());
            System.out.print(ee.formatEntry());
        } catch (MissingPropertyException e) {
            System.err.println(e.toString());
        }
        System.out.flush();
    }
}

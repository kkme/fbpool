// CVS ID: $Id: EmailRecap.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.football.util.email;

import java.lang.String;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import com.talient.util.MissingPropertyException;
import com.talient.football.jdbc.JDBCHomes;
import com.talient.football.reports.WeeklyStats;
import com.talient.football.reports.Crosstable;
import com.talient.football.view.text.TextRecap;

/**
 * <p>
 * @author Steve Schmadeke
 * @version $Revision: 1.1.1.1 $
 */
public class EmailRecap {

    private EmailRecap() {};

    public static void main(String[] args) {

        JDBCHomes.setHomes();

        int year = 0;
        int week = 0;

        String usage =
            "Usage: " +
            (new EmailRecap()).getClass().getName() +
            " <year> <week>";

        if (args.length < 2) {
            System.err.println(usage);
            System.exit(1);
        }

        // Get the year and week
        try {
            year = Integer.parseInt(args[0]);
            week = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            System.err.println(usage);
            System.exit(1);
        }

        if (year < 1996 || year > 2050) {
            System.err.println(usage);
            System.exit(1);
        }

        TextRecap tr = new TextRecap(new WeeklyStats(
          Crosstable.getHome().findByYearWeek(year, week)));

        try {
            System.out.print(tr.format());
        } catch (MissingPropertyException e) {
            System.err.println(e.toString());
        }
        System.out.flush();
    }
}

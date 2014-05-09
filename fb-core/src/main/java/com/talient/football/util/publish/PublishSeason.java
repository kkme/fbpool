// CVS ID: $Id: PublishSeason.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.football.util.publish;

import java.io.IOException;

import com.talient.util.MissingPropertyException;
import com.talient.football.jdbc.JDBCHomes;
import com.talient.football.publish.PublishCrosstable;
import com.talient.football.publish.PublishWeeklyStats;
import com.talient.football.publish.PublishYearToDate;
import com.talient.football.publish.PublishRecap;
import com.talient.football.publish.PublishResults;
import com.talient.football.publish.PublishStandings;
import com.talient.football.reports.Season;

/**
 * @author Steve Schmadeke
 * @version $Revision: 1.1.1.1 $
 */
public class PublishSeason {

    private PublishSeason() {}

    public static void main(String[] args) {

        JDBCHomes.setHomes();

        final String usage =
            "Usage: " +
            (new PublishSeason()).getClass().getName() +
            " <year> [all]";

        if (args.length < 1 || args.length > 2) {
            System.err.println(usage);
            System.exit(1);
        }
        if (args.length == 2 && ! args[1].equals("all")) {
            System.err.println(usage);
            System.exit(1);
        }

        int year = 0;

        try {
            year = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid year:" + args[0]);
            System.err.println(usage);
            System.exit(1);
        }

        try {
            System.out.println("Created " + 
                    com.talient.football.publish.PublishSeason.publish(year));
            if (args.length == 2 && args[1].equals("all")) {
                Season season = Season.getHome().findByYear(year);
                for (int i=1; i<=season.getLastCompleteWeek(); i++) {
                    System.out.println("Created " +
                                       PublishCrosstable.publish(year, i));
                    System.out.println("Created " +
                                       PublishWeeklyStats.publish(year, i));
                    System.out.println("Created " +
                                       PublishYearToDate.publish(year, i));
                    System.out.println("Created " +
                                       PublishRecap.publish(year, i));
                    System.out.println("Created " +
                                       PublishResults.publish(year, i));
                    System.out.println("Created " +
                                       PublishStandings.publish(year, i));
                }
                if (season.getWeekInProgress() > 0) {
                    int week = season.getWeekInProgress();
                    System.out.println("Created " +
                                       PublishCrosstable.publish(year, week));
                    System.out.println("Created " +
                                       PublishWeeklyStats.publish(year, week));
                    System.out.println("Created " +
                                       PublishRecap.publish(year, week));
                    System.out.println("Created " +
                                       PublishResults.publish(year, week));
                }
                System.out.println("Created " +
                                   PublishYearToDate.publish(year, 0));
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.toString());
            System.exit(1);
        }
    }
}

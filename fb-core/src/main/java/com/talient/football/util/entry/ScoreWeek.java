// CVS ID: @(#) $Id: ScoreWeek.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.football.util.entry;

import java.lang.String;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;

import com.talient.football.entities.Entry;
import com.talient.football.entities.WeeklyResult;
import com.talient.football.reports.Crosstable;
import com.talient.football.reports.WeeklyStats;
import com.talient.football.jdbc.JDBCEntry;
import com.talient.football.jdbc.JDBCWeeklyResult;
import com.talient.football.jdbc.JDBCHomes;

/**
 * <p>
 * @author Steve Schmadeke
 * @version $Version: $
 */
public class ScoreWeek {

    private ScoreWeek() {};

    public ScoreWeek(int year, int week) 
        throws Exception {
        Crosstable crosstable = Crosstable.getHome().findByYearWeek(year, week);
        if (crosstable.size() == 0) {
            throw new Exception("No entries found for week " + week +
                                ", " + year);
        }

        Entry consensus = crosstable.addConsensus();
        crosstable.setBonus();
        JDBCEntry.store(consensus);

        WeeklyStats weeklyStats = new WeeklyStats(crosstable);

        Collection results = crosstable.getResults();
        Iterator iter = results.iterator();
        while (iter.hasNext()) {
            WeeklyResult result = (WeeklyResult)iter.next();
            result.setWins(weeklyStats.getWins(result.getEntrant()));
            result.setLosses(weeklyStats.getLosses(result.getEntrant()));
            result.setTies(weeklyStats.getTies(result.getEntrant()));
            JDBCWeeklyResult.store(result);
        }
    }

    public static void main(String[] args) {

        JDBCHomes.setHomes();

        int year = 0;
        int week = 0;

        String usage = "Usage: com.talient.football.util.entry.ScoreWeek " +
            "<year> <week> [persist]";

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

        try {
            ScoreWeek sw = new ScoreWeek(year, week);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        
        System.out.println("Persisted Concensus entry and weekly results for " +
            year + " week " + week);
        System.exit(0);
    }
}

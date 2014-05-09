// CVS ID: @(#) $Id: TextYearToDate.java,v 1.2 2008-11-16 22:58:27 husker Exp $

package com.talient.football.view.text;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.text.NumberFormat;

import com.talient.util.Text;
import com.talient.util.Properties;
import com.talient.util.MissingPropertyException;
import com.talient.football.entities.Entrant;
import com.talient.football.entities.WeeklySchedule;
import com.talient.football.reports.YearToDate;

public class TextYearToDate {

/*
This section of code will be moved into the YearToDate class
shortly.  It is here only to avoid merge conflicts with code
changes being made by Nick.
*/

    public static final class EntrantYearToDate {

        public EntrantYearToDate(Entrant entrant) {
            this.entrant = entrant;
        }

        public EntrantYearToDate(
                    Entrant entrant,
                    int rank,
                    int totalPoints,
                    int lowestWeek,
                    int secondLowestWeek,
                    int wins,
                    int losses,
                    int ties,
                    int weeklyWins,
                    int freeEntries,
                    int weeksPlayed) {
            this.entrant = entrant;
            this.rank = rank;
            this.totalPoints = totalPoints;
            this.lowestWeek = lowestWeek;
            this.secondLowestWeek = secondLowestWeek;
            this.wins = wins;
            this.losses = losses;
            this.ties = ties;
            this.weeklyWins = weeklyWins;
            this.freeEntries = freeEntries;
            this.weeksPlayed = weeksPlayed;
        }

        public Entrant getEntrant() {
            return entrant;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public int getTotalPoints() {
            return totalPoints;
        }

        public void setTotalPoints(int totalPoints) {
            this.totalPoints = totalPoints;
        }

        public int getLowestWeek() {
            return lowestWeek;
        }

        public void setLowestWeek(int lowestWeek) {
            this.lowestWeek = lowestWeek;
        }

        public int getSecondLowestWeek() {
            return secondLowestWeek;
        }

        public void setSecondLowestWeek(int secondLowestWeek) {
            this.secondLowestWeek = secondLowestWeek;
        }

        public int getWins() {
            return wins;
        }

        public void setWins(int wins) {
            this.wins = wins;
        }

        public int getLosses() {
            return losses;
        }

        public void setLosses(int losses) {
            this.losses = losses;
        }

        public int getTies() {
            return ties;
        }

        public void setTies(int ties) {
            this.ties = ties;
        }

        public int getWeeklyWins() {
            return weeklyWins;
        }

        public void setWeeklyWins(int weeklyWins) {
            this.weeklyWins = weeklyWins;
        }

        public int getFreeEntries() {
            return freeEntries;
        }

        public void setFreeEntries(int freeEntries) {
            this.freeEntries = freeEntries;
        }

        public int getWeeksPlayed() {
            return weeksPlayed;
        }

        public void setWeeksPlayed(int weeksPlayed) {
            this.weeksPlayed = weeksPlayed;
        }

        public String toString() {
            StringBuffer str = new StringBuffer();

            str.append(getClass().getName());
            str.append("[");
            str.append("entrant="          +getEntrant().getUsername());
            str.append(",rank="            +getRank());
            str.append(",totalPoints="     +getTotalPoints());
            str.append(",lowestWeek="      +getLowestWeek());
            str.append(",secondLowestWeek="+getSecondLowestWeek());
            str.append(",wins="            +getWins());
            str.append(",losses="          +getLosses());
            str.append(",ties="            +getTies());
            str.append(",weeklyWins="      +getWeeklyWins());
            str.append(",freeEntries="     +getFreeEntries());
            str.append(",weeksPlayed="     +getWeeksPlayed());
            str.append("]");

            return str.toString();
        }

        private final Entrant entrant;
        private int rank = 0;
        private int totalPoints = 0;
        private int lowestWeek = 0;
        private int secondLowestWeek = 0;
        private int wins = 0;
        private int losses = 0;
        private int ties = 0;
        private int weeklyWins = 0;
        private int freeEntries = 0;
        private int weeksPlayed = 0;
    }

    // List of Entrant YTD statistics, sorted by ranking.
    private final List entrantStats = new ArrayList();

    // Same List of Entrant YTD statistics, just sorted by Username.
    private List usernameStats = null;

    private void yearToDateCalculate() {

        int rank = 1;

        for (Iterator i = yearToDate.getStandings().iterator();
             i.hasNext(); ) {
            Entrant entrant = (Entrant)i.next();
            entrantStats.add(
                new EntrantYearToDate(
                    entrant,
                    rank++,
                    yearToDate.getTotalPoints(entrant),
                    yearToDate.getLowestWeek(entrant),
                    yearToDate.getSecondLowestWeek(entrant),
                    yearToDate.getWins(entrant),
                    yearToDate.getLosses(entrant),
                    yearToDate.getTies(entrant),
                    yearToDate.getWeeklyWins(entrant),
                    yearToDate.getFreeEntries(entrant),
                    yearToDate.getWeeksPlayed(entrant)));
        }

        usernameStats = new ArrayList(entrantStats);
        Collections.sort(usernameStats, usernameComparator);
    }

    private static final Comparator usernameComparator = new Comparator() {
        public int compare(Object a, Object b) {

            EntrantYearToDate ea = (EntrantYearToDate)a;
            EntrantYearToDate eb = (EntrantYearToDate)b;

            return ea.getEntrant().getUsername().compareTo(
                       eb.getEntrant().getUsername());

/*
            EntrantYearToDate ea = (EntrantYearToDate)a;
            EntrantYearToDate eb = (EntrantYearToDate)b;

            int totPtsa = getTotalPoints(ea);
            int totPtsb = getTotalPoints(eb);
            int netPtsa = getTotalPoints(ea) -
                          getLowestWeek(ea) -
                          getSecondLowestWeek(ea);
            int netPtsb = getTotalPoints(eb) -
                          getLowestWeek(eb) -
                          getSecondLowestWeek(eb);

            if (netPtsa == netPtsb) {
                return totPtsb - totPtsa;
            }
            else {
                return netPtsb - netPtsa;
            }
*/
        }
    };


/*
End code to be moved to YearToDate.
*/

    private final YearToDate yearToDate;
    private final NumberFormat pointsFormat;
    private final WeeklySchedule schedule;

    private final String poolName =
        Properties.getProperty("football.pool.name");

    private final String poolEmail =
        Properties.getProperty("football.pool.email");

    public TextYearToDate(YearToDate yearToDate) {
        this.yearToDate = yearToDate;

        schedule =
            WeeklySchedule.getHome().findByYearWeek(yearToDate.getYear(),
                                                    yearToDate.getWeek());

        pointsFormat = schedule.getNumberFormat();

        calculate();
    }

    public String formatHeader() throws MissingPropertyException {

        if (poolName == null) {
            throw new MissingPropertyException(
              "The football.pool.name property has not been set");
        }

        if (poolEmail == null) {
            throw new MissingPropertyException(
              "The football.pool.email property has not been set");
        }

        StringBuffer b = new StringBuffer();

        b.append("From: ");
        b.append(poolName);
        b.append(" <");
        b.append(poolEmail);
        b.append(">\n");
        b.append("Subject: ");
        b.append(poolName);
        if (schedule.isFinal()) {
            b.append(" Final");
        }
        b.append(" Standings after ");
        b.append(schedule.getLabel());
        b.append("\n");

        return b.toString();
    }

    public String formatStandings() throws MissingPropertyException {

        if (poolName == null) {
            throw new MissingPropertyException(
              "The football.pool.name property has not been set");
        }

        final int rankWidth =
            String.valueOf(entrantStats.size()).length();
        final int nameWidth = 20;
        final int playedWidth = 2;

        final int hgpWidth =
            pointsFormat.format(yearToDate.getHighestGrossPoints()).length();

        final int hlwWidth =
            pointsFormat.format(yearToDate.getHighestLowestWeek()).length();

        final int hslwWidth =
            pointsFormat.format(
                yearToDate.getHighestSecondLowestWeek()).length();

        final EntrantYearToDate first =
            (EntrantYearToDate)entrantStats.get(0);

        final int hapWidth =
            pointsFormat.format(
                first.getTotalPoints() -
                first.getLowestWeek() -
                first.getSecondLowestWeek()
            ).length();

        StringBuffer b = new StringBuffer();

        b.append("\n");
        b.append(yearToDate.getYear());
        b.append(" ");
        b.append(poolName);
        if (schedule.isFinal()) {
            b.append(" Final");
        }
        b.append(" Standings after ");
        b.append(schedule.getLabel());
        b.append("\n");

        b.append("\nOVERALL STANDINGS");
        b.append("\n-----------------\n\n");

        for (Iterator i = entrantStats.iterator(); i.hasNext(); ) {

            EntrantYearToDate e = (EntrantYearToDate) i.next();

            b.append(Text.padRight(
                String.valueOf(e.getRank()), rankWidth));
            b.append("  ");
            b.append(Text.padLeft(
                e.getEntrant().getUsername(), nameWidth, true));
            b.append(" ");
            b.append(Text.padRight(
                pointsFormat.format(e.getTotalPoints()), hgpWidth));
            b.append(" - ");
            b.append(Text.padRight(
                pointsFormat.format(e.getLowestWeek()), hlwWidth));
            b.append(" - ");
            b.append(Text.padRight(
                pointsFormat.format(e.getSecondLowestWeek()), hslwWidth));
            b.append(" = ");
            b.append(Text.padRight(
                pointsFormat.format(e.getTotalPoints() -
                                    e.getLowestWeek() -
                                    e.getSecondLowestWeek()), hapWidth));
            b.append("\n");
        }

        return b.toString();
    }

    public String formatStandingsAlphabetically() {

        final int rankWidth =
            String.valueOf(entrantStats.size()).length();

        final int nameWidth = 20;

        final List ranks = new ArrayList(usernameStats.size());

        for (Iterator i = usernameStats.iterator(); i.hasNext(); ) {
            EntrantYearToDate entrantYTD = (EntrantYearToDate) i.next();
            ranks.add(
                Text.padRight(
                    String.valueOf(entrantYTD.getRank()), rankWidth) + " " +
                Text.padLeft(
                    entrantYTD.getEntrant().getUsername(), nameWidth, true));
        }

        StringBuffer b = new StringBuffer();

        b.append("\nRANK BY NAME");
        b.append("\n------------\n\n");

        for (Iterator i = Text.makeColumns(ranks, 3).iterator();
             i.hasNext(); ) {
            b.append(i.next()).append("\n");
        }

        return b.toString();
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(getClass().getName());
        str.append("[");
        str.append("YearToDate="+yearToDate);
        str.append("]");

        return str.toString();
    }

    private void calculate() {
        yearToDateCalculate();
    }

    public static void main(String argv[]) {
        YearToDate.setHome(new com.talient.football.jdbc.JDBCWeeklyResult());
        TextYearToDate tytd = new TextYearToDate(new YearToDate(2001, 2));
        try {
            System.out.println(tytd.formatHeader());
            System.out.println(tytd.formatStandings());
        } catch (MissingPropertyException e) {
            System.err.println(e.toString());
        }
        System.out.println(tytd.formatStandingsAlphabetically());
    }
}

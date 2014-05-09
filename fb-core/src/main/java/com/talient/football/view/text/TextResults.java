// CVS ID: @(#) $Id: TextResults.java,v 1.2 2008-11-16 22:58:27 husker Exp $

package com.talient.football.view.text;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.text.NumberFormat;

import com.talient.util.Text;
import com.talient.util.Properties;
import com.talient.util.MissingPropertyException;
import com.talient.football.entities.TeamResult;
import com.talient.football.entities.Entrant;
import com.talient.football.entities.WeeklySchedule;
import com.talient.football.entities.Entry;
import com.talient.football.entities.WeeklyResult;
import com.talient.football.reports.Crosstable;
import com.talient.football.reports.WeeklyStats;

public class TextResults {

    private final WeeklyStats weeklyStats;

    private final String name =
        Properties.getProperty("football.pool.name");

    private final String email =
        Properties.getProperty("football.pool.email");

    public TextResults(WeeklyStats weeklyStats) {
        this.weeklyStats = weeklyStats;
    }

    public String formatHeader() throws MissingPropertyException {

        if (name == null) {
            throw new MissingPropertyException(
                "The football.pool.name property has not been set");
        }

        if (email == null) {
            throw new MissingPropertyException(
                "The football.pool.email property has not been set");
        }

        StringBuffer b = new StringBuffer();

        b.append("From: ");
        b.append(name);
        b.append(" <");
        b.append(email);
        b.append(">\n");
        b.append("Subject: ");
        b.append(name);   
        b.append(" ");
        b.append(weeklyStats.getSchedule().getLabel());
        b.append(" Results\n");

        return b.toString();
    }

    public String formatTitle() throws MissingPropertyException {

        if (name == null) {
            throw new MissingPropertyException(
                "The football.pool.name property has not been set");
        }

        StringBuffer b = new StringBuffer();

        b.append("\n");
        b.append(weeklyStats.getSchedule().getYear());
        b.append(" ");
        b.append(name);   
        b.append(" ");
        b.append(weeklyStats.getSchedule().getLabel());
        b.append(" Results\n");

        return b.toString();
    }

    public String formatResults() {

        final WeeklySchedule schedule = weeklyStats.getSchedule();

        final NumberFormat pointsFormat = schedule.getNumberFormat();
        pointsFormat.setGroupingUsed(false);

        final int perfectWeek = schedule.getTotalPoints();

        final boolean isFinalScore = weeklyStats.getEntry(0).isFinalScore();

        final int rankWidth =
            String.valueOf(weeklyStats.size()).length();
        final int nameWidth = 20;
        final int totalWidth = pointsFormat.format(perfectWeek).length();
        final int winsWidth = 3;
        final int gamesWidth = 3;
        final int pctWidth = 6;

        percentFormat.setMinimumFractionDigits(1);

        final int scoreWidth =
            pointsFormat.format(perfectWeek).length();

        StringBuffer b = new StringBuffer();

        b.append("\nWEEK'S RANKING");
        b.append("\n--------------\n");

        if (weeklyStats.getBonusAmount() > 0) {
            final List winners = weeklyStats.getBonusWinners();
            if (winners.size() > 1) {
                b.append("\nScores for ");
                b.append(((Entrant)winners.get(0)).getUsername());
                for (int i = 1; i < winners.size() - 1; i++) {
                    b.append(", ");
                    b.append(((Entrant)winners.get(i)).getUsername());
                }
                b.append(" and ");
                b.append(((Entrant)winners.get(winners.size()-1)).getUsername());
                b.append(" include\n");
                b.append(pointsFormat.format(weeklyStats.getBonusAmount()));
                b.append(" point bonus for winning week.\n");
            } else if (winners.size() == 1) {
                b.append("\nScore for ");
                b.append(((Entrant)winners.get(0)).getUsername());
                b.append(" includes ");
                b.append(pointsFormat.format(weeklyStats.getBonusAmount()));
                b.append(" point bonus for winning week.\n");
            } else {
                b.append("\nTop score does not reflect possible ");
                b.append(pointsFormat.format(weeklyStats.getBonusAmount()));
                b.append(" point bonus.\n");
            }
        }

        if (!isFinalScore) {
            b.append("\nNot all games are complete.  The maximum possible\n");
            b.append("score for each entry is in parentheses.\n");
        }

        b.append("\n");
        b.append("Capital letters denote a correct pick for that Confidence");
        b.append("\n");
        b.append("Value.  Dashes (-) indicate a incorrect pick.");
        if (!isFinalScore) {
            b.append("  Lower-case\n");
            b.append("letters indicate the game has not been completed.");
        }
        b.append("\n\n");

        final int size = weeklyStats.size();

        for (int i = 0; i < size; i++) {

            final Entry e = weeklyStats.getEntry(i);
            final WeeklyResult r = weeklyStats.getResult(i);

            final int wins =   weeklyStats.getWins(e.getEntrant());
            final int losses = weeklyStats.getLosses(e.getEntrant());

            final double pct =
                ((double)wins)/(wins + losses);

            b.append(Text.padRight(String.valueOf(i+1), rankWidth));
            b.append("  ");
            b.append(
                Text.padLeft(e.getEntrant().getUsername(), nameWidth, true));
            b.append(" ");
            b.append(Text.padRight(
                pointsFormat.format(r.getTotalScore()), totalWidth));
            b.append(" ");
            b.append(Text.padRight(String.valueOf(wins), winsWidth));
            b.append("/");
            b.append(Text.padLeft(String.valueOf(wins + losses), gamesWidth));
            b.append(Text.padRight(percentFormat.format(pct), pctWidth));
            b.append(" ");

            for (int t = 0; t < e.size(); t++) {
                TeamResult result = schedule.getResult(e.get(t));
                if (result == TeamResult.DROPPED) {
                    continue;
                }

                if (result == TeamResult.WON) {
                    b.append(WINNERS_CODES[t]);
                } else if (result == TeamResult.LOST) {
                    b.append(LOSERS_CODES[t]);
                } else if (result == TeamResult.TIED) {
                    b.append(TIED_CODES[t]);
                } else {
                    b.append(UNDETERMINED_CODES[t]);
                }
            }

            if (!isFinalScore) {
                b.append(" (");
                b.append(Text.padRight(
                    pointsFormat.format(e.getMaximumScore()), totalWidth));
                b.append(")");
            }
            b.append("\n");
        }

        return b.toString();
    }

    public String formatWeights() {
        final int[] weights = weeklyStats.getSchedule().getWeights();

        final NumberFormat pointsFormat =
            weeklyStats.getSchedule().getNumberFormat();
        final int weightWidth = pointsFormat.format(weights[0]).length();

        final int codeWidth = 8;

        final StringBuffer b = new StringBuffer();

        b.append("\nCONFIDENCE VALUES");
        b.append("\n-----------------\n\n");

        List strings = new ArrayList(weights.length);
        for (int i = 0; i < weights.length; i++) {
            strings.add(
                Text.padRight("'" + WINNERS_CODES[i] + "' = ", codeWidth) +
                Text.padRight(pointsFormat.format(weights[i]), weightWidth) +
                "      ");
        }   

        for (Iterator i = Text.makeColumns(strings, 3).iterator();
             i.hasNext(); ) {
            b.append(i.next()).append("\n");
        }

        return b.toString();
    }

    public String formatRankByName() {

        final Crosstable crosstable = weeklyStats.getCrosstable();

        final List entries =
            new ArrayList(crosstable.getEntries());

        Collections.sort(entries, usernameComparator);

        final int rankWidth =
            String.valueOf(entries.size()).length();

        final int nameWidth = 20;

        final List ranks = new ArrayList(entries.size());

        for (Iterator i = entries.iterator(); i.hasNext(); ) {
            Entry entry = (Entry) i.next();
            ranks.add(
                Text.padRight(
                    String.valueOf(crosstable.indexOf(entry)+1), rankWidth) +
                " " +
                Text.padLeft(
                    entry.getEntrant().getUsername(), nameWidth, true));
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

    public String format() throws MissingPropertyException {
        return formatHeader() +
               formatTitle() +
               formatResults() +
               formatWeights() +
               formatRankByName();
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(getClass().getName());
        str.append("[");
        str.append("WeeklyStats="+weeklyStats);
        str.append("]");

        return str.toString();
    }

    /**
     * Compare entries by Username of the Entrant.
     */
    private static final Comparator usernameComparator = new Comparator() {
        public int compare(Object a, Object b) {

            Entry ea = (Entry)a;
            Entry eb = (Entry)b;

            return ea.getEntrant().getUsername().compareTo(
                       eb.getEntrant().getUsername());
        }
    };

    private static final NumberFormat percentFormat =
        NumberFormat.getPercentInstance();

    // These four arrays contain the strings used to format the individual
    // game results for each entry.  The formatter will go through each
    // entry, emitting a string from one of the following three arrays
    // depending on the result of that pick.  The index of the string
    // chosen depends on the confidence level of that pick.  For example,
    // for the highest confidence pick, the string will be either "A",
    // "a"a "*" or "-".  Note that there are only 36 strings in these arrays.
    // A weekly schedule consisting of a greater number of games will
    // trigger an ArrayOutOfBounds exception.

    private static final String[] WINNERS_CODES =
        { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
          "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
          "AA", "BB", "CC", "DD", "EE", "FF", "GG", "HH", "II", "JJ"};

    private static final String[] LOSERS_CODES =
        { "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-",
          "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-",
          "--", "--", "--", "--", "--", "--", "--", "--", "--", "--"};

    private static final String[] TIED_CODES =
        { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*",
          "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*",
          "**", "**", "**", "**", "**", "**", "**", "**", "**", "**"};

    private static final String[] UNDETERMINED_CODES =
        { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
          "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
          "aa", "bb", "cc", "dd", "ee", "ff", "gg", "hh", "ii", "jj"};

    public static void main(String argv[]) {
        com.talient.football.jdbc.JDBCHomes.setHomes();

        final TextResults tr = new TextResults(
          new WeeklyStats(
            Crosstable.getHome().findByYearWeek(2001, 2)));

        try {
            System.out.print(tr.format());
        } catch (MissingPropertyException e) {
            System.err.println(e.toString());
        }

        final TextResults tri = new TextResults(
          new WeeklyStats(
            Crosstable.getHome().findByYearWeek(2002, 1)));

        try {
            System.out.print(tri.format());
        } catch (MissingPropertyException e) {
            System.err.println(e.toString());
        }

        final TextResults trb = new TextResults(
          new WeeklyStats(
            Crosstable.getHome().findByYearWeek(2000, 13)));

        try {
            System.out.print(trb.format());
        } catch (MissingPropertyException e) {
            System.err.println(e.toString());
        }
    }
}

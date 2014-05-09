// CVS ID: @(#) $Id: TextRecap.java,v 1.2 2008-11-16 22:58:27 husker Exp $

package com.talient.football.view.text;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.text.NumberFormat;

import com.talient.util.Text;
import com.talient.util.Properties;
import com.talient.util.MissingPropertyException;
import com.talient.football.entities.TeamResult;
import com.talient.football.entities.Team;
import com.talient.football.entities.Game;
import com.talient.football.entities.Entrant;
import com.talient.football.entities.WeeklySchedule;
import com.talient.football.entities.Entry;
import com.talient.football.entities.WeeklyResult;
import com.talient.football.reports.Crosstable;
import com.talient.football.reports.WeeklyStats;

public class TextRecap {

    private final WeeklyStats weeklyStats;
    private final List games;
    private final List teams;

    private final String name =
        Properties.getProperty("football.pool.name");

    private final String email =
        Properties.getProperty("football.pool.email");

    public TextRecap(WeeklyStats weeklyStats) {
        this.weeklyStats = weeklyStats;

        games = weeklyStats.getSchedule().orderedGames();

        teams = new ArrayList(games.size() * 2);
        for (Iterator i = games.iterator(); i.hasNext(); ) {
            final Game game = (Game)i.next();
            teams.add(game.getVisitor());
            teams.add(game.getHome());
        }
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
        b.append(" Recap\n");

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
        b.append(" Recap\n");

        return b.toString();
    }

    public String formatTeams() {

        StringBuffer b = new StringBuffer();

        b.append("\nTEAMS");
        b.append("\n-----\n");
        for (int i = 0; i < games.size(); i++) {  
            Game game = (Game)games.get(i);
            b.append(Text.padRight(String.valueOf(2 * i), 6));
            b.append(" ");
            b.append(Text.padLeft(game.getVisitor().toString(), 25));
            b.append(Text.padRight(String.valueOf(2 * i + 1), 6));
            b.append(" ");
            b.append(Text.padLeft(game.getHome().toString(), 25));
            b.append("\n");
        }   

        return b.toString();
    }

    public String formatEntries() {

        final int MONIKER_LENGTH = 16;

        final List entries = weeklyStats.getSortedEntries();

        final StringBuffer d = new StringBuffer(120);
        d.append("+");
        for (int i = 0; i < MONIKER_LENGTH; i++) { d.append("-"); }
        for (int i = games.size(); i > 0; i--) { d.append("+--"); }
        d.append("+\n");
        final String divider =  d.toString();

        StringBuffer b = new StringBuffer();

        b.append("\n\nENTRIES\n");
        b.append("-------\n");
        for (int i = MONIKER_LENGTH + 1; i > 0; i--) { b.append(" "); }
        for (int i = 0; i < teams.size(); i++) {
                if (i % 2 == 0) { b.append(" "); }
                int digit = i / 10;
                b.append(digit == 0 ? " " : String.valueOf(digit));
        }
        b.append("\n");
        for (int i = MONIKER_LENGTH + 1; i > 0; i--) { b.append(" "); }
        for (int i = 0; i < teams.size(); i++) {
                if (i % 2 == 0) { b.append(" "); }
                int digit = i % 10;
                b.append(String.valueOf(digit));
        }
        b.append("\n");
        b.append(divider);
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = (Entry)entries.get(i);
            b.append("|");
            b.append(Text.padLeft(entry.getEntrant().getUsername(),
                                  MONIKER_LENGTH,
                                  true));
            for (int j = 0; j < teams.size(); j++) {
                if (j % 2 == 0) { b.append("|"); }
                Team team = (Team)teams.get(j);
                if (entry.contains(team)) {
                    b.append(WEIGHT_CODES[entry.indexOf(team)]);
                } else {
                    b.append(" ");
                }
            }
            b.append("|\n");

            if (i % 5 == 4) {
                b.append(divider);
            }
        }
        if ((entries.size() - 1) % 5 != 4) { b.append(divider); }

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
                Text.padRight("'" + WEIGHT_CODES[i] + "' = ", codeWidth) +
                Text.padRight(pointsFormat.format(weights[i]), weightWidth) +
                "      ");
        }   

        for (Iterator i = Text.makeColumns(strings, 3).iterator();
             i.hasNext(); ) {
            b.append(i.next()).append("\n");
        }

        return b.toString();
    }

    public String formatFavorites() {

        final NumberFormat f = weeklyStats.getSchedule().getNumberFormat();

        StringBuffer b = new StringBuffer();

        b.append("\n\nFAVORITES\n");
        b.append("---------\n");
        b.append("Times --Team-most-picked-- AvgConf | Times --Team-least-picked-- AvgConf\n"); 
        f.setMinimumFractionDigits(f.getMinimumFractionDigits() + 2);
        for (Iterator i = weeklyStats.getFavorites().iterator(); i.hasNext();) {
            Game game = (Game)i.next();

            int order = weeklyStats.getTimesPicked(game.getHome()) -
                        weeklyStats.getTimesPicked(game.getVisitor());
                
            if (order == 0) { 
                order = weeklyStats.getTotalPoints(game.getHome()) -
                        weeklyStats.getTotalPoints(game.getVisitor());
            }

            final Team favorite;
            final Team underdog;

            if (order < 0) {
                favorite = game.getVisitor();
                underdog = game.getHome();
            } else {
                favorite = game.getHome();
                underdog = game.getVisitor();
            }

            b.append(Text.padRight(String.valueOf(
                weeklyStats.getTimesPicked(favorite)), 4));
            b.append("  ");
            b.append(Text.padLeft(favorite.toString(), 20, true));
            double confidence =
                (double) weeklyStats.getTotalPoints(favorite) /
                         weeklyStats.getTimesPicked(favorite);
            b.append(Text.padRight(f.format(confidence), 8));

            b.append(" | ");
            b.append(Text.padRight(String.valueOf(
                weeklyStats.getTimesPicked(underdog)), 4));
            b.append("  ");
            b.append(Text.padLeft(underdog.toString(), 21, true));
            confidence =
                (double) weeklyStats.getTotalPoints(underdog) /
                         weeklyStats.getTimesPicked(underdog);
            b.append(Text.padRight(f.format(confidence), 8));
            b.append("\n");
        }
        f.setMinimumFractionDigits(f.getMinimumFractionDigits() - 2);

        return b.toString();
    }

    public String formatConsensus() {

        final NumberFormat f = weeklyStats.getSchedule().getNumberFormat();

        final Entry consensus = weeklyStats.getConsensus();

        StringBuffer b = new StringBuffer();

        f.setMinimumFractionDigits(f.getMinimumFractionDigits() + 2);
        b.append("\n\nCONSENSUS\n");
        b.append("---------\n"); 
        b.append("---Consensus-favorite--- ----Team-not-favored---- Weight\n");
        f.setMinimumFractionDigits(4);
        for (int i = 0; i < consensus.size(); i++) {
            Team pick = consensus.get(i);
            Team opponent = weeklyStats.getSchedule().getOpponent(pick);
            double weight =
                (weeklyStats.getTotalPoints(pick) -
                 weeklyStats.getTotalPoints(opponent)) /
       (double) (weeklyStats.getTimesPicked(pick) +
                 weeklyStats.getTimesPicked(opponent));
            b.append(Text.padLeft(pick.toString(), 24));
            b.append(" ");
            b.append(Text.padLeft(opponent.toString(), 24));
            b.append(Text.padRight(f.format(weight), 8));
            b.append("\n");
        }
        f.setMinimumFractionDigits(f.getMinimumFractionDigits() - 2);

        return b.toString();
    }

    public String formatChoicesAgainstConsensus() {

        StringBuffer b = new StringBuffer();

        b.append("\n\nCHOICES AGAINST CONSENSUS\n");
        b.append("-------------------------\n");

        final List against = weeklyStats.getAgainstConsensus();
        final List strings = new ArrayList(against.size());

        for (Iterator i = against.iterator(); i.hasNext();) {
            Entrant entrant = (Entrant)i.next();
            strings.add(Text.padLeft(
                weeklyStats.getAgainstConsensus(entrant) +
                " " +
                entrant.getUsername(), 25, true));
        }

        for (Iterator i = Text.makeColumns(strings, 3).iterator();
             i.hasNext(); ) {   
            b.append(i.next()).append("\n");
        }   

        return b.toString();
    }

    public String format() throws MissingPropertyException {
        return formatHeader() +
               formatTitle() +
               formatTeams() +
               formatEntries() +
               formatWeights() +
               formatFavorites() +
               formatConsensus() +
               formatChoicesAgainstConsensus();
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(getClass().getName());
        str.append("[");
        str.append("WeeklyStats="+weeklyStats);
        str.append("]");

        return str.toString();
    }

    private static final String[] WEIGHT_CODES =
        { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
          "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
          "AA", "BB", "CC", "DD", "EE", "FF", "GG", "HH", "II", "JJ"};

    public static void main(String argv[]) {
        com.talient.football.jdbc.JDBCHomes.setHomes();

        final TextRecap tr = new TextRecap(
          new WeeklyStats(
            Crosstable.getHome().findByYearWeek(2001, 2)));

        try {
            System.out.print(tr.format());
        } catch (MissingPropertyException e) {
            System.err.println(e.toString());
        }

        final TextRecap tri = new TextRecap(
          new WeeklyStats(
            Crosstable.getHome().findByYearWeek(2002, 1)));

        try {
            System.out.print(tri.format());
        } catch (MissingPropertyException e) {
            System.err.println(e.toString());
        }

        final TextRecap trb = new TextRecap(
          new WeeklyStats(
            Crosstable.getHome().findByYearWeek(2000, 13)));

        try {
            System.out.print(trb.format());
        } catch (MissingPropertyException e) {
            System.err.println(e.toString());
        }
    }
}

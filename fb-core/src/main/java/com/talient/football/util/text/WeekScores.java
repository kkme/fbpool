// CVS ID: $Id: WeekScores.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.football.util.text;

import com.talient.football.entities.Entry;
import com.talient.football.entities.Game;
import com.talient.football.entities.GameResult;

import com.talient.football.jdbc.JDBCHomes;
import com.talient.football.jdbc.JDBCEntry;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;

/**
 * <p>
 * @author Steve Schmadeke
 * @version $Revision: 1.1.1.1 $
 */
public class WeekScores {

    private WeekScores() {};

    private static final Comparator entryComparator = new Comparator() {
        public int compare(Object a, Object b) {
            return ((Entry)b).getScore() - ((Entry)a).getScore();
        }
    };

    private static void printPadRight(String string, int cols) {
        for (int i = cols - string.length(); i > 0; i--) {
            System.out.print(" ");
         }
         System.out.print(string);
    }

    private static void dumpScores(ArrayList entries) {

        for (int i = 0; i < entries.size(); i++) {
            ((Entry)entries.get(i)).invalidateScores();
        }

        Collections.sort(entries, entryComparator);

        for (int i = 0; i < entries.size(); i++) {
            Entry entry = (Entry)entries.get(i);
            System.out.print("   ");
            printPadRight(String.valueOf(entry.getScore()), 4);
            System.out.print(" ");
            System.out.print(entry.getEntrant().getUsername());
            System.out.print("\n");
        }
    }

    public static void main(String[] args) {
		JDBCHomes.setHomes();

        String usage = "nflpool.WeekScores <year> <week>";

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

        ArrayList entries =
             new ArrayList(JDBCEntry.findByYearWeek(year, week));

        if (entries.size() == 0) {
            System.err.println("No entries found for week " + args[1] +
                               " of year " + args[0]);
            System.exit(1);
        }

        // Count how many games have undetermined results.  Keep track
        // of up to two such games.

        Game g1 = null;
        Game g2 = null;
        int count = 0;

        ArrayList gameList =
            new ArrayList(((Entry)entries.get(0)).getSchedule().games());

        for (int i = 0; i < gameList.size(); i++) {
            Game game = (Game)gameList.get(i);
            if (game.getResult() == GameResult.UNDETERMINED) {
                count++;
                g2 = g1;
                g1 = game;
            }
        }

        System.out.println("From: NFL Pool <nflpool@talient.com>");

        // Future what-if analysis.

        if (count > 2) {

            System.out.println("Subject: NFL Pool Week #" + week +
                " Pre-results");
            System.out.println("\nWith " + count + " games to play:\n");
            dumpScores(entries);

        } else if (count == 2) {

            System.out.println("Subject: NFL Pool Week #" + week +
                " Pre-results");
            g2.setGameState(Game.FINAL);
            g1.setGameState(Game.FINAL);

            g2.setVisitorScore(0);
            g1.setVisitorScore(0);
            g2.setHomeScore(7);
            g1.setHomeScore(7);
            System.out.println(
                "\nIf " + g2.getHome() + " and " + g1.getHome() + " win:\n");
            dumpScores(entries);

            g1.setVisitorScore(14);
            System.out.println(
                "\nIf " + g2.getHome() + " and " +g1.getVisitor() + " win:\n");
            dumpScores(entries);

            g2.setVisitorScore(14);
            System.out.println(
                "\nIf " + g2.getVisitor() + " and "+g1.getVisitor()+" win:\n");
            dumpScores(entries);

            g1.setVisitorScore(0);
            System.out.println(
                "\nIf " + g2.getVisitor() + " and " + g1.getHome() + " win:\n");
            dumpScores(entries);

        } else if (count == 1) {

            System.out.println("Subject: NFL Pool Week #" + week +
                " Pre-results");
            g1.setGameState(Game.FINAL);
            g1.setVisitorScore(0);
            g1.setHomeScore(7);

            System.out.println(
                "\nIf " + g1.getHome() + " wins:\n");
            dumpScores(entries);

            g1.setVisitorScore(14);
            System.out.println(
                "\nIf " + g1.getVisitor() + " wins:\n");
            dumpScores(entries);

            g1.setVisitorScore(7);
            System.out.println(
                "\nIf " + g1.getVisitor() + " and " + g1.getHome() + " tie:\n");
            dumpScores(entries);

        } else {
            System.out.println("Subject: NFL Pool Week #" + week +
                " Final Results");
            dumpScores(entries);
        }
    }
}

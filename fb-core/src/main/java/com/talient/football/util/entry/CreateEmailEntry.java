package com.talient.football.util.entry;

import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

import com.talient.football.entities.Game;

import com.talient.football.jdbc.JDBCGame;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.0
 */
public class CreateEmailEntry {

    private CreateEmailEntry() {};

    public static void main(String[] args) {
        String usage = "Usage: com.talient.football.util.entry.CreateEmailEntry year week";

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

        List games = new ArrayList(JDBCGame.findByYearWeek(year, week));

        if (games.size() == 0) {
            System.err.println("No games found for week " + args[1] +
                               " of year " + args[0]);
            System.exit(1);
        }

        // Sort games by date.

        StringBuffer buffer = new StringBuffer(256);

        buffer.append("\nTo enter using your browser, go to:");
        buffer.append("\n\n    <http://www.talient.com/nflpool/");
        buffer.append("online/online");
        buffer.append(decimalFormat.format(year % 100));
        buffer.append(decimalFormat.format(week));
        buffer.append(".htm>");
        buffer.append("\n\nFor email formatting instructions and ");
        buffer.append("other NFL pool information, go to:");
        buffer.append("\n\n    <http://www.talient.com/nflpool/index.html>");
        buffer.append("\n\nGames for ");
        buffer.append(year);
        buffer.append(" NFL Week ");
        buffer.append(week);
        buffer.append(", due ");
        buffer.append(simpleDateFormat.format(((Game)games.get(0)).getStartTime()));
        buffer.append(":\n");

        System.out.println(buffer.toString());

        System.out.println("$");
        Iterator i = games.iterator();
        while (i.hasNext()) {
            Game game = (Game)i.next();
            System.out.print(game.getVisitor().getLongName());
            System.out.print(" at ");
            System.out.println(game.getHome().getLongName());
        }

    }

    private static SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat("EEEE, M/d, h:mm z");

    private static DecimalFormat decimalFormat =
        new DecimalFormat("00");
}

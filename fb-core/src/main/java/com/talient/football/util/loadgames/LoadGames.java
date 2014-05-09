package com.talient.football.util.loadgames;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.StringTokenizer;
import java.util.Date;

import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.talient.football.entities.Team;
import com.talient.football.entities.Game;

import com.talient.football.jdbc.JDBCTeam;
import com.talient.football.jdbc.JDBCGame;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.0
 */
public class LoadGames {

    private LoadGames() {};

    public static void main(String[] args) {
        String usage = "Usage: com.talient.football.util.loadgames.LoadGames gamefile";

        if (args.length == 0) {
            System.err.println(usage);
            System.exit(1);
        }

        List games;

        try {
            games = parseGames(new FileInputStream(args[0]));
        } catch (FileNotFoundException e) {
            System.err.println(usage);
            throw new RuntimeException(e.toString());
        }

        Iterator iterator = games.iterator();

        while (iterator.hasNext()) {
            Game game = (Game)iterator.next();
/*
            System.out.println("game = " + game);
            System.out.println("game.homeScore = " + game.getHomeScore());
            System.out.println("game.visitorScore = " + game.getVisitorScore());
            System.out.println("game.startTime = " + String.valueOf(game.getStartTime()));
*/
            JDBCGame.store(game);
        }

    }

    private static List parseGames(InputStream i) {
        ArrayList games = new ArrayList();

        BufferedReader b = new BufferedReader(new InputStreamReader(i));

        try {
            String line;

            int year = 0;
            int week = 0;
            Team visitor = null;
            Team home = null;
            int visitorScore = 0;
            int homeScore = 0;
            Date startTime = null;

            int displayOrder = 1;
            int currentWeek = 0;
            while ((line = b.readLine()) != null) {
                StringTokenizer tokens = new StringTokenizer(line, "\t");

                if (tokens.hasMoreTokens()) {
                    try {
                        year = Integer.parseInt(tokens.nextToken());
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(
                            "The following line did not contain " +
                            "a valid year:\n    " + line + "/n" + e.toString());
                    }
                } else {
                    throw new RuntimeException(
                        "The following line did not contain a " +
                        "year:\n    " + line);
                }

                if (tokens.hasMoreTokens()) {
                    try {
                        week = Integer.parseInt(tokens.nextToken());
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(
                            "The following line did not contain " +
                            "a valid week:\n    " + line + "/n" + e.toString());
                    }
                    if (currentWeek != week) {
                        currentWeek = week;
                        displayOrder = 1;
                    }
                } else {
                    throw new RuntimeException(
                        "The following line did not contain a " +
                        "week:\n    " + line);
                }

                if (tokens.hasMoreTokens()) {
                    visitor = JDBCTeam.findByLongNameYear(
                                  tokens.nextToken(), year);
                    if (visitor == null) {
                        throw new RuntimeException(
                            "The following line did not contain a " +
                            "valid visiting team:\n    " + line);
                    }
                } else {
                    throw new RuntimeException(
                        "The following line did not contain a " +
                        "visiting team:\n    " + line);
                }

                if (tokens.hasMoreTokens()) {
                    home = JDBCTeam.findByLongNameYear(
                               tokens.nextToken(), year);
                    if (home == null) {
                        throw new RuntimeException(
                            "The following line did not contain a " +
                            "valid home team:\n    " + line);
                    }
                } else {
                    throw new RuntimeException(
                        "The following line did not contain a " +
                        "home team:\n    " + line);
                }

                if (tokens.hasMoreTokens()) {
                    try {
                        visitorScore = Integer.parseInt(tokens.nextToken());
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(
                            "The following line did not contain " +
                            "a valid visitor score:\n    " + line + 
                            "/n" + e.toString());
                    }
                } else {
                    throw new RuntimeException(
                        "The following line did not contain a " +
                        "visitor score:\n    " + line);
                }

                if (tokens.hasMoreTokens()) {
                    try {
                        homeScore = Integer.parseInt(tokens.nextToken());
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(
                            "The following line did not contain " +
                            "a valid home score:\n    " + line + 
                            "/n" + e.toString());
                    }
                } else {
                    throw new RuntimeException(
                        "The following line did not contain a " +
                        "home score:\n    " + line);
                }

                if (tokens.hasMoreTokens()) {
                    try {
                        startTime = simpleDateFormat.parse(tokens.nextToken());
                    } catch (ParseException e) {
                        throw new RuntimeException(
                            "The following line did not contain " +
                            "a valid starting time:\n    " + line + 
                            "/n" + e.toString());
                    }
                } else {
                    startTime = null;
                }

                Game game = new Game(year, week, home, visitor);

                game.setVisitorScore(visitorScore);
                game.setHomeScore(homeScore);
                if (startTime != null) {
                    game.setStartTime(startTime);
                }
                game.setDisplayOrder(displayOrder++);

                games.add(game);

            }
        } catch (java.io.IOException e) {
        }

        return games;
    }

    private static Team findByLongNameYear(String longName, int year) {
        return JDBCTeam.findByLongNameYear(longName, year);
    }

    private static SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat("MM/dd/yy HH:mm");
}

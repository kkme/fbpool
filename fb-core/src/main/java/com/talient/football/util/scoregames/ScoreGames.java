package com.talient.football.util.scoregames;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.StringTokenizer;
import java.util.Date;
import java.util.Calendar;

import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.talient.football.entities.Team;
import com.talient.football.entities.Game;
import com.talient.football.entities.WeeklySchedule;

import com.talient.football.jdbc.JDBCHomes;
import com.talient.football.jdbc.JDBCTeam;
import com.talient.football.jdbc.JDBCGame;

import com.talient.football.publish.PublishCrosstable;
import com.talient.football.publish.PublishWeeklyStats;
import com.talient.football.publish.PublishResults;

import com.talient.util.Properties;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.0
 */
public class ScoreGames {

    private ScoreGames() {};

    public static void main(String[] args) {
        String usage = "Usage: com.talient.football.util.scoregames.ScoreGames gamefile";

        if (args.length == 0) {
            System.err.println(usage);
            System.exit(1);
        }

        JDBCHomes.setHomes();

        try {
            parseGames(getSchedule(), new FileInputStream(args[0]));
        } catch (FileNotFoundException e) {
            System.err.println(usage);
            throw new RuntimeException(e.toString());
        }
    }

    private static WeeklySchedule getSchedule() {
        Date now = Calendar.getInstance().getTime();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        Collection weeks = WeeklySchedule.getHome().findByYear(year);
        if (weeks.size() == 0) {
            weeks = WeeklySchedule.getHome().findByYear(year-1);
            if (weeks.size() == 0) {
                return null;
            }
        }

        Iterator witer = weeks.iterator();
        WeeklySchedule schedule = null;
        int week = 0;
        while (witer.hasNext()) {
            WeeklySchedule s = (WeeklySchedule)witer.next();
            Date start = s.getStartTime();
            if (now.after(start)) {
                schedule = s;
                week++;
            }
            else {
                break;
            }
        }

        return schedule;
    }

    private static void parseGames(WeeklySchedule schedule, InputStream i) {
        BufferedReader b = new BufferedReader(new InputStreamReader(i));

        try {
            String line;

            if (schedule == null) {
                throw new RuntimeException(
                    "Could not determine the current weekly schedule ");
            }
            int year = schedule.getYear();
            int week = schedule.getWeek();
            System.out.println("Scoring games for Week " + week + " " + year);
            
            Collection games = schedule.games();

            int displayOrder = 1;
            int currentWeek = 0;
            while ((line = b.readLine()) != null) {
                Team visitor = null;
                Team home = null;
                int visitorScore = 0;
                int homeScore = 0;
                String gameTime = "";
                int state = 0;
                int qtr = 0;

                StringTokenizer tokens = new StringTokenizer(line, "\t");
                boolean validVisitor = true;

                if (tokens.hasMoreTokens()) {
                    visitor = JDBCTeam.findByLongNameYear(
                                  tokens.nextToken(), year);
                    if (visitor == null) {
                        validVisitor = false;
                    }
                } else {
                    System.out.println(
                        "The following line did not contain a " +
                        "visiting team:\n    " + line);
                    continue;
                }

                if (tokens.hasMoreTokens()) {
                    try {
                        visitorScore = Integer.parseInt(tokens.nextToken());
                    } catch (NumberFormatException e) {
                        System.out.println(
                            "The following line did not contain " +
                            "a valid visitor score:\n    " + line + 
                            "/n" + e.toString());
                        continue;
                    }
                } else {
                    System.out.println(
                        "The following line did not contain a " +
                        "visitor score:\n    " + line);
                    continue;
                }

                if (tokens.hasMoreTokens()) {
                    home = JDBCTeam.findByLongNameYear(
                               tokens.nextToken(), year);
                    if (home == null) {
                        System.out.println(
                            "The following line did not contain a " +
                            "valid home team:\n    " + line);
                        continue;
                    }
                    if (! validVisitor) {
                        System.out.println(
                            "The following line did not contain a " +
                            "valid visiting team:\n    " + line);
                        continue;
                    }
                } else {
                    System.out.println(
                        "The following line did not contain a " +
                        "home team:\n    " + line);
                    continue;
                }

                if (tokens.hasMoreTokens()) {
                    try {
                        homeScore = Integer.parseInt(tokens.nextToken());
                    } catch (NumberFormatException e) {
                        System.out.println(
                            "The following line did not contain " +
                            "a valid home score:\n    " + line + 
                            "/n" + e.toString());
                        continue;
                    }
                } else {
                    System.out.println(
                        "The following line did not contain a " +
                        "home score:\n    " + line);
                    continue;
                }

                if (tokens.hasMoreTokens()) {
                    try {
                        gameTime = tokens.nextToken();
                    } catch (Exception e) {
                        System.out.println(
                            "The following line did not contain " +
                            "a valid time:\n    " + line + 
                            "/n" + e.toString());
                        gameTime = null;
                        continue;
                    }
                } else {
                    gameTime = null;
                }

                if (tokens.hasMoreTokens()) {
                    try {
                        qtr = Integer.parseInt(tokens.nextToken());
                    } catch (NumberFormatException e) {
                        System.out.println(
                            "The following line did not contain " +
                            "a valid quarter:\n    " + line + 
                            "/n" + e.toString());
                        continue;
                    }
                } else {
                    System.out.println(
                        "The following line did not contain a " +
                        "quarter:\n    " + line);
                    continue;
                }

                if (tokens.hasMoreTokens()) {
                    try {
                        state = Integer.parseInt(tokens.nextToken());
                    } catch (NumberFormatException e) {
                        System.out.println(
                            "The following line did not contain " +
                            "a valid state:\n    " + line + 
                            "/n" + e.toString());
                        continue;
                    }
                } else {
                    System.out.println(
                        "The following line did not contain a " +
                        "state:\n    " + line);
                    continue;
                }

                if (schedule.contains(visitor)) {
                    Team scheduleHome = schedule.getOpponent(visitor);
                    if (! scheduleHome.equals(home)) {
                        System.out.println(
                            "Could not find a game in the schedule where " + 
                            visitor + " plays " + scheduleHome + " [" + home + "]");
                        continue;
                    }
                }

                Game game = null;
                Iterator giter = games.iterator();
                boolean found = false;
                while (giter.hasNext()) {
                    game = (Game)giter.next();
                    if (game.getVisitor().equals(visitor)) {
                        found = true;
                        break;
                    }
                    if (game.getHome().equals(visitor)) {
                        int tmpScore = homeScore;
                        homeScore = visitorScore;
                        visitorScore = tmpScore;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.out.println(
                        "Could not find a game in the schedule where " + 
                        visitor + " plays " + home);
                    continue;
                }

                if (game.getGameState() > Game.FINAL) {
                    continue;
                }

                game.setVisitorScore(visitorScore);
                game.setHomeScore(homeScore);
                int dbQtr = game.getQuarter();
                int dbGameTime = game.getGameClock();
                if (qtr > dbQtr) {
                    gameTime="15:00";
                    dbGameTime = 0;
                    game.setQuarter(qtr);
                }
                game.setGameState(state);
                if (gameTime != null) {
                    try {
                        if (Properties.getProperty("football.pool.name").
                                                   equals("CFPOOL") &&
                                                               qtr == 5) {
                            gameTime="0:00";
                        }
                        if (dbGameTime > 0 && ! gameTime.equals("15:00")) {
                            game.setGameClock(gameTime);
                        }
                        else if (dbGameTime == 0) {
                            game.setGameClock(gameTime);
                        }
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }

                JDBCGame.store(game);

            }
            try {
                System.out.println("Publishing Crosstable...");
                String path = PublishCrosstable.publish(year, week);
            }
            catch (Exception e) {
                System.out.println("Could not publish Crosstable");
            }

            try {
                System.out.println("Publishing WeeklyStats...");
                String path =
                    PublishWeeklyStats.publish(year, week);
            }
            catch (Exception e) {
                System.out.println("Could not publish WeeklyStats");
            }

            try {
                System.out.println("Publishing Results...");
                String path = PublishResults.publish(year, week);
            }
            catch (Exception e) {
                System.out.println("Could not publish Results");
            }
        } catch (java.io.IOException e) {
        }
    }

    private static Team findByLongNameYear(String longName, int year) {
        return JDBCTeam.findByLongNameYear(longName, year);
    }

    private static SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat("MM/dd/yy HH:mm");
}

package com.talient.football.jdbc;

import java.io.*;
import java.util.*;
import com.talient.football.entities.Game;
import com.talient.football.entities.Team;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.talient.util.Properties;

public class JDBCGame
{
    public static int store(Game game) {
        // Make sure everything is populated correctly
        if (game == null) {
            System.err.println("ERROR: trying to store a null game");
            return 0;
        }
        if ((game.getYear() == 0) || (game.getWeek() == 0)) {
            System.err.println("ERROR: trying to store a game with an " +
                "illegal value for Year or Week");
            return 0;
        }
        if (!(game.getHome() instanceof JDBCTeam.JDBCTeamInfo) ||
            !(game.getVisitor() instanceof JDBCTeam.JDBCTeamInfo)) {
            System.err.println("ERROR: Can only store Game objects who's " +
                "Team data members are JDBCTeam.JDBCTeamInfo objects.");
            return 0;
        }

        JDBCTeam.JDBCTeamInfo home =
            (JDBCTeam.JDBCTeamInfo)game.getHome();
        JDBCTeam.JDBCTeamInfo visitor =
            (JDBCTeam.JDBCTeamInfo)game.getVisitor();

        // Store the JDBCGame
        try {
            Connection con = ConnectionManager.getConnection();

            if (home == null || visitor == null) {
                System.err.println("ERROR: Found duplicate game for [" +
                    game.getYear() + ", " + game.getWeek() + ", " +
                    game.getHome() + ", " + game.getVisitor() + "]");
                return 0;
            }

            String query;
            query = "select Year, Week, HomeTeamID, VisitorTeamID, " +
                           "HomeScore, VisitorScore, GameState, Quarter, " +
                           "GameClock, StartTime, DisplayOrder " +
                    "from Games " +
                    "where Year=" + game.getYear() +
                    " and Week=" + game.getWeek() +
                    " and HomeTeamID=" + home.getTeamID() +
                    " and VisitorTeamID=" + visitor.getTeamID();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            int cnt = 0;
            while (rs.next()) {
                cnt++;
            }

            if (cnt > 1) {
                System.err.println("ERROR: Found duplicate game for [" +
                    game.getYear() + ", " + game.getWeek() + ", " +
                    game.getHome() + ", " + game.getVisitor() + "]");
                return 0;
            }

            String update;
            Timestamp st = new Timestamp(game.getStartTime().getTime());
            if (cnt == 1) {
                update = "update Games set " +
                              "HomeScore=" + game.getHomeScore() +
                              ", VisitorScore=" + game.getVisitorScore() +
                              ", GameState=" + game.getGameState() +
                              ", Quarter=" + game.getQuarter() +
                              ", GameClock=" + game.getGameClock() +
                              ", StartTime='" + st +
                             "', DisplayOrder=" + game.getDisplayOrder() +
                         " where Year=" + game.getYear() +
                           " and Week=" + game.getWeek() +
                           " and HomeTeamID=" + home.getTeamID() +
                           " and VisitorTeamID=" + visitor.getTeamID();
            }
            else {
                update = "insert into Games set " +
                         "Year=" + game.getYear() +
                         ", Week=" + game.getWeek() +
                         ", HomeTeamID=" + home.getTeamID() +
                         ", VisitorTeamID=" + visitor.getTeamID() +
                         ", HomeScore=" + game.getHomeScore() +
                         ", VisitorScore=" + game.getVisitorScore() +
                         ", GameState=" + game.getGameState() +
                         ", Quarter=" + game.getQuarter() +
                         ", GameClock=" + game.getGameClock() +
                         ", StartTime='" + st +
                         "', DisplayOrder=" + game.getDisplayOrder();
            }

            stmt = con.createStatement();

            return stmt.executeUpdate(update);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCGame.store");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCGame.store");
            System.err.println("Exception: " + e.getMessage());
            return 0;
        }
    }

    private static int remove(Game game) {
        // Make sure everything is populated correctly
        if (game == null) {
            System.err.println("ERROR: trying to remove a null game");
            return 0;
        }
        if ((game.getYear() == 0) || (game.getWeek() == 0)) {
            System.err.println("ERROR: trying to remove a game with an " +
                "illegal value for Year or Week");
            return 0;
        }
        if (!(game.getHome() instanceof JDBCTeam.JDBCTeamInfo) ||
            !(game.getVisitor() instanceof JDBCTeam.JDBCTeamInfo)) {
            System.err.println("ERROR: Can only remove Game objects who's " +
                "Team data members are JDBCTeam.JDBCTeamInfo objects.");
            return 0;
        }

        JDBCTeam.JDBCTeamInfo home =
            (JDBCTeam.JDBCTeamInfo)game.getHome();
        JDBCTeam.JDBCTeamInfo visitor =
            (JDBCTeam.JDBCTeamInfo)game.getVisitor();

        try {
            Connection con = ConnectionManager.getConnection();

            String delete;
            delete = "delete from Games " +
                     "where Year=" + game.getYear() +
                         " and Week=" + game.getWeek() +
                         " and HomeTeamID=" + home.getTeamID() +
                         " and VisitorTeamID=" + visitor.getTeamID();

            Statement stmt = con.createStatement();

            return stmt.executeUpdate(delete);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCGame.remove");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCGame.remove");
            System.err.println("Exception: " + e.getMessage());
            return 0;
        }
    }

    public static Game findByYearWeekHomeVisitor(int year, int week,
                                                 Team home, Team visitor) {
        Game game = null;
        try {
            Connection con = ConnectionManager.getConnection();

            int homeID = 0;
            int visitorID = 0;
            if (home instanceof JDBCTeam.JDBCTeamInfo) {
                homeID = ((JDBCTeam.JDBCTeamInfo)home).getTeamID();
            }
            else {
                JDBCTeam.JDBCTeamInfo team = (JDBCTeam.JDBCTeamInfo)
                    JDBCTeam.findByYearLongName(year, home.getLongName());
                if (team != null) {
                    homeID = team.getTeamID();
                }
                else {
                    System.err.println("Trying to find a Game with out the " +
                                       "Home team set.");
                    return null;
                }
            }
            if (visitor instanceof JDBCTeam.JDBCTeamInfo) {
                visitorID = ((JDBCTeam.JDBCTeamInfo)visitor).getTeamID();
            }
            else {
                JDBCTeam.JDBCTeamInfo team = (JDBCTeam.JDBCTeamInfo)
                    JDBCTeam.findByYearLongName(year, visitor.getLongName());
                if (team != null) {
                    visitorID = team.getTeamID();
                }
                else {
                    System.err.println("Trying to find a Game with out the " +
                                       "Visitor team set.");
                    return null;
                }
            }

            String query;
            query = "select Year, Week, HomeTeamID, VisitorTeamID, " +
                           "HomeScore, VisitorScore, GameState, Quarter, " +
                           "GameClock, StartTime, DisplayOrder " +
                    "from Games " +
                    "where Year=" + year +
                    " and  Week=" + week +
                    " and HomeTeamID=" + homeID +
                    " and VisitorTeamID=" + visitorID;
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);

            // Get the JDBCTeamInfo object for home and visitor
            JDBCTeam.JDBCTeamInfo jdbcHome = null;
            JDBCTeam.JDBCTeamInfo jdbcVisitor = null;
            if (home instanceof JDBCTeam.JDBCTeamInfo) {
                jdbcHome = (JDBCTeam.JDBCTeamInfo)home;
            }
            else {
                jdbcHome = JDBCTeam.findByTeamID(rs.getInt("HomeTeamID"));
            }
            if (visitor instanceof JDBCTeam.JDBCTeamInfo) {
                jdbcVisitor = (JDBCTeam.JDBCTeamInfo)visitor;
            }
            else {
                jdbcVisitor = JDBCTeam.findByTeamID(rs.getInt("VisitorTeamID"));
            }

            while (rs.next()) {
                game = new Game(rs.getInt("Year"),
                                rs.getInt("Week"),
                                (Team)jdbcHome,
                                (Team)jdbcVisitor);
                game.setHomeScore(rs.getInt("HomeScore"));
                game.setVisitorScore(rs.getInt("VisitorScore"));
                game.setGameState(rs.getInt("GameState"));
                game.setQuarter(rs.getInt("Quarter"));
                game.setGameClock(rs.getInt("GameClock"));
                game.setStartTime(rs.getTimestamp("StartTime"));
                game.setDisplayOrder(rs.getInt("DisplayOrder"));
                return game;
            }
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCGame.findByYearWeekHomeVisitor");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return null;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCGame.findByYearWeekHomeVisitor");
            System.err.println("Exception: " + e.getMessage());
            return null;
        }

        return null;
    }

    public static Collection findByYearWeek(int year, int week) {
        ArrayList games = new ArrayList();

        try {
            Connection con = ConnectionManager.getConnection();

            String query;
            query = "select Year, Week, HomeTeamID, VisitorTeamID, " +
                           "HomeScore, VisitorScore, GameState, Quarter, " +
                           "GameClock, StartTime, DisplayOrder " +
                    "from Games " +
                    "where Year=" + year +
                    " and Week=" + week +
                    " order by DisplayOrder";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                JDBCTeam.JDBCTeamInfo jdbcHome =
                    JDBCTeam.findByTeamID(rs.getInt("HomeTeamID"));
                JDBCTeam.JDBCTeamInfo jdbcVisitor =
                    JDBCTeam.findByTeamID(rs.getInt("VisitorTeamID"));

                Game game;
                game = new Game(rs.getInt("Year"),
                                rs.getInt("Week"),
                                (Team)jdbcHome,
                                (Team)jdbcVisitor);
                game.setHomeScore(rs.getInt("HomeScore"));
                game.setVisitorScore(rs.getInt("VisitorScore"));
                game.setGameState(rs.getInt("GameState"));
                game.setQuarter(rs.getInt("Quarter"));
                game.setGameClock(rs.getInt("GameClock"));
                game.setStartTime(rs.getTimestamp("StartTime"));
                game.setDisplayOrder(rs.getInt("DisplayOrder"));
                games.add(game);
            }
            return games;
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCGame.findByYearWeek");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return games;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCGame.findByYearWeek");
            System.err.println("Exception: " + e.getMessage());
            return games;
        }
    }


    public static void main(String[] Args) {
        ConnectionManager cm =
            new ConnectionManager(
                Properties.getProperty("football.database.url"));

        Team den = JDBCTeam.findByLongName("Denver");
        Team min = JDBCTeam.findByLongName("Minnesota");
        Team oak = JDBCTeam.findByLongName("Oakland");
        Team ks = JDBCTeam.findByLongName("Kansas City");

        int result;
        System.out.println("Calling store to create a Game");
        Game game1 = new Game(1950, 1, den, min);
        game1.setHomeScore(14);
        game1.setVisitorScore(7);
        game1.setGameState(Game.FINAL);
        game1.setQuarter(4);
        game1.setGameClock(0);
        game1.setDisplayOrder(2);
        result = JDBCGame.store(game1);
        System.out.println("store result = " + result);

        Game game1a = JDBCGame.findByYearWeekHomeVisitor(1950, 1, den, min);
        System.out.println("ByYearWeekHomeVisitor = " + game1a);
        System.out.println("Score: " + game1a.getHomeScore() + " to " +
                           game1a.getVisitorScore());

        game1a.setHomeScore(21);
        JDBCGame.store(game1a);

        game1a = JDBCGame.findByYearWeekHomeVisitor(1950, 1, den, min);
        System.out.println("ByYearWeekHomeVisitor = " + game1a);
        System.out.println("Score: " + game1a.getHomeScore() + " to " +
                           game1a.getVisitorScore());

        Game game2 = new Game(1950, 1, oak, ks);
        game2.setHomeScore(3);
        game2.setVisitorScore(10);
        game2.setGameState(Game.FINAL);
        game2.setQuarter(4);
        game2.setGameClock(0);
        game2.setDisplayOrder(1);
        result = JDBCGame.store(game2);

        Collection games = JDBCGame.findByYearWeek(1950, 1);
        // System.exit(1);
        System.out.println("ByYearWeek 1950-1 length = " + games.size());
        Iterator itr = games.iterator();
        while (itr.hasNext()) {
            Game game = (Game)itr.next();
            System.out.println("1999 game = " + game);
        }

        System.out.println("Removing existing Games");
        result = JDBCGame.remove(game1);
        System.out.println("remove result = " + result);
        result = JDBCGame.remove(game2);
        System.out.println("remove result = " + result);

        games = JDBCGame.findByYearWeek(1950, 1);
        System.out.println("Number of games for 1950-1 is " + games.size());

        System.out.println("Removing an non-existing Games");
        result = JDBCGame.remove(game1);
    }
}

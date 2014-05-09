package com.talient.football.jdbc;

import java.io.*;
import java.util.*;
import com.talient.football.entities.WeeklyResult;
import com.talient.football.entities.Entrant;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.talient.util.Properties;

import com.talient.football.reports.YearToDate;

public class JDBCWeeklyResult implements YearToDate.YearToDateHome
{
    public static int store(WeeklyResult weeklyResult) {
        // Make sure everything is populated correctly
        if (weeklyResult == null) {
            System.err.println("ERROR: trying to store a null weeklyResult");
            return 0;
        }
        if ((weeklyResult.getYear() == 0) || (weeklyResult.getWeek() == 0)) {
            System.err.println("ERROR: trying to store a weeklyResult with " +
                "an illegal value for Year or Week");
            return 0;
        }
        Entrant entrant = weeklyResult.getEntrant();
        JDBCEntrant.JDBCEntrantInfo jdbcEntrant = null;
        if (entrant instanceof JDBCEntrant.JDBCEntrantInfo) {
            jdbcEntrant = (JDBCEntrant.JDBCEntrantInfo)entrant;
        }
        else {
            jdbcEntrant = (JDBCEntrant.JDBCEntrantInfo)
                JDBCEntrant.findByUsername(entrant.getUsername());
            if (jdbcEntrant == null) {
                System.err.println("ERROR: trying to store a weeklyResult " +
                    "with an illegal value for Entrant: " + entrant);
                return 0;
            }
        }

        JDBCWeeklyResult.remove(weeklyResult);

        // Store the JDBCWeeklyResult
        try {
            Connection con = ConnectionManager.getConnection();

            String update;
            update = "insert into WeeklyResults set " +
                     "Year=" + weeklyResult.getYear() +
                     ", Week=" + weeklyResult.getWeek() +
                     ", EntrantID=" + jdbcEntrant.getEntrantID() +
                     ", Score=" + weeklyResult.getScore() +
                     ", Bonus=" + weeklyResult.getBonus() +
                     ", Wins=" + weeklyResult.getWins() +
                     ", Losses=" + weeklyResult.getLosses() +
                     ", Ties=" + weeklyResult.getTies();

            Statement stmt = con.createStatement();

            return stmt.executeUpdate(update);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCWeeklyResult.store");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCWeeklyResult.store");
            System.err.println("Exception: " + e.getMessage());
            return 0;
        }
    }

    private static int remove(WeeklyResult weeklyResult) {
        // Make sure everything is populated correctly
        if (weeklyResult == null) {
            System.err.println("ERROR: trying to remove a null weeklyResult");
            return 0;
        }
        if ((weeklyResult.getYear() == 0) || (weeklyResult.getWeek() == 0)) {
            System.err.println("ERROR: trying to remove a weeklyResult with " +
                "an illegal value for Year or Week");
            return 0;
        }

        Entrant entrant = weeklyResult.getEntrant();
        JDBCEntrant.JDBCEntrantInfo jdbcEntrant = null;
        if (entrant instanceof JDBCEntrant.JDBCEntrantInfo) {
            jdbcEntrant = (JDBCEntrant.JDBCEntrantInfo)entrant;
        }
        else {
            jdbcEntrant = (JDBCEntrant.JDBCEntrantInfo)
                JDBCEntrant.findByUsername(entrant.getUsername());
            if (jdbcEntrant == null) {
                System.err.println("ERROR: trying to store a weeklyResult " +
                    "with an illegal value for Entrant: " + entrant);
                return 0;
            }
        }

        try {
            Connection con = ConnectionManager.getConnection();

            String delete;
            delete = "delete from WeeklyResults " +
                     "where Year=" + weeklyResult.getYear() +
                         " and Week=" + weeklyResult.getWeek() +
                         " and EntrantID=" + jdbcEntrant.getEntrantID();

            Statement stmt = con.createStatement();

            return stmt.executeUpdate(delete);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCWeeklyResult.remove");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCWeeklyResult.remove");
            System.err.println("Exception: " + e.getMessage());
            return 0;
        }
    }

    public static WeeklyResult findByYearWeekEntrant(int year, int week,
                                                     Entrant entrant) {
        WeeklyResult weeklyResult = null;
        try {
            Connection con = ConnectionManager.getConnection();

            JDBCEntrant.JDBCEntrantInfo jdbcEntrant = null;
            int entrantID = 0;
            if (entrant instanceof JDBCEntrant.JDBCEntrantInfo) {
                entrantID =
                    ((JDBCEntrant.JDBCEntrantInfo)entrant).getEntrantID();
                jdbcEntrant = (JDBCEntrant.JDBCEntrantInfo)entrant;
            }
            else {
                jdbcEntrant = (JDBCEntrant.JDBCEntrantInfo)
                    JDBCEntrant.findByUsername(entrant.getUsername());
                if (jdbcEntrant != null) {
                    entrantID = jdbcEntrant.getEntrantID();
                }
                else {
                    System.err.println("Trying to find a WeeklyResult with " +
                        "out the Entrant set.");
                    return null;
                }
            }

            String query;
            query = "select Year, Week, EntrantID, " +
                           "Score, Bonus, Wins, Losses, Ties " +
                    "from WeeklyResults " +
                    "where Year=" + year +
                    " and  Week=" + week +
                    " and EntrantID=" + entrantID;

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                weeklyResult = new WeeklyResult(rs.getInt("Year"),
                                                rs.getInt("Week"),
                                                (Entrant)jdbcEntrant);
                weeklyResult.setScore(rs.getInt("Score"));
                weeklyResult.setBonus(rs.getInt("Bonus"));
                weeklyResult.setWins(rs.getInt("Wins"));
                weeklyResult.setLosses(rs.getInt("Losses"));
                weeklyResult.setTies(rs.getInt("Ties"));
                return weeklyResult;
            }
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCWeeklyResult.findByYearWeekEntrant");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return null;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCWeeklyResult.findByYearWeekEntrant");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public Collection findByYearWeek(int year, int week) {
        ArrayList weeklyResults = new ArrayList();

        try {
            Connection con = ConnectionManager.getConnection();

            String query;
            query = "select Year, Week, EntrantID, " +
                           "Score, Bonus, Wins, Losses, Ties " +
                    "from WeeklyResults " +
                    "where Year=" + year +
                    " and Week=" + week +
                    " order by Score desc";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                JDBCEntrant.JDBCEntrantInfo jdbcEntrant =
                    JDBCEntrant.findByEntrantID(rs.getInt("EntrantID"));

                WeeklyResult weeklyResult;
                weeklyResult = new WeeklyResult(rs.getInt("Year"),
                                                rs.getInt("Week"),
                                                (Entrant)jdbcEntrant);
                weeklyResult.setScore(rs.getInt("Score"));
                weeklyResult.setBonus(rs.getInt("Bonus"));
                weeklyResult.setWins(rs.getInt("Wins"));
                weeklyResult.setLosses(rs.getInt("Losses"));
                weeklyResult.setTies(rs.getInt("Ties"));
                weeklyResults.add(weeklyResult);
            }
            return weeklyResults;
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCWeeklyResult.findByYearWeek");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return weeklyResults;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCWeeklyResult.findByYearWeek");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return weeklyResults;
        }
    }


    public static void main(String[] Args) {
        ConnectionManager cm =
            new ConnectionManager(
                Properties.getProperty("football.database.url"));

        Entrant bugs = new Entrant("Bugs Bunny", "bugs@talient.com");
        JDBCEntrant.store(bugs);
        Entrant daffy = new Entrant("Daffy Duck", "daffy@talient.com");
        JDBCEntrant.store(daffy);

        int result;
        System.out.println("Calling store to create bugs WeeklyResult");
        WeeklyResult bugsWR = new WeeklyResult(1950, 1, bugs);
        bugsWR.setScore(758);
        bugsWR.setBonus(0);
        result = JDBCWeeklyResult.store(bugsWR);
        System.out.println("store result = " + result);

        WeeklyResult find1 =
            JDBCWeeklyResult.findByYearWeekEntrant(1950, 1, bugs);
        System.out.println("ByYearWeekEntrant = " + find1);
        System.out.println("Score: " + find1.getScore() + " Bonus: " +
                           find1.getBonus());

        find1.setBonus(100);
        JDBCWeeklyResult.store(find1);

        System.out.println("Bugs hasWeeklyResults = " +
            JDBCEntrant.hasWeeklyResults(bugs));

        System.out.println("Daffy hasWeeklyResults = " +
            JDBCEntrant.hasWeeklyResults(daffy));

        WeeklyResult find2 =
            JDBCWeeklyResult.findByYearWeekEntrant(1950, 1, bugs);
        System.out.println("ByYearWeekEntrant = " + find2);
        System.out.println("Score: " + find2.getScore() + " Bonus: " +
                           find2.getBonus());

        System.out.println("Calling store to create daffy WeeklyResult");
        WeeklyResult daffyWR = new WeeklyResult(1950, 1, daffy);
        daffyWR.setScore(800);
        daffyWR.setBonus(0);
        result = JDBCWeeklyResult.store(daffyWR);
        System.out.println("store result = " + result);

        YearToDate.setHome(new JDBCWeeklyResult());

        Collection weeklyResults = YearToDate.getHome().findByYearWeek(1950, 1);
        System.out.println("ByYearWeek 1950-1 length = " +
                            weeklyResults.size());
        Iterator itr = weeklyResults.iterator();
        while (itr.hasNext()) {
            WeeklyResult weeklyResult = (WeeklyResult)itr.next();
            System.out.println("1950 week 1 weeklyResult = " + weeklyResult);
        }

        System.out.println("Removing existing WeeklyResults");
        result = JDBCWeeklyResult.remove(bugsWR);
        System.out.println("remove result = " + result);
        result = JDBCWeeklyResult.remove(daffyWR);
        System.out.println("remove result = " + result);

        weeklyResults = YearToDate.getHome().findByYearWeek(1950, 1);
        System.out.println("Number of weeklyResults for 1950-1 is " +
                           weeklyResults.size());

        System.out.println("Removing an non-existing WeeklyResults");
        result = JDBCWeeklyResult.remove(bugsWR);

        JDBCEntrant.remove(bugs);
        JDBCEntrant.remove(daffy);
    }
}

package com.talient.football.jdbc;

import java.io.*;
import java.util.*;
import com.talient.football.entities.WeeklySchedule;
import com.talient.football.entities.Team;
import com.talient.football.entities.Game;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import com.talient.util.Properties;

public class JDBCWeeklySchedule implements WeeklySchedule.WeeklyScheduleHome
{
    public int store(WeeklySchedule schedule) {
        // Make sure everything is populated correctly
        if (schedule == null) {
            System.err.println("ERROR: trying to store a null schedule");
            return 0;
        }
        if ((schedule.getYear() == 0) || (schedule.getWeek() == 0)) {
            System.err.println("ERROR: trying to store a schedule with an " +
                "illegal value for Year or Week");
            return 0;
        }

        // Remove the old schedule first
        remove(schedule);

        // Store the JDBCWeeklySchedule
        Collection games = schedule.games();
        Iterator iter = games.iterator();
        int cnt = 0;
        while (iter.hasNext()) {
            Game game = (Game)iter.next();
            int result = JDBCGame.store(game);
            if (result == 1) {
                cnt++;
            }
        }
        return cnt;
    }

    private int remove(WeeklySchedule schedule) {
        // Make sure everything is populated correctly
        if (schedule == null) {
            System.err.println("ERROR: trying to remove a null schedule");
            return 0;
        }
        if ((schedule.getYear() == 0) || (schedule.getWeek() == 0)) {
            System.err.println("ERROR: trying to remove a schedule with an " +
                "illegal value for Year or Week");
            return 0;
        }

        try {
            Connection con = ConnectionManager.getConnection();

            String delete;
            delete = "delete from Games " +
                     "where Year=" + schedule.getYear() +
                     " and Week=" + schedule.getWeek();

            Statement stmt = con.createStatement();
            return stmt.executeUpdate(delete);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCWeeklySchedule.remove");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCWeeklySchedule.remove");
            System.err.println("Exception: " + e.getMessage());
            return 0;
        }
    }

    public WeeklySchedule findByYearWeek(int year, int week) {
        WeeklySchedule schedule = new WeeklySchedule(year, week);

        Collection games = JDBCGame.findByYearWeek(year, week);
        Iterator iter = games.iterator();
        while (iter.hasNext()) {
            Game game = (Game)iter.next();
            schedule.add(game);
        }
        return schedule;
    }

    public Collection findByYear(int year) {
        ArrayList schedules = new ArrayList();

        int week = 1;
        Collection games = JDBCGame.findByYearWeek(year, week);
        while (games.size() > 0) {
            WeeklySchedule schedule = new WeeklySchedule(year, week);

            Iterator iter = games.iterator();
            while (iter.hasNext()) {
                Game game = (Game)iter.next();
                schedule.add(game);
            }
            schedules.add(schedule);

            games = JDBCGame.findByYearWeek(year, ++week);
        }

        return schedules;
    }


    public static void main(String[] Args) {
        JDBCWeeklySchedule jdbcWeeklySchedule = new JDBCWeeklySchedule();
        WeeklySchedule.setHome(jdbcWeeklySchedule);

        ConnectionManager cm =
            new ConnectionManager(
                Properties.getProperty("football.database.url"));

        Team den = JDBCTeam.findByLongName("Denver");
        Team min = JDBCTeam.findByLongName("Minnesota");
        Team oak = JDBCTeam.findByLongName("Oakland");
        Team ks = JDBCTeam.findByLongName("Kansas City");
        Team gb = JDBCTeam.findByLongName("Green Bay");
        Team dal = JDBCTeam.findByLongName("Dallas");

        Game game1 = new Game(1950, 1, den, min);
        game1.setDisplayOrder(1);
        Game game2 = new Game(1950, 1, oak, ks);
        game1.setDisplayOrder(3);
        Game game3 = new Game(1950, 1, gb, dal);
        game1.setDisplayOrder(2);

        WeeklySchedule schedule1 = new WeeklySchedule(1950, 1);
        schedule1.add(game1);
        schedule1.add(game2);
        schedule1.add(game3);

        System.out.println("\nBefore schedule.store():\n\t" + schedule1);

        int result;
        result = WeeklySchedule.getHome().store(schedule1);

        WeeklySchedule schedule2 =
            WeeklySchedule.getHome().findByYearWeek(1950, 1);

        System.out.println("\nAfter schedule.store():\n\t" + schedule2);
        System.out.println("store() result = " + result);

        result = jdbcWeeklySchedule.remove(schedule2);

        WeeklySchedule schedule3 =
            WeeklySchedule.getHome().findByYearWeek(1950, 1);

        System.out.println("\nAfter schedule.remove():\n\t" + schedule3);
        System.out.println("remove() result = " + result);
    }
}

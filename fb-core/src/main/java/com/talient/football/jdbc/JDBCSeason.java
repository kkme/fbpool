package com.talient.football.jdbc;

import java.io.*;
import java.util.*;
import com.talient.football.reports.Season;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.talient.util.Properties;

public class JDBCSeason implements Season.SeasonHome
{
    public Season findByYear(int year) {
        try {
            Connection con = ConnectionManager.getConnection();

            String query;
            query = "select distinct Week " +
                    "from Games " +
                    "where Year=" + year;

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.last();
            int weeks = rs.getRow();

            return new Season(year, weeks);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCSeason.findByYear");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return null;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCSesason.findByYear");
            System.err.println("Exception: " + e.getMessage());
            return null;
        }
    }


    public static void main(String[] Args) {
        JDBCSeason jdbcSeason = new JDBCSeason();
        JDBCHomes.setHomes();

        Season season =
            Season.getHome().findByYear(2002);
        System.out.println("Total Weeks for 2002 is " + season.getTotalWeeks());
    }
}

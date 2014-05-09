package com.talient.football.jdbc;

import java.io.*;
import java.util.*;
import com.talient.football.entities.Team;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.talient.util.Properties;

public class JDBCTeam
{
    protected interface JDBCTeamInfo {
        public void setTeamID(int teamID);
        public int getTeamID();
    }

    private static class JDBCTeamImpl extends Team implements JDBCTeamInfo {
        int id;

        public JDBCTeamImpl(String longName, String shortName) {
            super(longName, shortName);
        }
        public void setTeamID(int teamID)       { id = teamID; }
        public int  getTeamID()                 { return id; }
    }

    // Cache the teams for future lookups
    private static HashMap byTeamID = new HashMap();
    private static HashMap byLongName = new HashMap();
    private static HashMap byYearLongName = new HashMap();
    private static HashMap byYear = new HashMap();

    private static int store(Team team) {
        try {
            Connection con = ConnectionManager.getConnection();

            String update;
            if (team instanceof JDBCTeamImpl) {
                update = "update Teams set ";
            }
            else {
                update = "insert into Teams set ";
            }
            update += "LongName='" + team.getLongName() +
                      "', ShortName='" + team.getShortName() + "'";
            if (team instanceof JDBCTeamImpl) {
                update += "where TeamID=" + ((JDBCTeamImpl)team).getTeamID();
            }

            Statement stmt = con.createStatement();

            return stmt.executeUpdate(update);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCTeam.store");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCTeam.store");
            System.err.println("Exception: " + e.getMessage());
            return 0;
        }
    }

    private static int remove(Team team) {
        try {
            Connection con = ConnectionManager.getConnection();

            String del;
            if (team instanceof JDBCTeamImpl) {
                del = "delete from Teams " +
                         "where TeamID=" + ((JDBCTeamImpl)team).getTeamID();
            }
            else {
                return 0;
            }

            Statement stmt = con.createStatement();

            return stmt.executeUpdate(del);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCTeam.store");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCTeam.store");
            System.err.println("Exception: " + e.getMessage());
            return 0;
        }
    }

    public static JDBCTeamInfo findByTeamID(int id) {
        JDBCTeamImpl team = (JDBCTeamImpl)byTeamID.get(new Integer(id));
        if (team != null) {
            return team;
        }

        try {
            Connection con = ConnectionManager.getConnection();

            String query = "select TeamID, LongName, ShortName from Teams " +
                           "where TeamID=" + id;
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                team = new JDBCTeamImpl(rs.getString("LongName"),
                                        rs.getString("ShortName"));
                team.setTeamID(rs.getInt("TeamID"));
                ((Team)team).setCurrentName(rs.getString("LongName"));
                byTeamID.put(new Integer(id), team);
                return team;
            }
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCTeamImpl.findByTeamID");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return null;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCTeamImpl.findByTeamID");
            System.err.println("Exception: " + e.getMessage());
            return null;
        }

        return null;
    }

    public static Team findByLongName(String longName) {
        JDBCTeamImpl team = (JDBCTeamImpl)byLongName.get(longName);
        if (team != null) {
            return team;
        }
        try {
            Connection con = ConnectionManager.getConnection();

            String query = "select TeamID, LongName, ShortName from Teams " +
                           "where LongName='" + longName + "'";
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                team = new JDBCTeamImpl(rs.getString("LongName"),
                                        rs.getString("ShortName"));
                team.setTeamID(rs.getInt("TeamID"));
                ((Team)team).setCurrentName(rs.getString("LongName"));
                byLongName.put(longName, team);
                return team;
            }
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCTeam.findByLongName");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return null;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCTeam.findByLongName");
            System.err.println("Exception: " + e.getMessage());
            return null;
        }

        return null;
    }

    public static Team findByYearLongName(int year, String longName) {
        JDBCTeamImpl team = (JDBCTeamImpl)byYearLongName.get(longName + year);
        if (team != null) {
            return team;
        }
        try {
            Connection con = ConnectionManager.getConnection();

            String query = "select TeamID, LongName, ShortName from Teams " +
                           "where Year=" + year +
                                " LongName='" + longName + "'";
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                team = new JDBCTeamImpl(rs.getString("LongName"),
                                        rs.getString("ShortName"));
                team.setTeamID(rs.getInt("TeamID"));
                ((Team)team).setCurrentName(rs.getString("LongName"));
                byYearLongName.put(longName + year, team);
                return team;
            }
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCTeam.findByYearLongName");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return null;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCTeam.findByYearLongName");
            System.err.println("Exception: " + e.getMessage());
            return null;
        }

        return null;
    }

    public static Team findByLongNameYear(String longName, int year) {
        JDBCTeamImpl team = null;
        try {
            Connection con = ConnectionManager.getConnection();

            String query = "select TeamID, LongName, ShortName " +
                           "from FranchiseNameHistory " +
                           "where LongName='" + longName + "' and " +
                                  year + " between StartYear and EndYear";
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                team = new JDBCTeamImpl(rs.getString("LongName"),
                                        rs.getString("ShortName"));
                team.setTeamID(rs.getInt("TeamID"));
                ((Team)team).setCurrentName(rs.getString("LongName"));
                return team;
            }
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCTeam.findByLongNameYear");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return null;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCTeam.findByLongNameYear");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public static Collection findByYear(int year) {
        ArrayList teams = (ArrayList)byYear.get(new Integer(year));
        if (teams != null) {
            return teams;
        }

        teams = new ArrayList();

        try {
            Connection con = ConnectionManager.getConnection();

            String query = "select TeamID, LongName, ShortName " +
                           "from FranchiseNameHistory " +
                           "where " + year + " between StartYear and EndYear " +
                           "order by LongName";
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                JDBCTeamImpl team = new JDBCTeamImpl(rs.getString("LongName"),
                                                     rs.getString("ShortName"));
                team.setTeamID(rs.getInt("TeamID"));
                ((Team)team).setCurrentName(rs.getString("LongName"));
                teams.add(team);
            }
            byYear.put(new Integer(year), teams);
            return teams;
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCTeam.findByYear");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return teams;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCTeam.findByYear");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return teams;
        }
    }


    public static void main(String[] Args) {
        ConnectionManager cm =
            new ConnectionManager(
                Properties.getProperty("football.database.url"));

        int result;
        System.out.println("Calling store to create a Team");
        Team rogers = new Team("Rogers", "Rog");
        result = JDBCTeam.store(rogers);
        System.out.println("store result = " + result);

        rogers = JDBCTeam.findByLongName("Rogers");
        System.out.println("ByLongName = " + rogers);
        System.out.println("  ShortName = " + rogers.getShortName());

        System.out.println("Calling store to update a Team");
        rogers.setShortName("RND");
        result = JDBCTeam.store(rogers);
        System.out.println("store result = " + result);

        Team team = JDBCTeam.findByLongName("Rogers");
        System.out.println("ByLongName = " + team);
        System.out.println("  ShortName = " + team.getShortName());

        System.out.println("Removing an existing Team");
        result = JDBCTeam.remove(rogers);
        System.out.println("remove result = " + result);

        team = JDBCTeam.findByLongName("Rogers");
        System.out.println("ByLongName = " + team);

        System.out.println("Removing non-existing Team");
        result = JDBCTeam.remove(rogers);
        System.out.println("remove result = " + result);

        team = JDBCTeam.findByLongName("Rogers");
        System.out.println("ByLongName = " + team);

        team = JDBCTeam.findByLongNameYear("Denver", 2000);
        System.out.println("ByLongNameYear = " + team);

        Collection teams99 = JDBCTeam.findByYear(1999);
        System.out.println("ByYear 1999 length = " + teams99.size());
        Iterator itr99 = teams99.iterator();
        while (itr99.hasNext()) {
            team = (Team)itr99.next();
            System.out.println("1999 team = " + team);
        }

        Collection teams98 = JDBCTeam.findByYear(1998);
        System.out.println("ByYear 1998 length = " + teams98.size());
        Iterator itr98 = teams98.iterator();
        while (itr98.hasNext()) {
            team = (Team)itr98.next();
            System.out.println("1998 team = " + team);
        }

        Team entityTeam = new Team("Imperial", "Imp");

        JDBCTeamImpl jdbcTeam = new JDBCTeamImpl("Imperial", "Imp");
        jdbcTeam.setTeamID(345);

        JDBCTeamImpl jdbcOldTeam = new JDBCTeamImpl("North Platte", "NP ");
        jdbcOldTeam.setCurrentName("Imperial");
        jdbcOldTeam.setTeamID(345);

        HashMap hashMap = new HashMap();

        hashMap.put(jdbcTeam, "JDBC in; Team out");
        System.out.println(String.valueOf(hashMap.get(entityTeam)));

        hashMap.put(jdbcTeam, "JDBC in; JDBC out");
        System.out.println(String.valueOf(hashMap.get(jdbcTeam)));

        hashMap.put(entityTeam, "Team in; Team out");
        System.out.println(String.valueOf(hashMap.get(entityTeam)));

        hashMap.put(entityTeam, "Team in; JDBC out");
        System.out.println(String.valueOf(hashMap.get(entityTeam)));

        hashMap.put(jdbcTeam, "JDBC in; Old out");
        System.out.println(String.valueOf(hashMap.get(jdbcOldTeam)));

        hashMap.put(entityTeam, "Team in; Old out");
        System.out.println(String.valueOf(hashMap.get(jdbcOldTeam)));

        hashMap.put(jdbcOldTeam, "Old in; JDBC out");
        System.out.println(String.valueOf(hashMap.get(jdbcTeam)));

        hashMap.put(jdbcOldTeam, "Old in; Team out");
        System.out.println(String.valueOf(hashMap.get(entityTeam)));
    }
}

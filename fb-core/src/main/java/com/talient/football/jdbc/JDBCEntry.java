package com.talient.football.jdbc;

import java.io.*;
import java.util.*;
import com.talient.football.entities.Entry;
import com.talient.football.entities.Entrant;
import com.talient.football.entities.Team;
import com.talient.football.entities.WeeklySchedule;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.talient.util.Properties;

public class JDBCEntry
{
    public static int store(Entry entry) {
        // Make sure everything is populated correctly
        if (entry == null) {
            System.err.println("ERROR: trying to store a null entry");
            return 0;
        }

        WeeklySchedule schedule = entry.getSchedule();
        int year = schedule.getYear();
        int week = schedule.getWeek();

        if (year == 0 || week == 0) {
            System.err.println("ERROR: trying to store a entry with an " +
                "illegal value for Year or Week");
            return 0;
        }
        if (entry.size() != schedule.size()) {
            System.err.println("ERROR: Entry size does != Schedule size");
            return 0;
        }

        // Get the EntrantID
        Entrant entrant = entry.getEntrant();
        JDBCEntrant.JDBCEntrantInfo jdbcEntrant = null;
        if (entrant instanceof JDBCEntrant.JDBCEntrantInfo) {
            jdbcEntrant = (JDBCEntrant.JDBCEntrantInfo)entrant;
        }
        else {
            jdbcEntrant = (JDBCEntrant.JDBCEntrantInfo)
                JDBCEntrant.findByUsername(entrant.getUsername());
        }

        // Remove the old entry first
        JDBCEntry.remove(entry);

        // Store the JDBCEntry
        try {
            Connection con = ConnectionManager.getConnection();

            for (int i=0; i<entry.size(); i++) {
                Team team = entry.get(i);

                if (team == null) {
                    System.err.println("ERROR: null Team in entry");
                    return 0;
                }

                JDBCTeam.JDBCTeamInfo jdbcTeam = null;
                if (team instanceof JDBCTeam.JDBCTeamInfo) {
                    jdbcTeam = (JDBCTeam.JDBCTeamInfo)team;
                }
                else {
                    jdbcTeam = (JDBCTeam.JDBCTeamInfo)
                        JDBCTeam.findByLongName(team.getLongName());
                    if (jdbcTeam == null) {
                        System.err.println("ERROR: Could not find Team: " +
                            team);
                        return 0;
                    }
                }

                String update;
                update = "insert into Entries set " +
                         "Year=" + year +
                         ", Week=" + week +
                         ", ConfidenceOrder=" + (i+1) +
                         ", EntrantID=" + jdbcEntrant.getEntrantID() +
                         ", TeamID=" + jdbcTeam.getTeamID();

                Statement stmt = con.createStatement();

                stmt.executeUpdate(update);
            }
            return entry.size();
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCEntry.store");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCEntry.store");
            System.err.println("Exception: " + e.getMessage());
            return 0;
        }
    }

    public static int remove(Entry entry) {
        // Make sure everything is populated correctly
        if (entry == null) {
            System.err.println("ERROR: trying to remove a null entry");
            return 0;
        }

        WeeklySchedule schedule = entry.getSchedule();
        if ((schedule.getYear() == 0) || (schedule.getWeek() == 0)) {
            System.err.println("ERROR: trying to remove a entry with an " +
                "illegal value for Year or Week");
            return 0;
        }

        // Get the EntrantID
        Entrant entrant = entry.getEntrant();
        JDBCEntrant.JDBCEntrantInfo jdbcEntrant = null;
        if (entrant instanceof JDBCEntrant.JDBCEntrantInfo) {
            jdbcEntrant = (JDBCEntrant.JDBCEntrantInfo)entrant;
        }
        else {
            jdbcEntrant = (JDBCEntrant.JDBCEntrantInfo)
                JDBCEntrant.findByUsername(entrant.getUsername());
            if (jdbcEntrant == null) {
                System.err.println("ERROR: trying to remove a entry with an " +
                    "illegal entrant: " + entrant);
                return 0;
            }
        }

        try {
            Connection con = ConnectionManager.getConnection();

            String delete;
            delete = "delete from Entries " +
                     "where Year=" + schedule.getYear() +
                     " and Week=" + schedule.getWeek() +
                     " and EntrantID=" + jdbcEntrant.getEntrantID();

            Statement stmt = con.createStatement();
            return stmt.executeUpdate(delete);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCEntry.remove");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCEntry.remove");
            System.err.println("Exception: " + e.getMessage());
            return 0;
        }
    }

    public static Entry findByYearWeekEntrant(int year,
                                              int week,
                                              Entrant entrant) {
        WeeklySchedule schedule = 
            WeeklySchedule.getHome().findByYearWeek(year, week);

        Entry entry = new Entry(schedule, entrant);

        try {
            Connection con = ConnectionManager.getConnection();

            int entrantID = 0;
            if (entrant instanceof JDBCEntrant.JDBCEntrantInfo) {
                entrantID =
                    ((JDBCEntrant.JDBCEntrantInfo)entrant).getEntrantID();
            }
            else {
                JDBCEntrant.JDBCEntrantInfo jdbcEntrant =
                    (JDBCEntrant.JDBCEntrantInfo)
                    JDBCEntrant.findByUsername(entrant.getUsername());
                if (jdbcEntrant != null) {
                    entrantID = jdbcEntrant.getEntrantID();
                }
                else {
                    System.err.println("ERROR: Trying to find an entry " +
                        "an illegal Entrant: " + entrant);
                    return null;
                }
            }

            String query;
            query = "select Year, Week, ConfidenceOrder, EntrantID, " +
                           "TeamID " +
                    "from Entries " +
                    "where Year=" + year +
                    " and  Week=" + week +
                    " and EntrantID=" + entrantID +
                    " order by ConfidenceOrder";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                JDBCTeam.JDBCTeamInfo jdbcTeam =
                    JDBCTeam.findByTeamID(rs.getInt("TeamID"));
                
                entry.add((Team)jdbcTeam);
            }

            if (entry.size() > 0) {
                return entry;
            }
            return null;
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCEntry.findByYearWeekEntrant");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return null;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCEntry.findByYearWeekEntrant");
            System.err.println("Exception: " + e.getMessage());
            return null;
        }
    }

    public static Collection findByYearWeek(int year, int week) {
        ArrayList entries = new ArrayList();

        WeeklySchedule schedule =
            WeeklySchedule.getHome().findByYearWeek(year, week);
        if (schedule == null) {
            System.err.println("Could not find schedule for " + year +
                " Week #" + week);
            return entries;
        }

        try {
            Connection con = ConnectionManager.getConnection();

            String query;
            query = "select Year, Week, ConfidenceOrder, EntrantID, " +
                           "TeamID " +
                    "from Entries " +
                    "where Year=" + year +
                    " and Week=" + week +
                    " order by EntrantID, ConfidenceOrder";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            Entry entry = null;
            int currentEntrant = -1;
            while (rs.next()) {
                if (rs.getInt("EntrantID") != currentEntrant) {
                    if (entry != null) {
                        entries.add(entry);
                    }
                    currentEntrant = rs.getInt("EntrantID");
                    Entrant entrant = (Entrant)
                        JDBCEntrant.findByEntrantID(currentEntrant);
                    entry = new Entry(schedule, entrant);
                }

                Team team = (Team)JDBCTeam.findByTeamID(rs.getInt("TeamID"));
                
                if (team != null) {
                    entry.add(team);
                }
            }
            if (entry != null) {
                entries.add(entry);
            }

            return entries;
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCEntry.findByYearWeek");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return entries;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCEntry.findByYearWeek");
            System.err.println("Exception: " + e.getMessage());
            return entries;
        }
    }


    public static void main(String[] Args) {
        WeeklySchedule.setHome(new JDBCWeeklySchedule());
        ConnectionManager cm =
            new ConnectionManager(
                Properties.getProperty("football.database.url"));

        int result;

        Team atl = JDBCTeam.findByLongName("Atlanta");
        Team buf = JDBCTeam.findByLongName("Buffalo");
        Team cle = JDBCTeam.findByLongName("Cleveland");
        Team dal = JDBCTeam.findByLongName("Dallas");
        Team gb = JDBCTeam.findByLongName("Green Bay");
        Team ks = JDBCTeam.findByLongName("Kansas City");
        Team mia = JDBCTeam.findByLongName("Miami");
        Team min = JDBCTeam.findByLongName("Minnesota");
        Team ne = JDBCTeam.findByLongName("New England");
        Team no = JDBCTeam.findByLongName("New Orleans");
        Team nyg = JDBCTeam.findByLongName("NY Giants");
        Team oak = JDBCTeam.findByLongName("Oakland");
        Team pit = JDBCTeam.findByLongName("Pittsburgh");
        Team stl = JDBCTeam.findByLongName("Saint Louis");
        Team was = JDBCTeam.findByLongName("Washington");

        Team den = JDBCTeam.findByLongName("Denver");

        Entrant bugs = new Entrant("Bugs Bunny", "bugs@talient.com");
        JDBCEntrant.store(bugs);
        Entrant daffy = new Entrant("Daffy Duck", "daffy@talient.com");
        JDBCEntrant.store(daffy);

        WeeklySchedule schedule =
            WeeklySchedule.getHome().findByYearWeek(2000, 1);

        Entry daffyEntry = new Entry(schedule, daffy);
        daffyEntry.add(atl);
        daffyEntry.add(buf);
        daffyEntry.add(cle);
        daffyEntry.add(dal);
        daffyEntry.add(gb);
        daffyEntry.add(ks);
        daffyEntry.add(mia);
        daffyEntry.add(min);
        daffyEntry.add(ne);
        daffyEntry.add(no);
        daffyEntry.add(nyg);
        daffyEntry.add(oak);
        daffyEntry.add(pit);
        daffyEntry.add(stl);
        daffyEntry.add(was);
        System.out.println("\nBefore daffyEntry store():\n" + daffyEntry);
        result = JDBCEntry.store(daffyEntry);
        System.out.println("After daffyEntry store(): result=" + result);

        System.out.println("Entrant daffy hasEntries() = " + 
            JDBCEntrant.hasEntries(daffy));

        System.out.println("Entrant bugs hasEntries() = " + 
            JDBCEntrant.hasEntries(bugs));

        Entry bugsEntry = new Entry(schedule, bugs);
        bugsEntry.add(atl);
        bugsEntry.add(buf);
        bugsEntry.add(cle);
        bugsEntry.add(dal);
        bugsEntry.add(gb);
        bugsEntry.add(ks);
        bugsEntry.add(mia);
        bugsEntry.add(min);
        bugsEntry.add(ne);
        bugsEntry.add(no);
        bugsEntry.add(nyg);
        bugsEntry.add(oak);
        bugsEntry.add(pit);
        bugsEntry.add(den);
        bugsEntry.add(was);
        System.out.println("\nBefore bugsEntry store():\n" + bugsEntry);
        result = JDBCEntry.store(bugsEntry);
        System.out.println("After bugsEntry store(): result=" + result);

        Entry entry1 = JDBCEntry.findByYearWeekEntrant(2000, 1, bugs);
        System.out.println("\nAfter findByYearWeekEntrant(bugs):\n" + entry1);

        System.out.println("\nBefore findByYearWeek():\n");
        Collection entries = JDBCEntry.findByYearWeek(2000, 1);
        Iterator iter = entries.iterator();
        int cnt = 1;
        while (iter.hasNext()) {
            Entry entry = (Entry)iter.next();
            System.out.println("Entry #" + cnt++ + "\n" + entry);
        }

        System.out.println("\nBefore bugsEntry remove():\n");
        result = JDBCEntry.remove(bugsEntry);
        System.out.println("After bugsEntry remove(): result=" + result);

        System.out.println("\nBefore daffyEntry remove():\n");
        result = JDBCEntry.remove(daffyEntry);
        System.out.println("After daffyEntry remove(): result=" + result);

        JDBCEntrant.remove(bugs);
        JDBCEntrant.remove(daffy);
    }
}

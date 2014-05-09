package com.talient.football.jdbc;

import java.io.*;
import java.util.*;
import com.talient.football.entities.Entrant;
import com.talient.football.entities.Alias;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.talient.util.Properties;

public class JDBCEntrant
{
    protected interface JDBCEntrantInfo {
        public void setEntrantID(int entrantID);
        public int getEntrantID();
    }

    private static class JDBCEntrantImpl extends Entrant
                                         implements JDBCEntrantInfo {
        int id;

        public JDBCEntrantImpl(String username, String email, boolean active) {
            super(username, email, active);
        }

        public JDBCEntrantImpl(String username, String email) {
            super(username, email);
        }
        public void setEntrantID(int entrantID)    { id = entrantID; }
        public int  getEntrantID()                 { return id; }
    }

    private static HashMap byEntrantID = new HashMap();
    private static HashMap byUsername = new HashMap();
    private static HashMap aliasByUsername = new HashMap();

    public static int store(Entrant entrant) {
        return store(entrant, null);
    }

    public static int store(Entrant entrant, Alias alias) {
        try {
            Connection con = ConnectionManager.getConnection();

            Entrant found = JDBCEntrant.findByUsername(entrant.getUsername());

            String update;
            if (entrant instanceof JDBCEntrantImpl || found != null) {
                update = "update Entrants set " +
                         "ContactEmail='" + entrant.getContactEmail() + "'" +
                         ", Active=" + (entrant.getActive()?1:0) +
                         ", WeeklyEntry=" + (entrant.getWeeklyEntry()?1:0) +
                         ", GameOrderedRecap=" + (entrant.getGameOrderedRecap()?1:0) +
                         ", ConfOrderedRecap=" + (entrant.getConfOrderedRecap()?1:0) +
                         ", WeeklyStats=" + (entrant.getWeeklyStats()?1:0) +
                         ", WeeklyResult=" + (entrant.getWeeklyResult()?1:0) +
                         ", Standings=" + (entrant.getStandings()?1:0) +
                         ", NotifyEarly=" + (entrant.getNotifyEarly()?1:0) +
                         ", NotifyMedium=" + (entrant.getNotifyMedium()?1:0) +
                         ", NotifyLate=" + (entrant.getNotifyLate()?1:0);
            }
            else {
                update = "insert into Entrants set " +
                         "Username='" + entrant.getUsername() + "'" +
                         ", ContactEmail='" + entrant.getContactEmail() + "'" +
                         ", Active=" + (entrant.getActive()?1:0) +
                         ", WeeklyEntry=" + (entrant.getWeeklyEntry()?1:0) +
                         ", GameOrderedRecap=" + (entrant.getGameOrderedRecap()?1:0) +
                         ", ConfOrderedRecap=" + (entrant.getConfOrderedRecap()?1:0) +
                         ", WeeklyStats=" + (entrant.getWeeklyStats()?1:0) +
                         ", WeeklyResult=" + (entrant.getWeeklyResult()?1:0) +
                         ", Standings=" + (entrant.getStandings()?1:0) +
                         ", NotifyEarly=" + (entrant.getNotifyEarly()?1:0) +
                         ", NotifyMedium=" + (entrant.getNotifyMedium()?1:0) +
                         ", NotifyLate=" + (entrant.getNotifyLate()?1:0);
            }

            if (found != null) {
                update += " where EntrantID=" +
                              ((JDBCEntrantInfo)found).getEntrantID();
            }
            else if (entrant instanceof JDBCEntrantImpl) {
                update += " where EntrantID=" +
                              ((JDBCEntrantInfo)entrant).getEntrantID();
            }

            Statement stmt = con.createStatement();
            stmt.executeUpdate(update);

            Entrant jdbcEntrant =
                JDBCEntrant.findByUsername(entrant.getUsername());

            Alias foundAlias =
                JDBCEntrant.findAliasByUsername(entrant.getUsername());
            String password = "";
            if (alias == null) {
                foundAlias =
                    JDBCEntrant.findAliasByUsername(entrant.getUsername());
                if (foundAlias != null) {
                    password = foundAlias.getPassword();
                }
            }
            else {
                password = alias.getPassword();
            }

            Timestamp lastLog = new Timestamp(new Date().getTime());

            if (foundAlias != null) {
                update = "update Aliases set " +
                         "Password='" + password + "'" +
                         ", lastLog='" + lastLog + "'";
            }
            else {
                update = "insert into Aliases set " +
                         "Username='" + entrant.getUsername() + "'" +
                         ", Password='" + password + "'" +
                         ", lastLog='" + lastLog + "'";
            }
            if (foundAlias != null) {
                update += " where EntrantID=" +
                      ((JDBCEntrantImpl)foundAlias.getEntrant()).getEntrantID();
            }
            else if (entrant instanceof JDBCEntrantImpl) {
                update += " where EntrantID=" +
                              ((JDBCEntrantImpl)entrant).getEntrantID();
            }
            else { // this is an insert
                update += ", EntrantID=" +
                              ((JDBCEntrantImpl)jdbcEntrant).getEntrantID();
            }

            stmt = con.createStatement();
            byUsername.remove(jdbcEntrant.getUsername());
            byEntrantID.remove(Integer.toString(((JDBCEntrantImpl)jdbcEntrant).getEntrantID()));
            return stmt.executeUpdate(update);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in Entrant.store");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in Entrant.store");
            System.err.println("Exception: " + e.getMessage());
            return 0;
        }
    }

    public static int remove(Entrant entrant) {
        // Cannot remove an entrant if they have Entries or WeeklyResults
        if (hasEntries(entrant) || hasWeeklyResults(entrant)) {
            return 0;
        }

        try {
            Connection con = ConnectionManager.getConnection();

            Entrant jdbcEntrant = null;
            if (entrant instanceof JDBCEntrantInfo) {
                jdbcEntrant = entrant;
            }
            else {
                jdbcEntrant = JDBCEntrant.findByUsername(entrant.getUsername());
                if (jdbcEntrant == null) {
                    return 0;
                }
            }

            String del = "delete from Entrants " +
                            "where EntrantID=" +
                                ((JDBCEntrantInfo)jdbcEntrant).getEntrantID();

            Statement stmt = con.createStatement();

            stmt.executeUpdate(del);
            byUsername.remove(jdbcEntrant.getUsername());
            byEntrantID.remove(Integer.toString(((JDBCEntrantInfo)jdbcEntrant).getEntrantID()));

            del = "delete from Aliases " +
                     "where EntrantID=" +
                        ((JDBCEntrantInfo)jdbcEntrant).getEntrantID();

            stmt = con.createStatement();
            aliasByUsername.remove(jdbcEntrant.getUsername());

            return stmt.executeUpdate(del);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in Entrant.remove");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in Entrant.remove");
            System.err.println("Exception: " + e.getMessage());
            return 0;
        }
    }

    public static boolean hasEntries(Entrant entrant) {
        try {
            Connection con = ConnectionManager.getConnection();

            int entrantID = 0;
            if (entrant instanceof JDBCEntrant.JDBCEntrantInfo) {
                entrantID = ((JDBCTeam.JDBCTeamInfo)entrant).getTeamID();
            }
            else {
                JDBCEntrant.JDBCEntrantInfo jdbcEntrant =
                    (JDBCEntrant.JDBCEntrantInfo)
                    JDBCEntrant.findByUsername(entrant.getUsername());
                if (jdbcEntrant != null) {
                    entrantID = jdbcEntrant.getEntrantID();
                }
                else {
                    return false;
                }
            }

            String query;
            query = "select Year, Week, ConfidenceOrder, EntrantID, " +
                           "TeamID " +
                    "from Entries " +
                    "where EntrantID=" + entrantID +
                    " order by Year, Week, ConfidenceOrder";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                return true;
            }

            return false;
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCEntrant.hasEntries");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return false;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCEntrant.hasEntries");
            System.err.println("Exception: " + e.getMessage());
            return false;
        }
    }

    public static boolean hasWeeklyResults(Entrant entrant) {
        try {
            Connection con = ConnectionManager.getConnection();

            int entrantID = 0;
            if (entrant instanceof JDBCEntrant.JDBCEntrantInfo) {
                entrantID = ((JDBCTeam.JDBCTeamInfo)entrant).getTeamID();
            }
            else {
                JDBCEntrant.JDBCEntrantInfo jdbcEntrant =
                    (JDBCEntrant.JDBCEntrantInfo)
                    JDBCEntrant.findByUsername(entrant.getUsername());
                if (jdbcEntrant != null) {
                    entrantID = jdbcEntrant.getEntrantID();
                }
                else {
                    return false;
                }
            }

            String query;
            query = "select Year, Week, EntrantID, Score, Bonus " +
                    "from WeeklyResults " +
                    "where EntrantID=" + entrantID +
                    " order by Year, Week";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                return true;
            }

            return false;
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCEntrant.hasWeeklyResults");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return false;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCEntrant.hasWeeklyResults");
            System.err.println("Exception: " + e.getMessage());
            return false;
        }
    }

    public static JDBCEntrantInfo findByEntrantID(int id) {
        JDBCEntrantImpl entrant =
            (JDBCEntrantImpl)byEntrantID.get(new Integer(id));
        if (entrant != null) {
            return entrant;
        }
        try {
            Connection con = ConnectionManager.getConnection();

            String query = "select EntrantID, Username, ContactEmail, " +
                         "Active, WeeklyEntry, GameOrderedRecap, " +
                         "ConfOrderedRecap, WeeklyStats, WeeklyResult, " +
                         "Standings, NotifyEarly, NotifyMedium, NotifyLate " +
                         "from Entrants " +
                         "where EntrantID=" + id;
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                boolean active = (rs.getInt("Active") == 0 ? false : true);
                entrant = new JDBCEntrantImpl(rs.getString("Username"),
                                              rs.getString("ContactEmail"),
                                              active);
                entrant.setWeeklyEntry(
                    rs.getInt("WeeklyEntry") == 0 ? false : true);
                entrant.setGameOrderedRecap(
                    rs.getInt("GameOrderedRecap") == 0 ? false : true);
                entrant.setConfOrderedRecap(
                    rs.getInt("ConfOrderedRecap") == 0 ? false : true);
                entrant.setWeeklyStats(
                    rs.getInt("WeeklyStats") == 0 ? false : true);
                entrant.setWeeklyResult(
                    rs.getInt("WeeklyResult") == 0 ? false : true);
                entrant.setStandings(
                    rs.getInt("Standings") == 0 ? false : true);
                entrant.setNotifyEarly(
                    rs.getInt("NotifyEarly") == 0 ? false : true);
                entrant.setNotifyMedium(
                    rs.getInt("NotifyMedium") == 0 ? false : true);
                entrant.setNotifyLate(
                    rs.getInt("NotifyLate") == 0 ? false : true);
                entrant.setEntrantID(rs.getInt("EntrantID"));
                byEntrantID.put(new Integer(id), entrant);
                return entrant;
            }
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCEntrantImpl.findByEntrantID");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return null;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCEntrantImpl.findByEntrantID");
            System.err.println("Exception: " + e.getMessage());
            return null;
        }

        return null;
    }

    public static Entrant findByUsername(String username) {
        JDBCEntrantImpl entrant =
            (JDBCEntrantImpl)byUsername.get(username);
        if (entrant != null) {
            return entrant;
        }
        try {
            Connection con = ConnectionManager.getConnection();

            String query = "select EntrantID, Username, ContactEmail, " +
                         "Active, WeeklyEntry, GameOrderedRecap, " +
                         "ConfOrderedRecap, WeeklyStats, WeeklyResult, " +
                         "Standings, NotifyEarly, NotifyMedium, NotifyLate " +
                         "from Entrants " +
                         "where Username='" + username + "'";
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                boolean active = (rs.getInt("Active") == 0 ? false : true);
                entrant = new JDBCEntrantImpl(rs.getString("Username"),
                                              rs.getString("ContactEmail"),
                                              active);
                entrant.setEntrantID(rs.getInt("EntrantID"));
                entrant.setWeeklyEntry(
                    rs.getInt("WeeklyEntry") == 0 ? false : true);
                entrant.setGameOrderedRecap(
                    rs.getInt("GameOrderedRecap") == 0 ? false : true);
                entrant.setConfOrderedRecap(
                    rs.getInt("ConfOrderedRecap") == 0 ? false : true);
                entrant.setWeeklyStats(
                    rs.getInt("WeeklyStats") == 0 ? false : true);
                entrant.setWeeklyResult(
                    rs.getInt("WeeklyResult") == 0 ? false : true);
                entrant.setStandings(
                    rs.getInt("Standings") == 0 ? false : true);
                entrant.setNotifyEarly(
                    rs.getInt("NotifyEarly") == 0 ? false : true);
                entrant.setNotifyMedium(
                    rs.getInt("NotifyMedium") == 0 ? false : true);
                entrant.setNotifyLate(
                    rs.getInt("NotifyLate") == 0 ? false : true);
                entrant.setEntrantID(rs.getInt("EntrantID"));
                byUsername.put(username, entrant);
                return entrant;
            }
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in Entrant.findByLongName");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return null;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in Entrant.findByLongName");
            System.err.println("Exception: " + e.getMessage());
            return null;
        }

        return null;
    }

    public static Alias findAliasByUsername(String username) {
        Alias alias = (Alias)aliasByUsername.get(username);
        if (alias != null) {
            return alias;
        }
        try {
            Connection con = ConnectionManager.getConnection();

            String query = "select EntrantID, Username, Password, LastLog " +
                           "from Aliases " +
                           "where Username='" + username + "'";
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            alias = null;
            while (rs.next()) {
                Entrant entrant = (Entrant)
                    JDBCEntrant.findByEntrantID(rs.getInt("EntrantID"));
                alias = new Alias(entrant,
                                  rs.getString("Username"),
                                  rs.getString("Password"));
                alias.setLastLog(rs.getTimestamp("LastLog"));
                aliasByUsername.put(username, alias);
                return alias;
            }
            return alias;
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in Entrant.findByLongName");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return null;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in Entrant.findByLongName");
            System.err.println("Exception: " + e.getMessage());
            return null;
        }
    }

    public static int storeAlias(Entrant entrant,
                                 Alias alias) {
        JDBCEntrantInfo jdbcEntrant = null;
        if (entrant instanceof JDBCEntrantInfo) {
            jdbcEntrant = (JDBCEntrantInfo)entrant;
        }
        else {
            Entrant found = findByUsername(entrant.getUsername());
            if (found == null) {
                return 0;
            }
            else {
                jdbcEntrant = (JDBCEntrantInfo)found;
            }
        }

        try {
            Connection con = ConnectionManager.getConnection();

            String query = "select EntrantID, Username, Password, LastLog " +
                           "from Aliases " +
                           "where EntrantID=" + jdbcEntrant.getEntrantID() +
                           " and Username='" + alias.getUsername() + "'";
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            boolean found = false;
            while (rs.next()) {
                found = true;
            }
            
            Timestamp lastLog = new Timestamp(new Date().getTime());

            String update;
            if (found) {
                update = "update Aliases set " +
                         "Password='" + alias.getPassword() +
                         "', EntrantID=" + jdbcEntrant.getEntrantID() +
                         ", LastLog='" + lastLog + "'";
            }
            else {
                update = "insert into Aliases set " +
                         "Username='" + alias.getUsername() +
                         "', Password='" + alias.getPassword() +
                         "', EntrantID=" + jdbcEntrant.getEntrantID() +
                         ", LastLog='" + lastLog + "'";
            }
            if (found) {
                update += " where EntrantID=" + jdbcEntrant.getEntrantID();
            }

            aliasByUsername.remove(alias.getUsername());
            stmt = con.createStatement();
            return stmt.executeUpdate(update);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in Entrant.storeAlias");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in Entrant.storeAlias");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public static int removeAlias(Alias alias) {
        try {
            Connection con = ConnectionManager.getConnection();

            String del;
            del = "delete Aliases where " +
                     "Username='" + alias.getUsername() + "'";

            Statement stmt = con.createStatement();
            aliasByUsername.remove(alias.getUsername());
            return stmt.executeUpdate(del);
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in Entrant.removeAlias");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return 0;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in Entrant.removeAlias");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }


    public static Collection findAliases(Entrant entrant) {
        ArrayList aliases = new ArrayList();

        JDBCEntrantInfo jdbcEntrant = null;
        if (entrant instanceof JDBCEntrantInfo) {
            jdbcEntrant = (JDBCEntrantInfo)entrant;
        }
        else {
            Entrant found = findByUsername(entrant.getUsername());
            if (found == null) {
                return aliases;
            }
            else {
                jdbcEntrant = (JDBCEntrantInfo)found;
            }
        }

        try {
            Connection con = ConnectionManager.getConnection();

            String query = "select EntrantID, Username, Password, LastLog " +
                           "from Aliases " +
                           "where EntrantID=" + jdbcEntrant.getEntrantID() +
                           " order by Username";
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Alias alias = new Alias((Entrant)jdbcEntrant,
                                        rs.getString("Username"),
                                        rs.getString("Password"));
                alias.setLastLog(rs.getTimestamp("LastLog"));
                aliases.add(alias);
            }
            return aliases;
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in Entrant.findAliases");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return aliases;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in Entrant.findAliases");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return aliases;
        }
    }


    public static Collection findActive() {
        ArrayList entrants = new ArrayList();

        try {
            Connection con = ConnectionManager.getConnection();

            String query = "select EntrantID, Username, ContactEmail, " +
                         "Active, WeeklyEntry, GameOrderedRecap, " +
                         "ConfOrderedRecap, WeeklyStats, WeeklyResult, " +
                         "Standings, NotifyEarly, NotifyMedium, NotifyLate " +
                         "from Entrants " +
                         "where Active=1";
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                JDBCEntrantImpl entrant = null;
                boolean active = (rs.getInt("Active") == 0 ? false : true);
                entrant = new JDBCEntrantImpl(rs.getString("Username"),
                                              rs.getString("ContactEmail"),
                                              active);
                entrant.setWeeklyEntry(
                    rs.getInt("WeeklyEntry") == 0 ? false : true);
                entrant.setGameOrderedRecap(
                    rs.getInt("GameOrderedRecap") == 0 ? false : true);
                entrant.setConfOrderedRecap(
                    rs.getInt("ConfOrderedRecap") == 0 ? false : true);
                entrant.setWeeklyStats(
                    rs.getInt("WeeklyStats") == 0 ? false : true);
                entrant.setWeeklyResult(
                    rs.getInt("WeeklyResult") == 0 ? false : true);
                entrant.setStandings(
                    rs.getInt("Standings") == 0 ? false : true);
                entrant.setNotifyEarly(
                    rs.getInt("NotifyEarly") == 0 ? false : true);
                entrant.setNotifyMedium(
                    rs.getInt("NotifyMedium") == 0 ? false : true);
                entrant.setNotifyLate(
                    rs.getInt("NotifyLate") == 0 ? false : true);
                entrant.setEntrantID(rs.getInt("EntrantID"));
                entrants.add(entrant);
            }
            return entrants;
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in Entrant.findActive");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VenderError:  " + e.getErrorCode());
            return entrants;
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in Entrant.findActive");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return entrants;
        }
    }


    public static void main(String[] Args) {
        ConnectionManager cm =
            new ConnectionManager(
                Properties.getProperty("football.database.url"));

        int result;
        System.out.println("Calling store to create a Entrant");
        Entrant nick = new Entrant("Bugs Bunny", "bugs@talient.com");
        result = JDBCEntrant.store(nick);
        System.out.println("store result = " + result);

        Collection aliases = JDBCEntrant.findAliases(nick);
        Iterator iter = aliases.iterator();
        Alias alias = null;
        while (iter.hasNext()) {
            alias = (Alias)iter.next();
        }

        System.out.println("Calling storeAlias to set the password");
        alias.setPassword("newpw");
        result = JDBCEntrant.storeAlias(nick, alias);
        System.out.println("store result = " + result);

        System.out.println("Calling storeAlias to add nlonsky");
        Alias alias1 = new Alias(nick, "nlonsky", "nlonskypw");
        result = JDBCEntrant.storeAlias(nick, alias1);
        System.out.println("store result = " + result);

        aliases = JDBCEntrant.findAliases(nick);
        iter = aliases.iterator();
        while (iter.hasNext()) {
            alias = (Alias)iter.next();
            System.out.println("Alias: "+ alias + " - " + alias.getPassword());
        }

        System.out.println("Calling store to update a Entrant");
        nick.setContactEmail("bugs@lonsky.com");
        result = JDBCEntrant.store(nick);
        System.out.println("store result = " + result);

        System.out.println("Calling findByUsername");
        Entrant entrant = JDBCEntrant.findByUsername("Bugs Bunny");
        System.out.println("  Username = " + entrant);
        System.out.println("     Email = " + entrant.getContactEmail());

        System.out.println("Calling findAliasByUsername");
        alias = JDBCEntrant.findAliasByUsername("nlonsky");
        System.out.println("  Username = " + alias);
        System.out.println("  Password = " + alias.getPassword());

        System.out.println("Removing an existing Entrant");
        result = JDBCEntrant.remove(nick);
        System.out.println("remove result = " + result);

        entrant = JDBCEntrant.findByUsername("Bugs Bunny");
        System.out.println("  Username = " + entrant);

        System.out.println("Removing non-existing Entrant");
        result = JDBCEntrant.remove(nick);
        System.out.println("remove result = " + result);

        entrant = JDBCEntrant.findByUsername("Bugs Bunny");
        System.out.println("  Username = " + entrant);

        Entrant entityEntrant = new Entrant("Daffy", "daffy@talient.com");

        JDBCEntrantImpl jdbcEntrant =
            new JDBCEntrantImpl("Daffy", "daffy@talient.com", true);
        jdbcEntrant.setEntrantID(345);

        JDBCEntrantImpl jdbcOldEntrant =
            new JDBCEntrantImpl("Donnald", "donnald@hsacorp.net");
        jdbcOldEntrant.setEntrantID(345);

        HashMap hashMap = new HashMap();

        hashMap.put(jdbcEntrant, "JDBC in; Entrant out");
        System.out.println(String.valueOf(hashMap.get(entityEntrant)));

        hashMap.put(jdbcEntrant, "JDBC in; JDBC out");
        System.out.println(String.valueOf(hashMap.get(jdbcEntrant)));

        hashMap.put(entityEntrant, "Entrant in; Entrant out");
        System.out.println(String.valueOf(hashMap.get(entityEntrant)));

        hashMap.put(entityEntrant, "Entrant in; JDBC out");
        System.out.println(String.valueOf(hashMap.get(entityEntrant)));

        hashMap.put(jdbcEntrant, "JDBC in; Old out");
        System.out.println(String.valueOf(hashMap.get(jdbcOldEntrant)));

        hashMap.put(entityEntrant, "Entrant in; Old out");
        System.out.println(String.valueOf(hashMap.get(jdbcOldEntrant)));

        hashMap.put(jdbcOldEntrant, "Old in; JDBC out");
        System.out.println(String.valueOf(hashMap.get(jdbcEntrant)));

        hashMap.put(jdbcOldEntrant, "Old in; Entrant out");
        System.out.println(String.valueOf(hashMap.get(entityEntrant)));
    }
}

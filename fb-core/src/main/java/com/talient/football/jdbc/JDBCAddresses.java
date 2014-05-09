package com.talient.football.jdbc;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.talient.football.reports.Addresses;

public class JDBCAddresses implements Addresses.AddressesHome
{

    public Set findActive() {

        return findByEmailFlag("Active");
    }

    public Set findWeeklyEntry() {

        return findByEmailFlag("WeeklyEntry");
    }

    public Set findGameOrderedRecap() {

        return findByEmailFlag("GameOrderedRecap");
    }

    public Set findWeeklyStats() {

        return findByEmailFlag("WeeklyStats");
    }

    public Set findWeeklyResult() {

        return findByEmailFlag("WeeklyResult");
    }

    public Set findStandings() {

        return findByEmailFlag("Standings");
    }

    private Set findByEmailFlag(String emailFlag) {

        Set addresses = new HashSet();

        try {
            Connection con = ConnectionManager.getConnection();

            String where = "";
            if (emailFlag.equals("Active")) {
                where = "Active = 1";
            }
            else {
                where = "Active = 1 and " + emailFlag + " = 1";
            }
            String query =
                "select ContactEmail from Entrants where " +
                 where +
                 " and ContactEmail like '%@%';";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String address = rs.getString(1);
                if (isValidEmail(address)) {
                    addresses.add(address);
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCAddresses.findActive");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VendorError:  " + e.getErrorCode());
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCAddresses.findActive");
            System.err.println("Exception: " + e.getMessage());
        }

        return addresses;
    }

    public Set findByYearWeek(int year, int week) {

        Set addresses = new HashSet();

        try {
            Connection con = ConnectionManager.getConnection();

            String query =
                "select distinct Entrants.ContactEmail " +
                "from Entrants, Entries " +
                "where Entrants.EntrantID = Entries.EntrantID" +
                " and Entries.Year = " + year +
                " and Entries.Week = " + week +
                " and Entrants.ContactEmail like '%@%';";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String address = rs.getString(1);
                if (isValidEmail(address)) {
                    addresses.add(address);
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Caught SQL Exception " +
                               "in JDBCAddresses.findByYearWeek");
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState:     " + e.getSQLState());
            System.err.println("VendorError:  " + e.getErrorCode());
        }
        catch (Exception e) {
            System.err.println("Caught Exception " +
                               "in JDBCAddresses.findByYearWeek");
            System.err.println("Exception: " + e.getMessage());
        }

        return addresses;
    }

    public Set findMissingByYearWeek(int year, int week) {
        Set addresses = findActive();
        addresses.removeAll(findByYearWeek(year, week));
        return addresses;
    }

    public Set findNotifyEarly(int year, int week) {
        Set addresses = findByEmailFlag("NotifyEarly");
        addresses.removeAll(findByYearWeek(year, week));
        return addresses;
    }

    public Set findNotifyMedium(int year, int week) {
        Set addresses = findByEmailFlag("NotifyMedium");
        addresses.removeAll(findByYearWeek(year, week));
        return addresses;
    }

    public Set findNotifyLate(int year, int week) {
        Set addresses = findByEmailFlag("NotifyLate");
        addresses.removeAll(findByYearWeek(year, week));
        return addresses;
    }

    public static void main(String[] Args) {

        Addresses.setHome(new JDBCAddresses());

        System.out.println("Getting active addresses...");
        Iterator i = Addresses.getHome().findActive().iterator();
        while (i.hasNext()) {
            System.out.println(i.next());
        }

        System.out.println("Getting addresses for Year 2000, Week 1...");
        i = Addresses.getHome().findByYearWeek(2000, 1).iterator();
        while (i.hasNext()) {
            System.out.println(i.next());
        }

        System.out.println("Getting missing addresses for Year 2000, Week 1...");
        i = Addresses.getHome().findMissingByYearWeek(2000, 1).iterator();
        while (i.hasNext()) {
            System.out.println(i.next());
        }
    }

    private boolean isValidEmail(String address) {
        return address.indexOf("@") >= 0;
    }
}

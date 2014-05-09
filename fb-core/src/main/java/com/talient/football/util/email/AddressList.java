package com.talient.football.util.email;

import java.util.Iterator;

import com.talient.football.reports.Addresses;
import com.talient.football.jdbc.JDBCHomes;

/**
 * Dump the Address reports to stdout for use by email utility
 * scripts.  If invoked with no arguments, will list the email
 * addresses of all active entrants in the database.  With two
 * arguments, will list the email addresses of entrants who have
 * submitted an entry for the given year and week.  With three
 * arguments, will list the email addresses of all active entrants
 * who have NOT submitted an entry for the given year and week.
 * The value of the third argument isn't actually checked, its
 * mere presence indicates that the missing people should be listed.
 * <p>
 * @author Steve Schmadeke
 * @version 1.0
 */
public class AddressList {

    private AddressList() {};

    public static void main(String[] args) {
        String usage = "com.talient.football.util.email.AddressList [weeklyEntry | weeklyStats | weeklyRecap | weeklyResult | standings | missing | current | notifyEarly | notifyMedium | notifyLate [ <year> <week> ] ]";
 
        int year = 0;
        int week = 0;

        JDBCHomes.setHomes();

        if (args.length == 0) {
            Iterator i = Addresses.getHome().
                findActive().iterator();
            while (i.hasNext()) { System.out.println(i.next()); }
        } else if (args.length == 1) {
            if (args[0].equals("weeklyEntry")) {
                Iterator i = Addresses.getHome().
                    findWeeklyEntry().iterator();
                while (i.hasNext()) { System.out.println(i.next()); }
            } else if (args[0].equals("weeklyStats")) {
                Iterator i = Addresses.getHome().
                    findWeeklyStats().iterator();
                while (i.hasNext()) { System.out.println(i.next()); }
            } else if (args[0].equals("weeklyRecap")) {
                Iterator i = Addresses.getHome().
                    findGameOrderedRecap().iterator();
                while (i.hasNext()) { System.out.println(i.next()); }
            } else if (args[0].equals("weeklyResult")) {
                Iterator i = Addresses.getHome().
                    findWeeklyResult().iterator();
                while (i.hasNext()) { System.out.println(i.next()); }
            } else if (args[0].equals("standings")) {
                Iterator i = Addresses.getHome().
                    findStandings().iterator();
                while (i.hasNext()) { System.out.println(i.next()); }
            } else {
                System.err.println(usage);
                System.exit(1);
            }
        } else if (args.length == 3) {
            try {
                year = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid year:" + args[1]);
                System.err.println(usage);
                System.exit(1);
            }

            try {
                week = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid week:" + args[2]);
                System.err.println(usage);
                System.exit(1);
            }
            if (args[0].equals("notifyEarly")) {
                Iterator i = Addresses.getHome().
                    findNotifyEarly(year, week).iterator();
                while (i.hasNext()) { System.out.println(i.next()); }
            } else if (args[0].equals("notifyMedium")) {
                Iterator i = Addresses.getHome().
                    findNotifyMedium(year, week).iterator();
                while (i.hasNext()) { System.out.println(i.next()); }
            } else if (args[0].equals("notifyLate")) {
                Iterator i = Addresses.getHome().
                    findNotifyLate(year, week).iterator();
                while (i.hasNext()) { System.out.println(i.next()); }
            } else if (args[0].equals("missing")) {
                Iterator i = Addresses.getHome().
                    findMissingByYearWeek(year, week).iterator();
                while (i.hasNext()) { System.out.println(i.next()); }
            } else if (args[0].equals("current")) {
                Iterator i = Addresses.getHome().
                    findByYearWeek(year, week).iterator();
                while (i.hasNext()) { System.out.println(i.next()); }
            } else {
                System.err.println(usage);
                System.exit(1);
            }
        } else {
            System.err.println(usage);
            System.exit(1);
        }
    }
}

// SCCS ID: %Z% %G% %I% %M%

package com.talient.football.reports;

import java.util.Set;

/**
 * Report of email addresses.  This class has only static
 * methods to return the implementation of the Home interface.
 * It has no instance variables or methods.  The Home interface
 * includes methods to find the email addresses of all currently
 * active entrants, all entrants that have submitted an entry
 * for a given year and week and all active entrants that have
 * not submitted an entry for a given year and week.
 * <p>
 * The default implementation of the Home interface throws 
 * an UnsupportedOperationException for all methods.
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class Addresses {

    public static interface AddressesHome {

        public Set findActive();

        public Set findWeeklyEntry();

        public Set findGameOrderedRecap();

        public Set findWeeklyStats();

        public Set findWeeklyResult();

        public Set findStandings();

        public Set findByYearWeek(int year, int week);

        public Set findMissingByYearWeek(int year, int week);

        public Set findNotifyEarly(int year, int week);

        public Set findNotifyMedium(int year, int week);

        public Set findNotifyLate(int year, int week);

    }

    public static AddressesHome getHome() {
        return home;
    }

    public static void setHome(AddressesHome home) {
        Addresses.home = home;
    }

    private static AddressesHome home = new AddressesHome() {
        public Set findActive() {
            throw new UnsupportedOperationException(
                "findActive() not supported by default Addresses Home interface");
        }
        public Set findWeeklyEntry() {
            throw new UnsupportedOperationException(
                "findWeeklyEntry() not supported by default Addresses Home interface");
        }
        public Set findGameOrderedRecap() {
            throw new UnsupportedOperationException(
                "findGameOrderedRecap() not supported by default Addresses Home interface");
        }
        public Set findWeeklyStats() {
            throw new UnsupportedOperationException(
                "findWeeklyStats() not supported by default Addresses Home interface");
        }
        public Set findWeeklyResult() {
            throw new UnsupportedOperationException(
                "findResult() not supported by default Addresses Home interface");
        }
        public Set findStandings() {
            throw new UnsupportedOperationException(
                "findStandings() not supported by default Addresses Home interface");
        }
        public Set findByYearWeek(int year, int week) {
            throw new UnsupportedOperationException(
                "findByYearWeek() not supported by default Addresses Home interface");
        }
        public Set findMissingByYearWeek(int year, int week) {
            throw new UnsupportedOperationException(
                "findMissingByYearWeek() not supported by default Addresses Home interface");
        }
        public Set findNotifyEarly(int year, int week) {
            throw new UnsupportedOperationException(
                "findNotifyEarly() not supported by default Addresses Home interface");
        }
        public Set findNotifyMedium(int year, int week) {
            throw new UnsupportedOperationException(
                "findNotifyMedium() not supported by default Addresses Home interface");
        }
        public Set findNotifyLate(int year, int week) {
            throw new UnsupportedOperationException(
                "findNotifyLate() not supported by default Addresses Home interface");
        }
    };
}

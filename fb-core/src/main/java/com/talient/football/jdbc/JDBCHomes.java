package com.talient.football.jdbc;

import com.talient.football.reports.Addresses;
import com.talient.football.reports.YearToDate;
import com.talient.football.reports.Season;
import com.talient.football.reports.Crosstable;
import com.talient.football.entities.WeeklySchedule;

/**
 * This utility class is the home of a convenience function that sets
 * all of the Home interfaces for classes in the com.talient.football.entities
 * and com.talient.football.reports packages to their JDBC-based
 * implementations included the com.talient.football.jdbc package.
 * 
 * The main() method of any program that wants to use the JDBC classes
 * to persist and retrieve entities and reports (i.e. all of them,
 * for now) information should invoke this method first.  For example:
 *
 *     JDBCHomes.setHomes();
 *
 * The Home interfaces allow the rest of the code to be written
 * without any dependencies on the JDBC packages.  Later, there will
 * be a separate EJB implementation of this package.  If all other
 * packages rely solely on the Home interfaces, all that will need
 * to be done to transition to the new implementation is changing
 * the setHomes() call to invoke the EJB implementation.
 */
public class JDBCHomes
{
    public static void setHomes() {
        WeeklySchedule.setHome(new JDBCWeeklySchedule());
        Addresses.setHome(new JDBCAddresses());
        YearToDate.setHome(new JDBCWeeklyResult());
        Season.setHome(new JDBCSeason());
        Crosstable.setHome(new JDBCCrosstable());

        // Note: the other Home interfaces have not yet been written.
    }
}

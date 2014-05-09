// CVS ID: $Id: PublishStandings.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.football.util.publish;

import java.io.IOException;

import com.talient.util.MissingPropertyException;
import com.talient.football.jdbc.JDBCHomes;

/**
 * @author Steve Schmadeke
 * @version $Revision: 1.1.1.1 $
 */
public class PublishStandings {

    private PublishStandings() {}

    public static void main(String[] args) {

        JDBCHomes.setHomes();

        final String usage =
            "Usage: " +
            (new PublishStandings()).getClass().getName() +
            " <year> <week>";

        if (args.length != 2) {
            System.err.println(usage);
            System.exit(1);
        }

        int year = 0;
        int week = 0;

        try {
            year = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid year:" + args[0]);
            System.err.println(usage);
            System.exit(1);
        }

        try {
            week = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid week:" + args[1]);
            System.err.println(usage);
            System.exit(1);
        }

        try {
            System.out.println("Created " +
                com.talient.football.publish.PublishStandings.publish(year, week));
        } catch (IOException e) {
            System.err.println("Error: " + e.toString());
            System.exit(1);
        } catch (MissingPropertyException e) {
            System.err.println("Error: " + e.toString());
            System.exit(1);
        }
    }
}

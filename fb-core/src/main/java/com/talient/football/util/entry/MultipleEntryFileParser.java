// SCCS ID: @(#) 08/23/98 1.1 MultipleEntryFileParser.java

package com.talient.football.util.entry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.System;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import com.talient.football.entities.WeeklySchedule;
import com.talient.football.entities.Team;
import com.talient.football.entities.Entrant;
import com.talient.football.entities.Entry;
import com.talient.football.entities.Alias;
import com.talient.football.entities.IllegalEntryException;
import com.talient.football.jdbc.JDBCEntrant;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class MultipleEntryFileParser {

    private MultipleEntryFileParser() {};

    public static Collection parse(int year, int week, String[] args) {

        if (args.length == 0) {
            throw new RuntimeException("no filenames to parse");
        }

        WeeklySchedule schedule =
            WeeklySchedule.getHome().findByYearWeek(year, week);

        ArrayList entries = new ArrayList();

        for (int i = 0; i < args.length; i++) {

            String name = args[i].replace('_', ' ');

            int slashIndex = name.lastIndexOf('/');
            if (slashIndex >= 0) {
                name = name.substring(slashIndex + 1, name.length());
            }
            if (name.endsWith(".txt")) {
                name = name.substring(0, name.length() - 4);
            }

            Entrant entrant = JDBCEntrant.findByUsername(name);
            if (entrant == null) {
                System.out.println("Cannot find entrant " + name);
                System.exit(1);
/*
                System.out.println("Creating entrant " + name);
                entrant = new Entrant(name, "unknown", false);
                Alias alias = new Alias(entrant, name, "nflpool");
                int rc = JDBCEntrant.store(entrant, alias);
                if (rc == 0) {
                    System.out.println("Could not create entrant " + name);
                    System.exit(1);
                }
*/
            }

            Entry entry = new Entry(schedule, entrant);

            try {
                Iterator teams =
                    EntryFileParser.parse(new FileInputStream(args[i]),
                                          year).iterator();

                while (teams.hasNext()) {
                    entry.add((Team)teams.next());
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e.toString());
            }

            if (entry.size() != schedule.size()) {
                 throw new IllegalEntryException(
                      "Entry for " + name +
                      " does not have the correct number of games " +
                      entry.size() + " != " + schedule.size());
            }

            entries.add(entry);
        }

        return entries;
    }
}

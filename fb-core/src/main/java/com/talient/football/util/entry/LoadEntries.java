// CVS ID: @(#) $Id: LoadEntries.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.football.util.entry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;

import java.lang.String;

import java.util.StringTokenizer;
import java.util.Date;

import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.talient.football.entities.Entry;
import com.talient.football.jdbc.JDBCHomes;
import com.talient.football.jdbc.JDBCEntry;

/**
 * <p>
 * @author Steve Schmadeke
 * @version $Version: $
 */
public class LoadEntries {

    private LoadEntries() {};

    public static void main(String[] args) {

        JDBCHomes.setHomes();

        int year = 0;
        int week = 0;
        File directory;

        String usage = "Usage: com.talient.football.util.entry.LoadEntries <year> <week> <directory>";

        if (args.length < 3) {
            System.err.println(usage);
            System.exit(1);
        }

        // Get the year
        try {
            year = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            System.err.println(usage);
            System.exit(1);
        }
        if (year < 1950 || year > 2050) {
            System.err.println(usage);
            System.exit(1);
        }

        // Get the week
        try {
            week = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            System.err.println(usage);
            System.exit(1);
        }

        directory = new File(args[2]);
        String[] filenames = directory.list();

        ArrayList entries =
            new ArrayList(MultipleEntryFileParser.parse(year, week, filenames));

        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            Entry entry = (Entry)iter.next();

            System.out.println("Storing entry for " +
                entry.getEntrant().getUsername());

            JDBCEntry.store(entry);
        }

        System.out.println("Found " + filenames.length + " entry files");

        System.exit(0);
    }
}

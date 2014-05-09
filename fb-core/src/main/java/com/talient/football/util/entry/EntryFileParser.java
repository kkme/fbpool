// SCCS ID: @(#) 08/23/98 1.1 EntryFileParser.java

package com.talient.football.util.entry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.StringTokenizer;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import com.talient.football.entities.Team;
import com.talient.football.jdbc.JDBCTeam;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class EntryFileParser {

    private EntryFileParser() {};

    public static Collection parse(InputStream i, int year) {

        ArrayList teams = new ArrayList();

        BufferedReader b = new BufferedReader(new InputStreamReader(i));

        try {
            String line;

            // Ignore everything until the line with the dollar sign.
            while ((line = b.readLine()) != null &&
                   !line.trim().equals("$")) {
            }

            if (line == null) {
                throw new RuntimeException("Could not find $");
            }

            // Now start reading teams.
            while ((line = b.readLine()) != null && !line.equals("")) {
                Team team = JDBCTeam.findByLongName(line);
                if (team == null) {
                    throw new RuntimeException(line+" is not a valid Team name");
                }
                teams.add(team);
            }

        } catch (java.io.IOException e) {
        }

        return teams;
    }

    public static void main(String[] args) {
        Iterator teams =
            EntryFileParser.parse(System.in, Integer.parseInt(args[0])
                                 ).iterator();

        while (teams.hasNext()) {
            System.out.println(teams.next());
        }
    }
}

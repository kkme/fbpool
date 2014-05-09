package com.talient.football.util.entrant;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Collection;
import java.util.Iterator;

import com.talient.football.entities.Entrant;
import com.talient.football.entities.Alias;

import com.talient.football.jdbc.JDBCEntrant;

/**
 * <p>
 * @author Nick Lonsky
 * @version 1.0
 */
public class MakePassfile {

    private MakePassfile() {};

    public static void main(String[] args) {
        String usage =
            "Usage: com.talient.football.util.entrant.MakePassfile ";
            
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter("passfile"));
        }
        catch (IOException e) {
            System.err.println("Could not open file: passfile");
            System.exit(1);
        }

        if (pw == null) {
            System.err.println("Could not create passfile.");
        }

        Collection entrants = JDBCEntrant.findActive();

        Iterator iter = entrants.iterator();
        while (iter.hasNext()) {
            Entrant entrant = (Entrant)iter.next();

            Collection aliases = JDBCEntrant.findAliases(entrant);

            Iterator aiter = aliases.iterator();
            while (aiter.hasNext()) {
                Alias alias = (Alias)aiter.next();

                pw.print(alias.getUsername() + ":" +
                         alias.getPassword() + "\n");

                System.out.println(alias.getUsername());
            }
        }
        pw.flush();
        pw.close();
    }
}

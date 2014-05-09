package com.talient.football.util.entrant;

import com.talient.football.entities.Entrant;
import com.talient.football.entities.Alias;

import com.talient.football.jdbc.JDBCEntrant;

/**
 * <p>
 * @author Nick Lonsky
 * @version 1.0
 */
public class DisplayEntrant {

    private DisplayEntrant() {};

    public static void main(String[] args) {
        String usage =
            "Usage: com.talient.football.util.entrant.DisplayEntrant " +
            "<Username>";

        if (args.length != 1) {
            System.err.println(usage);
            System.exit(1);
        }

        Entrant entrant = JDBCEntrant.findByUsername(args[0]);
        Alias alias = JDBCEntrant.findAliasByUsername(args[0]);

        if (entrant == null || alias == null) {
            System.out.println("Entrant " + args[0] + " does not exist.");
            System.exit(1);
        }

        System.out.println("");
        System.out.println("Entrant:  " + entrant.getUsername());
        System.out.println("Password: " + alias.getPassword());
        System.out.println("Email:    " + entrant.getContactEmail());
        System.out.println("Active:   " + entrant.getActive());
        System.out.println("");
    }
}

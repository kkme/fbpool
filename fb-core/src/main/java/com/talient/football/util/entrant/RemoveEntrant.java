package com.talient.football.util.entrant;

import com.talient.football.entities.Entrant;
import com.talient.football.entities.Alias;

import com.talient.football.jdbc.JDBCEntrant;

/**
 * <p>
 * @author Nick Lonsky
 * @version 1.0
 */
public class RemoveEntrant {

    private RemoveEntrant() {};

    public static void main(String[] args) {
        String usage =
            "Usage: com.talient.football.util.entrant.RemoveEntrant " +
            "<Username>";

        if (args.length != 1) {
            System.err.println(usage);
            System.exit(1);
        }

        Entrant entrant = new Entrant(args[0], "dummy@mail.com");

        int rs = JDBCEntrant.remove(entrant);

        if (rs == 0) {
            System.out.println("Failed to remove Entrant: " + args[0]);
        }
        else {
            System.out.println("Removed Entrant: " + args[0]);
        }
    }
}

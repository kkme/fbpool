package com.talient.football.util.entrant;

import com.talient.football.entities.Entrant;
import com.talient.football.entities.Alias;

import com.talient.football.jdbc.JDBCEntrant;

/**
 * <p>
 * @author Nick Lonsky
 * @version 1.0
 */
public class ActivateEntrant {

    private ActivateEntrant() {};

    public static void main(String[] args) {
        String usage =
            "Usage: com.talient.football.util.entrant.ActivateEntrant " +
            "<Username> <Active : true | false>";

        if (args.length != 2) {
            System.err.println(usage);
            System.exit(1);
        }

        boolean active = false;
        if (args[1].equals("true")) {
            active = true;
        }

        Entrant entrant = JDBCEntrant.findByUsername(args[0]);

        if (entrant == null) {
            System.out.println("Entrant " + args[0] + " does not exist.");
            System.exit(1);
        }

        entrant.setActive(active);

        int rs = JDBCEntrant.store(entrant);

        if (rs == 0) {
            System.out.println("Failed to activate Entrant: " + args[0]);
        }
        else {
            System.out.println("Activated Entrant: " + args[0]);
        }
    }
}

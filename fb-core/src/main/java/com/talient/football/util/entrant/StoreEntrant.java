package com.talient.football.util.entrant;

import com.talient.football.entities.Entrant;
import com.talient.football.entities.Alias;

import com.talient.football.jdbc.JDBCEntrant;

/**
 * <p>
 * @author Nick Lonsky
 * @version 1.0
 */
public class StoreEntrant {

    private StoreEntrant() {};

    public static void main(String[] args) {
        String usage =
            "Usage: com.talient.football.util.entrant.StoreEntrant " +
            "<Username> <Email> <Password> <Active : true | false>";

        if (args.length != 4) {
            System.err.println(usage);
            System.exit(1);
        }

        boolean active = false;
        if (args[3].equals("true")) {
            active = true;
        }

        Entrant entrant = new Entrant(args[0], args[1], active);
        Alias alias = new Alias(entrant, args[0], args[2]);

        int rs = JDBCEntrant.store(entrant, alias);

        if (rs == 0) {
            System.out.println("Failed to store Entrant: " + args[0]);
        }
        else {
            System.out.println("Stored Entrant: " + args[0]);
        }
    }
}

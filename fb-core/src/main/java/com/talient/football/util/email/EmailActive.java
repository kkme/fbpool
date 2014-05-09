package com.talient.football.util.email;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
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
public class EmailActive {

    private EmailActive() {};

    public static void main(String[] args) {
        String usage =
            "Usage: com.talient.football.util.entrant.EmailActive " +
            "<filename> [dest. dir]";

        if (args.length < 1) {
            System.err.println(usage);
            System.exit(1);
        }

        File emailFile = new File(args[0]);
        String emailStr = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(emailFile));
            String line;
            while ((line = br.readLine()) != null) {
                emailStr += line + "\n";
            }
            br.close();
        }
        catch (FileNotFoundException fileE) {
            System.err.println("Could not read email file: " + emailFile +
                " Error: " + fileE.getMessage());
            System.exit(1);
        }
        catch (IOException ioE) {
            System.err.println("Could not read email file: " + emailFile +
                " Error: " + ioE.getMessage());
            System.exit(1);
        }

        File destDir;
        if (args.length == 2) {
            destDir = new File(args[1]+"/");
        }
        else {
            destDir = new File("./");
        }
        System.out.println("Destination Directory: " + destDir);

        Collection entrants = JDBCEntrant.findActive();
        Iterator iter = entrants.iterator();
        while (iter.hasNext()) {
            Entrant entrant = (Entrant)iter.next();
            System.out.println(entrant.getUsername());

            // Check for a valid email address
            if (entrant.getContactEmail().indexOf("@") < 0) {
                continue;
            }

            String tmpFilename = "";
            try {
                File entrantFile = emailFile.createTempFile(
                                       entrant.getUsername().replace(' ', '_'),
                                       null,
                                       destDir);
                FileWriter fw = new FileWriter(entrantFile);

                tmpFilename = entrantFile.toString();

                fw.write("To: " + entrant.getContactEmail() + "\n");
                fw.write(emailStr);
                fw.close();
            }
            catch (IOException ioE) {
                System.err.println("Could not write to file: " + tmpFilename +
                    " Error: " + ioE.getMessage());
                System.exit(1);
            }
        }
    }
}

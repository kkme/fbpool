// CVS ID: @(#) $Id: MakeEntryPages.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.football.util.entrypages;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.text.SimpleDateFormat;

import com.talient.football.entities.WeeklySchedule;
import com.talient.football.entities.Entrant;
import com.talient.football.entities.Alias;
import com.talient.football.entities.Game;
import com.talient.football.entities.Team;

import com.talient.football.jdbc.JDBCEntrant;
import com.talient.football.jdbc.JDBCTeam;
import com.talient.football.jdbc.JDBCHomes;

import com.talient.html.Template;

/**
 * <p>
 * @author Nick Lonsky
 * @version $Version: $
 */
public class MakeEntryPages {

    private MakeEntryPages() {};

    public static void main(String[] args) {

        JDBCHomes.setHomes();

        PrintWriter pw = null;

        String usage =
            "Usage: com.talient.football.util.entrant.MakeEntryPages <Year>";

        if (args.length != 1) {
            System.err.println(usage);
            System.exit(1);
        }

        int year = Integer.valueOf(args[0]).intValue();

        Collection schedules = WeeklySchedule.getHome().findByYear(year);

        // Due date format
        SimpleDateFormat ddf = new SimpleDateFormat("EEEE, MMM. d, h:mma");
        // Game Time format
        SimpleDateFormat gdf = new SimpleDateFormat("EEE. h:mm");

        Iterator iter = schedules.iterator();
        while (iter.hasNext()) {
            WeeklySchedule schedule = (WeeklySchedule)iter.next();

            Collection teams = JDBCTeam.findByYear(year);

            String yearStr = "";
            if ((year % 100) < 10) {
                yearStr += "0";
            }
            yearStr += Integer.toString(year % 100);

            String filename = "online" + yearStr;
            if (schedule.getWeek() < 10) {
                filename += "0";
            }
            filename += schedule.getWeek() + ".htm";
            try {
                pw = new PrintWriter(new FileWriter(filename));
            }
            catch (IOException e) {
                System.err.println("Could not open file: " + filename);
                System.exit(1);
            }

            String tpltResource =
                "/com/talient/football/util/entrypages/EntryPage" +
                schedule.games().size() + ".htm";

            Template tplt = new Template(tpltResource);
            tplt.setCgi(false);

            tplt.replace("#wnum", Integer.toString(schedule.getWeek()));
            tplt.replace("#year", Integer.toString(year));

            tplt.replace("#duetime", ddf.format(schedule.getStartTime()));

            Collection games = schedule.orderedGames();
            int gnum = 1;
            Iterator giter = games.iterator();
            while (giter.hasNext()) {
                Game game = (Game)giter.next();
                tplt.replace("#G"+gnum+"TIME",
                             gdf.format(game.getStartTime()));
                tplt.replace("#G"+gnum+"V",
                             game.getVisitor().getLongName());
                tplt.replace("#G"+gnum+"H",
                             game.getHome().getLongName());
                
                teams.remove(game.getHome());
                teams.remove(game.getVisitor());
                gnum++;
            }

            Iterator titer = teams.iterator();
            int byeNum = 1;
            while (titer.hasNext()) {
                Team team = (Team)titer.next();
                tplt.replace("#BYE"+byeNum, team.getLongName());
                byeNum++;
            }
            if (pw != null) {
                System.out.println("Writing " + filename);
                tplt.print(pw);
                pw.flush();
                pw.close();
            }
            else {
                System.out.println("Could not open " + filename);
            }
        }
    }
}

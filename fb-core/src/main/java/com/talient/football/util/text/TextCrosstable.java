// CVS ID: $Id: TextCrosstable.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.football.util.text;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

import com.talient.football.entities.Entry;
import com.talient.football.entities.Entrant;
import com.talient.football.entities.Game;
import com.talient.football.entities.Team;
import com.talient.football.entities.Weights;
import com.talient.football.entities.WeeklySchedule;

import com.talient.football.jdbc.JDBCEntry;

/**
 * <p>
 * @author Steve Schmadeke
 * @version $Revision: 1.1.1.1 $
 */
public class TextCrosstable {

    private TextCrosstable() {};

    private static final Comparator entryComparator = new Comparator() {
        public int compare(Object a, Object b) {
            if (((Entry)a).getEntrant().getUsername() == null) {
                return (((Entry)b).getEntrant().getUsername() == null) ? 0 : 1;
            } else if (((Entry)b).getEntrant().getUsername() == null) {
                return -1;
            } else {
                return ((Entry)a).getEntrant().getUsername().compareTo(
                    ((Entry)b).getEntrant().getUsername());
            }
        }
    };

    // Helper class so our syntax doesn't get way out of control.
    private static class Counter {

        private final Map values = new HashMap();

        private static final class MutableInt {
            public int i = 0;
        }

        public void add(Team team, int value) {
            MutableInt i = (MutableInt)values.get(team);

            if (i == null) {
                i = new MutableInt();
                i.i = value;
                values.put(team, i);
            } else {
                i.i += value;
            }
        }

        public int get(Team team) {
            MutableInt i = (MutableInt)values.get(team);

            if (i == null) {
                return 0;
            } else {
                return i.i;
            }
        }
    }

    // Number of times each team was picked by an entry
    // (except entries beginning with '~').
    private static final Counter picked = new Counter();

    // Total confidence weight allocated to teams by all
    // entries combined (except those beginning with '~').
    private static final Counter weight = new Counter();

    private static final Comparator pickedComparator = new Comparator() {
        public int compare(Object a, Object b) {

            Game ga = (Game)a;
            Game gb = (Game)b;

            int pa = Math.max(picked.get(ga.getHome()),
                              picked.get(ga.getVisitor()));
            int pb = Math.max(picked.get(gb.getHome()),
                              picked.get(gb.getVisitor()));

            if (pa != pb) {
                 return pb - pa;
            }

            int wa = Math.max(weight.get(ga.getHome()),
                              weight.get(ga.getVisitor()));
            int wb = Math.max(weight.get(gb.getHome()),
                              weight.get(gb.getVisitor()));

             return wb - wa;
        }
    };

    private static final Comparator weightComparator = new Comparator() {
        public int compare(Object a, Object b) {

            Game ga = (Game)a;
            Game gb = (Game)b;

            int da = Math.abs(weight.get(ga.getHome()) -
                              weight.get(ga.getVisitor()));
            int db = Math.abs(weight.get(gb.getHome()) -
                              weight.get(gb.getVisitor()));

            return db - da;
        }
    };

    private static final Comparator againstComparator = new Comparator() {
        public int compare(Object a, Object b) {
            return ((String)b).compareTo((String)a);
        }
    };

    private static void printPadRight(String string, int cols) {
        for (int i = cols - string.length(); i > 0; i--) {
            System.out.print(" ");
         }
         System.out.print(string);
    }

    private static void printPadLeft(String string, int cols) {
        System.out.print(string);
        for (int i = cols - string.length(); i > 0; i--) {
            System.out.print(" ");
        }
    }

    public static void main(String[] args) {
        String usage = "com.talient.football.util.text.TextCrosstable <year> <week>";

        if (args.length != 2) {
            System.err.println(usage);
            System.exit(1);
        }

        int year = 0;
        int week = 0;

        try {
            year = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid year:" + args[0]);
            System.err.println(usage);
            System.exit(1);
        }


        try {
            week = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid week:" + args[1]);
            System.err.println(usage);
            System.exit(1);
        }

        ArrayList entries =
             new ArrayList(JDBCEntry.findByYearWeek(year, week));

        if (entries.size() == 0) {
            System.err.println("No entries found for week " + args[1] +
                               " of year " + args[0]);
            System.exit(1);
        }

        Collections.sort(entries, entryComparator);

        WeeklySchedule schedule = ((Entry)entries.get(0)).getSchedule();
        int numGames = schedule.size();

        System.out.println("From: NFL Pool <nflpool@talient.com>");
        System.out.println("Subject: NFL Pool Week #" + week + " Crosstable");

        if (entries.size() != 1) {
			System.out.print("\n\n");
			System.out.println("Crosstable");
			System.out.println("----------\n");

			System.out.print("    ##  ");
		}
		else {
			System.out.print("        ");
		}
        for (int i = 0; i < numGames; i++) {
            printPadRight(
                String.valueOf(schedule.getWeights()[i]), 4);
        }
		System.out.print("\n");
        if (entries.size() != 1) {
			System.out.print("    --  ");
		}
		else {
			System.out.print("        ");
		}
        for (int i = 0; i < numGames; i++) {
            System.out.print(" ---");
        }
        System.out.print("\n");

        for (int i = 0; i < entries.size(); i++) {
            System.out.print("   ");
            printPadRight(String.valueOf(i+1), 3);
            System.out.print(" -");
            Entry entry = (Entry)entries.get(i);
            boolean artificial = entry.getEntrant().isGenerated();
            for (int j = 0; j < entry.size(); j++) {
                Team team = entry.get(j);
                System.out.print(" ");
                printPadLeft(team.getShortName(), 3);
                if (!artificial) {
                    picked.add(team, 1);
                    weight.add(team, schedule.getWeights()[j]);
                }
            }
            System.out.print("\n");
        }

        if (entries.size() != 1) {
			System.out.print("\n\n");
			System.out.println("    ##   Name");
			System.out.println("    --   ---------------");

			for (int i = 0; i < entries.size(); i++) {
				System.out.print("   ");
				printPadRight(String.valueOf(i+1), 3);
				System.out.print(" - ");
				System.out.print(((Entry)entries.get(i)).getEntrant()
                    .getUsername());
				System.out.print("\n");
			}
		}

        if (entries.size() == 1) {
            return;
        }

        System.out.print("\n\n");
        System.out.println("Favorites");
        System.out.println("---------");
        System.out.println(
            "\n(Sorted by number of times picked and average confidence)\n");

        ArrayList gameList = new ArrayList(schedule.games());
        Collections.sort(gameList, pickedComparator);

        for (int i = 0; i < gameList.size(); i++) {

            Game game = (Game)gameList.get(i);

            int order = picked.get(game.getHome()) -
                        picked.get(game.getVisitor());

            if (order == 0) {
                order = weight.get(game.getHome()) -
                        weight.get(game.getVisitor());
            }

            Team favorite;
            Team underdog;

            if (order < 0) {
                favorite = game.getVisitor();
                underdog = game.getHome();
            } else {
                favorite = game.getHome();
                underdog = game.getVisitor();
            }

            int pf = picked.get(favorite);
            int pu = picked.get(underdog);

            int wf = weight.get(favorite);
            int wu = weight.get(underdog);

            double favoriteAverageWeight = 0.0;
            if (pf > 0) {
                favoriteAverageWeight =
                    10.0 * wf / pf;
                favoriteAverageWeight =
                    Math.rint(favoriteAverageWeight) / 10.0;
            }

            double underdogAverageWeight = 0.0;
            if (pu > 0) {
                underdogAverageWeight =
                    10.0 * wu / pu;
                underdogAverageWeight =
                    Math.rint(underdogAverageWeight) / 10.0;
            }

            printPadRight("   " + pf + " ", 7);
            printPadLeft(favorite.getLongName(), 13);
            printPadRight(" " + favoriteAverageWeight, 7);

            printPadRight("   " + pu + " ", 7);
            printPadLeft(underdog.getLongName(), 13);
            printPadRight(" " + underdogAverageWeight, 7);

            System.out.print("\n");
        }

        System.out.print("\n\n");
        System.out.println("Consensus");
        System.out.println("---------");
        System.out.println(
            "\n(Sorted by average net confidence weight)\n");

        Collections.sort(gameList, weightComparator);

        Entry consensus = new Entry(schedule, new Entrant("~Consensus", ""));

        for (int i = 0; i < gameList.size(); i++) {

            Game game = (Game)gameList.get(i);

            int order = weight.get(game.getHome()) -
                        weight.get(game.getVisitor());

            Team favorite;
            Team underdog;

            if (order < 0) {
                favorite = game.getVisitor();
                underdog = game.getHome();
            } else {
                favorite = game.getHome();
                underdog = game.getVisitor();
            }

            consensus.add(favorite);

            int numPicks = picked.get(game.getHome()) +
                           picked.get(game.getVisitor());

            double confidence =
                Math.rint(10.0 * Math.abs(order) / numPicks) / 10.0;

            printPadLeft("    " + favorite.getLongName(), 17);
            printPadLeft(" over " + underdog.getLongName(), 19);
            printPadRight(" " + confidence, 7);

            System.out.print("\n");
        }

        System.out.print("\n\n");
        System.out.println("Picks Against Consensus");
        System.out.println("-----------------------\n");

        ArrayList against = new ArrayList(entries.size());

        for (int i = 0; i < entries.size(); i++) {

            Entry entry = (Entry)entries.get(i);

            if (entry.getEntrant().isGenerated()) {
                continue;
            }

            int count = 0;
            for (int j = 0; j < entry.size(); j++) {
                if (!consensus.contains(entry.get(j))) {
                    count++;
                }
            }

            if (count < 10) {
                against.add("     " + count + " " +
                    entry.getEntrant().getUsername());
            } else {
                against.add("    " + count + " " +
                    entry.getEntrant().getUsername());
            }
        }

        Collections.sort(against, againstComparator);

        for (int i = 0; i < against.size(); i++) {
            System.out.println(against.get(i));
        }
    }
}

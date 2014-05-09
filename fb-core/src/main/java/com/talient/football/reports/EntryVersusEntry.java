// CVS ID: $Id $

package com.talient.football.reports;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import java.text.NumberFormat;

import com.talient.football.entities.Team;
import com.talient.football.entities.Entry;
import com.talient.football.entities.TeamResult;


public class EntryVersusEntry {

    public static final EntryVersusEntry
        createEntryVersusEntry(int year,
                               int week,
                               String entrant,
                               String otherEntrant) {
        return null;
    }

    public static final EntryVersusEntry
        createEntryVersusEntry(int year,
                               int week,
                               String entrant) {
        return null;
    }

    public EntryVersusEntry(Entry entry, Crosstable crosstable) {
    }

    public EntryVersusEntry(Entry entry, Entry otherEntry) {
    }

    public class Pick {
        public Team getTeamPicked() {
            return null;
        }

        public Team getTeamNotPicked() {
            return null;
        }

        public boolean getDisputed() {
            return false;
        }

        public int getWeight() {
            return 0;
        }

        public TeamResult getResult() {
            return TeamResult.UNDETERMINED;
        }

        public int getGainIfCorrect() {
            return 0;
        }

        public int getGainIfIncorrect() {
            return 0;
        }

        public int getGain() {
            return 0;
        }

        private Pick() {
        }
    }

    public Entry getEntry() {
        return null;
    }

    // Can return null depending on constructor.
    public Entry getOtherEntry() {
        return null;
    }

    // Can return null depending on constructor.
    public Crosstable getCrosstable() {
        return null;
    }

    public List getAllPicks() {
        return null;
    }

    public List getUndeterminedPicks() {
        return null;
    }

    public List getDeterminedPicks() {
        return null;
    }

    public int getNetGain() {
        return 0;
    }

    public int getMaxGain() {
        return 0;
    }

    public int getMinGain() {
        return 0;
    }

    public int getGamesWon() {
        return 0;
    }

    public int getGamesLost() {
        return 0;
    }

    public int getGamesTied() {
        return 0;
    }

    public int getDisputedGamesWon() {
        return 0;
    }

    public int getDisputedGamesLost() {
        return 0;
    }

    public int getDisputedGamesTied() {
        return 0;
    }

    public static final Comparator weightComparator = new Comparator() {
        public int compare(Object a, Object b) {
            return 0;
        }
    };

    public static final Comparator gainComparator = new Comparator() {
        public int compare(Object a, Object b) {
            return 0;
        }
    };

    public static final Comparator absGainComparator = new Comparator() {
        public int compare(Object a, Object b) {
            return 0;
        }
    };

    public static final void main(String[] argv) {

        final EntryVersusEntry e1 =
            EntryVersusEntry.createEntryVersusEntry(
                2002, 15, "Steve Schmadeke", "Nick Lonsky");

        final List picks = e1.getAllPicks();
        Collections.sort(picks, absGainComparator);

        for (Iterator i = picks.iterator(); i.hasNext(); ) {
            final EntryVersusEntry.Pick p =
                (EntryVersusEntry.Pick)i.next();

            final StringBuffer b = new StringBuffer();

            b.append(p.getTeamPicked());
            b.append(" over ");
            b.append(p.getTeamNotPicked());
            b.append(" \t ");
            b.append(p.getWeight());
            b.append(" \t ");
            if (p.getResult() == TeamResult.UNDETERMINED) {
                b.append(p.getGainIfCorrect());
                b.append(" - ");
                b.append(p.getGainIfIncorrect());
            } else {
                b.append(p.getGain());
            }

            System.out.println(b.toString());
        }

        System.out.println(
            "\nNet Gain: " + e1.getNetGain() +
            " Max Gain: " + e1.getMaxGain() +
            " Min Gain: " + e1.getMinGain());

        System.out.println(
            "\nRecord: " + e1.getGamesWon() +
            " - " +        e1.getGamesLost() +
            " - " +        e1.getGamesTied());

        System.out.println(
            "\nRecord versus " + e1.getEntry().getEntrant().getUsername() +
            ": " +  e1.getDisputedGamesWon() +
            " - " + e1.getDisputedGamesLost() +
            " - " + e1.getDisputedGamesTied());
    }
}

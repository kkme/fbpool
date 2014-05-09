// CVS ID: @(#) $Id: WeeklyStats.java,v 1.2 2008-11-16 22:58:27 husker Exp $

package com.talient.football.reports;

import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.text.NumberFormat;

import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.jdom.Document;
import org.jdom.Element;

import com.talient.util.Properties;
import com.talient.util.MissingPropertyException;
import com.talient.football.entities.WeeklySchedule;
import com.talient.football.entities.WeeklyResult;
import com.talient.football.entities.Entry;
import com.talient.football.entities.Entrant;
import com.talient.football.entities.Game;
import com.talient.football.entities.Team;
import com.talient.football.entities.TeamResult;
import com.talient.football.entities.GameResult;

public class WeeklyStats {

    private Crosstable crosstable;

    // number of times each team was picked
    private HashMap timesPicked = new HashMap();

    // Total number of points per team for all entries except ~ entries
    private HashMap totalPoints = new HashMap();

    // number of wins/losses/ties each entrant has
    private HashMap wins = new HashMap();
    private HashMap losses = new HashMap();
    private HashMap ties = new HashMap();

    // number of wins/losses/ties each entrant has
    private HashMap againstWins = new HashMap();
    private HashMap againstLosses = new HashMap();
    private HashMap againstTies = new HashMap();

    // number of picks entrants have against consensus
    private HashMap againstConsensus = new HashMap();

    // The score for each entrants for this week
    private HashMap score = new HashMap();

    // sorted list of games based off of number of times favorite was picked
    // and average weight.
    private ArrayList favorites = new ArrayList();
    private HashMap favAverage = new HashMap();

    // NumberFormat for displaying average confidence
    private NumberFormat avgFormat = null;

    // sorted list of entrants based off of username
    private ArrayList sortedEntries = new ArrayList();

    // sorted list of entrants based off of number of wins and points
    private ArrayList winLossRecord = new ArrayList();

    // sorted list of entrants based off of number of picks against consensus
    private ArrayList againstConsensusArray = new ArrayList();

    // The following two members are set/populated based on two factors.
    // If the WeeklySchedule for the week does *not* report a bonus, the
    // bonusAmount will be zero and the bonusWinners list will remain
    // empty.
    //
    // If the WeeklySchedule does report a bonus, but none of the
    // WeeklyResults in the crosstable include a bonus (i.e. not all the
    // games in the week are final), the bonusAmount will be the same as
    // the amount reported by WeeklySchedule while the bonusWinners list
    // will remain empty.
    //
    // Finally, if there is a bonus for the weeks and the results for
    // the entrants in the crosstable include bonuses, each entrant that
    // earned a bonus will be included in the bonusWinners list.  The
    // bonusAmount will reflect the bonus earned by each entrant in the
    // list.  That amount could be lower than the bonus reported by
    // WeeklySchedule if multiple entrants tied for first.
    private ArrayList bonusWinners = new ArrayList();
    private int bonusAmount = 0;

    private WeeklyStats() { }
    public WeeklyStats(Crosstable crosstable) {
        this.crosstable = crosstable;
        avgFormat = crosstable.getSchedule().getNumberFormat();
        if (crosstable.getSchedule().getTotalPoints() > 10000) {
            avgFormat.setMinimumFractionDigits(2);
            avgFormat.setMaximumFractionDigits(2);
        } else {
            avgFormat.setMinimumFractionDigits(1);
            avgFormat.setMaximumFractionDigits(1);
        }
        calculate();
    }

    public List getFavorites()         { return favorites; }
    public List getWinLossRecord()     { return winLossRecord; }
    public List getAgainstConsensus()  { return againstConsensusArray; }
    public List getBonusWinners()      { return bonusWinners; }
    public int  getBonusAmount()       { return bonusAmount; }
    public NumberFormat getAvgFormat() { return avgFormat; }

    public int getTimesPicked(Team team) {
        Integer num =  (Integer)timesPicked.get(team);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getTotalPoints(Team team) {
        Integer num = (Integer)totalPoints.get(team);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getWins(Entrant entrant) {
        Integer num = (Integer)wins.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getLosses(Entrant entrant) {
        Integer num = (Integer)losses.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getTies(Entrant entrant) {
        Integer num = (Integer)ties.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getAgainstWins(Entrant entrant) {
        Integer num = (Integer)againstWins.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getAgainstLosses(Entrant entrant) {
        Integer num = (Integer)againstLosses.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getAgainstTies(Entrant entrant) {
        Integer num = (Integer)againstTies.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getAgainstConsensus(Entrant entrant) {
        Integer num = (Integer)againstConsensus.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getScore(Entrant entrant) {
        Integer num = (Integer)score.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public Crosstable getCrosstable() { return crosstable; }
    public int getYear() { return crosstable.getYear(); }
    public int getWeek() { return crosstable.getWeek(); }

    public boolean contains(Entry entry) {
        return crosstable.contains(entry);
    }

    public Entry getEntry(int index) {
        return crosstable.getEntry(index);
    }

    public List getEntries() {
        return crosstable.getEntries();
    }

    public WeeklyResult getResult(int index) {
        return crosstable.getResult(index);
    }

    public int indexOf(Entry entry) {
        return crosstable.indexOf(entry);
    }

    public void remove(int index) {
        crosstable.remove(index);
        calculate();
    }

    public void remove(Entry entry) {
        crosstable.remove(entry);
        calculate();
    }

    public int size() {
        return crosstable.size();
    }

    public WeeklySchedule getSchedule() {
        return crosstable.getSchedule();
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(getClass().getName());
        str.append("[");
        str.append("Year="+getYear());
        str.append(",Week="+getWeek());
        str.append("]");

        return str.toString();
    }

    public Entry addConsensus() {
        return crosstable.addConsensus();
    }

    public Entry getConsensus() {
        return crosstable.getConsensus();
    }

    public List getSortedEntries() {
        return new ArrayList(sortedEntries);
    }

    public Team getFavorite(Game game) {
        if (getTimesPicked(game.getHome()) >
            getTimesPicked(game.getVisitor())) {
            return game.getHome();
        }
        else if (getTimesPicked(game.getHome()) <
                 getTimesPicked(game.getVisitor())) {
            return game.getVisitor();
        }
        else if (getTotalPoints(game.getHome()) >=
                 getTotalPoints(game.getVisitor())) {
            return game.getHome();
        }
        else if (getTotalPoints(game.getHome()) <
                 getTotalPoints(game.getVisitor())) {
            return game.getVisitor();
        }
        return null;
    }

    public Team getUnderdog(Game game) {
        if (getTimesPicked(game.getHome()) >
            getTimesPicked(game.getVisitor())) {
            return game.getVisitor();
        }
        else if (getTimesPicked(game.getHome()) <
                 getTimesPicked(game.getVisitor())) {
            return game.getHome();
        }
        else if (getTotalPoints(game.getHome()) >=
                 getTotalPoints(game.getVisitor())) {
            return game.getVisitor();
        }
        else if (getTotalPoints(game.getHome()) <
                 getTotalPoints(game.getVisitor())) {
            return game.getHome();
        }
        return null;
    }

    public void calculate() {
        Entry consensus = getConsensus();
        if (consensus == null) {
            consensus = addConsensus();
        }
        WeeklySchedule schedule = getSchedule();
        bonusAmount = schedule.getBonusPoints();
        int weights[] = schedule.getWeights();
        for (int i=0; i<size(); i++) {
            Entry entry = getEntry(i);
            Entrant entrant = entry.getEntrant();

            // Add entrant to bonusWinners if they have bonus points
            if (getResult(i).getBonus() > 0) {
                bonusAmount = getResult(i).getBonus();
                bonusWinners.add(entrant);
            }

            int againstTotal = 0;
            int widx = 0;
            for (int j=0; j<entry.size(); j++) {
                Team pick = entry.get(j);
                if (schedule.getResult(pick) == TeamResult.DROPPED) {
                    continue;
                }
                boolean against = !consensus.contains(pick);
                int num;
                
                if (! entrant.isGenerated()) {
                    num = getTimesPicked(pick);
                    timesPicked.put(pick, new Integer(num+1));

                    int weight = weights[widx++];
                    num = getTotalPoints(pick);
                    totalPoints.put(pick, new Integer(num+weight));
                }
                
                TeamResult result = entry.getResult(pick);
                if (result.toString().equals(TeamResult.WON.toString())) {
                    num = getWins(entrant);
                    wins.put(entrant, new Integer(num+1));
                    if (against) {
                        num = getAgainstWins(entrant);
                        againstWins.put(entrant, new Integer(num+1));
                    }
                }
                else if (result.toString().equals(TeamResult.LOST.toString())) {
                    num = getLosses(entrant);
                    losses.put(entrant, new Integer(num+1));
                    if (against) {
                        num = getAgainstLosses(entrant);
                        againstLosses.put(entrant, new Integer(num+1));
                    }
                }
                else if (result.toString().equals(TeamResult.TIED.toString())) {
                    num = getTies(entrant);
                    ties.put(entrant, new Integer(num+1));
                    if (against) {
                        num = getAgainstTies(entrant);
                        againstTies.put(entrant, new Integer(num+1));
                    }
                }

                if (!consensus.contains(pick)) {
                    againstTotal++;
                }
            }
            againstConsensus.put(entrant, new Integer(againstTotal));
            score.put(entrant, new Integer(entry.getScore()));

            // add entrants for future sorting
            winLossRecord.add(entrant);
            againstConsensusArray.add(entrant);
        }

        Iterator iter = schedule.games().iterator();
        while (iter.hasNext()) {
            Game game = (Game)iter.next();
            if (game.getResult() != GameResult.DROPPED) {
                favorites.add(game);
                double home_avg = 0.0;
                if (getTimesPicked(game.getHome()) > 0) {
                    home_avg = (double)getTotalPoints(game.getHome()) /
                                       getTimesPicked(game.getHome());
                }
                favAverage.put(game.getHome(),
                               new String(avgFormat.format(home_avg)));
                double visitor_avg = 0.0;
                if (getTimesPicked(game.getVisitor()) > 0) {
                    visitor_avg = (double)getTotalPoints(game.getVisitor()) /
                                          getTimesPicked(game.getVisitor());
                }
                favAverage.put(game.getVisitor(),
                               new String(avgFormat.format(visitor_avg)));
            }
        }
        Collections.sort(favorites, favoriteComparator);

        Collections.sort(winLossRecord, winlossComparator);

        sortedEntries = new ArrayList(crosstable.getEntries());
        Collections.sort(sortedEntries, usernameComparator); 

        Collections.sort(againstConsensusArray, againstComparator);

        return;
    }

    private final Comparator againstComparator = new Comparator() {
        public int compare(Object a, Object b) {

            Entrant ea = (Entrant)a;
            Entrant eb = (Entrant)b;

            int aa = getAgainstConsensus(ea);
            int ab = getAgainstConsensus(eb);

            if (aa == ab) {
                return ea.getUsername().compareTo(eb.getUsername());
            } else {
                return ab - aa;
            }
        }
    };

    private final Comparator winlossComparator = new Comparator() {
        public int compare(Object a, Object b) {

            Entrant ea = (Entrant)a;
            Entrant eb = (Entrant)b;

            int wa = getWins(ea);
            int wb = getWins(eb);
            int pa = getScore(ea);
            int pb = getScore(eb);

            if (wa == wb) {
                return pb - pa;
            }
            else {
                return wb - wa;
            }
        }
    };

    private final Comparator favoriteComparator = new Comparator() {
        public int compare(Object a, Object b) {

            Game ga = (Game)a;
            Game gb = (Game)b;

            int pa =
                Math.max(getTimesPicked(ga.getHome()),
                         getTimesPicked(ga.getVisitor()));
            int pb =
                Math.max(getTimesPicked(gb.getHome()),
                         getTimesPicked(gb.getVisitor()));

            int wa =
                Math.max(getTotalPoints(ga.getHome()),
                         getTotalPoints(ga.getVisitor()));
            int wb =
                Math.max(getTotalPoints(gb.getHome()),
                         getTotalPoints(gb.getVisitor()));

            if (pa == pb) {
                 return wb - wa;
            } else {
                 return pb - pa;
            }
        }
    };
    
    /**
     * Compare entries by Username of the Entrant.
     */ 
    private static final Comparator usernameComparator = new Comparator() {
        public int compare(Object a, Object b) {

            Entry ea = (Entry)a;
            Entry eb = (Entry)b;

            return ea.getEntrant().getUsername().trim().compareTo(
                       eb.getEntrant().getUsername().trim());
        }   
    };

    public String getStateStr(Team team) {
        String state = "";
        String wlt;  // win lose tie or undetermined
        String aw = null;   // against or with consensus
        TeamResult result = getSchedule().getResult(team);
        if (result == TeamResult.DROPPED) {
            return state;
        } else if (result == TeamResult.WON) {
            wlt = "w";
        } else if (result == TeamResult.LOST) {
            wlt = "l";
        } else if (result == TeamResult.TIED) {
            wlt = "t";
        }
        else {
            wlt = "";
        }
        Entry conEntry = getConsensus();
        if (conEntry.contains(team)) {
            aw = "wc";
        } else {
            aw = "ac";
        }
        state = wlt + aw;
        return state;
    }

    public Document getJDOM()
        throws MissingPropertyException {
        final String name = Properties.getProperty("football.pool.name");
        if (name == null) {
            throw new MissingPropertyException(
                    "football.pool.name property has not been set");
        }
        final String url = Properties.getProperty("football.pool.url");
        if (url == null) {
            throw new MissingPropertyException(
                    "football.pool.url property has not been set");
        }

        Element root = new Element("WeeklyStats");
        root.addContent(new Element("Title").setText(name));
        root.addContent(new Element("Year").setText(Integer.toString(getYear())));
        root.addContent(new Element("Week").setText(Integer.toString(getWeek())));
        root.addContent(new Element("Url").setText(url));

        root.addContent(new Element("WeekName").setText(getSchedule().getLabel()));

        ListIterator iter = getFavorites().listIterator();
        while (iter.hasNext()) {
            Element e = new Element("Favorites");
            Game game = (Game)iter.next();
            Team fav = getFavorite(game);
            Team ud = getUnderdog(game);
            e.addContent(new Element("Favorite").setText(fav.toString()));
            e.addContent(new Element("FavoritePicked").setText(Integer.toString(getTimesPicked(fav))));
            e.addContent(new Element("FavoriteAverage").setText((String)favAverage.get(fav)));
            e.addContent(new Element("FavoriteState").setText(getStateStr(fav)));
            e.addContent(new Element("Underdog").setText(ud.toString()));
            e.addContent(new Element("UnderdogPicked").setText(Integer.toString(getTimesPicked(ud))));
            e.addContent(new Element("UnderdogAverage").setText((String)favAverage.get(ud)));
            e.addContent(new Element("UnderdogState").setText(getStateStr(ud)));
            root.addContent(e);
        }

        Entry conEntry = addConsensus();
        for (int i=0; i<conEntry.size(); i++) {
            Element e = new Element("Consensus");
            Team pick = conEntry.get(i);
            if (getSchedule().getResult(pick) == TeamResult.DROPPED) {
                continue;
            }
            Team opponent = getSchedule().getOpponent(pick);
            e.addContent(new Element("Team").setText(pick.toString()));
            double weight = ( getTotalPoints(pick) -
                              getTotalPoints(opponent)) /
                   (double) ( getTimesPicked(pick) + 
                              getTimesPicked(opponent));
            e.addContent(new Element("Weight").setText(avgFormat.format(weight)));
            e.addContent(new Element("State").setText(getStateStr(pick)));
            root.addContent(e);
        }

        iter = getWinLossRecord().listIterator();
        while (iter.hasNext()) {
            Entrant entrant = (Entrant)iter.next();
            Element e = new Element("Entrants");
            e.addContent(new Element("Name").setText(entrant.getUsername()));
            e.addContent(new Element("AgainstConsensus").setText(Integer.toString(getAgainstConsensus(entrant))));
            e.addContent(new Element("ConsensusWins").setText(Integer.toString(getAgainstWins(entrant))));
            e.addContent(new Element("ConsensusLosses").setText(Integer.toString(getAgainstLosses(entrant))));
            e.addContent(new Element("ConsensusTies").setText(Integer.toString(getAgainstTies(entrant))));
            e.addContent(new Element("Wins").setText(Integer.toString(getWins(entrant))));
            e.addContent(new Element("Losses").setText(Integer.toString(getLosses(entrant))));
            e.addContent(new Element("Ties").setText(Integer.toString(getTies(entrant))));
            e.addContent(new Element("Score").setText(crosstable.getSchedule().getNumberFormat().format(getScore(entrant))));
            root.addContent(e);
        }

        Document doc = new Document(root);
        return doc;
    }

    public String toXML() 
        throws MissingPropertyException {
        XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());
        return outp.outputString(getJDOM());
    }

    public static void main(String argv[]) {
        com.talient.football.jdbc.JDBCHomes.setHomes();

        Crosstable ct = Crosstable.getHome().findByYearWeek(2002, 17);
        WeeklyStats ws = new WeeklyStats(ct);

        try {
            System.out.println(ws.toXML());
        }
        catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}

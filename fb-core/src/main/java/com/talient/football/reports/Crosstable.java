// SCCS ID: @(#) 08/23/98 1.1 Crosstable.java

package com.talient.football.reports;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.jdom.Document;
import org.jdom.Element;

import com.talient.util.Properties;
import com.talient.util.MissingPropertyException;
import com.talient.football.entities.WeeklySchedule;
import com.talient.football.entities.WeeklyResult;
import com.talient.football.entities.TeamResult;
import com.talient.football.entities.Entry;
import com.talient.football.entities.Entrant;
import com.talient.football.entities.Game;
import com.talient.football.entities.Team;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class Crosstable {

    public static interface CrosstableHome {
        public Crosstable findByYearWeek(int year, int week);
    }

    public static CrosstableHome getHome() {
        return home;
    }

    public static void setHome(CrosstableHome home) {
        Crosstable.home = home;
    }

    private static CrosstableHome home = new CrosstableHome() {
        public Crosstable findByYearWeek(int year, int week) {
            throw new UnsupportedOperationException(
                "findByYearWeek() not supported by default " +
                "Crosstable Home interface");
        }
    };

    private int year;
    private int week;
    private WeeklySchedule schedule;
    private List entries = new ArrayList();
    private List results = new ArrayList();
    private double consensusWeights[] = null;
    private Map weights = new HashMap();

    public Crosstable(int year, int week) {
        this.year = year;
        this.week = week;
    }

    public int getYear() { return year; }
    public int getWeek() { return week; }
    public double[] getConsensusWeights() { return consensusWeights; }

    public void add(Entry entry, WeeklyResult result) {

        if (entries.contains(entry)) {
            return;
        }

        if (results.contains(result)) {
            return;
        }

        // Sort by score
        int index = -1;
        for (int i=0; i<entries.size(); i++) {
            WeeklyResult r = (WeeklyResult)results.get(i);
            Entry e = (Entry)entries.get(i);
            // Sort the Entries as they are added.
            if (result.getScore() == r.getScore()) {
                if (entry.getEntrant().getUsername().trim().compareTo(
                        e.getEntrant().getUsername().trim()) < 0) {
                    index = i;
                    break;
                }
            }
            if (result.getScore() > r.getScore()) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            index = entries.size();
        }

        entries.add(index, entry);
        results.add(index, result);
    }

    public boolean contains(Entry entry) {
        return entries.contains(entry);
    }

    public Entry getEntry(int index) {
        return (Entry)entries.get(index);
    }

    public List getEntries() {
        return entries;
    }

    public WeeklyResult getResult(int index) {
        return (WeeklyResult)results.get(index);
    }

    public Collection getResults() {
        return results;
    }

    public int indexOf(Entry entry) {
        return entries.indexOf(entry);
    }

    public void remove(int index) {
        entries.remove(index);
        results.remove(index);
    }

    public void remove(Entry entry) {
        remove(indexOf(entry));
    }

    public int size() {
        return entries.size();
    }

    public void setSchedule(WeeklySchedule schedule) {
        this.schedule = schedule;
    }

    public WeeklySchedule getSchedule() {
        return schedule;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(getClass().getName());
        str.append("[");
        str.append("Year="+year);
        str.append(",Week="+week);
        str.append("]");

        return str.toString();
    }

    public void setBonus() {
        if (schedule == null || entries.size() == 0) {
            return; // Crosstable is not initialized
        }

        if (schedule.getBonusPoints() <= 0) {
            return; // There is not bonus to calculate
        }

        if (! schedule.allGamesValidated()) {
            return; // All the games have not been scored yet
        }

        // Find the number of top scores
        int top = 0;
        for (int i=0; i<size(); i++) {
            if (getResult(i).getEntrant().isGenerated()) {
                top++;
            }
            else {
                break;
            }
        }
        int topScore = getResult(top).getScore();
        int numTopScores = 0;
        for (int i=top; i<size(); i++) {
            if (getResult(i).getEntrant().isGenerated()) {
                continue;
            }
            if (getResult(i).getScore() == topScore) {
                numTopScores++;
            }
            else {
                break;
            }
        }

        int bonusPts = schedule.getBonusPoints() / numTopScores;

        for (int i=0; i<size(); i++) {
            WeeklyResult result = getResult(i);
            if (result.getEntrant().isGenerated()) {
                continue;
            }
            if (result.getScore() == topScore) {
                result.setBonus(bonusPts);
            }
            else {
                break;
            }
        }
    }

    public Entry addConsensus() {
        // Check to see if the entry is already added.
        Entrant consensus = new Entrant("~Consensus", "");
        Entry conEntry = new Entry(schedule, consensus);

        if (entries.contains(conEntry)) {
            Iterator iter = entries.iterator();
            while (iter.hasNext()) {
                Entry e = (Entry)iter.next();
                if (e.equals(conEntry)) {
                    return e;
                }
            }
        }

        // Consensus not found so generate it.
        Entry entry = getConsensus();

        // Add the consensus entry
        WeeklyResult consensusResult =
            new WeeklyResult(getYear(), getWeek(), entry.getEntrant());
        consensusResult.setScore(entry.getScore());
                                
        add(entry, consensusResult);

        return entry;
    }

    public Entry getConsensus() {
        Entrant consensus = new Entrant("~Consensus", "");

        Entry conEntry = new Entry(schedule, consensus);

        int weightArray[] = schedule.getWeights();

        int numGames = weightArray.length;

        weights = new HashMap();

        // Calculate the consensus weight
        int numEntries = 0;
        for (int i=0; i<size(); i++) {
            Entry entry = getEntry(i);
            boolean artificial = entry.getEntrant().isGenerated();
            if (! artificial) {
                numEntries++;
            }
            int widx = 0;
            for (int j=0; j<entry.size(); j++) {
                if (!artificial) {
                    Team team = entry.get(j);
                    if (schedule.getResult(team) == TeamResult.DROPPED) {
                        continue;
                    }
                    Integer weight = (Integer)weights.get(team);
                    if (weight == null) {
                        weight = new Integer(0);
                    }
                    weight = new Integer(weight.intValue() + weightArray[widx++]);
                    weights.put(team, weight);
                }
            }
        }

        ArrayList gameList = new ArrayList(schedule.orderedGames());
        Collections.sort(gameList, weightComparator);
        consensusWeights = new double[gameList.size()];

        for (int i = 0; i < gameList.size(); i++) {

            Game game = (Game)gameList.get(i);

            Integer homeWeight = (Integer)weights.get(game.getHome());
            if (homeWeight == null) {  homeWeight = new Integer(0); }
            Integer visitorWeight = (Integer)weights.get(game.getVisitor());
            if (visitorWeight == null) {  visitorWeight = new Integer(0); }

            int order = homeWeight.intValue() - visitorWeight.intValue();

            Team favorite;
            Team underdog;

            if (order < 0) {
                favorite = game.getVisitor();
                underdog = game.getHome();
            } else {
                favorite = game.getHome();
                underdog = game.getVisitor();
            }

            consensusWeights[i] = (double)Math.abs(order) / numEntries;

            conEntry.add(favorite);
        }

        return conEntry;
    }

    private final Comparator weightComparator = new Comparator() {
        public int compare(Object a, Object b) {

            Game ga = (Game)a;
            Game gb = (Game)b;

            Integer gaHomeWeight = (Integer)weights.get(ga.getHome());
            if (gaHomeWeight == null) {  gaHomeWeight = new Integer(0); }
            Integer gaVisitorWeight = (Integer)weights.get(ga.getVisitor());
            if (gaVisitorWeight == null) {  gaVisitorWeight = new Integer(0); }
            Integer gbHomeWeight = (Integer)weights.get(gb.getHome());
            if (gbHomeWeight == null) {  gbHomeWeight = new Integer(0); }
            Integer gbVisitorWeight = (Integer)weights.get(gb.getVisitor());
            if (gbVisitorWeight == null) {  gbVisitorWeight = new Integer(0); }

            int da = Math.abs(gaHomeWeight.intValue() -
                              gaVisitorWeight.intValue());
            int db = Math.abs(gbHomeWeight.intValue() -
                              gbVisitorWeight.intValue());

            return db - da;
        }
    };

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

        Element root = new Element("Crosstable");
        root.addContent(new Element("Title").setText(name));
        root.addContent(new Element("Year").setText(Integer.toString(year)));
        root.addContent(new Element("Week").setText(Integer.toString(week)));
        root.addContent(new Element("Url").setText(url));

        root.addContent(new Element("WeekName").setText(schedule.getLabel()));

        int weightArray[] = schedule.getWeights();
        Element weights = new Element("Weights");
        for (int i=0; i<weightArray.length; i++) {
            String weight = schedule.getNumberFormat().format(weightArray[i]);
            weights.addContent(new Element("Weight").setText(weight));
        }
        root.addContent(weights);

        String bonusStr = null;
        int bonusPts = schedule.getBonusPoints();
        StringBuffer winners = new StringBuffer();
        Entry conEntry = addConsensus();
        int rank = 1;
        for (int i=0; i<size(); i++) {
            Entry entry = getEntry(i);
            WeeklyResult wresult = getResult(i);
            if (wresult.getBonus() > 0) { // Bonus is included
                bonusPts = wresult.getBonus();
                if (i > 0) {
                    winners.append(", ");
                }
                winners.append(entry.getEntrant().getUsername());
            }
            Element e = new Element("Entry");
            e.addContent(new Element("Name").setText(
                                 entry.getEntrant().getUsername()));
            if (entry.getEntrant().isGenerated()) {
                e.addContent(new Element("Rank").setText(""));
            }
            else {
                e.addContent(new Element("Rank").setText(
                                     Integer.toString(rank)));
                rank++;
            }
            for (int j=0; j<entry.size(); j++) {
                String wlt;  // win lose tie or undetermined
                String aw = null;   // against or with consensus
                Team team = entry.get(j);
                TeamResult result = schedule.getResult(team);
                if (result == TeamResult.DROPPED) {
                    continue;
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
                if (conEntry.contains(team)) {
                    aw = "wc";
                } else {
                    aw = "ac";
                }
                String state = wlt + aw;
                Element t = new Element("Team");
                String abbrev = schedule.getAbbrev(team);
                t.addContent(new Element("TeamName").setText(abbrev));
                t.addContent(new Element("State").setText(state));
                e.addContent(t);
            }
            String score = schedule.
                            getNumberFormat().
                             format(getResult(i).getTotalScore());
            e.addContent(new Element("Score").setText(score));
            root.addContent(e);
        }
        root.addContent(new Element("Bonus").setText(
                schedule.getNumberFormat().format(bonusPts)));
        root.addContent(new Element("Winner").setText(winners.toString()));
        Document gdoc = schedule.getJDOM(conEntry);
        Element games = gdoc.getRootElement();
        // List glist = games.getChildren("Game");
        // ListIterator iter = glist.listIterator();
        Iterator iter = new ArrayList(games.getChildren("Game")).iterator();
        while (iter.hasNext()) {
            Element game = (Element)iter.next();
            game.detach();
            root.addContent(game);
        }

        Document doc = new Document(root);
        return doc;
    }

    public String toXML() 
        throws MissingPropertyException {
        XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());
        return outp.outputString(getJDOM());
    }

    public void print(PrintWriter pw) {
        pw.println("Text version of Crosstable not available.");
    }

    public static void main(String argv[]) {
        com.talient.football.jdbc.JDBCHomes.setHomes();
        Crosstable ct = Crosstable.getHome().findByYearWeek(1997, 1);
        try {
            System.out.println(ct.toXML());
        }
        catch (MissingPropertyException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}

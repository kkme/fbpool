// CVS ID: $Id: YearToDate.java,v 1.3 2008-11-18 15:17:24 husker Exp $

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
import java.util.Random;

import org.jdom.output.XMLOutputter;
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
import com.talient.football.entities.Weights;

public class YearToDate {

    public static interface YearToDateHome {

        public Collection findByYearWeek(int year, int week);
    }

    public static YearToDateHome getHome() {
        return home;
    }

    public static void setHome(YearToDateHome home) {
        YearToDate.home = home;
    }

    private static YearToDateHome home = new YearToDateHome() {
        public Collection findByYearWeek(int year, int week) {
            throw new UnsupportedOperationException(
                "findByYearWeek() not supported by default YearToDate Home interface");
        }
    };

    private int year;
    private int week;

    // True if the YearToDate was requested for weeks that had
    // WeeklyResults available.
    private boolean valid = true;

    // Weekly Schedule set by calculate()
    WeeklySchedule schedule;

    // Total points for each entrant
    private HashMap totalPoints = new HashMap();

    // Lowest and 2nd lowest scoring week for each entrant
    private HashMap lowestWeek = new HashMap();
    private HashMap secondLowestWeek = new HashMap();

    // number of wins/losses/ties for each entrant
    private HashMap wins = new HashMap();
    private HashMap losses = new HashMap();
    private HashMap ties = new HashMap();

    // Number of weeks entrant played
    private HashMap weeksPlayed = new HashMap();

    // Number of scores that the entrant has considered when
    // dropping the lowest two scores.  This is slightly different
    // from the number of Weeks played because a Bowl Week entry
    // counts as two separate scores.  For those who submit a
    // Bowl Week entry in the CFPOOL, the number of scores will
    // exceed the number of weeks played by one.  There should
    // never be any difference in the NFL Pool.
    private HashMap numScores = new HashMap();

    // Number of weekly Wins
    private HashMap weeklyWins = new HashMap();

    // Number of free entries
    private HashMap freeEntries = new HashMap();

    // The set of all WeeklyResults
    private ArrayList allResults = new ArrayList();

    // Highest Weekly Score
    public ArrayList highestWeeklyScore = new ArrayList();
    public int highestWeeklyScoreValue = 0;

    // Biggest winning margin
    public ArrayList biggestWinningMargin = new ArrayList();
    public int biggestWinningMarginValue = 0;

    // Closest Runner-up
    public ArrayList closestRunnerup = new ArrayList();
    public int closestRunnerupValue = 999999;

    // Lowest Weekly Score
    public ArrayList lowestWeeklyScore = new ArrayList();
    public int lowestWeeklyScoreValue = 999999;

    // Biggest Losing Margin
    public ArrayList biggestLosingMargin = new ArrayList();
    public int biggestLosingMarginValue = 0;

    // Lowest Weekly Score that did not earn a free entry
    public ArrayList lowestScoreWithoutFreeEntry = new ArrayList();
    public int lowestScoreWithoutFreeEntryValue = 999999;

    // Highest total points without winning a week
    public ArrayList highestAdjPointsWithoutWin = new ArrayList();
    public int highestAdjPointsWithoutWinValue = 0;

    // Highest point total without dropping a week
    public ArrayList highestGrossPoints = new ArrayList();
    public int highestGrossPointsValue = 0;

    // Lowest point total
    public ArrayList lowestPointTotal = new ArrayList();
    public int lowestPointTotalValue = 999999;

    // Fourth place (just out of the money)
    public ArrayList fourthPlace = new ArrayList();
    public int fourthPlaceValue = 0;

    // Random entry
    public WeeklyResult randomEntry = null;

    // sorted list of entrants based off of the net total points.
    private ArrayList standings = new ArrayList();

    // Highest score for lowest week.
    private int highestLowestWeek = 0;

    // Highest score for second lowest week.
    private int highestSecondLowestWeek = 0;

    public YearToDate(int year, int week) {
        this.year = year;
        if (week == 0) {
            this.week = 25;
        } else {
            this.week = week;
        }
        calculate();
    }

    public boolean isValid() { return valid; }

    public WeeklySchedule getSchedule() { return schedule; }

    public int getHighestLowestWeek() {
        return highestLowestWeek;
    }

    public int getHighestSecondLowestWeek() {
        return highestSecondLowestWeek;
    }

    public Collection getStandings()        { return standings; }

    public int getTotalPoints(Entrant entrant) {
        Integer num =  (Integer)totalPoints.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getLowestWeek(Entrant entrant) {
        Integer num = (Integer)lowestWeek.get(entrant);
        if (num == null) { num = new Integer(999999); }

        return num.intValue();
    }

    public int getSecondLowestWeek(Entrant entrant) {
        Integer num = (Integer)secondLowestWeek.get(entrant);
        if (num == null) { num = new Integer(999999); }

        return num.intValue();
    }

    public int getWins(Entrant entrant) {
        Integer num = (Integer)wins.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getWeeklyWins(Entrant entrant) {
        Integer num = (Integer)weeklyWins.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getFreeEntries(Entrant entrant) {
        Integer num = (Integer)freeEntries.get(entrant);
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

    public int getWeeksPlayed(Entrant entrant) {
        Integer num = (Integer)weeksPlayed.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getNumScores(Entrant entrant) {
        Integer num = (Integer)numScores.get(entrant);
        if (num == null) { num = new Integer(0); }

        return num.intValue();
    }

    public int getYear() { return year; }
    public int getWeek() { return week; }

    public int getHighestGrossPoints() {
        return highestGrossPointsValue;
    }

    public List getHighestGrossPointsList() {
        return (ArrayList)highestGrossPoints.clone();
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

    public void calculate() {

        // The maximum number of scores for an Entrant.
        int maxNumScores = 0;

        for (int i=0; i<week; i++) {

            WeeklyResult first = null;
            WeeklyResult second = null;
            WeeklyResult secondlast = null;
            WeeklyResult last = null;

            Iterator iter = home.findByYearWeek(year, i+1).iterator();
            if (!iter.hasNext()) {
                // valid = false;
                week = i;
                break;
            }

            schedule = WeeklySchedule.getHome().findByYearWeek(year, i+1);

            maxNumScores += schedule.isSplit() ? 2 : 1;

            int topScore = 0;
            while (iter.hasNext()) {
                WeeklyResult result = (WeeklyResult)iter.next();
                Entrant entrant = result.getEntrant();

                if (!standings.contains(entrant)) {
                    standings.add(entrant);
                }

                if ((topScore == 0 || result.getScore() == topScore) &&
                    !entrant.isGenerated()) {
                    if (topScore == 0) {
                        topScore = result.getScore();
                    }
                    weeklyWins.put(entrant,
                                    new Integer(getWeeklyWins(entrant) + 1));
                }

                weeksPlayed.put(entrant,
                                new Integer(getWeeksPlayed(entrant) + 1));

                totalPoints.put(entrant,
                                new Integer(getTotalPoints(entrant) +
                                            result.getTotalScore())
                               );


                if (schedule.isSplit()) {
                    // For Bowl games, the score is split into two halves
                    // when determining which scores should be dropped.
                    // One is added to the score before dividing it by
                    // two the second time it is considered in case the
                    // score was odd to begin with (the div operation on
                    // integers always truncates the result).  For example,
                    // a 100,000 score for a Bowl Week gets split into
                    // two 50,000 scores when dropping the two low scores.
                    // However, a 100,001 score gets split into a 50,000
                    // and a 50,001 score.
                    checkLowestScore(entrant, result.getTotalScore() / 2);
                    checkLowestScore(entrant, (result.getTotalScore() + 1) / 2);

                    numScores.put(entrant,
                                new Integer(getNumScores(entrant) + 2));
                }
                else {
                    checkLowestScore(entrant, result.getTotalScore());

                    numScores.put(entrant,
                                new Integer(getNumScores(entrant) + 1));
                }

                wins.put(entrant,
                         new Integer(getWins(entrant) + result.getWins()));

                losses.put(entrant,
                       new Integer(getLosses(entrant) + result.getLosses()));

                ties.put(entrant,
                         new Integer(getTies(entrant) + result.getTies()));

                if (!result.getEntrant().isGenerated()) {
                    allResults.add(result);
                    if (first != null && second == null) {
                        if (first.getTotalScore() != result.getTotalScore()) {
                            second = result;
                        }
                    }
                    if (first == null) {
                        first = result;
                    }
                    secondlast = last;
                    last = result;
                }
            }

            if ((i+1) != 17) {
		freeEntries.put(last.getEntrant(),
				new Integer(getFreeEntries(last.getEntrant()) + 1));
		if (last.getTotalScore() == secondlast.getTotalScore()) {
			freeEntries.put(secondlast.getEntrant(),
			  new Integer(getFreeEntries(secondlast.getEntrant()) + 1));
		}
            }

            // Calculate highest weekly score
            int value = first.getTotalScore();
            if (value > highestWeeklyScoreValue) {
                highestWeeklyScoreValue = value;
                highestWeeklyScore.clear();
                highestWeeklyScore.add(first);
                
            }
            else if (value == highestWeeklyScoreValue) {
                highestWeeklyScore.add(first);
            }

            // Calculate biggest winning margin
            value = first.getTotalScore() - second.getTotalScore();
            if (value > biggestWinningMarginValue) {
                biggestWinningMarginValue = value;
                biggestWinningMargin.clear();
                biggestWinningMargin.add(first);
            }
            else if (value == biggestWinningMarginValue) {
                biggestWinningMargin.add(first);
            }

            // Calculate closest runnerup
            value = first.getTotalScore() - second.getTotalScore();
            if (value < closestRunnerupValue) {
                closestRunnerupValue = value;
                closestRunnerup.clear();
                closestRunnerup.add(second);
                
            }
            else if (value == closestRunnerupValue) {
                closestRunnerup.add(second);
            }

            // Calculate lowest weekly score
            value = last.getTotalScore();
            if (value < lowestWeeklyScoreValue) {
                lowestWeeklyScoreValue = value;
                lowestWeeklyScore.clear();
                lowestWeeklyScore.add(last);
                
            }
            else if (value == lowestWeeklyScoreValue) {
                lowestWeeklyScore.add(last);
            }

            // Calculate biggest losing margin
            value = secondlast.getTotalScore() - last.getTotalScore();
            if (value > biggestLosingMarginValue) {
                biggestLosingMarginValue = value;
                biggestLosingMargin.clear();
                biggestLosingMargin.add(last);
            }
            else if (value == biggestLosingMarginValue) {
                biggestLosingMargin.add(last);
            }

            // Calculate lowest score that did not earn a free entry
            value = secondlast.getTotalScore();
            if (value < lowestScoreWithoutFreeEntryValue) {
                lowestScoreWithoutFreeEntryValue = value;
                lowestScoreWithoutFreeEntry.clear();
                lowestScoreWithoutFreeEntry.add(secondlast);
            }
            else if (value == lowestScoreWithoutFreeEntryValue) {
                lowestScoreWithoutFreeEntry.add(secondlast);
            }
        }

        Iterator iter = getStandings().iterator();
        while(iter.hasNext()) {
            Entrant entrant = (Entrant)iter.next();
            int numScores = getNumScores(entrant);
            if (numScores == (maxNumScores-1)) {
                secondLowestWeek.put(entrant,
                                     new Integer(getLowestWeek(entrant)));
                lowestWeek.put(entrant, new Integer(0));
            }
            else if (numScores < maxNumScores) {
                lowestWeek.put(entrant, new Integer(0));
                secondLowestWeek.put(entrant, new Integer(0));
            }

            if (getLowestWeek(entrant) == 999999) {
                lowestWeek.put(entrant, new Integer(0));
            }
            if (getSecondLowestWeek(entrant) == 999999) {
                secondLowestWeek.put(entrant, new Integer(0));
            }

            // Dont include special entants in special awards
            if (entrant.isGenerated()) {
                continue;
            }

            // Calculate highest adj point total that did not win a week
            int value = getTotalPoints(entrant) -
                        getSecondLowestWeek(entrant) -
                        getLowestWeek(entrant);
            if (value > 0) {
                if (value > highestAdjPointsWithoutWinValue &&
                                            getWeeklyWins(entrant) == 0) {
                    highestAdjPointsWithoutWinValue = value;
                    highestAdjPointsWithoutWin.clear();
                    highestAdjPointsWithoutWin.add(entrant);
                }
                else if (value == highestAdjPointsWithoutWinValue &&
                                            !weeklyWins.containsKey(entrant)) {
                    highestAdjPointsWithoutWin.add(entrant);
                }
            }

            // Calculate highest total points without dropping a week
            value = getTotalPoints(entrant);
            if (value > highestGrossPointsValue) {
                highestGrossPointsValue = value;
                highestGrossPoints.clear();
                highestGrossPoints.add(entrant);
            }
            else if (value == highestGrossPointsValue) {
                highestGrossPoints.add(entrant);
            }

            // Calculate lowest adj points min 15 weeks
            if (week > 2 && getWeeksPlayed(entrant) >= week-2) {
                value = getTotalPoints(entrant) -
                            getSecondLowestWeek(entrant) -
                            getLowestWeek(entrant);
                if (value < lowestPointTotalValue) {
                    lowestPointTotalValue = value;
                    lowestPointTotal.clear();
                    lowestPointTotal.add(entrant);
                }
                else if (value == lowestPointTotalValue) {
                    lowestPointTotal.add(entrant);
                }
            }
        }

        Iterator lwValues = lowestWeek.values().iterator();
        while (lwValues.hasNext()) {
            final int lw = ((Integer)lwValues.next()).intValue();
            if (lw > highestLowestWeek) {
                highestLowestWeek = lw;
            }
        }

        Iterator slwValues = secondLowestWeek.values().iterator();
        while (slwValues.hasNext()) {
            final int slw = ((Integer)slwValues.next()).intValue();
            if (slw > highestSecondLowestWeek) {
                highestSecondLowestWeek = slw;
            }
        }

        if (allResults.size() == 0) {
            return;
        }

        // Get the random entry
        Random rand = new Random(System.currentTimeMillis());
        randomEntry = (WeeklyResult)allResults.get(rand.nextInt(allResults.size()));

        Collections.sort(standings, standingsComparator);

        // Calculate 4th place
        iter = getStandings().iterator();
        int index = 0;
        while(iter.hasNext()) {
            Entrant entrant = (Entrant)iter.next();
            if (entrant.isGenerated()) {
                continue;
            }
            index++;
            int value = getTotalPoints(entrant) -
                        getSecondLowestWeek(entrant) -
                        getLowestWeek(entrant);
            if (index >= 4 && value != 0) {
                if (value > fourthPlaceValue) {
                    fourthPlaceValue = value;
                    fourthPlace.clear();
                    fourthPlace.add(entrant);
                }
                else if (value == fourthPlaceValue) {
                    fourthPlace.add(entrant);
                }
            }
        }

        return;
    }

    /**
     * Keep track of whether this is one of the two
     * lowest scores so far for the given entrant.
     * This has been split off into a separate convenience
     * function because the Bowl Week for the CFPOOL
     * counts as two weeks in terms of dropping the
     * low scores.
     */
    private void checkLowestScore(Entrant entrant, int score) {

        final int lowest = getLowestWeek(entrant);
        final int secondlowest = getSecondLowestWeek(entrant);

        if (score < lowest) {
            secondLowestWeek.put(entrant, new Integer(lowest));
            lowestWeek.put(entrant, new Integer(score));
        } else if (score < secondlowest) {
            secondLowestWeek.put(entrant, new Integer(score));
        }
    }

    private final Comparator standingsComparator = new Comparator() {
        public int compare(Object a, Object b) {

            Entrant ea = (Entrant)a;
            Entrant eb = (Entrant)b;

            int totPtsa = getTotalPoints(ea);
            int totPtsb = getTotalPoints(eb);
            int netPtsa = getTotalPoints(ea) -
                          getLowestWeek(ea) -
                          getSecondLowestWeek(ea);
            int netPtsb = getTotalPoints(eb) -
                          getLowestWeek(eb) -
                          getSecondLowestWeek(eb);

            if (netPtsa == netPtsb) {
                return totPtsb - totPtsa;
            }
            else {
                return netPtsb - netPtsa;
            }
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

        Element root = new Element("YearToDate");
        root.addContent(new Element("Title").setText(name));
        root.addContent(new Element("Year").setText(Integer.toString(year)));
        root.addContent(new Element("Week").setText(Integer.toString(week)));
        root.addContent(new Element("Url").setText(url));

        root.addContent(new Element("WeekName").setText(schedule.getLabel()));

        int rank = 1;
        Iterator siter = getStandings().iterator();
        while (siter.hasNext()) {
            Entrant entrant = (Entrant)siter.next();

            Element e = new Element("Entrant");
            e.addContent(new Element("Name").setText(
                                 entrant.getUsername()));
            if (entrant.isGenerated()) {
                e.addContent(new Element("Rank").setText(""));
            }
            else {
                e.addContent(new Element("Rank").setText(
                                     Integer.toString(rank)));
                rank++;
            }
            e.addContent(new Element("WeeksPlayed").setText(
                                 String.valueOf(getWeeksPlayed(entrant))));
            e.addContent(new Element("WeeksWon").setText(
                                 String.valueOf(getWeeklyWins(entrant))));
            e.addContent(new Element("WeeksLast").setText(
                                 String.valueOf(getFreeEntries(entrant))));
            e.addContent(new Element("Wins").setText(
                                 String.valueOf(getWins(entrant))));
            e.addContent(new Element("Losses").setText(
                                 String.valueOf(getLosses(entrant))));
            e.addContent(new Element("Ties").setText(
                                 String.valueOf(getTies(entrant))));
            e.addContent(new Element("TotalPts").setText(
                schedule.getNumberFormat().format(getTotalPoints(entrant))));
            e.addContent(new Element("WorstWeek").setText(
                schedule.getNumberFormat().format(getLowestWeek(entrant))));
            e.addContent(new Element("SecondWorst").setText(
              schedule.getNumberFormat().format(getSecondLowestWeek(entrant))));
            e.addContent(new Element("AdjTotal").setText(
                schedule.getNumberFormat().format(
                                                getTotalPoints(entrant) -
                                                getSecondLowestWeek(entrant) -
                                                getLowestWeek(entrant))));
            root.addContent(e);
        }

        ListIterator iter = highestWeeklyScore.listIterator();
        while (iter.hasNext()) {
            WeeklyResult wr = (WeeklyResult)iter.next();
            Element e = new Element("HighestWeeklyScore");
            e.addContent(new Element("Name").setText(
                                 wr.getEntrant().getUsername()));
            e.addContent(new Element("Week").setText(
                                 String.valueOf(wr.getWeek())));
            e.addContent(new Element("Score").setText(
                schedule.getNumberFormat().format(highestWeeklyScoreValue)));
            root.addContent(e);
        }

        iter = biggestWinningMargin.listIterator();
        while (iter.hasNext()) {
            WeeklyResult wr = (WeeklyResult)iter.next();
            Element e = new Element("BiggestWinningMargin");
            e.addContent(new Element("Name").setText(
                                 wr.getEntrant().getUsername()));
            e.addContent(new Element("Week").setText(
                                 String.valueOf(wr.getWeek())));
            e.addContent(new Element("Score").setText(
                schedule.getNumberFormat().format(biggestWinningMarginValue)));
            root.addContent(e);
        }

        iter = closestRunnerup.listIterator();
        while (iter.hasNext()) {
            WeeklyResult wr = (WeeklyResult)iter.next();
            Element e = new Element("ClosestRunnerup");
            e.addContent(new Element("Name").setText(
                                 wr.getEntrant().getUsername()));
            e.addContent(new Element("Week").setText(
                                 String.valueOf(wr.getWeek())));
            e.addContent(new Element("Score").setText(
                schedule.getNumberFormat().format(closestRunnerupValue)));
            root.addContent(e);
        }

        iter = lowestWeeklyScore.listIterator();
        while (iter.hasNext()) {
            WeeklyResult wr = (WeeklyResult)iter.next();
            Element e = new Element("LowestWeeklyScore");
            e.addContent(new Element("Name").setText(
                                 wr.getEntrant().getUsername()));
            e.addContent(new Element("Week").setText(
                                 String.valueOf(wr.getWeek())));
            e.addContent(new Element("Score").setText(
                schedule.getNumberFormat().format(lowestWeeklyScoreValue)));
            root.addContent(e);
        }

        iter = lowestScoreWithoutFreeEntry.listIterator();
        while (iter.hasNext()) {
            WeeklyResult wr = (WeeklyResult)iter.next();
            Element e = new Element("LowestScoreWithoutFreeEntry");
            e.addContent(new Element("Name").setText(
                                 wr.getEntrant().getUsername()));
            e.addContent(new Element("Week").setText(
                                 String.valueOf(wr.getWeek())));
            e.addContent(new Element("Score").setText(
                schedule.getNumberFormat().format(lowestScoreWithoutFreeEntryValue)));
            root.addContent(e);
        }

        iter = highestAdjPointsWithoutWin.listIterator();
        while (iter.hasNext()) {
            Entrant entrant = (Entrant)iter.next();
            Element e = new Element("HighestAdjPointsWithoutWin");
            e.addContent(new Element("Name").setText(
                                 entrant.getUsername()));
            e.addContent(new Element("Score").setText(
                schedule.getNumberFormat().
                    format(highestAdjPointsWithoutWinValue)));
            root.addContent(e);
        }

        iter = highestGrossPoints.listIterator();
        while (iter.hasNext()) {
            Entrant entrant = (Entrant)iter.next();
            Element e = new Element("HighestGrossPoints");
            e.addContent(new Element("Name").setText(
                                 entrant.getUsername()));
            e.addContent(new Element("Score").setText(
                schedule.getNumberFormat().format(highestGrossPointsValue)));
            root.addContent(e);
        }

        iter = lowestPointTotal.listIterator();
        while (iter.hasNext()) {
            Entrant entrant = (Entrant)iter.next();
            Element e = new Element("LowestPointTotal");
            e.addContent(new Element("Name").setText(
                                 entrant.getUsername()));
            e.addContent(new Element("Score").setText(
                schedule.getNumberFormat().format(lowestPointTotalValue)));
            root.addContent(e);
        }

        iter = fourthPlace.listIterator();
        while (iter.hasNext()) {
            Entrant entrant = (Entrant)iter.next();
            Element e = new Element("FourthPlace");
            e.addContent(new Element("Name").setText(
                                 entrant.getUsername()));
            e.addContent(new Element("Score").setText(
                schedule.getNumberFormat().format(fourthPlaceValue)));
            root.addContent(e);
        }

        Element e = new Element("RandomEntry");
        e.addContent(new Element("Name").setText(
                             randomEntry.getEntrant().getUsername()));
        e.addContent(new Element("Week").setText(
                             String.valueOf(randomEntry.getWeek())));
        root.addContent(e);

        Document doc = new Document(root);
        return doc;
    }

    public void print(PrintWriter pw) {
        pw.println("Text version of YearToDate not available.");
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("Usage: com.talient.football.reports.YearToDate <year> <week>"); 
            System.exit(0);
        }

        com.talient.football.jdbc.JDBCHomes.setHomes();

        YearToDate stats = new YearToDate(Integer.parseInt(args[0]),
                                          Integer.parseInt(args[1]));

        Iterator iter = stats.getStandings().iterator();
        while (iter.hasNext()) {
            Entrant entrant = (Entrant)iter.next();
            System.out.println(entrant.getUsername() + " \t" +
                stats.getTotalPoints(entrant) + " - " +
                stats.getLowestWeek(entrant) + " - " +
                stats.getSecondLowestWeek(entrant) + " \t" +
                (stats.getTotalPoints(entrant) -
                 stats.getLowestWeek(entrant) -
                 stats.getSecondLowestWeek(entrant)));
        }
        System.out.println("Valid = " + stats.isValid());
    }
}

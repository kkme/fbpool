// CVS ID: $Id: WeeklySchedule.java,v 1.3 2008-11-18 15:16:52 husker Exp $

package com.talient.football.entities;

import java.util.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Properties;

import org.jdom.output.XMLOutputter;
import org.jdom.Document;
import org.jdom.Element;

/**
 * <p>
 * @author Steve Schmadeke
 * @version $Version: $
 */
public class WeeklySchedule {

    public static interface WeeklyScheduleHome {
        public int store(WeeklySchedule schedule);
        public Collection findByYear(int year);
        public WeeklySchedule findByYearWeek(int year, int week);
    }

    public static WeeklyScheduleHome getHome() {
        return home;
    }

    public static void setHome(WeeklyScheduleHome home) {
        WeeklySchedule.home = home;
    }

    // The default implementation does nothing.
    private static WeeklyScheduleHome home = new WeeklyScheduleHome() {
        public int store(WeeklySchedule schedule) {
            throw new UnsupportedOperationException(
                "store() not supported by default " +
                "WeeklySchedule Home interface");
        }
        public Collection findByYear(int year) {
            throw new UnsupportedOperationException(
                "findByYear() not supported by default " +
                "WeeklySchedule Home interface");
        }
        public WeeklySchedule findByYearWeek(int year, int week) {
            throw new UnsupportedOperationException(
                "findByYearWeek() not supported by default " +
                "WeeklySchedule Home interface");
        }
    };

    // The primary keys to the class.
    private final int year;
    private final int week;

    // All of the Games in this set.
    private final HashSet gameSet = new HashSet();

    // All of the Games in this ArrayList.
    private final ArrayList gameArray = new ArrayList();

    // Map of Teams to the corresponding Game.
    private final HashMap teamMap = new HashMap();

    // Map of Teams to their abbreviation
    private final HashMap abbrevMap = new HashMap();

    // Number of points available for the week.
    private int totalPoints = 0;

    // Number of bonus points to be awarded to the week's winner.
    private int bonusPoints = 0;

    // Factor to use when calculating confidence weights for the week.
    private double factor = 1.1;

    // Flag indicating whether the week should be split according to Bowl
    // rules when deciding which scores to drop.
    private boolean isSplit = false;

    // Flag indicating whether this is the last week of the season (to
    // be deprecated once we have a YearlySchedule entity).
    private boolean isFinal = false;

    // Flag indicating the label of the Week.  Will be "Bowls", "First Half
    // Bowls", "Second Half Bowls" or just "Week #", with the proper number.
    private String label = null;

    // Cached calculation of weights for the week.
    private int[] weights = null;

    public WeeklySchedule(int year, int week) {
        this.year = year;
        this.week = week;
        setLabel("Week #" + week);
    }

    public boolean equals(Object other) {
        return (this.getYear() == ((WeeklySchedule)other).getYear() &&
                this.getWeek() == ((WeeklySchedule)other).getWeek()
               );
    }

    public int getYear() { return year; }
    public int getWeek() { return week; }

    public void add(Game game) {

        if (game == null) {
            throw new IllegalGameException(
                "Attempted to add null game to schedule");
        }

        if (!gameSet.contains(game)) {

            // Check if the game has the right year and week
            if (game.getYear() != getYear() ||
                game.getWeek() != getWeek()) {
                throw new IllegalGameException(
                    "Game year / week conflicts with schedule\n" +
                    "\n    Game: " + game +
                    "\n    Schedule Year/Week: " + getYear() + "/" + getWeek());
            }

            // Check to see if new game conflicts with
            // previous game entered for home team.

            Object o = teamMap.get(game.getHome());
            if (o instanceof Game) {
                if (game.getHome() != ((Game)o).getHome() ||
                    game.getVisitor() != ((Game)o).getVisitor()) {
                    throw new IllegalGameException(
                        "Game conflicts with previous game in schedule\n" +
                        "\n    New Game: " + game +
                        "\n    Old Game: " + o);
                }
            }

            // Check to see if new game conflicts with
            // previous game entered for visiting team.

            o = teamMap.get(game.getVisitor());
            if (o instanceof Game) {
                if (game.getHome() != ((Game)o).getHome() ||
                    game.getVisitor() != ((Game)o).getVisitor()) {
                    throw new IllegalGameException(
                        "Game conflicts with previous game in schedule\n" +
                        "\n    New Game: " + game +
                        "\n    Old Game: " + o);
                }
            }

            teamMap.put(game.getHome(), game);
            teamMap.put(game.getVisitor(), game);
            gameSet.add(game);
            gameArray.add(game);
        }
    }

    public void remove(Game game) {
        if (game != null) {
            teamMap.remove(game.getHome());
            teamMap.remove(game.getVisitor());
            gameSet.remove(game);
            gameArray.remove(game);
        }
    }

    public boolean contains(Game game) {
        return gameSet.contains(game);
    }

    public boolean contains(Team team) {
        return teamMap.containsKey(team);
    }

    public Team getOpponent(Team team) {
        Object o = teamMap.get(team);
        if (o instanceof Game) {
            Game game = (Game)o;
             if (game.getHome().equals(team)) {
                 return game.getVisitor();
             } else {
                 return game.getHome();
             }
        } else {
            throw new NoSuchElementException(
                "Team (" + team + ") not in schedule");
        }
    }

    public TeamResult getResult(Team team) {
        Object o = teamMap.get(team);
        if (o instanceof Game) {
            Game game = (Game)o;
            if (game.getResult().equals(GameResult.UNDETERMINED)) {
                return TeamResult.UNDETERMINED;
            } else if (game.getResult().equals(GameResult.DROPPED)) {
                return TeamResult.DROPPED;
            } else if (game.getResult().equals(GameResult.TIE)) {
                return TeamResult.TIED;
            } else if (game.getResult().equals(GameResult.HOME) &&
                       game.getHome().equals(team) ||
                       game.getResult().equals(GameResult.VISITOR) &&
                       game.getVisitor().equals(team)) {
                return TeamResult.WON;
            } else {
                return TeamResult.LOST;
            }
        } else {
            throw new NoSuchElementException(
                "Team (" + team + ") not in schedule");
        }
    }

    public Date getStartTime() {
        Date startTime = null;
        Collection glist = games();
        Iterator iter = glist.iterator();
        while (iter.hasNext()) {
            Game game = (Game)iter.next();
            if (startTime == null || startTime.after(game.getStartTime())) {
                startTime = game.getStartTime();
            }
        }
        return startTime;
    }

    public int size() {
        return gameSet.size();
    }

    public Collection games() {
        return (Collection)gameSet.clone();
    }

    public List orderedGames() {
        return (List)gameArray.clone();
    }

    public boolean allGamesValidated() {
        Collection glist = games();
        Iterator iter = glist.iterator();
        while (iter.hasNext()) {
            Game game = (Game)iter.next();
            if (game.getGameState() == Game.DROPPED) {
                continue;
            }
            if (game.getGameState() != Game.VALIDATED) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return gameSet.toString();
    }

    public int getTotalPoints() {
        initializeToDefaults();
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getBonusPoints() {
        initializeToDefaults();
        return bonusPoints;
    }

    public void setBonusPoints(int bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public boolean isSplit() {
        initializeToDefaults();
        return isSplit;
    }

    public void setSplit(boolean isSplit) {
        this.isSplit = isSplit;
    }

    public boolean isFinal() {
        initializeToDefaults();
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public String getLabel() {
        initializeToDefaults();
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int[] getWeights() {

        if (weights != null) { return weights; }

        double factorSum = 0.0;
        double currentFactor = 1.0;
        final int total = getTotalPoints();

        int games = 0;
        for (Iterator i = games().iterator(); i.hasNext(); ) {
            if (((Game)i.next()).getResult() != GameResult.DROPPED) {
                games++;
            }
        }

        for (int i = 0; i < games; i++) {
            factorSum += currentFactor;
            currentFactor *= factor;
        }

        weights = new int[games];

        double currentWeight = total / factorSum;
        int weightSum = 0;

        for (int i = games - 1; i >= 0; i--) {
            weights[i] = (int)Math.round(currentWeight);
            weightSum += weights[i];
            currentWeight *= factor;
        }

        // Correct for accumulated rounding errors.
        weights[0] += total - weightSum;

        return weights;
    }

    public void invalidateWeights() {
        weights = null;
    }

    public String getAbbrev(Team team) {
        if (size() == 0) {
            return "";
        }

        // If the ShortName for team is not a number (CFPOOL short names are
        // the team IDs) then return the short name.  Otherwise generate
        // a 3 character short name for CFPOOL.
        try {
            Integer.parseInt(team.getShortName());
        }
        catch (NumberFormatException e) {
            return team.getShortName();
        }

        // If abbrevMap has been calculated then return the abbreviation
        if (abbrevMap.size() > 0) {
            return (String)abbrevMap.get(team);
        }

        // Calculate the abbreviation for each team.
        for (int i=0; i<gameArray.size(); i++) {
            Game game = (Game)gameArray.get(i);
            abbrevMap.put(game.getVisitor(), Integer.toString(i+1) + "V");
            abbrevMap.put(game.getHome(), Integer.toString(i+1) + "H");
        }
        return (String)abbrevMap.get(team);
    }

    public Document getJDOM(Entry consensus) {
        Element root = new Element("Games");
        for (int i=0; i<size(); i++) {
            Game game = (Game)gameArray.get(i);
            // getStateStr returns null if the game has been dropped.
            if (getStateStr(game.getHome(), consensus) == null) {
                continue;
            }
            Element g = new Element("Game");
            SimpleDateFormat df = new SimpleDateFormat("EEE. h:mm");
            g.addContent(new Element("Time").setText(
                                   df.format(game.getStartTime())));
            g.addContent(new Element("Vname").setText(
                                   game.getVisitor().getLongName()));
            g.addContent(new Element("Vabbrev").setText(
                                   getAbbrev(game.getVisitor())));
            g.addContent(new Element("Hname").setText(
                                   game.getHome().getLongName()));
            g.addContent(new Element("Habbrev").setText(
                                   getAbbrev(game.getHome())));
            g.addContent(new Element("Vscore").setText(
                                   Integer.toString(game.getVisitorScore())));
            g.addContent(new Element("Hscore").setText(
                                   Integer.toString(game.getHomeScore())));
            g.addContent(new Element("Hstate").setText(
                                   getStateStr(game.getHome(), consensus)));
            g.addContent(new Element("Vstate").setText(
                                   getStateStr(game.getVisitor(), consensus)));
            if (game.getGameState() == Game.FINAL ||
                game.getGameState() == Game.VALIDATED) {
                if (game.getQuarter() == 5) {
                    g.addContent(new Element("State").setText("Final OT"));
                }
                else {
                    g.addContent(new Element("State").setText("Final"));
                }
            }
            else {
                String clock = " " + (game.getGameClock() / 60) + ":" +
                    (((game.getGameClock() % 60) < 10) ? "0" : "") +
                    (game.getGameClock() % 60);
                String gstate;
                switch (game.getQuarter()) {
                    case 0:
                        gstate = "";
                        break;
                    case 1:
                        gstate = "1st Qtr" + clock;
                        break;
                    case 2:
                        gstate = "2nd Qtr" + clock;
                        break;
                    case 3:
                        gstate = "3rd Qtr" + clock;
                        break;
                    case 4:
                        gstate = "4th Qtr" + clock;
                        break;
                    default:
                        gstate = "OT" + clock;
                        break;
                }
                g.addContent(new Element("State").setText(gstate));
            }

            root.addContent(g);
        }
        return new Document(root);
    }

    public String getStateStr(Team team, Entry consensus) {
        String wlt;
        String aw;
        TeamResult result = getResult(team);
        if (result == TeamResult.DROPPED) {
            return null;
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
        if (consensus.contains(team)) {
            aw = "wc";
        } else {
            aw = "ac";
        }
        return  wlt + aw;
    }

    /**
     * If the number of points in a week are greater than 10000, we
     * are going to assume that this is a CFPOOL week and to use the
     * NumberFormat that divides by 100 before formatting the number.
     * Otherwise we'll return the native NumberFormat for the locale.
     */
    public NumberFormat getNumberFormat() {
        if (getTotalPoints() > 10000) {
            final NumberFormat format = new CfpoolFormat();
            format.setMinimumFractionDigits(2);
            return format;
        } else {
            return NumberFormat.getInstance();
        }
    }

    /**
     * Initialize the totalPoints and bonusPoints attributes based
     * on the number of games contained in the week.  If there
     * are fewer than 17 games, we assume this is an NFL week.
     * More than 17 means a standard college week.  After we get
     * the Database in place to persist these attributes properly,
     * this method goes away.
     */
    private void initializeToDefaults() {
        if (totalPoints != 0) return;

        // Must have a set of games before we can set the defaults.
        if (size() == 0) {
            throw new RuntimeException(
                "Must set totalPoints manually or must add games first.");
        } else if (size() < 17) { // NFL Pool
            totalPoints = 1000;
            if (getWeek() == 17) {
              setFinal(true);
            }
        } else { // CFPOOL

            // CFPOOL changed over the years.  Prior to 2008, there was only
            // one week designated as the Bowl Week, which was worth double
            // with a 100 point bonus and which would be split in two in order
            // to determine which scores should be dropped.  Starting in the
            // 2008 season, there was two separate Bowl weeks worth the normal
            // total score, but with an increased bonus and each of which was
            // considered independently when dropping the two lowest scores for
            // the season.
            // 
            // The number of weeks in a season has varied as well.  1999
            // through 2001 had only thirteen weeks.  2003 had fifteen weeks,
            // being the first year that we split the week before Thanksgiving
            // from the Thanksgiving week itself and being one of the years
            // with an extra week between Labor Day and Thanksgiving.  (2002
            // was as well, but we didn't split the Thanksgiving week entry
            // that year.)  All other years prior to 2008 had fourteen weeks.
            // Because of the extra week in the calendar, 2008 (and 2013 and
            // 2014) has sixteen weeks, the last two of which are the two
            // halves of the Bowl season.  2009 and onward will generally have
            // fifteen weeks, counting the two halves of the Bowl season,
            // except when there is an extra weeks between Labor Day and
            // Thanksgiving.

            if (getWeek() <= 12) {
                // Weeks 1 through 12 are always normal.
                setNormalWeek();

            } else if (getWeek() == 13) {

                // In 1999 through 2001, Week 13 was Bowls.  Otherwise,
                // Week 13 is always normal week.

                if (getYear() == 1999 || getYear() == 2000 ||
                    getYear() == 2001) {
                    setBowls();
                } else {
                    setNormalWeek();
                }

            } else if (getWeek() == 14) {

                // In 2003, 2008, 2013 and 2014, Week 14 is normal week.  In
                // any other year prior to 2009, Week 14 is Bowls.
                // Otherwise, with the exceptions noted above, Week 14 is First
                // Half Bowls.

                if (getYear() == 2003 || getYear() == 2008 ||
                    getYear() == 2013 || getYear() == 2014) {
                    setNormalWeek();
                } else if (getYear() < 2009) {
                    setBowls();
                } else {
                    setFirstHalfBowls();
                }

            } else if (getWeek() == 15) {

                // In 2003, Week 15 was Bowls.  In 2008, 2013 and 2014,
                // Week 15 is First Half Bowls.  Otherwise, Week 15 is
                // Second Half Bowls.

                if (getYear() == 2003) {
                    setBowls();
                } else if (getYear() == 2008 || getYear() == 2013 ||
                           getYear() == 2014) {
                    setFirstHalfBowls();
                } else {
                    setSecondHalfBowls();
                }

            } else if (getWeek() == 16) {

                // Weeks 16 is always Second Half Bowls.
                setSecondHalfBowls();

            } else {

                throw new RuntimeException(
                    "Unexpected CFPOOL week: " + getWeek() + " for Year: " +
                    getYear());
            }
        }
    }

    private void setNormalWeek() {
        totalPoints = 100000;
        bonusPoints = 4000;
        setSplit(false);
        setFinal(false);
        setLabel("Week #" + getWeek());
    }

    private void setBowls() {
        totalPoints = 200000;
        bonusPoints = 10000;
        setSplit(true);
        setFinal(true);
        setLabel("Bowls");
    }

    private void setFirstHalfBowls() {
        totalPoints = 100000;
        bonusPoints = 5000;
        setSplit(false);
        setFinal(false);
        setLabel("Early Bowls");
    }

    private void setSecondHalfBowls() {
        totalPoints = 100000;
        bonusPoints = 5000;
        setSplit(false);
        setFinal(true);
        setLabel("Late Bowls");
    }

    private static class CfpoolFormat extends NumberFormat {

        private NumberFormat target = NumberFormat.getInstance();

        public StringBuffer format(double number,
                                   StringBuffer toAppendTo,
                                   FieldPosition pos) {
            return target.format(number/100, toAppendTo, pos);
        }

        public StringBuffer format(long number,
                                   StringBuffer toAppendTo,
                                   FieldPosition pos) {
            return target.format((double)number/100, toAppendTo, pos);
        }

        public Number parse(String text,
                            ParsePosition parsePosition) {
            Number number = target.parse(text, parsePosition);
            return new Double(number.doubleValue()*100);
        }

        public int getMaximumFractionDigits() {
            return target.getMaximumFractionDigits();
        }

        public int getMinimumFractionDigits() {
            return target.getMinimumFractionDigits();
        }

        public int getMaximumIntegerDigits() {
            return target.getMaximumIntegerDigits();
        }

        public int getMinimumIntegerDigits() {
            return target.getMinimumIntegerDigits();
        }

        public boolean isGroupingUsed() {
            return target.isGroupingUsed();
        }

        public boolean isParseIntegerOnly() {
            return target.isParseIntegerOnly();
        }

        public void setGroupingUsed(boolean newValue) {
            target.setGroupingUsed(newValue);
        }

        public void setParseIntegerOnly(boolean newValue) {
            target.setParseIntegerOnly(newValue);
        }

        public void setMaximumFractionDigits(int newValue) {
            target.setMaximumFractionDigits(newValue);
        }

        public void setMinimumFractionDigits(int newValue) {
            target.setMinimumFractionDigits(newValue);
        }

        public void setMaximumIntegerDigits(int newValue) {
            target.setMaximumIntegerDigits(newValue);
        }

        public void setMinimumIntegerDigits(int newValue) {
            target.setMinimumIntegerDigits(newValue);
        }
    }

    public static void main(String argv[]) {

        final Team[] TEAMS = {
            new Team("One", "One"),
            new Team("Two", "Two"),
            new Team("Three", "Three"),
            new Team("Four", "Four"),
            new Team("Five", "Five"),
            new Team("Six", "Six"),
            new Team("Seven", "Seven"),
            new Team("Eight", "Eight"),
            new Team("Nine", "Nine"),
            new Team("Ten", "Ten"),
            new Team("Eleven", "Eleven"),
            new Team("Twelve", "Twelve"),
            new Team("Thirteen", "Thirteen"),
            new Team("Fourteen", "Fourteen"),
            new Team("Fifteen", "Fifteen"),
            new Team("Sixteen", "Sixteen"),
            new Team("Seventeen", "Seventeen"),
            new Team("Eighteen", "Eighteen"),
            new Team("Nineteen", "Nineteen"),
            new Team("Twenty", "Twenty"),
            new Team("Twenty-one", "Twenty-one"),
            new Team("Twenty-two", "Twenty-two"),
            new Team("Twenty-three", "Twenty-three"),
            new Team("Twenty-four", "Twenty-four"),
            new Team("Twenty-five", "Twenty-five"),
            new Team("Twenty-six", "Twenty-six"),
            new Team("Twenty-seven", "Twenty-seven"),
            new Team("Twenty-eight", "Twenty-eight"),
            new Team("Twenty-nine", "Twenty-nine"),
            new Team("Thirty", "Thirty"),
            new Team("Thirty-one", "Thirty-one"),
            new Team("Thirty-two", "Thirty-two"),
            new Team("Thirty-three", "Thirty-three"),
            new Team("Thirty-four", "Thirty-four"),
            new Team("Thirty-five", "Thirty-five"),
            new Team("Thirty-six", "Thirty-six"),
            new Team("Thirty-seven", "Thirty-seven"),
            new Team("Thirty-eight", "Thirty-eight"),
            new Team("Thirty-nine", "Thirty-nine"),
            new Team("Forty", "Forty"),
            new Team("Forty-one", "Forty-one"),
            new Team("Forty-two", "Forty-two"),
            new Team("Forty-three", "Forty-three"),
            new Team("Forty-four", "Forty-four"),
            new Team("Forty-five", "Forty-five"),
            new Team("Forty-six", "Forty-six"),
            new Team("Forty-seven", "Forty-seven"),
            new Team("Forty-eight", "Forty-eight"),
            new Team("Forty-nine", "Forty-nine"),
            new Team("Fifty", "Fifty")
        };

        WeeklySchedule schedule = new WeeklySchedule(2001, 1);

        for (int i = 0; i < 15; i++) {
            schedule.add(new Game(2001, 1, TEAMS[2*i], TEAMS[2*i+1]));
        }

        NumberFormat f = schedule.getNumberFormat();

        System.out.print("TotalPoints=" + f.format(schedule.getTotalPoints()));
        System.out.print(", Bonus=" + f.format(schedule.getBonusPoints()));
        System.out.println(", isSplit=" + String.valueOf(schedule.isSplit()));

        int[] weights = schedule.getWeights();
        System.out.print("Size=" + schedule.size());
        System.out.println(", weights.length=" + weights.length);
        for (int i = 0; i < weights.length; i++) {
            System.out.println(f.format(weights[i]));
        }

        schedule = new WeeklySchedule(2001, 1);

        for (int i = 0; i < 18; i++) {
            schedule.add(new Game(2001, 1, TEAMS[2*i], TEAMS[2*i+1]));
        }

        f = schedule.getNumberFormat();

        System.out.print("TotalPoints=" + f.format(schedule.getTotalPoints()));
        System.out.print(", Bonus=" + f.format(schedule.getBonusPoints()));
        System.out.println(", isSplit=" + String.valueOf(schedule.isSplit()));

        weights = schedule.getWeights();
        System.out.print("Size=" + schedule.size());
        System.out.println(", weights.length=" + weights.length);
        for (int i = 0; i < weights.length; i++) {
            System.out.println(f.format(weights[i]));
        }

        schedule = new WeeklySchedule(2001, 12);

        for (int i = 0; i < 25; i++) {
            schedule.add(new Game(2001, 12, TEAMS[2*i], TEAMS[2*i+1]));
        }

        f = schedule.getNumberFormat();

        System.out.print("TotalPoints=" + f.format(schedule.getTotalPoints()));
        System.out.print(", Bonus=" + f.format(schedule.getBonusPoints()));
        System.out.println(", isSplit=" + String.valueOf(schedule.isSplit()));

        weights = schedule.getWeights();
        System.out.print("Size=" + schedule.size());
        System.out.println(", weights.length=" + weights.length);
        for (int i = 0; i < weights.length; i++) {
            System.out.println(f.format(weights[i]));
        }

        schedule = new WeeklySchedule(2001, 13);

        for (int i = 0; i < 25; i++) {
            schedule.add(new Game(2001, 13, TEAMS[2*i], TEAMS[2*i+1]));
        }

        f = schedule.getNumberFormat();

        System.out.print("TotalPoints=" + f.format(schedule.getTotalPoints()));
        System.out.print(", Bonus=" + f.format(schedule.getBonusPoints()));
        System.out.println(", isSplit=" + String.valueOf(schedule.isSplit()));

        weights = schedule.getWeights();
        System.out.print("Size=" + schedule.size());
        System.out.println(", weights.length=" + weights.length);
        for (int i = 0; i < weights.length; i++) {
            System.out.println(f.format(weights[i]));
        }

        ((Game)schedule.orderedGames().get(0)).setGameState(Game.DROPPED);
        schedule.invalidateWeights();

        System.out.print("TotalPoints=" + f.format(schedule.getTotalPoints()));
        System.out.print(", Bonus=" + f.format(schedule.getBonusPoints()));
        System.out.println(", isSplit=" + String.valueOf(schedule.isSplit()));

        weights = schedule.getWeights();
        System.out.print("Size=" + schedule.size());
        System.out.println(", weights.length=" + weights.length);
        for (int i = 0; i < weights.length; i++) {
            System.out.println(f.format(weights[i]));
        }

        schedule.invalidateWeights();
        Date start = new Date();
        for (int i = 0; i < 50; i++) {
            weights = schedule.getWeights();
            schedule.invalidateWeights();
        }
        Date end = new Date();
        System.out.println("time for 50 getWeight() calls with invalidating=" +
            String.valueOf(end.getTime() - start.getTime()));

        schedule.invalidateWeights();
        start = new Date();
        for (int i = 0; i < 5000; i++) {
            weights = schedule.getWeights();
        }
        end = new Date();
        System.out.println("time for 5000 calls without invalidating=" +
            String.valueOf(end.getTime() - start.getTime()));
    }
}

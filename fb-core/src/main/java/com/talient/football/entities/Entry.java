// SCCS ID: @(#) 08/23/98 1.1 Entry.java

package com.talient.football.entities;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class Entry {

    public Entry(WeeklySchedule schedule, Entrant entrant) {
        this.schedule = schedule;
        this.entrant = entrant;
    }

    public boolean equals(Object other) {
        return (this.getEntrant().equals(((Entry)other).getEntrant()) &&
                this.getSchedule().equals(((Entry)other).getSchedule())
               );
    }

    public void add(Team team) {

        if (teams.contains(team)) {
            throw new IllegalEntryException(
                "Team (" + team + ") already part of entry");
        }

        if (opponents.contains(team)) {
            throw new IllegalEntryException(
                "Opponent of team (" + team + ") already part of entry");
        }

        invalidateScores();
        teams.add(team);
        opponents.add(schedule.getOpponent(team));
    }

    public void add(int index, Team team) {

        if (teams.contains(team)) {
            throw new IllegalEntryException(
                "Team (" + team + ") already part of entry");
        }

        if (opponents.contains(team)) {
            throw new IllegalEntryException(
                "Opponent of team (" + team + ") already part of entry");
        }

        invalidateScores();
        teams.add(index, team);
        opponents.add(schedule.getOpponent(team));
    }

    public boolean contains(Team team) {
        return teams.contains(team);
    }

    public Team get(int index) {
        return (Team)teams.get(index);
    }

    public int indexOf(Team team) {
        return teams.indexOf(team);
    }

    public void remove(int index) {
        invalidateScores();
        opponents.remove(schedule.getOpponent(get(index)));
        teams.remove(index);
    }

    public void remove(Team team) {
        invalidateScores();
        opponents.remove(schedule.getOpponent(team));
        teams.remove(team);
    }

    public int size() {
        return teams.size();
    }

    public void setSchedule(WeeklySchedule schedule) {
        invalidateScores();
        this.schedule = schedule;
    }

    public WeeklySchedule getSchedule() {
        return schedule;
    }

    public int getScore() {
        calculateScores();
        return score;
    }

    public boolean isFinalScore() {
        calculateScores();
        if (score == 0 && maximumScore == 0) {
            return false;
        }
        return (score == maximumScore);
    }

    public int getMaximumScore() {
        calculateScores();
        return maximumScore;
    }

    public void setEntrant(Entrant entrant) {
        this.entrant = entrant;
    }

    public Entrant getEntrant() {
        return entrant;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(getClass().getName());
        str.append("[");
        str.append("Entrant="+entrant.getUsername());
        str.append(",scoresValid="+scoresValid);
        str.append(",score="+score);
        str.append(",maximum score="+maximumScore);
        str.append(",teams="+teams);
        str.append(",schedule="+schedule);
        str.append("]");

        return str.toString();
    }

    public void invalidateScores() {
        scoresValid = false;
        score = 0;
        maximumScore = 0;
        if (schedule != null) {
            schedule.invalidateWeights();
        }
    }

    public TeamResult getResult(Team team) {
        if (schedule != null) {
            return schedule.getResult(team);
        }
        return TeamResult.UNDETERMINED;
    }

    private void calculateScores() {
        if (scoresValid) { return; }

        if (teams.size() != schedule.size()) {
            throw new IllegalEntryException(
                "Entry does not have same number of teams (" +
                teams.size() + ") as schedule has games (" +
                schedule.size() + ")");
        }

        int weights[] = schedule.getWeights();
        int widx = weights.length - 1;

        boolean fullTies = true;
        score = 0;
        maximumScore = 0;

        for (int i = teams.size() - 1; i >= 0; i--) {
            TeamResult result = schedule.getResult((Team)teams.get(i));
            if (result == TeamResult.DROPPED) {
                continue;
            }
            int weight = weights[widx];
            if (result == TeamResult.UNDETERMINED) {
                maximumScore += weight;
                fullTies = false;
            } else if (result == TeamResult.WON) {
                score += weight;
                maximumScore += weight;
                fullTies = false;
            } else if (result == TeamResult.LOST) {
                fullTies = false;
            } else if (result == TeamResult.TIED) {
                if (!fullTies) {
                    weight = (int)Math.round(Math.ceil(weight/2.0));
                }
                score += weight;
                maximumScore += weight;
            }
            widx--;
        }

        if (widx >= 0) { // Weights array does not match picked games
            throw new IllegalEntryException(
                "Entry does not have same number of teams (" +
                teams.size() + ") as weights array has values (" +
                weights.length + ")");
        }

        scoresValid = true;
    }

    private WeeklySchedule schedule = null;

    private Entrant entrant = null;

    private final ArrayList teams = new ArrayList(16);

    private final HashSet opponents = new HashSet();

    private boolean scoresValid = false;

    private int score = 0;

    private int maximumScore = 0;

    public static void main(String argv[]) {

        // Actual data from Week 13, 1997.  Chris scored 664 points.

        Entrant chris = new Entrant("Chris Humphery", "chris@talient.com");

        Team arizona =      new Team("Arizona",       "Ari");
        Team baltimore =    new Team("Baltimore",     "Bal");
        Team buffalo =      new Team("Buffalo",       "Buf");
        Team tennessee =    new Team("Tennessee",     "Ten");
        Team carolina =     new Team("Carolina",      "Car");
        Team saintlouis =   new Team("Saint Louis",   "Stl");
        Team dallas =       new Team("Dallas",        "Dal");
        Team greenbay =     new Team("Green Bay",     "GB");
        Team indianapolis = new Team("Indianapolis",  "Ind");
        Team detroit =      new Team("Detroit",       "Det");
        Team jacksonville = new Team("Jacksonville",  "Jac");
        Team cincinnati =   new Team("Cincinnati",    "Cin");
        Team kansascity =   new Team("Kansas City",   "KC");
        Team seatle =       new Team("Seatle",        "Sea");
        Team miami =        new Team("Miami",         "Mia");
        Team newengland =   new Team("New England",   "NE");
        Team minnesota =    new Team("Minnesota",     "Min");
        Team nyjets =       new Team("NY Jets",       "NYJ");
        Team neworleans =   new Team("New Orleans",   "NO");
        Team atlanta =      new Team("Atlanta",       "Atl");
        Team pittsburgh =   new Team("Pittsburgh",    "Pit");
        Team philadelphia = new Team("Philadelphia",  "Phl");
        Team sandiego =     new Team("San Diego",     "SD");
        Team sanfrancisco = new Team("San Francisco", "SF");
        Team tampabay =     new Team("Tampa Bay",     "TB");
        Team chicago =      new Team("Chicago",       "Chi");
        Team nygiants =     new Team("NY Giants",     "NYG");
        Team washington =   new Team("Washington",    "Was");
        Team oakland =      new Team("Oakland",       "Oak");
        Team denver =       new Team("Denver",        "Den");

        WeeklySchedule s = new WeeklySchedule(1999, 1);

        Game g = new Game(1999, 1, arizona, baltimore);
        // g.setResult(GameResult.VISITOR);
        g.setGameState(Game.FINAL);
        g.setVisitorScore(7);
        s.add(g);

        g = new Game(1999, 1, buffalo, tennessee);
        g.setGameState(Game.FINAL);
        g.setHomeScore(7);
        s.add(g);

        g = new Game(1999, 1, carolina, saintlouis);
        g.setGameState(Game.FINAL);
        g.setVisitorScore(7);
        s.add(g);

        g = new Game(1999, 1, dallas, greenbay);
        g.setGameState(Game.FINAL);
        g.setHomeScore(7);
        s.add(g);

        g = new Game(1999, 1, indianapolis, detroit);
        g.setGameState(Game.FINAL);
        g.setHomeScore(7);
        s.add(g);

        g = new Game(1999, 1, jacksonville, cincinnati);
        g.setGameState(Game.FINAL);
        g.setHomeScore(7);
        s.add(g);

        g = new Game(1999, 1, kansascity, seatle);
        g.setGameState(Game.FINAL);
        g.setVisitorScore(7);
        s.add(g);

        g = new Game(1999, 1, miami, newengland);
        g.setGameState(Game.FINAL);
        g.setHomeScore(7);
        s.add(g);

        g = new Game(1999, 1, minnesota, nyjets);
        g.setGameState(Game.FINAL);
        g.setHomeScore(7);
        s.add(g);

        g = new Game(1999, 1, neworleans, atlanta);
        g.setGameState(Game.FINAL);
        g.setHomeScore(7);
        s.add(g);

        g = new Game(1999, 1, pittsburgh, philadelphia);
        g.setGameState(Game.FINAL);
        g.setHomeScore(7);
        s.add(g);

        g = new Game(1999, 1, sandiego, sanfrancisco);
        g.setGameState(Game.FINAL);
        g.setHomeScore(7);
        s.add(g);

        g = new Game(1999, 1, tampabay, chicago);
        g.setGameState(Game.FINAL);
        g.setHomeScore(7);
        s.add(g);

        g = new Game(1999, 1, nygiants, washington);
        g.setGameState(Game.FINAL);
        g.setHomeScore(7);
        g.setVisitorScore(7);
        s.add(g);

        g = new Game(1999, 1, oakland, denver);
        g.setGameState(Game.FINAL);
        g.setHomeScore(7);
        s.add(g);

        System.out.println(s);

        Entry e = new Entry(s, chris);

        e.setSchedule(s);
        e.add(greenbay);
        e.add(sanfrancisco);
        e.add(carolina);
        e.add(pittsburgh);
        e.add(detroit);
        e.add(jacksonville);
        e.add(kansascity);
        e.add(buffalo);
        e.add(baltimore);
        e.add(newengland);
        e.add(nyjets);
        e.add(atlanta);
        e.add(tampabay);
        e.add(nygiants);
        e.add(denver);

        System.out.println(e);
        e.getScore();
        System.out.println(e);
    }
}

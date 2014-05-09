// SCCS ID: @(#) 08/23/98 1.1 Game.java

package com.talient.football.entities;

import java.util.Date;
import java.util.Calendar;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class Game {

    public Game(int year, int week, Team home, Team visitor) {
        if (visitor == null) {
            throw new IllegalGameException("visiting team is null");
        }

        if (home == null) {
            throw new IllegalGameException("home team is null");
        }

        if (visitor.equals(home)) {
            throw new IllegalGameException("home and visiting team is same");
        }

        this.year = year;
        this.week = week;
        this.visitor = visitor;
        this.home = home;
        Calendar cal = Calendar.getInstance();
        cal.set(year, Calendar.APRIL, 1, 0, 0, 0);
        this.startTime = cal.getTime();
    }

    public int  getYear()                   { return year; }
    public int  getWeek()                   { return week; }
    public Team getVisitor()                { return visitor; }
    public Team getHome()                   { return home; }
    public void setHomeScore(int score)     { homeScore = score; }
    public int  getHomeScore()              { return homeScore; }
    public void setVisitorScore(int score)  { visitorScore = score; }
    public int  getVisitorScore()           { return visitorScore; }
    public void setGameState(int state)     { gameState = state; }
    public int  getGameState()              { return gameState; }
    public void setQuarter(int qtr)         { quarter = qtr; }
    public int  getQuarter()                { return quarter; }
    public void setStartTime(Date date)     { startTime = date; }
    public Date getStartTime()              { return startTime; }
    public void setGameClock(int clk)       { gameClock = clk; }
    public int  getGameClock()              { return gameClock; }
    public void setDisplayOrder(int order)  { displayOrder = order; }
    public int  getDisplayOrder()           { return displayOrder; }

    public void setGameClock(String clk) throws Exception {
        int idx = clk.indexOf(":");
        if (idx < 0) {
            return;
        }
        try {
            int min = Integer.parseInt(clk.substring(0, idx).trim());
            int sec = Integer.parseInt(clk.substring(idx+1));
            gameClock = min * 60 + sec;
        }
        catch (NumberFormatException e) {
            gameClock = 0;
        }
    }

    public String getGameClockString() {
        String clock = ((getGameClock() / 60) + ":");
        if ((getGameClock() % 60) < 10) {
            clock += "0";
        }
        clock += String.valueOf(getGameClock() % 60);
        return clock;
    }

    public GameResult getResult() {
        if (gameState == INPROGRESS) {
            return GameResult.UNDETERMINED;
        }
        else if (gameState == DROPPED) {
            return GameResult.DROPPED;
        }
        else {
            if (homeScore > visitorScore) {
                return GameResult.HOME;
            }
            else if (visitorScore > homeScore) {
                return GameResult.VISITOR;
            }
            else {
                return GameResult.TIE;
            }
        }
    }

    public boolean containsTeam(Team team) {
        return team.equals(visitor) || team.equals(home);
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
 
        str.append(getClass().getName());
        str.append("[");
        str.append("year="+year);
        str.append(",week="+week);
        str.append(",home="+home);
        str.append(",visitor="+visitor);
        str.append("]");
 
        return str.toString();
    }

    private final int year;
    private final int week;
    private final Team visitor;
    private final Team home;
    private int homeScore;
    private int visitorScore;
    private int gameState;
    private int quarter;
    private int gameClock;
    private Date startTime;
    private int displayOrder;

    public static final int INPROGRESS = 0;
    public static final int FINAL = 1;
    public static final int VALIDATED = 2;
    public static final int DROPPED = 3;

    private GameResult result = GameResult.UNDETERMINED;
}

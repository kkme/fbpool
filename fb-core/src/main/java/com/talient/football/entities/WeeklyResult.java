// SCCS ID: @(#) 08/23/98 1.1 WeeklyResult.java

package com.talient.football.entities;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class WeeklyResult {

    public WeeklyResult(int year, int week, Entrant entrant) {
        this.year = year;
        this.week = week;
        this.entrant = entrant;
    }

    public Entrant getEntrant() { return this.entrant; }
    public int     getYear()    { return this.year; }
    public int     getWeek()    { return this.week; }

    public void setScore(int score)   { this.score = score; }
    public int  getScore()            { return score; }
    public void setBonus(int bonus)   { this.bonus = bonus; }
    public int  getBonus()            { return bonus; }
    public void setWins(int wins)     { this.wins = wins; }
    public int  getWins()             { return wins; }
    public void setLosses(int losses) { this.losses = losses; }
    public int  getLosses()           { return losses; }
    public void setTies(int ties)     { this.ties = ties; }
    public int  getTies()             { return ties; }
    public int  getTotalScore()       { return score + bonus; }

    public String toString() {
        StringBuffer str = new StringBuffer();
 
        str.append(getClass().getName());
        str.append("[");
        str.append("name="+entrant.getUsername());
        str.append(",year="+year);
        str.append(",week="+week);
        str.append(",score="+score);
        str.append(",bonus="+bonus);
        str.append(",wins="+wins);
        str.append(",losses="+losses);
        str.append(",ties="+ties);
        str.append("]");
 
        return str.toString();
    }

    private int year = 0;
    private int week = 0;
    private Entrant entrant = null;
    private int score = 0;
    private int bonus = 0;
    private int wins = 0;
    private int losses = 0;
    private int ties = 0;
}

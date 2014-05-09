// SCCS ID: @(#) 08/23/98 1.1 FranchiseNameHistory.java

package com.talient.football.entities;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public final class FranchiseNameHistory {

    private Team team;
    private String longName;
    private String shortName;
    private int startYear;
    private int endYear;
    private String confAffiliation;

    public FranchiseNameHistory(Team team, String longName, String shortName,
                                int startYear, int endYear,
                                String confAffiliation) {
        this.team = team;
        this.longName = longName;
        this.shortName = shortName;
        this.startYear = startYear;
        this.endYear = endYear;
        this.confAffiliation = confAffiliation;
    }
    
    public void   setTeam(Team t)                 { team = t; }
    public Team   getTeam()                       { return team; }
    public void   setLongName(String name)        { longName = name; }
    public String getLongName()                   { return longName; }
    public void   setShortName(String name)       { shortName = name; }
    public String getShortName()                  { return shortName; }
    public void   setStartYear(int yr)            { startYear = yr; }
    public int    getStartYear()                  { return startYear; }
    public void   setEndYear(int yr)              { endYear = yr; }
    public int    getEndYear()                    { return endYear; }
    public void   setConfAffiliation(String name) { confAffiliation = name; }
    public String getConfAffiliation()            { return confAffiliation; }
    
    public String toString() { return this.longName; }

    public boolean isSameTeam(FranchiseNameHistory f) {
        if (f.getTeam().getLongName().equals(team.getLongName())) {
            return true;
        }
        return false;
    }

    public boolean equals(Team t) {
        return t.getLongName().equals(team.getLongName());
    }

    public static void main(String[] args) {
        Team t = new Team("Denver Broncos", "Den");
        FranchiseNameHistory team =
            new FranchiseNameHistory(t,
                                     "Denver Broncos",
                                     "Den",
                                     1960,
                                     0,
                                     "AFC West");

        System.out.println("Long Name: " + team.getLongName());
        System.out.println("Short Name: " + team.getShortName());
        System.out.println("Start Year: " + team.getStartYear());
        System.out.println("End Year: " + team.getEndYear());
        System.out.println("Conf.: " + team.getConfAffiliation());
    }
}

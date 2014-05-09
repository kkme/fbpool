// SCCS ID: @(#) 08/23/98 1.1 Entrant.java

package com.talient.football.entities;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class Entrant {

    private String username;
    private String contactEmail;
    private boolean active;
    private boolean weeklyEntry;
    private boolean gameOrderedRecap;
    private boolean confOrderedRecap;
    private boolean weeklyStats;
    private boolean weeklyResult;
    private boolean standings;
    private boolean notifyEarly;
    private boolean notifyMedium;
    private boolean notifyLate;

    public Entrant(String username, String contactEmail, boolean active) {
        this.username = username;
        this.contactEmail = contactEmail;
        this.active = active;
        this.weeklyEntry = true;
        this.gameOrderedRecap = true;
        this.confOrderedRecap = true;
        this.weeklyStats = true;
        this.weeklyResult = true;
        this.standings = true;
        this.notifyEarly = false;
        this.notifyMedium = false;
        this.notifyLate = false;
    }

    public Entrant(String username, String contactEmail) {
        this.username = username;
        this.contactEmail = contactEmail;
        this.active = false;
        this.weeklyEntry = true;
        this.gameOrderedRecap = true;
        this.confOrderedRecap = true;
        this.weeklyStats = true;
        this.weeklyResult = true;
        this.standings = true;
        this.notifyEarly = false;
        this.notifyMedium = false;
        this.notifyLate = false;
    }
    
    public void    setUsername(String name)          { username = name; }
    public String  getUsername()                     { return username; }
    public void    setActive(boolean act)            { active = act; }
    public boolean getActive()                       { return active; }
    public void    setWeeklyEntry(boolean bool)      { weeklyEntry = bool; }
    public boolean getWeeklyEntry()                  { return weeklyEntry; }
    public void    setGameOrderedRecap(boolean bool) { gameOrderedRecap = bool; }
    public boolean getGameOrderedRecap()             { return gameOrderedRecap; }
    public void    setConfOrderedRecap(boolean bool) { confOrderedRecap = bool; }
    public boolean getConfOrderedRecap()             { return confOrderedRecap; }
    public void    setWeeklyStats(boolean bool)      { weeklyStats = bool; }
    public boolean getWeeklyStats()                  { return weeklyStats; }
    public void    setWeeklyResult(boolean bool)     { weeklyResult = bool; }
    public boolean getWeeklyResult()                 { return weeklyResult; }
    public void    setStandings(boolean bool)        { standings = bool; }
    public boolean getStandings()                    { return standings; }
    public void    setNotifyEarly(boolean bool)      { notifyEarly = bool; }
    public boolean getNotifyEarly()                  { return notifyEarly; }
    public void    setNotifyMedium(boolean bool)     { notifyMedium = bool; }
    public boolean getNotifyMedium()                 { return notifyMedium; }
    public void    setNotifyLate(boolean bool)       { notifyLate = bool; }
    public boolean getNotifyLate()                   { return notifyLate; }
    public void    setContactEmail(String email)     { contactEmail = email; }
    public String  getContactEmail()                 { return contactEmail; }
    public boolean isGenerated()           { return username.startsWith("~"); }
    
    public String toString() { return this.username; }
    public boolean equals(Object other) {
        return this.getUsername().equals(((Entrant)other).getUsername());
    }
    public int hashCode() {
        return getUsername().hashCode();
    }

    public static void main(String[] args) {
        Entrant player = new Entrant("nlonsky", "nick@lonsky.com");

        System.out.println("Username: " + player.getUsername());
        System.out.println("Email: " + player.getContactEmail());
    }
}

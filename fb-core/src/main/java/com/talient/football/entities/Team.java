// SCCS ID: @(#) 08/23/98 1.1 Team.java

package com.talient.football.entities;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class Team {

    private String longName;
    private String shortName;
    private String currentName;

    public Team(String longName, String shortName) {
        setLongName(longName);
        setShortName(shortName);
        setCurrentName(longName);
    }
    
    public void   setLongName(String name)  { longName = name; }
    public String getLongName()             { return longName; }
    public void   setShortName(String name) { shortName = name; }
    public String getShortName()            { return shortName; }
    public void   setCurrentName(String cn) { currentName = cn; }
    public String getCurrentName()          { return currentName; }

    public String toString() { return getCurrentName(); }

    public boolean equals(Object other) {
        if (other instanceof Team) {
            return toString().equals(((Team)other).toString());
        }
        return false;
    }

    public int hashCode() {
        return getCurrentName().hashCode();
    }

    public static void main(String[] args) {
    }
}

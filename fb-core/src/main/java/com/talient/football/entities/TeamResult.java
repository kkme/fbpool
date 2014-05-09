// SCCS ID: @(#) 08/23/98 1.1 TeamResult.java

package com.talient.football.entities;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public final class TeamResult {

    private String id;

    private TeamResult(String result) {

        this.id = result;
    }

    public String toString() { return this.id; }

    public static final TeamResult WON = new TeamResult("Won");
    public static final TeamResult LOST = new TeamResult("Lost");
    public static final TeamResult TIED = new TeamResult("Tied");
    public static final TeamResult UNDETERMINED =
        new TeamResult("Undetermined");
    public static final TeamResult DROPPED = new TeamResult("Dropped");
}

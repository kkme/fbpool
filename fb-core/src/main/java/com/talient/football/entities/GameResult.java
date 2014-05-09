// SCCS ID: @(#) 08/23/98 1.1 GameResult.java

package com.talient.football.entities;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public final class GameResult {

    private String id;

    private GameResult(String result) {

        this.id = result;
    }

    public String toString() { return this.id; }

    public boolean equals(Object other) {
        return (this.toString().equals(((GameResult)other).toString()));
    }

    public static final GameResult VISITOR = new GameResult("Visitor");
    public static final GameResult HOME = new GameResult("Home");
    public static final GameResult TIE = new GameResult("Tie");
    public static final GameResult UNDETERMINED =
        new GameResult("Undetermined");
    public static final GameResult DROPPED = new GameResult("Dropped");
}

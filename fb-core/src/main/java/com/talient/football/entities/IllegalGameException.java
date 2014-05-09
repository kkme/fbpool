// SCCS ID: @(#) 08/23/98 1.1 IllegalGameException.java

package com.talient.football.entities;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class IllegalGameException extends RuntimeException {

    public IllegalGameException() {
        super();
    }

    public IllegalGameException(String text) {
        super(text);
    }

}

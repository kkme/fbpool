// SCCS ID: @(#) 08/23/98 1.1 IllegalEntryException.java

package com.talient.football.entities;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class IllegalEntryException extends RuntimeException {

    public IllegalEntryException() {
        super();
    }

    public IllegalEntryException(String text) {
        super(text);
    }

}

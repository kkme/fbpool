// CVS ID: @(#) $Id: MissingPropertyException.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.util;

/**
 * <p>
 * @author Steve Schmadeke
 * @version $Version: $
 */
public class MissingPropertyException extends Exception {

    public MissingPropertyException() {
        super();
    }

    public MissingPropertyException(String text) {
        super(text);
    }
}
